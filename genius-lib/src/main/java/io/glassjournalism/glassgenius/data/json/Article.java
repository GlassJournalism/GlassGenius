package io.glassjournalism.glassgenius.data.json;

import com.google.gson.annotations.Expose;

public class Article {

    @Expose
    private String title;
    @Expose
    private String contents;
    @Expose
    private String date;
    @Expose
    private String publication;
    @Expose
    private String author;
    @Expose
    private String thumbnailURL;
    @Expose
    private String iconURL;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String dates) {
        this.date = dates;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getIconURL() {
        return iconURL;
    }

    public void setIconURL(String iconURL) {
        this.iconURL = iconURL;
    }


    public String getPublication() {
        return publication;
    }

    public void setPublication(String publication) {
        this.publication = publication;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
