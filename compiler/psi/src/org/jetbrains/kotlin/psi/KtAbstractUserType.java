/*
 * Copyright 2010-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.psi;

import com.google.common.collect.Lists;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.kotlin.lexer.KtTokens;
import org.jetbrains.kotlin.psi.stubs.elements.KtStubElementTypes;

import java.util.Collections;
import java.util.List;

public abstract class KtAbstractUserType<T extends KtAbstractUserType<T>> extends KtElementImplStub<StubElement<T>> implements KtTypeElement {
    public KtAbstractUserType(@NotNull ASTNode node) {
        super(node);
    }

    public KtAbstractUserType(@NotNull StubElement<T> stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    @Nullable
    public final KtTypeArgumentList getTypeArgumentList() {
        return getStubOrPsiChild(KtStubElementTypes.TYPE_ARGUMENT_LIST);
    }

    @NotNull
    public final List<KtTypeProjection> getTypeArguments() {
        // TODO: empty elements in PSI
        KtTypeArgumentList typeArgumentList = getTypeArgumentList();
        return typeArgumentList == null ? Collections.emptyList() : typeArgumentList.getArguments();
    }

    @NotNull
    @Override
    public final List<KtTypeReference> getTypeArgumentsAsTypes() {
        List<KtTypeReference> result = Lists.newArrayList();
        for (KtTypeProjection projection : getTypeArguments()) {
            result.add(projection.getTypeReference());
        }
        return result;
    }

    @Nullable @IfNotParsed
    public abstract KtSimpleNameExpression getReferenceExpression();

    @Nullable
    public KtUserType getQualifier() {
        return getStubOrPsiChild(KtStubElementTypes.USER_TYPE);
    }

    public final void deleteQualifier() {
        KtUserType qualifier = getQualifier();
        assert qualifier != null;
        PsiElement dot = findChildByType(KtTokens.DOT);
        assert dot != null;
        qualifier.delete();
        dot.delete();
    }

    @Nullable
    public final String getReferencedName() {
        KtSimpleNameExpression referenceExpression = getReferenceExpression();
        return referenceExpression == null ? null : referenceExpression.getReferencedName();
    }
}
