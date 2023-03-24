package com.webstudio.controller;

import com.webstudio.model.Model;

import java.util.SortedSet;

public class ControllerSingleton {
    private ControllerSingleton() {

    }
    private static final class HolderSingleton {
        private final static ControllerSingleton INSTANCE = new ControllerSingleton();
    }

    public static ControllerSingleton getInstance() {
        return HolderSingleton.INSTANCE;
    }

    public void modelLoadEntries(SortedSet<String> entries) {
        Model.loadEntries(entries);
    }

    public Integer modelCalculatePositions(int curPos, String allText) {
        return Model.calculatePositions(curPos, allText);
    }

    public String modelGetOneWordInText(String text) {
        return Model.getOneWordInText(text);
    }
}
