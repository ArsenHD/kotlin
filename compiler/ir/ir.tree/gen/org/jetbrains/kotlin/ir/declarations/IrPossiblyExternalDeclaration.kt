/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// This file was generated automatically. See the link to the original in the kdoc below.
// DO NOT MODIFY IT MANUALLY.

package org.jetbrains.kotlin.ir.declarations


/**
 * A non-leaf IR tree element.
 * @sample org.jetbrains.kotlin.ir.generator.IrTree.possiblyExternalDeclaration
 */
interface IrPossiblyExternalDeclaration : IrDeclarationWithName {
    val isExternal: Boolean
}
