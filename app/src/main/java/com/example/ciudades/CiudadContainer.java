package com.example.ciudades;

public class CiudadContainer {
    private Ciudad ciudad;
    private Util.Accion accion;
    private String key;

    public CiudadContainer(Ciudad ciudad, Util.Accion accion) {
        this.ciudad = ciudad;
        this.accion = accion;
    }

    public CiudadContainer(Ciudad ciudad, Util.Accion accion, String key) {
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

    public Util.Accion getAccion() {
        return accion;
    }

    public void setAccion(Util.Accion accion) {
        this.accion = accion;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
