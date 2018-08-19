package de.jkolodziej.hackollective

import io.kotlintest.matchers.collections.shouldHaveSize
import io.kotlintest.matchers.numerics.shouldBeGreaterThan
import io.kotlintest.matchers.string.shouldMatch
import org.junit.jupiter.api.RepeatedTest
import org.junit.jupiter.api.Test

internal class KeyGeneratorTest {

    @RepeatedTest(200)
    fun createsLongKeys() {
        KeyGenerator.createKey(4).length.shouldBeGreaterThan(15)
    }

    @Test
    fun shouldNotRepeatKeys() {
        val keys = (1..10000).map { KeyGenerator.createKey(3) }
        keys.distinct().shouldHaveSize(10000)
    }

    @RepeatedTest(200)
    fun shouldBeUrlCompatible() {
        KeyGenerator.createKey(3).shouldMatch("[a-z\\-]+")
    }


}