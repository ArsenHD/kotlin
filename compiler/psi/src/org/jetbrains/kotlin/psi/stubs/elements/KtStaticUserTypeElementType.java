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

package org.jetbrains.kotlin.psi.stubs.elements;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.psi.KtStaticUserType;
import org.jetbrains.kotlin.psi.stubs.KotlinStaticUserTypeStub;
import org.jetbrains.kotlin.psi.stubs.impl.KotlinStaticUserTypeStubImpl;

public class KtStaticUserTypeElementType extends KtAbstractUserTypeElementType<KtStaticUserType> {
    public KtStaticUserTypeElementType(@NotNull @NonNls String debugName) {
        super(debugName, KtStaticUserType.class, KotlinStaticUserTypeStub.class);
    }

    @NotNull
    @Override
    public KotlinStaticUserTypeStub createStub(@NotNull KtStaticUserType psi, StubElement parentStub) {
        return new KotlinStaticUserTypeStubImpl((StubElement<?>) parentStub);
    }
    @NotNull
    @Override
    public KotlinStaticUserTypeStub deserialize(@NotNull StubInputStream dataStream, StubElement parentStub) {
        return new KotlinStaticUserTypeStubImpl((StubElement<?>) parentStub);
    }
}
