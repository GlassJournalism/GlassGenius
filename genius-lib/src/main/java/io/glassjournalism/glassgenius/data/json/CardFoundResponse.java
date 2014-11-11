package io.glassjournalism.glassgenius.data.json;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;

@Generated("org.jsonschema2pojo")
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