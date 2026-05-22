package com.tasktopia.controller;

import com.tasktopia.MainApp;
import com.tasktopia.model.CategoryManager;
import com.tasktopia.model.CustomCategory;
import com.tasktopia.model.ITaskDAO;
import com.tasktopia.model.SqliteCategoryDAO;
import com.tasktopia.model.SqliteTaskDAO;
import com.tasktopia.model.Task;
import com.tasktopia.model.TaskStore;
import com.tasktopia.util.Styles;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

// imports for AI task management
import ai.TaskExtractor;
import au.edu.qut.cogniti.CognitiConversation;
import au.edu.qut.cogniti.Secrets;

/**
 * JavaFX controller for the main Home screen of the Tasktopia application.
 * <p>
 * This is the primary user-facing screen after login. It is responsible for:
 * <ul>
 *   <li>Displaying the current user's tasks, filtered by category.</li>
 *   <li>Building and managing all modal overlays (Add Manual, Add AI, Task Detail,
 *       Edit Task, Add Category, Settings).</li>
 *   <li>Handling category navigation, including custom user-defined categories
 *       persisted via {@link SqliteCategoryDAO}.</li>
 *   <li>Integrating the AI-powered {@link TaskExtractor} (via Cogniti) to parse
 *       natural-language task descriptions into {@link Task} objects.</li>
 * </ul>
 * </p>
 *
 * <p>All overlays are built once during {@link #initialize()} and toggled
 * visible/invisible rather than being recreated on each open, to preserve their
 * state across interactions.</p>
 */
public class HomeController {

    // ── FXML fields ───────────────────────────────────────────────────────

    /** Horizontal navigation bar at the top of the screen for category buttons. */
    @FXML private HBox      topbarNav;

    /** Label displaying the title of the currently selected category. */
    @FXML private Label     pageTitle;

    /** Label displaying a subtitle / description for the current category. */
    @FXML private Label     pageSubtitle;

    /** Label showing the current date in a human-readable format. */
    @FXML private Label     headerDate;

    /** Vertical container into which individual task cards are rendered. */
    @FXML private VBox      tasksCard;

    /** Root {@link StackPane} used as the host for all modal overlays. */
    @FXML private StackPane rootStack;

    // ── State ─────────────────────────────────────────────────────────────

    /** DAO used to load and persist tasks. */
    private ITaskDAO taskDAO;

    /** DAO used to load, save, and delete custom user-defined categories. */
    private SqliteCategoryDAO categoryDAO;

    /** In-memory cache of all tasks belonging to the current user. */
    private List<Task> allTasks;

    /**
     * Key of the currently active category filter.
     * {@code "all"} shows every task; any other value filters by that category key.
     */
    private String currentCategory = "all";

    /**
     * The task currently displayed in the detail or edit modal, or {@code null}
     * if no task is selected.
     */
    private Task selectedTask = null;

    /** Manages in-memory custom categories for the current session. */
    private final CategoryManager categoryManager = new CategoryManager();

    // ── AI state ──────────────────────────────────────────────────────────

    /**
     * AI component that parses a natural-language string into a {@link Task}.
     * May be {@code null} if the AI service fails to initialise.
     */
    private TaskExtractor taskExtractor;

    /** Cogniti agent ID used to initialise the AI conversation. */
    private static final String AGENT_ID = "6a046b7d369faae92bfcd391";

    /** Bearer token for authenticating with the Cogniti API. */
    private static final String BEARER_TOKEN = Secrets.getBearerToken();

    // ── Overlay StackPanes ────────────────────────────────────────────────

    /** Overlay for manually creating a new task. */
    private StackPane manualOverlay;

    /** Overlay for creating a task via AI natural-language input. */
    private StackPane aiOverlay;

    /** Overlay showing the full details of a selected task. */
    private StackPane detailOverlay;

    /** Overlay for application settings and help information. */
    private StackPane settingsOverlay;

    /** Overlay for editing an existing task's details. */
    private StackPane editOverlay;

    /** Overlay for adding a new custom category with a colour picker. */
    private StackPane addCategoryOverlay;

    // ── Detail modal labels (reused across openings) ──────────────────────

    /** Label showing the selected task's name in the detail modal. */
    private Label  detailNameLbl;

    /** Large label showing the selected task's time in the detail modal. */
    private Label  detailTimeLbl;

    /** Label showing the selected task's date in the detail modal. */
    private Label  detailDateLbl;

    /** Label showing the selected task's description in the detail modal. */
    private Label  detailDescLbl;

    /** Row of badge labels for category, priority, and done status in the detail modal. */
    private HBox   detailBadges;

    /** Button to toggle the selected task's done/incomplete state in the detail modal. */
    private Button detailDoneBtn;

    // ═══════════════════════════════════════════════════════════════════
    // INITIALISE
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Called by the JavaFX runtime after all {@code @FXML} fields are injected.
     * <p>
     * Performs the following in order:
     * <ol>
     *   <li>Initialises the task and category DAOs.</li>
     *   <li>Loads the current user's tasks from the database.</li>
     *   <li>Sets the header date label to today's date.</li>
     *   <li>Attempts to initialise the AI {@link TaskExtractor}; shows a toast on failure.</li>
     *   <li>Builds all modal overlays.</li>
     *   <li>Loads and adds navigation buttons for any previously saved custom categories.</li>
     *   <li>Renders the initial task list.</li>
     * </ol>
     * </p>
     */
    @FXML
    public void initialize() {
        taskDAO     = new SqliteTaskDAO();
        categoryDAO = new SqliteCategoryDAO();
        allTasks    = taskDAO.getTasksByUser(TaskStore.getInstance().getLoggedInUserId());

        headerDate.setText(LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")));

        try {
            taskExtractor = new TaskExtractor(
                    CognitiConversation.initialise(AGENT_ID, BEARER_TOKEN)
            );
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            showToast("AI system failed to start");
        }

        buildOverlays();
        loadSavedCategories();
        renderTasks();
    }

    /**
     * Loads any custom categories persisted for the current user and adds a
     * navigation button to the top bar for each one.
     */
    private void loadSavedCategories() {
        int userId = TaskStore.getInstance().getLoggedInUserId();
        List<CustomCategory> saved = categoryDAO.getCategoriesByUser(userId);
        for (CustomCategory cat : saved) {
            categoryManager.addCategory(cat);
            addNavButton(cat);
        }
    }

    /**
     * Reloads the current user's tasks from the database into {@link #allTasks}.
     * Call this after any add, edit, or delete operation to keep the cache current.
     */
    private void reloadTasks() {
        allTasks = taskDAO.getTasksByUser(TaskStore.getInstance().getLoggedInUserId());
    }

    // ═══════════════════════════════════════════════════════════════════
    // SIDEBAR ACTIONS
    // ═══════════════════════════════════════════════════════════════════

    /** Switches the view to show all tasks. */
    @FXML private void onAllTasks()    { selectCategory("all",      "All Tasks",      "All your tasks"); }

    /** Switches the view to show Work-category tasks. */
    @FXML private void onWork()        { selectCategory("work",     "Work Tasks",     "Tasks in the Work category"); }

    /** Switches the view to show Grocery-category tasks. */
    @FXML private void onGrocery()     { selectCategory("grocery",  "Grocery Tasks",  "Tasks in the Grocery category"); }

    /** Switches the view to show Personal-category tasks. */
    @FXML private void onPersonal()    { selectCategory("personal", "Personal Tasks", "Tasks in the Personal category"); }

    /** Switches the view to show School-category tasks. */
    @FXML private void onSchool()      { selectCategory("school",   "School Tasks",   "Tasks in the School category"); }

    /** Switches the view to show Medical-category tasks. */
    @FXML private void onMedical()     { selectCategory("medical",  "Medical Tasks",  "Tasks in the Medical category"); }

    /** Switches the view to show Social-category tasks. */
    @FXML private void onSocial()      { selectCategory("social",   "Social Tasks",   "Tasks in the Social category"); }

    /** Switches the view to show Fitness-category tasks. */
    @FXML private void onFitness()     { selectCategory("fitness",  "Fitness Tasks",  "Tasks in the Fitness category"); }

    /** Opens the Add Category overlay. */
    @FXML private void onAddCategory() { openOverlay(addCategoryOverlay); }

    /** Opens the Settings overlay. */
    @FXML private void onSettings()    { openOverlay(settingsOverlay); }

    /**
     * Logs the current user out and navigates back to the login screen.
     * Any navigation exception is printed to standard error.
     */
    @FXML
    private void onLogout() {
        try { MainApp.showLogin(); }
        catch (Exception e) { e.printStackTrace(); }
    }

    /** Opens the AI Smart Task overlay. */
    @FXML private void onAddAI()     { openOverlay(aiOverlay); }

    /** Opens the Manual Add Task overlay. */
    @FXML private void onAddManual() { openOverlay(manualOverlay); }

    // ═══════════════════════════════════════════════════════════════════
    // CATEGORY SELECTION
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Updates the active category, refreshes the page title and subtitle,
     * updates navigation highlighting, and re-renders the task list.
     *
     * @param key      the category key used for filtering (e.g. {@code "work"});
     *                 use {@code "all"} to show every task
     * @param title    the human-readable title to display in the page header
     * @param subtitle the descriptive subtitle to display below the title
     */
    private void selectCategory(String key, String title, String subtitle) {
        currentCategory = key;
        pageTitle.setText(title);
        pageSubtitle.setText(subtitle);
        refreshNavHighlights(key);
        renderTasks();
    }

    /**
     * Updates the visual highlight state of navigation buttons to indicate
     * the currently active category.
     * <p><strong>Note:</strong> Currently a no-op stub; highlighting logic
     * is pending implementation.</p>
     *
     * @param activeKey the key of the currently selected category
     */
    private void refreshNavHighlights(String activeKey) {
        // TODO: implement nav button highlight toggling
    }

    // ═══════════════════════════════════════════════════════════════════
    // RENDER TASK LIST
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Clears and rebuilds the task card list for the currently active category.
     * <p>
     * If {@link #currentCategory} is {@code "all"}, every task in {@link #allTasks}
     * is shown. Otherwise, tasks are included if their {@link Task#getTags() tags}
     * contain the category key (case-insensitive) or their
     * {@link Task#getCategory() category} enum name matches it.
     * </p>
     * An empty-state label is shown when no tasks match the current filter.
     */
    private void renderTasks() {
        tasksCard.getChildren().clear();

        List<Task> filtered;
        if (currentCategory.equals("all")) {
            filtered = allTasks;
        } else {
            filtered = allTasks.stream()
                    .filter(t -> {
                        if (t.getTags() != null &&
                                t.getTags().toLowerCase().contains(currentCategory)) return true;
                        if (t.getCategory() != null &&
                                t.getCategory().name().equalsIgnoreCase(currentCategory)) return true;
                        return false;
                    })
                    .collect(Collectors.toList());
        }

        if (filtered.isEmpty()) {
            Label empty = new Label("📋  No tasks here yet. Add one below!");
            empty.setStyle(Styles.emptyState());
            VBox.setMargin(empty, new Insets(60, 0, 0, 0));
            tasksCard.getChildren().add(empty);
            return;
        }

        for (Task task : filtered) {
            tasksCard.getChildren().add(buildTaskCard(task));
        }
    }

    /**
     * Constructs an {@link HBox} card representing a single task for display
     * in the task list.
     * <p>
     * The card includes:
     * <ul>
     *   <li>A checkbox {@link Button} that toggles {@link Task#isDone()} and
     *       persists the change.</li>
     *   <li>An info section showing the task name, category/tag badge, priority
     *       badge, and scheduled date.</li>
     *   <li>A time label derived from {@link Task#getTimeString()} or the start date.</li>
     *   <li>An edit button that opens the edit modal.</li>
     * </ul>
     * Clicking the card itself opens the detail modal via {@link #openDetail(Task)}.
     * </p>
     *
     * @param task the {@link Task} to render; must not be {@code null}
     * @return the constructed {@link HBox} card node
     */
    private HBox buildTaskCard(Task task) {
        HBox card = new HBox(16);
        card.setAlignment(Pos.CENTER_LEFT);

        String cardStyle = task.getPriority() != null
                ? Styles.card(task.getPriority())
                : Styles.card(Task.Priority.MEDIUM);
        card.setStyle(cardStyle);
        HBox.setHgrow(card, Priority.ALWAYS);

        // Checkbox button
        Button check = new Button(task.isDone() ? "✓" : "");
        check.setStyle(Styles.checkCircle(task.isDone()));
        check.setOnAction(e -> {
            task.setDone(!task.isDone());
            if (task.getId() > 0) taskDAO.updateTask(task);
            renderTasks();
            showToast(task.isDone() ? "✅ Task completed!" : "Task marked incomplete");
        });

        // Info section
        VBox info = new VBox(5);
        HBox.setHgrow(info, Priority.ALWAYS);

        String displayName = task.getTitle() != null ? task.getTitle() : task.getName();
        Label name = new Label(displayName);
        name.setStyle(Styles.taskName(task.isDone()));

        HBox meta = new HBox(8);
        meta.setAlignment(Pos.CENTER_LEFT);

        String catLabel = "";
        if (task.getCategory() != null) catLabel = task.getCategoryLabel();
        else if (task.getTags() != null && !task.getTags().isEmpty()) catLabel = task.getTags();
        if (!catLabel.isEmpty()) {
            Label catBadge = new Label(catLabel);
            catBadge.setStyle(Styles.badge("category"));
            meta.getChildren().add(catBadge);
        }

        if (task.getPriority() != null) {
            Label priBadge = new Label("Priority: " + task.getPriorityLabel());
            priBadge.setStyle(Styles.badge(task.getPriority().name().toLowerCase()));
            meta.getChildren().add(priBadge);
        }

        if (task.getDate() != null) {
            Label dateLbl = new Label("📅  " + task.getDate().format(
                    DateTimeFormatter.ofPattern("EEE, d MMM yyyy")));
            dateLbl.setStyle(Styles.taskMeta());
            meta.getChildren().add(dateLbl);
        } else if (task.getStartDate() != null) {
            Label dateLbl = new Label("📅  " + task.getStartDate().format(
                    DateTimeFormatter.ofPattern("EEE, d MMM yyyy HH:mm")));
            dateLbl.setStyle(Styles.taskMeta());
            meta.getChildren().add(dateLbl);
        }

        info.getChildren().addAll(name, meta);

        String timeStr = task.getTimeString();
        if ((timeStr == null || timeStr.isEmpty()) && task.getStartDate() != null)
            timeStr = task.getStartDate().format(DateTimeFormatter.ofPattern("HH:mm"));
        Label timeLbl = new Label(timeStr != null ? timeStr : "");
        timeLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; "
                + "-fx-font-family: 'Segoe UI'; -fx-text-fill: " + Styles.TEXT_MUTED + ";");

        Button editBtn = new Button("✏");
        editBtn.setStyle("-fx-background-color: rgba(74,108,247,0.10); "
                + "-fx-text-fill: #4a6cf7; -fx-background-radius: 10; -fx-cursor: hand;");
        editBtn.setOnAction(e -> {
            e.consume();
            openEditModal(task);
        });

        card.getChildren().addAll(check, info, timeLbl, editBtn);

        final String fs = cardStyle;
        card.setOnMouseEntered(e -> card.setStyle(
                fs.replace("rgba(74,108,247,0.10), 16", "rgba(74,108,247,0.18), 24")));
        card.setOnMouseExited(e  -> card.setStyle(fs));
        card.setOnMouseClicked(e -> openDetail(task));

        // Debug: log task category and tag parsing from Cogniti
        System.out.println("TASK: " + task.getTitle()
                + " CATEGORY: " + task.getCategory()
                + " TAGS: " + task.getTags());

        return card;
    }

    // ═══════════════════════════════════════════════════════════════════
    // BUILD OVERLAYS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Constructs all modal overlays and stores references to them.
     * Called once during {@link #initialize()}.
     */
    private void buildOverlays() {
        manualOverlay      = buildManualModal();
        aiOverlay          = buildAiModal();
        detailOverlay      = buildDetailModal();
        settingsOverlay    = buildSettingsModal();
        editOverlay        = buildEditModal();
        addCategoryOverlay = buildAddCategoryModal();
    }

    /**
     * Makes the given overlay visible, adds it to the root stack if not already
     * present, and plays a scale + fade-in animation on the inner card.
     *
     * @param overlay the {@link StackPane} overlay to open; must not be {@code null}
     */
    private void openOverlay(StackPane overlay) {
        if (!rootStack.getChildren().contains(overlay))
            rootStack.getChildren().add(overlay);
        overlay.setVisible(true);

        var inner = overlay.getChildren().get(0);
        inner.setScaleX(0.88); inner.setScaleY(0.88);
        inner.setOpacity(0);
        ScaleTransition st = new ScaleTransition(Duration.millis(200), inner);
        st.setToX(1); st.setToY(1);
        FadeTransition ft = new FadeTransition(Duration.millis(200), inner);
        ft.setToValue(1);
        new ParallelTransition(st, ft).play();
    }

    /**
     * Hides the given overlay by setting it invisible.
     *
     * @param overlay the {@link StackPane} overlay to close; must not be {@code null}
     */
    private void closeOverlay(StackPane overlay) {
        overlay.setVisible(false);
    }

    /**
     * Wraps a modal card {@link VBox} in a full-screen {@link StackPane} that acts
     * as a dimmed backdrop. Clicking the backdrop (but not the card itself) closes
     * the overlay.
     *
     * @param card the modal content card to wrap; must not be {@code null}
     * @return the backdrop {@link StackPane} with the card as its child,
     *         initially invisible
     */
    private StackPane wrapOverlay(VBox card) {
        StackPane shell = new StackPane(card);
        shell.setStyle(Styles.overlayBg());
        shell.setVisible(false);
        shell.setOnMouseClicked(e -> { if (e.getTarget() == shell) closeOverlay(shell); });
        return shell;
    }

    // ═══════════════════════════════════════════════════════════════════
    // MANUAL ADD MODAL
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Builds the manual task creation modal.
     * <p>
     * The modal collects task name, description, date ({@code yyyy-MM-dd}),
     * time ({@code HH:mm}), category, and priority. Custom categories are appended
     * to the category combo box dynamically when it is opened. On submission, a
     * new {@link Task} is persisted, the task list is reloaded, and a toast is shown.
     * </p>
     *
     * @return the modal wrapped in a backdrop {@link StackPane}
     */
    private StackPane buildManualModal() {
        VBox card = new VBox(14);
        card.setStyle(Styles.modalCard());
        card.setMaxWidth(480);
        card.setMaxHeight(Region.USE_PREF_SIZE);

        HBox header = modalHeader("Add Task");
        Button closeBtn = (Button) header.getChildren().get(1);

        TextField nameField = field("Task name");
        TextArea  descField = area("Task description...");
        TextField dateField = field("Date  (yyyy-MM-dd)");
        TextField timeField = field("Time  (HH:mm)");
        ComboBox<String> catBox = combo("Select Category",
                "Work","Grocery","Personal","School","Medical","Social","Fitness");
        catBox.setOnShowing(e -> {
            java.util.List<String> builtin = java.util.Arrays.asList(
                    "Work","Grocery","Personal","School","Medical","Social","Fitness");
            catBox.getItems().removeIf(item -> !builtin.contains(item));
            categoryManager.getCategoryNames().forEach(name -> {
                if (!catBox.getItems().contains(name)) catBox.getItems().add(name);
            });
        });
        ComboBox<String> priBox = combo("Select Priority","High","Medium","Low");

        HBox row1 = new HBox(14, labeled("Date", dateField), labeled("Time", timeField));
        HBox row2 = new HBox(14, labeled("Category", catBox), labeled("Priority", priBox));
        HBox.setHgrow(((VBox)row1.getChildren().get(0)), Priority.ALWAYS);
        HBox.setHgrow(((VBox)row1.getChildren().get(1)), Priority.ALWAYS);
        HBox.setHgrow(((VBox)row2.getChildren().get(0)), Priority.ALWAYS);
        HBox.setHgrow(((VBox)row2.getChildren().get(1)), Priority.ALWAYS);

        Button submit = new Button("Submit");
        submit.setStyle(Styles.primaryButton());
        submit.setMaxWidth(Double.MAX_VALUE);

        StackPane shell = wrapOverlay(card);
        closeBtn.setOnAction(e -> closeOverlay(shell));

        submit.setOnAction(e -> {
            String taskName = nameField.getText().trim();
            if (taskName.isEmpty()) { showToast("Please enter a task name"); return; }

            LocalDate date = null;
            LocalTime time = null;
            try { if (!dateField.getText().isBlank())
                date = LocalDate.parse(dateField.getText().trim()); }
            catch (DateTimeParseException ignored) { showToast("Date format: yyyy-MM-dd"); return; }
            try { if (!timeField.getText().isBlank())
                time = LocalTime.parse(timeField.getText().trim()); }
            catch (DateTimeParseException ignored) { showToast("Time format: HH:mm"); return; }

            Task.Category cat = null;
            String catTag = null;
            if (catBox.getValue() != null) {
                try {
                    cat = Task.Category.valueOf(catBox.getValue().toUpperCase());
                } catch (Exception ignored) {
                    catTag = catBox.getValue().toLowerCase();
                }
            }

            Task.Priority pri = Task.Priority.MEDIUM;
            if (priBox.getValue() != null)
                try { pri = Task.Priority.valueOf(priBox.getValue().toUpperCase()); }
                catch (Exception ignored) {}

            LocalDate finalDate = date != null ? date : LocalDate.now();
            LocalTime finalTime = time != null ? time : LocalTime.now();
            LocalDateTime startDT = LocalDateTime.of(finalDate, finalTime);

            String tagValue = cat != null ? cat.name().toLowerCase() : (catTag != null ? catTag : "");
            Task newTask = new Task(taskName, startDT, startDT.plusHours(1),
                    descField.getText().trim(), tagValue,
                    TaskStore.getInstance().getLoggedInUserId());
            newTask.setCategory(cat);
            newTask.setTags(tagValue);
            newTask.setPriority(pri);
            newTask.setDate(finalDate);
            newTask.setTime(finalTime);

            taskDAO.addTask(newTask);
            reloadTasks();
            closeOverlay(shell);
            renderTasks();
            showToast("✅ Task added!");
            nameField.clear(); descField.clear();
            dateField.clear(); timeField.clear();
            catBox.setValue(null); priBox.setValue(null);
        });

        card.getChildren().addAll(header,
                labeled("Task Name", nameField),
                labeled("Description", descField),
                row1, row2, submit);
        return shell;
    }

    // ═══════════════════════════════════════════════════════════════════
    // AI SMART TASK MODAL
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Builds the AI Smart Task modal.
     * <p>
     * The user types a natural-language task description (e.g. "Buy milk tomorrow
     * morning"). On submission, the text is passed to {@link TaskExtractor#extract(String)},
     * which uses the Cogniti AI to parse it into a {@link Task}. The task is then
     * assigned the current user's ID, given a default start time if one was not
     * extracted, persisted, and added to the task list.
     * </p>
     * <p>The modal can be submitted with either the Submit button or the Enter key.</p>
     *
     * @return the AI modal wrapped in a backdrop {@link StackPane}
     */
    private StackPane buildAiModal() {
        VBox card = new VBox(14);
        card.setStyle(Styles.modalCard());
        card.setMaxWidth(480);
        card.setMaxHeight(Region.USE_PREF_SIZE);

        HBox header = modalHeader("✨ Add Smart Task");
        Button closeBtn = (Button) header.getChildren().get(1);

        TextField inputField = field("");

        Label hint = new Label("Examples: \n\"Buy milk tomorrow morning as soon as possible!\""
                + "\n\"Call boss next friday.\"");
        hint.setStyle(Styles.aiHint());
        hint.setWrapText(true);
        hint.setMaxWidth(Double.MAX_VALUE);

        Button submit = new Button("Submit");
        submit.setStyle(Styles.primaryButton());
        submit.setMaxWidth(Double.MAX_VALUE);

        StackPane shell = wrapOverlay(card);
        closeBtn.setOnAction(e -> closeOverlay(shell));

        Runnable doSubmit = () -> {
            String raw = inputField.getText().trim();

            if (raw.isEmpty()) {
                showToast("Please describe your task");
                return;
            }

            try {
                Task task = taskExtractor.extract(raw);

                task.setUserId(TaskStore.getInstance().getLoggedInUserId());

                if (task.getStartDate() == null) {
                    LocalDateTime now = LocalDateTime.now();
                    task.setStartDate(now);
                    task.setEndDate(now.plusHours(1));
                }

                taskDAO.addTask(task);
                reloadTasks();

                closeOverlay(shell);
                inputField.clear();
                renderTasks();

                showToast("✨ Smart task added!");

            } catch (Exception e) {
                e.printStackTrace();
                showToast("❌ AI failed to parse task");
            }
        };

        submit.setOnAction(e -> doSubmit.run());
        inputField.setOnAction(e -> doSubmit.run());

        card.getChildren().addAll(header,
                labeled("What do you need to get done?", inputField),
                hint, submit);
        return shell;
    }

    // ═══════════════════════════════════════════════════════════════════
    // TASK DETAIL MODAL
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Builds the task detail modal shell.
     * <p>
     * The modal displays the selected task's name, time, date, description, and
     * status/priority badges. It provides three action buttons:
     * <ul>
     *   <li><strong>Mark as Done / Incomplete</strong> — toggles {@link Task#isDone()}
     *       and persists the change.</li>
     *   <li><strong>Delete</strong> — removes the task from the database and
     *       refreshes the list.</li>
     *   <li><strong>Edit</strong> — closes this modal and opens the edit modal.</li>
     * </ul>
     * The modal's label references ({@link #detailNameLbl}, {@link #detailTimeLbl},
     * etc.) are stored as fields so they can be updated by {@link #openDetail(Task)}.
     * </p>
     *
     * @return the detail modal wrapped in a backdrop {@link StackPane}
     */
    private StackPane buildDetailModal() {
        VBox card = new VBox(12);
        card.setStyle(Styles.modalCard());
        card.setMaxWidth(460);
        card.setMaxHeight(Region.USE_PREF_SIZE);

        HBox header = modalHeader("");
        detailNameLbl = (Label) header.getChildren().get(0);
        detailNameLbl.setStyle(Styles.modalTitle());
        detailNameLbl.setWrapText(true);
        Button closeBtn = (Button) header.getChildren().get(1);

        detailTimeLbl = new Label("—");
        detailTimeLbl.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; "
                + "-fx-font-family: 'Segoe UI'; -fx-text-fill: " + Styles.TEXT + ";");
        detailTimeLbl.setMaxWidth(Double.MAX_VALUE);
        detailTimeLbl.setAlignment(Pos.CENTER);

        detailDateLbl = new Label("—");
        detailDateLbl.setStyle(Styles.mainSubtitle());
        detailDateLbl.setMaxWidth(Double.MAX_VALUE);
        detailDateLbl.setAlignment(Pos.CENTER);

        detailBadges = new HBox(10);
        detailBadges.setAlignment(Pos.CENTER);

        detailDescLbl = new Label();
        detailDescLbl.setStyle(Styles.taskMeta() + " -fx-font-size: 13px;");
        detailDescLbl.setWrapText(true);

        detailDoneBtn = new Button("Mark as Done");
        detailDoneBtn.setStyle(Styles.primaryButton());
        detailDoneBtn.setMaxWidth(Double.MAX_VALUE);

        Button deleteBtn = new Button("🗑 Delete");
        deleteBtn.setStyle(Styles.dangerButton());
        deleteBtn.setMaxWidth(Double.MAX_VALUE);

        Button editBtn = new Button("✏ Edit");
        editBtn.setStyle(Styles.secondaryButton());
        editBtn.setMaxWidth(Double.MAX_VALUE);

        HBox btnRow = new HBox(12, detailDoneBtn, deleteBtn, editBtn);
        HBox.setHgrow(detailDoneBtn, Priority.ALWAYS);
        HBox.setHgrow(deleteBtn, Priority.ALWAYS);
        HBox.setHgrow(editBtn, Priority.ALWAYS);

        StackPane shell = wrapOverlay(card);
        closeBtn.setOnAction(e -> closeOverlay(shell));

        detailDoneBtn.setOnAction(e -> {
            if (selectedTask != null) {
                selectedTask.setDone(!selectedTask.isDone());
                if (selectedTask.getId() > 0) taskDAO.updateTask(selectedTask);
                reloadTasks();
                renderTasks();
                closeOverlay(shell);
                showToast(selectedTask.isDone() ? "✅ Task completed!" : "Task marked incomplete");
            }
        });

        deleteBtn.setOnAction(e -> {
            if (selectedTask != null) {
                if (selectedTask.getId() > 0) taskDAO.deleteTask(selectedTask);
                selectedTask = null;
                reloadTasks();
                renderTasks();
                closeOverlay(shell);
                showToast("🗑 Task deleted");
            }
        });

        editBtn.setOnAction(e -> {
            closeOverlay(shell);
            openEditModal(selectedTask);
        });

        card.getChildren().addAll(header, detailTimeLbl, detailDateLbl,
                detailBadges, detailDescLbl, btnRow);
        return shell;
    }

    /**
     * Populates the detail modal with the given task's data and opens it.
     * <p>
     * Sets {@link #selectedTask}, updates all detail labels and badges, and
     * adjusts the done/incomplete button text to reflect the task's current state.
     * </p>
     *
     * @param task the {@link Task} to display; must not be {@code null}
     */
    private void openDetail(Task task) {
        selectedTask = task;
        String displayName = task.getTitle() != null ? task.getTitle() : task.getName();
        detailNameLbl.setText(displayName);

        String timeStr = task.getTimeString();
        if ((timeStr == null || timeStr.isEmpty()) && task.getStartDate() != null)
            timeStr = task.getStartDate().format(DateTimeFormatter.ofPattern("HH:mm"));
        detailTimeLbl.setText(timeStr != null && !timeStr.isEmpty() ? timeStr : "—");

        if (task.getDate() != null)
            detailDateLbl.setText(task.getDate().format(
                    DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")));
        else if (task.getStartDate() != null)
            detailDateLbl.setText(task.getStartDate().format(
                    DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")));
        else
            detailDateLbl.setText("—");

        detailDescLbl.setText(task.getDescription() == null || task.getDescription().isEmpty()
                ? "No description provided." : task.getDescription());

        detailBadges.getChildren().clear();
        if (task.getCategory() != null) {
            Label cat = new Label(task.getCategoryLabel());
            cat.setStyle(Styles.badge("category"));
            detailBadges.getChildren().add(cat);
        } else if (task.getTags() != null && !task.getTags().isEmpty()) {
            Label cat = new Label(task.getTags());
            cat.setStyle(Styles.badge("category"));
            detailBadges.getChildren().add(cat);
        }
        if (task.getPriority() != null) {
            Label pri = new Label("Priority: " + task.getPriorityLabel());
            pri.setStyle(Styles.badge(task.getPriority().name().toLowerCase()));
            detailBadges.getChildren().add(pri);
        }
        if (task.isDone()) {
            Label done = new Label("✓ Completed");
            done.setStyle(Styles.badge("low"));
            detailBadges.getChildren().add(done);
        }

        detailDoneBtn.setText(task.isDone() ? "Mark as Incomplete" : "Mark as Done");
        openOverlay(detailOverlay);
    }

    // ═══════════════════════════════════════════════════════════════════
    // EDIT TASK MODAL
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Builds the edit task modal shell.
     * <p>
     * The structure mirrors the manual add modal. The card's {@code userData}
     * is set to an array of the six editable controls so that
     * {@link #openEditModal(Task)} can retrieve and pre-populate them without
     * keeping separate field references.
     * </p>
     * On submission, the changes are applied to {@link #selectedTask} and persisted.
     *
     * @return the edit modal wrapped in a backdrop {@link StackPane}
     */
    private StackPane buildEditModal() {
        VBox card = new VBox(14);
        card.setStyle(Styles.modalCard());
        card.setMaxWidth(480);
        card.setMaxHeight(Region.USE_PREF_SIZE);

        HBox header = modalHeader("✏ Edit Task");
        Button closeBtn = (Button) header.getChildren().get(1);

        TextField nameField = field("Task name");
        TextArea  descField = area("Task description...");
        TextField dateField = field("Date  (yyyy-MM-dd)");
        TextField timeField = field("Time  (HH:mm)");
        ComboBox<String> catBox = combo("Select Category",
                "Work","Grocery","Personal","School","Medical","Social","Fitness");
        catBox.setOnShowing(e -> {
            java.util.List<String> builtin = java.util.Arrays.asList(
                    "Work","Grocery","Personal","School","Medical","Social","Fitness");
            catBox.getItems().removeIf(item -> !builtin.contains(item));
            categoryManager.getCategoryNames().forEach(name -> {
                if (!catBox.getItems().contains(name)) catBox.getItems().add(name);
            });
        });
        ComboBox<String> priBox = combo("Select Priority","High","Medium","Low");

        HBox row1 = new HBox(14, labeled("Date", dateField), labeled("Time", timeField));
        HBox row2 = new HBox(14, labeled("Category", catBox), labeled("Priority", priBox));
        HBox.setHgrow(((VBox)row1.getChildren().get(0)), Priority.ALWAYS);
        HBox.setHgrow(((VBox)row1.getChildren().get(1)), Priority.ALWAYS);
        HBox.setHgrow(((VBox)row2.getChildren().get(0)), Priority.ALWAYS);
        HBox.setHgrow(((VBox)row2.getChildren().get(1)), Priority.ALWAYS);

        Button submit = new Button("Save Changes");
        submit.setStyle(Styles.primaryButton());
        submit.setMaxWidth(Double.MAX_VALUE);

        StackPane shell = wrapOverlay(card);
        closeBtn.setOnAction(e -> closeOverlay(shell));

        // Store field references in userData for retrieval by openEditModal()
        card.setUserData(new Object[]{nameField, descField, dateField, timeField, catBox, priBox});

        submit.setOnAction(e -> {
            if (selectedTask == null) return;
            String taskName = nameField.getText().trim();
            if (taskName.isEmpty()) { showToast("Please enter a task name"); return; }

            LocalDate date = null;
            LocalTime time = null;
            try { if (!dateField.getText().isBlank())
                date = LocalDate.parse(dateField.getText().trim()); }
            catch (DateTimeParseException ignored) { showToast("Date format: yyyy-MM-dd"); return; }
            try { if (!timeField.getText().isBlank())
                time = LocalTime.parse(timeField.getText().trim()); }
            catch (DateTimeParseException ignored) { showToast("Time format: HH:mm"); return; }

            Task.Category cat = null;
            String catTag = null;
            if (catBox.getValue() != null) {
                try {
                    cat = Task.Category.valueOf(catBox.getValue().toUpperCase());
                } catch (Exception ignored) {
                    catTag = catBox.getValue().toLowerCase();
                }
            }

            Task.Priority pri = Task.Priority.MEDIUM;
            if (priBox.getValue() != null)
                try { pri = Task.Priority.valueOf(priBox.getValue().toUpperCase()); }
                catch (Exception ignored) {}

            LocalDate finalDate = date != null ? date : LocalDate.now();
            LocalTime finalTime = time != null ? time : LocalTime.now();
            LocalDateTime startDT = LocalDateTime.of(finalDate, finalTime);

            String tagValue = cat != null ? cat.name().toLowerCase() : (catTag != null ? catTag : "");
            selectedTask.setTitle(taskName);
            selectedTask.setDescription(descField.getText().trim());
            selectedTask.setStartDate(startDT);
            selectedTask.setEndDate(startDT.plusHours(1));
            selectedTask.setTags(tagValue);
            selectedTask.setCategory(cat);
            selectedTask.setPriority(pri);
            selectedTask.setDate(finalDate);
            selectedTask.setTime(finalTime);

            if (selectedTask.getId() > 0) taskDAO.updateTask(selectedTask);
            reloadTasks();
            closeOverlay(shell);
            renderTasks();
            showToast("✅ Task updated!");
        });

        card.getChildren().addAll(header,
                labeled("Task Name", nameField),
                labeled("Description", descField),
                row1, row2, submit);
        return shell;
    }

    /**
     * Pre-populates the edit modal fields with the given task's current values
     * and opens the modal.
     * <p>
     * Field references are retrieved from the card's {@code userData} array
     * (set by {@link #buildEditModal()}). The category combo box is also updated
     * with any custom categories from {@link #categoryManager} before opening.
     * </p>
     *
     * @param task the {@link Task} to edit; must not be {@code null}
     */
    private void openEditModal(Task task) {
        selectedTask = task;

        VBox card = (VBox) editOverlay.getChildren().get(0);
        Object[] fields = (Object[]) card.getUserData();
        TextField        editNameField = (TextField)        fields[0];
        TextArea         editDescField = (TextArea)         fields[1];
        TextField        editDateField = (TextField)        fields[2];
        TextField        editTimeField = (TextField)        fields[3];
        ComboBox<String> editCatBox    = (ComboBox<String>) fields[4];
        ComboBox<String> editPriBox    = (ComboBox<String>) fields[5];

        editNameField.setText(task.getTitle() != null ? task.getTitle() : "");
        editDescField.setText(task.getDescription() != null ? task.getDescription() : "");

        if (task.getDate() != null)
            editDateField.setText(task.getDate().toString());
        else if (task.getStartDate() != null)
            editDateField.setText(task.getStartDate().toLocalDate().toString());
        else
            editDateField.clear();

        if (task.getTime() != null)
            editTimeField.setText(task.getTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        else if (task.getStartDate() != null)
            editTimeField.setText(task.getStartDate().format(DateTimeFormatter.ofPattern("HH:mm")));
        else
            editTimeField.clear();

        java.util.List<String> builtinCats = java.util.Arrays.asList(
                "Work","Grocery","Personal","School","Medical","Social","Fitness");
        editCatBox.getItems().removeIf(item -> !builtinCats.contains(item));
        categoryManager.getCategoryNames().forEach(name -> {
            if (!editCatBox.getItems().contains(name)) editCatBox.getItems().add(name);
        });

        if (task.getCategory() != null)
            editCatBox.setValue(task.getCategoryLabel());
        else if (task.getTags() != null && !task.getTags().isEmpty())
            editCatBox.setValue(task.getTags().substring(0, 1).toUpperCase()
                    + task.getTags().substring(1).toLowerCase());
        else
            editCatBox.setValue(null);

        if (task.getPriority() != null)
            editPriBox.setValue(task.getPriorityLabel());
        else
            editPriBox.setValue(null);

        openOverlay(editOverlay);
    }

    // ═══════════════════════════════════════════════════════════════════
    // ADD CATEGORY MODAL
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Builds the Add Category modal with a name field, a 16-colour palette picker,
     * and a live chip preview.
     * <p>
     * On submission, a {@link CustomCategory} is created and validated for
     * duplicates (case-insensitive key check). If unique, it is added to
     * {@link #categoryManager}, persisted to the database, and a navigation
     * button is appended to the top bar. The modal resets its state after
     * a successful submission.
     * </p>
     *
     * @return the add-category modal wrapped in a backdrop {@link StackPane}
     */
    private StackPane buildAddCategoryModal() {
        VBox card = new VBox(16);
        card.setStyle(Styles.modalCard());
        card.setMaxWidth(460);
        card.setMaxHeight(Region.USE_PREF_SIZE);

        HBox header = modalHeader("🏷+ Add Category");
        Button closeBtn = (Button) header.getChildren().get(1);

        TextField nameField = field("e.g. Travel, Hobbies, Finance…");

        String[] palette = {
                "#FF6B6B", "#FF9F43", "#F7C59F", "#FFD93D",
                "#6BCB77", "#4D9DE0", "#A3CFF5", "#845EC2",
                "#D65DB1", "#FF6F91", "#C4B5FD", "#6EE7B7",
                "#FCA5A5", "#93C5FD", "#FDBA74", "#A3A3A3"
        };

        final String[] selectedColour = { palette[0] };
        final javafx.scene.shape.Rectangle[] selectedRect = { null };

        Label paletteLabel = new Label("PICK A COLOUR");
        paletteLabel.setStyle(Styles.formLabel());

        javafx.scene.layout.GridPane colourGrid = new javafx.scene.layout.GridPane();
        colourGrid.setHgap(10);
        colourGrid.setVgap(10);

        for (int i = 0; i < palette.length; i++) {
            String hex = palette[i];
            javafx.scene.shape.Rectangle swatch = new javafx.scene.shape.Rectangle(36, 36);
            swatch.setArcWidth(10);
            swatch.setArcHeight(10);
            swatch.setFill(javafx.scene.paint.Color.web(hex));
            swatch.setStyle("-fx-cursor: hand;");

            if (i == 0) {
                swatch.setStroke(javafx.scene.paint.Color.web("#1e2a4a"));
                swatch.setStrokeWidth(3);
                selectedRect[0] = swatch;
            } else {
                swatch.setStroke(javafx.scene.paint.Color.TRANSPARENT);
                swatch.setStrokeWidth(2);
            }

            swatch.setOnMouseClicked(e -> {
                if (selectedRect[0] != null)
                    selectedRect[0].setStroke(javafx.scene.paint.Color.TRANSPARENT);
                swatch.setStroke(javafx.scene.paint.Color.web("#1e2a4a"));
                swatch.setStrokeWidth(3);
                selectedRect[0] = swatch;
                selectedColour[0] = hex;
            });

            colourGrid.add(swatch, i % 4, i / 4);
        }

        Label previewLabel = new Label("PREVIEW");
        previewLabel.setStyle(Styles.formLabel());

        Label previewChip = new Label("Category Name");
        String chipBase = "-fx-background-radius: 10; -fx-padding: 8 16; "
                + "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: black;";
        previewChip.setStyle(chipBase + "-fx-background-color: " + selectedColour[0] + ";");

        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            previewChip.setText(newVal.isBlank() ? "Category Name" : newVal);
        });

        colourGrid.setOnMouseClicked(e ->
                previewChip.setStyle(chipBase + "-fx-background-color: " + selectedColour[0] + ";"));

        Button submit = new Button("Add Category");
        submit.setStyle(Styles.primaryButton());
        submit.setMaxWidth(Double.MAX_VALUE);

        StackPane shell = wrapOverlay(card);
        closeBtn.setOnAction(e -> closeOverlay(shell));

        submit.setOnAction(e -> {
            String catName = nameField.getText().trim();
            if (catName.isEmpty()) { showToast("Please enter a category name"); return; }

            CustomCategory newCat = new CustomCategory(catName, selectedColour[0]);

            boolean duplicate = categoryManager.getCategories().stream()
                    .anyMatch(c -> c.getKey().equals(newCat.getKey()));
            if (duplicate) { showToast("Category \"" + catName + "\" already exists"); return; }

            categoryManager.addCategory(newCat);
            categoryDAO.saveCategory(TaskStore.getInstance().getLoggedInUserId(), newCat);
            addNavButton(newCat);
            closeOverlay(shell);
            showToast("✅ Category \"" + catName + "\" added!");

            // Reset modal state
            nameField.clear();
            selectedColour[0] = palette[0];
            if (selectedRect[0] != null) selectedRect[0].setStroke(javafx.scene.paint.Color.TRANSPARENT);
            javafx.scene.shape.Rectangle firstSwatch =
                    (javafx.scene.shape.Rectangle) colourGrid.getChildren().get(0);
            firstSwatch.setStroke(javafx.scene.paint.Color.web("#1e2a4a"));
            firstSwatch.setStrokeWidth(3);
            selectedRect[0] = firstSwatch;
            previewChip.setText("Category Name");
            previewChip.setStyle(chipBase + "-fx-background-color: " + palette[0] + ";");
        });

        card.getChildren().addAll(
                header,
                labeled("Category Name", nameField),
                paletteLabel, colourGrid,
                previewLabel, previewChip,
                submit);
        return shell;
    }

    /**
     * Creates a styled navigation {@link Button} for the given custom category
     * and appends it to {@link #topbarNav}.
     * <p>
     * The button's text colour is automatically set to white or black based on
     * the perceived luminance of the category's background colour, to ensure
     * readable contrast.
     * </p>
     *
     * @param cat the {@link CustomCategory} for which to create the button;
     *            must not be {@code null}
     */
    private void addNavButton(CustomCategory cat) {
        javafx.scene.paint.Color fill = javafx.scene.paint.Color.web(cat.getColour());
        double luminance = 0.2126 * fill.getRed() + 0.7152 * fill.getGreen() + 0.0722 * fill.getBlue();
        String textFill = luminance < 0.45 ? "white" : "black";

        String baseStyle = "-fx-background-color: " + cat.getColour() + "; "
                + "-fx-text-fill: " + textFill + "; "
                + "-fx-padding: 8 16; "
                + "-fx-background-radius: 10; "
                + "-fx-font-size: 20px; "
                + "-fx-font-weight: bold;";

        Button btn = new Button(cat.getName());
        btn.setStyle(baseStyle);
        btn.setOnAction(e -> selectCategory(
                cat.getKey(),
                cat.getName() + " Tasks",
                "Tasks in the " + cat.getName() + " category"));

        topbarNav.getChildren().add(btn);
    }

    // ═══════════════════════════════════════════════════════════════════
    // SETTINGS MODAL
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Builds the Settings / Help modal.
     * <p>
     * Displays three read-only sections:
     * <ul>
     *   <li><strong>Account</strong> — shows the currently logged-in user's name.</li>
     *   <li><strong>Help</strong> — brief usage hints.</li>
     *   <li><strong>About</strong> — application version information.</li>
     * </ul>
     * </p>
     *
     * @return the settings modal wrapped in a backdrop {@link StackPane}
     */
    private StackPane buildSettingsModal() {
        VBox card = new VBox(14);
        card.setStyle(Styles.modalCard());
        card.setMaxWidth(420);
        card.setMaxHeight(Region.USE_PREF_SIZE);

        HBox header = modalHeader("⚙️ Help & Settings");
        Button closeBtn = (Button) header.getChildren().get(1);

        Label accLabel = new Label("ACCOUNT");
        accLabel.setStyle(Styles.formLabel());
        Label userRow = new Label("Logged in as:   " + TaskStore.getInstance().getLoggedInUser());
        userRow.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13px; "
                + "-fx-text-fill: " + Styles.TEXT + ";");

        Label helpLabel = new Label("HELP");
        helpLabel.setStyle(Styles.formLabel());
        Label h1 = new Label("📖  How to add tasks");
        Label h2 = new Label("🤖  Using Smart Tasks — type naturally with #tags");
        Label h3 = new Label("📞  Contact Support");
        for (Label l : new Label[]{h1, h2, h3})
            l.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13px; "
                    + "-fx-text-fill: " + Styles.TEXT_MUTED + ";");

        Label aboutLabel = new Label("ABOUT");
        aboutLabel.setStyle(Styles.formLabel());
        Label version = new Label("Version 1.0.0  –  CAB302 Assignment");
        version.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13px; "
                + "-fx-text-fill: " + Styles.TEXT_MUTED + ";");

        Separator sep1 = new Separator();
        Separator sep2 = new Separator();

        StackPane shell = wrapOverlay(card);
        closeBtn.setOnAction(e -> closeOverlay(shell));

        card.getChildren().addAll(header, accLabel, userRow, sep1,
                helpLabel, h1, h2, h3, sep2, aboutLabel, version);
        return shell;
    }

    // ═══════════════════════════════════════════════════════════════════
    // SHARED UI HELPERS
    // ═══════════════════════════════════════════════════════════════════

    /**
     * Creates a standard modal header row containing a title label and a close (✕) button.
     * The title label grows to fill available horizontal space.
     *
     * @param titleText the text to display as the modal title
     * @return an {@link HBox} containing the title and close button
     */
    private HBox modalHeader(String titleText) {
        Label title = new Label(titleText);
        title.setStyle(Styles.modalTitle());
        HBox.setHgrow(title, Priority.ALWAYS);

        Button close = new Button("✕");
        close.setStyle(Styles.closeButton());

        HBox header = new HBox(title, close);
        header.setAlignment(Pos.CENTER_LEFT);
        VBox.setMargin(header, new Insets(0, 0, 6, 0));
        return header;
    }

    /**
     * Creates a styled single-line {@link TextField} with a focus listener that
     * swaps between the default and focused input styles.
     *
     * @param prompt the placeholder text shown when the field is empty
     * @return the configured {@link TextField}
     */
    private TextField field(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setStyle(Styles.formInput());
        f.setMaxWidth(Double.MAX_VALUE);
        f.focusedProperty().addListener((obs, o, n) ->
                f.setStyle(n ? Styles.formInputFocused() : Styles.formInput()));
        return f;
    }

    /**
     * Creates a styled multi-line {@link TextArea} with word wrapping and a
     * default height of three rows.
     *
     * @param prompt the placeholder text shown when the area is empty
     * @return the configured {@link TextArea}
     */
    private TextArea area(String prompt) {
        TextArea a = new TextArea();
        a.setPromptText(prompt);
        a.setStyle(Styles.formInput());
        a.setPrefRowCount(3);
        a.setWrapText(true);
        a.setMaxWidth(Double.MAX_VALUE);
        return a;
    }

    /**
     * Creates a styled {@link ComboBox} pre-populated with the given items and
     * configured to fill its container width.
     *
     * @param prompt the placeholder text shown when no item is selected
     * @param items  the initial items to populate the combo box with
     * @return the configured {@link ComboBox}
     */
    private ComboBox<String> combo(String prompt, String... items) {
        ComboBox<String> cb = new ComboBox<>();
        cb.getItems().addAll(items);
        cb.setPromptText(prompt);
        cb.setMaxWidth(Double.MAX_VALUE);
        cb.setStyle(Styles.formInput());
        return cb;
    }

    /**
     * Wraps a form control in a {@link VBox} with an uppercase label above it.
     * The container grows horizontally to fill available space.
     *
     * @param labelText the label text to display above the control (will be uppercased)
     * @param field     the JavaFX {@link javafx.scene.Node} to place below the label
     * @return a {@link VBox} containing the label and field
     */
    private VBox labeled(String labelText, javafx.scene.Node field) {
        Label lbl = new Label(labelText.toUpperCase());
        lbl.setStyle(Styles.formLabel());
        VBox box = new VBox(5, lbl, field);
        box.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    /**
     * Displays a brief toast notification in the bottom-right corner of the screen.
     * <p>
     * The toast fades in over 250 ms, stays visible for approximately 2.4 seconds,
     * then fades out over 400 ms before being removed from the scene graph.
     * </p>
     *
     * @param message the message to display in the toast; must not be {@code null}
     */
    private void showToast(String message) {
        Label toast = new Label(message);
        toast.setStyle(Styles.toastStyle());
        StackPane.setAlignment(toast, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(toast, new Insets(0, 32, 32, 0));
        rootStack.getChildren().add(toast);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), toast);
        fadeIn.setFromValue(0); fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), toast);
        fadeOut.setFromValue(1); fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(2.4));
        fadeOut.setOnFinished(e -> rootStack.getChildren().remove(toast));

        new SequentialTransition(fadeIn, fadeOut).play();
    }
}