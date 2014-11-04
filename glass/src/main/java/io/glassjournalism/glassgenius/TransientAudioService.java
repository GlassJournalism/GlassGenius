package io.glassjournalism.glassgenius;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import io.glassjournalism.glassgenius.data.json.GeniusCard;
import io.glassjournalism.glassgenius.data.json.GeniusCardListener;
import io.glassjournalism.glassgenius.data.json.GlassGeniusAPI;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TransientAudioService extends Service implements RecognitionListener {

    private final String TAG = getClass().getName();
    private final IBinder mBinder = new TransientAudioBinder();
    private SpeechRecognizer mSpeechRecognizer;
    private Intent mRecognizerIntent;
    private CountDownTimer mTimer;
    private GlassGeniusAPI glassGeniusAPI;
    private GeniusCardListener mGeniusCardListener;

    public void setCardListener(GeniusCardListener listener) {
        mGeniusCardListener = listener;
    }

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(this);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint("http://glacial-ridge-6503.herokuapp.com").build();
        glassGeniusAPI = restAdapter.create(GlassGeniusAPI.class);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
        mSpeechRecognizer.setRecognitionListener(null);
        mSpeechRecognizer.stopListening();
        mSpeechRecognizer.cancel();
        mSpeechRecognizer.destroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        mRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
        mRecognizerIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
                this.getPackageName());
        mSpeechRecognizer.startListening(mRecognizerIntent);
        return mBinder;
    }

    @Override
    public void onReadyForSpeech(Bundle bundle) {
        Log.d(TAG, "onReadyForSpeech");
        if (mTimer != null) {
            mTimer.cancel();
        }
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
        restartSpeech();
    }

    @Override
    public void onResults(Bundle bundle) {
        if (mTimer != null) {
            mTimer.cancel();
        }

        List<String> wordList = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        Log.i(TAG, "onResults: " + wordList);
        String words = TextUtils.join(",", wordList);
        glassGeniusAPI.findCard(words, new Callback<GeniusCard>() {
            @Override
            public void success(GeniusCard geniusCard, Response response) {
                if (null != mGeniusCardListener) {
                    mGeniusCardListener.onCardFound(geniusCard);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "findCard failure");
            }
        });

        restartSpeech();
    }

    private void restartSpeech() {
        mSpeechRecognizer.startListening(mRecognizerIntent);

        if (mTimer == null) {
            mTimer = new CountDownTimer(2000, 500) {
                @Override
                public void onTick(long l) {
                }

                @Override
                public void onFinish() {
                    mSpeechRecognizer.cancel();
                    mSpeechRecognizer.startListening(mRecognizerIntent);
                }
            };
        }
        mTimer.start();
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        List<String> stringList = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (stringList.size() > 0) {
            Log.i(TAG, "onPartialResults" + stringList);
        }
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
