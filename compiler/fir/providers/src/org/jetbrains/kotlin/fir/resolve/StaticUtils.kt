/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.resolve

import org.jetbrains.kotlin.fir.declarations.FirClass
import org.jetbrains.kotlin.fir.declarations.FirDeclaration
import org.jetbrains.kotlin.fir.declarations.FirRegularClass
import org.jetbrains.kotlin.fir.declarations.utils.classId
import org.jetbrains.kotlin.fir.types.ConeKotlinType
import org.jetbrains.kotlin.fir.types.classId
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.SpecialNames

val FirDeclaration.isSelfStaticObject: Boolean
    get() = this is FirRegularClass && isSelfStaticObject

@Suppress("unused")
val FirDeclaration.isNotSelfStaticObject: Boolean
    get() = !isSelfStaticObject

val FirClass.isSelfStaticObject: Boolean
    get() = classId.isSelfStaticObject

val FirClass.isNotSelfStaticObject: Boolean
    get() = !isSelfStaticObject

val ConeKotlinType.isSelfStaticObject: Boolean
    get() = classId?.isSelfStaticObject == true

val ConeKotlinType.isNotSelfStaticObject: Boolean
    get() = !isSelfStaticObject

val ClassId.isSelfStaticObject: Boolean
    get() = shortClassName == SpecialNames.SELF_STATIC_OBJECT

val ClassId.isNotSelfStaticObject: Boolean
    get() = !isSelfStaticObject
