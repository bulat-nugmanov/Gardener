package com.example.Gardener.model;

/**
 * Represents day of the week, indexed from Monday = 0
 */
public enum Day {
    MON(0), TUE(1), WED(2), THU(3), FRI(4), SAT(5), SUN(6);

    Day(int dayNum){
        this.dayNum = dayNum;
    }

    private int dayNum;

    public int getDayNum(){
        return dayNum;
    }
}
