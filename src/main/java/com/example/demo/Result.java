package com.example.demo;

public class Result {

    private int dayOfPurchase;
    private int dayOfSale;
    private int diff;

    public Result(int dayOfPurchase, int dayOfSale, int diff) {
        this.dayOfPurchase = dayOfPurchase;
        this.dayOfSale = dayOfSale;
        this.diff = diff;

    }

    public int getDiff() {
        return diff;
    }

    public int getDayOfPurchase() {
        return dayOfPurchase;
    }

    public int getDayOfSale() {
        return dayOfSale;
    }

    @Override
    public String toString() {
        return "Dayofpurchase= " + (dayOfPurchase + 1) + ", Dayofsale= " + (dayOfSale + 1) + ", Rate= " + diff;
    }


}
