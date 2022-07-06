/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.declarations.utils

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.EffectiveVisibility
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.FirModuleData
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirDeclarationOrigin
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.builder.FirRegularClassBuilder
import org.jetbrains.kotlin.fir.declarations.builder.FirTypeParameterBuilder
import org.jetbrains.kotlin.fir.declarations.impl.FirResolvedDeclarationStatusImpl
import org.jetbrains.kotlin.fir.scopes.FirScopeProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirRegularClassSymbol
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.SpecialNames

fun FirTypeParameterBuilder.addDefaultBoundIfNecessary() {
    if (bounds.isEmpty()) {
        bounds += moduleData.session.builtinTypes.nullableAnyType
    }
}

fun FirRegularClassBuilder.addDeclaration(declaration: FirDeclaration) {
    declarations += declaration
}

fun FirRegularClassBuilder.addDeclarations(declarations: Collection<FirDeclaration>) {
    declarations.forEach(this::addDeclaration)
}

fun createEmptySelfStaticObject(
    ownerClassId: ClassId,
    moduleData: FirModuleData,
    scopeProvider: FirScopeProvider
): FirRegularClass {
    return initSelfStaticObject(ownerClassId, moduleData, scopeProvider).build()
}

fun initSelfStaticObject(
    ownerClassId: ClassId,
    moduleData: FirModuleData,
    scopeProvider: FirScopeProvider
): FirRegularClassBuilder {
    return FirRegularClassBuilder().apply {
        this.moduleData = moduleData
        origin = FirDeclarationOrigin.Source
        this.name = SpecialNames.SELF_STATIC_OBJECT
        status = FirResolvedDeclarationStatusImpl(
            Visibilities.Public,
            Modality.FINAL,
            EffectiveVisibility.Public
        ).apply {
            isStatic = true
        }
        classKind = ClassKind.STATIC_OBJECT
        this.scopeProvider = scopeProvider
        symbol = FirRegularClassSymbol(ownerClassId.selfStaticObjectId)
    }
}

private val ClassId.selfStaticObjectId: ClassId
    get() = createNestedClassId(SpecialNames.SELF_STATIC_OBJECT)
