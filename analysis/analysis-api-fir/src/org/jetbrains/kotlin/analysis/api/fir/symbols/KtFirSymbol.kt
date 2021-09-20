/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.fir.symbols

import org.jetbrains.kotlin.fir.FirFakeSourceElementKind
import org.jetbrains.kotlin.fir.declarations.FirCallableDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.synthetic.FirSyntheticProperty
import org.jetbrains.kotlin.fir.originalIfFakeOverride
import org.jetbrains.kotlin.fir.render
import org.jetbrains.kotlin.fir.scopes.impl.importedFromObjectData
import org.jetbrains.kotlin.analysis.api.ValidityTokenOwner
import org.jetbrains.kotlin.analysis.api.fir.utils.FirRefWithValidityCheck
import org.jetbrains.kotlin.analysis.api.symbols.KtSymbol
import org.jetbrains.kotlin.analysis.api.symbols.KtSymbolOrigin

internal interface KtFirSymbol<out F : FirDeclaration> : KtSymbol, ValidityTokenOwner {
    val firRef: FirRefWithValidityCheck<F>

    override val origin: KtSymbolOrigin get() = firRef.withFir { it.ktSymbolOrigin() }
}

internal fun KtFirSymbol<*>.symbolEquals(other: Any?): Boolean {
    if (other !is KtFirSymbol<*>) return false
    if (this.token != other.token) return false
    return this.firRef == other.firRef
}

internal fun KtFirSymbol<*>.symbolHashCode(): Int = firRef.hashCode() * 31 + token.hashCode()

private tailrec fun FirDeclaration.ktSymbolOrigin(): KtSymbolOrigin = when (origin) {
    FirDeclarationOrigin.Source -> {
        when (source?.kind) {
            FirFakeSourceElementKind.ImplicitConstructor,
            FirFakeSourceElementKind.DataClassGeneratedMembers,
            FirFakeSourceElementKind.EnumGeneratedDeclaration,
            FirFakeSourceElementKind.ItLambdaParameter -> KtSymbolOrigin.SOURCE_MEMBER_GENERATED

            else -> KtSymbolOrigin.SOURCE
        }
    }
    FirDeclarationOrigin.Library, FirDeclarationOrigin.BuiltIns -> KtSymbolOrigin.LIBRARY
    FirDeclarationOrigin.Java -> KtSymbolOrigin.JAVA
    FirDeclarationOrigin.SamConstructor -> KtSymbolOrigin.SAM_CONSTRUCTOR
    FirDeclarationOrigin.Enhancement -> KtSymbolOrigin.JAVA
    FirDeclarationOrigin.SubstitutionOverride -> KtSymbolOrigin.SUBSTITUTION_OVERRIDE
    FirDeclarationOrigin.IntersectionOverride -> KtSymbolOrigin.INTERSECTION_OVERRIDE
    FirDeclarationOrigin.Delegated -> KtSymbolOrigin.DELEGATED
    FirDeclarationOrigin.Synthetic -> {
        when {
            this is FirSyntheticProperty -> KtSymbolOrigin.JAVA_SYNTHETIC_PROPERTY
            else -> throw InvalidFirDeclarationOriginForSymbol(this)
        }
    }
    FirDeclarationOrigin.ImportedFromObject -> {
        val importedFromObjectData = (this as FirCallableDeclaration).importedFromObjectData
            ?: error("Declaration has ImportedFromObject origin, but no importedFromObjectData present")

        importedFromObjectData.original.ktSymbolOrigin()
    }
    else -> {
        val overridden = (this as? FirCallableDeclaration)?.originalIfFakeOverride()
            ?: throw InvalidFirDeclarationOriginForSymbol(this)
        overridden.ktSymbolOrigin()
    }
}


class InvalidFirDeclarationOriginForSymbol(declaration: FirDeclaration) :
    IllegalStateException("Invalid FirDeclarationOrigin ${declaration.origin::class.simpleName} for ${declaration.render()}")
