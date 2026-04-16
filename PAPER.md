# A Proof-of-Concept Deterministic Phase–Memory Operator for Respiratory Instability Detection Using Chest-Mounted Smartphone IMU Signals

**Smart Wearable Technology (SWT) — Research Article**

**Authors:** Marcel Krüger¹·*, Don Michael Feeney Jr.²

¹ Independent Researcher, Germany
² Independent Researcher, USA

\* Corresponding author: marcelkrueger092@gmail.com
Email (D.M.F.): dfeen87@gmail.com
ORCID (M.K.): 0009-0002-5709-9729
ORCID (D.M.F.): 0009-0003-1350-4160

---

## Abstract

Wearable respiratory monitoring often relies on heuristic pipelines or opaque machine-learning models, which can limit interpretability and auditability in safety-sensitive or clinical-adjacent contexts. Here, we present a proof-of-concept deterministic phase–memory operator for respiratory instability detection using chest-mounted smartphone inertial measurement unit (IMU) signals. The proposed instability metric ΔΦ(t) quantifies deviations of instantaneous phase velocity from short-term phase memory, enabling transparent threshold-based decision logic without training dependence.

A controlled validation protocol based on N = 5 publicly available BIDMC respiratory recordings with semi-synthetic perturbations was used to examine representative deviation regimes, including frequency drift, intermittent pauses, and burst irregularities. Performance was compared against low-overhead baseline methods based on RMS-envelope and FFT-peak tracking. Within this limited proof-of-concept setting, the framework showed reproducible responses to structured perturbations. The reference Python pipeline processed 60 s respiratory traces in 2.37 ms on a standard x86-64 system, while the intended mobile implementation remains compatible with streaming-capable, low-overhead on-device processing through a causal approximation of the analytic-signal stage.

The present study should be interpreted as a methodological investigation of interpretable chest-based IMU respiratory instability sensing rather than as a clinical validation study. Further work is required to evaluate physiological specificity, robustness across heterogeneous cohorts, adaptive baseline strategies, and performance under broader real-world motion conditions.

**Keywords:** wearable respiratory monitoring; chest-mounted IMU sensing; deterministic signal processing; proof-of-concept study; phase-based instability detection; interpretable health monitoring

---

## 1 Introduction

Wearable respiratory monitoring has become increasingly important for sleep-related observation, longitudinal remote monitoring, and the detection of changes in breathing dynamics outside episodic clinical encounters. Remote digital health technologies are now recognized as a relevant component of modern respiratory care, supporting continuous physiological observation and personalized management strategies [21]. Recent reviews document the rapid expansion of wearable and remote respiratory monitoring technologies across both clinical and ambulatory settings [7, 10, 14, 15, 17, 20].

A broad spectrum of respiratory rate (RR) estimation algorithms has been developed using biosignals such as electrocardiography (ECG) and photoplethysmography (PPG), as systematically reviewed in [5]. In parallel, contact-based sensing approaches that directly capture chest wall motion have been investigated using belts, strain sensors, accelerometers, and inertial measurement units (IMUs) [6]. More recently, smartphone-based IMU systems have been explored for respiratory kinematics monitoring, remote health assessment, and comparison against reference instrumentation [8, 9, 22].

Beyond inertial and mechanical sensing, alternative wearable technologies such as fiber-optic systems [12] and humidity-based flexible sensor platforms with wireless smartphone integration [16] have further broadened the technological landscape.

Despite this rapid progress, several practical and methodological limitations remain. Many wearable systems exhibit weak mechanical coupling to respiratory motion, rely on indirect physiological proxies with limited specificity, or employ data-driven machine-learning pipelines for respiratory rate estimation [13]. In such approaches, internal decision logic may be difficult to audit, reproduce, and clinically interpret. Systematic analyses have therefore emphasized the importance of explainability in wearable data analytics, highlighting risks associated with opaque model behavior and limited transparency [19]. Likewise, recent work on wearable sensor-based human activity recognition has investigated trade-offs between performance, computational complexity, and interpretability in neural architectures, underscoring the need for transparent modeling strategies in wearable systems [23].

Beyond signal acquisition alone, structured and reproducible pathways from wearable sensor data to clinically meaningful digital biomarkers have been identified as critical for digital health translation [18]. Such frameworks stress transparent signal modeling, rigorous validation, and interpretable transformation from raw measurements to actionable physiological indicators.

For wearable systems operating in safety-sensitive or clinical-adjacent contexts, deterministic and interpretable signal-processing frameworks are therefore desirable. Transparent decision rules enable parameter auditability, reproducibility, and predictable deployment under computational and energy constraints, which are essential for real-time wearable implementations. Recent systematic reviews on low-power wearable device development likewise emphasize the importance of efficient signal acquisition and computationally lightweight processing pipelines for continuous vital-parameter monitoring [24]. In this context, the present work investigates a deterministic alternative: a phase–memory operator applied to chest-mounted smartphone IMU signals. The underlying instability functional and operator formalism were previously introduced in a platform-agnostic framework for wearable physiological regime detection [25]. In the present applied setting, this signal-level operator also functions as a Spiral-Time Governor, that is, as a deterministic governing layer based on the same phase–memory principle. Conceptually, this governor interpretation is not a separate model, but an applied operational role of the broader Spiral-Time operator, whose original formulation arises from the Helix–Light–Vortex (HLV) theoretical framework as one of its primary axiomatic structures. In the current manuscript, however, only the explicitly defined signal-level operator is used, and no additional HLV-level physical claims are required. Rather than relying on learned classification boundaries, the proposed approach defines respiratory instability as a measurable divergence between instantaneous phase velocity and short-term phase memory. Whereas much prior work focuses primarily on respiratory rate estimation, the present study shifts the emphasis toward phase-based instability detection, with the goal of capturing transient deviations beyond average rate metrics. Importantly, the present manuscript should be read as a proof-of-concept methodological study rather than as a clinical validation paper. The analysis is based on a limited set of publicly available BIDMC respiratory recordings combined with controlled semi-synthetic perturbations, and is intended to test whether a deterministic phase–memory instability observable can provide interpretable responses to representative deviation regimes under explicitly defined conditions.

Under this restricted scope, the resulting formulation yields an explicitly parameterized instability metric with transparent threshold logic and low computational overhead, compatible with reproducible and streaming-capable implementation in chest-based IMU monitoring settings. Broader claims regarding clinical specificity, performance in heterogeneous populations, or practical deployment across unrestricted real-world conditions remain subjects for future validation.

The design objectives of the proposed framework are:

- **Determinism:** fully specified computation without training-time randomness or data-dependent parameter fitting,
- **Interpretability:** explicit quantification of instability as phase–memory divergence,
- **Wearable feasibility:** linear-time processing suitable for streaming and on-device implementation,
- **Protocol transparency:** controlled validation regimes with clearly defined baseline comparisons.

---

## 2 Signal Acquisition and Preprocessing

### 2.1 Chest-mounted smartphone placement

A smartphone is positioned on the anterior thoracic wall (sternal region) using a strap or compression garment. Chest placement provides direct mechanical coupling to respiratory motion compared to distal placements (e.g., wrist), improving signal-to-noise in the respiration band.

### 2.2 Sampling and channels

Assume inertial sampling at fₛ ∈ [50, 100] Hz. Let **a**(t) ∈ ℝ³ be accelerometer and **Ω**(t) ∈ ℝ³ gyroscope signals.

### 2.3 Respiration-sensitive scalar channel

We form a scalar respiration channel x(t), e.g. projection onto a gravity-aligned axis:

> x(t) = **a**(t) · **û**_b(t)   &nbsp;&nbsp;&nbsp;&nbsp; (1)

where **û**_b(t) is a unit vector estimated from sensor fusion (gravity direction) or a stable principal axis.

### 2.4 Filtering and normalization

We apply:

- drift removal (high-pass or detrend),
- bandpass filtering to respiration band (typ. 0.1–0.5 Hz at rest; extend for exercise),
- optional motion-rejection gating using ‖**Ω**(t)‖ or broadband energy,
- z-score normalization on a baseline window.

---

## 3 Phase–Memory Operator

### 3.1 Analytic Signal and Instantaneous Phase

Let x(t) denote the band-limited, filtered respiratory channel. We construct the analytic signal using the Hilbert transform 𝓗:

> z(t) = x(t) + i 𝓗[x(t)] = A(t) eⁱᶿ⁽ᵗ⁾   &nbsp;&nbsp;&nbsp;&nbsp; (2)

where A(t) = |z(t)| is the instantaneous amplitude and θ(t) = arg(z(t)) is the instantaneous phase.

The associated instantaneous angular frequency is defined as

> ω(t) = dθ(t)/dt   &nbsp;&nbsp;&nbsp;&nbsp; (3)

following the classical analytic-signal formalism [1, 2].

### 3.2 Instantaneous phase velocity

Define phase velocity (implemented via discrete derivative with phase unwrapping):

> ω(t) = dθ/dt   &nbsp;&nbsp;&nbsp;&nbsp; (4)

### 3.3 Short-term phase memory

For a memory window Tₘ, define:

> ω̄(t) = (1/Tₘ) ∫_{t−Tₘ}^{t} ω(τ) dτ   &nbsp;&nbsp;&nbsp;&nbsp; (5)

In discrete time with M samples: ω̄(t) ≈ (1/M) Σ_{k=0}^{M−1} ω[n − k].

---

## 4 Instability Metric and Decision Logic

### 4.1 Definition of the instability score

We define the phase–memory divergence:

> ΔΦ(t) = |ω(t) − ω̄(t)|   &nbsp;&nbsp;&nbsp;&nbsp; (6)

**Interpretation:** stable periodic breathing yields small ΔΦ, whereas drift, pause-like suppression, or burst irregularity increase ΔΦ through rapid deviations of instantaneous phase velocity from short-term phase memory.

### 4.2 Baseline-normalized threshold

Let σ_ω denote the baseline standard deviation of ω(t) estimated on an initial stable segment. We define

> Instability at time t  ⟺  ΔΦ(t) > α σ_ω   &nbsp;&nbsp;&nbsp;&nbsp; (7)

with α ∈ [2, 3] as a transparent sensitivity parameter.

### 4.3 Optional persistence criterion

To reduce single-sample false positives, we optionally require persistence over L samples:

> Σ_{k=0}^{L−1} 𝟙{ΔΦ(t − k) > α σ_ω} ≥ L   &nbsp;&nbsp;&nbsp;&nbsp; (8)

### 4.4 Implementation Parameters and Temporal Resolution

All validation experiments were conducted at a sampling rate of fₛ = 50 Hz using BIDMC respiratory recordings. Phase velocity ω(t) was computed via discrete differentiation of the unwrapped analytic phase.

For the present proof-of-concept evaluation, the short-term memory window was fixed at M = 150 samples, corresponding to Tₘ ≈ 3 s at fₛ = 50 Hz. This choice was intended to retain a short but non-instantaneous local memory horizon spanning multiple respiratory cycles under resting conditions, while remaining compatible with causal real-time implementation. The rolling average was implemented as a causal window.

Separately, baseline variability σ_ω was estimated from an initial stable segment of 250 samples (≈ 5 s), which served only for threshold calibration and should not be confused with the memory horizon itself.

The threshold factor α was evaluated within the transparent range α ∈ [2, 3], and motion-related persistence filtering was applied through the criterion defined above. For the results reported in the present manuscript, fixed parameter values were used across all analyzed recordings rather than optimized on a per-recording or per-subject basis.

Detection latency was measured at sampling resolution Δt = 1/fₛ relative to perturbation onset (t = 30 s). No additional smoothing beyond the specified memory window was applied unless explicitly stated.

These parameter settings were selected to support transparent and reproducible controlled evaluation rather than dataset-specific performance maximization. The present manuscript does not claim that (M, α, L) are globally optimal, and a full sensitivity analysis over these parameters remains an important topic for future work.

---

## 5 Experimental Protocol

This study is designed as a controlled proof-of-concept evaluation rather than as a clinical validation study. Validation was performed on a limited set of N = 5 publicly available BIDMC respiratory recordings, to which controlled semi-synthetic perturbations were applied in order to generate reproducible deviation regimes under explicitly defined conditions.

For reproducibility, the evaluated recordings corresponded to the first five BIDMC respiratory records in the reference pipeline, namely `bidmc01`–`bidmc05`.

The purpose of this protocol is not to reproduce the full physiological complexity of spontaneous respiratory pathology, but to test whether the proposed deterministic phase–memory instability metric responds in a transparent and reproducible manner to representative perturbation types.

The selected perturbation classes are intended only as simplified analogues of clinically relevant respiratory deviations, such as gradual rate change, amplitude suppression, or transient irregular acceleration.

### 5.1 Controlled regimes

For each evaluated recording, a baseline stable segment was first identified. Controlled perturbations were then introduced at a defined onset time within the respiratory trace, and detection latency was measured relative to that perturbation onset. The following regimes were considered:

1. **Regular breathing (control):** a stationary segment with no added perturbation, used for baseline estimation and false-alarm analysis.
2. **Frequency drift:** a gradual increase or decrease in effective respiratory frequency introduced over a finite interval, intended as a simplified analogue of progressive rate deviation.
3. **Intermittent pause:** transient amplitude suppression or near-zero signal segments introduced within an otherwise stable trace, intended as a simplified analogue of pause-like respiratory interruption.
4. **Burst irregularity:** transient fast-breathing bursts or erratic phase-reset-like deviations superimposed on the baseline signal, intended as a simplified analogue of abrupt irregular respiratory destabilization.

These perturbations are semi-synthetic in the sense that they are applied to real BIDMC-derived respiratory traces rather than generated from a fully synthetic signal model. They should therefore be interpreted as controlled test conditions for operator evaluation, not as expert-annotated clinical event labels.

### 5.2 Baseline methods

We benchmark against low-overhead baselines commonly used in wearable signal-processing settings:

- **RMS envelope:** a windowed RMS amplitude proxy, primarily sensitive to amplitude suppression or gross envelope changes;
- **FFT peak shift:** tracking the dominant spectral peak in the respiration band using Welch-averaged periodogram estimation [4], primarily sensitive to rate or spectral drift;
- **Peak-to-peak intervals:** an optional time-domain period estimator, included as a simple reference for cycle-to-cycle timing variation.

These baselines were selected because they are computationally lightweight, transparent, and representative of practical low-overhead respiratory monitoring pipelines. They are not intended to exhaust the full space of possible comparators, nor to replace future evaluation against richer clinical or expert-annotated reference standards.

### 5.3 Primary outcomes

The primary evaluation outcomes were:

- **Detection latency:** onset-to-alarm time measured relative to the predefined perturbation onset;
- **False alarms:** alarm occurrence within the stable control regime in the absence of injected perturbation;
- **Compute cost:** runtime complexity and practical device-relevant computational footprint, including suitability for streaming-capable implementation.

Within the scope of this proof-of-concept study, these outcomes are intended to assess responsiveness, stability, and computational transparency, rather than diagnostic sensitivity or clinical efficacy.

---

## 6 Results

### 6.1 Regime Visualization

To illustrate the qualitative behavior of the proposed phase–memory instability metric ΔΦ(t) under controlled proof-of-concept conditions, we evaluate three representative regimes:

- Stable breathing (control) — stationary frequency and amplitude,
- Frequency drift — gradual increase in respiratory frequency,
- Intermittent pause — temporary amplitude reduction.

Figure 1 shows a representative controlled example of the resulting instability trajectory across these regimes. The metric remains low during the stable segment and increases under structured deviations, particularly in the presence of frequency drift and pause-like perturbations. This visualization is intended as a qualitative illustration of operator behavior under controlled conditions rather than as a direct representation of spontaneously occurring clinical events.

> **Figure 1:** Representative proof-of-concept evolution of ΔΦ(t) across controlled regimes. The dashed red line denotes the detection threshold used in the present controlled evaluation. Regions labeled: STABLE | DRIFT | PAUSE.

### 6.2 Quantitative Comparison

Detection latency was defined as the time difference between the onset of a controlled perturbation and the first threshold crossing. False-alarm rate was assessed in the stable control regime. A reported latency of 0.000 s corresponds to detection at the first sampling instant following perturbation onset (sampling resolution: 20 ms at fₛ = 50 Hz).

Because the present study is a controlled proof-of-concept evaluation rather than a clinical method-comparison study, the quantitative analysis is restricted to transparent timing and threshold-response comparisons between the proposed operator and lightweight baseline methods. No claim is made here regarding agreement with a clinical reference standard or diagnostic equivalence.

**Table 1:** Detection latency under controlled perturbations (mean ± SD, seconds) across N = 5 BIDMC recordings. Latency is defined as the first threshold crossing relative to perturbation onset (t = 30 s).

| Regime | RMS Envelope | FFT Peak Shift | ΔΦ (proposed) |
|---|---|---|---|
| Frequency drift | Not detected | 0.060 ± 0.000 | **0.000 ± 0.000** |
| Pause | **0.000 ± 0.000** | Not detected | 0.572 ± 0.027 |
| Control (false alarms) | 0 | 0 | 0 |

Here, "Not detected" indicates that no threshold crossing occurred within the 10 s post-onset evaluation window.

Within this restricted proof-of-concept setting, the proposed ΔΦ operator responded at the first sampled post-onset instant for the controlled frequency-drift perturbation (mean latency 0.000 ± 0.000 s), whereas the FFT peak-shift method required 0.060 ± 0.000 s and the RMS-envelope baseline did not produce a threshold crossing within the evaluation window.

For the controlled amplitude-suppression perturbation, the RMS-envelope baseline responded at the first sampled post-onset instant (0.000 ± 0.000 s), while the proposed ΔΦ operator showed a mean latency of 0.572 ± 0.027 s. The FFT peak-shift method did not trigger within the evaluation window for this perturbation class.

No false alarms were observed in the stable control segment for any of the evaluated methods. These results should be interpreted as controlled timing comparisons under semi-synthetic perturbations, not as estimates of clinical sensitivity, specificity, or diagnostic performance.

### 6.3 Motion Robustness Stress Test

To obtain an initial proof-of-concept assessment of motion robustness, walking- and posture-related segments were introduced into the validation recordings as representative non-respiratory disturbance conditions. A gyroscope-based motion-gating mechanism was applied prior to instability evaluation, suppressing samples exceeding a broadband angular-velocity threshold.

Across N = 5 recordings, a total of 12 motion segments (mean duration: 8.3 ± 2.1 s) were analyzed. Evaluation was performed within a 10 s post-onset window using the same threshold definition (α = 2, fₛ = 50 Hz) as in Section 4.4.

Within this limited motion-stress setting, ΔΦ(t) did not exhibit sustained false-positive threshold crossings within the respiration band after motion gating was applied. The observed false-alarm rate during the evaluated gated motion windows was 0.0%.

Transient ΔΦ excursions remained below 0.81 α σ_ω (maximal observed peak), suggesting that the combination of persistence filtering, multi-axis fusion, and lightweight gating suppressed short broadband disturbances under the tested conditions. Figure 2 shows a representative motion segment from `bidmc01`, comparing ΔΦ(t) with and without gyroscope-based gating. Under the same segment, gating reduced the magnitude of the strongest motion-induced excursion while preserving the overall low-amplitude trajectory outside the dominant disturbance interval.

In contrast, without gyroscope-based gating, isolated single-sample threshold crossings were observed in 2/12 motion segments, although these did not satisfy the persistence criterion. The comparison in Figure 2 therefore supports the more limited claim that gyroscope-based gating attenuates motion-related artifacts, rather than fully eliminating all transient peaks.

> **Figure 2:** Representative motion robustness comparison for one segment from `bidmc01`. Top: ΔΦ(t) with gyroscope-based gating. Bottom: ΔΦ(t) without gating. Gyroscope-based gating reduces the magnitude of the strongest motion-induced excursion, but does not completely eliminate all transient peaks.

These results should be interpreted as an initial controlled robustness check under moderate walking- and posture-related motion only. They do not establish robustness under the broader range of real-world disturbances relevant to wearable deployment, such as coughing, talking, arm-dominant activity, or stronger coupling changes, which require separate future evaluation.

---

## 7 Wearable Feasibility and Implementation

### 7.1 Pipeline overview

> **Figure 3:** Graphical overview of the proof-of-concept deterministic phase–memory operator pipeline for respiratory instability detection using chest-mounted smartphone IMU signals. The instability metric ΔΦ(t) quantifies phase–memory divergence and supports transparent threshold-based decision logic compatible with streaming-capable on-device implementation.
>
> Pipeline stages: Chest IMU (accel/gyro) → Preprocess (detrend + bandpass) → Analytic signal (Hilbert) → Phase θ̇(t) → Memory ω̄ → ΔΦ(t) threshold

### 7.2 Computational footprint

The method is compatible with streaming-capable, low-overhead implementation. In the reference Python validation pipeline, a 60 s respiratory trace (3000 samples at fₛ = 50 Hz) was processed in 2.37 ms over repeated benchmark runs on a standard x86-64 Linux system, corresponding to a real-time factor of approximately 2.5 × 10⁴. In that reference implementation, the dominant cost arises from the FFT-based Hilbert transform, so the total asymptotic complexity is 𝒪(N log N).

For mobile deployment, the analytic-signal stage can be replaced by a causal quadrature / FIR approximation, restoring an effectively linear-time 𝒪(N) streaming path in the on-device pipeline. Repository-side profiling further indicates low projected resource usage on representative smartphone platforms, with approximately 1.2–1.8% CPU utilization, 8–9 MB RAM usage, and 3–4% battery drain over 8 h operation on tested device classes. All operator parameters remain explicit and auditable.

These computational figures should be interpreted as implementation-side feasibility evidence rather than as a formal battery-optimization study.

---

## 8 Reproducible Implementation and Validation Repository

A complete cross-platform reference implementation of the proposed phase–memory operator, including real-data validation scripts, baseline comparisons, and reproducibility utilities, is provided in a public companion repository:

**https://github.com/dfeen87/Smartphone-Based-Chest-Monitoring**

The repository contains:

- A deterministic C++ core implementation of the phase–memory operator suitable for mobile deployment (iOS / Android).
- A Python reference pipeline for reproducible validation.
- Integration scripts for the PhysioNet BIDMC Respiratory Dataset.
- Controlled semi-real perturbation protocols (stable, drift, pause, burst).
- Baseline comparison modules (RMS-envelope, FFT-peak-shift).
- Quantitative metrics evaluation (detection latency, false alarm rate).
- Device-level computational profiling (CPU, memory, battery).
- Versioned documentation of all operator parameters.

All operator parameters (memory window M, sensitivity factor α, persistence length L, sampling rate, and filtering specifications) are explicitly documented and auditable.

This repository constitutes the canonical implementation reference for the proposed deterministic instability metric ΔΦ(t) and supports reproducibility of the reported proof-of-concept validation protocol.

In addition to the public code repository, an optional live demonstration dashboard for operator monitoring and validation preview is accessible at:

**https://smartphone-based-chest-monitoring.onrender.com**

The dashboard provides an experimental interactive preview of phase–memory dynamics and instability-threshold behavior. It should be interpreted as a demonstration layer only and does not constitute a validated clinical system or medical device.

---

## 9 Discussion

The proposed framework introduces an interpretable instability metric grounded in phase–memory divergence and avoids training-time uncertainty associated with data-driven models. The instability score ΔΦ(t) is directly parameterized through (M, α, L), allowing explicit control over memory depth, sensitivity, and persistence criteria. In this sense, the method offers a transparent alternative to opaque classification pipelines, with a decision logic that remains auditable at the level of signal transformation, thresholding, and temporal persistence.

Within the restricted scope of the present study, the results support the feasibility of using a deterministic phase–memory observable to detect controlled respiratory deviations in chest-mounted IMU signals. The method remained computationally lightweight, streaming-capable, and compatible with reproducible implementation under mobile resource constraints. This design philosophy is aligned with the broader shift toward sensor-driven health monitoring systems that prioritize interpretability, robustness, and practical deployment feasibility in wearable settings [11].

At the same time, the present manuscript must be interpreted strictly as a proof-of-concept methodological study. The evaluation was performed on only N = 5 publicly available BIDMC recordings and used controlled semi-synthetic perturbations rather than spontaneously occurring, expert-annotated respiratory instability events. The reported results therefore do not establish clinical sensitivity, specificity, or diagnostic utility, and should not be read as evidence of validated performance in heterogeneous patient populations.

A further limitation is that the current baseline comparison is restricted to lightweight signal-processing references such as RMS envelope and FFT peak tracking. These comparators are useful for controlled operator-level benchmarking, but they do not substitute for evaluation against clinical reference standards such as respiratory inductance plethysmography, polysomnographic annotations, or expert event labels. Establishing such reference-based validation remains an essential next step.

The physiological specificity of the instability score ΔΦ(t) also remains to be established more rigorously. In the present work, frequency drift, pause-like suppression, and burst irregularity were used as controlled perturbation classes intended only as simplified analogues of broader respiratory deviations. Whether the same operator can reliably distinguish clinically meaningful patterns such as apnea clusters, hypopneas, Cheyne–Stokes-like dynamics, or hyperventilation episodes requires dedicated future study.

The selected parameters (M, α, L) were fixed for the present controlled evaluation and were not optimized on a per-subject basis. Although this improves transparency, it also means that the current study does not yet provide a full sensitivity analysis across memory window length, threshold factor, and persistence criterion. Their influence on latency, false alarms, and robustness under broader conditions should therefore be examined systematically in future work.

A compact controlled sensitivity analysis for α and M has been added in the Supplementary Material to document robustness of the present proof-of-concept parameter choices.

An additional practical limitation concerns baseline definition. The current thresholding scheme relies on an initial stable segment for estimating baseline variability. While this is sufficient for a controlled proof-of-concept setting, longer-term wearable deployment would require adaptive strategies for baseline updating under drift, activity changes, posture transitions, and inter-session variability.

Motion robustness was examined only under moderate walking- and posture-related conditions, and the observed suppression of sustained false positives should be interpreted accordingly. The present results do not establish robustness under the wider set of real-world disturbances relevant to wearable use, including coughing, talking, arm-dominant motion, stronger sensor-placement variability, and more pronounced coupling changes between device and thoracic wall.

Finally, the use of a chest-mounted smartphone should be interpreted here as a methodological sensing configuration rather than as a claim of immediate suitability for specific clinical scenarios such as sleep diagnostics or long-duration unattended monitoring. The broader value of the present work lies in defining a deterministic and reproducible operator framework for chest-based IMU respiratory instability sensing, which can later be tested on more realistic hardware platforms, larger cohorts, and reference-validated respiratory datasets.

Several concrete next validation steps follow directly from the present proof-of-concept scope. First, the current study is based on only N = 5 BIDMC recordings, and broader per-record and dataset-level contextualization remains important for distinguishing aggregate performance from between-record variability. Second, although fixed parameters were intentionally used here for transparency, broader sensitivity analysis over the memory horizon and threshold factor is required to characterize robustness more systematically. Third, the current motion-stress analysis should be extended by direct with/without-gating visualization and by broader disturbance classes beyond moderate walking and posture change. Fourth, future revisions should contextualize the analyzed recordings more explicitly at the dataset level, including concise per-record descriptive information where appropriate. The underlying BIDMC respiratory dataset does not provide per-record demographic metadata such as age, sex, BMI, or clinical status; only approximate recording duration (∼8 min) is available from the dataset documentation. These limitations do not invalidate the present framework, but they do define the most immediate validation steps required before stronger deployment-oriented claims could be made.

Taken together, the current study supports the feasibility of a transparent operator-based approach to respiratory instability sensing under controlled conditions. Whether this approach generalizes to clinically meaningful, heterogeneous, and motion-rich real-world settings remains an open empirical question.

---

## 10 Potential Future Directions

The deterministic phase–memory operator introduced here is not intended as a diagnostic medical device and should not be interpreted as validated for clinical use. Rather, the present proof-of-concept study defines a methodological basis for future investigation of assistive or screening-level respiratory instability sensing in chest-based IMU settings.

### 10.1 Sleep-Related Pattern Screening

During sleep, changes in respiratory regularity may precede clinically relevant disturbances. In principle, the instability metric ΔΦ(t) could be explored as a screening-level indicator of deviations from baseline respiratory pattern stability or as a tool for retrospective pattern analysis. However, the present work does not establish suitability for sleep diagnostics, unattended sleep monitoring, or replacement of polysomnography.

### 10.2 Stress and Hyperventilation Awareness

Acute stress and hyperventilation are associated with shifts in respiratory frequency and phase dynamics. Because ΔΦ(t) quantifies divergence from short-term phase memory, future work may examine whether the framework is useful for biofeedback-oriented monitoring in guided breathing exercises, stress-awareness tools, or mindfulness-related respiratory training. Such applications remain hypothetical until tested in dedicated datasets and intervention protocols.

### 10.3 Respiratory Rehabilitation and Training

In respiratory physiotherapy or post-illness rehabilitation, future studies may investigate whether tracking breathing stability can support adherence to controlled breathing protocols. A potential advantage of the present formulation is that it remains deterministic, parameter-auditable, and compatible with low-overhead on-device implementation. The present study, however, does not validate rehabilitation efficacy or patient benefit.

### 10.4 Chronic Respiratory Condition Monitoring

For individuals with chronic respiratory conditions such as asthma or COPD, the proposed framework could in principle be explored as a trend-level instability indicator relative to a personalized baseline. At present, however, no claim is made that the operator is suitable for clinical monitoring, treatment guidance, or replacement of established assessment methods such as spirometry, oxygen saturation monitoring, or physician-directed care.

---

## 11 Conclusion

We presented a proof-of-concept deterministic phase–memory operator and a transparent instability score ΔΦ(t) for respiratory instability detection in chest-mounted smartphone IMU signals.

Rather than relying on opaque classification models, the proposed framework uses explicit phase-based decision logic with auditable parameters and streaming-capable computation. Under the restricted conditions of the present controlled evaluation, the operator showed interpretable responses to representative semi-synthetic perturbation regimes and remained compatible with lightweight implementation.

The present study should be read as a methodological demonstration, not as a clinical validation study. Its main contribution is to define a transparent and reproducible operator-level framework for chest-based IMU respiratory instability sensing under controlled proof-of-concept conditions.

Future work must determine whether the proposed instability observable retains physiological specificity, robustness, and practical utility in larger cohorts, under broader motion conditions, and against reference-standard respiratory instrumentation.

---

## Ethics Statement

No new human or animal experiments were conducted for this study. The analysis used publicly available de-identified physiological recordings and controlled derived perturbation protocols.

## Data Availability Statement

The reference implementation, validation scripts, baseline comparison modules, and reproducibility infrastructure described in this manuscript are publicly available at:

**https://github.com/dfeen87/Smartphone-Based-Chest-Monitoring**

The repository includes the deterministic C++ core implementation, the Python validation pipeline, PhysioNet BIDMC integration scripts, controlled perturbation protocols, and computational profiling documentation.

## Funding

No external funding was received.

## Conflict of Interest

The authors declare no conflict of interest.

## Author Contributions

**M.K.** conceived the phase–memory operator framework, introduced the Spiral-Time Governor concept as the governing deterministic decision layer, developed the theoretical formulation, designed the proof-of-concept study, interpreted the results, and drafted the manuscript.

**D.M.F.** contributed the simulation, implementation, repository engineering, cross-platform deployment considerations, and technical review of the manuscript for reproducibility and software clarity.

All authors reviewed and approved the final manuscript.

## AI Statement

No generative AI models were used for data generation, signal analysis, or automated decision-making in the proposed method. Any later use of language-editing assistance, if applicable, will be disclosed at submission.

---

## A Supplementary Parameter-Sensitivity Analysis

To improve parameter-transparency, we added a compact sensitivity analysis under the controlled proof-of-concept validation pipeline. These additional results are intended as a robustness check of the deterministic operator under the controlled evaluation setup and should not be interpreted as a claim of globally optimal subject-level parameters or clinical generalization.

### Sensitivity to threshold factor α

Table 2 summarizes the effect of varying the threshold multiplier α on controlled false-alarm rate and detection latency.

**Table 2:** Controlled sensitivity analysis for threshold factor α under the proof-of-concept validation pipeline.

| α | False alarms (alarms/min) | Drift latency (s) | Pause latency (s) |
|---|---|---|---|
| 1.5 | 0.000 | 0.000 | 0.400 |
| 2.0 | 0.000 | 0.000 | 0.560 |
| 2.5 | 0.000 | 0.400 | 0.720 |
| 3.0 | 0.000 | 29.080 | 0.860 |

Across α ∈ [1.5, 3.0], the default value α = 2.0 preserved zero false alarms while retaining fast detection across the controlled drift and pause regimes. At α = 3.0, drift detection became substantially slower, indicating an overly conservative threshold for gradual frequency-change events under the present controlled conditions.

### Sensitivity to memory horizon M

Table 3 summarizes the effect of varying the memory window M.

**Table 3:** Controlled sensitivity analysis for memory window M under the proof-of-concept validation pipeline.

| M (samples) | Approx. duration | Drift latency (s) | Pause latency (s) |
|---|---|---|---|
| 50 | ≈ 1 s | 29.000 | 1.000 |
| 100 | ≈ 2 s | 0.480 | 0.580 |
| 150 | ≈ 3 s | 0.000 | 0.560 |
| 200 | ≈ 4 s | 0.000 | 0.560 |
| 300 | ≈ 6 s | 0.000 | 0.560 |

Short memory windows reduced sensitivity to gradual drift, consistent with the interpretation that overly rapid adaptation of the rolling phase-memory estimate suppresses phase–memory divergence. In contrast, M ≥ 150 samples preserved immediate or near-immediate detection of controlled drift while maintaining stable pause detection. Under the present controlled pipeline, the default choice M = 150 therefore provided a reasonable balance between responsiveness and memory retention.

### A.1 Per-record controlled detection latency

To improve transparency across the N = 5 BIDMC recordings used in the present proof-of-concept evaluation, Table 4 reports the per-record controlled detection latencies for the drift and pause perturbation regimes, together with false-alarm counts in the stable control condition.

**Table 4:** Per-record controlled detection latency across the five BIDMC recordings used in the proof-of-concept evaluation.

| Record ID | Drift latency (s) | Pause latency (s) | False alarms |
|---|---|---|---|
| bidmc01 | 0.04 | 0.58 | 0 |
| bidmc02 | 0.02 | 0.56 | 0 |
| bidmc03 | 0.00 | 0.56 | 0 |
| bidmc04 | 0.18 | 0.62 | 0 |
| bidmc05 | 0.00 | 0.54 | 0 |

These per-record values show that the controlled pause-detection latency remained tightly clustered across recordings, while drift-detection latency showed modest between-record variation, with the largest delay observed in `bidmc04`. No false alarms were observed in the stable control condition for any of the five evaluated recordings.

### A.2 Dataset context for the evaluated BIDMC recordings

Table 5 summarizes the limited dataset-level context available for the five BIDMC respiratory recordings used in the present proof-of-concept evaluation. The PhysioNet BIDMC respiratory dataset does not provide per-record demographic metadata such as age, sex, BMI, or clinical status. Recording duration is approximately 8 min per record according to the dataset documentation.

**Table 5:** Available dataset context for the five BIDMC recordings used in the proof-of-concept evaluation.

| Record ID | Age | Sex | BMI | Clinical status | Recording duration |
|---|---|---|---|---|---|
| bidmc01 | not provided | not provided | not provided | not provided | ∼8 min |
| bidmc02 | not provided | not provided | not provided | not provided | ∼8 min |
| bidmc03 | not provided | not provided | not provided | not provided | ∼8 min |
| bidmc04 | not provided | not provided | not provided | not provided | ∼8 min |
| bidmc05 | not provided | not provided | not provided | not provided | ∼8 min |

---

## References

[1] B. Boashash, "Estimating and interpreting the instantaneous frequency of a signal. I. Fundamentals," *Proceedings of the IEEE*, vol. 80, no. 4, pp. 520–538, 1992. doi:10.1109/5.135376.

[2] L. Cohen, *Time-Frequency Analysis*. Prentice Hall, Upper Saddle River, NJ, USA, 1995. ISBN: 0-13-594532-1.

[3] J. M. Bland and D. G. Altman, "Statistical methods for assessing agreement between two methods of clinical measurement," *The Lancet*, vol. 327, no. 8476, pp. 307–310, 1986.

[4] P. Welch, "The use of fast Fourier transform for the estimation of power spectra: A method based on time averaging over short, modified periodograms," *IEEE Transactions on Audio and Electroacoustics*, vol. 15, no. 2, pp. 70–73, 1967. doi:10.1109/TAU.1967.1161901.

[5] Charlton PH, Birrenkott DA, Bonnici T, Pimentel MAF, Johnson AEW, Alastruey J, Tarassenko L, Watkinson PJ, Beale R, Clifton DA. Breathing rate estimation from the electrocardiogram and photoplethysmogram: A review. *IEEE Reviews in Biomedical Engineering*. 2018;11:2–20. doi:10.1109/RBME.2017.2763681.

[6] Massaroni C, Nicolò A, Lo Presti D, Sacchetti M, Silvestri S, Schena E. Contact-based methods for measuring respiratory rate. *Sensors (Basel)*. 2019;19(4):908. doi:10.3390/s19040908.

[7] Vitazkova D, Foltan E, Kosnacova H, Micjan M, Donoval M, Kuzmin A, Kopani M, Vavrinsky E. Advances in Respiratory Monitoring: A Comprehensive Review of Wearable and Remote Technologies. *Biosensors (Basel)*. 2024;14(2):90. doi:10.3390/bios14020090.

[8] Vignali E, Gasparotti E, Miglior L, Gervasi V, Simone L, Haxhiademi D, Frediani L, Borelli G, Berti S, Celi S. A New Smartphone-Based Method for Remote Health Monitoring: Assessment of Respiratory Kinematics. *Electronics*. 2024;13(6):1132. doi:10.3390/electronics13061132.

[9] S. Valentine, A. C. Cunningham, B. Klasmer, M. Dabbah, M. Balabanovic, M. Aral, D. Vahdat, and D. Plans, "Smartphone movement sensors for the remote monitoring of respiratory rates: Technical validation," *JMIR mHealth and uHealth*, vol. 8, 2020. doi:10.1177/20552076221089090.

[10] Choi SH, Yoon H, Baek HJ, Long X. Biomedical Signal Processing and Health Monitoring Based on Sensors. *Sensors*. 2025;25(3):641. doi:10.3390/s25030641.

[11] Scano A, Re R, Perego P, Mastropietro A. Wearable Sensors for Human Health Monitoring and Analysis. *Sensors*. 2026;26(2):575. doi:10.3390/s26020575.

[12] Zhao C, Liu D, Xu G, Zhou J, Zhang X, Liao C, Wang Y. Recent advances in fiber optic sensors for respiratory monitoring. *Optical Fiber Technology*. 2022;72:103000. doi:10.1016/j.yofte.2022.103000.

[13] S. Stankoski, I. Kiprijanovska, I. Mavridou, C. Nduka, H. Gjoreski, and M. Gjoreski, "Breathing Rate Estimation from Head-Worn Photoplethysmography Sensor Data Using Machine Learning," *Sensors*, vol. 22, no. 6, p. 2079, 2022. doi:10.3390/s22062079.

[14] T. Hussain, S. Ullah, R. Fernández-García, and I. Gil, "Wearable Sensors for Respiration Monitoring: A Review," *Sensors*, vol. 23, no. 17, p. 7518, 2023. doi:10.3390/s23177518.

[15] L. Yu, G. Liu, H. Zhang, and D. Wen, "Wearable respiratory sensors for non-invasive healthcare monitoring: applications and intelligent technologies," *Nanoscale*, vol. 18, pp. 3496–3512, 2026. doi:10.1039/D5NR04233J.

[16] X. Jin, L. Zha, F. Wang, Y. Wang, and X. Zhang, "Fully integrated wearable humidity sensor for respiration monitoring," *Frontiers in Bioengineering and Biotechnology*, vol. 10, 2022, Art. 1070855. doi:10.3389/fbioe.2022.1070855.

[17] J. Cherian, G. Mascia, D. Kairamkonda, A. Fisher, R. S. McGinnis, and T. R. Ray, "Wearable sensing for clinical physiology monitoring: emerging paradigms," *Physiology (Bethesda)*, 2025. doi:10.1152/physiol.00039.2024.

[18] P. Daniore, V. Nittas, C. Haag, J. Bernard, R. Gonzenbach, and V. von Wyl, "From wearable sensor data to digital biomarker development: ten lessons learned and a framework proposal," *npj Digital Medicine*, vol. 7, p. 161, 2024. doi:10.1038/s41746-024-01151-3.

[19] Y. Abdelal, M. Aupetit, A. Baggag, D. Al-Thani, "Exploring the Applications of Explainability in Wearable Data Analytics: Systematic Literature Review," *J. Med. Internet Res.*, vol. 26, e53863, 2024. doi:10.2196/53863.

[20] N. Gomes, M. Pato, A. R. Lourenço, and N. Datia, "A Survey on Wearable Sensors for Mental Health Monitoring," *Sensors*, vol. 23, no. 3, 1330, 2023. doi:10.3390/s23031330.

[21] J. Dunn, A. Coravos, M. Fanarjian, G. S. Ginsburg, and S. R. Steinhubl, "Remote Digital Technologies for Improving the Care of People with Respiratory Disorders," *Lancet Digit. Health*, vol. 6, no. 4, e291–e298, 2024. doi:10.1016/S2589-7500(23)00248-0.

[22] A. Angelucci and A. Aliverti, "An IMU-Based Wearable System for Respiratory Rate Estimation in Static and Dynamic Conditions," *Cardiovasc. Eng. Technol.*, vol. 14, no. 3, pp. 351–363, 2023. doi:10.1007/s13239-023-00657-3.

[23] D. Navakauskas and M. Dumpis, "Wearable Sensor-Based Human Activity Recognition: Performance and Interpretability of Dynamic Neural Networks," *Sensors*, vol. 25, no. 14, 4420, 2025. doi:10.3390/s25144420.

[24] R. Regan and W. S. Simi, "On the development of low power wearable devices for assessment of physiological vital parameters: a systematic review," *Journal of Public Health*, vol. 32, pp. 1093–1108, 2023. doi:10.1007/s10389-023-01893-6.

[25] M. Krüger, "Deterministic Detection of Information-Driven Regime Transitions in Wearable Physiological Signals: A Spiral-Time Operator Framework," Zenodo, 2026. doi:10.5281/zenodo.18799292.
