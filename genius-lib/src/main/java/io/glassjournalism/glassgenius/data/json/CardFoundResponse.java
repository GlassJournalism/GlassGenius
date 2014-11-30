package io.glassjournalism.glassgenius.data.json;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class CardFoundResponse {

    @Expose
    private String id;
    @Expose
    private List<String> triggers = new ArrayList<String>();

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

    /**
     *
     * @return
     * The triggers
     */
    public List<String> getTriggers() {
        return triggers;
    }

    /**
     *
     * @param triggers
     * The triggers
     */
    public void setTriggers(List<String> triggers) {
        this.triggers = triggers;
    }

}