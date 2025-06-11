module org.example.mnistann {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires deeplearning4j.datasets;
    requires nd4j.api;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;

    opens org.example.mnistann to javafx.fxml;
    exports org.example.mnistann;
    exports org.example.mnistann.controllers;
    opens org.example.mnistann.controllers to javafx.fxml;
}