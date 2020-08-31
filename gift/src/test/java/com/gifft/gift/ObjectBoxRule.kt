package com.gifft.gift

import io.objectbox.BoxStore
import org.junit.rules.ExternalResource
import java.io.File

class ObjectBoxRule : ExternalResource() {
    companion object {
        private val TEST_DIRECTORY = File("build/intermediates/objectbox-test/test-db")
    }

    lateinit var store: BoxStore

    override fun before() {
        super.before()

        BoxStore.deleteAllFiles(TEST_DIRECTORY)

        store =
            MyObjectBox.builder()
                .directory(TEST_DIRECTORY)
                .build()
    }

    override fun after() {
        super.after()

        store.close()
        BoxStore.deleteAllFiles(TEST_DIRECTORY)
    }
}
