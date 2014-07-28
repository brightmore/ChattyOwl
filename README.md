# ChattyOwl

A very simple Android app for listening for voice commands and sending them to your server. App can also display basic name/value properties returned by the server.

## Voice commands

Push the mic button to speak (e.g "Lights on"), when done [SpeechRecognizer](http://developer.android.com/reference/android/speech/SpeechRecognizer.html) will extract your command ("lights on") and POST it to specified endpoint.

Very basic PHP endpoint for proxying commands to Raspberry Pi (running Steven Hickson's [PiAUISuite](https://github.com/StevenHickson/PiAUISuite)) is available [in this Gist](https://gist.github.com/znupy/e83cfc7dfb85875fa047).

## Properties

App can also display name/value property pairs returned from the server. JSON format of (successfully returned) properties is:

```
{
    "success": "true",
    "data": [
        {"name": "Property 1", "value": "Value 1" },
        {"name": "Property 2", "value": "Value 2" }
    ]
}
```


## Settings

For accessing the settings screen, long press the mic button. On settings screen you can change endpoint for (voice) commands and endpoint for accessing name/value properties.
