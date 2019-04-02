# QGamesJSInterfaceTester

An android application to test the JS Interface used to get the user auth token when fetched from inside the QTalk app. 

## How does it work?

The mechanism used internally is a JS Bridge provided by the android framework, [doc](https://developer.android.com/guide/webapps/webview.html#BindingJavaScript)

This bridge, called as JS interface can be used both to get values from android and provide values to the underlying android app as well.

## Getting the user auth token

On the JS side, to get the user auth token from the device running the web app in WebView, use the following code: 

```jshint
var token = QTalkApp.getUserAuthToken()
```

## Validating the token

To validate the token, use the following endpoint for staging: 

API Endpoint: https://staging.remote.qtalk.io/v1/verifyAuthIdToken

Method: GET

Headers: x-auth-id-token: `token` (this token value is the one fetched initially)

Reponse:
    If validated: 200
    ```json
    {
      "isTokenValid" : Boolean,
      "userId" : String (nullable),
    }
    ``` 
    Else: 
        Respone code: 401 (Unauthorized)

## Testing steps

1. To test with the underlying `test.html` file type "test-url" when prompted for a URL
2. To test with an actual website put the link of the website as it is. The logs in `console.log` are visible in ADB logs.


### Download the APK: 

Check the releases section [here](https://github.com/quiph/QGamesJSInterfaceTester/releases) for the latest apk. 

Note that this is a debug apk and logs should be visible perfectly fine. 
