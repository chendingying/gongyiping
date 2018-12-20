package com.glory.process.util;

import com.glory.process.model.Menu;

import java.util.Comparator;

/**
 * Created by CDZ on 2018/11/30.
 */
public class MyCompare implements Comparator<Menu> {
    @Override
    public int compare(Menu o1, Menu o2) {
        if (o1.getOrderNum() < o2.getOrderNum()) {
            return -1;
        } else {
            return 1;
        }
    }
}
