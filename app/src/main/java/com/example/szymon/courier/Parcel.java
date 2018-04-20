package com.example.szymon.courier;


public final class Parcel {

    private String parcelID;
    private String parcelName;
    private String parcelAdres1;
    private String parcelAdres2;
    private String parcelKurier;
    private String parcelNumertel;
    private String parcelStatus;


    public Parcel() {
    }

    public void setParcelID(String parcelID) {this.parcelID = parcelID;}

    public void setparcelName(String parcelName) {this.parcelName = parcelName;}

    public void setparcelAdres1(String parcelAdres1) {this.parcelAdres1 = parcelAdres1;}

    public void setparcelAdres2(String parcelAdres2) {this.parcelAdres2 = parcelAdres2;}

    public void setparcelKurier(String parcelKurier) {this.parcelKurier = parcelKurier;}

    public void setparcelNumertel(String parcelNumertel) {this.parcelNumertel = parcelNumertel;}

    public void setparcelStatus(String parcelStatus) {this.parcelStatus = parcelStatus;}



    public String getParcelID(){
        return parcelID;
    }

    public String getParcelName(){
        return parcelName;
    }

    public String getParcelAdres1(){
        return parcelAdres1;
    }

    public String getParcelAdres2(){
        return parcelAdres2;
    }

    public String getparcelKurier(){
        return parcelKurier;
    }

    public String getParcelNumertel(){
        return parcelNumertel;
    }

    public String getparcelStatus(){
        return parcelStatus;
    }


}

