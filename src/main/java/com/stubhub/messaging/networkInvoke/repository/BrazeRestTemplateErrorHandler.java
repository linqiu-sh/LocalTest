package com.stubhub.messaging.networkInvoke.repository;

import com.stubhub.messaging.networkInvoke.exception.BrazeClientException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BrazeRestTemplateErrorHandler implements ResponseErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(BrazeRestTemplateErrorHandler.class);
    private ResponseErrorHandler errorHandler = new DefaultResponseErrorHandler();

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return errorHandler.hasError(response);
    }

    @Override
    public void handleError(ClientHttpResponse response) throws IOException {
        HttpStatus statusCode = HttpStatus.resolve(response.getRawStatusCode());
        try {
            errorHandler.handleError(response);
        } catch (RestClientException scx) {
            throw new BrazeClientException(scx, convertStreamToString(response.getBody()), scx.getMessage(), statusCode);
        }

    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            logger.error("Exception in BufferedReader.readLine() ,exception={}", e.toString());
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                logger.error("Exception in InputStream.close() ,exception={}", e.toString());
            }
        }
        return sb.toString();
    }
}
