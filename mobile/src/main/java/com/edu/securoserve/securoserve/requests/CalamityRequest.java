package com.edu.securoserve.securoserve.requests;

import com.edu.securoserve.securoserve.rest.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import interfaces.ConfirmationMessage;
import interfaces.ICalamity;
import library.Plan;

/**
 * Created by guillaimejanssen on 22/05/2017.
 */

public class CalamityRequest implements ICalamity{

    RestClient restClient = new RestClient();

    public CalamityRequest() {
        this.restClient = new RestClient();
    }

    @Override
    public ConfirmationMessage allCalamity() {
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();

        Object value = restClient.request(RequestUtils.REQUEST_PREFIX + RequestUtils.ALL_CALAMITY, RestClient.RequestType.GET, parameters);
        ObjectMapper mapper = new ObjectMapper();

        return mapper.convertValue(value, ConfirmationMessage.class);
    }

    @Override
    public ConfirmationMessage calamityById(int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConfirmationMessage addCalamity(String token, String title, String message, double latitude, double longitude, double radius, boolean confirmed, boolean closed) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConfirmationMessage updateCalamity(String token, int id, String name, String message, int locId, double latitude, double longitude, double radius, boolean confirmed, boolean closed) {
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("token", token);
        parameters.add("id", id);
        parameters.add("title", name);
        parameters.add("message", message);
        parameters.add("locationid", locId);
        parameters.add("latitude", latitude);
        parameters.add("longitude", longitude);
        parameters.add("radius", radius);
        parameters.add("confirmed", confirmed);
        parameters.add("closed", closed);

        Object value = restClient.request(RequestUtils.REQUEST_PREFIX + RequestUtils.UPDATE_CALAMITY, RestClient.RequestType.GET, parameters);
        ObjectMapper mapper = new ObjectMapper();

        return  mapper.convertValue(value, ConfirmationMessage.class);
    }

    @Override
    public ConfirmationMessage deleteCalamity(String token, int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConfirmationMessage addCalamityAssignee(String token, int calamityId, int userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConfirmationMessage deleteCalamityAssignee(String token, int calamityId, int userId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConfirmationMessage addPost(String token, int userId, int calamityId, String text) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConfirmationMessage addPlan(String token, int calamityId, Plan plan) {
        throw new UnsupportedOperationException();
    }
}
