package com.pharmacy.supplierinventory.exception;

import feign.Response;
import feign.codec.ErrorDecoder;
import feign.FeignException;

public class CustomErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        // Use errorStatus to create FeignException
        return FeignException.errorStatus(methodKey, response);
    }
}
