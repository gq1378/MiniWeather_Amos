package com.amos.bean;

public class TodayWeather {
    private String city=getCity()+"天气";
    private String updatetime;
    private String wendu;
    private String shidu;
    private String pm25;
    private String quality;
    private String fengxiang;
    private String fengli;
    private String date;
    private String high;
    private String low;
    private String type;
//    private String pmImag;
//    private String weatherImg;


    public int getPmImag() {
        return Integer.parseInt(pm25);
    }

//    public void setPmImag(String pm25) {
//        this.pmImag = pm25;
//    }

    public String getWeatherImg() {
        return type;
    }

//    public void setWeatherImg(String type) {
//        this.weatherImg = type;
//    }


    public String getCityName() {
        return city+"天气";
    }

    public String getCity() {
        return city;
    }

    public String getUpdatetime() {
        return updatetime+"发布";
    }

    public String getWendu() {
        return "温度："+wendu+"℃";
    }

    public String getShidu() {
        return "湿度："+shidu;
    }

    public String getPm25() {
        return pm25;
    }

    public String getQuality() {
        return quality;
    }

    public String getFengxiang() {
        return fengxiang;
    }

    public String getFengli() {
        return "风力："+fengli;
    }

    public String getDate() {
        return date;
    }

    public String getHL() {
        return high+"~"+low;
    }

//    public String getLow() {
//        return low;
//    }

    public String getType() {
        return type;
    }

//
    public void setCity(String city) {
        this.city = city;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }

    public void setWendu(String wendu) {
        this.wendu = wendu;
    }

    public void setShidu(String shidu) {
        this.shidu = shidu;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public void setFengxiang(String fengxiang) {
        this.fengxiang = fengxiang;
    }

    public void setFengli(String fengli) {
        this.fengli = fengli;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHigh(String high) {
        this.high = high;
    }

    public void setLow(String low) {
        this.low = low;
    }

    public void setType(String type) {
        this.type = type;
    }



    @Override
    public String toString(){
        return "TodayWeather{"+
                "city='"+city+'\''+
                ",updatetime='"+updatetime+'\''+
                ",wendu='"+wendu+'\''+
                ",shidu='"+shidu+'\''+
                ",pm25='"+pm25+'\''+
                ",quality='"+quality+'\''+
                ",fengxiang='"+fengxiang+'\''+
                ",fengli='"+fengli+'\''+
                ",date='"+date+'\''+
                ",high='"+high+'\''+
                ",low='"+low+'\''+
                ",type='"+type+'\''+
                '}';

    }
}
