#!/bin/bash

# E-Commerce PWA Android Setup Script
# This script helps set up the environment for building the Android application

echo "=== E-Commerce PWA Android Setup Script ==="
echo

# Check if Java is installed
echo "Checking for Java..."
if command -v java &> /dev/null; then
    java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    echo "Java is installed (version $java_version)"
else
    echo "Java is not installed. Installing OpenJDK..."
    sudo apt update
    sudo apt install -y openjdk-11-jdk
    echo "Java installed."
fi

# Check for Android SDK
echo
echo "Checking for Android SDK..."
if [ -d "$HOME/Android/Sdk" ]; then
    echo "Android SDK found at $HOME/Android/Sdk"
    ANDROID_SDK_ROOT="$HOME/Android/Sdk"
else
    echo "Android SDK not found."
    echo "Would you like to install the Android command-line tools? (y/n)"
    read -r install_sdk
    
    if [[ $install_sdk == "y" || $install_sdk == "Y" ]]; then
        echo "Installing Android command-line tools..."
        
        # Create Android directory
        mkdir -p "$HOME/Android/Sdk/cmdline-tools"
        cd "$HOME/Android/Sdk/cmdline-tools" || exit
        
        # Download command-line tools
        wget https://dl.google.com/android/repository/commandlinetools-linux-9477386_latest.zip
        unzip commandlinetools-linux-9477386_latest.zip
        mv cmdline-tools latest
        rm commandlinetools-linux-9477386_latest.zip
        
        # Set up environment variables
        echo "export ANDROID_SDK_ROOT=$HOME/Android/Sdk" >> "$HOME/.bashrc"
        echo "export PATH=\$PATH:\$ANDROID_SDK_ROOT/cmdline-tools/latest/bin:\$ANDROID_SDK_ROOT/platform-tools" >> "$HOME/.bashrc"
        
        # Source the updated bashrc
        source "$HOME/.bashrc"
        
        # Install necessary SDK components
        yes | sdkmanager --licenses
        sdkmanager "platform-tools" "platforms;android-33" "build-tools;33.0.0"
        
        echo "Android SDK components installed."
        ANDROID_SDK_ROOT="$HOME/Android/Sdk"
    else
        echo "Skipping Android SDK installation."
        echo "You'll need to install Android Studio or the Android SDK manually to build the project."
    fi
fi

# Check if adb is installed
echo
echo "Checking for ADB..."
if command -v adb &> /dev/null; then
    echo "ADB is installed."
else
    echo "ADB not found. Installing Android platform tools..."
    sudo apt update
    sudo apt install -y android-tools-adb android-tools-fastboot
    echo "ADB installed."
fi

# Check for Gradle
echo
echo "Checking for Gradle..."
if command -v gradle &> /dev/null; then
    gradle_version=$(gradle --version | grep Gradle | head -n 1 | cut -d' ' -f2)
    echo "Gradle is installed (version $gradle_version)"
else
    echo "Gradle not found. The project includes a Gradle wrapper that will download Gradle automatically."
fi

echo
echo "Setup complete. You can now build the project using:"
echo "./gradlew assembleDebug"
echo
echo "To install on a connected device:"
echo "./gradlew installDebug"
echo
echo "Note: You may need to restart your terminal for all environment changes to take effect." 