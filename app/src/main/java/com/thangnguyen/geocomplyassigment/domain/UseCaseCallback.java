package com.thangnguyen.geocomplyassigment.domain;

public interface UseCaseCallback<Response> {
    /**
     * Called when a use case successfully executes.
     * @param response Contains a response from the use case
     */
    void onSuccess(Response response);

    /**
     * Called when a use case fails to execute.
     * @param ex Contains error information
     */
    void onFailure(Exception ex);
}