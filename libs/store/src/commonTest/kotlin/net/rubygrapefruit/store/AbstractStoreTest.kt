@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

import net.rubygrapefruit.file.Directory
import net.rubygrapefruit.file.fixtures.AbstractFileTest
import kotlin.properties.Delegates

abstract class AbstractStoreTest : AbstractFileTest() {
    val testStore: Directory
        get() = fixture.testDir

    fun withStore(action: (Store) -> Unit) {
        Store.open(testStore).use {
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

    fun Store.indexChanges(): Int {
        var result by Delegates.notNull<Int>()
        accept(object : ContentVisitor {
            override fun store(detail: ContentVisitor.StoreInfo) {
                result = detail.changes
            }
        })
        return result
    }

    fun Store.generation(): Int {
        var result by Delegates.notNull<Int>()
        accept(object : ContentVisitor {
            override fun store(detail: ContentVisitor.StoreInfo) {
                result = detail.generation
            }
        })
        return result
    }

    sealed class StoreFile(val name: String) {
        data object Metadata : StoreFile("store.bin")
    }
}