module bloom {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires mysql.connector.j;
    requires jbcrypt;

    opens org.bcc.bloom to javafx.fxml;

    exports org.bcc.bloom;
    exports org.bcc.bloom.exception;
    exports org.bcc.bloom.model;
    exports org.bcc.bloom.service;
    opens org.bcc.bloom.controller to javafx.fxml;
    opens images;
}