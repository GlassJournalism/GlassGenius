package io.glassjournalism.glassgenius.data.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Variables {

    @SerializedName("BackgroundImage")
    @Expose
    private String backgroundImage;
    @SerializedName("AvatarImage")
    @Expose
    private String avatarImage;
    @SerializedName("Title")
    @Expose
    private String title;
    @SerializedName("SubTitle")
    @Expose
    private String subTitle;
    @SerializedName("BodyText")
    @Expose
    private String bodyText;

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public Variables withBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
        return this;
    }

    public String getAvatarImage() {
        return avatarImage;
    }

    public void setAvatarImage(String avatarImage) {
        this.avatarImage = avatarImage;
    }

    public Variables withAvatarImage(String avatarImage) {
        this.avatarImage = avatarImage;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Variables withTitle(String title) {
        this.title = title;
        return this;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Variables withSubTitle(String subTitle) {
        this.subTitle = subTitle;
        return this;
    }

    public String getBodyText() {
        return bodyText;
    }

    public void setBodyText(String bodyText) {
        this.bodyText = bodyText;
    }

    public Variables withBodyText(String bodyText) {
        this.bodyText = bodyText;
        return this;
    }
}