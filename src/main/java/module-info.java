module mx.uv.fei {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires transitive javafx.graphics;

    opens mx.uv.fei to javafx.fxml;
    opens mx.uv.fei.presentation.controllers to javafx.fxml;
    opens mx.uv.fei.domain.entities to javafx.base;

    exports mx.uv.fei;
}
