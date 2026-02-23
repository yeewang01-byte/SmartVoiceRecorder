#!/bin/bash

# macOS Development Environment Setup
# This script installs all required dependencies for Android development

echo "========================================="
echo "macOS Android Development Environment Setup"
echo "========================================="
echo ""

# Check if Homebrew is installed
if ! command -v brew &> /dev/null; then
    echo "📥 Installing Homebrew..."
    /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
    
    # Add Homebrew to PATH
    eval "$(/opt/homebrew/bin/brew shellenv)"
fi

echo "✅ Homebrew installed"
echo ""

# Install Java 17
echo "📥 Installing Java 17..."
brew install openjdk@17

# Set JAVA_HOME
echo ""
echo "Setting JAVA_HOME environment variable..."
echo 'export JAVA_HOME=/opt/homebrew/opt/openjdk@17' >> ~/.zprofile
export JAVA_HOME=/opt/homebrew/opt/openjdk@17

echo "✅ Java 17 installed: $(java -version 2>&1 | head -1)"
echo ""

# Install Android SDK via Command Line Tools
echo "📥 Installing Android SDK Command-line Tools..."
ANDROID_SDK_ROOT="$HOME/Library/Android/sdk"

# Create SDK directory
mkdir -p "$ANDROID_SDK_ROOT"

# Download SDK tools (if needed)
if [ ! -d "$ANDROID_SDK_ROOT/cmdline-tools" ]; then
    echo "Downloading Android SDK Command-line Tools..."
    
    # Get latest cmdline-tools
    cd /tmp
    curl -o cmdline-tools.zip https://dl.google.com/android/repository/commandlinetools-mac-10406996_latest.zip
    
    unzip -q cmdline-tools.zip -d "$ANDROID_SDK_ROOT/"
    mv "$ANDROID_SDK_ROOT/cmdline-tools" "$ANDROID_SDK_ROOT/cmdline-tools-latest"
    mkdir -p "$ANDROID_SDK_ROOT/cmdline-tools/latest"
    mv "$ANDROID_SDK_ROOT/cmdline-tools-latest"/* "$ANDROID_SDK_ROOT/cmdline-tools/latest/"
    
    rm cmdline-tools.zip
    cd -
fi

# Set Android SDK environment variable
echo ""
echo "Setting ANDROID_HOME environment variable..."
echo 'export ANDROID_HOME=$HOME/Library/Android/sdk' >> ~/.zprofile
echo 'export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools' >> ~/.zprofile

export ANDROID_HOME="$ANDROID_SDK_ROOT"
export PATH="$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools"

# Accept licenses
echo ""
echo "Accepting Android SDK licenses..."
mkdir -p "$ANDROID_SDK_ROOT/licenses"
echo "24333f8a63b6825ea9c5514f83c2829b004d1fee" > "$ANDROID_SDK_ROOT/licenses/android-sdk-license"

# Install required SDK packages using sdkmanager
echo ""
echo "Installing SDK packages..."
sdkmanager --install "platforms;android-34" "build-tools;34.0.0" "platform-tools" --sdk_root="$ANDROID_SDK_ROOT" 2>/dev/null || true

echo ""
echo "✅ Android SDK installed at: $ANDROID_SDK_ROOT"
echo ""

# Verify installation
echo "📋 Verifying installation..."
java -version
echo ""
echo "JAVA_HOME: $JAVA_HOME"
echo "ANDROID_HOME: $ANDROID_HOME"

echo ""
echo "========================================="
echo "✅ Setup Complete!"
echo "========================================="
echo ""
echo "📝 Next steps:"
echo ""
echo "1. Reload your shell:"
echo "   source ~/.zprofile"
echo ""
echo "2. Navigate to the project:"
echo "   cd ~/Desktop/My_AI_Agent/SmartVoiceRecorder"
echo ""
echo "3. Build the APK:"
echo "   ./build_apk.sh"
echo ""
