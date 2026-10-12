package ai.callshield

import ai.callshield.domain.CallRiskEngine
import ai.callshield.domain.CallerProfile
import ai.callshield.domain.RiskLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CallRiskEngineTest {
    private val engine = CallRiskEngine()
    private val profile = CallerProfile("Alex", "Northstar Bank", "+1 555 0142", "northstar-blue")

    @Test fun `detects combined social engineering signals`() {
        val result = engine.assess(
            "This is bank security. Send a gift card immediately and don't tell anyone.",
            "+1 555 0199", profile
        )
        assertEquals(RiskLevel.CRITICAL, result.level)
        assertTrue(result.signals.any { it.category == "Identity" })
        assertTrue(result.signals.any { it.category == "Financial" })
    }

    @Test fun `low risk transcript has no false alarm signals`() {
        val result = engine.assess("Hello, I am calling to confirm our meeting tomorrow.", profile.verifiedNumber, profile)
        assertEquals(RiskLevel.LOW, result.level)
        assertTrue(result.signals.isEmpty())
    }

    @Test fun `does not match urgency words inside larger words`() {
        val result = engine.assess("I know we planned to speak tomorrow.", profile.verifiedNumber, profile)
        assertEquals(RiskLevel.LOW, result.level)
    }
}
