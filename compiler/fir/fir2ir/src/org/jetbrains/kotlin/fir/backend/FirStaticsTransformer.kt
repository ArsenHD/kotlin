/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.backend

import org.jetbrains.kotlin.builtins.functions.FunctionClassKind
import org.jetbrains.kotlin.fir.FirElement
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.expressions.*
import org.jetbrains.kotlin.fir.expressions.builder.buildArgumentList
import org.jetbrains.kotlin.fir.expressions.impl.FirNoReceiverExpression
import org.jetbrains.kotlin.fir.references.FirNamedReference
import org.jetbrains.kotlin.fir.references.builder.buildResolvedNamedReference
import org.jetbrains.kotlin.fir.references.toResolvedFunctionSymbol
import org.jetbrains.kotlin.fir.resolve.isInvoke
import org.jetbrains.kotlin.fir.resolve.isNotSelfStaticObject
import org.jetbrains.kotlin.fir.resolve.isSelfStaticObject
import org.jetbrains.kotlin.fir.resolve.providers.getClassDeclaredFunctionSymbols
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.fir.visitors.FirDefaultTransformer
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.util.OperatorNameConventions

class FirStaticsTransformer(components: Fir2IrComponents) : FirDefaultTransformer<Nothing?>(), Fir2IrComponents by components {
    override fun <E : FirElement> transformElement(element: E, data: Nothing?): E {
        @Suppress("UNCHECKED_CAST")
        return element.transformChildren(this, data) as E
    }

    override fun transformSimpleFunction(simpleFunction: FirSimpleFunction, data: Nothing?): FirStatement {
        if (simpleFunction.receiverParameter?.isSelfStaticObject == true) {
            simpleFunction.replaceReceiverParameter(null)
        }
        return super.transformSimpleFunction(simpleFunction, data)
    }

    override fun transformProperty(property: FirProperty, data: Nothing?): FirStatement {
        if (property.receiverParameter?.isSelfStaticObject == true) {
            property.replaceReceiverParameter(null)
        }
        return super.transformProperty(property, data)
    }

    override fun transformTypeRef(typeRef: FirTypeRef, data: Nothing?): FirTypeRef {
        return tryDropSelfStaticObjectReceiver(typeRef)
    }

    override fun transformFunctionCall(functionCall: FirFunctionCall, data: Nothing?): FirStatement {
        functionCall.apply {
            if (explicitReceiver?.typeRef?.isSelfStaticObject == true) {
                replaceExplicitReceiver(null)
            }
            if (extensionReceiver.typeRef.isSelfStaticObject) {
                replaceExtensionReceiver(FirNoReceiverExpression)
            }
            if (dispatchReceiver.typeRef.isSelfStaticObject) {
                replaceDispatchReceiver(FirNoReceiverExpression)
            }

            val updatedArguments = argumentList.transformArguments(this@FirStaticsTransformer, null)
            replaceArgumentList(updatedArguments)

            modifyIfStaticExtensionCall()
        }
        return super.transformFunctionCall(functionCall, data)
    }

    override fun transformPropertyAccessExpression(propertyAccessExpression: FirPropertyAccessExpression, data: Nothing?): FirStatement {
        if (propertyAccessExpression.explicitReceiver?.typeRef?.isSelfStaticObject == true) {
            propertyAccessExpression.replaceExplicitReceiver(null)
        }
        if (propertyAccessExpression.extensionReceiver.typeRef.isSelfStaticObject) {
            propertyAccessExpression.replaceExtensionReceiver(FirNoReceiverExpression)
        }
        if (propertyAccessExpression.dispatchReceiver.typeRef.isSelfStaticObject) {
            propertyAccessExpression.replaceDispatchReceiver(FirNoReceiverExpression)
        }
        return super.transformPropertyAccessExpression(propertyAccessExpression, data)
    }

    override fun transformCallableReferenceAccess(callableReferenceAccess: FirCallableReferenceAccess, data: Nothing?): FirStatement {
        if (callableReferenceAccess.explicitReceiver?.typeRef?.isSelfStaticObject == true) {
            callableReferenceAccess.replaceExplicitReceiver(null)
        }
        if (callableReferenceAccess.extensionReceiver.typeRef.isSelfStaticObject) {
            callableReferenceAccess.replaceExtensionReceiver(FirNoReceiverExpression)
        }
        if (callableReferenceAccess.dispatchReceiver.typeRef.isSelfStaticObject) {
            callableReferenceAccess.replaceDispatchReceiver(FirNoReceiverExpression)
        }
        return super.transformCallableReferenceAccess(callableReferenceAccess, data)
    }

    override fun transformLambdaArgumentExpression(lambdaArgumentExpression: FirLambdaArgumentExpression, data: Nothing?): FirStatement {
        val receiver = lambdaArgumentExpression.typeRef
            .coneTypeSafe<ConeKotlinType>()
            ?.receiverType(session)
            ?: return lambdaArgumentExpression

        if (receiver.isNotSelfStaticObject) return lambdaArgumentExpression

        modifyStaticExtensionLambdaArgument(lambdaArgumentExpression)
        return lambdaArgumentExpression
    }

    private fun modifyStaticExtensionLambdaArgument(lambdaArgument: FirLambdaArgumentExpression) {
        val lambda = (lambdaArgument.expression as? FirAnonymousFunctionExpression)
            ?.anonymousFunction
            ?: return
        lambda.replaceReceiverParameter(null)
        lambda.replaceTypeRef(tryDropSelfStaticObjectReceiver(lambda.typeRef))
    }

    private val FirQualifiedAccess.isStaticExtensionAccess: Boolean
        get() {
            val dispatchReceiverType = dispatchReceiver.typeRef
            if (!dispatchReceiverType.isExtensionFunctionType(session)) return false

            val receiverType = dispatchReceiverType
                .coneTypeSafe<ConeKotlinType>()
                ?.receiverType(session)
                ?: return false
            return receiverType.isSelfStaticObject
        }

    private val FirNamedReference.isInvokeReference: Boolean
        get() = toResolvedFunctionSymbol()?.callableId?.isInvoke() == true

    private fun FirNamedReference.getUpdatedInvokeFunctionSymbol(): FirNamedFunctionSymbol? {
        val functionClassId = toResolvedFunctionSymbol()?.callableId?.classId ?: return null
        val packageName = functionClassId.packageFqName
        val className = functionClassId.shortClassName.asString()
        val functionArity = FunctionClassKind.parseClassName(className, packageName)?.arity ?: return null

        val updatedFunctionClassId = StandardClassIds.FunctionN(functionArity - 1)
        return session.symbolProvider
            .getClassDeclaredFunctionSymbols(updatedFunctionClassId, OperatorNameConventions.INVOKE)
            .single()
    }

    private fun FirNamedReference.modifyReferenceIfInvoke(): FirNamedReference {
        if (!isInvokeReference) return this
        val updatedInvokeFunctionSymbol = getUpdatedInvokeFunctionSymbol() ?: return this
        return buildResolvedNamedReference {
            name = updatedInvokeFunctionSymbol.name
            resolvedSymbol = updatedInvokeFunctionSymbol
        }
    }

    private fun FirFunctionCall.modifyIfStaticExtensionCall() {
        if (!isStaticExtensionAccess) return

        replaceExplicitReceiver(null)

        val updatedDispatchReceiver: FirExpression = dispatchReceiver.transform(this@FirStaticsTransformer, null)
        replaceDispatchReceiver(updatedDispatchReceiver)

        replaceCalleeReference(calleeReference.modifyReferenceIfInvoke())

        val updatedArguments =
            buildArgumentList {
                arguments += this@modifyIfStaticExtensionCall.arguments.filterNot {
                    it.typeRef.isSelfStaticObject
                }
            }
        replaceArgumentList(updatedArguments)
    }

    /**
     * If a given type is a functional type and its receiver is a self static object,
     * then a new functional type with this receiver removed will be returned.
     */
    private fun tryDropSelfStaticObjectReceiver(type: FirTypeRef): FirTypeRef {
        if (!type.isExtensionFunctionType(session)) return type
        val coneType = type.coneType
        val receiverParameterType = coneType.receiverType(session) ?: return type
        if (receiverParameterType.isNotSelfStaticObject) return type

        val classId = coneType.classId ?: return type
        val className = classId.shortClassName.asString()
        val packageFqName = classId.packageFqName

        val functionArity = FunctionClassKind.parseClassName(className, packageFqName)?.arity ?: return type
        val updatedFunctionClassId = StandardClassIds.FunctionN(functionArity - 1)

        val updatedFunctionConeType = updatedFunctionClassId.constructClassLikeType(
            typeArguments = coneType.typeArguments.drop(1).toTypedArray(),
            isNullable = coneType.isNullable,
            attributes = coneType.attributes.remove(CompilerConeAttributes.ExtensionFunctionType)
        )

        return type.withReplacedConeType(updatedFunctionConeType)
    }
}
