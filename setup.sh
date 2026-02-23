#!/bin/bash

# Setup Script for Smart Voice Recorder Development Environment
# Creates necessary gradle wrapper files if they don't exist

set -e

echo "================================"
echo "Setup Smart Voice Recorder"
echo "================================"
echo ""

ANDROID_SDK_ROOT=${ANDROID_HOME:-$HOME/Library/Android/sdk}
GRADLE_VERSION="8.1.0"

echo "📋 Checking environment..."

# Check Android SDK
if [ -d "$ANDROID_SDK_ROOT" ]; then
    echo "✅ Android SDK found at: $ANDROID_SDK_ROOT"
else
    echo "⚠️  Android SDK not found. Please set ANDROID_HOME environment variable."
    echo "   Expected location: $ANDROID_SDK_ROOT"
fi

# Check Java
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | grep 'version' | cut -d'"' -f2)
    echo "✅ Java found: version $JAVA_VERSION"
else
    echo "❌ Java not found. Please install Java JDK 17 or later."
    exit 1
fi

echo ""
echo "🔧 Setting up Gradle..."

# Make gradle executable
if [ -f "gradlew" ]; then
    chmod +x gradlew
    chmod +x gradle/wrapper/gradle-wrapper.jar
    echo "✅ Gradle wrapper is ready"
else
    echo "⚠️  Gradle wrapper not found. You may need to initialize the project."
fi

echo ""
echo "📦 Installing dependencies..."
./gradlew --version

echo ""
echo "✅ Setup complete!"
echo ""
echo "Next steps:"
echo "1. Open the project in Android Studio"
echo "2. Sync Gradle files (if not automatic)"
echo "3. Run: ./build_apk.sh to build the APK"
echo ""
