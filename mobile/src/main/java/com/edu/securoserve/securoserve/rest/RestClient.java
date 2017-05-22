package com.edu.securoserve.securoserve.rest;

import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import interfaces.ConfirmationMessage;

/**
 * Created by guillaimejanssen on 22/05/2017.
 */

public class RestClient {

    private RestTemplate rest;

    /**
     * Constructor
     */
    public RestClient() {
        rest = new RestTemplate();
        rest.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

    }

    /**
     * Creates a new REST request.
     *
     * @param baseUrl     The request URL (including URL parameters)
     * @param requestType The request type (GET, POST, PUT, DELETE)
     * @param parameters  A MultiValueMap containing parameters
     * @return ConfirmationMessage
     */
    public ConfirmationMessage request(String baseUrl, RequestType requestType, MultiValueMap<String, Object> parameters) {
        switch (requestType) {
            case POST:
                return post(baseUrl, parameters);

            case GET:
                return get(generateUrl(baseUrl, parameters));

            case PUT:
                throw new UnsupportedOperationException();

            case DELETE:
                delete(baseUrl, parameters);
                return new ConfirmationMessage(ConfirmationMessage.StatusType.SUCCES, "Deleted object", null);

            default:
                return new ConfirmationMessage(ConfirmationMessage.StatusType.ERROR, "Request error.", null);
        }
    }

    /**
     * Adds parameters from a MultiValueMap onto a URL.
     *
     * @param baseUrl    The request URL
     * @param parameters A MultiValueMap containing parameters
     * @return ConfirmationMessage
     */
    public String generateUrl(String baseUrl, MultiValueMap<String, Object> parameters) {

        StringBuilder sb = new StringBuilder();
        sb.append(baseUrl).append("?");

        for (MultiValueMap.Entry<String, List<Object>> entry : parameters.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue().get(0)).append("&");
        }

        sb.deleteCharAt(sb.length() - 1);

        return sb.toString();
    }

    /**
     * Executes a GET request using the Spring REST library.
     *
     * @param uri The request URL
     * @return ConfirmationMessage
     */
    private ConfirmationMessage get(String uri) {
        return rest.getForObject(uri, ConfirmationMessage.class);
    }

    /**
     * Executes a POST request using the Spring REST library.
     *
     * @param uri        The request URL
     * @param parameters A MultiValueMap containing parameters.
     * @return ConfirmationMessage
     */
    private ConfirmationMessage post(String uri, MultiValueMap<String, Object> parameters) {
        return rest.postForObject(uri, parameters, ConfirmationMessage.class);
    }

    /**
     * Executes a DELETE request using the Spring REST library.
     * @param uri           The request URL
     * @param parameters    A MultiValueMap containing parameters.
     */
    private void delete(String uri, MultiValueMap<String, Object> parameters) {
        rest.delete(uri, parameters);
    }

    /**
     * An enum with request types
     */
    public enum RequestType {
        POST,
        GET,
        PUT,
        DELETE
    }
}
