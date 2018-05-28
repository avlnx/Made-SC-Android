package com.made.madesc.model;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainViewModel extends ViewModel {
    // Auth model for main activity
    private MutableLiveData<FirebaseUser> currentUser;

    public LiveData<FirebaseUser> getCurrentUser() {
        if (currentUser == null) {
            currentUser = new MutableLiveData<>();
            loadUser();
        }
        return currentUser;
    }

    private void loadUser() {
        // Do an asynchronous operation to fetch user
        currentUser.postValue(FirebaseAuth.getInstance().getCurrentUser());
    }

    public void setCurrentUser(FirebaseUser currentUser) {
        this.currentUser.postValue(currentUser);
    }
}
