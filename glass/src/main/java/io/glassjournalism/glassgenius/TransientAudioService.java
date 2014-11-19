package io.glassjournalism.glassgenius;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import io.glassjournalism.glassgenius.data.json.CardFieldResponse;
import io.glassjournalism.glassgenius.data.json.CardFoundResponse;
import io.glassjournalism.glassgenius.data.json.Constants;
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
    private List<String> keyWordList = new ArrayList<String>();
    private Set<String> viewedCardIDs = new HashSet<String>();
    private Deque<String> imageURLs = new LinkedList<String>();
    private SharedPreferences sharedPrefs;

    public void setCardListener(GeniusCardListener listener) {
        mGeniusCardListener = listener;
    }

    @Override
    public void onCreate() {
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        mSpeechRecognizer.setRecognitionListener(this);
        RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(Constants.API_ROOT).build();
        glassGeniusAPI = restAdapter.create(GlassGeniusAPI.class);
        glassGeniusAPI.getTriggers(new Callback<JsonArray>() {
            @Override
            public void success(JsonArray triggerResponse, Response response) {
                for (JsonElement trigger : triggerResponse) {
                    if (trigger.getAsString().length() > 0) {
                        keyWordList.add(trigger.getAsString());
                    }
                }
                if (mGeniusCardListener != null) {
                    sharedPrefs.edit().putString("session", String.valueOf(System.currentTimeMillis())).apply();
                    mGeniusCardListener.onKeywordsLoaded();
                }
                Log.d(TAG, "loaded keywords: " + keyWordList.toString());
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d(TAG, "error fetching keyword list at start");
                if (mGeniusCardListener != null) {
                    mGeniusCardListener.onError("Check Network");
                }
                try {
                    Log.d(TAG, error.getUrl());
                    Log.d(TAG, error.getResponse().getReason());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        glassGeniusAPI.getAllCardIDs(new Callback<List<CardFieldResponse>>() {
            @Override
            public void success(List<CardFieldResponse> cardFieldResponses, Response response) {
                for (CardFieldResponse card : cardFieldResponses) {
                    String cardImageURL = Constants.API_ROOT + "/card/render/" + card.getId();
                    imageURLs.push(cardImageURL);
                }
                new ImageLoadTask().execute();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
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

    private void findCardsForWords(String words) {
        Log.d(TAG, "findCardsForWords " + words);
        for (String keyword : keyWordList) {
            if (words.toLowerCase().contains(keyword.toLowerCase())) {
                Log.d(TAG, "matched trigger: " + keyword + " from dict to " + words);
                glassGeniusAPI.findCard(getSessionID(), words, new Callback<List<CardFoundResponse>>() {
                    @Override
                    public void success(List<CardFoundResponse> cardFoundResponses, Response response) {
                        for (CardFoundResponse card : cardFoundResponses) {
                            if (!viewedCardIDs.contains(card.getId())) {
                                viewedCardIDs.add(card.getId());
                                mGeniusCardListener.onCardFound(card);
                                Log.d(TAG, "adding card with ID: " + card.getId());
                                break;
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.d(TAG, "findCard failure");
                    }
                });
            }
        }
    }

    @Override
    public void onPartialResults(Bundle bundle) {
        Log.d(TAG, "onParitialResults");
        List<String> wordList = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String words = TextUtils.join(",", wordList);
        findCardsForWords(words);
    }

    @Override
    public void onResults(Bundle bundle) {
        Log.d(TAG, "onResults");
        if (mTimer != null) {
            mTimer.cancel();
        }
        List<String> wordList = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String words = TextUtils.join(",", wordList);
        findCardsForWords(words);
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
    public void onEvent(int i, Bundle bundle) {

    }

    public class TransientAudioBinder extends Binder {
        public TransientAudioService getService() {
            return TransientAudioService.this;
        }
    }

    private class ImageLoadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (imageURLs.size() > 0) {
                String url = imageURLs.pop();
//                Log.d(TAG, "preloading: " + url);
                Ion.with(TransientAudioService.this).load(url).asBitmap().setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result) {
                        if (imageURLs.size() > 0) {
                            new ImageLoadTask().execute();
                        }
                    }
                });
            }
            return null;
        }
    }

    private String getSessionID() {
        String deviceId = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        String session = sharedPrefs.getString("session", "");
        return deviceId + session;
    }
}
