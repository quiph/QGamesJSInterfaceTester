# From v1.5:

## Bug Fixes
* Fixed clear text url usage for devices running Android 9 and above.

### Optimizations
* Making localhost IP have `http` by default instead of `https`

# From v1.4:

# Major Changes
* Keeping `https` as the default protocol if not provided.
* Making target and source compatibility to java 8.

## Bug Fixes
* Fixed `updateGamePrompts` and `clearGamePrompts` using local test.html functions for callbacks.
* Fixed exception in gradle sync due to `variantOutput` field usage.

### Optimizations
* Adding final apk name generation build automation.
* Keeping `https` as the default protocol if not provided.

#### Updated Dependencies
* Updated AndroidX Components
* MaterialDialogs from `2.6.0` to `3.0.1`

# From v1.3:

## New Features
* Adding prompts support.
* Adding clear WebView cache functionality.

