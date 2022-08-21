package com.thangnguyen.geocomplyassigment.domain;


import java.util.concurrent.ExecutorService;

import javax.inject.Inject;

public final class UseCaseScheduler {

    /* Stores reference to thread-pool executor that's lazy loaded */
    private ExecutorService executor;


    @Inject
    public UseCaseScheduler(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * Prepares a given use case and then executes it on a new Thread.
     *
     * @param useCase  {@link UseCase}
     * @param request  Request data
     * @param callback {@link UseCaseCallback}
     * @param <T>      Request data type
     * @param <V>      Response data type
     */
    public <T, V> void execute(final UseCase<T, V> useCase, T request, UseCaseCallback<V> callback) {
        useCase.setCallback(callback);
        useCase.setRequest(request);
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    useCase.onExecute();
                } catch (Exception e) {
                    callback.onFailure(e);
                }

            }
        });
    }

}
