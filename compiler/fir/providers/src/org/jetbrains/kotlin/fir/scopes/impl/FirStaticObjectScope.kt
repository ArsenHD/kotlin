/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.scopes.impl

import org.jetbrains.kotlin.fir.resolve.substitution.ConeSubstitutor
import org.jetbrains.kotlin.fir.scopes.*
import org.jetbrains.kotlin.fir.symbols.impl.*
import org.jetbrains.kotlin.name.Name

class FirStaticObjectScope(val scope: FirContainingNamesAwareScope) : FirTypeScope() {
    override fun getCallableNames(): Set<Name> {
        return scope.getCallableNames()
    }

    override fun getClassifierNames(): Set<Name> {
        return scope.getClassifierNames()
    }

    override fun processFunctionsByName(name: Name, processor: (FirNamedFunctionSymbol) -> Unit) {
        return scope.processFunctionsByName(name, processor)
    }

    override fun processPropertiesByName(name: Name, processor: (FirVariableSymbol<*>) -> Unit) {
        return scope.processPropertiesByName(name, processor)
    }

    override fun processClassifiersByNameWithSubstitution(name: Name, processor: (FirClassifierSymbol<*>, ConeSubstitutor) -> Unit) {
        return scope.processClassifiersByNameWithSubstitution(name, processor)
    }

    /**
     * Static objects cannot inherit functions or properties from anything (at least at the moment).
     *
     * All static declarations in classes in FIR are contained inside a "self static object" (SSO), and every class has its own SSO.
     * It is possible that inheritance of static declarations will be allowed in the future, and then we will have to allow
     * inheritance between SSOs to support it. But right now static declaration inheritance is not supported in Kotlin,
     * so there is no inheritance between SSOs as well as there is no inheritance between independent static objects.
     */
    override fun processDirectOverriddenFunctionsWithBaseScope(
        functionSymbol: FirNamedFunctionSymbol,
        processor: (FirNamedFunctionSymbol, FirTypeScope) -> ProcessorAction
    ): ProcessorAction {
        return ProcessorAction.NONE
    }

    override fun processDirectOverriddenPropertiesWithBaseScope(
        propertySymbol: FirPropertySymbol,
        processor: (FirPropertySymbol, FirTypeScope) -> ProcessorAction
    ): ProcessorAction {
        return ProcessorAction.NONE
    }
}
