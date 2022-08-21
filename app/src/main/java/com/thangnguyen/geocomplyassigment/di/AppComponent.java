package com.thangnguyen.geocomplyassigment.di;


import com.thangnguyen.geocomplyassigment.data.repository.LinkRepositoryImpl;
import com.thangnguyen.geocomplyassigment.domain.repository.LinkRepository;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class AppComponent {

    @Provides
    public ExecutorService provideSchedule() {
        final int SIZE = 2;
        final int MAX = 4;
        final int TIMEOUT = 60;
        return new ThreadPoolExecutor(SIZE, MAX, TIMEOUT, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(MAX));
    }

    @Provides
    public LinkRepository providesLinkRepository() {
        return new LinkRepositoryImpl();
    }
}
