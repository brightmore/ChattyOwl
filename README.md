# ChattyOwl

A very simple Android app for sending voice commands to your server.

Push the mic button to speak (e.g "Lights on"), when done [SpeechRecognizer](http://developer.android.com/reference/android/speech/SpeechRecognizer.html) will extract your command ("lights on") and POST it to specified endpoint.

Very basic PHP endpoint for proxying commands to Raspberry Pi (running Steven Hickson's [PiAUISuite](https://github.com/StevenHickson/PiAUISuite)) is available [in this Gist](https://gist.github.com/znupy/e83cfc7dfb85875fa047).

### Setting the endpoint

Long press mic button to open settings screen.