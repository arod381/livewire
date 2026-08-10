package com.example.livewire.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.livewire.Repository.MainRepository;

public class MainViewModel extends ViewModel {

    private String prompt;

    private final MutableLiveData<String> response =
            new MutableLiveData<>();

    private final MutableLiveData<Boolean> loading =
            new MutableLiveData<>(false);

    private final MainRepository repository;

    public MainViewModel() {
        repository = new MainRepository();
    }

    public void submitPrompt(String prompt) {
        this.prompt = prompt;

        loading.setValue(true);

        repository.submitPrompt(prompt, new MainRepository.RepositoryCallback() {
            @Override
            public void onResult(String result) {
                response.postValue(result);
                loading.postValue(false);
            }

            @Override
            public void onError(String error) {
                response.postValue("Error: " + error);
                loading.postValue(false);
            }
        });
    }

    public LiveData<String> getResponse() {
        return response;
    }

    public LiveData<Boolean> getLoading() {
        return loading;
    }
}
