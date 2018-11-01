package com.amos.bean;

public class City {
    private String province;
    private String city;
    private String number;
    private String firstPY;
    private String allPY;
    private String allFirstPY;

    public String getCity() {
        return city;
    }

    public String getNumber() {
        return number;
    }

    @Override
    public String toString(){
        return province+" "+city+" "+number+" "+firstPY.toLowerCase()+" "+allPY.toLowerCase()+" "+allFirstPY.toLowerCase();
    }

    public City(String province, String city, String number, String firstPY, String allPY, String allFirstPY){
        this.province=province;
        this.city=city;
        this.number=number;
        this.firstPY=firstPY;
        this.allPY=allPY;
        this.allFirstPY=allFirstPY;
    }
}
