package com.tasktopia.controller;

import com.tasktopia.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.control.SpinnerValueFactory;
import java.time.LocalDateTime;
import java.util.List;

public class MainController {

    // ─── Contact fields ───────────────────────────────────────────────
    @FXML private ListView<Contact> contactsListView;
    @FXML private TextField firstNameTextField;
    @FXML private TextField lastNameTextField;
    @FXML private TextField emailTextField;
    @FXML private TextField passwordTextField;
    @FXML private VBox contactContainer;

    private IContactDAO contactDAO;

    // ─── Task fields ──────────────────────────────────────────────────
    @FXML private ListView<Task> tasksListView;
    @FXML private TextField taskTitleTextField;
    @FXML private DatePicker taskStartDatePicker;
    @FXML private DatePicker taskEndDatePicker;
    @FXML private TextField taskDescriptionTextField;
    @FXML private TextField taskTagsTextField;
    @FXML private VBox taskContainer;
    @FXML private Spinner<Integer> taskStartHourSpinner;
    @FXML private Spinner<Integer> taskStartMinuteSpinner;
    @FXML private Spinner<Integer> taskEndHourSpinner;
    @FXML private Spinner<Integer> taskEndMinuteSpinner;

    private ITaskDAO taskDAO;

    public MainController() {
        contactDAO = new SqliteContactDAO();
        taskDAO = new SqliteTaskDAO();
    }

    // ═══════════════════════════════════════════════════════════════════
    // CONTACTS
    // ═══════════════════════════════════════════════════════════════════

    private void selectContact(Contact contact) {
        contactsListView.getSelectionModel().select(contact);
        firstNameTextField.setText(contact.getFirstName());
        lastNameTextField.setText(contact.getLastName());
        emailTextField.setText(contact.getEmail());
        passwordTextField.setText(contact.getPassword());
    }

    private ListCell<Contact> renderContactCell(ListView<Contact> listView) {
        return new ListCell<>() {
            private void onContactSelected(MouseEvent mouseEvent) {
                ListCell<Contact> clickedCell = (ListCell<Contact>) mouseEvent.getSource();
                Contact selected = clickedCell.getItem();
                if (selected != null) selectContact(selected);
            }

            @Override
            protected void updateItem(Contact contact, boolean empty) {
                super.updateItem(contact, empty);
                if (empty || contact == null || contact.getFullName() == null) {
                    setText(null);
                    super.setOnMouseClicked(this::onContactSelected);
                } else {
                    setText(contact.getFullName());
                }
            }
        };
    }

    private void syncContacts() {
        contactsListView.getItems().clear();
        List<Contact> contacts = contactDAO.getAllContacts();
        boolean hasContacts = !contacts.isEmpty();
        if (hasContacts) contactsListView.getItems().addAll(contacts);
        contactContainer.setVisible(hasContacts);
    }

    @FXML
    private void onAddContact() {
        Contact newContact = new Contact("New", "Contact", "", "");
        contactDAO.addContact(newContact);
        syncContacts();
        selectContact(newContact);
        firstNameTextField.requestFocus();
    }

    @FXML
    private void onEditContactConfirm() {
        Contact selected = contactsListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setFirstName(firstNameTextField.getText());
            selected.setLastName(lastNameTextField.getText());
            selected.setEmail(emailTextField.getText());
            selected.setPassword(passwordTextField.getText());
            contactDAO.updateContact(selected);
            syncContacts();
        }
    }

    @FXML
    private void onDeleteContact() {
        Contact selected = contactsListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            contactDAO.deleteContact(selected);
            syncContacts();
        }
    }

    @FXML
    private void onCancelContact() {
        Contact selected = contactsListView.getSelectionModel().getSelectedItem();
        if (selected != null) selectContact(selected);
    }

    // ═══════════════════════════════════════════════════════════════════
    // TASKS
    // ═══════════════════════════════════════════════════════════════════

    private void selectTask(Task task) {
        tasksListView.getSelectionModel().select(task);
        taskTitleTextField.setText(task.getTitle());
        taskStartDatePicker.setValue(task.getStartDate().toLocalDate());
        taskStartHourSpinner.getValueFactory().setValue(task.getStartDate().getHour());
        taskStartMinuteSpinner.getValueFactory().setValue(task.getStartDate().getMinute());
        taskEndDatePicker.setValue(task.getEndDate().toLocalDate());
        taskEndHourSpinner.getValueFactory().setValue(task.getEndDate().getHour());
        taskEndMinuteSpinner.getValueFactory().setValue(task.getEndDate().getMinute());
        taskDescriptionTextField.setText(task.getDescription());
        taskTagsTextField.setText(task.getTags());
    }

    private ListCell<Task> renderTaskCell(ListView<Task> listView) {
        return new ListCell<>() {
            private void onTaskSelected(MouseEvent mouseEvent) {
                ListCell<Task> clickedCell = (ListCell<Task>) mouseEvent.getSource();
                Task selected = clickedCell.getItem();
                if (selected != null) selectTask(selected);
            }

            @Override
            protected void updateItem(Task task, boolean empty) {
                super.updateItem(task, empty);
                if (empty || task == null || task.getTitle() == null) {
                    setText(null);
                    super.setOnMouseClicked(this::onTaskSelected);
                } else {
                    setText(task.getTitle());
                }
            }
        };
    }

    private void syncTasks() {
        tasksListView.getItems().clear();
        List<Task> tasks = taskDAO.getAllTasks();
        boolean hasTasks = !tasks.isEmpty();
        if (hasTasks) tasksListView.getItems().addAll(tasks);
        taskContainer.setVisible(hasTasks);
    }

    @FXML
    private void onAddTask() {
        Task newTask = new Task("New Task", LocalDateTime.now(),
                LocalDateTime.now().plusDays(1), "", "", 0);
        taskDAO.addTask(newTask);
        syncTasks();
        selectTask(newTask);
        taskTitleTextField.requestFocus();
    }

    @FXML
    private void onEditTaskConfirm() {
        Task selected = tasksListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            selected.setTitle(taskTitleTextField.getText());
            selected.setStartDate(taskStartDatePicker.getValue().atTime(
                    taskStartHourSpinner.getValue(), taskStartMinuteSpinner.getValue()));
            selected.setEndDate(taskEndDatePicker.getValue().atTime(
                    taskEndHourSpinner.getValue(), taskEndMinuteSpinner.getValue()));
            selected.setDescription(taskDescriptionTextField.getText());
            selected.setTags(taskTagsTextField.getText());
            taskDAO.updateTask(selected);
            syncTasks();
            Task reSelected = tasksListView.getSelectionModel().getSelectedItem();
            if (reSelected != null) selectTask(reSelected);
        }
    }
    @FXML
    private void onDeleteTask() {
        Task selected = tasksListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            taskDAO.deleteTask(selected);
            syncTasks();
        }
    }

    @FXML
    private void onCancelTask() {
        Task selected = tasksListView.getSelectionModel().getSelectedItem();
        if (selected != null) selectTask(selected);
    }

    // ═══════════════════════════════════════════════════════════════════
    // INIT
    // ═══════════════════════════════════════════════════════════════════

    @FXML
    public void initialize() {
        // Contacts
        contactsListView.setCellFactory(this::renderContactCell);
        syncContacts();
        contactsListView.getSelectionModel().selectFirst();
        Contact firstContact = contactsListView.getSelectionModel().getSelectedItem();
        if (firstContact != null) selectContact(firstContact);

        taskStartHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        taskStartMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        taskEndHourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        taskEndMinuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

        // Tasks
        tasksListView.setCellFactory(this::renderTaskCell);
        syncTasks();
        tasksListView.getSelectionModel().selectFirst();
        Task firstTask = tasksListView.getSelectionModel().getSelectedItem();
        if (firstTask != null) selectTask(firstTask);
    }
}