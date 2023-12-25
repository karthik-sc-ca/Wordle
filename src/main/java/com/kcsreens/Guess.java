package com.kcsreens;
public class Guess {
    Character c;
    Match g;
    int i;

    Guess(int index) {
        this.i = index;
    }

    @Override
    public String toString() {
        return c + "->" + g;
    }
}
