package com.webstudio.view;

import com.webstudio.controller.ControllerSingleton;
import com.webstudio.view.MaxSizedContextMenu;
import javafx.geometry.Bounds;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CustomCodeArea extends CodeArea {
    private static final Pattern HTML_TAG = Pattern.compile("(?<ELEMENT>(</?\\h*)(\\w+)([^<>]*)(\\h*/?>))"
            +"|(?<COMMENT><!--(.|\\v)+?-->)"
            + "|(?<CSS>\\.[a-zA-Z][A-Za-z0-9-_]*)"
            +"|(?<STATE>[a-zA-Z][A-Za-z0-9-_]*:)"
            + "|(?<ID>#[0-9a-zA-Z_]+)");
    private static final Pattern ATTRIBUTES = Pattern.compile("(\\w+\\h*)(=|:=)(\\h*\"[^\"]+\")");

    private static final int GROUP_OPEN_BRACKET = 2;
    private static final int GROUP_ELEMENT_NAME = 3;
    private static final int GROUP_ATTRIBUTES_SECTION = 4;
    private static final int GROUP_CLOSE_BRACKET = 5;
    private static final int GROUP_ATTRIBUTE_NAME = 1;
    private static final int GROUP_EQUAL_SYMBOL = 2;
    private static final int GROUP_ATTRIBUTE_VALUE = 3;

    private final SortedSet<String> entries;
    private final ContextMenu entriesPopup;

    public CustomCodeArea() {
        super();
        entries = new TreeSet<>();
        ControllerSingleton.getInstance().modelLoadEntries(entries);
        entriesPopup = new MaxSizedContextMenu();
        setSetting();
        listenerCodeArea();
        focusedProperty().addListener((observableValue, aBoolean, aBoolean2) -> entriesPopup.hide());
    }

    private void setSetting() {
        setFocused(false);
        setFocusTraversable(true);
        setWrapText(false);
        entriesPopup.getStyleClass().add("menu");
    }

    private int getDelIndex() {
        int rezSpace = getText(0, getCaretPosition()).lastIndexOf(" ") + 1;
        int rezEnter = getText(0, getCaretPosition()).lastIndexOf("\n") + 1;
        int rezTab = getText(0, getCaretPosition()).lastIndexOf("\t") + 1;
        return Math.max(Math.max(rezSpace, rezEnter), rezTab);
    }

    public String getOneWordInTextArea(int pos) {
        String text = null;
        try {
            text = getText(0, pos);
        } catch (Exception ignored) {
        }

        if (text == null) {
            try {
                text = getText();
            } catch (Exception ignored) {
            }
        }

        if (text != null && !text.isEmpty()) {
            return ControllerSingleton.getInstance().modelGetOneWordInText(text);
        }

        return "";
    }

    private void listenerCodeArea() {
        textProperty().addListener((obs, oldText, newText) -> {
            setStyleSpans(0, computeHighlighting(newText));
            if (getText() == null || getText().length() == 0) {
                entriesPopup.hide();
            } else {
                LinkedList<String> searchResult = new LinkedList<>();
                String result = getOneWordInTextArea(getCaretPosition() + 1);
                if (!result.isEmpty()) {
                    searchResult.addAll(entries.subSet(result, result + Character.MAX_VALUE));
                }
                if (entries.size() > 0 && searchResult.size() > 0) {
                    populatePopup(searchResult);
                    if (!entriesPopup.isShowing()) {
                        Optional<Bounds> tmp = this.getCaretBounds();
                        tmp.ifPresent(bounds -> entriesPopup.show(this, bounds.getMinX() + 20, bounds.getMinY() + 20));
                    }
                } else {
                    entriesPopup.hide();
                }
            }
        });
    }

    private void populatePopup(List<String> searchResult) {
        List<CustomMenuItem> menuItems = new LinkedList<>();

        for (final String result : searchResult) {
            Label entryLabel = new Label(result);
            CustomMenuItem item = new CustomMenuItem(entryLabel, true);

            item.setOnAction(actionEvent -> {
                deleteText(getDelIndex(), getCaretPosition());
                insertText(getCaretPosition(), result);
                if (result.charAt(1) != '/') {
                    int tmpCaretPos = getCaretPosition();
                    insertText(tmpCaretPos, "</" + result.substring(1));
                    moveTo(tmpCaretPos);
                }
            });
            menuItems.add(item);
        }
        entriesPopup.getItems().clear();
       entriesPopup.getItems().addAll(menuItems);
    }


    private static StyleSpans<Collection<String>> computeHighlighting(String text) {

        Matcher matcher = HTML_TAG.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        while(matcher.find()) {

            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            if(matcher.group("COMMENT") != null) {
                spansBuilder.add(Collections.singleton("comment"), matcher.end() - matcher.start());
            } else if(matcher.group("CSS") != null) {
                spansBuilder.add(Collections.singleton("css"), matcher.end() - matcher.start());
            } else if (matcher.group("STATE") != null) {
                spansBuilder.add(Collections.singleton("state"), matcher.end() - matcher.start());
            } else if(matcher.group("ID") != null) {
                spansBuilder.add(Collections.singleton("id"), matcher.end() - matcher.start());
            } else {
                if(matcher.group("ELEMENT") != null) {
                    String attributesText = matcher.group(GROUP_ATTRIBUTES_SECTION);

                    spansBuilder.add(Collections.singleton("tagmark"), matcher.end(GROUP_OPEN_BRACKET) - matcher.start(GROUP_OPEN_BRACKET));
                    spansBuilder.add(Collections.singleton("anytag"), matcher.end(GROUP_ELEMENT_NAME) - matcher.end(GROUP_OPEN_BRACKET));

                    if(!attributesText.isEmpty()) {

                        lastKwEnd = 0;

                        Matcher amatcher = ATTRIBUTES.matcher(attributesText);
                        while(amatcher.find()) {
                            spansBuilder.add(Collections.emptyList(), amatcher.start() - lastKwEnd);
                            spansBuilder.add(Collections.singleton("attribute"), amatcher.end(GROUP_ATTRIBUTE_NAME) - amatcher.start(GROUP_ATTRIBUTE_NAME));
                            spansBuilder.add(Collections.singleton("tagmark"), amatcher.end(GROUP_EQUAL_SYMBOL) - amatcher.end(GROUP_ATTRIBUTE_NAME));
                            spansBuilder.add(Collections.singleton("avalue"), amatcher.end(GROUP_ATTRIBUTE_VALUE) - amatcher.end(GROUP_EQUAL_SYMBOL));
                            lastKwEnd = amatcher.end();
                        }
                        if(attributesText.length() > lastKwEnd)
                            spansBuilder.add(Collections.emptyList(), attributesText.length() - lastKwEnd);
                    }

                    lastKwEnd = matcher.end(GROUP_ATTRIBUTES_SECTION);

                    spansBuilder.add(Collections.singleton("tagmark"), matcher.end(GROUP_CLOSE_BRACKET) - lastKwEnd);
                }
            }
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }

}
