package com.example.demo;

import java.text.ParseException;
import java.text.RuleBasedCollator;

public class City implements Comparable<City> {

    private final String zipNumber;

    private final String name;

    private final String districtName;

    private final static int ZIP_POSITION = 0;

    private final static int CITY_POSITION = 1;

    private final static int DISTRICT_POSITION = 2;

    private final static String DATA_SEPARATOR = ";";

    private static final String HUNGARIAN_COLLATOR_RULE = "<' ' < a,A < á,Á < b,B < c,C < cs,Cs,CS < d,D < dz,Dz,DZ < dzs,Dzs,DZS \n" +
            " < e,E < é,É < f,F < g,G < gy,Gy,GY < h,H < i,I < í,Í < j,J\n" +
            " < k,K < l,L < ly,Ly,LY < m,M < n,N < ny,Ny,NY < o,O < ó,Ó \n" +
            " < ö,Ö < ő,Ő < p,P < q,Q < r,R < s,S < sz,Sz,SZ < t,T \n" +
            " < ty,Ty,TY < u,U < ú,Ú < ü,Ü < ű,Ű < v,V < w,W < x,X < y,Y < z,Z < zs,Zs,ZS";

    private RuleBasedCollator hunRuleBasedCollator;


    public City(String zipNumber, String name, String districtName) {

        if (zipNumber == null || name == null || zipNumber.isBlank() || name.isBlank()) {
            throw new IllegalArgumentException("Wrong parameters");
        }

        this.zipNumber = zipNumber;

        this.name = name;

        this.districtName = districtName;
        try {
            hunRuleBasedCollator = new RuleBasedCollator(HUNGARIAN_COLLATOR_RULE);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static City generateFromCsv(String csvString) {

        if (csvString == null || csvString.isBlank()) {
            throw new IllegalArgumentException("Wrong input");
        }

        String[] parts = csvString.split(DATA_SEPARATOR);
        String district = null;
        if (parts.length > DISTRICT_POSITION) {
            district = parts[DISTRICT_POSITION];
        }

        return new City(parts[ZIP_POSITION], parts[CITY_POSITION], district);
    }

    public String getZipNumber() {
        return zipNumber;
    }

    public String getName() {
        return name;
    }


    public String getDistrictName() {
        return districtName;
    }

    @Override
    public String toString() {
        return name.trim() + (districtName == null || districtName.isBlank() ? "" : "-" + districtName.trim());
        //return zipNumber + " " + name.trim() + (districtName == null ? "" : "-" + districtName.trim());
        //return name;
    }


    @Override
    public int compareTo(City o) {
        int result = hunRuleBasedCollator.compare(name, o.name);
        if (result != 0) {
            return result;
        }
        if (districtName == null && o.districtName == null) {
            return 0;
        }
        if (districtName == null) {
            return -1;
        }
        if (o.districtName == null) {
            return 1;
        }
        return hunRuleBasedCollator.compare(districtName, o.districtName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        City city = (City) o;

        if (!name.equals(city.name)) return false;
        return districtName != null ? districtName.equals(city.districtName) : city.districtName == null;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + (districtName != null ? districtName.hashCode() : 0);
        return result;
    }
}
