package com.example.passwordchecker

import kotlin.random.Random

object PasswordGenerator {
    private const val LOWER = "abcdefghijklmnopqrstuvwxyz"
    private const val UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private const val DIGITS = "0123456789"
    private const val SPECIAL = "!@#\$%^&*()-_=+[]{};:,.<>?/"

    data class Options(
        val length: Int,
        val includeUpper: Boolean,
        val includeLower: Boolean,
        val includeDigits: Boolean,
        val includeSpecial: Boolean
    )

    fun generate(options: Options): String {
        val length = options.length.coerceIn(8, 64)

        val pools = mutableListOf<String>()
        if (options.includeLower) pools += LOWER
        if (options.includeUpper) pools += UPPER
        if (options.includeDigits) pools += DIGITS
        if (options.includeSpecial) pools += SPECIAL

        // If user unchecks everything, fall back to lower+digits
        if (pools.isEmpty()) {
            pools += LOWER
            pools += DIGITS
        }

        // Guarantee at least one char from each selected pool
        val chars = mutableListOf<Char>()
        for (pool in pools) {
            chars += pool.random(Random)
        }

        val all = pools.joinToString("")
        while (chars.size < length) {
            chars += all.random(Random)
        }

        // Shuffle
        chars.shuffle(Random)

        return chars.joinToString("")
    }

    private fun String.random(r: Random): Char = this[r.nextInt(this.length)]
}
