package ai.callshield.domain

/**
 * Offline, explainable baseline for the research prototype.
 *
 * It deliberately returns evidence instead of a black-box label. A future
 * TensorFlow Lite classifier can be added as another signal provider while
 * this safety layer remains the final, inspectable decision boundary.
 */
class CallRiskEngine {
    private val urgencyTerms = setOf("urgent", "immediately", "now", "last chance", "final warning")
    private val secrecyTerms = setOf("don't tell", "keep this secret", "do not tell", "confidential")
    private val paymentTerms = setOf("gift card", "crypto", "bitcoin", "wire transfer", "one-time code", "otp")
    private val authorityTerms = setOf("police", "tax office", "bank security", "government", "court")

    fun assess(
        transcript: String,
        claimedNumber: String,
        profile: CallerProfile?
    ): RiskAssessment {
        val normalized = transcript.lowercase()
        val signals = mutableListOf<CallSignal>()

        fun matches(terms: Set<String>) = terms.filter { term ->
            if (term.contains(' ')) normalized.contains(term)
            else Regex("\\b${Regex.escape(term)}\\b").containsMatchIn(normalized)
        }

        val urgency = matches(urgencyTerms)
        if (urgency.isNotEmpty()) signals += CallSignal(
            "Artificial urgency",
            "Pressure language detected: ${urgency.joinToString()}", 18, "Manipulation"
        )

        val secrecy = matches(secrecyTerms)
        if (secrecy.isNotEmpty()) signals += CallSignal(
            "Isolation attempt",
            "The caller discourages you from checking with someone else.", 24, "Manipulation"
        )

        val payment = matches(paymentTerms)
        if (payment.isNotEmpty()) signals += CallSignal(
            "High-risk payment request",
            "Sensitive payment or authentication terms detected: ${payment.joinToString()}", 28, "Financial"
        )

        val authority = matches(authorityTerms)
        if (authority.isNotEmpty()) signals += CallSignal(
            "Authority impersonation",
            "The caller invokes institutional authority: ${authority.joinToString()}", 16, "Impersonation"
        )

        if (profile != null && claimedNumber != profile.verifiedNumber) signals += CallSignal(
            "Caller identity mismatch",
            "This number is not the verified number for ${profile.displayName}.", 30, "Identity"
        )

        val score = signals.sumOf { it.points }.coerceIn(0, 100)
        val level = when {
            score >= 70 -> RiskLevel.CRITICAL
            score >= 45 -> RiskLevel.HIGH
            score >= 20 -> RiskLevel.MEDIUM
            else -> RiskLevel.LOW
        }
        val recommendation = when (level) {
            RiskLevel.CRITICAL -> "End the call. Do not share codes, credentials, or money. Verify through an official number."
            RiskLevel.HIGH -> "Pause. Ask for independent verification before taking any action."
            RiskLevel.MEDIUM -> "Stay cautious. Do not reveal sensitive information under pressure."
            RiskLevel.LOW -> "No strong scam pattern detected. Continue normal caution."
        }
        return RiskAssessment(score, level, signals, recommendation)
    }
}
