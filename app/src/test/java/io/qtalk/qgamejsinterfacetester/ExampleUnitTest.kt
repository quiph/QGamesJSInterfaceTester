package io.qtalk.qgamejsinterfacetester

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun is_generateSHA1Working(){
        assertEquals("cdc9b8e03a9e85e02a425983028b602ecdd7bdd5", "QTalkTest1".generateSHA1())
    }

    @Test
    fun is_generateMD5Working(){
        assertEquals("3727f0766be9219b8de7610d176ccf1c", "QTalkTest1".generateMD5())
    }
}
