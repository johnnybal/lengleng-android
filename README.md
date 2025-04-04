# LengLeng Android App Deployment

This document explains how to deploy the LengLeng Android app for different environments.

## Prerequisites

- Android Studio
- Android SDK
- Gradle
- ADB (Android Debug Bridge)
- Keystore file for signing release builds
- Git

## Environment Variables

For staging and production builds, you need to set the following environment variables:

```bash
export KEYSTORE_PASSWORD=your_keystore_password
export KEY_ALIAS=your_key_alias
export KEY_PASSWORD=your_key_password
```

## Deployment Scripts

### Development Deployment

To deploy a development build to a connected device:

```bash
./deploy-dev.sh
```

This will:
1. Clean the project
2. Build a debug APK
3. Install it on the connected device
4. Launch the app

### Staging Deployment

To create a staging release build:

```bash
./deploy-staging.sh
```

This will:
1. Clean the project
2. Build a signed release APK
3. Generate SHA-256 hash
4. Place artifacts in `deploy/staging/`

### Production Deployment

To create a production release build:

```bash
./deploy-prod.sh
```

This will:
1. Clean the project
2. Build signed release APK and AAB
3. Generate SHA-256 hashes
4. Generate version info
5. Place artifacts in `deploy/prod/`

To also upload to Google Play Console:

```bash
./deploy-prod.sh --upload
```

## GitHub Integration

### Initial Setup

1. Create a new repository on GitHub
2. Initialize the local repository:
```bash
git init
git remote add origin <repository-url>
```

### Pushing Changes

To push changes to GitHub:

```bash
./push-to-github.sh
```

This will:
1. Check if Git is installed
2. Initialize repository if needed
3. Add all files
4. Commit changes with timestamp
5. Push to the main branch

### Important Notes

- The `.gitignore` file excludes sensitive information like keystore files and build artifacts
- Never commit the following files:
  - `keystore/release.keystore`
  - `google-services.json`
  - Any API keys or secrets

## Build Types

- **Debug**: For development and testing
- **Release**: For staging and production

## Product Flavors

- **dev**: Development environment
- **staging**: Staging environment
- **prod**: Production environment

## Output Files

### Development
- `app/build/outputs/apk/dev/debug/app-dev-debug.apk`

### Staging
- `deploy/staging/app-staging-release.apk`
- `deploy/staging/app-staging-release.apk.sha256`

### Production
- `deploy/prod/app-prod-release.apk`
- `deploy/prod/app-prod-release.aab`
- `deploy/prod/app-prod-release.apk.sha256`
- `deploy/prod/app-prod-release.aab.sha256`
- `deploy/prod/version.txt`

## Troubleshooting

1. **Build Fails**
   - Check if all environment variables are set
   - Verify keystore file exists and is valid
   - Check Gradle version compatibility

2. **Installation Fails**
   - Ensure device is connected and authorized
   - Check if previous version is uninstalled
   - Verify sufficient storage space

3. **App Crashes**
   - Check logcat for error messages
   - Verify Firebase configuration
   - Ensure all required permissions are granted

4. **Git Push Fails**
   - Verify GitHub credentials
   - Check internet connection
   - Ensure repository URL is correct
   - Make sure you have write access to the repository 