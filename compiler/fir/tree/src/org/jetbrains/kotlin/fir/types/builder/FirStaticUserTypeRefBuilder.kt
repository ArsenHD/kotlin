/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.types.builder

import org.jetbrains.kotlin.fir.builder.FirBuilderDsl
import org.jetbrains.kotlin.fir.types.FirStaticUserTypeRef
import org.jetbrains.kotlin.fir.types.impl.FirStaticUserTypeRefImpl


@FirBuilderDsl
open class FirStaticUserTypeRefBuilder : FirUserTypeRefBuilder() {
    override fun build(): FirStaticUserTypeRef {
        return FirStaticUserTypeRefImpl(source, isMarkedNullable, qualifier, annotations)
    }
}

inline fun buildStaticUserTypeRef(init: FirStaticUserTypeRefBuilder.() -> Unit): FirStaticUserTypeRef {
    return FirStaticUserTypeRefBuilder().apply(init).build()
}
