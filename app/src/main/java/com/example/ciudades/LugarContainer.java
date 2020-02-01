package com.example.ciudades;

class LugarContainer {
    private Lugar lugar;
    private Util.Accion accion;
    private String key;

    public LugarContainer(Lugar lugar, Util.Accion accion, String key) {
        this.lugar = lugar;
        this.accion = accion;
        this.key = key;
    }

    public LugarContainer(Lugar lugar, Util.Accion accion) {
        this.lugar = lugar;
        this.accion = accion;
    }

    public LugarContainer() {
    }

    public Lugar getLugar() {
        return lugar;
    }

    public void setLugar(Lugar lugar) {
        this.lugar = lugar;
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
