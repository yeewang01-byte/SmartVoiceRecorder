#!/bin/bash

# Generate Keystore for APK Signing
# This script creates a keystore for signing the Android APK

echo "🔐 Generating Android Keystore..."
echo ""

# Check if keytool is available
if ! command -v keytool &> /dev/null; then
    echo "❌ keytool not found. Please ensure Java is properly installed."
    exit 1
fi

# Create keystore
keytool -genkey -v -keystore release.keystore \
    -alias smartvoice \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000 \
    -storepass smartvoice123 \
    -keypass smartvoice123 \
    -dname "CN=SmartVoice, O=SmartVoice, L=Beijing, C=CN"

if [ -f "release.keystore" ]; then
    echo ""
    echo "✅ Keystore created successfully!"
    echo "📍 Location: release.keystore"
    echo""
    echo "ℹ️  Keystore Details:"
    echo "   - Alias: smartvoice"
    echo "   - Store Password: smartvoice123"
    echo "   - Key Password: smartvoice123"
    echo "   - Validity: 10000 days (~27 years)"
else
    echo "❌ Failed to create keystore"
    exit 1
fi
