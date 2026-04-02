# A Proof-of-Concept Deterministic Phase–Memory Operator for Respiratory Instability Detection Using Chest-Mounted Smartphone IMU Signals

**Marcel Krüger¹ · Don Michael Feeney Jr.²**

¹ Independent Researcher, Germany · ² Independent Researcher, USA

📧 marcelkrueger092@gmail.com · dfeen87@gmail.com  
🔗 ORCID (M.K.): [0009-0002-5709-9729](https://orcid.org/0009-0002-5709-9729) · ORCID (D.M.F.): [0009-0003-1350-4160](https://orcid.org/0009-0003-1350-4160)

> **Published in:** *Smart Wearable Technology (SWT)* · DOI: *(assigned by journal)*

---

## Abstract

Wearable respiratory monitoring often relies on heuristic pipelines or opaque machine-learning models, which can limit interpretability and auditability in safety-sensitive or clinical-adjacent contexts. Here, we present a proof-of-concept **deterministic phase–memory operator** for respiratory instability detection using chest-mounted smartphone inertial measurement unit (IMU) signals. The proposed instability metric **ΔΦ(t)** quantifies deviations of instantaneous phase velocity from short-term phase memory, enabling transparent threshold-based decision logic without training dependence.

A controlled validation protocol based on **N = 5** publicly available BIDMC respiratory recordings with semi-synthetic perturbations was used to examine representative deviation regimes, including frequency drift, intermittent pauses, and burst irregularities. Performance was compared against low-overhead baseline methods based on RMS-envelope and FFT-peak tracking. Within this limited proof-of-concept setting, the framework showed reproducible responses to structured perturbations and remained compatible with linear-time, streaming-capable implementation.

> ⚠️ **Scope Notice:** The present study should be interpreted as a *methodological investigation* of interpretable chest-based IMU respiratory instability sensing rather than as a clinical validation study. Further work is required to evaluate physiological specificity, robustness across heterogeneous cohorts, adaptive baseline strategies, and performance under broader real-world motion conditions.

**Keywords:** wearable respiratory monitoring · chest-mounted IMU sensing · deterministic signal processing · proof-of-concept study · phase-based instability detection · interpretable health monitoring

---

## Table of Contents

1. [Introduction](#1-introduction)
2. [Signal Acquisition and Preprocessing](#2-signal-acquisition-and-preprocessing)
3. [Phase–Memory Operator](#3-phasememory-operator)
4. [Instability Metric and Decision Logic](#4-instability-metric-and-decision-logic)
5. [Experimental Protocol](#5-experimental-protocol)
6. [Results](#6-results)
7. [Wearable Feasibility and Implementation](#7-wearable-feasibility-and-implementation)
8. [Reproducible Implementation and Validation Repository](#8-reproducible-implementation-and-validation-repository)
9. [Discussion](#9-discussion)
10. [Potential Future Directions](#10-potential-future-directions)
11. [Conclusion](#11-conclusion)
- [Ethics Statement](#ethics-statement)
- [Data Availability](#data-availability-statement)
- [References](#references)
- [Appendix A — REST API](#appendix-a-reproducibility-layer-minimal-rest-api)

---

## 1  Introduction

Wearable respiratory monitoring has become increasingly important for sleep-related observation, longitudinal remote monitoring, and the detection of changes in breathing dynamics outside episodic clinical encounters. Remote digital health technologies are now recognized as a relevant component of modern respiratory care, supporting continuous physiological observation and personalized management strategies [21]. Recent reviews document the rapid expansion of wearable and remote respiratory monitoring technologies across both clinical and ambulatory settings [7, 10, 14, 15, 17, 20].

A broad spectrum of respiratory rate (RR) estimation algorithms has been developed using biosignals such as electrocardiography (ECG) and photoplethysmography (PPG), as systematically reviewed in [5]. In parallel, contact-based sensing approaches that directly capture chest wall motion have been investigated using belts, strain sensors, accelerometers, and inertial measurement units (IMUs) [6]. More recently, smartphone-based IMU systems have been explored for respiratory kinematics monitoring, remote health assessment, and comparison against reference instrumentation [8, 9, 22].

Beyond inertial and mechanical sensing, alternative wearable technologies such as fiber-optic systems [12] and humidity-based flexible sensor platforms with wireless smartphone integration [16] have further broadened the technological landscape.

Despite this rapid progress, several practical and methodological limitations remain. Many wearable systems exhibit weak mechanical coupling to respiratory motion, rely on indirect physiological proxies with limited specificity, or employ data-driven machine-learning pipelines for respiratory rate estimation [13]. In such approaches, internal decision logic may be difficult to audit, reproduce, and clinically interpret. Systematic analyses have therefore emphasized the importance of explainability in wearable data analytics, highlighting risks associated with opaque model behavior and limited transparency [19]. Likewise, recent work on wearable sensor-based human activity recognition has investigated trade-offs between performance, computational complexity, and interpretability in neural architectures, underscoring the need for transparent modeling strategies in wearable systems [23].

Beyond signal acquisition alone, structured and reproducible pathways from wearable sensor data to clinically meaningful digital biomarkers have been identified as critical for digital health translation [18]. Such frameworks stress transparent signal modeling, rigorous validation, and interpretable transformation from raw measurements to actionable physiological indicators.

For wearable systems operating in safety-sensitive or clinical-adjacent contexts, **deterministic and interpretable signal-processing frameworks are therefore desirable.** Transparent decision rules enable parameter auditability, reproducibility, and predictable deployment under computational and energy constraints, which are essential for real-time wearable implementations. Recent systematic reviews on low-power wearable device development likewise emphasize the importance of efficient signal acquisition and computationally lightweight processing pipelines for continuous vital-parameter monitoring [24].

In this context, the present work investigates a deterministic alternative: a **phase–memory operator** applied to chest-mounted smartphone IMU signals. The underlying instability functional and operator formalism were previously introduced in a platform-agnostic framework for wearable physiological regime detection [25]. Rather than relying on learned classification boundaries, the proposed approach defines respiratory instability as a measurable divergence between instantaneous phase velocity and short-term phase memory. Whereas much prior work focuses primarily on respiratory rate estimation, the present study shifts the emphasis toward **phase-based instability detection**, with the goal of capturing transient deviations beyond average rate metrics.

> **Scope:** The present manuscript should be read as a proof-of-concept methodological study rather than as a clinical validation paper. The analysis is based on a limited set of publicly available BIDMC respiratory recordings combined with controlled semi-synthetic perturbations.

The design objectives of the proposed framework are:

| Objective | Description |
|---|---|
| **Determinism** | Fully specified computation without training-time randomness or data-dependent parameter fitting |
| **Interpretability** | Explicit quantification of instability as phase–memory divergence |
| **Wearable feasibility** | Linear-time processing suitable for streaming and on-device implementation |
| **Protocol transparency** | Controlled validation regimes with clearly defined baseline comparisons |

---

## 2  Signal Acquisition and Preprocessing

### 2.1  Chest-Mounted Smartphone Placement

A smartphone is positioned on the **anterior thoracic wall (sternal region)** using a strap or compression garment. Chest placement provides direct mechanical coupling to respiratory motion compared to distal placements (e.g., wrist), improving signal-to-noise in the respiration band.

```
         ╔══════════╗
         ║ STERNAL  ║  ← Smartphone placement
         ║  REGION  ║     (anterior thoracic wall)
         ╚══════════╝
        /            \
       /   Thorax     \
      /                \
     ────────────────────
```

### 2.2  Sampling and Channels

Assume inertial sampling at **fₛ ∈ [50, 100] Hz**.

Let:
- **a(t) ∈ ℝ³** — accelerometer signals
- **Ω(t) ∈ ℝ³** — gyroscope signals

### 2.3  Respiration-Sensitive Scalar Channel

We form a scalar respiration channel x(t) via projection onto a gravity-aligned axis:

$$x(t) = \mathbf{a}(t) \cdot \hat{u}_b(t)$$

> **x(t) = a(t) · û_b(t)**  &emsp; *(Eq. 1)*

where **û_b(t)** is a unit vector estimated from sensor fusion (gravity direction) or a stable principal axis.

### 2.4  Filtering and Normalization

| Step | Description |
|---|---|
| Drift removal | High-pass filter or detrend |
| Bandpass | 0.1–0.5 Hz at rest; extend for exercise |
| Motion rejection | Optional gating using ‖Ω(t)‖ or broadband energy |
| Normalization | z-score on a baseline window |

---

## 3  Phase–Memory Operator

### 3.1  Analytic Signal and Instantaneous Phase

Let **x(t)** denote the band-limited, filtered respiratory channel. We construct the analytic signal using the Hilbert transform 𝓗:

> **z(t) = x(t) + i 𝓗[x(t)] = A(t) e^{iθ(t)}**  &emsp; *(Eq. 2)*

where:
- **A(t) = |z(t)|** — instantaneous amplitude
- **θ(t) = arg(z(t))** — instantaneous phase

The associated instantaneous angular frequency is:

> **ω(t) = dθ(t)/dt**  &emsp; *(Eq. 3)*

following the classical analytic-signal formalism [1, 2].

### 3.2  Instantaneous Phase Velocity

Define phase velocity (implemented via discrete derivative with phase unwrapping):

> **ω(t) = dθ/dt**  &emsp; *(Eq. 4)*

### 3.3  Short-Term Phase Memory

For a memory window **Tₘ**, define:

> **ω̄(t) = (1/Tₘ) ∫_{t−Tₘ}^{t} ω(τ) dτ**  &emsp; *(Eq. 5)*

In discrete time with **M** samples:

> **ω̄(t) ≈ (1/M) Σ_{k=0}^{M−1} ω[n − k]**

---

## 4  Instability Metric and Decision Logic

### 4.1  Definition of the Instability Score

We define the **phase–memory divergence**:

> **ΔΦ(t) = |ω(t) − ω̄(t)|**  &emsp; *(Eq. 6)*

**Interpretation:**
- 🟢 Stable periodic breathing → small ΔΦ
- 🔴 Drift, pause-like suppression, or burst irregularity → large ΔΦ (rapid deviations of instantaneous phase velocity from short-term memory)

### 4.2  Baseline-Normalized Threshold

Let **σ_ω** denote the baseline standard deviation of ω(t) estimated on an initial stable segment. We define:

> **Instability at time t  ⟺  ΔΦ(t) > α · σ_ω**  &emsp; *(Eq. 7)*

with **α ∈ [2, 3]** as a transparent sensitivity parameter.

### 4.3  Optional Persistence Criterion

To reduce single-sample false positives, we optionally require persistence over **L** samples:

> **Σ_{k=0}^{L−1} 𝟙{ΔΦ(t−k) > α·σ_ω} ≥ L**  &emsp; *(Eq. 8)*

### 4.4  Implementation Parameters and Temporal Resolution

| Parameter | Value | Notes |
|---|---|---|
| Sampling rate fₛ | 50 Hz | BIDMC recordings |
| Memory window Tₘ | 5 s (M = 250 samples) | Spans multiple respiratory cycles |
| Threshold factor α | ∈ [2, 3] | Fixed across all recordings |
| Detection resolution Δt | 20 ms (= 1/fₛ) | Relative to perturbation onset t = 30 s |

> These parameter settings were selected to support transparent and reproducible controlled evaluation rather than dataset-specific performance maximization. A full sensitivity analysis over (Tₘ, α, L) remains an important topic for future work.

---

## 5  Experimental Protocol

> ⚠️ This study is designed as a **controlled proof-of-concept evaluation**, not a clinical validation study. Validation was performed on N = 5 publicly available BIDMC respiratory recordings with controlled semi-synthetic perturbations.

### 5.1  Controlled Regimes

| Regime | Description | Clinical Analogue |
|---|---|---|
| **Regular breathing (control)** | Stationary segment; no perturbation | Baseline / false-alarm analysis |
| **Frequency drift** | Gradual increase or decrease in effective respiratory frequency | Progressive rate deviation |
| **Intermittent pause** | Transient amplitude suppression or near-zero signal segments | Pause-like respiratory interruption |
| **Burst irregularity** | Transient fast-breathing bursts or erratic phase-reset-like deviations | Abrupt irregular respiratory destabilization |

> Perturbations are **semi-synthetic**: applied to real BIDMC-derived respiratory traces rather than generated from a fully synthetic signal model.

### 5.2  Baseline Methods

| Method | Sensitivity | Notes |
|---|---|---|
| **RMS envelope** | Amplitude suppression / gross envelope changes | Windowed RMS amplitude proxy |
| **FFT peak shift** | Rate or spectral drift | Welch-averaged periodogram [4] |
| **Peak-to-peak intervals** | Cycle-to-cycle timing variation | Simple time-domain period estimator |

### 5.3  Primary Outcomes

| Outcome | Description |
|---|---|
| **Detection latency** | Onset-to-alarm time relative to predefined perturbation onset |
| **False alarms** | Alarm occurrence within stable control regime |
| **Compute cost** | Runtime complexity and device-relevant computational footprint |

---

## 6  Results

### 6.1  Regime Visualization

The figure below shows a representative controlled example of ΔΦ(t) across three regimes. The metric remains low during the stable segment and increases under structured deviations, particularly for frequency drift and pause-like perturbations.

```
  ΔΦ(t)
  │
6 │                              ╭─────╮        ╭──────╮
  │                             ╱       ╲      ╱        ╲
4 │                            ╱         ╲    ╱          ╲
  │- - - - - - - - - - - - - -╱- - - - - -╲--╱- THRESHOLD-╲- - -
2 │                           ╱             ╲╱              ╲
  │▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔▔                               ▔▔▔
0 │◄──── STABLE ────►◄────── DRIFT ────────►◄──── PAUSE ────►
  └───────────────────────────────────────────────────────────→
  0    10    20    30    40    50    60    70    80    90   t (s)
```

*Representative proof-of-concept evolution of ΔΦ(t) across controlled regimes. The dashed line denotes the detection threshold used in the present controlled evaluation.*

### 6.2  Quantitative Comparison

**Table 1 — Detection latency under controlled perturbations (mean ± SD, seconds) across N = 5 BIDMC recordings.** Latency is defined as the first threshold crossing relative to perturbation onset (t = 30 s).

| Regime | RMS Envelope | FFT Peak Shift | **ΔΦ (proposed)** |
|---|---|---|---|
| Frequency drift | ✗ Not detected | 0.060 ± 0.000 s | **0.000 ± 0.000 s** |
| Pause (amplitude suppression) | **0.000 ± 0.000 s** | ✗ Not detected | 0.572 ± 0.027 s |
| Control (false alarms) | 0 | 0 | **0** |

> "Not detected" indicates no threshold crossing within the 10 s post-onset evaluation window.

**Key findings:**
- For **frequency drift**: ΔΦ detected at the first sampled post-onset instant (0.000 s); FFT required 0.060 s; RMS failed to trigger.
- For **amplitude suppression**: RMS triggered immediately (0.000 s); ΔΦ showed a mean latency of 0.572 s; FFT failed to trigger.
- **Zero false alarms** observed in the stable control segment across all methods.

> ⚠️ These results should be interpreted as controlled timing comparisons under semi-synthetic perturbations, **not** as estimates of clinical sensitivity, specificity, or diagnostic performance.

### 6.3  Motion Robustness Stress Test

Walking and posture-change segments were introduced as representative non-respiratory disturbance conditions, with a **gyroscope-based motion-gating** mechanism applied prior to instability evaluation.

| Metric | Value |
|---|---|
| Motion segments analyzed | 12 (across N = 5 recordings) |
| Mean segment duration | 8.3 ± 2.1 s |
| False-alarm rate (with gating) | **0.0%** |
| Max transient ΔΦ excursion | < 0.81 α·σ_ω |
| Isolated crossings (without gating) | 2/12 segments (did not satisfy persistence criterion) |

> ⚠️ These results represent an initial robustness check under **moderate walking and posture-related motion only**. They do not establish robustness under coughing, talking, arm-dominant activity, or stronger coupling changes.

---

## 7  Wearable Feasibility and Implementation

### 7.1  Pipeline Overview

```
┌─────────────┐   ┌──────────────────────┐   ┌────────────────────┐
│  Chest IMU  │──►│     Preprocess       │──►│  Analytic Signal   │
│(accel/gyro) │   │(detrend + bandpass)  │   │    (Hilbert)       │
└─────────────┘   └──────────────────────┘   └────────┬───────────┘
                                                       │
                                                       ▼
                                              ┌────────────────────┐
                                              │   Phase  θ̇(t)     │
                                              └────────┬───────────┘
                                                       │
                                                       ▼
                                              ┌────────────────────┐
                                              │  Memory  ω̄(t·Tₘ)  │
                                              └────────┬───────────┘
                                                       │
                                                       ▼
                                              ┌────────────────────┐
                                              │  ΔΦ(t) threshold   │
                                              │   → INSTABILITY    │
                                              └────────────────────┘
```

*Graphical overview of the proof-of-concept deterministic phase–memory operator pipeline for respiratory instability detection using chest-mounted smartphone IMU signals.*

**Instability metric:** ΔΦ(t) = |ω_inst − ω̄(t)|

### 7.2  Computational Footprint

The method is **streaming-capable and linear-time** in samples. A practical mobile implementation uses:

1. A **causal bandpass** filter
2. A **lightweight analytic-signal approximation** (Hilbert FIR / quadrature filter)
3. **Rolling window averages**

All parameters are explicit and auditable.

**Reproducibility layer.** A minimal REST-based experiment interface used for monitoring and standardized evaluation hooks is provided in the companion repository (see [Appendix A](#appendix-a-reproducibility-layer-minimal-rest-api)).

---

## 8  Reproducible Implementation and Validation Repository

A complete cross-platform reference implementation is provided at:

🔗 **[https://github.com/dfeen87/Smartphone-Based-Chest-Monitoring](https://github.com/dfeen87/Smartphone-Based-Chest-Monitoring)**

### Repository Contents

| Component | Description |
|---|---|
| `core/` | Deterministic C++ core implementation (iOS / Android deployment) |
| `python/` | Python reference pipeline for reproducible validation |
| `data/` | Integration scripts for PhysioNet BIDMC Respiratory Dataset |
| `protocols/` | Controlled semi-real perturbation protocols (stable, drift, pause, burst) |
| `baselines/` | Baseline comparison modules (RMS-envelope, FFT-peak-shift) |
| `metrics/` | Quantitative metrics evaluation (detection latency, false alarm rate) |
| `profiling/` | Device-level computational profiling (CPU, memory, battery) |
| `docs/` | Versioned documentation of all operator parameters |

All operator parameters (memory window **Tₘ**, sensitivity factor **α**, persistence length **L**, sampling rate, and filtering specifications) are explicitly documented and auditable.

### Live Demonstration Dashboard

An optional live demonstration dashboard is accessible at:

🌐 **[https://smartphone-based-chest-monitoring.onrender.com](https://smartphone-based-chest-monitoring.onrender.com)**

The dashboard exposes a minimal interactive interface for observing phase–memory dynamics and instability threshold behavior in real time.

> ⚠️ This deployment serves as an **experimental demonstration layer** and does not constitute a medical device.

---

## 9  Discussion

The proposed framework introduces an interpretable instability metric grounded in phase–memory divergence and avoids training-time uncertainty associated with data-driven models. The instability score **ΔΦ(t)** is directly parameterized through **(Tₘ, α, L)**, allowing explicit control over memory depth, sensitivity, and persistence criteria.

Within the restricted scope of the present study, the results support the feasibility of using a deterministic phase–memory observable to detect controlled respiratory deviations in chest-mounted IMU signals.

### Limitations

| Limitation | Detail |
|---|---|
| **Sample size** | Only N = 5 BIDMC recordings; controlled semi-synthetic perturbations rather than spontaneous clinical events |
| **Baseline comparators** | Restricted to lightweight signal-processing references (RMS, FFT); no clinical reference standard |
| **Physiological specificity** | Whether ΔΦ(t) can distinguish apnea, hypopnea, Cheyne–Stokes, or hyperventilation requires future study |
| **Parameter sensitivity** | (Tₘ, α, L) were fixed; no per-subject optimization or full sensitivity analysis |
| **Baseline definition** | Relies on initial stable segment; longer-term wearable deployment requires adaptive strategies |
| **Motion robustness** | Tested under moderate walking/posture only; coughing, talking, arm-dominant motion not evaluated |
| **Hardware scope** | Chest-mounted smartphone is a methodological configuration, not a validated clinical device |

> These limitations **constrain interpretation** but do not invalidate the framework. Whether this approach generalizes to clinically meaningful, heterogeneous, and motion-rich real-world settings remains an open empirical question.

---

## 10  Potential Future Directions

> ⚠️ The deterministic phase–memory operator is **not intended as a diagnostic medical device** and should not be interpreted as validated for clinical use.

### 10.1  Sleep-Related Pattern Screening

During sleep, changes in respiratory regularity may precede clinically relevant disturbances. ΔΦ(t) could be explored as a screening-level indicator of deviations from baseline respiratory pattern stability or for retrospective pattern analysis. The present work **does not establish suitability** for sleep diagnostics, unattended sleep monitoring, or replacement of polysomnography.

### 10.2  Stress and Hyperventilation Awareness

Acute stress and hyperventilation are associated with shifts in respiratory frequency and phase dynamics. Future work may examine whether the framework is useful for biofeedback-oriented monitoring in guided breathing exercises, stress-awareness tools, or mindfulness-related respiratory training. **Such applications remain hypothetical** until tested in dedicated datasets and intervention protocols.

### 10.3  Respiratory Rehabilitation and Training

Future studies may investigate whether tracking breathing stability can support adherence to controlled breathing protocols in respiratory physiotherapy or post-illness rehabilitation. The present study **does not validate rehabilitation efficacy or patient benefit.**

### 10.4  Chronic Respiratory Condition Monitoring

For individuals with asthma or COPD, the proposed framework could in principle be explored as a trend-level instability indicator relative to a personalized baseline. At present, **no claim is made** that the operator is suitable for clinical monitoring, treatment guidance, or replacement of established assessment methods such as spirometry, oxygen saturation monitoring, or physician-directed care.

---

## 11  Conclusion

We presented a **proof-of-concept deterministic phase–memory operator** and a transparent instability score **ΔΦ(t)** for respiratory instability detection in chest-mounted smartphone IMU signals.

Rather than relying on opaque classification models, the proposed framework uses explicit phase-based decision logic with auditable parameters and streaming-capable computation. Under the restricted conditions of the present controlled evaluation, the operator showed interpretable responses to representative semi-synthetic perturbation regimes and remained compatible with lightweight implementation.

> **Main contribution:** Define a transparent and reproducible operator-level framework for chest-based IMU respiratory instability sensing under controlled proof-of-concept conditions.

Future work must determine whether the proposed instability observable retains physiological specificity, robustness, and practical utility in larger cohorts, under broader motion conditions, and against reference-standard respiratory instrumentation.

---

## Ethics Statement

No new human or animal experiments were conducted for this study. The analysis used publicly available de-identified physiological recordings and controlled derived perturbation protocols.

## Data Availability Statement

The reference implementation, validation scripts, baseline comparison modules, and reproducibility infrastructure described in this manuscript are publicly available at:

🔗 **[https://github.com/dfeen87/Smartphone-Based-Chest-Monitoring](https://github.com/dfeen87/Smartphone-Based-Chest-Monitoring)**

## Funding

No external funding was received.

## Conflict of Interest

The authors declare no conflict of interest.

## Author Contributions

**M.K.** conceived the deterministic phase–memory operator framework and drafted the manuscript.  
**D.M.F.** contributed implementation considerations for mobile and cross-platform deployment and reviewed the manuscript for engineering clarity.  
All authors approved the final version.

## AI Statement

No generative AI models were used for data generation, signal analysis, or automated decision-making in the proposed method. Any later use of language-editing assistance, if applicable, will be disclosed at submission.

---

## Appendix A  Reproducibility Layer: Minimal REST API

The prototype may expose a minimal REST interface for monitoring experiments and standardizing evaluation. A short illustrative example is shown below.

```python
import requests

BASE = "http://localhost:5000/api"

# Register a node
response = requests.post(
    f"{BASE}/network/nodes",
    json={"frequency_hz": 440.0, "q_factor": 200.0, "beta": 1e-4}
)
node_id = response.json()["id"]

# Inject and evolve
requests.post(
    f"{BASE}/network/nodes/{node_id}/inject",
    json={"amplitude": 1.0, "phase": 0.0}
)
requests.post(
    f"{BASE}/network/tick",
    json={"dt": 1e-6, "steps": 1000}
)

# Read state
state = requests.get(f"{BASE}/network/state").json()
print(f"R(t) = {state['order_parameter']:.4f}")
```

---

## References

[1] B. Boashash, "Estimating and interpreting the instantaneous frequency of a signal. I. Fundamentals," *Proceedings of the IEEE*, vol. 80, no. 4, pp. 520–538, 1992. doi:10.1109/5.135376.

[2] L. Cohen, *Time-Frequency Analysis*. Prentice Hall, Upper Saddle River, NJ, USA, 1995. ISBN: 0-13-594532-1.

[3] J. M. Bland and D. G. Altman, "Statistical methods for assessing agreement between two methods of clinical measurement," *The Lancet*, vol. 327, no. 8476, pp. 307–310, 1986.

[4] P. Welch, "The use of fast Fourier transform for the estimation of power spectra: A method based on time averaging over short, modified periodograms," *IEEE Transactions on Audio and Electroacoustics*, vol. 15, no. 2, pp. 70–73, 1967. doi:10.1109/TAU.1967.1161901.

[5] Charlton PH, Birrenkott DA, Bonnici T, et al., "Breathing rate estimation from the electrocardiogram and photoplethysmogram: A review," *IEEE Reviews in Biomedical Engineering*, 2018;11:2–20. doi:10.1109/RBME.2017.2763681.

[6] Massaroni C, Nicolò A, Lo Presti D, et al., "Contact-based methods for measuring respiratory rate," *Sensors (Basel)*, 2019;19(4):908. doi:10.3390/s19040908.

[7] Vitazkova D, Foltan E, Kosnacova H, et al., "Advances in Respiratory Monitoring: A Comprehensive Review of Wearable and Remote Technologies," *Biosensors (Basel)*, 2024;14(2):90. doi:10.3390/bios14020090.

[8] Vignali E, Gasparotti E, Miglior L, et al., "A New Smartphone-Based Method for Remote Health Monitoring: Assessment of Respiratory Kinematics," *Electronics*, 2024;13(6):1132. doi:10.3390/electronics13061132.

[9] S. Valentine, A. C. Cunningham, B. Klasmer, et al., "Smartphone movement sensors for the remote monitoring of respiratory rates: Technical validation," *JMIR mHealth and uHealth*, vol. 8, 2020. doi:10.1177/20552076221089090.

[10] Choi SH, Yoon H, Baek HJ, Long X., "Biomedical Signal Processing and Health Monitoring Based on Sensors," *Sensors*, 2025;25(3):641. doi:10.3390/s25030641.

[11] Scano A, Re R, Perego P, Mastropietro A., "Wearable Sensors for Human Health Monitoring and Analysis," *Sensors*, 2026;26(2):575. doi:10.3390/s26020575.

[12] Zhao C, Liu D, Xu G, et al., "Recent advances in fiber optic sensors for respiratory monitoring," *Optical Fiber Technology*, 2022;72:103000. doi:10.1016/j.yofte.2022.103000.

[13] S. Stankoski, I. Kiprijanovska, I. Mavridou, et al., "Breathing Rate Estimation from Head-Worn Photoplethysmography Sensor Data Using Machine Learning," *Sensors*, vol. 22, no. 6, p. 2079, 2022. doi:10.3390/s22062079.

[14] T. Hussain, S. Ullah, R. Fernández-García, and I. Gil, "Wearable Sensors for Respiration Monitoring: A Review," *Sensors*, vol. 23, no. 17, p. 7518, 2023. doi:10.3390/s23177518.

[15] L. Yu, G. Liu, H. Zhang, and D. Wen, "Wearable respiratory sensors for non-invasive healthcare monitoring: applications and intelligent technologies," *Nanoscale*, vol. 18, pp. 3496–3512, 2026. doi:10.1039/D5NR04233J.

[16] X. Jin, L. Zha, F. Wang, Y. Wang, and X. Zhang, "Fully integrated wearable humidity sensor for respiration monitoring," *Frontiers in Bioengineering and Biotechnology*, vol. 10, 2022, Art. 1070855. doi:10.3389/fbioe.2022.1070855.

[17] J. Cherian, G. Mascia, D. Kairamkonda, et al., "Wearable sensing for clinical physiology monitoring: emerging paradigms," *Physiology (Bethesda)*, 2025. doi:10.1152/physiol.00039.2024.

[18] P. Daniore, V. Nittas, C. Haag, et al., "From wearable sensor data to digital biomarker development: ten lessons learned and a framework proposal," *npj Digital Medicine*, vol. 7, p. 161, 2024. doi:10.1038/s41746-024-01151-3.

[19] Y. Abdelal, M. Aupetit, A. Baggag, D. Al-Thani, "Exploring the Applications of Explainability in Wearable Data Analytics: Systematic Literature Review," *J. Med. Internet Res.*, vol. 26, e53863, 2024. doi:10.2196/53863.

[20] N. Gomes, M. Pato, A. R. Lourenço, and N. Datia, "A Survey on Wearable Sensors for Mental Health Monitoring," *Sensors*, vol. 23, no. 3, 1330, 2023. doi:10.3390/s23031330.

[21] J. Dunn, A. Coravos, M. Fanarjian, et al., "Remote Digital Technologies for Improving the Care of People with Respiratory Disorders," *Lancet Digit. Health*, vol. 6, no. 4, e291–e298, 2024. doi:10.1016/S2589-7500(23)00248-0.

[22] A. Angelucci and A. Aliverti, "An IMU-Based Wearable System for Respiratory Rate Estimation in Static and Dynamic Conditions," *Cardiovasc. Eng. Technol.*, vol. 14, no. 3, pp. 351–363, 2023. doi:10.1007/s13239-023-00657-3.

[23] D. Navakauskas and M. Dumpis, "Wearable Sensor-Based Human Activity Recognition: Performance and Interpretability of Dynamic Neural Networks," *Sensors*, vol. 25, no. 14, 4420, 2025. doi:10.3390/s25144420.

[24] R. Regan and W. S. Simi, "On the development of low power wearable devices for assessment of physiological vital parameters: a systematic review," *Journal of Public Health*, vol. 32, pp. 1093–1108, 2023. doi:10.1007/s10389-023-01893-6.

[25] M. Krüger, "Deterministic Detection of Information-Driven Regime Transitions in Wearable Physiological Signals: A Spiral-Time Operator Framework," *Zenodo*, 2026. doi:10.5281/zenodo.18799292.

---

*© Marcel Krüger & Don Michael Feeney Jr. — Submitted to Smart Wearable Technology (SWT)*
