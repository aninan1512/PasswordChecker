package com.example.passwordchecker

object CommonPasswords {
    // Small starter list (you can expand later)
    private val common = setOf(
        "password", "123456", "123456789", "12345678", "qwerty", "abc123",
        "111111", "123123", "admin", "letmein", "welcome", "iloveyou",
        "monkey", "dragon", "football", "princess", "sunshine", "password1",
        "000000", "654321", "qwerty123", "login", "guest", "master"
    )

    fun isCommon(pw: String): Boolean = common.contains(pw.lowercase())
}
