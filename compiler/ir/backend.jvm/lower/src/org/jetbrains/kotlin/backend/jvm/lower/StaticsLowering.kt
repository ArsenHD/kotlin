/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.backend.jvm.lower

import org.jetbrains.kotlin.backend.common.CommonBackendContext
import org.jetbrains.kotlin.backend.common.FileLoweringPass
import org.jetbrains.kotlin.backend.common.phaser.makeIrFilePhase
import org.jetbrains.kotlin.backend.jvm.JvmBackendContext
import org.jetbrains.kotlin.backend.jvm.ir.parentClassId
import org.jetbrains.kotlin.builtins.functions.FunctionClassKind
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.symbols.IrFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrSimpleFunctionSymbol
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import org.jetbrains.kotlin.ir.types.IrSimpleType
import org.jetbrains.kotlin.ir.types.IrType
import org.jetbrains.kotlin.ir.types.IrTypeSystemContext
import org.jetbrains.kotlin.ir.types.typeOrNull
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer
import org.jetbrains.kotlin.name.CallableId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.name.StandardClassIds
import org.jetbrains.kotlin.types.checker.SimpleClassicTypeSystemContext.isCompanion
import org.jetbrains.kotlin.types.checker.SimpleClassicTypeSystemContext.isCompanionAndSSOIntersection
import org.jetbrains.kotlin.types.checker.SimpleClassicTypeSystemContext.isSelfStaticObject
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstanceOrNull

internal val staticsPhase = makeIrFilePhase(
    ::StaticsLowering,
    name = "StaticsLowering",
    description = "Remove self static object occurrences from IR tree"
)

class StaticsLowering(val context: JvmBackendContext) : FileLoweringPass {
    override fun lower(irFile: IrFile) {
//        context.symbolTable.referenceClass(IdSignature.LoweredDeclarationSignature())
        irFile.accept(IrStaticsTransformer(context.typeSystem), null)
    }
}

private class IrStaticsTransformer(typeSystem: IrTypeSystemContext)
    : IrElementTransformer<Nothing?>,
        IrTypeSystemContext by typeSystem
{
    override fun visitFunction(declaration: IrFunction, data: Nothing?): IrStatement {
        if (declaration.extensionReceiverParameter?.isSelfStaticObject == true) {
            declaration.extensionReceiverParameter = null
        }
        if (declaration.dispatchReceiverParameter?.isSelfStaticObject == true) {
            declaration.dispatchReceiverParameter = null
        }
        declaration.returnType = tryDropSelfStaticObjectReceiver(declaration.returnType)
        return super.visitFunction(declaration, data)
    }

    // TODO: sso-receiver will be on getter/setter
//    override fun visitProperty(declaration: IrProperty, data: Nothing?): IrStatement {
//        return super.visitProperty(declaration, data)
//    }


    override fun visitVariable(declaration: IrVariable, data: Nothing?): IrStatement {
        declaration.type = tryDropSelfStaticObjectReceiver(declaration.type)
        return super.visitVariable(declaration, data)
    }

    override fun visitValueParameter(declaration: IrValueParameter, data: Nothing?): IrStatement {
        declaration.type = tryDropSelfStaticObjectReceiver(declaration.type)
        return super.visitValueParameter(declaration, data)
    }

    override fun visitExpression(expression: IrExpression, data: Nothing?): IrExpression {
        expression.type = tryDropSelfStaticObjectReceiver(expression.type)
        return super.visitExpression(expression, data)
    }

    override fun visitMemberAccess(expression: IrMemberAccessExpression<*>, data: Nothing?): IrElement {
        if (expression.extensionReceiver?.type?.isSelfStaticObject() == true) {
            expression.extensionReceiver = null
        }
        if (expression.dispatchReceiver?.type?.isSelfStaticObject() == true) {
            expression.dispatchReceiver = null
        }
        return super.visitMemberAccess(expression, data)
    }

    override fun visitCall(expression: IrCall, data: Nothing?): IrElement {
        if (expression.isInvokeOnFunctionalTypeWithStaticReceiver) {
            expression.symbol.getUpdatedInvokeFunctionSymbol()?.let { newSymbol ->
                // TODO: replace symbol
//                expression.symbol = newSymbol
            }
        }
        // TODO: remove sso-argument
//        expression.addArguments()
        return super.visitCall(expression, data)
    }

    override fun visitFieldAccess(expression: IrFieldAccessExpression, data: Nothing?): IrExpression {
        if (expression.receiver?.type?.isSelfStaticObject() == true) {
            expression.receiver = null
        }
        return super.visitFieldAccess(expression, data)
    }

    // TOOD: is it function call AND property access AND callable reference access?
//    override fun visitCall(expression: IrCall, data: Nothing?): IrElement {
//        if (expression.extensionReceiver?.type?.isSelfStaticObject() == true) {
//            expression.extensionReceiver = null
//        }
//        return super.visitCall(expression, data)
//    }

    // TODO: lambda argument expression

    internal val IrValueParameter.isSelfStaticObject: Boolean
        get() = type.isSelfStaticObject()

//    private fun modifyStaticExtensionLambdaArgument(lambdaArgument: FirLambdaArgumentExpression) {
//        val lambda = (lambdaArgument.expression as? FirAnonymousFunctionExpression)
//            ?.anonymousFunction
//            ?: return
//        lambda.replaceReceiverParameter(null)
//        lambda.replaceTypeRef(tryDropSelfStaticObjectReceiver(lambda.typeRef))
//    }

    /**
     * Checks if a call is a FunctionN.invoke() call on a value of a functional type with a static receiver.
     * For example:
     * ```
     * fun foo(block: A.static.() -> Unit) {
     *     A.block()
     * }
     * ```
     * Here `block` has functional type with static receiver `A.static`.
     * Expression `A.block()` is a call to `invoke()` method of interface `Function1`.
     */
    private val IrCall.isInvokeOnFunctionalTypeWithStaticReceiver: Boolean
        get() {
            if (!isInvokeCall) return false

            val dispatchReceiverType = dispatchReceiver?.type as? IrSimpleType ?: return false
            if (!dispatchReceiverType.isExtensionFunction()) return false

            // TODO
            val contextReceiverNumber = 0
//            dispatchReceiverType
//                .getAttributes()
//                .firstIsInstanceOrNull<CompilerCon>()
//                ?.count ?: 0

//            val receiverType = dispatchReceiverType.arguments[contextReceiverNumber]
//            return receiverType.typeOrNull?.isSelfStaticObject() == true
            // TODO
            return false
        }

    private val IrCall.isInvokeCall: Boolean
        get() = symbol.owner.callableId.isInvoke()
//        get() = callableId?.isInvoke() == true

    private val IrFunction.callableId: CallableId
        get() {
            val classId = parentClassId
            return if (classId != null) {
                CallableId(classId, name)
            } else {
                CallableId(this.getPackageFragment().fqName, name)
            }
        }

    private fun CallableId.isInvoke(): Boolean =
        isKFunctionInvoke()
                || callableName.asString() == "invoke"
                && className?.asString()?.startsWith("Function") == true
                && packageName == StandardClassIds.BASE_KOTLIN_PACKAGE

    private fun CallableId.isKFunctionInvoke(): Boolean =
        callableName.asString() == "invoke"
                && className?.asString()?.startsWith("KFunction") == true
                && packageName.asString() == "kotlin.reflect"

    private fun IrFunctionSymbol.getUpdatedInvokeFunctionSymbol(): IrFunctionSymbol? {
        val functionClassId = owner.callableId.classId ?: return null
        val packageName = functionClassId.packageFqName
        val className = functionClassId.shortClassName.asString()
        val functionArity = FunctionClassKind.parseClassName(className, packageName)?.arity ?: return null

        val updatedFunctionClassId = StandardClassIds.FunctionN(functionArity - 1)
//        IrSymbolPro
//        return session.symbolProvider
//            .getClassDeclaredFunctionSymbols(updatedFunctionClassId, org.jetbrains.kotlin.util.OperatorNameConventions.INVOKE)
//            .single()
        return null
    }

//    private fun FirNamedReference.modifyReferenceIfInvoke(): FirNamedReference {
//        if (!isInvokeReference) return this
//        val updatedInvokeFunctionSymbol = getUpdatedInvokeFunctionSymbol() ?: return this
//        return buildResolvedNamedReference {
//            name = updatedInvokeFunctionSymbol.name
//            resolvedSymbol = updatedInvokeFunctionSymbol
//        }
//    }

//    private fun IrCall.modifyIfStaticExtensionCall() {
//        if (!isStaticExtensionAccess) return
//
//        replaceExplicitReceiver(null)
//
//        val updatedDispatchReceiver: FirExpression = dispatchReceiver.transform(this@FirStaticsTransformer, null)
//        replaceDispatchReceiver(updatedDispatchReceiver)
//
//        replaceCalleeReference(calleeReference.modifyReferenceIfInvoke())
//
//        val updatedArguments =
//            buildArgumentList {
//                arguments += this@modifyIfStaticExtensionCall.arguments.filterNot {
//                    it.typeRef.isSelfStaticObject
//                }
//            }
//        replaceArgumentList(updatedArguments)
//    }

    /**
     * If a given type is a functional type and its receiver is a self static object,
     * then a new functional type with this receiver removed will be returned.
     */
    private fun tryDropSelfStaticObjectReceiver(type: IrType): IrType {
        if (type !is IrSimpleType) return type
        if (!type.isExtensionFunction()) return type

//        val coneType = type.coneType
//        val receiverParameterType = coneType.receiverType(session) ?: return type
//        if (receiverParameterType.isNotSelfStaticObject) return type

//        val classId = coneType.classId ?: return type
//        val className = classId.shortClassName.asString()
//        val packageFqName = classId.packageFqName

//        val functionArity = FunctionClassKind.parseClassName(className, packageFqName)?.arity ?: return type
//        val updatedFunctionClassId = StandardClassIds.FunctionN(functionArity - 1)

//        val updatedFunctionConeType = updatedFunctionClassId.constructClassLikeType(
//            typeArguments = coneType.typeArguments.drop(1).toTypedArray(),
//            isNullable = coneType.isNullable,
//            attributes = coneType.attributes.remove(CompilerConeAttributes.ExtensionFunctionType)
//        )

//        return type.withReplacedConeType(updatedFunctionConeType)
        // TODO
        return type
    }
}
