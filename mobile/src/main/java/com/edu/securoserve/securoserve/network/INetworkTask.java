package com.edu.securoserve.securoserve.network;

import android.content.Context;
import android.os.AsyncTask;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by guillaimejanssen on 19/06/2017.
 */

public interface INetworkTask {

    void sendAlert(String title, String message, String buttonText);
}
