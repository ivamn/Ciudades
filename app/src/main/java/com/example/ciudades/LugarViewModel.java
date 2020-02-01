package com.example.ciudades;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LugarViewModel extends ViewModel {
    private MutableLiveData<LugarContainer> liveData;

    public LiveData<LugarContainer> getData(){
        if (liveData != null) {
            setData(liveData.getValue());
        } else {
            liveData = new MutableLiveData<>();
        }
        return liveData;
    }

    public void setData(LugarContainer contacto) {
        if (liveData == null) {
            liveData = new MutableLiveData<>();
        } else {
            liveData.setValue(contacto);
        }
    }
}
