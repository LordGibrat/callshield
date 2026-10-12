package ai.callshield.domain

enum class RiskLevel { LOW, MEDIUM, HIGH, CRITICAL }

data class CallerProfile(
    val displayName: String,
    val organization: String,
    val verifiedNumber: String,
    val verificationPhrase: String
)

data class CallSignal(
    val title: String,
    val explanation: String,
    val points: Int,
    val category: String
)

data class RiskAssessment(
    val score: Int,
    val level: RiskLevel,
    val signals: List<CallSignal>,
    val recommendation: String
)

data class TranscriptTurn(
    val speaker: String,
    val text: String,
    val isIncoming: Boolean
)
