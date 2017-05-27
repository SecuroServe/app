package com.edu.securoserve.securoserve.requests;

import com.edu.securoserve.securoserve.rest.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import interfaces.ConfirmationMessage;
import interfaces.ICalamity;

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
        throw new UnsupportedOperationException();
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
}
