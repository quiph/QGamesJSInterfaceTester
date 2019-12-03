# QGamesJSInterfaceTester

An android application to test the JS Interface used to get the user auth token when fetched from inside the QTalk app. 

## How does it work?

The mechanism used internally is a JS Bridge provided by the android framework, [doc](https://developer.android.com/guide/webapps/webview.html#BindingJavaScript)

This bridge, called as JS interface can be used both to get values from android and provide values to the underlying android app as well.

## Getting the user auth token

On the JS side, to get the user auth token from the device running the web app in WebView, use the following code: 

```javascript
var token = QTalkApp.getUserAuthToken();
```

## Validating the token

To validate the token, use the following endpoint for staging: 

API Endpoint: https://staging.remote.qtalk.io/utilities/v1/verifyAuthIdToken

Method: **GET**

Headers: x-auth-id-token: `token` (this token value is the one fetched initially)

Reponse:
    If validated: 200
    
```javascript
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

### Debugging `verifyAuthIdToken` api

The value passed in `x-auth-id-token` is a JWT, JWTs expire every 15mins or so, thus having an updated token everytime is problem. To solve this while debugging with 
the api, append a url param as `isDebug=true` and send the converted `uid` from the token (which you can save somewhere locally). 

Remember, this will *skip* authentication and will just consume the auth id as it is and **will not** work in production environments.

[Link](https://jwt.io/introduction/) for reading more about JWTs and how they work.
[Link](https://jwt.io/) for getting a `uid` from the JWT. 

## Sending 'gameRoundStarted' and 'gameRoundEnded' events to client WebView

The QTalk App requires the underline game to notify when a round starts or ends. This is done by invoking the following methods: 

```javascript
QTalkApp.notifyGameRoundStarted()
```

```javascript
QTalkApp.notifyGameRoundEnded()
```
Both the methods will show a toast as an acknowledgement.

## Game Prompts:
Game prompts are dynamic components sent by the game developer based on states, consider this example:

Example: If a game having two players A & B has a state where the developer can identify that the player A is losing, a winning prompt can be generated and sent to the device on the
B player's end. This prompt will then be shown as a message by the underlying QTalk Application, to show these prompts, call:
```javascript
QTalkApp.updateGamePrompts(JSON.stringify(promptsToSend))
```
Here `promptsToSend` is an array of strings to be sent to the QTalk application
 
#### Clearing game prompts:
If the game ever reaches a state where an older game prompt needs to cleared call the interface method for clearing the game prompts as following:
`QTalkApp.clearGamePrompts()`

## Top level Id support: 
Every game URL should have a top level game-id. This id will be the call id in case the game is called from QTalk and will be appended by the application. For testing and debugging purposes, this id should be appended into the final game url as a param to avoid unforseen scenarios.
The param key to this id is: `id`. Thus making the url have the following format: `https://<game-url>/?id=<game_id>`.

The uniqueness of this id is guaranteed when called from the QTalk application and can be used by the game developer as a unique key to store game level information. 

## Testing steps

1. To test with the underlying `test.html` file, select the "test-url" option in the input dialog or type "test-url" when prompted for a URL. [Link](https://github.com/quiph/QGamesJSInterfaceTester/blob/master/app/src/main/assets/test.html)
 to the test HTML file
2. To test with an actual website put the link of the website as it is. The logs in `console.log` are visible in ADB logs as well as the log view in the app.

## Testing with 'test users'

1. The QTalk backend has provision for 3 'test users', their tokens are present in the application. 
2. Use the value of these tokens to hit the staging server with the request URL mentioned above, just append the url param with `isTestUser=true`, final url should look something like this, 
"https://staging.remote.qtalk.io/utilities/v1/verifyAuthIdToken?isTestUser=true"
3. This will evaluate the current token as a test user and will return the the corresponding user details as well. (QTalk Test 1, QTalk Test 2 etc..)
4. Note that this will **only** work with the **Staging** server and will return an HTTP **UNAUTHORIZED** if used with the production server and will fail if the token used
is not of a test user. 

## Test only features

There are some components which are available only during testing, the main QTalk App won't have these: 

1. **Using console.log to debug in the WebView:**
The app has a log view enabled which can be expanded or collapsed and will show all the messages printed via 
```javascript
console.log("message")
```

2. **Clearing the WebView cache**: This clears the underlying WebViews cache as well as disk cache if any found.


### Download the APK: 

Check the releases section [here](https://github.com/quiph/QGamesJSInterfaceTester/releases) for the latest apk. 

Note that this is a debug apk and logs should be visible perfectly fine. 
