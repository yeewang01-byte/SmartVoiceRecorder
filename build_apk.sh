#!/bin/bash

# App Build Script
# This script builds the Android APK

set -e

echo "================================"
echo "Smart Voice Recorder Build Script"
echo "================================"
echo ""

# Check if gradle exists
if ! command -v ./gradlew &> /dev/null; then
    echo "❌ Gradle not found. Please ensure you're in the SmartVoiceRecorder directory."
    exit 1
fi

echo "📦 Starting build process..."
echo ""

# Clean previous builds
echo "🧹 Cleaning previous builds..."
./gradlew clean

# Build release APK
echo "🔨 Building release APK..."
./gradlew assembleRelease

# Check if build was successful
if [ -f "app/build/outputs/apk/release/app-release.apk" ]; then
    echo ""
    echo "✅ Build successful!"
    echo "📍 APK location: app/build/outputs/apk/release/app-release.apk"
    echo ""
    
    # Sign the APK if keystore exists
    if [ -f "release.keystore" ]; then
        echo "🔐 Signing APK..."
        jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
            -keystore release.keystore \
            app/build/outputs/apk/release/app-release.apk alias_name
        echo "✅ APK signed successfully!"
    else
        echo "⚠️  No keystore found. The APK is unsigned."
        echo "   To sign the APK, create a keystore and update the build script."
    fi
    
    echo ""
    echo "📊 Build Summary:"
    ls -lh app/build/outputs/apk/release/
else
    echo ""
    echo "❌ Build failed. Check the output above for errors."
    exit 1
fi
