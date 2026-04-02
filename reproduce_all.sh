#!/usr/bin/env bash
# reproduce_all.sh — Reproduce all respiratory figures and validation results
# ============================================================================
# Running this script from the repository root re-generates every figure and
# CSV result referenced in PAPER.md, using only the offline synthetic fallback
# so that no internet connection or PhysioNet account is required.
#
# Usage
# -----
#   bash reproduce_all.sh                 # offline mode (default, reproducible)
#   bash reproduce_all.sh --physionet     # real data (requires internet + wfdb)
#
# Output
# ------
#   validation/figures/regime1_stable.png          — PAPER.md §5.1 row 1
#   validation/figures/regime2_drift.png           — PAPER.md §5.1 row 2
#   validation/figures/regime3_pause.png           — PAPER.md §5.1 row 3
#   validation/figures/comparison_baselines.png    — PAPER.md §5.2
#   results/metrics.csv                            — per-record metrics
#   results/summary.csv                            — mean ± SD (PAPER.md Table 1)
#
# Reproducibility guarantee
# -------------------------
# All randomness is seeded:
#   - Single-record validation uses seed=42 (physionet_loader.generate_synthetic_resp)
#   - Multi-record validation uses seed=record_id (one deterministic signal per record)
# The phase–memory pipeline itself (pipeline.py) is fully deterministic (no RNG).
#
# Operator parameters used (PAPER.md §4.2 / §8):
#   fs             = 50 Hz
#   bandpass       = 0.1–0.5 Hz (2nd-order Butterworth)
#   M              = 150 samples  (≈ 3 s phase-memory window)
#   baseline_samp  = 250 samples  (≈ 5 s calibration window)
#   alpha (α)      = 2.0          (threshold sensitivity)
# ============================================================================

set -euo pipefail

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
SYNTHETIC_FLAG="--synthetic"

# ── Parse arguments ──────────────────────────────────────────────────────────
for arg in "$@"; do
    case "$arg" in
        --physionet)
            SYNTHETIC_FLAG=""
            echo "[INFO] Real-data mode — downloading from PhysioNet."
            ;;
        --synthetic)
            SYNTHETIC_FLAG="--synthetic"
            ;;
        *)
            echo "[WARN] Unknown argument: $arg  (ignored)"
            ;;
    esac
done

echo "========================================================================"
echo " RespiroSync — reproduce_all.sh"
echo " Repository: ${REPO_ROOT}"
echo " Mode: $([ -n "${SYNTHETIC_FLAG}" ] && echo "offline (synthetic)" || echo "online (PhysioNet)")"
echo "========================================================================"

# ── Step 0: Install Python dependencies ──────────────────────────────────────
echo ""
echo "[0/2] Installing validation dependencies …"
pip install --quiet -r "${REPO_ROOT}/validation/requirements.txt"

# ── Step 1: Single-record validation — generates the four manuscript figures ─
echo ""
echo "[1/2] Single-record validation (PAPER.md §5) …"
echo "      Figures → validation/figures/"
python "${REPO_ROOT}/validation/validate_bidmc.py" ${SYNTHETIC_FLAG}

# ── Step 2: Multi-record validation — generates CSV results (PAPER.md §5.3) ─
echo ""
echo "[2/2] Multi-record validation — N=5 records (PAPER.md §5.3) …"
echo "      Results → results/"
python "${REPO_ROOT}/validation/multi_record_validation.py" \
    --n-records 5 ${SYNTHETIC_FLAG}

echo ""
echo "========================================================================"
echo " Done.  All outputs are deterministic and reproducible."
echo ""
echo " Figures:"
echo "   validation/figures/regime1_stable.png"
echo "   validation/figures/regime2_drift.png"
echo "   validation/figures/regime3_pause.png"
echo "   validation/figures/comparison_baselines.png"
echo ""
echo " Results:"
echo "   results/metrics.csv"
echo "   results/summary.csv"
echo "========================================================================"
