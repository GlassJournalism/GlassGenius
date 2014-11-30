package io.glassjournalism.glassgenius.data.json;

import com.google.gson.annotations.Expose;

public class CardFieldResponse {

    @Expose
    private String id;

    /**
     *
     * @return
     * The id
     */
    public String getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(String id) {
        this.id = id;
    }

}