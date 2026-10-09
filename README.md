# FitForge

An Android fitness tracker for running, cycling, weightlifting, and badminton.

## Contents

- `app/` and `gradle/` — Android Studio project source.
- `backend/hhk_FitTest/` — PHP endpoints used by the app.
- `database/schema.sql` — schema-only database setup. Sample account and workout records were excluded.

## Run locally

1. Open this folder in Android Studio and let Gradle sync the project.
2. Set up the `fitforge_db` database from `database/schema.sql` in a local MySQL environment.
3. Serve `backend/hhk_FitTest/` from a local PHP server. The Android emulator endpoints use `10.0.2.2` to reach the host computer.

Android Studio build output and machine-specific `local.properties` are not included.

Before running the app, replace `YOUR_GOOGLE_MAPS_API_KEY` in `app/src/main/res/values/google_map_API.xml` with a restricted key for your own Google Cloud project. Do not commit a private or unrestricted API key.
