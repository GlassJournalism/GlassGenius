package io.glassjournalism.glassgenius.json;

import com.google.gson.annotations.Expose;

public class Template {

    @Expose
    private String name;
    @Expose
    private String handlebarsTemplate;
    @Expose
    private String createdAt;
    @Expose
    private String updatedAt;
    @Expose
    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Template withName(String name) {
        this.name = name;
        return this;
    }

    public String getHandlebarsTemplate() {
        return handlebarsTemplate;
    }

    public void setHandlebarsTemplate(String handlebarsTemplate) {
        this.handlebarsTemplate = handlebarsTemplate;
    }

    public Template withHandlebarsTemplate(String handlebarsTemplate) {
        this.handlebarsTemplate = handlebarsTemplate;
        return this;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Template withCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Template withUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Template withId(String id) {
        this.id = id;
        return this;
    }
}