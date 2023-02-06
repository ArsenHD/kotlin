/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.load.java.descriptors

import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.descriptors.annotations.Annotations

class JavaForKotlinOverridePropertyDescriptor(
    ownerDescriptor: ClassDescriptor,
    val getterMethod: SimpleFunctionDescriptor,
    val setterMethod: SimpleFunctionDescriptor?,
    val overriddenProperty: PropertyDescriptor
) : JavaPropertyDescriptor(
    ownerDescriptor,
    Annotations.EMPTY,
    getterMethod.modality,
    getterMethod.visibility,
    setterMethod != null,
    overriddenProperty.name,
    getterMethod.source,
    null,
    CallableMemberDescriptor.Kind.DECLARATION,
    false,
    overriddenProperty.modality == Modality.FINAL,
    null
)
