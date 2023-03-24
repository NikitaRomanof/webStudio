module com.webstudio.webstudio {
    requires javafx.controls;
    requires javafx.fxml;


    requires org.controlsfx.controls;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.fxmisc.richtext;
    requires org.fxmisc.flowless;
    requires javafx.web;

    opens com.webstudio to javafx.fxml;
    exports com.webstudio;
    exports com.webstudio.model;
    opens com.webstudio.model to javafx.fxml;
    exports com.webstudio.controller;
    opens com.webstudio.controller to javafx.fxml;
    exports com.webstudio.view;
    opens com.webstudio.view to javafx.fxml;
}