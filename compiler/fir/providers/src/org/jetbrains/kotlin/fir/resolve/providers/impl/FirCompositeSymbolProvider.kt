/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.resolve.providers.impl

import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.NoMutableState
import org.jetbrains.kotlin.fir.resolve.isNotSelfStaticObject
import org.jetbrains.kotlin.fir.resolve.providers.FirSymbolProvider
import org.jetbrains.kotlin.fir.resolve.providers.FirSymbolProviderInternals
import org.jetbrains.kotlin.fir.symbols.impl.*
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name

@NoMutableState
class FirCompositeSymbolProvider(session: FirSession, val providers: List<FirSymbolProvider>) : FirSymbolProvider(session) {
    override fun getTopLevelCallableSymbols(packageFqName: FqName, name: Name): List<FirCallableSymbol<*>> {
        return providers.flatMap { it.getTopLevelCallableSymbols(packageFqName, name) }
    }

    @FirSymbolProviderInternals
    override fun getTopLevelCallableSymbolsTo(destination: MutableList<FirCallableSymbol<*>>, packageFqName: FqName, name: Name) {
        destination += getTopLevelCallableSymbols(packageFqName, name)
    }

    @FirSymbolProviderInternals
    override fun getTopLevelFunctionSymbolsTo(destination: MutableList<FirNamedFunctionSymbol>, packageFqName: FqName, name: Name) {
        providers.forEach {
            it.getTopLevelFunctionSymbolsTo(destination, packageFqName, name)
        }
    }

    @FirSymbolProviderInternals
    override fun getTopLevelPropertySymbolsTo(destination: MutableList<FirPropertySymbol>, packageFqName: FqName, name: Name) {
        providers.forEach {
            it.getTopLevelPropertySymbolsTo(destination, packageFqName, name)
        }
    }

    override fun getPackage(fqName: FqName): FqName? {
        return providers.firstNotNullOfOrNull { it.getPackage(fqName) }
    }

    override fun getClassLikeSymbolByClassId(classId: ClassId): FirClassLikeSymbol<*>? {
        if (classId.isNotSelfStaticObject) {
            return providers.firstNotNullOfOrNull { it.getClassLikeSymbolByClassId(classId) }
        }
        val ownerClassId = classId.outerClassId ?: return null
        val ownerClassSymbol = providers.firstNotNullOfOrNull { it.getClassLikeSymbolByClassId(ownerClassId) }
        return (ownerClassSymbol as? FirRegularClassSymbol)?.fir?.selfStaticObjectSymbol
    }
}
