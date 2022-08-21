package com.thangnguyen.geocomplyassigment.presentation.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.thangnguyen.geocomplyassigment.domain.UseCaseCallback;
import com.thangnguyen.geocomplyassigment.domain.UseCaseScheduler;
import com.thangnguyen.geocomplyassigment.domain.usecase.ParseDataUseCase;
import com.thangnguyen.geocomplyassigment.presentation.BaseViewModel;


import org.json.JSONObject;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MainViewModel extends BaseViewModel {

    private final ParseDataUseCase parseDataUseCase;
    private MutableLiveData<String> content = new MutableLiveData<>();
    private MutableLiveData<Exception> error = new MutableLiveData<>();


    @Inject
    public MainViewModel(UseCaseScheduler scheduler, ParseDataUseCase parseDataUseCase) {
        super(scheduler);
        this.parseDataUseCase = parseDataUseCase;
    }

    public LiveData<String> getContent() {
        return content;
    }

    public LiveData<Exception> getError() {
        return error;
    }

    public void submitInput(String input) {
        getScheduler().execute(parseDataUseCase, input, new UseCaseCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject jsonObject) {
                content.postValue(jsonObject.toString());
            }

            @Override
            public void onFailure(Exception ex) {
                content.postValue("");
                error.postValue(ex);
            }
        });
    }
}
