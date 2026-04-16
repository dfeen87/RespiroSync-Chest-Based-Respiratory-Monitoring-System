🚀 Quick Start Guide - Get RespiroSync Running in 5 Minutes
Not a developer? No problem. Here's how to try RespiroSync on your phone right now.

What You Need
✅ An iPhone (6 or newer) OR Android phone (5.0+)
✅ A way to hold your phone on your chest:

Compression shirt with phone pocket
Running armband (worn on chest)
Elastic band or belt
DIY vest (literally tape it to a tank top)

⚠️ IMPORTANT: Phone must be positioned vertically on your chest (screen facing out, top edge pointing up)

Option 1: Download Pre-Built Apps (Coming Soon)
We're working on simple apps you can just install:

iOS: [Link to TestFlight Beta] (waiting approval)
Android: [Link to APK] (in progress)


Option 2: For Developers - Build It Yourself
iOS (Xcode Required)

Clone the repo:

bash   git clone https://github.com/dfeen87/Smartphone-Based-Chest-Monitoring
   cd respirosync

Open the example project:

bash   cd ios/
   open RespiroSyncDemo.xcodeproj

Connect your iPhone and hit Run (▶️ button in Xcode)
Put phone in chest vest and start session

Android (Android Studio Required)

Clone the repo:

bash   git clone https://github.com/dfeen87/Smartphone-Based-Chest-Monitoring
   cd respirosync

Open in Android Studio:

   File → Open → Select android/

Connect your Android phone and hit Run (▶️ button)
Put phone in chest vest and start session


How to Position Your Phone
        👤 YOU
        
    [📱] ← Phone here
    vertical, screen out
    top edge pointing up
    
  Secure with:
  - Compression shirt pocket
  - Elastic band
  - Running armband (chest)
  - Sports bra pocket
Pro tip: The tighter and more stable, the better the accuracy.

What You'll See
Once running, you'll get real-time data:
💨 Breathing Rate: 14.2 BPM
😴 Sleep Stage: LIGHT_SLEEP
📊 Confidence: 0.87 (87%)
🎯 Breath Cycles: 23
⚠️ Apnea: No issues detected

Testing It Works (Before Sleep)
Breathe normally while sitting:

Should show 12-20 BPM
Stage: AWAKE (high movement)

Hold your breath for 15 seconds:

BPM drops to near 0
Should flag "Possible Apnea" after 10 sec

Breathe deeply and slowly:

BPM drops (8-12)
Stage may switch to LIGHT_SLEEP

Move around:

Movement intensity increases
Stage switches to AWAKE

If all of these work → you're ready for sleep tracking!

Sleep Tracking Tips
Before Bed:

✅ Charge phone to 100%
✅ Enable Do Not Disturb
✅ Put phone in Airplane Mode (optional, saves battery)
✅ Secure phone in vest/band
✅ Start RespiroSync session
✅ Place phone on nightstand for easy access if needed

Positioning:

Phone should be snug but comfortable
If it moves around, accuracy drops
Vertical orientation is critical

Battery Life:

Expect 3-5% drain over 8 hours
Enable Low Power Mode for extra safety

Morning:

Stop session when you wake up
Review your sleep report
Export data if desired


Troubleshooting
"BPM shows 0.0 or crazy numbers"

❌ Phone not positioned correctly
✅ Ensure it's vertical on chest, not loose

"Stage always shows UNKNOWN"

❌ Not enough data yet (wait 2-3 minutes)
✅ Stay still, breathe normally

"Confidence is very low (<0.3)"

❌ Phone is moving too much
✅ Tighten your vest/band

"Battery drains fast (>10%/hour)"

❌ Other apps running in background
✅ Close all apps, enable Airplane Mode

"Gyroscope not available error"

❌ Your phone is too old (pre-2015)
❌ Sensors are disabled in settings


DIY Chest Vest Ideas
Budget Option ($5):

Elastic band from craft store
Cut hole for phone
Wear over t-shirt

Athletic Option ($15):

Buy phone armband from Amazon
Wear on chest instead of arm
Perfect fit, very secure

Premium Option ($25):

Compression tank top with pocket
Most comfortable for sleep
Search "phone running vest" on Amazon

Genius Option (Free):

Old sports bra + safety pins
Seriously, it works


Privacy & Data
🔒 All processing is on-device
🚫 Nothing is uploaded to the cloud
👤 Your data stays on your phone
🗑️ Delete the app = delete all data

What's Next?
Once you've tested it:
⭐ Star the repo if it worked for you
🐛 Report bugs on GitHub Issues
💬 Share your results in Discussions
📸 Post your DIY vest (we'll feature it!)

Get Help

📖 Full Documentation: docs/
💬 Questions: GitHub Discussions
🐛 Bugs: GitHub Issues
📧 Direct Contact: dfeen87@gmail.com


Welcome to RespiroSync! You're now tracking sleep like Oura Ring users, but for $0. 🎉
