package ai.callshield

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ai.callshield.domain.CallRiskEngine
import ai.callshield.domain.CallerProfile
import ai.callshield.domain.RiskAssessment
import ai.callshield.domain.RiskLevel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { CallShieldApp() }
    }
}

@androidx.compose.runtime.Composable
private fun CallShieldApp() {
    MaterialTheme {
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF0A0F1C)) {
            var running by remember { mutableStateOf(false) }
            var transcript by remember { mutableStateOf("") }
            var assessment by remember { mutableStateOf<RiskAssessment?>(null) }
            val engine = remember { CallRiskEngine() }
            val profile = remember { CallerProfile("Alex Morgan", "Northstar Bank", "+1 555 0142", "northstar-blue") }

            if (!running) {
                HomeScreen(onStart = { running = true })
            } else {
                LiveCallScreen(
                    transcript = transcript,
                    assessment = assessment,
                    onTranscriptChange = { transcript = it },
                    onAnalyze = { assessment = engine.assess(transcript, "+1 555 0199", profile) },
                    onStop = { running = false; assessment = null; transcript = "" }
                )
            }
        }
    }
}

@androidx.compose.runtime.Composable
private fun HomeScreen(onStart: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center) {
        Text("CALLSHIELD", color = Color(0xFF7CA8FF), fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text("Offline defense against voice social engineering", color = Color.White, style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))
        Text("A research prototype that verifies caller claims and explains scam signals without sending audio to the cloud.", color = Color(0xFFB8C2D6))
        Spacer(Modifier.height(28.dp))
        Button(onClick = onStart, modifier = Modifier.fillMaxWidth()) { Text("Start simulated call") }
        Spacer(Modifier.height(16.dp))
        Text("Privacy mode: OFFLINE  •  Model: EXPLAINABLE BASELINE", color = Color(0xFF71809A), style = MaterialTheme.typography.labelSmall)
    }
}

@androidx.compose.runtime.Composable
private fun LiveCallScreen(
    transcript: String,
    assessment: RiskAssessment?,
    onTranscriptChange: (String) -> Unit,
    onAnalyze: () -> Unit,
    onStop: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f)) {
                Text("SIMULATED CALL", color = Color(0xFF7CA8FF), fontWeight = FontWeight.Bold)
                Text("Alex Morgan • Northstar Bank", color = Color.White, style = MaterialTheme.typography.titleLarge)
            }
            TextButton(onClick = onStop) { Text("End") }
        }
        Spacer(Modifier.height(16.dp))
        Text("Live transcript input", color = Color(0xFFB8C2D6))
        androidx.compose.material3.OutlinedTextField(
            value = transcript,
            onValueChange = onTranscriptChange,
            modifier = Modifier.fillMaxWidth().height(150.dp),
            placeholder = { Text("Paste a consented test transcript…") },
            minLines = 5
        )
        Spacer(Modifier.height(12.dp))
        Button(onClick = onAnalyze, modifier = Modifier.fillMaxWidth()) { Text("Analyze locally") }
        Spacer(Modifier.height(16.dp))
        assessment?.let { ResultPanel(it) }
    }
}

@androidx.compose.runtime.Composable
private fun ResultPanel(assessment: RiskAssessment) {
    val accent = when (assessment.level) {
        RiskLevel.LOW -> Color(0xFF51D88A)
        RiskLevel.MEDIUM -> Color(0xFFF6C85F)
        RiskLevel.HIGH -> Color(0xFFFF9F5A)
        RiskLevel.CRITICAL -> Color(0xFFFF657A)
    }
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF151E30))) {
                Column(Modifier.padding(16.dp)) {
                    Text("${assessment.level} RISK", color = accent, fontWeight = FontWeight.Bold)
                    Text("${assessment.score}/100", color = Color.White, style = MaterialTheme.typography.displaySmall)
                    Text(assessment.recommendation, color = Color(0xFFD7DEEC))
                }
            }
        }
        item { Text("Evidence timeline", color = Color.White, fontWeight = FontWeight.Bold) }
        items(assessment.signals) { signal ->
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF151E30))) {
                Column(Modifier.padding(14.dp)) {
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(signal.title, color = Color.White, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                        Text("+${signal.points}", color = accent)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(signal.explanation, color = Color(0xFF9CAAC0))
                }
            }
        }
        item { Text("Identity check: claimed number does not match the verified contact profile.", color = Color(0xFFFF9F5A)) }
    }
}
