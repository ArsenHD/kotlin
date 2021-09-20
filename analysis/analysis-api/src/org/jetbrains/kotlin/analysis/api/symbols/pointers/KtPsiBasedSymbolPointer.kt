/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.symbols.pointers

import com.intellij.psi.SmartPsiElementPointer
import org.jetbrains.kotlin.analysis.api.KtAnalysisSession
import org.jetbrains.kotlin.analysis.api.symbols.KtSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KtSymbolOrigin
import org.jetbrains.kotlin.psi.KtDeclaration
import org.jetbrains.kotlin.psi.KtObjectLiteralExpression
import org.jetbrains.kotlin.psi.psiUtil.createSmartPointer
import java.util.*

public class KtPsiBasedSymbolPointer<S : KtSymbol>(private val psiPointer: SmartPsiElementPointer<out KtDeclaration>) :
    KtSymbolPointer<S>() {
    @Deprecated("Consider using org.jetbrains.kotlin.analysis.api.KtAnalysisSession.restoreSymbol")
    override fun restoreSymbol(analysisSession: KtAnalysisSession): S? {
        val psi = psiPointer.element ?: return null

        @Suppress("UNCHECKED_CAST")
        return with(analysisSession) { psi.getSymbol() } as S?
    }

    public companion object {
        private val originsWithoutProperSource: Set<KtSymbolOrigin> = EnumSet.of(
            /**
             * Library might have no PSI at all, so we do not try to create a pointer by it.
             */
            KtSymbolOrigin.LIBRARY,

            /**
             * If symbol points to a generated member, we won't be able to recover it later on, because there is no corresponding
             * psi by which it can be found
             */
            KtSymbolOrigin.SOURCE_MEMBER_GENERATED,

            /**
             * If symbol points to a substituted member, the PSI will be pointing to the original (base) declaration. Because
             * of that the recovery is impossible.
             */
            KtSymbolOrigin.SUBSTITUTION_OVERRIDE,

            /**
             * Same goes for the intersection overrides.
             */
            KtSymbolOrigin.INTERSECTION_OVERRIDE,
        )

        public fun <S : KtSymbol> createForSymbolFromSource(symbol: S): KtPsiBasedSymbolPointer<S>? {
            if (symbol.origin in originsWithoutProperSource) return null

            val psi = when (val psi = symbol.psi) {
                is KtDeclaration -> psi
                is KtObjectLiteralExpression -> psi.objectDeclaration
                else -> null
            } ?: return null
            return KtPsiBasedSymbolPointer(psi.createSmartPointer())
        }
    }
}
