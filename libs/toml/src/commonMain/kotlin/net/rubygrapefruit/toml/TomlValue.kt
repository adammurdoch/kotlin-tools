package net.rubygrapefruit.toml

sealed interface TomlValue

class TomlTable(val values: List<Value>) : TomlValue

class TomlString(val value: String) : TomlValue

class TomlInt(val value: Int) : TomlValue

class TomlBoolean(val value: Boolean) : TomlValue

class TomlArray(val value: List<TomlValue>) : TomlValue

class Value(val name: String, val value: TomlValue)
