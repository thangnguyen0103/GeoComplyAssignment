package com.thangnguyen.geocomplyassigment.domain;

import androidx.annotation.WorkerThread;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public abstract class UseCase<Request, Response> {
    private UseCaseCallback<Response> callback;
    private Request request;
    private ExecutorService executor;

    public UseCase(ExecutorService executor) {
        this.executor = executor;
    }


    @WorkerThread
    protected abstract void onExecute();

    /**
     * Passes the use case by logging it and invoking the appropriate callback
     * with the response data from the successful execution.
     *
     * @param response Response data
     */
    protected final void pass(final Response response) {
        this.callback.onSuccess(response);
    }

    /**
     * Fails the use case by logging it and invoking the appropriate callback
     * containing information about by it fail execution.
     *
     * @param ex {@link Exception}
     */
    protected final void fail(final Exception ex) {
        this.callback.onFailure(ex);
    }

    public Request getRequest() {
        return request;
    }

    void setRequest(final Request request) {
        this.request = request;
    }

    void setCallback(final UseCaseCallback<Response> callback) {
        this.callback = callback;
    }

    public ExecutorService getExecutor() {
        return executor;
    }
}