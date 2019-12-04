# From v2.0:

## Bug Fixes
* Fixed audio stream auto play bug

#### Updated Dependencies
* Kotlin to 1.3.61 

# From v1.7:

# Major Changes
* Adding Interaction types
* Adding option to make a preview call, writing to QTalk debug rtdb with participant info.
* Integrating firebase sdk.

## New Features
* Adding Interaction types
* Adding feature to save last entered url and use it as the input in the next open
* Adding option to make a preview call, writing to QTalk debug rtdb with participant info.
* Adding deeplink to open the dialog, listening to http and https links

## Bug Fixes
* Fixing intent problems when opening from deeplink.

# From v1.6:

# Major Changes
* Adding permission aware capabilities for Camera and microphone permissions at webkit level
* Bumped `minSdk` version to 21
* Abstracted out JSInterface.kt and added bridge interface to communicate with consuming component.

#### Updated Dependencies
* Updating dependencies
* Updating kotlin version to 1.3.60

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

