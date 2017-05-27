package com.edu.securoserve.securoserve.requests;

import com.edu.securoserve.securoserve.rest.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import interfaces.ConfirmationMessage;
import interfaces.IUser;

import static com.edu.securoserve.securoserve.requests.RequestUtils.REQUEST_PREFIX;

/**
 * Created by guillaimejanssen on 22/05/2017.
 */

public class UserRequest implements IUser {

    private RestClient restClient;
    private ObjectMapper objectMapper;

    public UserRequest() {
        this.restClient = new RestClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ConfirmationMessage allusers(String token) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConfirmationMessage getUser(String userToken) {
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("usertoken", userToken);

        Object value = restClient.request(RequestUtils.REQUEST_PREFIX + RequestUtils.GET_USER, RestClient.RequestType.GET, parameters);
        return objectMapper.convertValue(value, ConfirmationMessage.class);
    }

    @Override
    public ConfirmationMessage addUser(int userTypeId, int buildingId, String username, String password, String email, String city, String token) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConfirmationMessage updateUser(String token, int id, String username, String password, String email, String city) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConfirmationMessage deleteUser(String token, int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConfirmationMessage giveUserToken(String userToken, String firebaseToken) {
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();
        parameters.add("usertoken", userToken);
        parameters.add("firebasetoken", firebaseToken);

        Object value = restClient.request(RequestUtils.REQUEST_PREFIX + RequestUtils.SET_FIREBASE_TOKEN, RestClient.RequestType.GET, parameters);
        return objectMapper.convertValue(value, ConfirmationMessage.class);
    }
}
