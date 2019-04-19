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
      "userDetails": {
         "displayName": String (nullable)
       }
    }
    ``` 
    Else: 
        Respone code: 401 (Unauthorized)

**Note:** The base url mentioned here is for the staging server, for the url for the production server please submit your web app for review [here](https://forms.gle/mir2dCUmAD1x44KW8)

## Testing steps

1. To test with the underlying `test.html` file type "test-url" when prompted for a URL. [Link](https://github.com/quiph/QGamesJSInterfaceTester/blob/master/app/src/main/assets/test.html)
 to the test HTML file
2. To test with an actual website put the link of the website as it is. The logs in `console.log` are visible in ADB logs.

## Testing with 'test users'

1. The QTalk backend has provision for 3 'test users', their tokens are present in the application. 
2. Use the value of these tokens to hit the staging server with the request URL mentioned above, just append the url param with `isTestUser=true`, final url should look something like this, 
"https://staging.remote.qtalk.io/v1/verifyAuthIdToken?isTestUser=true"
3. This will evaluate the current token as a test user and will return the the corresponding user details as well. (QTalk Test 1, QTalk Test 2 etc..)
4. Note that this will **only** work with the **Staging** server and will return an HTTP **UNAUTHORIZED** if used with the production server and will fail if the token used
is not of a test user. 

## Sending 'gameRoundStarted' and 'gameRoundEnded' events to client WebView

Send game data in the following form 

    ```json
    {
      "gameName" : String,
      "startTime" : Long,
      "endTime": Long
    }
    ``` 
    
by calling the following methods in the JSInterface.

```jshint
QTalkApp.setGameRoundStarted(json String)
```

```jshint
QTalkApp.setGameRoundEnded(json String)
```


### Download the APK: 

Check the releases section [here](https://github.com/quiph/QGamesJSInterfaceTester/releases) for the latest apk. 

Note that this is a debug apk and logs should be visible perfectly fine. 
