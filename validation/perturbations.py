"""
Semi-synthetic perturbation helpers (PAPER.md §5.1).

Shared by validate_bidmc.py and multi_record_validation.py to avoid
code duplication.
"""

import numpy as np
from scipy.signal import resample


def _apply_drift(signal: np.ndarray, fs: float, onset_s: float) -> np.ndarray:
    """
    Semi-synthetic frequency drift (PAPER.md §5.1 row 2).

    After onset_s, compress the tail of the signal in time to simulate a
    rising respiratory rate (frequency drift).  This preserves real signal
    morphology in the stable portion while introducing a controlled perturbation.
    """
    onset = int(onset_s * fs)
    stable_part = signal[:onset].copy()
    tail = signal[onset:]
    # Resample tail to 1.6× samples → same duration but higher rate
    tail_fast = resample(tail, int(len(tail) * 1.6))[:len(tail)]
    return np.concatenate([stable_part, tail_fast])


def _apply_pause(
    signal: np.ndarray,
    fs: float,
    onset_s: float,
    duration_s: float = 8.0,
) -> np.ndarray:
    """
    Semi-synthetic intermittent pause (PAPER.md §5.1 row 3).

    Near-zeros the amplitude for duration_s seconds starting at onset_s to
    simulate a breathing pause / apnea event.
    """
    out = signal.copy()
    start = int(onset_s * fs)
    end = min(int((onset_s + duration_s) * fs), len(out))
    out[start:end] *= 0.03   # near-zero; matches §5.1 "reduced amplitude"
    return out
