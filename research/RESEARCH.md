# CallShield research brief

## Hypothesis

An explainable, on-device detector that combines identity mismatch with conversational manipulation signals can provide useful early warnings while preserving call privacy.

## Threat model

The adversary uses a phone call to impersonate a trusted institution or person and create urgency, isolation, or payment pressure. CallShield does not claim to prove who is speaking. It estimates risk and directs the user to independently verify the caller.

## Features in the MVP

| Signal family | Example | Rationale |
|---|---|---|
| Identity | Number differs from local trusted profile | Caller ID alone is not proof, but mismatch is useful evidence |
| Urgency | “Immediately”, “last chance” | Reduces time available for reflection |
| Isolation | “Do not tell anyone” | Prevents independent verification |
| Authority | “Bank security”, “police” | Exploits institutional trust |
| Payment/authentication | “Gift card”, “one-time code” | High-risk requested action |

## Evaluation plan

Create a consented dataset of short transcripts across benign calls and multiple attack tactics. Report precision, recall, false-positive rate, and per-tactic recall. Include an ablation table showing the impact of removing identity verification or each language signal.

## Limitations

The baseline is a transparent rules engine, not a trained speech model. It can miss euphemisms, sarcasm, multilingual attacks, and spoofed numbers. The app must never be presented as a guarantee of caller identity. Future work can add on-device speech recognition and a calibrated TensorFlow Lite classifier, evaluated against the same held-out scenarios.
