#ifndef RESPIROSYNC_CORE_H
#define RESPIROSYNC_CORE_H

#ifdef __cplusplus
extern "C" {
#endif

#include <stdint.h>

/* ============================================================================
 * RespiroSync™ Core Engine
 * Stable C API for cross-platform respiratory monitoring
 *
 * Version: 1.0.0
 * Status:  Stable public interface
 *
 * This header defines the binding contract for the RespiroSync core engine.
 * The implementation is provided by a C++ backend and exposed via an opaque
 * handle for ABI stability across platforms and languages.
 * ============================================================================
 */

/* -----------------------------
 * Versioning
 * ----------------------------- */
#define RESPIROSYNC_VERSION_MAJOR 1
#define RESPIROSYNC_VERSION_MINOR 0
#define RESPIROSYNC_VERSION_PATCH 0

#define RESPIROSYNC_VERSION_STRING "1.0.0"

/* -----------------------------
 * Opaque Engine Handle
 * ----------------------------- */
/* The concrete type is RespiroSync::RespiroEngine (C++),
 * intentionally hidden from consumers.
 */
typedef void* RespiroHandle;

/* -----------------------------
 * Sleep Stage Classification
 * ----------------------------- */
typedef enum {
    AWAKE = 0,
    LIGHT_SLEEP = 1,
    DEEP_SLEEP = 2,
    REM_SLEEP = 3,
    UNKNOWN = 4
} SleepStage;

/* -----------------------------
 * Sleep / Respiratory Metrics
 * ----------------------------- */
typedef struct {
    SleepStage current_stage;

    float confidence;               /* 0.0 – 1.0 heuristic confidence */
    float breathing_rate_bpm;       /* breaths per minute */
    float breathing_regularity;     /* 0.0 – 1.0 (higher = more consistent) */
    float movement_intensity;       /* 0.0 – 1.0 (higher = more movement) */

    int   breath_cycles_detected;
    int   possible_apnea;           /* boolean (0 = false, 1 = true) */
} SleepMetrics;

/* -----------------------------
 * Lifecycle Management
 * ----------------------------- */

/* Create a new RespiroSync engine instance */
RespiroHandle respiro_create(void);

/* Destroy an engine instance and release all resources */
void respiro_destroy(RespiroHandle handle);

/* Reset internal state and begin a new monitoring session */
void respiro_start_session(
    RespiroHandle handle,
    uint64_t timestamp_ms
);

/* -----------------------------
 * Sensor Data Ingestion
 * ----------------------------- */

/* Feed a gyroscope sample (rad/s or device-native units) */
void respiro_feed_gyro(
    RespiroHandle handle,
    float x, float y, float z,
    uint64_t timestamp_ms
);

/* Feed an accelerometer sample (m/s^2 or device-native units) */
void respiro_feed_accel(
    RespiroHandle handle,
    float x, float y, float z,
    uint64_t timestamp_ms
);

/* -----------------------------
 * Metrics Retrieval
 * ----------------------------- */

/* Retrieve the latest computed sleep and respiratory metrics */
void respiro_get_metrics(
    RespiroHandle handle,
    uint64_t timestamp_ms,
    SleepMetrics* out_metrics
);

#ifdef __cplusplus
} /* extern "C" */
#endif

#endif /* RESPIROSYNC_CORE_H */
