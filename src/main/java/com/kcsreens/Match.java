package com.kcsreens;

public enum Match
{
    UNSET("WHITE"), IN_POS("GREEN"), OUT_OF_POS("YELLOW"), MISSING("GREY");

    private final String color;

    Match(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return color;
    }
}
