package com.tasktopia.controller;

import com.tasktopia.model.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.control.SpinnerValueFactory;
import java.time.LocalDateTime;
import java.util.List;
import com.tasktopia.model.TaskStore;

/**
 * JavaFX controller for the main admin/management screen.
 * <p>
 * Provides full CRUD (Create, Read, Update, Delete) management for both
 * {@link Contact} and {@link Task} entities through two {@link ListView} panels.
 * Contacts are handled via {@link IContactDAO} and tasks via {@link ITaskDAO},
 * both backed by SQLite by default.
 * </p>
 *
 * <p>Tasks are filtered by the currently logged-in user (from {@link TaskStore})
 * when a valid user ID is available; otherwise all tasks are shown.</p>
 */
public class MainController {

    // ─── Contact FXML fields ──────────────────────────────────────────────

    /** ListView displaying all contacts in the system. */
    @FXML private ListView<Contact> contactsListView;

    /** Input field for a contact's first name. */
    @FXML private TextField firstNameTextField;

    /** Input field for a contact's last name. */
    @FXML private TextField lastNameTextField;

    /** Input field for a contact's email address. */
    @FXML private TextField emailTextField;

    /** Input field for a contact's password. */
    @FXML private TextField passwordTextField;

    /** Container that is hidden when no contacts exist. */
    @FXML private VBox contactContainer;

    /** DAO used for all contact persistence operations. */
    private IContactDAO contactDAO;

    // ─── Task FXML fields ─────────────────────────────────────────────────

    /** ListView displaying tasks belonging to the current user. */
    @FXML private ListView<Task> tasksListView;

    /** Input field for a task's title. */
    @FXML private TextField taskTitleTextField;

    /** Date picker for the task's start date. */
    @FXML private DatePicker taskStartDatePicker;

    /** Date picker for the task's end date. */
    @FXML private DatePicker taskEndDatePicker;

    /** Input field for a task's description. */
    @FXML private TextField taskDescriptionTextField;

    /** Input field for a task's tags. */
    @FXML private TextField taskTagsTextField;

    /** Container that is hidden when no tasks exist. */
    @FXML private VBox taskContainer;

    /** Spinner for selecting the hour component of the task's start time (0–23). */
    @FXML private Spinner<Integer> taskStartHourSpinner;

    /** Spinner for selecting the minute component of the task's start time (0–59). */
    @FXML private Spinner<Integer> taskStartMinuteSpinner;

    /** Spinner for selecting the hour component of the task's end time (0–23). */
    @FXML private Spinner<Integer> taskEndHourSpinner;

    /** Spinner for selecting the minute component of the task's end time (0–59). */
    @FXML private Spinner<Integer> taskEndMinuteSpinner;

    /** DAO used for all task persistence operations. */
    private ITaskDAO taskDAO;

    /**
     * Constructs a new {@code MainController} and initialises both DAOs
     * with their default SQLite implementations.
     */
    public MainController() {
        contactDAO = new SqliteContactDAO();
        taskDAO = new SqliteTaskDAO();
    }

    // ═══════════════════════════════════════════════════════════════════
    // CONTACTS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Selects the given contact in the ListView and populates the edit fields
     * with its current values.
     *
     * @param contact the {@link Contact} to select; must not be {@code null}
     */
    private void selectContact(Contact contact) {
        contactsListView.getSelectionModel().select(contact);
        firstNameTextField.setText(contact.getFirstName());
        lastNameTextField.setText(contact.getLastName());
        emailTextField.setText(contact.getEmail());
        passwordTextField.setText(contact.getPassword());
    }

    /**
     * Returns a {@link ListCell} factory for the contacts ListView.
     * Each cell displays the contact's full name and calls
     * {@link #selectContact(Contact)} when clicked.
     *
     * @param listView the parent {@link ListView}; provided by the cell factory callback
     * @return a new {@link ListCell} configured for {@link Contact} display
     */
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

    /**
     * Reloads all contacts from the DAO and refreshes the ListView.
     * The {@code contactContainer} is hidden if there are no contacts.
     */
    private void syncContacts() {
        contactsListView.getItems().clear();
        List<Contact> contacts = contactDAO.getAllContacts();
        boolean hasContacts = !contacts.isEmpty();
        if (hasContacts) contactsListView.getItems().addAll(contacts);
        contactContainer.setVisible(hasContacts);
    }

    /**
     * Creates a placeholder contact named "New Contact" with empty email and
     * password, persists it, refreshes the list, and selects it for immediate editing.
     */
    @FXML
    private void onAddContact() {
        Contact newContact = new Contact("New", "Contact", "", "");
        contactDAO.addContact(newContact);
        syncContacts();
        selectContact(newContact);
        firstNameTextField.requestFocus();
    }

    /**
     * Reads the values from the contact edit fields, applies them to the
     * currently selected contact, and persists the update.
     */
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

    /**
     * Deletes the currently selected contact from the data store and
     * refreshes the contacts ListView.
     */
    @FXML
    private void onDeleteContact() {
        Contact selected = contactsListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            contactDAO.deleteContact(selected);
            syncContacts();
        }
    }

    /**
     * Reverts any unsaved edits by re-populating the edit fields from the
     * currently selected contact's persisted values.
     */
    @FXML
    private void onCancelContact() {
        Contact selected = contactsListView.getSelectionModel().getSelectedItem();
        if (selected != null) selectContact(selected);
    }

    // ═══════════════════════════════════════════════════════════════════
    // TASKS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Selects the given task in the ListView and populates all task edit fields,
     * date pickers, and time spinners with its current values.
     *
     * @param task the {@link Task} to select; must not be {@code null}
     */
    private void selectTask(Task task) {
        tasksListView.getSelectionModel().select(task);
        taskTitleTextField.setText(task.getTitle() != null ? task.getTitle() : "");
        taskStartDatePicker.setValue(task.getStartDate().toLocalDate());
        taskStartHourSpinner.getValueFactory().setValue(task.getStartDate().getHour());
        taskStartMinuteSpinner.getValueFactory().setValue(task.getStartDate().getMinute());
        taskEndDatePicker.setValue(task.getEndDate().toLocalDate());
        taskEndHourSpinner.getValueFactory().setValue(task.getEndDate().getHour());
        taskEndMinuteSpinner.getValueFactory().setValue(task.getEndDate().getMinute());
        taskDescriptionTextField.setText(task.getDescription() != null ? task.getDescription() : "");
        taskTagsTextField.setText(task.getTags() != null ? task.getTags() : "");
    }

    /**
     * Returns a {@link ListCell} factory for the tasks ListView.
     * Each cell displays the task's title and calls {@link #selectTask(Task)}
     * when clicked.
     *
     * @param listView the parent {@link ListView}; provided by the cell factory callback
     * @return a new {@link ListCell} configured for {@link Task} display
     */
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

    /**
     * Reloads all tasks from the DAO and refreshes the ListView without
     * re-selecting any particular task.
     */
    private void syncTasks() {
        syncTasks(-1);
    }

    /**
     * Reloads tasks from the DAO for the currently logged-in user (or all tasks
     * if no valid user ID is set) and refreshes the ListView.
     * If {@code reSelectId} is greater than zero, the task with that ID is
     * automatically re-selected after the reload.
     *
     * @param reSelectId the ID of the task to re-select after sync, or {@code -1} to skip
     */
    private void syncTasks(int reSelectId) {
        tasksListView.getItems().clear();
        int userId = com.tasktopia.model.TaskStore.getInstance().getLoggedInUserId();
        List<Task> tasks = userId > 0
                ? taskDAO.getTasksByUser(userId)
                : taskDAO.getAllTasks();
        boolean hasTasks = !tasks.isEmpty();
        if (hasTasks) tasksListView.getItems().addAll(tasks);
        taskContainer.setVisible(hasTasks);

        if (reSelectId > 0) {
            tasks.stream()
                    .filter(t -> t.getId() == reSelectId)
                    .findFirst()
                    .ifPresent(t -> {
                        tasksListView.getSelectionModel().select(t);
                        selectTask(t);
                    });
        }
    }

    /**
     * Creates a new placeholder task with the current time as the start time
     * and one day later as the end time, persists it, and selects it for
     * immediate editing.
     */
    @FXML
    private void onAddTask() {
        Task newTask = new Task("New Task", LocalDateTime.now(),
                LocalDateTime.now().plusDays(1), "", "", 0);
        taskDAO.addTask(newTask);
        syncTasks(newTask.getId());
        taskTitleTextField.requestFocus();
    }

    /**
     * Reads all task edit fields and spinners, applies the new values to the
     * currently selected task, persists the update, and refreshes the ListView.
     */
    @FXML
    private void onEditTaskConfirm() {
        Task selected = tasksListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            int taskId = selected.getId();
            selected.setTitle(taskTitleTextField.getText());
            selected.setStartDate(taskStartDatePicker.getValue().atTime(
                    taskStartHourSpinner.getValue(), taskStartMinuteSpinner.getValue()));
            selected.setEndDate(taskEndDatePicker.getValue().atTime(
                    taskEndHourSpinner.getValue(), taskEndMinuteSpinner.getValue()));
            selected.setDescription(taskDescriptionTextField.getText());
            selected.setTags(taskTagsTextField.getText());
            taskDAO.updateTask(selected);
            syncTasks(taskId);
        }
    }

    /**
     * Deletes the currently selected task from the data store, refreshes the
     * ListView, and clears the task edit fields.
     */
    @FXML
    private void onDeleteTask() {
        Task selected = tasksListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            taskDAO.deleteTask(selected);
            syncTasks();
            taskTitleTextField.clear();
            taskDescriptionTextField.clear();
            taskTagsTextField.clear();
        }
    }

    /**
     * Reverts any unsaved task edits by re-populating all edit fields from the
     * currently selected task's persisted values.
     */
    @FXML
    private void onCancelTask() {
        Task selected = tasksListView.getSelectionModel().getSelectedItem();
        if (selected != null) selectTask(selected);
    }

    // ═══════════════════════════════════════════════════════════════════
    // INIT
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Called by the JavaFX runtime after all {@code @FXML} fields are injected.
     * <p>
     * Performs the following setup in order:
     * <ol>
     *   <li>Sets custom cell factories on both ListViews.</li>
     *   <li>Loads and displays contacts; selects the first one if present.</li>
     *   <li>Configures all four time {@link Spinner} value factories.</li>
     *   <li>Loads and displays tasks for the current user; selects the first one if present.</li>
     * </ol>
     * The spinners must be initialised before {@link #syncTasks()} is called to
     * avoid a {@link NullPointerException} when a task is auto-selected.
     * </p>
     */
    @FXML
    public void initialize() {
        // Contacts
        contactsListView.setCellFactory(this::renderContactCell);
        syncContacts();
        contactsListView.getSelectionModel().selectFirst();
        Contact firstContact = contactsListView.getSelectionModel().getSelectedItem();
        if (firstContact != null) selectContact(firstContact);

        // Spinners must be set up before syncTasks so selectTask() doesn't NPE
        taskStartHourSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        taskStartMinuteSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));
        taskEndHourSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, 0));
        taskEndMinuteSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0));

        // Tasks
        tasksListView.setCellFactory(this::renderTaskCell);
        syncTasks();
        tasksListView.getSelectionModel().selectFirst();
        Task firstTask = tasksListView.getSelectionModel().getSelectedItem();
        if (firstTask != null) selectTask(firstTask);
    }
}