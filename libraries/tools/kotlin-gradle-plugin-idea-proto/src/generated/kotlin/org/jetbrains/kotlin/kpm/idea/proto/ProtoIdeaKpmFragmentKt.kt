//Generated by the protocol buffer compiler. DO NOT EDIT!
// source: proto_idea_kpm.proto

package org.jetbrains.kotlin.kpm.idea.proto;

@kotlin.jvm.JvmSynthetic
internal inline fun protoIdeaKpmFragment(block: org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmFragmentKt.Dsl.() -> kotlin.Unit): org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmFragment =
  org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmFragmentKt.Dsl._create(org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmFragment.newBuilder()).apply { block() }._build()
internal object ProtoIdeaKpmFragmentKt {
  @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
  @com.google.protobuf.kotlin.ProtoDslMarker
  internal class Dsl private constructor(
    private val _builder: org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmFragment.Builder
  ) {
    internal companion object {
      @kotlin.jvm.JvmSynthetic
      @kotlin.PublishedApi
      internal fun _create(builder: org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmFragment.Builder): Dsl = Dsl(builder)
    }

    @kotlin.jvm.JvmSynthetic
    @kotlin.PublishedApi
    internal fun _build(): org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmFragment = _builder.build()

    /**
     * <code>.org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmExtras extras = 1;</code>
     */
    internal var extras: org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmExtras
      @JvmName("getExtras")
      get() = _builder.getExtras()
      @JvmName("setExtras")
      set(value) {
        _builder.setExtras(value)
      }
    /**
     * <code>.org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmExtras extras = 1;</code>
     */
    internal fun clearExtras() {
      _builder.clearExtras()
    }
    /**
     * <code>.org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmExtras extras = 1;</code>
     * @return Whether the extras field is set.
     */
    internal fun hasExtras(): kotlin.Boolean {
      return _builder.hasExtras()
    }

    /**
     * <code>.org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmFragmentCoordinates coordinates = 2;</code>
     */
    internal var coordinates: org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmFragmentCoordinates
      @JvmName("getCoordinates")
      get() = _builder.getCoordinates()
      @JvmName("setCoordinates")
      set(value) {
        _builder.setCoordinates(value)
      }
    /**
     * <code>.org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmFragmentCoordinates coordinates = 2;</code>
     */
    internal fun clearCoordinates() {
      _builder.clearCoordinates()
    }
    /**
     * <code>.org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmFragmentCoordinates coordinates = 2;</code>
     * @return Whether the coordinates field is set.
     */
    internal fun hasCoordinates(): kotlin.Boolean {
      return _builder.hasCoordinates()
    }

    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    internal class PlatformsProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * <code>repeated .org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmPlatform platforms = 3;</code>
     */
     internal val platforms: com.google.protobuf.kotlin.DslList<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmPlatform, PlatformsProxy>
      @kotlin.jvm.JvmSynthetic
      get() = com.google.protobuf.kotlin.DslList(
        _builder.getPlatformsList()
      )
    /**
     * <code>repeated .org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmPlatform platforms = 3;</code>
     * @param value The platforms to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addPlatforms")
    internal fun com.google.protobuf.kotlin.DslList<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmPlatform, PlatformsProxy>.add(value: org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmPlatform) {
      _builder.addPlatforms(value)
    }/**
     * <code>repeated .org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmPlatform platforms = 3;</code>
     * @param value The platforms to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignPlatforms")
    @Suppress("NOTHING_TO_INLINE")
    internal inline operator fun com.google.protobuf.kotlin.DslList<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmPlatform, PlatformsProxy>.plusAssign(value: org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmPlatform) {
      add(value)
    }/**
     * <code>repeated .org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmPlatform platforms = 3;</code>
     * @param values The platforms to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllPlatforms")
    internal fun com.google.protobuf.kotlin.DslList<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmPlatform, PlatformsProxy>.addAll(values: kotlin.collections.Iterable<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmPlatform>) {
      _builder.addAllPlatforms(values)
    }/**
     * <code>repeated .org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmPlatform platforms = 3;</code>
     * @param values The platforms to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllPlatforms")
    @Suppress("NOTHING_TO_INLINE")
    internal inline operator fun com.google.protobuf.kotlin.DslList<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmPlatform, PlatformsProxy>.plusAssign(values: kotlin.collections.Iterable<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmPlatform>) {
      addAll(values)
    }/**
     * <code>repeated .org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmPlatform platforms = 3;</code>
     * @param index The index to set the value at.
     * @param value The platforms to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setPlatforms")
    internal operator fun com.google.protobuf.kotlin.DslList<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmPlatform, PlatformsProxy>.set(index: kotlin.Int, value: org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmPlatform) {
      _builder.setPlatforms(index, value)
    }/**
     * <code>repeated .org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmPlatform platforms = 3;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearPlatforms")
    internal fun com.google.protobuf.kotlin.DslList<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmPlatform, PlatformsProxy>.clear() {
      _builder.clearPlatforms()
    }
    /**
     * <code>.org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmLanguageSettings language_settings = 4;</code>
     */
    internal var languageSettings: org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmLanguageSettings
      @JvmName("getLanguageSettings")
      get() = _builder.getLanguageSettings()
      @JvmName("setLanguageSettings")
      set(value) {
        _builder.setLanguageSettings(value)
      }
    /**
     * <code>.org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmLanguageSettings language_settings = 4;</code>
     */
    internal fun clearLanguageSettings() {
      _builder.clearLanguageSettings()
    }
    /**
     * <code>.org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmLanguageSettings language_settings = 4;</code>
     * @return Whether the languageSettings field is set.
     */
    internal fun hasLanguageSettings(): kotlin.Boolean {
      return _builder.hasLanguageSettings()
    }

    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    internal class DependenciesProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * <code>repeated .org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmDependency dependencies = 5;</code>
     */
     internal val dependencies: com.google.protobuf.kotlin.DslList<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmDependency, DependenciesProxy>
      @kotlin.jvm.JvmSynthetic
      get() = com.google.protobuf.kotlin.DslList(
        _builder.getDependenciesList()
      )
    /**
     * <code>repeated .org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmDependency dependencies = 5;</code>
     * @param value The dependencies to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addDependencies")
    internal fun com.google.protobuf.kotlin.DslList<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmDependency, DependenciesProxy>.add(value: org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmDependency) {
      _builder.addDependencies(value)
    }/**
     * <code>repeated .org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmDependency dependencies = 5;</code>
     * @param value The dependencies to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignDependencies")
    @Suppress("NOTHING_TO_INLINE")
    internal inline operator fun com.google.protobuf.kotlin.DslList<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmDependency, DependenciesProxy>.plusAssign(value: org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmDependency) {
      add(value)
    }/**
     * <code>repeated .org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmDependency dependencies = 5;</code>
     * @param values The dependencies to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllDependencies")
    internal fun com.google.protobuf.kotlin.DslList<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmDependency, DependenciesProxy>.addAll(values: kotlin.collections.Iterable<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmDependency>) {
      _builder.addAllDependencies(values)
    }/**
     * <code>repeated .org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmDependency dependencies = 5;</code>
     * @param values The dependencies to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllDependencies")
    @Suppress("NOTHING_TO_INLINE")
    internal inline operator fun com.google.protobuf.kotlin.DslList<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmDependency, DependenciesProxy>.plusAssign(values: kotlin.collections.Iterable<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmDependency>) {
      addAll(values)
    }/**
     * <code>repeated .org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmDependency dependencies = 5;</code>
     * @param index The index to set the value at.
     * @param value The dependencies to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setDependencies")
    internal operator fun com.google.protobuf.kotlin.DslList<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmDependency, DependenciesProxy>.set(index: kotlin.Int, value: org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmDependency) {
      _builder.setDependencies(index, value)
    }/**
     * <code>repeated .org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmDependency dependencies = 5;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearDependencies")
    internal fun com.google.protobuf.kotlin.DslList<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmDependency, DependenciesProxy>.clear() {
      _builder.clearDependencies()
    }
    /**
     * An uninstantiable, behaviorless type to represent the field in
     * generics.
     */
    @kotlin.OptIn(com.google.protobuf.kotlin.OnlyForUseByGeneratedProtoCode::class)
    internal class SourceDirectoriesProxy private constructor() : com.google.protobuf.kotlin.DslProxy()
    /**
     * <code>repeated .org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmSourceDirectory source_directories = 6;</code>
     */
     internal val sourceDirectories: com.google.protobuf.kotlin.DslList<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmSourceDirectory, SourceDirectoriesProxy>
      @kotlin.jvm.JvmSynthetic
      get() = com.google.protobuf.kotlin.DslList(
        _builder.getSourceDirectoriesList()
      )
    /**
     * <code>repeated .org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmSourceDirectory source_directories = 6;</code>
     * @param value The sourceDirectories to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addSourceDirectories")
    internal fun com.google.protobuf.kotlin.DslList<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmSourceDirectory, SourceDirectoriesProxy>.add(value: org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmSourceDirectory) {
      _builder.addSourceDirectories(value)
    }/**
     * <code>repeated .org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmSourceDirectory source_directories = 6;</code>
     * @param value The sourceDirectories to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignSourceDirectories")
    @Suppress("NOTHING_TO_INLINE")
    internal inline operator fun com.google.protobuf.kotlin.DslList<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmSourceDirectory, SourceDirectoriesProxy>.plusAssign(value: org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmSourceDirectory) {
      add(value)
    }/**
     * <code>repeated .org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmSourceDirectory source_directories = 6;</code>
     * @param values The sourceDirectories to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("addAllSourceDirectories")
    internal fun com.google.protobuf.kotlin.DslList<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmSourceDirectory, SourceDirectoriesProxy>.addAll(values: kotlin.collections.Iterable<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmSourceDirectory>) {
      _builder.addAllSourceDirectories(values)
    }/**
     * <code>repeated .org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmSourceDirectory source_directories = 6;</code>
     * @param values The sourceDirectories to add.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("plusAssignAllSourceDirectories")
    @Suppress("NOTHING_TO_INLINE")
    internal inline operator fun com.google.protobuf.kotlin.DslList<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmSourceDirectory, SourceDirectoriesProxy>.plusAssign(values: kotlin.collections.Iterable<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmSourceDirectory>) {
      addAll(values)
    }/**
     * <code>repeated .org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmSourceDirectory source_directories = 6;</code>
     * @param index The index to set the value at.
     * @param value The sourceDirectories to set.
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("setSourceDirectories")
    internal operator fun com.google.protobuf.kotlin.DslList<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmSourceDirectory, SourceDirectoriesProxy>.set(index: kotlin.Int, value: org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmSourceDirectory) {
      _builder.setSourceDirectories(index, value)
    }/**
     * <code>repeated .org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmSourceDirectory source_directories = 6;</code>
     */
    @kotlin.jvm.JvmSynthetic
    @kotlin.jvm.JvmName("clearSourceDirectories")
    internal fun com.google.protobuf.kotlin.DslList<org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmSourceDirectory, SourceDirectoriesProxy>.clear() {
      _builder.clearSourceDirectories()
    }}
}
@kotlin.jvm.JvmSynthetic
internal inline fun org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmFragment.copy(block: org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmFragmentKt.Dsl.() -> kotlin.Unit): org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmFragment =
  org.jetbrains.kotlin.kpm.idea.proto.ProtoIdeaKpmFragmentKt.Dsl._create(this.toBuilder()).apply { block() }._build()
