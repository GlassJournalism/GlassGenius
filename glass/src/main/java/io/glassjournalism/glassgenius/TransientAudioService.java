package io.glassjournalism.glassgenius;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import java.util.List;

public class TransientAudioService extends Service implements RecognitionListener {

    private final String TAG = getClass().getName();
    private final IBinder mBinder = new TransientAudioBinder();
    private SpeechRecognizer mSpeechRecognizer;

    @Override
    public void onCreate() {
        super.onCreate();
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSpeechRecognizer.setRecognitionListener(null);
        mSpeechRecognizer.stopListening();
        mSpeechRecognizer.cancel();
        mSpeechRecognizer.destroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Intent recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mSpeechRecognizer.startListening(recognizerIntent);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.d(TAG, "onReadyForSpeech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.d(TAG, "onBeginningOfSpeech");
    }

    @Override
    public void onRmsChanged(float v) {

    }

    @Override
    public void onBufferReceived(byte[] bytes) {

    }

    @Override
    public void onEndOfSpeech() {
        Log.d(TAG, "onEndOfSpeech");
    }

    @Override
    public void onError(int code) {
        switch (code) {
            case SpeechRecognizer.ERROR_AUDIO:
                Log.d(TAG, "ERROR_AUDIO");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                Log.d(TAG, "ERROR_CLIENT");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                Log.d(TAG, "ERROR_INSUFFICIENT_PERMISSIONS");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                Log.d(TAG, "ERROR_NETWORK");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                Log.d(TAG, "ERROR_NETWORK_TIMEOUT");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                Log.d(TAG, "ERROR_NO_MATCH");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                Log.d(TAG, "ERROR_RECOGNIZER_BUSY");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                Log.d(TAG, "ERROR_SERVER");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                Log.d(TAG, "ERROR_SPEECH_TIMEOUT");
                break;
        }
    }

    @Override
    public void onResults(Bundle bundle) {
        List<String> stringList = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Log.d(TAG, "onResults: " + stringList);
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        List<String> stringList = bundle.getStringArrayList(RecognizerIntent.EXTRA_PARTIAL_RESULTS);
        Log.d(TAG, "onPartialResults" + stringList);
    }

    @Override
    public void onEvent(int i, Bundle bundle) {

    }

    public class TransientAudioBinder extends Binder {
        public TransientAudioService getService() {
            return TransientAudioService.this;
        }
    }
}
