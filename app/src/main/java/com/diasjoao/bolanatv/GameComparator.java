package com.diasjoao.bolanatv;

import java.text.ParseException;
import java.util.Comparator;

public class GameComparator implements Comparator<Game> {

    @Override
    public int compare(Game o1, Game o2) {
        int value1 = 0;
        try {
            value1 = o1.getFullDate().compareTo(o2.getFullDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (value1 == 0) {
            return o1.getHour().compareTo(o2.getHour());
        }
        return value1;
    }
}
