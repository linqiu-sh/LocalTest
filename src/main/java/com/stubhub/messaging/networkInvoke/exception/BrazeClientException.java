package com.stubhub.messaging.networkInvoke.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClientException;

@Data
public class BrazeClientException extends RestClientException{

    private RestClientException restClientException;
    private String body;
    private String message;
    private HttpStatus statusCode;

    public BrazeClientException(RestClientException restClientException, String body, String message, HttpStatus statusCode) {
        super(message);
        this.restClientException = restClientException;
        this.body = body;
        this.message = message;
        this.statusCode = statusCode;
    }
}
