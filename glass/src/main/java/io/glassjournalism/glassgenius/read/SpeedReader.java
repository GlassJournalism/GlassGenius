package io.glassjournalism.glassgenius.read;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;

import io.glassjournalism.glassgenius.R;


public class SpeedReader extends Activity {

    TextView flash_me;
    int WPM = 500;
    int index = 0;
    String[] words;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.read_menu, menu);
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
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
            case R.id.view_source:
                String url = getIntent().getStringExtra(ReadActivity.SOURCE_URL);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
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
                index++;
                handle.postDelayed(timer, 2000);
            } else if (index < words.length) {
                flash_me.setText(words[index]);
                index++;
                handle.postDelayed(timer, 60000 / WPM);
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