package com.example.ciudades;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CiudadViewModel extends ViewModel {

    public enum Accion {ADD_REQUEST, ADD_ACTION}

    private MutableLiveData<CiudadContainer> liveData;

    public LiveData<CiudadContainer> getData(){
        if (liveData != null) {
            setData(liveData.getValue());
        } else {
            liveData = new MutableLiveData<>();
        }
        return liveData;
    }

    public void setData(CiudadContainer contacto) {
        if (liveData == null) {
            liveData = new MutableLiveData<>();
        } else {
            liveData.setValue(contacto);
        }
    }
}
