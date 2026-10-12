# CallShield

CallShield is an offline Android research prototype for defending against voice-based social engineering.

## Research question

Can an on-device, explainable detector combine caller-identity verification with conversational manipulation signals to warn a user before they disclose money, credentials, or one-time codes?

## What the MVP demonstrates

- A consent-first simulated call flow.
- Caller-claim verification against a local trusted profile.
- Explainable real-time risk evidence: urgency, isolation, authority impersonation, payment requests, and identity mismatch.
- A local risk score with calibrated levels and an action recommendation.
- No network permission and no cloud inference.

## Honest scope

Android does not give every third-party app unrestricted access to ordinary cellular call audio. This MVP therefore uses a simulated/consented transcript input to validate the decision layer. The next research milestone is an on-device speech-to-text pipeline and a TensorFlow Lite classifier trained on a consented, annotated dataset.

## Suggested 11-day research plan

1. Build and run the UI on a physical Android device.
2. Record 20–30 consented example scenarios: benign, phishing, impersonation, and payment pressure.
3. Annotate tactic categories and evaluate precision, recall, and false-positive rate.
4. Add a short report with threat model, privacy analysis, ablations, and limitations.
5. Publish the code, demo video, evaluation table, and architecture diagram.

## Build

Open this folder in Android Studio and let Gradle sync. Run `testDebugUnitTest` for the offline engine tests, then deploy to an Android phone or emulator.
