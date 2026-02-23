# Smart Voice Recorder - Android Application

A professional voice recording and AI transcription app built with Kotlin and Jetpack Compose.

## Features

- 🎙️ **Real-time Audio Recording** - Record high-quality audio with the device microphone
- 🌊 **Live Spectrum Visualization** - Real-time frequency spectrum and waveform display  
- 🤖 **AI Transcription** - Automatic speech-to-text conversion (supports Chinese & English)
- 📝 **Smart Summarization** - AI-powered summary generation with key points and action items
- 💾 **Local Database** - All recordings stored locally for privacy
- 🔍 **Search & Filter** - Easy searching by title and content
- 📤 **Share Functionality** - Share transcripts via email or messaging apps

## Architecture

The app follows a clean architecture with:
- **Data Layer** - Room database for local persistence
- **Domain Layer** - Business logic and AI services
- **Presentation Layer** - Jetpack Compose UI with MVVM ViewModels

### Project Structure

```
app/
├── src/main/java/com/smartvoice/recorder/
│   ├── data/
│   │   ├── database/ (Room DB)
│   │   └── model/ (Data classes)
│   ├── domain/
│   │   ├── audio/ (Recording & Spectrum)
│   │   └── ai/ (Transcription & Summarization)
│   ├── presentation/ (ViewModels)
│   ├── ui/ (Compose Screens)
│   └── MainActivity.kt
├── res/
│   └── values/ (Colors, Strings, Themes)
└── AndroidManifest.xml
```

## Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Database**: Room + SQLite
- **Architecture**: MVVM with StateFlow
- **Minimum API**: Android 9.0 (API 28)
- **Target API**: Android 14 (API 34)

## Building

### Prerequisites
- Android Studio Giraffe or later
- JDK 17
- Android SDK (API level 34)

### Build Steps

1. Clone the repository
2. Open in Android Studio
3. Let Gradle sync dependencies
4. Run on emulator or device

```bash
# Build APK
./gradlew assembleRelease

# Run on device
./gradlew installDebug
```

## Permissions

The app requires:
- **RECORD_AUDIO** - For microphone recording
- **READ/WRITE_EXTERNAL_STORAGE** - For audio file management
- **INTERNET** - For AI API calls (optional)

## Configuration

### API Keys

To enable cloud-based transcription and summarization:

1. Create config file: `app/src/main/res/values/api_config.xml`
2. Add your API keys:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="openai_api_key">your_key_here</string>
    <string name="transcription_api_key">your_key_here</string>
</resources>
```

## Usage

### Recording

1. Open the app and tap the record button (🎙️)
2. Speak into the microphone
3. Tap Stop when done
4. Enter a title for the recording
5. Recording is automatically saved

### Transcription & Summarization

1. Open a saved recording
2. Select the Summary tab
3. Tap "Generate Summary" to analyze the recording
4. View extracted key points and action items

### Search

Use the search box on Home screen to find recordings by:
- Title
- Transcript content
- Tags

## Performance

- **App Launch**: < 2 seconds
- **Recording Start**: < 500ms
- **Search Response**: < 300ms
- **Transcription Latency**: Near real-time

## Testing

Run unit tests:
```bash
./gradlew test
```

Run instrumentation tests:
```bash
./gradlew connectedAndroidTest
```

## Known Limitations

- Local transcription requires Android's built-in speech recognition
- Cloud APIs require network connectivity and API keys
- Audio files stored locally require adequate device storage

## Future Enhancements

- [ ] Cloud sync across devices
- [ ] Team collaboration features  
- [ ] Advanced audio editing
- [ ] Speaker identification
- [ ] Sentiment analysis
- [ ] Integration with calendar apps

## License

Proprietary - All Rights Reserved

## Support

For issues or feature requests, please contact the development team.
