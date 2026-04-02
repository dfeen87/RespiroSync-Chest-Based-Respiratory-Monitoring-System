"""
Semi-synthetic perturbation helpers (PAPER.md §5.1).

Provides `_apply_drift()` and `_apply_pause()`, which apply controlled
semi-synthetic transformations to respiratory signals to simulate frequency
drift (rising respiratory rate) and intermittent breathing pauses respectively.

Shared by validate_bidmc.py and multi_record_validation.py to avoid
code duplication.

Perturbation parameters (PAPER.md §5.1)
-----------------------------------------
DRIFT_RATIO     — resampling ratio for frequency-drift perturbation (1.6×
                  the number of tail samples → same window duration but at
                  1.6× the original respiratory rate, i.e., ~24 BPM from 15 BPM)
PAUSE_AMPLITUDE — fractional amplitude during a breathing pause (near-zero at
                  3 % of baseline; replicates an intermittent apnea event)
PAUSE_DURATION_S — default pause duration in seconds (8 s matches a clinically
                   relevant short apnea / hypopnea episode)
"""

import numpy as np
from scipy.signal import resample

# ── Perturbation constants (PAPER.md §5.1) ───────────────────────────────────

# Resampling ratio for frequency-drift perturbation (Regime 2).
# Tail of the signal is resampled to DRIFT_RATIO × original length so that
# the same duration now contains more respiratory cycles (higher rate).
DRIFT_RATIO: float = 1.6

# Amplitude scale factor applied to the pause window (Regime 3).
# 0.03 → 3 % of baseline amplitude, replicating near-zero chest motion
# during an intermittent apnea / breathing pause.
PAUSE_AMPLITUDE: float = 0.03

# Default pause duration in seconds (Regime 3).
# 8 s is representative of a short apnea event (clinically ≥ 10 s for
# formal apnea, but 8 s is used here to ensure detectability within the
# 30-second post-onset analysis window).
PAUSE_DURATION_S: float = 8.0


def _apply_drift(signal: np.ndarray, fs: float, onset_s: float) -> np.ndarray:
    """
    Semi-synthetic frequency drift (PAPER.md §5.1 row 2).

    After onset_s, compress the tail of the signal in time to simulate a
    rising respiratory rate (frequency drift).  This preserves real signal
    morphology in the stable portion while introducing a controlled perturbation.

    The tail is resampled to DRIFT_RATIO × its original length so that the
    same time window contains DRIFT_RATIO × as many respiratory cycles.
    """
    onset = int(onset_s * fs)
    stable_part = signal[:onset].copy()
    tail = signal[onset:]
    # Resample tail to DRIFT_RATIO× samples → same duration but higher rate
    tail_fast = resample(tail, int(len(tail) * DRIFT_RATIO))[:len(tail)]
    return np.concatenate([stable_part, tail_fast])


def _apply_pause(
    signal: np.ndarray,
    fs: float,
    onset_s: float,
    duration_s: float = PAUSE_DURATION_S,
) -> np.ndarray:
    """
    Semi-synthetic intermittent pause (PAPER.md §5.1 row 3).

    Near-zeros the amplitude for duration_s seconds starting at onset_s to
    simulate a breathing pause / apnea event.  The residual amplitude
    (PAUSE_AMPLITUDE × baseline) prevents numerical issues in phase estimation
    while faithfully representing the near-absent chest motion of an apnea.
    """
    out = signal.copy()
    start = int(onset_s * fs)
    end = min(int((onset_s + duration_s) * fs), len(out))
    out[start:end] *= PAUSE_AMPLITUDE   # near-zero; matches §5.1 "reduced amplitude"
    return out
