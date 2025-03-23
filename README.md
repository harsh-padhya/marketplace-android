# E-Commerce PWA Android Application

This Android application renders an e-commerce Progressive Web App (PWA) within a native Android wrapper. It provides a configurable web URL that can be changed through the app's settings.

## Features

- **PWA Integration**: Optimized WebView with service worker support
- **Offline Mode**: Detection and handling of connection loss
- **Configurable Web URL**: Can be changed through the settings screen
- **Pull-to-Refresh**: Easily refresh the web content
- **Back Navigation**: Proper web history navigation with the back button

## Project Structure

- `app/src/main/java/com/example/ecommercepwa/`
  - `MainActivity.java`: Main WebView container with PWA support
  - `SettingsActivity.java`: Configuration screen for the web URL
  - `ConfigManager.java`: Manages configuration and preferences
  - `NetworkUtils.java`: Utility for checking network connectivity

- `app/src/main/res/`: Resources directory
  - `layout/`: UI layouts
  - `values/`: Strings, colors, and styles
  - `xml/`: Web security configuration

- `app/src/main/assets/config.json`: Default configuration file

## Prerequisites

To build and run this project, you need the following tools installed:

- Android Studio 2022.1.1 or newer
- Java Development Kit (JDK) 8 or newer
- Android SDK (minimum API level 23)
- Gradle 8.2 or newer

### Installing Prerequisites (Manual Installation)

#### Java Development Kit (JDK)

Install OpenJDK 11:
```bash
sudo apt update
sudo apt install openjdk-11-jdk
```

Verify installation:
```bash
java -version
```

#### Android SDK (Option 1 - Android Studio)

1. Download Android Studio from https://developer.android.com/studio
2. Install Android Studio:
   ```bash
   cd ~/Downloads
   unzip android-studio-*.zip
   sudo mv android-studio /opt/
   /opt/android-studio/bin/studio.sh
   ```
3. Follow the setup wizard and install the recommended SDK components

#### Android SDK (Option 2 - Command Line Tools Only)

1. Download the command-line tools from https://developer.android.com/studio#command-tools
2. Set up the SDK:
   ```bash
   mkdir -p ~/Android/Sdk/cmdline-tools
   unzip commandlinetools-linux-*.zip -d ~/Android/Sdk/cmdline-tools
   mv ~/Android/Sdk/cmdline-tools/cmdline-tools ~/Android/Sdk/cmdline-tools/latest
   ```
3. Add SDK to your PATH:
   ```bash
   echo 'export ANDROID_SDK_ROOT=$HOME/Android/Sdk' >> ~/.bashrc
   echo 'export PATH=$PATH:$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:$ANDROID_SDK_ROOT/platform-tools' >> ~/.bashrc
   source ~/.bashrc
   ```
4. Install necessary SDK components:
   ```bash
   sdkmanager "platform-tools" "platforms;android-33" "build-tools;33.0.0"
   ```

## Building the Project

Once you have installed the prerequisites, you can build the project:

### Using Android Studio
1. Open Android Studio
2. Select "Open an existing project"
3. Navigate to the project directory and click "OK"
4. Wait for Gradle sync to complete
5. Click "Run" or "Debug" to build and install the app

### Using Command Line

```bash
# Navigate to the project directory
cd /path/to/ECommercePWA

# Build debug APK
./gradlew assembleDebug

# Install to connected device
./gradlew installDebug
```

## Configuration

The app loads its initial configuration from `app/src/main/assets/config.json`. You can update the default web URL by modifying this file:

```json
{
  "webUrl": "https://store-admin-v2.vercel.app/login"
}
```

Users can also update the URL through the app's settings menu. The new URL will be stored in the app's SharedPreferences.

## PWA Support

This app supports core PWA features:

- Service Workers for offline capabilities
- Caching strategies for assets and API responses
- Local storage and IndexedDB for client-side data storage
- Proper handling of web app manifest

## Testing on a Device

1. Enable Developer Options on your Android device
   - Go to Settings > About phone
   - Tap "Build number" 7 times to enable Developer Options
   
2. Enable USB Debugging
   - Go to Settings > Developer Options
   - Enable USB Debugging
   
3. Connect your device to your computer
   - Accept the USB debugging authorization prompt on your device
   
4. Install and run the app
   - Using Android Studio: Click "Run" button
   - Using command line: `./gradlew installDebug`

## Troubleshooting

### Build Issues
- Make sure you have the correct JDK version installed
- Verify Android SDK components are installed correctly
- Check Gradle version compatibility

### Device Connection Issues
- Ensure USB debugging is enabled on the device
- Try different USB cables or ports
- Install USB drivers for your device if necessary
- Run `adb devices` to check if your device is recognized

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Credits

Developed as part of the PWA integration requirements for an e-commerce platform.