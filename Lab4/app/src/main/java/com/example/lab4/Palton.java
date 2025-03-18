package com.example.lab4;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class Palton implements Parcelable {
    private String culoare;
    private boolean impermeabil;
    private String marime;
    private String pret;
    private String material;

    private Date dataAdaugare;

    public Palton(String culoare, boolean impermeabil, String marime, String pret, String material, Date data) {
        this.culoare = culoare;
        this.impermeabil = impermeabil;
        this.marime = marime;
        this.pret = pret;
        this.material = material;
        this.dataAdaugare=data;
    }

    protected Palton(Parcel in) {
        culoare = in.readString();
        impermeabil = in.readByte() != 0;
        marime = in.readString();
        pret = in.readString();
        material = in.readString();
    }

    public static final Creator<Palton> CREATOR = new Creator<Palton>() {
        @Override
        public Palton createFromParcel(Parcel in) {
            return new Palton(in);
        }

        @Override
        public Palton[] newArray(int size) {
            return new Palton[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(culoare);
        dest.writeByte((byte) (impermeabil ? 1 : 0));
        dest.writeString(marime);
        dest.writeString(pret);
        dest.writeString(material);
    }

    public String getCuloare() {
        return culoare;
    }

    public boolean isImpermeabil() {
        return impermeabil;
    }

    public String getMarime() {
        return marime;
    }

    public String getPret() {
        return pret;
    }

    public String getMaterial() {
        return material;
    }
    @Override
    public String toString() {
        return "Culoare: " + culoare + ", Mărime: " + marime + ", Preț: " + pret + ", Data Comanda:" + dataAdaugare;
    }

}
