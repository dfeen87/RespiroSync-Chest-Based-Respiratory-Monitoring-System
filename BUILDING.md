🔨 Building RespiroSync
Complete instructions for compiling RespiroSync for iOS and Android.

Prerequisites
For iOS Development:

macOS 12.0 or later
Xcode 14.0 or later
iPhone 6 or newer (for testing)
Command Line Tools: xcode-select --install

For Android Development:

Android Studio Arctic Fox or later
Android SDK 21 (Lollipop) or higher
NDK r21 or later
CMake 3.18 or later
Android phone with gyroscope (2015+)


iOS Build
Option 1: Using the Example Project (Fastest)

Clone the repository:

bash   git clone https://github.com/[your-username]/respirosync.git
   cd respirosync

Open example project:

bash   open examples/ios/RespiroSyncDemo.xcodeproj

Configure signing:

Select project in Xcode
Go to "Signing & Capabilities"
Select your Team
Change Bundle Identifier (e.g., com.yourname.respirosync)


Connect iPhone and Run:

Select your iPhone from device menu
Press ▶️ (Cmd+R)
Wait for build to complete
App will launch on your phone



Option 2: Building the Library from Source

Build core library:

bash   cd src/core
   clang++ -c respirosync_core.cpp -o respirosync_core.o \
       -std=c++17 -O3 -arch arm64

Build iOS wrapper:

bash   cd ../ios
   clang++ -c respirosync_ios.mm -o respirosync_ios.o \
       -framework CoreMotion -arch arm64

Create static library:

bash   ar rcs librespirosync.a respirosync_core.o respirosync_ios.o

Use in your Xcode project:

Drag librespirosync.a into your project
Add to "Link Binary With Libraries"
Add CoreMotion framework
Import: #import "RespiroSync.h"




Android Build
Option 1: Using the Example Project (Fastest)

Clone the repository:

bash   git clone https://github.com/[your-username]/respirosync.git
   cd respirosync

Open in Android Studio:

File → Open
Select respirosync/examples/android
Wait for Gradle sync


Connect Android phone:

Enable Developer Options
Enable USB Debugging
Connect via USB


Run the app:

Click Run (▶️) or Shift+F10
Select your device
App will install and launch



Option 2: Building the Library with CMake

Create CMakeLists.txt:

cmake   cmake_minimum_required(VERSION 3.18.1)
   project(respirosync)
   
   add_library(respirosync SHARED
       src/core/respirosync_core.cpp
       src/android/respirosync_android.cpp
   )
   
   target_include_directories(respirosync PRIVATE
       ${CMAKE_CURRENT_SOURCE_DIR}/src/core
   )
   
   target_compile_options(respirosync PRIVATE
       -std=c++17
       -O3
       -fvisibility=hidden
   )
   
   find_library(log-lib log)
   target_link_libraries(respirosync ${log-lib})

Add to your app's build.gradle:

gradle   android {
       ...
       externalNativeBuild {
           cmake {
               path "CMakeLists.txt"
           }
       }
   }

Build:

bash   ./gradlew assembleDebug

Use in your app:

kotlin   import com.respirosync.RespiroSyncEngine
   
   val respiro = RespiroSyncEngine(context)

Troubleshooting
iOS Issues
Problem: "Could not find developer disk image"

Solution: Update Xcode to support your iOS version

Problem: Code signing error

Solution:

Create free Apple Developer account
Select your team in Xcode signing settings
Change bundle identifier to something unique



Problem: "CoreMotion not available in simulator"

Solution: You MUST test on a real device, not simulator

Problem: Build fails with "Undefined symbols"

Solution: Ensure you've added CoreMotion framework to "Link Binary With Libraries"

Android Issues
Problem: "NDK not found"

Solution:

Tools → SDK Manager
SDK Tools tab
Check "NDK (Side by side)"
Click Apply



Problem: CMake version too old

Solution: Update CMake in SDK Manager (need 3.18+)

Problem: "Unsatisfied link error" at runtime

Solution: Check that System.loadLibrary("respirosync") matches your library name in CMakeLists.txt

Problem: Sensors not working

Solution: Add to AndroidManifest.xml:

xml  <uses-permission android:name="android.permission.BODY_SENSORS" />
  <uses-feature android:name="android.hardware.sensor.gyroscope" android:required="true" />
  <uses-feature android:name="android.hardware.sensor.accelerometer" android:required="true" />

Build Configurations
Debug Build (Default)

Includes debug symbols
Larger binary size
Easier to debug
Slightly slower

Release Build (Optimized)
For iOS:
bashclang++ -c respirosync_core.cpp -o respirosync_core.o \
    -std=c++17 -O3 -DNDEBUG -arch arm64
For Android (in build.gradle):
gradlebuildTypes {
    release {
        minifyEnabled true
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt')
    }
}

Performance Optimization
Compiler Flags for Maximum Speed:
iOS/macOS:
bash-O3 -flto -march=native -ffast-math
Android:
cmaketarget_compile_options(respirosync PRIVATE
    -O3
    -flto
    -ffast-math
    -fomit-frame-pointer
)
Battery Optimization:
Both platforms: The sensor update rate is set to 50Hz (20ms interval) by default. You can reduce to 25Hz for better battery life with minimal accuracy loss:
iOS:
objcmotionManager.gyroUpdateInterval = 0.04; // 25Hz
Android:
kotlinval samplingPeriodUs = 40_000 // 40ms = 25Hz

Verification
Test That It Works:

Build successful - No errors in console
App launches - Opens without crashing
Sensors detected - No "sensor unavailable" warnings
Breathing detected - Shows BPM value within 30 seconds
Values make sense - 10-20 BPM at rest is normal

Quick Test:
1. Start session while sitting
2. Breathe normally → Should show 12-18 BPM
3. Hold breath → BPM drops to ~0
4. Breathe fast → BPM increases to 25+
5. If all three work → Build is successful!

Distribution
For Beta Testing:
iOS (TestFlight):

Archive in Xcode
Upload to App Store Connect
Add to TestFlight
Share link with testers

Android (Internal Testing):

Build → Generate Signed Bundle/APK
Upload to Google Play Console
Create Internal Testing track
Share link with testers

For Open Source Distribution:

Provide pre-compiled binaries in GitHub Releases
Include both debug and release versions
Tag releases semantically (v1.0.0, v1.1.0, etc.)


Need Help?

💬 GitHub Discussions: Ask questions
🐛 GitHub Issues: Report build problems
📧 Email: dfeen87@gmail.com


Once built, see QUICKSTART.md for usage instructions.
