package com.edu.securoserve.securoserve;

import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by guillaimejanssen on 22/05/2017.
 */
public class SessionData {

    public static final String CURRENT_USER = "loggedInUser";
    public static final String CALAMITY_LIST = "calamityList";
    public static final String FIREBASE_TOKEN = "firebaseToken";

    private HashMap<String, Object> sessionValues = new HashMap<>();
    private static SessionData instance = null;

    protected SessionData() {

    }

    public static SessionData getInstance() {
        if(instance == null) {
            instance = new SessionData();
        }
        return instance;
    }

    public void addValues(HashMap<String, Object> values) {
        this.sessionValues.putAll(values);
    }

    public void addValue(String key, Object object) {
        this.sessionValues.put(key, object);
    }

    public HashMap<String, Object> getValues(Set<String> keys) {
        HashMap<String, Object> values = new HashMap<>();
        for (String k : keys) {
            values.put(k, this.sessionValues);
        }
        return values;
    }

    public Object getValue(String key) {
        return this.sessionValues.get(key);
    }

    public void clearValues() {
        this.sessionValues.clear();
    }

    public void clearValue(String key) {
        this.sessionValues.remove(key);
    }
}
