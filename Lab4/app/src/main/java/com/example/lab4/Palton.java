package com.example.lab4;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "paltoane")
public class Palton implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "culoare")
    private String culoare;

    @ColumnInfo(name = "impermeabil")
    private boolean impermeabil;

    @ColumnInfo(name = "marime")
    private String marime;

    @ColumnInfo(name = "pret")
    private String pret;

    @ColumnInfo(name = "material")
    private String material;

    @ColumnInfo(name = "dataAdaugare")
    private String dataAdaugare;

    // Constructorul public implicit, necesar pentru Room
    public Palton() {
    }

    // Constructorul principal cu parametri
    public Palton(String culoare, boolean impermeabil, String marime, String pret, String material, Date data) {
        this.culoare = culoare;
        this.impermeabil = impermeabil;
        this.marime = marime;
        this.pret = pret;
        this.material = material;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        this.dataAdaugare = formatter.format(data);
    }

    // Constructorul pentru Parcelable
    protected Palton(Parcel in) {
        culoare = in.readString();
        impermeabil = in.readByte() != 0;
        marime = in.readString();
        pret = in.readString();
        material = in.readString();
        dataAdaugare = in.readString();
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
        dest.writeString(dataAdaugare);
    }

    // Getteri și setteri
    public String getCuloare() {
        return culoare;
    }

    public boolean isImpermeabil() {
        return impermeabil;
    }

    public String getDataAdaugare() {
        return dataAdaugare;
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

    public void setCuloare(String culoare) {
        this.culoare = culoare;
    }

    public void setDataAdaugare(String dataAdaugare) {
        this.dataAdaugare = dataAdaugare;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public void setPret(String pret) {
        this.pret = pret;
    }

    public void setMarime(String marime) {
        this.marime = marime;
    }

    public void setImpermeabil(boolean impermeabil) {
        this.impermeabil = impermeabil;
    }

    @Override
    public String toString() {
        return "Culoare: " + culoare + ", Mărime: " + marime + ", Preț: " + pret + ", Data Comanda: " + dataAdaugare;
    }
}
