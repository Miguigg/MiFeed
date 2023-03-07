package com.tfg.mifeed.modelo;

public class MediosModel {
    private String medio;
    private String dominio;
    private boolean isSelected;

    public MediosModel(String medio, String dominio, boolean isSelected){
        this.medio = medio;
        this.dominio = dominio;
        this.isSelected = isSelected;
    }

    public String getMedio() {
        return medio;
    }

    public String getDominio() {
        return dominio;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
