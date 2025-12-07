/**
 * MainActivity.kt
 * RespiroSync Demo App for Android
 * 
 * Drop this into a new Android project and you're ready to go!
 */

package com.respirosync.demo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.respirosync.RespiroSyncEngine
import com.respirosync.SleepMetrics
import com.respirosync.SleepStage

class MainActivity : ComponentActivity() {
    private lateinit var respiro: RespiroSyncEngine
    private val handler = Handler(Looper.getMainLooper())
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        respiro = RespiroSyncEngine(this)
        
        setContent {
            RespiroSyncTheme {
                RespiroSyncApp(respiro, handler)
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        respiro.release()
        handler.removeCallbacksAndMessages(null)
    }
}

@Composable
fun RespiroSyncApp(respiro: RespiroSyncEngine, handler: Handler) {
    var isRunning by remember { mutableStateOf(false) }
    var metrics by remember { mutableStateOf<SleepMetrics?>(null) }
    
    // Background gradient
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF1a1a2e),
                        Color(0xFF16213e)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Spacer(modifier = Modifier.height(40.dp))
            
            Text(
                text = "RespiroSync",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Text(
                text = "Chest-Mounted Sleep Tracker",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Status indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(if (isRunning) Color.Green else Color.Gray)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = if (isRunning) "Session Active" else "Not Running",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Main content
            if (isRunning && metrics != null) {
                MetricsDisplay(metrics!!)
            } else {
                IdleDisplay()
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Control button
            Button(
                onClick = {
                    if (isRunning) {
                        respiro.stopSession()
                        isRunning = false
                        metrics = null
                        handler.removeCallbacksAndMessages(null)
                    } else {
                        respiro.startSession()
                        isRunning = true
                        
                        // Update metrics every second
                        val updateRunnable = object : Runnable {
                            override fun run() {
                                metrics = respiro.getCurrentMetrics()
                                handler.postDelayed(this, 1000)
                            }
                        }
                        handler.post(updateRunnable)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) Color.Red else Color(0xFF4cc9f0)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Close else Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRunning) "Stop Session" else "Start Session",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MetricsDisplay(metrics: SleepMetrics) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Breathing Rate
        MetricCard(
            icon = Icons.Default.PlayArrow,
            title = "Breathing Rate",
            value = String.format("%.1f", metrics.breathingRateBPM),
            unit = "BPM",
            color = Color(0xFF4cc9f0)
        )
        
        // Sleep Stage
        MetricCard(
            icon = Icons.Default.Star,
            title = "Sleep Stage",
            value = getSleepStageText(metrics.sleepStage),
            unit = "",
            color = Color(0xFF7209b7)
        )
        
        // Confidence and Breaths
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                icon = Icons.Default.Info,
                title = "Confidence",
                value = "${(metrics.confidence * 100).toInt()}%",
                unit = "",
                color = Color(0xFFf72585),
                modifier = Modifier.weight(1f),
                compact = true
            )
            
            MetricCard(
                icon = Icons.Default.Favorite,
                title = "Breaths",
                value = "${metrics.breathCyclesDetected}",
                unit = "",
                color = Color(0xFF3a0ca3),
                modifier = Modifier.weight(1f),
                compact = true
            )
        }
        
        // Apnea warning
        if (metrics.possibleApnea) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Yellow.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = Color.Yellow,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Possible Apnea Detected",
                        color = Color.Yellow,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    icon: ImageVector,
    title: String,
    value: String,
    unit: String,
    color: Color,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(if (compact) 12.dp else 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(if (compact) 8.dp else 12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(if (compact) 24.dp else 32.dp)
            )
            
            Text(
                text = title,
                fontSize = if (compact) 12.sp else 14.sp,
                color = Color.White.copy(alpha = 0.7f)
            )
            
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = value,
                    fontSize = if (compact) 24.sp else 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                if (unit.isNotEmpty()) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = unit,
                        fontSize = if (compact) 12.sp else 20.sp,
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun IdleDisplay() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Phone,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.White.copy(alpha = 0.3f)
        )
        
        Text(
            text = "Position phone on chest",
            fontSize = 20.sp,
            color = Color.White.copy(alpha = 0.8f)
        )
        
        Text(
            text = "Vertical orientation, screen facing out",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}

fun getSleepStageText(stage: SleepStage): String {
    return when (stage) {
        SleepStage.AWAKE -> "AWAKE"
        SleepStage.LIGHT_SLEEP -> "LIGHT"
        SleepStage.DEEP_SLEEP -> "DEEP"
        SleepStage.REM_SLEEP -> "REM"
        SleepStage.UNKNOWN -> "UNKNOWN"
    }
}

@Composable
fun RespiroSyncTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(),
        content = content
    )
}

/*
 * INSTRUCTIONS TO USE:
 * 
 * 1. Create new Android app in Android Studio (Jetpack Compose)
 * 2. Replace MainActivity.kt with this file
 * 3. Add RespiroSync library to your project
 * 4. Add permissions to AndroidManifest.xml:
 *    <uses-permission android:name="android.permission.BODY_SENSORS" />
 * 5. Run on your Android phone
 * 6. Put phone in chest vest and tap "Start Session"
 * 
 * That's it! You're tracking sleep.
 */
