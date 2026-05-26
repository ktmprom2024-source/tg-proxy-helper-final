# TG Proxy Helper

Android app template for fetching Telegram MTProto proxy lists from GitHub, checking reachability, ranking candidates, and opening the best proxy in Telegram via `tg://proxy` link.

## Features
- Multi-source GitHub proxy list loading
- Basic TCP reachability checks with latency ranking
- RecyclerView-based list with manual selection
- One-tap open in Telegram
- Pull-to-refresh style main workflow (button-based MVP)
- GitHub Actions workflow to build debug APK automatically

## Build locally
Open in Android Studio Hedgehog+ and let Gradle sync. Then build `assembleDebug`.

## Build on GitHub
Push the repository to GitHub. The workflow in `.github/workflows/android-apk.yml` builds `app-debug.apk` and uploads it as an artifact.
