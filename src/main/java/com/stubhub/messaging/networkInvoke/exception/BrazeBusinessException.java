package com.stubhub.messaging.networkInvoke.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrazeBusinessException extends Exception {

    private String message;

}
