# GitHub Actions

This project ships with two Android workflows:

- `android-ci.yml`
  Runs on pushes and pull requests.
  Builds the debug app, runs unit tests, and runs lint.

- `android-release.yml`
  Runs on version tags like `v1.0.0` or manually from GitHub Actions.
  Builds a release APK and uploads it as an artifact.

## Required secrets for signed release builds

Add these repository secrets if you want GitHub Actions to produce a signed release APK:

- `SCHOOLBRIDGE_UPLOAD_KEYSTORE_BASE64`
- `SCHOOLBRIDGE_UPLOAD_STORE_PASSWORD`
- `SCHOOLBRIDGE_UPLOAD_KEY_ALIAS`
- `SCHOOLBRIDGE_UPLOAD_KEY_PASSWORD`

If those secrets are missing, the release workflow still runs, but release signing will not be applied.

## Notes

- CI injects `SCHOOLBRIDGE_API_BASE_URL=https://example.invalid` so builds do not depend on a private local IP.
- Local development can continue using `local.properties`.
