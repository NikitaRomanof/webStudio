package com.webstudio.model;

import com.webstudio.WebStudio;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.SortedSet;

public class Model {
    public Model() {

    }

    public static void loadEntries(SortedSet<String> entries) {

        try(BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(WebStudio.
                class.getResourceAsStream("entries.txt"))))) {

            String line;

            while (reader.ready()) {
                line = reader.readLine();
                entries.add(line);
            }

        } catch (Exception ignored) {
        }
    }

    public static Integer calculatePositions(int curPos, String allText) {
        if (curPos < 2 || allText == null || allText.isEmpty()) return null;
        curPos = curPos >= allText.length() ? allText.length() - 1 : curPos;
        String beforePosText = allText.substring(0, curPos);
        String afterPosText = allText.substring(curPos);
        int beforePos = beforePosText.length() - 1;
        int afterPos = 0;

        while (beforePos > 1 && beforePosText.charAt(beforePos) != '\n') --beforePos;

        while (afterPos + curPos < allText.length() - 1 && afterPosText.charAt(afterPos) != '\n') ++afterPos;

        afterPos = afterPos + curPos;

        return afterPos;
    }

    public static String getOneWordInText(String text) {
        if (text.charAt(text.length() - 1) == ' '
                || text.charAt(text.length() - 1) == '\t'
                || text.charAt(text.length() - 1) == '\n') return "";

        String[] allStrBeforeCursor = text.split("\\s");
        if (allStrBeforeCursor.length == 0) return "";
        return allStrBeforeCursor[allStrBeforeCursor.length - 1].trim();
    }
}
