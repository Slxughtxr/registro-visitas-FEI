module mx.uv.fei {
    requires javafx.controls;
    requires javafx.fxml;

    opens mx.uv.fei to javafx.fxml;
    exports mx.uv.fei;
}
