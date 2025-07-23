module allday.minico {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires static lombok;
    requires java.desktop;
    requires annotations;

    opens allday.minico.controller.member to javafx.fxml;
    opens allday.minico.controller.miniroom to javafx.fxml;
    opens allday.minico.controller.oxgame to javafx.fxml;
    opens allday.minico to javafx.fxml;
    
    exports allday.minico;
    exports allday.minico.controller.miniroom;
    exports allday.minico.controller.member;

    // 타자게임
    opens allday.minico.controller.typinggame to javafx.fxml;
    exports allday.minico.controller.typinggame;
}
