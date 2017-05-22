package com.edu.securoserve.securoserve.requests;

import com.edu.securoserve.securoserve.rest.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import interfaces.ConfirmationMessage;
import interfaces.ILogin;

/**
 * Created by guillaimejanssen on 22/05/2017.
 */
public class LoginRequest implements ILogin {

    RestClient restClient;

    public LoginRequest() {
        restClient = new RestClient();
    }

    @Override
    public ConfirmationMessage login(String username, String password) {
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();

        parameters.add("username", username);
        parameters.add("password", password);

        Object value = restClient.request(RequestUtils.REQUEST_PREFIX + "login", RestClient.RequestType.GET, parameters);

        ObjectMapper mapper = new ObjectMapper();
        return (ConfirmationMessage) mapper.convertValue(value, ConfirmationMessage.class);
    }
}
