package net.rubygrapefruit.toml

sealed interface TomlValue

class TomlTable(val values: List<KeyValue>) : TomlValue

class TomlString(val value: String) : TomlValue

class TomlInt(val value: Int) : TomlValue

class TomlBoolean(val value: Boolean) : TomlValue

class TomlArray(val value: List<TomlValue>) : TomlValue

class KeyValue(val name: String, val value: TomlValue)
