@file:OptIn(ExperimentalStdlibApi::class)

package net.rubygrapefruit.store

import net.rubygrapefruit.file.Directory
import net.rubygrapefruit.file.fixtures.AbstractFileTest
import kotlin.properties.Delegates
import kotlin.test.assertEquals

abstract class AbstractStoreTest : AbstractFileTest() {
    val testStore: Directory
        get() = fixture.testDir

    fun withStore(action: (Store) -> Unit) {
        Store.open(testStore).use {
            action(it)
        }
        verifyAfterClose()
    }

    fun withMaxChanges(maxChanges: Int, action: (Store) -> Unit) {
        Store.open(testStore, maxChanges = maxChanges).use {
            action(it)
        }
        verifyAfterClose()
    }

    fun withCompactedStore(action: (Store) -> Unit) {
        Store.open(testStore, compact = true).use {
            action(it)
        }
        verifyAfterClose()
    }

    fun withDiscardedStore(action: (Store) -> Unit = {}) {
        Store.open(testStore, discard = true).use {
            action(it)
        }
        verifyAfterClose()
    }

    private fun verifyAfterClose() {
        // Should always be 3 files
        assertEquals(3, testStore.listEntries().get().size)
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

    fun Store.changes(): Int {
        return storeInfo().totalChanges
    }

    fun Store.changesSinceCompaction(): Int {
        return storeInfo().nonCompactedChanges
    }

    fun Store.generation(): Int {
        return storeInfo().generation
    }

    fun Store.storeInfo(): ContentVisitor.StoreInfo {
        var result by Delegates.notNull<ContentVisitor.StoreInfo>()
        accept(object : ContentVisitor {
            override fun store(detail: ContentVisitor.StoreInfo) {
                result = detail
            }
        })
        return result
    }

    sealed class StoreFile(val name: String) {
        data object Metadata : StoreFile("store.bin")
    }
}