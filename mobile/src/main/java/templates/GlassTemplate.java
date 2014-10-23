package templates;

import android.graphics.drawable.Drawable;
import android.provider.MediaStore;

/**
 * Created by ian on 10/21/14.
 */
public class GlassTemplate implements Template {
    String raw;
    String[] elements;

    public GlassTemplate(String s) {
        this.raw = s;
        elements = raw.split("/{([^{}]+)}/g");
    }

    @Override
    public boolean hasText() {
        return false;
    }

    @Override
    public int getNumTextAttributes() {
        return 0;
    }

    @Override
    public String getTextAttributeName(int i) {
        return null;
    }

    @Override
    public void setTextAttribute(int i, String s) {

    }

    @Override
    public boolean hasImage() {
        return false;
    }

    @Override
    public int getNumImageAttributes() {
        return 0;
    }

    @Override
    public String getImageAttributeName(int i) {
        return null;
    }

    @Override
    public void setImageAttributeUrl(int i, String s) {

    }

    @Override
    public void setImageAttributeUrl(int i, Drawable d) {

    }

    @Override
    public boolean hasVideo() {
        return false;
    }

    @Override
    public int getNumVideoAttributes() {
        return 0;
    }

    @Override
    public String getVideoAttributeName(int i) {
        return null;
    }

    @Override
    public void setVideoAttributeUrl(int i, String s) {

    }

    @Override
    public void setVideoAttributeUrl(int i, MediaStore.Video v) {

    }

    @Override
    public String generateHtml() {
        return null;
    }
}
