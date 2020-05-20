# From v3.0:

# Major Changes
* [f] Adds Remote Debugging capability to WebView

## New Features
* Adds Remote Debugging capability to WebView

## Bug Fixes
* Fixes crash in PermissionAwareWebChromeClient.kt

#### Updated Dependencies
* Kotlin version, gradle version, firebase analytics version and google service version - Suraj Shah

# From v2.9:

## New Features
* Adds feature to emulate QTalk calls which write participant info

#### Updated Dependencies
* Updates dependencies

# From v2.8:

# Major Changes
* Changed caller control layout. - Jaseemakhtar

# Minor Changes
* Marking audio route and mute states in the participants state field in RTDB. - Faisal Ahmed

## Bug Fixes
* Fixed bug where rtdbReference was referred before being initialised in WebViewFragment.kt.

# From v2.7:

# Major Changes
* Updating the presence state of user to "EXITED" on hang up button clicked.
* Writing "OUTGOING_VOIP" as call mode inside participants.
* Making main webview viewport similar to that inside call screen for QTalk

# From v2.6:

# Minor Changes
* Adding check if "Applet" key is present or not - Suraj Shah

# From v2.5:

# Major Changes
* Adding support for analytics event

# From v2.4:

# Major Changes
* Adding audio state and microphone controls - Suraj Shah

# From v2.3:

# Major Changes
* Adding remote user id in QTalkTestUsers.

## Bug Fixes
* Fixed bug of pushing firebaseId as tstId in QTalk RTDB

# From v2.2:

# Major Changes
* Writing call ended at to RTDB

## New Features
* Writing call ended at to RTDB
* Adding app icon

#### Updated Dependencies
* Android gradle plugin

# From v2.1:

## Bug Fixes
* Fixed user id writing for a preview call. 

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

