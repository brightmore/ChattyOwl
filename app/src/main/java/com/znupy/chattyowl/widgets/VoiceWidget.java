package com.znupy.chattyowl.widgets;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.znupy.chattyowl.R;
import com.znupy.chattyowl.activities.SettingsActivity;
import com.znupy.chattyowl.network.CommandCenter;
import com.znupy.chattyowl.ui.SoundIndicator;

import java.util.List;

public class VoiceWidget extends FrameLayout implements RecognitionListener {
    private static final String TAG = VoiceWidget.class.getSimpleName();

    private SpeechRecognizer speechRecognizer;
    private CommandCenter commandCenter;

    private TextView resultsText;
    private ImageButton listenButton;
    private SoundIndicator soundIndicator;


    public VoiceWidget(Context context) {
        super(context);
        build(context);
    }

    public VoiceWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        build(context);
    }

    private void build(Context context) {
        addView(View.inflate(context, R.layout.widget_voice, null));

        if(isInEditMode()) return;

        resultsText = (TextView)findViewById(R.id.voice_results);
        listenButton = (ImageButton)findViewById(R.id.button);
        soundIndicator = (SoundIndicator)findViewById(R.id.sound_indicator);

        listenButton.setOnClickListener(listenClickListener);
        listenButton.setOnLongClickListener(listenLongClickListener);

        commandCenter = new CommandCenter(context);
        prepareSpeechRecognizer();
    }


    private OnClickListener listenClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.znupy");

            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
            speechRecognizer.startListening(intent);
        }
    };

    private OnLongClickListener listenLongClickListener = new OnLongClickListener() {

        @Override
        public boolean onLongClick(View v) {
            getContext().startActivity(new Intent(getContext(), SettingsActivity.class));
            return false;
        }
    };

    private void prepareSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
        speechRecognizer.setRecognitionListener(this);
    }

    private void sendCommand(String command) {
        resultsText.setText(String.format(getContext().getString(R.string.sending_command), command));

        commandCenter.post(command, new CommandCenter.ResponseListener() {
            @Override
            public void onSuccess() {
                resultsText.setText(getContext().getString(R.string.response_command_executed));
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                resultsText.setText(String.format(getContext().getString(R.string.response_error), message));
            }
        });
    }

    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG, "onReadyForSpeech");
        resultsText.setText(getContext().getString(R.string.results_speak_now));
        listenButton.setImageResource(R.drawable.button_listening);
    }

    public void onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech");
        resultsText.setText("...");
    }

    public void onRmsChanged(float rmsdB) {
        Log.d(TAG, "onRmsChanged " + rmsdB);
        soundIndicator.setSoundLevel(rmsdB);
    }

    public void onBufferReceived(byte[] buffer) {
        Log.d(TAG, "onBufferReceived");
    }

    public void onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech");
    }

    public void onError(int error) {
        Log.d(TAG, "onError " + error);
        resultsText.setText(getContext().getString(R.string.results_tap_mic_retry));
        listenButton.setImageResource(R.drawable.button_listen);
    }

    public void onResults(Bundle results) {
        Log.d(TAG, "onResults " + results);

        List<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if(data.size() > 0) {
            String command = data.get(0);
            Log.d(TAG, "onResults: " + command);
            resultsText.setText(command);
            sendCommand(command);
        }

        listenButton.setImageResource(R.drawable.button_listen);
    }

    public void onPartialResults(Bundle partialResults)  {
        Log.d(TAG, "onPartialResults");
        List<String> data = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if(data.size() > 0) {
            Log.d(TAG, "onPartialResults: " + data.get(0));
        }

    }

    public void onEvent(int eventType, Bundle params) {
        Log.d(TAG, "onEvent " + eventType);
    }

}
