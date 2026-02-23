# Complete Setup & Build Guide

## 🚀 Getting Started

Choose your setup method below:

---

## Option 1: macOS with Homebrew (Recommended)

### Prerequisites:
- macOS 10.15+
- Homebrew installed (or will be installed by script)
- At least 5GB free disk space

### Quick Setup:

```bash
cd ~/Desktop/My_AI_Agent/SmartVoiceRecorder

# Run the setup script
chmod +x macos_setup.sh
./macos_setup.sh

# Reload shell configuration
source ~/.zprofile

# Build the APK
./build_apk.sh
```

The APK will be generated at:
```
app/build/outputs/apk/release/app-release.apk
```

---

## Option 2: Using Docker (Easiest - No local setup required)

### Prerequisites:
- Docker installed
- Docker Compose (optional)
- ~2GB free disk space (image size)

### Build with Docker:

```bash
cd ~/Desktop/My_AI_Agent/SmartVoiceRecorder

# Simple Docker build:
docker build -t smartvoice-builder .

# Run build in container:
docker run -v $(pwd):/workspace smartvoice-builder

# Or use Docker Compose:
docker-compose up --build
```

The APK will be copied to:
```
app/build/outputs/apk/release/app-release.apk
```

---

## Option 3: Android Studio (GUI Method)

### Prerequisites:
- Android Studio 2023.1+ installed
- Android SDK configured

### Steps:

1. Open Android Studio
2. Select "Open" → Navigate to `SmartVoiceRecorder` folder
3. Let Gradle sync
4. **Build** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
5. APK location will be shown in notification

---

## Option 4: Windows/Linux Manual Setup

### Prerequisites:
- JDK 17 or later
- Android SDK (API 34, Build Tools 34.0.0)
- Git

### Installation Steps:

#### 1. Install Java 17

**Windows:**
```cmd
# Download from: https://adoptium.net/
# Or use Chocolatey:
choco install temurin17
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt-get install openjdk-17-jdk
```

#### 2. Install Android SDK

Download from: https://developer.android.com/studio/command-line-tools

Extract and set environment:
```bash
export ANDROID_HOME=/path/to/android/sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
```

#### 3. Install SDK Components

```bash
sdkmanager "platforms;android-34" "build-tools;34.0.0" "platform-tools"
```

#### 4. Build APK

```bash
cd SmartVoiceRecorder

# On Windows:
gradlew assembleRelease

# On Linux/macOS:
./gradlew assembleRelease
```

---

## 📍 APK Output Locations

After successful build:

```
SmartVoiceRecorder/
└── app/
    └── build/
        └── outputs/
            ├── apk/
            │   ├── debug/
            │   │   └── app-debug.apk         # Unsigned debug build
            │   └── release/
            │       └── app-release.apk       # Signed release build ✅
            └── bundle/
                └── release/
                    └── app-release.aab       # For Google Play Store
```

---

## ✅ Installation on Device

### Via ADB:
```bash
# Install APK from build output
adb install app/build/outputs/apk/release/app-release.apk

# Or directly from Android Studio (automatically)
```

### Via File Manager:
1. Transfer `app-release.apk` to Android device
2. Open file manager
3. Tap APK file
4. Follow installation prompts

---

## ⚙️ Gradle Build Commands

```bash
# Clean build
./gradlew clean

# Debug APK (faster, unsigned)
./gradlew assembleDebug

# Release APK (signed, optimized)
./gradlew assembleRelease

# Bundle for Play Store
./gradlew bundleRelease

# Install on connected device
./gradlew installDebug

# Run on emulator/device
./gradlew run

# Show available tasks
./gradlew tasks

# Gradle daemon status
./gradlew --status
```

---

## 🔍 Troubleshooting

### Issue: "Java not found"
**Solution:**
```bash
# macOS:
brew install openjdk@17
export JAVA_HOME=/opt/homebrew/opt/openjdk@17

# Linux:
sudo apt-get install openjdk-17-jdk

# Windows:
choco install temurin17
```

### Issue: "Android SDK not found"
**Solution:**
```bash
# Set ANDROID_HOME
export ANDROID_HOME=$HOME/Library/Android/sdk  # macOS
export ANDROID_HOME=~/Android/Sdk              # Linux
set ANDROID_HOME=%USERPROFILE%\AppData\Local\Android\Sdk  # Windows
```

### Issue: "Build fails with gradle error"
**Solution:**
```bash
# Clear gradle cache
./gradlew clean
rm -rf .gradle/

# Rebuild
./gradlew assembleRelease --debug
```

### Issue: "Insufficient disk space"
**Solution:**
```bash
# Clean gradle cache
rm -rf ~/.gradle/caches/

# Reduce APK size in build.gradle
minifyEnabled = true
```

---

## 📦 APK Information

**App Details:**
- Name: 智能录音笔 (Smart Voice Recorder)
- Package: com.smartvoice.recorder
- Version: 1.0.0
- Min SDK: Android 9.0 (API 28)
- Target SDK: Android 14 (API 34)

**APK Size:** ~5-8 MB (depends on build type)

**Permissions:**
- RECORD_AUDIO
- INTERNET
- READ/WRITE_EXTERNAL_STORAGE

---

## 🔐 APK Signing

The build script automatically signs the release APK with:
- **Keystore**: `release.keystore` (generated)
- **Alias**: smartvoice
- **Validity**: 10,000 days (~27 years)

To create your own signing key:
```bash
./generate_keystore.sh
```

---

## 📊 System Requirements

### Minimum:
- 2GB RAM
- 5GB disk space
- Internet connection

### Recommended:
- 4GB+ RAM
- 10GB+ disk space
- SSD for faster builds
- macOS/Linux/Windows 10+

---

## 🚀 Distribution

### Share APK File
```bash
# Send app-release.apk via:
# - Email
# - Cloud storage (Google Drive, Dropbox)
# - GitHub releases
# - Direct download link
```

### Google Play Store
1. Create signing keystore
2. Build Bundle: `./gradlew bundleRelease`
3. Upload to Play Console
4. Review and publish

### Enterprise Distribution
1. Host signed APK on server
2. Share download link
3. Users install via browser or ADB

---

## 📞 Support

- **Build Issues**: Check BUILD_GUIDE.md
- **Project Structure**: See PROJECT_STATUS.md
- **Code Details**: Review README.md
- **Feature Requests**: Update PRD documentation

---

**Last Updated**: 2026-02-24  
**Build System**: Gradle 8.1  
**Kotlin Version**: 1.9.0  
**Compose Version**: 1.5.4
