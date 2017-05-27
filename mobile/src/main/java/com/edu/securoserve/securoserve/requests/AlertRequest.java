package com.edu.securoserve.securoserve.requests;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.edu.securoserve.securoserve.rest.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import interfaces.ConfirmationMessage;
import interfaces.IAlert;
import library.Location;

/**
 * Created by guillaimejanssen on 26/05/2017.
 */
public class AlertRequest implements IAlert {

    private final RestClient restClient;

    public AlertRequest() {
        this.restClient = new RestClient();
    }

    @Override
    public ConfirmationMessage getAllAlerts(String token) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConfirmationMessage getAlert(String token, int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConfirmationMessage addAlert(String token, String name, String description, int urgency, double lat, double lon, double radius) {
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();

        parameters.add("token", token);
        parameters.add("name", name);
        parameters.add("description", description);
        parameters.add("urgency", urgency);
        parameters.add("latitude", lat);
        parameters.add("longitude", lon);
        parameters.add("radius", radius);

        try{
            Object value = this.restClient.request(RequestUtils.REQUEST_PREFIX + RequestUtils.ADD_ALERT, RestClient.RequestType.GET, parameters);
            ObjectMapper mapper = new ObjectMapper();

            return mapper.convertValue(value, ConfirmationMessage.class);
        } catch (Exception ex) {
            System.out.println(ex);
        }

        return null;
    }

    @Override
    public ConfirmationMessage updateAlert(String token, int id, String name, String description, int urgency, double lat, double lon, double radius) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConfirmationMessage removeAlert(String token, int id) {
        throw new UnsupportedOperationException();
    }
}
