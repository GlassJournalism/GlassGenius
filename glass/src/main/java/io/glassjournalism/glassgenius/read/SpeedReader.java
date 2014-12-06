package io.glassjournalism.glassgenius.read;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.glass.media.Sounds;

import io.glassjournalism.glassgenius.R;


public class SpeedReader extends Activity {

    TextView flash_me;
    int index = 0;
    String[] words;
    private AudioManager audio;
    SharedPreferences sharedPrefs;
    private boolean paused = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.read_menu, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            paused = true;
            audio.playSoundEffect(Sounds.TAP);
            openOptionsMenu();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection. Menu items typically start another
        // activity, start a service, or broadcast another intent.
        switch (item.getItemId()) {
            case R.id.restart:
                index = 0;
                paused = false;
                return true;
            case R.id.view_source:
                String url = getIntent().getStringExtra(ReadActivity.SOURCE_URL);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                paused = false;
                return true;
            case R.id.speed_250:
                sharedPrefs.edit().putInt("WPM", 250).apply();
                paused = false;
                return true;
            case R.id.speed_500:
                sharedPrefs.edit().putInt("WPM", 500).apply();
                paused = false;
                return true;
            case R.id.speed_750:
                sharedPrefs.edit().putInt("WPM", 700).apply();
                paused = false;
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        Intent intent = getIntent();
        String toSplit = intent.getStringExtra(ReadActivity.EXTRA_MESSAGE);
        words = toSplit.split("[,\\s]+");

        setContentView(R.layout.activity_speed_reader);
        flash_me = (TextView) findViewById(R.id.flash);
    }

    private Handler handle = new Handler();
    private Runnable timer = new Runnable() {
        @Override
        public void run() {
            if (index == 0) {
                flash_me.setText(words[index]);
                if (!paused) index++;
                handle.postDelayed(timer, 2000);
            } else if (index < words.length) {
                flash_me.setText(words[index]);
                if (!paused) index++;
                handle.postDelayed(timer, 60000 / sharedPrefs.getInt("WPM", 500));
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        handle.post(timer);
    }

    @Override
    public void onPause() {
        super.onPause();
        handle.removeCallbacks(timer);
    }
}