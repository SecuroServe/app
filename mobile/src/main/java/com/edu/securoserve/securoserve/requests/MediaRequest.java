package com.edu.securoserve.securoserve.requests;

import com.edu.securoserve.securoserve.rest.RestClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import interfaces.ConfirmationMessage;
import interfaces.IMedia;
import library.Media;
import library.MediaFile;

/**
 * Created by guillaimejanssen on 19/06/2017.
 */

public class MediaRequest implements IMedia {

    private final RestClient restClient;

    public MediaRequest() {
        this.restClient = new RestClient();
    }

    @Override
    public ConfirmationMessage getMedia(String token, int id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConfirmationMessage addMedia(String token, Media media, int alertId) {
        throw new UnsupportedOperationException();

    }

    public ConfirmationMessage addMediaFile(String token, MediaFile media, int alertId) {
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();

        parameters.add("token", token);
        parameters.add("mediaName", media.getName());
        parameters.add("fileName", media.getFileName());
        parameters.add("fileType", media.getFileType().toString());
        parameters.add("alertId", alertId);

        Object value = null;
        try{
            value = this.restClient.request(RequestUtils.REQUEST_PREFIX + RequestUtils.ADD_MEDIA, RestClient.RequestType.GET, parameters);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(value, ConfirmationMessage.class);
    }

    @Override
    public ConfirmationMessage uploadMedia(String token, int mediaId, MultipartFile file) {
        MultiValueMap<String, Object> parameters = new LinkedMultiValueMap<>();

        parameters.add("token", token);
        parameters.add("mediaId", mediaId);
        parameters.add("file", file);

        Object value = this.restClient.request(RequestUtils.REQUEST_PREFIX + RequestUtils.UPLOAD_MEDIA, RestClient.RequestType.POST, parameters);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.convertValue(value, ConfirmationMessage.class);
    }

    @Override
    public ConfirmationMessage downloadMedia(String token, int mediaId) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConfirmationMessage updateMedia(String token, Media media) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ConfirmationMessage removeMedia(String token, int mediaId) {
        throw new UnsupportedOperationException();
    }
}
