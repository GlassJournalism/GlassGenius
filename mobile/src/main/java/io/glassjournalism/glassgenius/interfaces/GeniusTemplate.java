package io.glassjournalism.glassgenius.interfaces;

import android.webkit.WebView;

import java.util.List;
import java.util.Map;

/**
 * Created by ian on 11/10/14.
 */
public interface GeniusTemplate {
    public void update(Map<String, String> map, WebView webView);

    public List<String> getTextFields();
    public List<String> getImageFields();
}
