package com.example.passwordchecker

data class EvaluationResult(
    val score: Int,
    val label: String,
    val rules: List<RuleResult>,
    val suggestions: List<String>
)

data class RuleResult(val name: String, val passed: Boolean)

object StrengthEvaluator {

    fun evaluate(password: String): EvaluationResult {
        val pw = password
        if (pw.isEmpty()) {
            return EvaluationResult(
                score = 0,
                label = "Very Weak",
                rules = defaultRules(passed = false, length = 0, common = false, repeats = false, sequential = false, variety = 0),
                suggestions = listOf("Start typing a password to see feedback.")
            )
        }

        val length = pw.length
        val hasLower = pw.any { it.isLowerCase() }
        val hasUpper = pw.any { it.isUpperCase() }
        val hasDigit = pw.any { it.isDigit() }
        val hasSpecial = pw.any { !it.isLetterOrDigit() }

        val variety = listOf(hasLower, hasUpper, hasDigit, hasSpecial).count { it }

        val isCommon = CommonPasswords.isCommon(pw)
        val hasRepeats = hasRepeatedRun(pw, runLen = 4)
        val isSequential = hasSequentialPattern(pw, seqLen = 4)

        var score = 0

        // Length points
        score += when {
            length >= 16 -> 35
            length >= 12 -> 25
            length >= 8  -> 15
            else         -> 5
        }

        // Variety points
        if (hasLower) score += 10
        if (hasUpper) score += 10
        if (hasDigit) score += 10
        if (hasSpecial) score += 15

        // Bonuses
        if (variety >= 3) score += 10
        if (length >= 12 && variety >= 3) score += 10

        // Penalties
        if (isCommon) score -= 40
        if (hasRepeats) score -= 15
        if (isSequential) score -= 15

        // Clamp
        score = score.coerceIn(0, 100)

        val label = when (score) {
            in 0..20 -> "Very Weak"
            in 21..40 -> "Weak"
            in 41..60 -> "Medium"
            in 61..80 -> "Strong"
            else -> "Very Strong"
        }

        val rules = listOf(
            RuleResult("At least 12 characters", length >= 12),
            RuleResult("Contains lowercase letter", hasLower),
            RuleResult("Contains uppercase letter", hasUpper),
            RuleResult("Contains a number", hasDigit),
            RuleResult("Contains a special character", hasSpecial),
            RuleResult("Not a common password", !isCommon),
            RuleResult("No long repeated characters (e.g., aaaa)", !hasRepeats),
            RuleResult("No simple sequences (e.g., 1234 / abcd)", !isSequential)
        )

        val suggestions = buildSuggestions(
            length = length,
            hasLower = hasLower,
            hasUpper = hasUpper,
            hasDigit = hasDigit,
            hasSpecial = hasSpecial,
            isCommon = isCommon,
            hasRepeats = hasRepeats,
            isSequential = isSequential
        )

        return EvaluationResult(score, label, rules, suggestions)
    }

    private fun buildSuggestions(
        length: Int,
        hasLower: Boolean,
        hasUpper: Boolean,
        hasDigit: Boolean,
        hasSpecial: Boolean,
        isCommon: Boolean,
        hasRepeats: Boolean,
        isSequential: Boolean
    ): List<String> {
        val s = mutableListOf<String>()

        if (length < 12) s += "Increase length to 12+ characters."
        if (!hasLower) s += "Add at least one lowercase letter."
        if (!hasUpper) s += "Add at least one uppercase letter."
        if (!hasDigit) s += "Add at least one number."
        if (!hasSpecial) s += "Add at least one special character (e.g., !@#)."
        if (isCommon) s += "Avoid common passwords—use something unique."
        if (hasRepeats) s += "Avoid long repeated characters like 'aaaa' or '1111'."
        if (isSequential) s += "Avoid simple sequences like '1234' or 'abcd'."

        if (s.isEmpty()) s += "Looks great! Consider using a password manager for best security."
        return s
    }

    private fun hasRepeatedRun(pw: String, runLen: Int): Boolean {
        if (pw.length < runLen) return false
        var count = 1
        for (i in 1 until pw.length) {
            if (pw[i] == pw[i - 1]) {
                count++
                if (count >= runLen) return true
            } else {
                count = 1
            }
        }
        return false
    }

    private fun hasSequentialPattern(pw: String, seqLen: Int): Boolean {
        if (pw.length < seqLen) return false

        val lowered = pw.lowercase()

        fun isSeqAt(start: Int): Boolean {
            var inc = true
            var dec = true
            for (i in 1 until seqLen) {
                val a = lowered[start + i - 1].code
                val b = lowered[start + i].code
                inc = inc && (b == a + 1)
                dec = dec && (b == a - 1)
            }
            return inc || dec
        }

        for (i in 0..(lowered.length - seqLen)) {
            if (isSeqAt(i)) return true
        }
        return false
    }

    private fun defaultRules(
        passed: Boolean,
        length: Int,
        common: Boolean,
        repeats: Boolean,
        sequential: Boolean,
        variety: Int
    ): List<RuleResult> {
        return listOf(
            RuleResult("At least 12 characters", passed && length >= 12),
            RuleResult("Contains lowercase letter", passed && variety >= 1),
            RuleResult("Contains uppercase letter", passed && variety >= 2),
            RuleResult("Contains a number", passed && variety >= 3),
            RuleResult("Contains a special character", passed && variety >= 4),
            RuleResult("Not a common password", passed && !common),
            RuleResult("No long repeated characters (e.g., aaaa)", passed && !repeats),
            RuleResult("No simple sequences (e.g., 1234 / abcd)", passed && !sequential)
        )
    }
}
