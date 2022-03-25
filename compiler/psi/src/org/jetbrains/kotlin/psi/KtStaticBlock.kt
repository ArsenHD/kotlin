/*
 * Copyright 2010-2019 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.psi

import com.intellij.lang.ASTNode
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.psiUtil.getParentOfType
import org.jetbrains.kotlin.psi.stubs.KotlinPlaceHolderStub
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes.STATIC_BLOCK
import org.jetbrains.kotlin.psi.stubs.elements.KtTokenSets.DECLARATION_TYPES
import org.jetbrains.kotlin.utils.sure

class KtStaticBlock : KtDeclarationStub<KotlinPlaceHolderStub<KtStaticBlock>>, KtDeclarationContainer {
    constructor(node: ASTNode) : super(node)

    constructor(stub: KotlinPlaceHolderStub<KtStaticBlock>) : super(stub, STATIC_BLOCK)

    override fun getDeclarations(): List<KtDeclaration> =
        listOf(*getStubOrPsiChildren(DECLARATION_TYPES, KtDeclaration.ARRAY_FACTORY))

    override fun <R, D> accept(visitor: KtVisitor<R, D>, data: D) = visitor.visitStaticBlock(this, data)

    val openBraceNode: PsiElement?
        get() = node.getChildren(TokenSet.create(KtTokens.LBRACE)).singleOrNull()?.psi

    val staticKeyword: PsiElement
        get() = modifierList?.getModifier(KtTokens.STATIC_KEYWORD)!!

    val containingDeclaration: KtClass
        get() = getParentOfType<KtClass>(true).sure { "Should only be present in class" }
}
