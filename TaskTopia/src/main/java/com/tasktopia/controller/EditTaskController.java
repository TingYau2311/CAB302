package com.tasktopia.controller;

import com.tasktopia.model.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class EditTaskController {

    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private ComboBox<String> priorityBox;
    @FXML private Button saveButton;
    @FXML private Button deleteButton;
    @FXML private Button cancelButton;

    private Task task;

    @FXML
    public void initialize() {
        priorityBox.getItems().addAll("High","Medium","Low");

        saveButton.setOnAction(e -> onSave());
        cancelButton.setOnAction(e -> closeWindow());
        deleteButton.setOnAction(e -> closeWindow()); // or your delete logic
    }

    public void setTask(Task task) {
        this.task = task;
        if (task == null) return;

        nameField.setText(task.getName());
        descriptionField.setText(task.getDescription());
        datePicker.setValue(task.getDate());
        timeField.setText(task.getTimeString());
        priorityBox.setValue(task.getPriorityLabel());
    }

    private void onSave() {
        if (task == null) return;

        String name = nameField.getText() == null ? "" : nameField.getText().trim();
        if (name.isEmpty()) {
            showWarning("Please enter a task name");
            return;
        }

        LocalDate date = datePicker.getValue();

        LocalTime time = null;
        String timeText = timeField.getText() == null ? "" : timeField.getText().trim();
        if (!timeText.isBlank()) {
            try {
                time = LocalTime.parse(timeText);
            } catch (DateTimeParseException ex) {
                showWarning("Time must be in HH:mm format");
                return;
            }
        }

        Task.Priority pri = Task.Priority.MEDIUM;
        if (priorityBox.getValue() != null) {
            try { pri = Task.Priority.valueOf(priorityBox.getValue().toUpperCase()); }
            catch (Exception ignored) {}
        }

        task.setName(name);
        task.setDescription(descriptionField.getText() == null ? "" : descriptionField.getText().trim());
        task.setDate(date);
        task.setTime(time);
        task.setPriority(pri);

        closeWindow();
    }

    private void showWarning(String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
