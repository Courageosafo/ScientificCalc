module calculatorfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.java.scientific to javafx.fxml;
    exports com.java.scientific;
}