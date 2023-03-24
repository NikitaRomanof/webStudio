package com.webstudio.view;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.layout.Region;

public class MaxSizedContextMenu extends ContextMenu {
    public MaxSizedContextMenu() {
        setAutoFix(false);
        setMaxHeight(200.0);

        addEventHandler(Menu.ON_SHOWING, e -> {
            Node content = getSkin().getNode();
            if (content instanceof Region) {
                ((Region) content).setMaxHeight(getMaxHeight());
            }
        });
    }
}
