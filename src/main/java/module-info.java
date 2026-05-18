module com.tasktopia {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.google.gson;
    requires java.net.http;

    opens com.tasktopia to javafx.fxml;
    
    opens com.tasktopia.controller to javafx.fxml;
    opens com.tasktopia.model to javafx.fxml;
    opens fxml to javafx.fxml;

    opens au.edu.qut to com.google.gson;
    opens au.edu.qut.cogniti to com.google.gson;

    exports com.tasktopia;
    exports com.tasktopia.controller;
    exports com.tasktopia.model;
    exports com.tasktopia.util;

    exports au.edu.qut;
    exports au.edu.qut.cogniti;
}
