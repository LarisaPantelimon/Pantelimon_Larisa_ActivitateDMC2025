package com.example.lab4;

import android.os.Parcel;
import android.os.Parcelable;

public class Palton {
    private String culoare;
    private boolean impermeabil;
    private String marime;
    private String pret;
    private String material;

    // Constructor
    public Palton(String culoare, boolean impermeabil, String marime, String pret, String material) {
        this.culoare = culoare;
        this.impermeabil = impermeabil;
        this.marime = marime;
        this.pret = pret;
        this.material = material;
    }

    // Getters and setters
    public String getCuloare() {
        return culoare;
    }

    public void setCuloare(String culoare) {
        this.culoare = culoare;
    }

    public boolean isImpermeabil() {
        return impermeabil;
    }

    public void setImpermeabil(boolean impermeabil) {
        this.impermeabil = impermeabil;
    }

    public String getMarime() {
        return marime;
    }

    public void setMarime(String marime) {
        this.marime = marime;
    }

    public String getPret() {
        return pret;
    }

    public void setPret(String pret) {
        this.pret = pret;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }
}
