# CallShield

An offline Android research prototype for detecting voice-based social-engineering tactics with explainable evidence.

## What it demonstrates

- A consent-first simulated call flow.
- Local caller-identity verification against a trusted profile.
- Inspectable signals for urgency, secrecy, authority impersonation, payment requests, and identity mismatch.
- A local 0–100 risk score with `LOW`, `MEDIUM`, `HIGH`, and `CRITICAL` levels.
- A plain-language recommendation for the next safe action.
- No network permission and no cloud inference in the current prototype.

The core decision boundary is implemented in [`CallRiskEngine.kt`](app/src/main/java/ai/callshield/domain/CallRiskEngine.kt). It returns the evidence behind a result instead of hiding the decision inside a black-box label.

## Scope and limitations

This is a research prototype, not a production call-blocking app. Android does not provide every third-party app unrestricted access to ordinary cellular-call audio, so the current MVP evaluates simulated or consented transcript input. A future version could add on-device speech-to-text and a classifier trained on a consented, annotated dataset.

## Run locally

Open the repository in Android Studio, let Gradle sync, and run the app on an emulator or Android device. The offline unit tests can be run with:

```bash
./gradlew testDebugUnitTest
```

The test suite covers the risk engine and its explainable signals.

## Research materials

- [`research/RESEARCH.md`](research/RESEARCH.md) — evaluation plan and threat-model notes.
- [`research/scenarios.json`](research/scenarios.json) — sample scenarios.
- [`research/evaluate.py`](research/evaluate.py) — evaluation helper.

## Roadmap

1. Add consented speech-to-text input.
2. Measure precision, recall, false positives, and calibration on annotated scenarios.
3. Add a privacy review, threat model, and device-side performance measurements.
4. Publish a short demo and reproducible evaluation report.
