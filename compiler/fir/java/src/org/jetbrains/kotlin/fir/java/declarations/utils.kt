/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.java.declarations

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.fir.FirModuleData
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.FirResolvePhase
import org.jetbrains.kotlin.fir.declarations.builder.FirRegularClassBuilder
import org.jetbrains.kotlin.fir.declarations.impl.FirResolvedDeclarationStatusImpl
import org.jetbrains.kotlin.fir.declarations.utils.selfStaticObjectId
import org.jetbrains.kotlin.fir.java.JavaTypeParameterStack
import org.jetbrains.kotlin.fir.scopes.FirScopeProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.load.java.structure.JavaPackage
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.SpecialNames

internal fun javaOrigin(isFromSource: Boolean): FirDeclarationOrigin.Java {
    return if (isFromSource) FirDeclarationOrigin.Java.Source else FirDeclarationOrigin.Java.Library
}

internal fun initJavaSelfStaticObject(
    ownerClassId: ClassId,
    ownerJavaPackage: JavaPackage?,
    moduleData: FirModuleData,
    scopeProvider: FirScopeProvider,
    session: FirSession
): FirRegularClassBuilder {
    return FirJavaClassBuilder().apply {
        resolvePhase = FirResolvePhase.BODY_RESOLVE
        this.moduleData = moduleData
        this.name = SpecialNames.SELF_STATIC_OBJECT
        isFromSource = false
        visibility = Visibilities.Private
        val modality = Modality.FINAL
        this.modality = modality
        isTopLevel = false
        val isStatic = true
        this.isStatic = isStatic
        javaPackage = ownerJavaPackage
        javaTypeParameterStack = JavaTypeParameterStack.EMPTY
        this.scopeProvider = scopeProvider
        status = FirResolvedDeclarationStatusImpl(
            Visibilities.Public,
            modality,
            EffectiveVisibility.Public
        ).apply {
            this.isStatic = isStatic
        }
        classKind = ClassKind.STATIC_OBJECT
        this.scopeProvider = scopeProvider
        symbol = FirRegularClassSymbol(ownerClassId.selfStaticObjectId)
        superTypeRefs += session.builtinTypes.anyType
    }
}
