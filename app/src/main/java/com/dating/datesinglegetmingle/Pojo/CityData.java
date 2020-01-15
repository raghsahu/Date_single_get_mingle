package com.dating.datesinglegetmingle.Pojo;

/**
 * Created by Ravindra Birla on 01/07/2019.
 */
public class CityData {
    public String city_name;
    public String city_id;
    public String selectPage;

    public CityData(String s){
        city_name = s;
    }

    @Override
    public String toString() {
        return city_name;
    }
}
