module com.tasktopia {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.tasktopia to javafx.fxml;
    opens com.tasktopia.controller to javafx.fxml;
    opens com.tasktopia.model to javafx.fxml;
    opens fxml to javafx.fxml;

    exports com.tasktopia;
    exports com.tasktopia.controller;
    exports com.tasktopia.model;
    exports com.tasktopia.util;
}
