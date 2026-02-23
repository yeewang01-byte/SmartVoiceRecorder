# Use Android SDK Docker image
FROM gradle:8.1-jdk17

# Install additional tools
RUN apt-get update && apt-get install -y \
    android-sdk \
    git \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Set Android SDK environment
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools

# Create Android SDK directory
RUN mkdir -p $ANDROID_HOME/cmdline-tools

# Accept Android SDK licenses
RUN mkdir -p $ANDROID_HOME/licenses
RUN echo "24333f8a63b6825ea9c5514f83c2829b004d1fee" > $ANDROID_HOME/licenses/android-sdk-license

# Install Android SDK packages using sdkmanager
RUN sdkmanager --sdk_root=$ANDROID_HOME "platforms;android-34" "build-tools;34.0.0" "platform-tools" --verbose || true

# Set working directory
WORKDIR /workspace

# Copy project files
COPY . .

# Build the APK
RUN chmod +x gradlew
RUN ./gradlew clean assembleRelease

# Output will be at: /workspace/app/build/outputs/apk/release/app-release.apk
