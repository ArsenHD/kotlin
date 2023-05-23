/*
 * Copyright 2010-2021 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.fir.plugin.generators

import org.jetbrains.kotlin.GeneratedDeclarationKey
import org.jetbrains.kotlin.descriptors.ClassKind
import org.jetbrains.kotlin.descriptors.EffectiveVisibility
import org.jetbrains.kotlin.descriptors.Modality
import org.jetbrains.kotlin.descriptors.Visibilities
import org.jetbrains.kotlin.fir.*
import org.jetbrains.kotlin.fir.analysis.checkers.declaration.primaryConstructorSymbol
import org.jetbrains.kotlin.fir.caches.FirCache
import org.jetbrains.kotlin.fir.caches.createCache
import org.jetbrains.kotlin.fir.caches.firCachesFactory
import org.jetbrains.kotlin.fir.caches.getValue
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.declarations.builder.buildProperty
import org.jetbrains.kotlin.fir.declarations.builder.buildRegularClass
import org.jetbrains.kotlin.fir.declarations.builder.buildSimpleFunction
import org.jetbrains.kotlin.fir.declarations.builder.buildValueParameter
import org.jetbrains.kotlin.fir.declarations.impl.FirDefaultPropertyGetter
import org.jetbrains.kotlin.fir.declarations.impl.FirDefaultPropertySetter
import org.jetbrains.kotlin.fir.declarations.impl.FirResolvedDeclarationStatusImpl
import org.jetbrains.kotlin.fir.declarations.utils.effectiveVisibility
import org.jetbrains.kotlin.fir.declarations.utils.isCompanion
import org.jetbrains.kotlin.fir.expressions.FirExpression
import org.jetbrains.kotlin.fir.expressions.buildResolvedArgumentList
import org.jetbrains.kotlin.fir.expressions.builder.*
import org.jetbrains.kotlin.fir.extensions.*
import org.jetbrains.kotlin.fir.extensions.predicate.LookupPredicate
import org.jetbrains.kotlin.fir.plugin.createConeType
import org.jetbrains.kotlin.fir.plugin.createDefaultPrivateConstructor
import org.jetbrains.kotlin.fir.plugin.fqn
import org.jetbrains.kotlin.fir.references.builder.buildImplicitThisReference
import org.jetbrains.kotlin.fir.references.builder.buildResolvedCallableReference
import org.jetbrains.kotlin.fir.resolve.calls.ResolvedCallArgument
import org.jetbrains.kotlin.fir.resolve.defaultType
import org.jetbrains.kotlin.fir.resolve.providers.symbolProvider
import org.jetbrains.kotlin.fir.resolve.providers.toSymbol
import org.jetbrains.kotlin.fir.symbols.SymbolInternals
import org.jetbrains.kotlin.fir.symbols.impl.*
import org.jetbrains.kotlin.fir.types.*
import org.jetbrains.kotlin.fir.types.builder.buildResolvedTypeRef
import org.jetbrains.kotlin.fir.types.impl.ConeClassLikeTypeImpl
import org.jetbrains.kotlin.name.*

private typealias NestedClassesByName = Map<Name, FirClassSymbol<*>>
private typealias FunctionsByName = Map<Name, FirFunctionSymbol<*>>
private typealias PropertiesByName = Map<Name, FirPropertySymbol>

class BuilderGenerationExtension(session: FirSession) : FirDeclarationGenerationExtension(session) {
    companion object {
        private val BUILDER_NAME = Name.identifier("Builder")
        private val BUILD_METHOD_NAME = Name.identifier("build")
        private val PREDICATE = LookupPredicate.create { annotated("WithBuilder".fqn()) }

        private val immutableCollectionIds = setOf(StandardClassIds.List, StandardClassIds.Set, StandardClassIds.Map)
        private val mutableCollectionIds = setOf(StandardClassIds.MutableList, StandardClassIds.MutableSet, StandardClassIds.MutableMap)

        private fun mutableListType(typeArguments: Array<out ConeTypeProjection>, isNullable: Boolean = false) =
            StandardClassIds.MutableList.constructClassLikeType(typeArguments, isNullable)

        private fun mutableSetType(typeArguments: Array<out ConeTypeProjection>, isNullable: Boolean = false) =
            StandardClassIds.MutableSet.constructClassLikeType(typeArguments, isNullable)

        private fun mutableMapType(typeArguments: Array<out ConeTypeProjection>, isNullable: Boolean = false) =
            StandardClassIds.MutableMap.constructClassLikeType(typeArguments, isNullable)
    }

    private val predicateBasedProvider = session.predicateBasedProvider

    private val nestedClasses: FirCache<FirClassSymbol<*>, NestedClassesByName, Nothing?> =
        session.firCachesFactory.createCache(::createNestedClasses)

    private val functions: FirCache<FirClassSymbol<*>, FunctionsByName, Nothing?> =
        session.firCachesFactory.createCache(::createFunctions)

    private val properties: FirCache<FirClassSymbol<*>, PropertiesByName, Nothing?> =
        session.firCachesFactory.createCache(::createProperties)

    private fun createNestedClasses(owner: FirClassSymbol<*>): NestedClassesByName {
        if (!predicateBasedProvider.matches(PREDICATE, owner)) return emptyMap()

        val classes = mutableMapOf<Name, FirClassSymbol<*>>()

        if (owner.doesNotHaveCompanion) {
            val companion = createCompanionObject(owner).symbol
            classes[companion.name] = companion
        }

        val builder = createBuilderClass(owner).symbol
        classes[builder.name] = builder

        return classes
    }

    private fun createFunctions(owner: FirClassSymbol<*>): FunctionsByName {
        return when {
            owner.isCompanion -> createCompanionFunctions(owner)
            owner.isBuilder -> createBuilderMethods(owner)
            else -> emptyMap()
        }
    }

    private fun createProperties(owner: FirClassSymbol<*>): PropertiesByName {
        return when {
            owner.isBuilder -> createBuilderProperties(owner)
            else -> emptyMap()
        }
    }

    private fun createCompanionFunctions(owner: FirClassSymbol<*>): FunctionsByName {
        val constructor = createDefaultPrivateConstructor(owner, Key)
        val buildMethod = (owner.classId.outerClassId?.toSymbol(session) as? FirClassSymbol<*>)
            ?.let { outerClass -> createCompanionBuildMethod(outerClass) }
            ?: return emptyMap()
        return mapOf(
            SpecialNames.INIT to constructor.symbol,
            buildMethod.name to buildMethod.symbol
        )
    }

    private val FirClassSymbol<*>.isBuilder: Boolean
        get() {
            // make sure this is the builder generated by this plugin
            val origin = origin as? FirDeclarationOrigin.Plugin ?: return false
            if (origin.key != Key) return false

            // make sure outer class exists and is annotated with @WithBuilder
            val outerClass = classId.outerClassId?.toSymbol(session) as? FirClassSymbol<*> ?: return false
            if (!predicateBasedProvider.matches(PREDICATE, outerClass)) return false

            return name == BUILDER_NAME
        }

    override fun generateNestedClassLikeDeclaration(
        owner: FirClassSymbol<*>,
        name: Name,
        context: NestedClassGenerationContext,
    ): FirClassLikeSymbol<*>? {
        if (!predicateBasedProvider.matches(PREDICATE, owner)) return null
        return nestedClasses.getValue(owner)[name]
    }

    override fun generateConstructors(context: MemberGenerationContext): List<FirConstructorSymbol> {
        val owner = context.owner
        return (functions.getValue(owner)[SpecialNames.INIT] as? FirConstructorSymbol)?.let { listOf(it) } ?: emptyList()
    }

    override fun generateFunctions(callableId: CallableId, context: MemberGenerationContext?): List<FirNamedFunctionSymbol> {
        val owner = context?.owner ?: return emptyList()
        return (functions.getValue(owner)[callableId.callableName] as? FirNamedFunctionSymbol)?.let { listOf(it) } ?: emptyList()
    }

    override fun generateProperties(callableId: CallableId, context: MemberGenerationContext?): List<FirPropertySymbol> {
        val owner = context?.owner ?: return emptyList()
        return properties.getValue(owner)[callableId.callableName]?.let { listOf(it) } ?: emptyList()
    }

    override fun getCallableNamesForClass(classSymbol: FirClassSymbol<*>, context: MemberGenerationContext): Set<Name> {
        val functionNames = functions.getValue(classSymbol).keys
        val propertyNames = properties.getValue(classSymbol).keys
        return functionNames + propertyNames
    }

    override fun getNestedClassifiersNames(classSymbol: FirClassSymbol<*>, context: NestedClassGenerationContext): Set<Name> {
        if (!predicateBasedProvider.matches(PREDICATE, classSymbol)) return emptySet()
        return nestedClasses.getValue(classSymbol).keys
    }

    @OptIn(SymbolInternals::class)
    private fun createBuilderClass(owner: FirClassSymbol<*>): FirRegularClass {
        val ownerClass = owner.fir
        val builderClassId = owner.classId.createNestedClassId(BUILDER_NAME)

        return buildRegularClass {
            moduleData = owner.moduleData
            origin = Key.origin
            status = FirResolvedDeclarationStatusImpl(
                visibility = Visibilities.Public,
                modality = Modality.FINAL,
                effectiveVisibility = ownerClass.effectiveVisibility
            )
            classKind = ClassKind.CLASS
            symbol = FirRegularClassSymbol(builderClassId)
            scopeProvider = ownerClass.scopeProvider
            name = BUILDER_NAME
            superTypeRefs += session.builtinTypes.anyType
        }.also {
            it.symbol.bind(it)
        }
    }

    @OptIn(SymbolInternals::class)
    private fun createBuilderProperties(builder: FirClassSymbol<*>): PropertiesByName {
        val owner = builder.classId.outerClassId?.toSymbol(session) as? FirClassSymbol<*> ?: return emptyMap()
        val parameters = owner.fir.primaryConstructorIfAny(session)?.fir?.valueParameters ?: return emptyMap()
        return parameters
            .mapNotNull { createBuilderProperty(owner, it) }
            .associate { it.name to it.symbol }
    }

    private fun createBuilderProperty(ownerClass: FirClassSymbol<*>,originalParameter: FirValueParameter): FirProperty? {
        val isCollection = originalParameter.isCollection
        val isPrimitive = originalParameter.isPrimitive
        val hasInitializer = originalParameter.initializer != null
        val builderClass = nestedClasses.getValue(ownerClass)[BUILDER_NAME] ?: return null
        return buildProperty {
            moduleData = originalParameter.moduleData
            origin = Key.origin
            status = FirResolvedDeclarationStatusImpl(
                visibility = Visibilities.Public,
                modality = Modality.FINAL,
                effectiveVisibility = EffectiveVisibility.Public
            ).apply {
                this@apply.isLateInit = !isCollection && !isPrimitive && !hasInitializer
            }
            isVar = !isCollection
            returnTypeRef = toMutableCollectionRefIfNeeded(originalParameter)
            name = originalParameter.name
            symbol = FirPropertySymbol(name)
            initializer = when {
                !isCollection && hasInitializer -> originalParameter.initializer
                originalParameter.isImmutableCollection -> createMutableCollectionInitializer(originalParameter)
                originalParameter.isMutableCollection -> when {
                    hasInitializer -> originalParameter.initializer
                    else -> createMutableCollectionInitializer(originalParameter)
                }
                else -> null
            }
            dispatchReceiverType = builderClass.defaultType()
            getter = FirDefaultPropertyGetter(
                source = null,
                moduleData = originalParameter.moduleData,
                origin = Key.origin,
                propertyTypeRef = returnTypeRef,
                visibility = Visibilities.Public,
                propertySymbol = symbol,
            ).also { getter ->
                getter.status = FirResolvedDeclarationStatusImpl(
                    visibility = Visibilities.Public,
                    modality = Modality.FINAL,
                    effectiveVisibility = EffectiveVisibility.Public
                ).apply {
                    isLateInit = this@buildProperty.status.isLateInit
                }
                getter.symbol.bind(getter)
                getter.containingClassForStaticMemberAttr = builderClass.getContainingClassLookupTag()
            }
            if (isVar) {
                setter = FirDefaultPropertySetter(
                    source = null,
                    moduleData = originalParameter.moduleData,
                    origin = Key.origin,
                    propertyTypeRef = returnTypeRef,
                    visibility = Visibilities.Public,
                    propertySymbol = symbol,
                ).also { setter ->
                    setter.status = FirResolvedDeclarationStatusImpl(
                        visibility = Visibilities.Public,
                        modality = Modality.FINAL,
                        effectiveVisibility = EffectiveVisibility.Public
                    ).apply {
                        isLateInit = this@buildProperty.status.isLateInit
                    }
                    setter.symbol.bind(setter)
                    setter.containingClassForStaticMemberAttr = builderClass.getContainingClassLookupTag()
                }
            }
            isLocal = false
        }.also {
            it.symbol.bind(it)
        }
    }

    private val FirValueParameter.isCollection: Boolean
        get() = isImmutableCollection || isMutableCollection

    private val FirValueParameter.isImmutableCollection: Boolean
        get() = returnTypeRef.coneType.classId in immutableCollectionIds

    private val FirValueParameter.isMutableCollection: Boolean
        get() = returnTypeRef.coneType.classId in mutableCollectionIds

    private val FirValueParameter.isPrimitive: Boolean
        get() = returnTypeRef.coneType.isPrimitive

    private fun toMutableCollectionRefIfNeeded(parameter: FirValueParameter): FirTypeRef {
        val parameterConeType = parameter.returnTypeRef.coneType
        return buildResolvedTypeRef {
            type = when (parameterConeType.classId) {
                StandardClassIds.List -> mutableListType(parameterConeType.typeArguments)
                StandardClassIds.Set -> mutableSetType(parameterConeType.typeArguments)
                StandardClassIds.Map -> mutableMapType(parameterConeType.typeArguments)
                else -> parameterConeType
            }
        }
    }

    private fun createMutableCollectionInitializer(collection: FirValueParameter): FirExpression? {
        val constructorFunctionName = when (collection.returnTypeRef.coneType.classId) {
            StandardClassIds.List, StandardClassIds.MutableList -> "mutableListOf"
            StandardClassIds.Set, StandardClassIds.MutableSet -> "mutableSetOf"
            StandardClassIds.Map, StandardClassIds.MutableMap -> "mutableMapOf"
            else -> return null
        }.let(Name::identifier)

        val constructorFunction = session.symbolProvider
            .getTopLevelFunctionSymbols(StandardClassIds.BASE_COLLECTIONS_PACKAGE, constructorFunctionName)
            .first()
        return buildFunctionCall {
            calleeReference = buildResolvedCallableReference {
                name = constructorFunctionName
                resolvedSymbol = constructorFunction
                mappedArguments = emptyMap()
            }
            argumentList = buildResolvedArgumentList(linkedMapOf())
        }
    }

    private fun createBuilderMethods(builder: FirClassSymbol<*>): FunctionsByName {
        val buildMethod = createBuilderBuildMethod(builder)?.symbol ?: return emptyMap()
        val constructor = createBuilderDefaultConstructor(builder).symbol
        return mapOf(
            buildMethod.name to buildMethod,
            SpecialNames.INIT to constructor
        )
    }

    @OptIn(SymbolInternals::class)
    private fun createBuilderBuildMethod(builder: FirClassSymbol<*>): FirSimpleFunction? {
        val builderClassId = builder.classId
        val builderBuildMethodId = CallableId(builderClassId, BUILD_METHOD_NAME)

        val owner = builderClassId.outerClassId?.toSymbol(session) as? FirClassSymbol<*> ?: return null
        val ownerConstructor = owner.primaryConstructorSymbol() ?: return null
        val builderProperties = properties.getValue(builder).values

        val buildFunctionTarget = FirFunctionTarget(labelName = null, isLambda = false)
        return buildSimpleFunction {
            moduleData = owner.moduleData
            origin = Key.origin
            status = FirResolvedDeclarationStatusImpl(
                visibility = Visibilities.Public,
                modality = Modality.FINAL,
                effectiveVisibility = EffectiveVisibility.Public
            )
            returnTypeRef = buildResolvedTypeRef { type = owner.defaultType() }
            dispatchReceiverType = builderClassId.createConeType(session)
            symbol = FirNamedFunctionSymbol(builderBuildMethodId)
            body = buildBlock {
                statements += buildReturnExpression {
                    target = buildFunctionTarget
                    result =
                        buildFunctionCall {
                            calleeReference = buildResolvedCallableReference {
                                name = ownerConstructor.name
                                resolvedSymbol = ownerConstructor
                                mappedArguments = emptyMap()
                            }

                            val parameters = ownerConstructor.fir.valueParameters
                            val callArguments: List<FirExpression> = builderProperties.map { property ->
                                buildPropertyAccessExpression {
                                    dispatchReceiver = buildThisReceiverExpression {
                                        typeRef = buildResolvedTypeRef { type = builderClassId.createConeType(session) }
                                        calleeReference = buildImplicitThisReference { boundSymbol = builder }
                                        isImplicit = true
                                    }
                                    calleeReference = buildResolvedCallableReference {
                                        name = property.name
                                        resolvedSymbol = property
                                        mappedArguments = emptyMap()
                                    }
                                }
                            }
                            val argumentMapping = linkedMapOf(*(callArguments zip parameters).toTypedArray())
                            argumentList = buildResolvedArgumentList(argumentMapping)
                        }
                }
            }
            name = BUILD_METHOD_NAME
        }.also {
            it.symbol.bind(it)
            buildFunctionTarget.bind(it)
        }
    }

    private fun createBuilderDefaultConstructor(builder: FirClassSymbol<*>): FirConstructor {
        return createDefaultPrivateConstructor(builder, Key)
    }

    @OptIn(SymbolInternals::class)
    private fun createCompanionObject(owner: FirClassSymbol<*>): FirRegularClass {
        val ownerClass = owner.fir
        val companionName = SpecialNames.DEFAULT_NAME_FOR_COMPANION_OBJECT
        val companionClassId = owner.classId.createNestedClassId(companionName)
        return buildRegularClass {
            moduleData = owner.moduleData
            origin = Key.origin
            status = FirResolvedDeclarationStatusImpl(
                visibility = Visibilities.Public,
                modality = Modality.FINAL,
                effectiveVisibility = EffectiveVisibility.Public
            ).apply {
                isCompanion = true
            }
            classKind = ClassKind.OBJECT
            scopeProvider = ownerClass.scopeProvider
            name = companionName
            symbol = FirRegularClassSymbol(companionClassId)
            superTypeRefs += session.builtinTypes.anyType
        }.also {
            it.symbol.bind(it)
        }
    }

    @OptIn(SymbolInternals::class)
    private fun createCompanionBuildMethod(owner: FirClassSymbol<*>): FirSimpleFunction? {
        val companion = (owner as? FirRegularClassSymbol)?.companionObjectSymbol ?: return null
        val companionClassId = companion.classId

        val companionType = ConeClassLikeTypeImpl(
            lookupTag = ConeClassLikeLookupTagImpl(companionClassId),
            typeArguments = emptyArray(),
            isNullable = false
        )

        val builderClass = nestedClasses.getValue(owner)[BUILDER_NAME] ?: return null
        val builderConstructor = functions.getValue(builderClass)[SpecialNames.INIT] ?: return null
        val builderBuildMethod = functions.getValue(builderClass)[BUILD_METHOD_NAME] ?: return null
        val companionBuildMethodId = CallableId(companionClassId, BUILD_METHOD_NAME)

        val buildFunctionTarget = FirFunctionTarget(labelName = null, isLambda = false)
        return buildSimpleFunction {
            moduleData = owner.moduleData
            origin = Key.origin
            status = FirResolvedDeclarationStatusImpl(
                visibility = Visibilities.Public,
                modality = Modality.FINAL,
                effectiveVisibility = EffectiveVisibility.Public
            ).apply {
                isInline = true
            }
            returnTypeRef = buildResolvedTypeRef { type = owner.defaultType() }
            dispatchReceiverType = companionType
            symbol = FirNamedFunctionSymbol(companionBuildMethodId)

            val blockParameter = buildValueParameter {
                moduleData = owner.moduleData
                origin = Key.origin
                returnTypeRef = buildResolvedTypeRef {
                    type = ConeClassLikeTypeImpl(
                        ConeClassLikeLookupTagImpl(StandardClassIds.FunctionN(1)),
                        typeArguments = arrayOf(
                            builderClass.classId.createConeType(session),
                            session.builtinTypes.unitType.coneType
                        ),
                        isNullable = false,
                        attributes = ConeAttributes.WithExtensionFunctionType
                    )
                }
                name = Name.identifier("block")
                symbol = FirValueParameterSymbol(name)
                containingFunctionSymbol = this@buildSimpleFunction.symbol
                isCrossinline = false
                isNoinline = false
                isVararg = false
            }
            valueParameters += blockParameter
            body = buildBlock {
                statements += buildReturnExpression {
                    target = buildFunctionTarget
                    result =
                        buildFunctionCall {
                            calleeReference = buildResolvedCallableReference {
                                name = BUILDER_NAME
                                resolvedSymbol = builderConstructor
                                mappedArguments = emptyMap()
                            }
                            argumentList = buildResolvedArgumentList(linkedMapOf())
                        }.let { builder ->
                            val applySymbol = session.symbolProvider
                                .getTopLevelFunctionSymbols(FqName("kotlin"), Name.identifier("apply"))
                                .first()
                            buildFunctionCall {
                                explicitReceiver = builder

                                val parameter = applySymbol.fir.valueParameters.single()
                                val argument: FirExpression = buildPropertyAccessExpression {
                                    calleeReference = buildResolvedCallableReference {
                                        name = blockParameter.name
                                        resolvedSymbol = blockParameter.symbol
                                        mappedArguments = emptyMap()
                                    }
                                }
                                val argumentMapping = linkedMapOf(argument to parameter)
                                argumentList = buildResolvedArgumentList(argumentMapping)

                                calleeReference = buildResolvedCallableReference {
                                    name = applySymbol.name
                                    resolvedSymbol = applySymbol
                                    mappedArguments = mapOf(parameter to ResolvedCallArgument.SimpleArgument(argument))
                                }
                            }
                        }.let { builder ->
                            buildFunctionCall {
                                explicitReceiver = builder
                                calleeReference = buildResolvedCallableReference {
                                    name = BUILD_METHOD_NAME
                                    resolvedSymbol = builderBuildMethod
                                    mappedArguments = emptyMap()
                                }
                                argumentList = buildResolvedArgumentList(linkedMapOf())
                            }
                        }
                }
            }
            name = BUILD_METHOD_NAME
        }.also {
            it.symbol.bind(it)
            buildFunctionTarget.bind(it)
        }
    }

    @OptIn(SymbolInternals::class)
    private val FirClassSymbol<*>.hasCompanion: Boolean
        get() = (fir as? FirRegularClass)?.companionObjectSymbol != null

    private val FirClassSymbol<*>.doesNotHaveCompanion: Boolean
        get() = !hasCompanion

    override fun FirDeclarationPredicateRegistrar.registerPredicates() {
        register(PREDICATE)
    }

    private object Key : GeneratedDeclarationKey() {
        override fun toString(): String {
            return "BuilderGenerationExtensionKey"
        }
    }
}
