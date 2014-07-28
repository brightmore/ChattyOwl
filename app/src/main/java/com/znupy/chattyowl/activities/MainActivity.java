package com.znupy.chattyowl.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.znupy.chattyowl.network.CommandCenter;
import com.znupy.chattyowl.R;
import com.znupy.chattyowl.ui.SoundIndicator;

import java.util.List;

/**
 * Created by samok on 27/07/14.
 */
public class MainActivity extends Activity implements RecognitionListener, View.OnLongClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private SpeechRecognizer speechRecognizer;
    private CommandCenter commandCenter;

    private TextView resultsText;
    private ImageButton listenButton;
    private SoundIndicator soundIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultsText = (TextView)findViewById(R.id.voice_results);
        listenButton = (ImageButton)findViewById(R.id.button);
        soundIndicator = (SoundIndicator)findViewById(R.id.sound_indicator);

        listenButton.setOnLongClickListener(this);

        commandCenter = new CommandCenter(this);
        prepareSpeechRecognizer();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onListenClicked(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "com.znupy");

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
        speechRecognizer.startListening(intent);
    }

    private void prepareSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(this);
    }

    private void sendCommand(String command) {
        resultsText.setText(String.format(getString(R.string.sending_command), command));

        commandCenter.post(command, new CommandCenter.ResponseListener() {
            @Override
            public void onSuccess() {
                resultsText.setText(getString(R.string.response_command_executed));
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getBaseContext(), message, Toast.LENGTH_SHORT).show();
                resultsText.setText(String.format(getString(R.string.response_error), message));
            }
        });
    }

    public void onReadyForSpeech(Bundle params) {
        Log.d(TAG, "onReadyForSpeech");
        resultsText.setText(getString(R.string.results_speak_now));
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
        resultsText.setText(getString(R.string.results_tap_mic_retry));
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

    @Override
    public boolean onLongClick(View v) {
        startActivity(new Intent(this, SettingsActivity.class));
        return false;
    }
}
