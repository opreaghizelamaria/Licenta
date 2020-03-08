package com.example.opreaghizelamaria.licenta;

import org.json.JSONObject;

public class Message {
    private JSONObject object;
    private boolean belongsToCurrentUser;

    public Message( JSONObject data, boolean belongsToCurrentUser) {
        this.object = data;
        this.belongsToCurrentUser = belongsToCurrentUser;
    }



    public JSONObject getObject() {
        return object;
    }

    public boolean isBelongsToCurrentUser() {
        return belongsToCurrentUser;
    }
}
