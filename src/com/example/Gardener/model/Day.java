package com.example.Gardener.model;

/**
 * Represent a day of the week
 */
public enum Day {
    MON(1), TUE(2), WED(3), THU(4), FRI(5), SAT(6), SUN(7);

    Day(int dayNum){
        this.dayNum = dayNum;
    }

    private int dayNum;

    public int getDayNum(){
        return dayNum;
    }
}
