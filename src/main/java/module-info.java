module ensisa.lines {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens ensisa.lines to javafx.fxml;
    exports ensisa.lines;
}