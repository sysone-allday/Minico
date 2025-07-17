module allday.minico {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires java.desktop;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens allday.minico to javafx.fxml;
    exports allday.minico;
    exports allday.minico.controller;
    opens allday.minico.controller to javafx.fxml;
}