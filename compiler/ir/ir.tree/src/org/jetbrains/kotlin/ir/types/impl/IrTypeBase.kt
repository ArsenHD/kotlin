/*
 * Copyright 2010-2018 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.ir.types.impl

import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.DescriptorVisibilities
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.PackageFragmentDescriptor
import org.jetbrains.kotlin.ir.IrFileEntry
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.UNDEFINED_OFFSET
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.declarations.impl.IrClassImpl
import org.jetbrains.kotlin.ir.declarations.impl.IrFileImpl
import org.jetbrains.kotlin.ir.declarations.lazy.IrLazyClass
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.symbols.IrClassSymbol
import org.jetbrains.kotlin.ir.symbols.IrClassifierSymbol
import org.jetbrains.kotlin.ir.symbols.IrFileSymbol
import org.jetbrains.kotlin.ir.symbols.impl.IrClassSymbolImpl
import org.jetbrains.kotlin.ir.types.*
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.Name
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.Variance
import org.jetbrains.kotlin.types.model.CaptureStatus
import org.jetbrains.kotlin.types.model.CapturedTypeConstructorMarker
import org.jetbrains.kotlin.types.model.CapturedTypeMarker
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

abstract class IrTypeBase(val kotlinType: KotlinType?) : IrType(), IrTypeProjection {
    override val type: IrType get() = this
}

class IrErrorTypeImpl(
    kotlinType: KotlinType?,
    override val annotations: List<IrConstructorCall>,
    override val variance: Variance,
    isMarkedNullable: Boolean = false,
    private val errorClassStubSymbol: IrClassSymbol? = null
) : IrErrorType(kotlinType, isMarkedNullable, errorClassStubSymbol) {
    override fun equals(other: Any?): Boolean = other is IrErrorTypeImpl

    override fun hashCode(): Int = IrErrorTypeImpl::class.java.hashCode()
}

class IrErrorClassImpl : IrClassImpl(
    UNDEFINED_OFFSET, UNDEFINED_OFFSET, IrDeclarationOrigin.DEFAULT_PROPERTY_ACCESSOR, IrClassSymbolImpl(),
    Name.special("<error>"), ClassKind.CLASS, DescriptorVisibilities.DEFAULT_VISIBILITY, Modality.FINAL
) {
    override var parent: IrDeclarationParent
        get() = object : IrFile() {
            override val startOffset: Int
                get() = TODO("Not yet implemented")
            override val endOffset: Int
                get() = TODO("Not yet implemented")
            override var annotations: List<IrConstructorCall>
                get() = TODO("Not yet implemented")
                set(_) {}
            override val declarations: MutableList<IrDeclaration>
                get() = TODO("Not yet implemented")
            override val symbol: IrFileSymbol
                get() = TODO("Not yet implemented")
            override val module: IrModuleFragment
                get() = TODO("Not yet implemented")
            override val fileEntry: IrFileEntry
                get() = TODO("Not yet implemented")
            override var metadata: MetadataSource?
                get() = TODO("Not yet implemented")
                set(_) {}

            @ObsoleteDescriptorBasedAPI
            override val packageFragmentDescriptor: PackageFragmentDescriptor
                get() = TODO("Not yet implemented")
            override val fqName: FqName
                get() = FqName.ROOT
        }
        set(_) = TODO()
}

class IrDynamicTypeImpl(
    kotlinType: KotlinType?,
    override val annotations: List<IrConstructorCall>,
    override val variance: Variance,
) : IrDynamicType(kotlinType) {
    override fun equals(other: Any?): Boolean = other is IrDynamicTypeImpl

    override fun hashCode(): Int = IrDynamicTypeImpl::class.java.hashCode()
}


val IrType.originalKotlinType: KotlinType?
    get() = safeAs<IrTypeBase>()?.kotlinType


object IrStarProjectionImpl : IrStarProjection {
    override fun equals(other: Any?): Boolean = this === other

    override fun hashCode(): Int = System.identityHashCode(this)
}

/**
 * An instance which should be used when creating an IR element whose type cannot be determined at the moment of creation.
 *
 * Example: when translating generic functions in psi2ir, we're creating an IrFunction first, then adding IrTypeParameter instances to it,
 * and only then translating the function's return type with respect to those created type parameters.
 *
 * Instead of using this special instance, we could just make IrFunction/IrConstructor constructors allow to accept no return type,
 * however this could lead to a situation where we forget to set return type sometimes. This would result in crashes at unexpected moments,
 * especially in Kotlin/JS where function return types are not present in the resulting binary files.
 */
object IrUninitializedType : IrType() {
    override val annotations: List<IrConstructorCall> = emptyList()

    override fun equals(other: Any?): Boolean = this === other

    override fun hashCode(): Int = System.identityHashCode(this)
}

class ReturnTypeIsNotInitializedException(function: IrFunction) : IllegalStateException(
    "Return type is not initialized for function '${function.name}'"
)


// Please note this type is not denotable which means it could only exist inside type system
class IrCapturedType(
    val captureStatus: CaptureStatus,
    val lowerType: IrType?,
    projection: IrTypeArgument,
    typeParameter: IrTypeParameter
) : IrSimpleType(null), CapturedTypeMarker {

    override val variance: Variance
        get() = TODO("Not yet implemented")

    class Constructor(val argument: IrTypeArgument, val typeParameter: IrTypeParameter) :
        CapturedTypeConstructorMarker {

        private var _superTypes: List<IrType> = emptyList()

        val superTypes: List<IrType> get() = _superTypes

        fun initSuperTypes(superTypes: List<IrType>) {
            _superTypes = superTypes
        }
    }

    val constructor: Constructor = Constructor(projection, typeParameter)

    override val classifier: IrClassifierSymbol get() = error("Captured Type does not have a classifier")
    override val arguments: List<IrTypeArgument> get() = emptyList()
    override val abbreviation: IrTypeAbbreviation? get () = null
    override val nullability: SimpleTypeNullability get() = SimpleTypeNullability.DEFINITELY_NOT_NULL
    override val annotations: List<IrConstructorCall> get() = emptyList()

    override fun equals(other: Any?): Boolean {
        return other is IrCapturedType
                && captureStatus == other.captureStatus
                && lowerType == other.lowerType
                && constructor === other.constructor
    }

    override fun hashCode(): Int {
        return (captureStatus.hashCode() * 31 + (lowerType?.hashCode() ?: 0)) * 31 + constructor.hashCode()
    }
}
