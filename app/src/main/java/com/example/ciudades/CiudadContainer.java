package com.example.ciudades;

public class CiudadContainer {
    private Ciudad ciudad;
    private CiudadViewModel.Accion accion;
    private String key;

    public CiudadContainer(Ciudad ciudad, CiudadViewModel.Accion accion) {
        this.ciudad = ciudad;
        this.accion = accion;
    }

    public CiudadContainer(Ciudad ciudad, CiudadViewModel.Accion accion, String key) {
        this.ciudad = ciudad;
        this.accion = accion;
        this.key = key;
    }

    public CiudadContainer() {
    }

    public Ciudad getCiudad() {
        return ciudad;
    }

    public void setCiudad(Ciudad ciudad) {
        this.ciudad = ciudad;
    }

    public CiudadViewModel.Accion getAccion() {
        return accion;
    }

    public void setAccion(CiudadViewModel.Accion accion) {
        this.accion = accion;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
