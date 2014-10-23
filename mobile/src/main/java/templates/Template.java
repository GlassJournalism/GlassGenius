package templates;

import android.graphics.drawable.Drawable;
import android.provider.MediaStore;

/**
 * Created by ian on 10/21/14.
 */

public interface Template {
    public boolean hasText();
    public int getNumTextAttributes();
    public String getTextAttributeName(int i);
    public void setTextAttribute(int i, String s);

    public boolean hasImage();
    public int getNumImageAttributes();
    public String getImageAttributeName(int i);
    public void setImageAttributeUrl(int i, String s);
    public void setImageAttributeUrl(int i, Drawable d);

    public boolean hasVideo();
    public int getNumVideoAttributes();
    public String getVideoAttributeName(int i);
    public void setVideoAttributeUrl(int i, String s);
    public void setVideoAttributeUrl(int i, MediaStore.Video v);

    public String generateHtml();
}
