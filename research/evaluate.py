"""Small, dependency-free evaluation harness for the CallShield baseline."""

import json
from pathlib import Path

URGENCY = {"urgent", "immediately", "now", "last chance", "final warning"}
SECRECY = {"don't tell", "keep this secret", "do not tell", "confidential"}
PAYMENT = {"gift card", "crypto", "bitcoin", "wire transfer", "one-time code", "otp"}
AUTHORITY = {"police", "tax office", "bank security", "government", "court"}


def score(text: str) -> int:
    lowered = text.lower()
    points = 0
    if any(term in lowered for term in URGENCY):
        points += 18
    if any(term in lowered for term in SECRECY):
        points += 24
    if any(term in lowered for term in PAYMENT):
        points += 28
    if any(term in lowered for term in AUTHORITY):
        points += 16
    return min(points, 100)


def main() -> None:
    rows = json.loads(Path(__file__).with_name("scenarios.json").read_text())
    predictions = [score(row["text"]) >= 20 for row in rows]
    actual = [row["label"] == "scam" for row in rows]
    tp = sum(p and y for p, y in zip(predictions, actual))
    fp = sum(p and not y for p, y in zip(predictions, actual))
    fn = sum(not p and y for p, y in zip(predictions, actual))
    precision = tp / (tp + fp) if tp + fp else 0.0
    recall = tp / (tp + fn) if tp + fn else 0.0
    print(f"scenarios={len(rows)} true_positive={tp} false_positive={fp} false_negative={fn}")
    print(f"precision={precision:.2f} recall={recall:.2f}")


if __name__ == "__main__":
    main()
