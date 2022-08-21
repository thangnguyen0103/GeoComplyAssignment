package com.thangnguyen.geocomplyassigment.presentation;

import androidx.lifecycle.ViewModel;

import com.thangnguyen.geocomplyassigment.domain.UseCaseScheduler;

public abstract class BaseViewModel extends ViewModel {

    private UseCaseScheduler scheduler;

    public BaseViewModel(UseCaseScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public UseCaseScheduler getScheduler() {
        return scheduler;
    }
}
