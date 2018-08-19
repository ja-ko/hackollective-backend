package de.jkolodziej.hackollective

import java.io.InputStreamReader
import java.security.SecureRandom


class KeyGenerator { // only reason to create the class is to use this::class.java

    companion object {

        private val random = SecureRandom()
        private var currentIndex: Int = 0

        private val words: MutableList<String> = this::class.java.getResourceAsStream("/words.txt").use {
            InputStreamReader(it).readLines().shuffled(random).toMutableList()
        }

        fun createKey(size: Int): String {
            if (currentIndex > words.size / 3) { // reshuffle early to keep prevent running out of entropy. While this is not perfect this should be sufficient for the usecase
                currentIndex = 0
                words.shuffle()
            }
            val result = words.slice(currentIndex until (currentIndex + size)).joinToString(separator = "-")
            currentIndex += size
            return result
        }
    }


}


