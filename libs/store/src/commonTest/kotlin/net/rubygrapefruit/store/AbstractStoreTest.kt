@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

import net.rubygrapefruit.file.fixtures.AbstractFileTest

abstract class AbstractStoreTest : AbstractFileTest() {
    fun withStore(action: (Store) -> Unit) {
        Store.open(fixture.testDir).use {
            action(it)
        }
    }

    fun Store.values(): List<String> {
        val result = mutableListOf<String>()
        accept(object : ContentVisitor {
            override fun value(name: String, details: ContentVisitor.ValueInfo) {
                result.add(name)
            }
        })
        return result
    }
}