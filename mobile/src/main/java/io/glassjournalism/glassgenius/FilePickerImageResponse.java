package io.glassjournalism.glassgenius;

import com.google.gson.annotations.Expose;

public class FilePickerImageResponse {

    @Expose
    private String url;
    @Expose
    private Integer size;
    @Expose
    private String type;
    @Expose
    private String filename;
    @Expose
    private String key;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}