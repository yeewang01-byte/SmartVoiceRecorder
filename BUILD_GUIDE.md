# Smart Voice Recorder - Build & Deployment Guide

## 📋 Quick Start

### Prerequisites
- macOS (or Linux/Windows with Android SDK)
- Android Studio Giraffe or later
- JDK 17+
- Git

### 1. Setup Project

```bash
cd /Users/wangyi/Desktop/My_AI_Agent/SmartVoiceRecorder

# Make scripts executable
chmod +x gradlew setup.sh build_apk.sh generate_keystore.sh

# Run setup
./setup.sh
```

### 2. Configure Local SDK Path

Edit or create `local.properties`:
```properties
sdk.dir=/Users/YOUR_USERNAME/Library/Android/sdk
```

Or set environment variable:
```bash
export ANDROID_HOME=/Users/YOUR_USERNAME/Library/Android/sdk
```

### 3. Generate Signing Keystore

```bash
./generate_keystore.sh
```

This creates `release.keystore` with:
- Alias: `smartvoice`
- Password: `smartvoice123`

### 4. Build APK

```bash
./build_apk.sh
```

The APK will be generated at:
```
app/build/outputs/apk/release/app-release.apk
```

---

## 🔨 Manual Build Commands

### Clean Build
```bash
./gradlew clean
```

### Build Debug APK (unsigned)
```bash
./gradlew assembleDebug
```
Output: `app/build/outputs/apk/debug/app-debug.apk`

### Build Release APK (unsigned)
```bash
./gradlew assembleRelease
```
Output: `app/build/outputs/apk/release/app-release.apk`

### Install and Run on Device
```bash
./gradlew installDebug
./gradlew run
```

### Run Tests
```bash
./gradlew test
./gradlew connectedAndroidTest
```

### View Build Report
```bash
./gradlew tasks
```

---

## 📦 APK Signing

### Option 1: Manual Signing

```bash
# Create keystore (one time)
keytool -genkey -v -keystore my.keystore \
    -alias my_alias \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000

# Sign APK
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
    -keystore my.keystore \
    app/build/outputs/apk/release/app-release-unsigned.apk \
    my_alias

# Verify APK signature
jarsigner -verify -verbose app/build/outputs/apk/release/app-release-unsigned.apk
```

### Option 2: Configure in build.gradle (Automatic)

Edit `app/build.gradle.kts`:
```kotlin
signingConfigs {
    create("release") {
        storeFile = file("path/to/release.keystore")
        storePassword = "smartvoice123"
        keyAlias = "smartvoice"
        keyPassword = "smartvoice123"
    }
}

buildTypes {
    release {
        signingConfig = signingConfigs.getByName("release")
        isMinifyEnabled = false
    }
}
```

---

## 📱 Installation on Device/Emulator

### Via ADB
```bash
# Install debug APK
adb install app/build/outputs/apk/debug/app-debug.apk

# Install release APK  
adb install app/build/outputs/apk/release/app-release.apk

# Uninstall
adb uninstall com.smartvoice.recorder

# View logs
adb logcat | grep SmartVoice

# Clear app data
adb shell pm clear com.smartvoice.recorder
```

### Via Android Studio
1. Open the project in Android Studio
2. Connect device or start emulator
3. Click **Run** (or press `Shift+F10`)
4. Select target device

---

## 🐛 Troubleshooting

### Build Fails with Gradle Error
```bash
# Clear gradle cache
./gradlew clean
rm -rf ".gradle"

# Rebuild
./gradlew assembleRelease
```

### Permission Denied on gradlew
```bash
chmod +x gradlew
chmod +x gradle/wrapper/*
```

### Android SDK Not Found
```bash
# Check SDK location
echo $ANDROID_HOME

# Set environment variable
export ANDROID_HOME=/Users/$(whoami)/Library/Android/sdk

# Or create local.properties
cp local.properties.example local.properties
# Edit local.properties with correct path
```

### Java Version Error
```bash
# Check Java version
java -version

# Should be JDK 17+
# If not, install via Homebrew:
# brew install java17
# or set JAVA_HOME:
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

### Signing Key Alias Not Found
```bash
# List aliases in keystore
keytool -list -v -keystore release.keystore -storepass smartvoice123
```

---

## 📊 Build Variants

### Debug Build
- Unsigned, fast compilation
- Debug logging enabled
- Use for development/testing

### Release Build
- Signed and optimized
- ProGuard obfuscation enabled
- Ready for distribution

---

## 🔐 Security Considerations

### Before Distribution:
1. **Never commit keystore to Git**
   ```bash
   echo "release.keystore" >> .gitignore
   ```

2. **Create unique keystore passwords**
   ```bash
   # Change from default smartvoice123
   ```

3. **Secure keystore file**
   ```bash
   chmod 600 release.keystore
   ```

4. **Remember credentials**
   - Store password in secure location
   - Backup keystore file
   - Keystore is needed for app updates

---

## 📈 Performance Tips

### Speed Up Builds
```bash
# Parallel builds
./gradlew assembleRelease --parallel

# Gradle daemon (runs in background)
./gradlew assembleRelease --warning-mode=summary

# Use incremental compiler
export ORG_GRADLE_PROJECT_KOTLIN_INCREMENTAL=true
```

### Reduce APK Size
1. Enable ProGuard/R8 minification
2. Remove unused dependencies
3. Use vector drawables instead of PNGs
4. Enable compression in build.gradle

---

## 📝 Build Output Files

After successful build:
```
SmartVoiceRecorder/
└── app/build/outputs/
    ├── apk/
    │   ├── debug/
    │   │   └── app-debug.apk
    │   └── release/
    │       └── app-release.apk
    └── bundle/
        └── release/
            └── app-release.aab (for Play Store)
```

---

## 🚀 Distribution

### Google Play Store
1. Build release APK: `./gradlew bundleRelease`
2. Sign with release keystore
3. Upload to Play Store Console

### Direct Distribution
1. Build release APK: `./gradlew assembleRelease`
2. Share `app-release.apk` file
3. Users install via ADB or file manager

### Enterprise Distribution
1. Build signed APK
2. Use MDM solution
3. Host on internal server

---

## 📞 Support

For build issues:
1. Check Android SDK version (API 34+)
2. Verify Java version (JDK 17+)
3. Check network connectivity (for dependencies)
4. Review gradle logs: `./gradlew assembleRelease --debug`

---

**Last Updated**: 2026-02-24
**Build System**: Gradle 8.1
**Target API**: 34 (Android 14)
