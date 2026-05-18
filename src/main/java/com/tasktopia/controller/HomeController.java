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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HomeController {

    // ── FXML ─────────────────────────────────────────────────
    @FXML private HBox      topbarNav;
    @FXML private Label     pageTitle;
    @FXML private Label     pageSubtitle;
    @FXML private Label     headerDate;
    @FXML private VBox      tasksCard;
    @FXML private StackPane rootStack;

    // ── State ─────────────────────────────────────────────────
    private ITaskDAO taskDAO;
    private SqliteCategoryDAO categoryDAO;
    private List<Task> allTasks;
    private String currentCategory = "all";
    private Task   selectedTask    = null;
    private final CategoryManager categoryManager = new CategoryManager();

    // Overlays
    private StackPane manualOverlay;
    private StackPane aiOverlay;
    private StackPane detailOverlay;
    private StackPane settingsOverlay;
    private StackPane editOverlay;
    private StackPane addCategoryOverlay;

    // Detail modal labels
    private Label  detailNameLbl;
    private Label  detailTimeLbl;
    private Label  detailDateLbl;
    private Label  detailDescLbl;
    private HBox   detailBadges;
    private Button detailDoneBtn;

    // ── Initialise ────────────────────────────────────────────
    @FXML
    public void initialize() {
        taskDAO    = new SqliteTaskDAO();
        categoryDAO = new SqliteCategoryDAO();
        allTasks   = taskDAO.getTasksByUser(TaskStore.getInstance().getLoggedInUserId());

        headerDate.setText(LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")));

        buildOverlays();
        loadSavedCategories();   // restore persisted custom categories
        renderTasks();
    }

    // ── Load persisted custom categories for this user ────────
    private void loadSavedCategories() {
        int userId = TaskStore.getInstance().getLoggedInUserId();
        List<CustomCategory> saved = categoryDAO.getCategoriesByUser(userId);
        for (CustomCategory cat : saved) {
            categoryManager.addCategory(cat);
            addNavButton(cat);
        }
    }

    // ── Reload tasks from DB ──────────────────────────────────
    private void reloadTasks() {
        allTasks = taskDAO.getTasksByUser(TaskStore.getInstance().getLoggedInUserId());
    }

    // ══════════════════════════════════════════════════════════
    //  SIDEBAR ACTIONS
    // ══════════════════════════════════════════════════════════
    @FXML private void onAllTasks()    { selectCategory("all",      "All Tasks",      "All your tasks"); }
    @FXML private void onWork()        { selectCategory("work",     "Work Tasks",     "Tasks in the Work category"); }
    @FXML private void onGrocery()     { selectCategory("grocery",  "Grocery Tasks",  "Tasks in the Grocery category"); }
    @FXML private void onPersonal()    { selectCategory("personal", "Personal Tasks", "Tasks in the Personal category"); }
    @FXML private void onSchool()      { selectCategory("school",   "School Tasks",   "Tasks in the School category"); }
    @FXML private void onMedical()     { selectCategory("medical",  "Medical Tasks",  "Tasks in the Medical category"); }
    @FXML private void onSocial()      { selectCategory("social",   "Social Tasks",   "Tasks in the Social category"); }
    @FXML private void onFitness()     { selectCategory("fitness",  "Fitness Tasks",  "Tasks in the Fitness category"); }
    @FXML private void onAddCategory() { openOverlay(addCategoryOverlay); }
    @FXML private void onSettings()    { openOverlay(settingsOverlay); }

    @FXML
    private void onLogout() {
        try { MainApp.showLogin(); }
        catch (Exception e) { e.printStackTrace(); }
    }

    @FXML private void onAddAI()     { openOverlay(aiOverlay); }
    @FXML private void onAddManual() { openOverlay(manualOverlay); }

    // ══════════════════════════════════════════════════════════
    //  CATEGORY SELECTION
    // ══════════════════════════════════════════════════════════
    private void selectCategory(String key, String title, String subtitle) {
        currentCategory = key;
        pageTitle.setText(title);
        pageSubtitle.setText(subtitle);
        refreshNavHighlights(key);
        renderTasks();
    }

    private void refreshNavHighlights(String activeKey) {
    }

    // ══════════════════════════════════════════════════════════
    //  RENDER TASK LIST
    // ══════════════════════════════════════════════════════════
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

    // ── Build a single task card ──────────────────────────────
    private HBox buildTaskCard(Task task) {
        HBox card = new HBox(16);
        card.setAlignment(Pos.CENTER_LEFT);

        String cardStyle = task.getPriority() != null
                ? Styles.card(task.getPriority())
                : Styles.card(Task.Priority.MEDIUM);
        card.setStyle(cardStyle);
        HBox.setHgrow(card, Priority.ALWAYS);

        // Checkbox
        Button check = new Button(task.isDone() ? "✓" : "");
        check.setStyle(Styles.checkCircle(task.isDone()));
        check.setOnAction(e -> {
            task.setDone(!task.isDone());
            if (task.getId() > 0) taskDAO.updateTask(task);
            renderTasks();
            showToast(task.isDone() ? "✅ Task completed!" : "Task marked incomplete");
        });

        // Info
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

        return card;
    }

    // ══════════════════════════════════════════════════════════
    //  BUILD OVERLAYS
    // ══════════════════════════════════════════════════════════
    private void buildOverlays() {
        manualOverlay       = buildManualModal();
        aiOverlay           = buildAiModal();
        detailOverlay       = buildDetailModal();
        settingsOverlay     = buildSettingsModal();
        editOverlay         = buildEditModal();
        addCategoryOverlay  = buildAddCategoryModal();
    }

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

    private void closeOverlay(StackPane overlay) {
        overlay.setVisible(false);
    }

    private StackPane wrapOverlay(VBox card) {
        StackPane shell = new StackPane(card);
        shell.setStyle(Styles.overlayBg());
        shell.setVisible(false);
        shell.setOnMouseClicked(e -> { if (e.getTarget() == shell) closeOverlay(shell); });
        return shell;
    }

    // ══════════════════════════════════════════════════════════
    //  MANUAL ADD MODAL
    // ══════════════════════════════════════════════════════════
    private StackPane buildManualModal() {
        VBox card = new VBox(14);
        card.setStyle(Styles.modalCard());
        card.setMaxWidth(480);
        card.setMaxHeight(Region.USE_PREF_SIZE);

        HBox header = modalHeader("Add Task");
        Button closeBtn = (Button) header.getChildren().get(1);

        TextField nameField  = field("Task name");
        TextArea  descField  = area("Task description...");
        TextField dateField  = field("Date  (yyyy-MM-dd)");
        TextField timeField  = field("Time  (HH:mm)");
        ComboBox<String> catBox = combo("Select Category",
                "Work","Grocery","Personal","School","Medical","Social","Fitness");
        // Dynamically add any user-created categories when the dropdown opens
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
                    // Custom category — store as a tag instead
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
            newTask.setCategory(cat);   // null is fine — means custom category
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

    // ══════════════════════════════════════════════════════════
    //  AI SMART TASK MODAL
    // ══════════════════════════════════════════════════════════
    private StackPane buildAiModal() {
        VBox card = new VBox(14);
        card.setStyle(Styles.modalCard());
        card.setMaxWidth(480);
        card.setMaxHeight(Region.USE_PREF_SIZE);

        HBox header = modalHeader("✨  Add Smart Task");
        Button closeBtn = (Button) header.getChildren().get(1);

        TextField inputField = field("e.g. Call boss next tuesday #work #high");

        Label hint = new Label(
                "Use #category and #priority tags with a natural date.\n" +
                        "Example:  \"Buy milk tomorrow at 3pm #grocery #medium\"");
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
            if (raw.isEmpty()) { showToast("Please describe your task"); return; }

            String taskName = raw.replaceAll("#\\w+", "").trim();
            String lower = raw.toLowerCase();

            Task.Category cat = Task.Category.PERSONAL;
            if      (lower.contains("#work"))     cat = Task.Category.WORK;
            else if (lower.contains("#grocery"))  cat = Task.Category.GROCERY;
            else if (lower.contains("#school"))   cat = Task.Category.SCHOOL;
            else if (lower.contains("#medical"))  cat = Task.Category.MEDICAL;
            else if (lower.contains("#social"))   cat = Task.Category.SOCIAL;
            else if (lower.contains("#fitness"))  cat = Task.Category.FITNESS;

            Task.Priority pri = Task.Priority.MEDIUM;
            if      (lower.contains("#high")) pri = Task.Priority.HIGH;
            else if (lower.contains("#low"))  pri = Task.Priority.LOW;

            LocalDate date = null;
            if      (lower.contains("tomorrow")) date = LocalDate.now().plusDays(1);
            else if (lower.contains("today"))    date = LocalDate.now();
            else {
                String[] days = {"sunday","monday","tuesday","wednesday","thursday","friday","saturday"};
                for (int i = 0; i < days.length; i++) {
                    if (lower.contains(days[i])) {
                        int diff = (i - LocalDate.now().getDayOfWeek().getValue() % 7 + 7) % 7;
                        date = LocalDate.now().plusDays(diff == 0 ? 7 : diff);
                        break;
                    }
                }
            }

            LocalTime time = null;
            Matcher m = Pattern.compile("(\\d{1,2})(?::(\\d{2}))?\\s*(am|pm)?",
                    Pattern.CASE_INSENSITIVE).matcher(raw);
            if (m.find()) {
                try {
                    int h   = Integer.parseInt(m.group(1));
                    int min = m.group(2) != null ? Integer.parseInt(m.group(2)) : 0;
                    if ("pm".equalsIgnoreCase(m.group(3)) && h < 12) h += 12;
                    if ("am".equalsIgnoreCase(m.group(3)) && h == 12) h = 0;
                    time = LocalTime.of(h, min);
                } catch (Exception ignored) {}
            }

            LocalDate finalDate = date != null ? date : LocalDate.now();
            LocalTime finalTime = time != null ? time : LocalTime.now();
            LocalDateTime startDT = LocalDateTime.of(finalDate, finalTime);

            Task newTask = new Task(taskName, startDT, startDT.plusHours(1),
                    "", cat.name().toLowerCase(),
                    TaskStore.getInstance().getLoggedInUserId());
            newTask.setCategory(cat);
            newTask.setPriority(pri);
            newTask.setDate(finalDate);
            newTask.setTime(finalTime);

            taskDAO.addTask(newTask);
            reloadTasks();
            closeOverlay(shell);
            inputField.clear();
            renderTasks();
            showToast("✨ Smart task added!");
        };

        submit.setOnAction(e -> doSubmit.run());
        inputField.setOnAction(e -> doSubmit.run());

        card.getChildren().addAll(header,
                labeled("What do you need to get done?", inputField),
                hint, submit);
        return shell;
    }

    // ══════════════════════════════════════════════════════════
    //  TASK DETAIL MODAL
    // ══════════════════════════════════════════════════════════
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

    // ══════════════════════════════════════════════════════════
    //  EDIT TASK MODAL
    // ══════════════════════════════════════════════════════════
    private StackPane buildEditModal() {
        VBox card = new VBox(14);
        card.setStyle(Styles.modalCard());
        card.setMaxWidth(480);
        card.setMaxHeight(Region.USE_PREF_SIZE);

        HBox header = modalHeader("✏ Edit Task");
        Button closeBtn = (Button) header.getChildren().get(1);

        TextField nameField  = field("Task name");
        TextArea  descField  = area("Task description...");
        TextField dateField  = field("Date  (yyyy-MM-dd)");
        TextField timeField  = field("Time  (HH:mm)");
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
                    // Custom category — store as a tag
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
            selectedTask.setCategory(cat);   // null means custom category
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

    private void openEditModal(Task task) {
        selectedTask = task;

        VBox card = (VBox) editOverlay.getChildren().get(0);
        Object[] fields = (Object[]) card.getUserData();
        TextField editNameField     = (TextField)        fields[0];
        TextArea  editDescField     = (TextArea)         fields[1];
        TextField editDateField     = (TextField)        fields[2];
        TextField editTimeField     = (TextField)        fields[3];
        ComboBox<String> editCatBox = (ComboBox<String>) fields[4];
        ComboBox<String> editPriBox = (ComboBox<String>) fields[5];

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

    // ══════════════════════════════════════════════════════════
    //  SETTINGS MODAL
    // ══════════════════════════════════════════════════════════
    // ══════════════════════════════════════════════════════════
    //  ADD CATEGORY MODAL
    // ══════════════════════════════════════════════════════════
    private StackPane buildAddCategoryModal() {
        VBox card = new VBox(16);
        card.setStyle(Styles.modalCard());
        card.setMaxWidth(460);
        card.setMaxHeight(Region.USE_PREF_SIZE);

        HBox header = modalHeader("🏷+ Add Category");
        Button closeBtn = (Button) header.getChildren().get(1);

        // ── Category name field ──────────────────────────────
        TextField nameField = field("e.g. Travel, Hobbies, Finance…");

        // ── Colour palette ───────────────────────────────────
        // 16 hand-picked colours that look good as nav-bar chips
        String[] palette = {
                "#FF6B6B", "#FF9F43", "#F7C59F", "#FFD93D",
                "#6BCB77", "#4D9DE0", "#A3CFF5", "#845EC2",
                "#D65DB1", "#FF6F91", "#C4B5FD", "#6EE7B7",
                "#FCA5A5", "#93C5FD", "#FDBA74", "#A3A3A3"
        };

        // Track which colour swatch is selected
        final String[] selectedColour = { palette[0] };
        final javafx.scene.shape.Rectangle[] selectedRect = { null };

        Label paletteLabel = new Label("PICK A COLOUR");
        paletteLabel.setStyle(Styles.formLabel());

        // Build a 4-column grid of colour swatches
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

            // Mark first swatch as pre-selected
            if (i == 0) {
                swatch.setStroke(javafx.scene.paint.Color.web("#1e2a4a"));
                swatch.setStrokeWidth(3);
                selectedRect[0] = swatch;
            } else {
                swatch.setStroke(javafx.scene.paint.Color.TRANSPARENT);
                swatch.setStrokeWidth(2);
            }

            swatch.setOnMouseClicked(e -> {
                // Deselect previous
                if (selectedRect[0] != null) {
                    selectedRect[0].setStroke(javafx.scene.paint.Color.TRANSPARENT);
                }
                // Select this one
                swatch.setStroke(javafx.scene.paint.Color.web("#1e2a4a"));
                swatch.setStrokeWidth(3);
                selectedRect[0] = swatch;
                selectedColour[0] = hex;
            });

            colourGrid.add(swatch, i % 4, i / 4);
        }

        // ── Live preview chip ────────────────────────────────
        Label previewLabel = new Label("PREVIEW");
        previewLabel.setStyle(Styles.formLabel());

        Label previewChip = new Label("Category Name");
        String chipBase = "-fx-background-radius: 10; -fx-padding: 8 16; "
                + "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: black;";
        previewChip.setStyle(chipBase + "-fx-background-color: " + selectedColour[0] + ";");

        // Update preview as user types
        nameField.textProperty().addListener((obs, oldVal, newVal) -> {
            String display = newVal.isBlank() ? "Category Name" : newVal;
            previewChip.setText(display);
        });

        // Update preview colour when swatch is clicked
        colourGrid.setOnMouseClicked(e -> {
            previewChip.setStyle(chipBase + "-fx-background-color: " + selectedColour[0] + ";");
        });

        // ── Submit button ─────────────────────────────────────
        Button submit = new Button("Add Category");
        submit.setStyle(Styles.primaryButton());
        submit.setMaxWidth(Double.MAX_VALUE);

        StackPane shell = wrapOverlay(card);
        closeBtn.setOnAction(e -> closeOverlay(shell));

        submit.setOnAction(e -> {
            String catName = nameField.getText().trim();
            if (catName.isEmpty()) {
                showToast("Please enter a category name");
                return;
            }

            CustomCategory newCat = new CustomCategory(catName, selectedColour[0]);

            // Prevent exact duplicates
            boolean duplicate = categoryManager.getCategories().stream()
                    .anyMatch(c -> c.getKey().equals(newCat.getKey()));
            if (duplicate) {
                showToast("Category \"" + catName + "\" already exists");
                return;
            }

            categoryManager.addCategory(newCat);
            categoryDAO.saveCategory(TaskStore.getInstance().getLoggedInUserId(), newCat);
            addNavButton(newCat);

            closeOverlay(shell);
            showToast("✅ Category \"" + catName + "\" added!");

            // Reset form
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
     * Dynamically creates and inserts a nav button for a custom category
     * into the topbarNav HBox (before the end, after built-in buttons).
     */
    private void addNavButton(CustomCategory cat) {
        // Decide text colour: use white for dark backgrounds, black for light ones
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

    // ══════════════════════════════════════════════════════════
    //  SHARED HELPERS
    // ══════════════════════════════════════════════════════════
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

    private TextField field(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setStyle(Styles.formInput());
        f.setMaxWidth(Double.MAX_VALUE);
        f.focusedProperty().addListener((obs, o, n) ->
                f.setStyle(n ? Styles.formInputFocused() : Styles.formInput()));
        return f;
    }

    private TextArea area(String prompt) {
        TextArea a = new TextArea();
        a.setPromptText(prompt);
        a.setStyle(Styles.formInput());
        a.setPrefRowCount(3);
        a.setWrapText(true);
        a.setMaxWidth(Double.MAX_VALUE);
        return a;
    }

    private ComboBox<String> combo(String prompt, String... items) {
        ComboBox<String> cb = new ComboBox<>();
        cb.getItems().addAll(items);
        cb.setPromptText(prompt);
        cb.setMaxWidth(Double.MAX_VALUE);
        cb.setStyle(Styles.formInput());
        return cb;
    }

    private VBox labeled(String labelText, javafx.scene.Node field) {
        Label lbl = new Label(labelText.toUpperCase());
        lbl.setStyle(Styles.formLabel());
        VBox box = new VBox(5, lbl, field);
        box.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(box, Priority.ALWAYS);
        return box;
    }

    // ── Toast notification ────────────────────────────────────
    private void showToast(String message) {
        Label toast = new Label(message);
        toast.setStyle(Styles.toastStyle());
        StackPane.setAlignment(toast, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(toast, new Insets(0, 32, 32, 0));
        rootStack.getChildren().add(toast);

        FadeTransition fadeIn  = new FadeTransition(Duration.millis(250), toast);
        fadeIn.setFromValue(0); fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), toast);
        fadeOut.setFromValue(1); fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(2.4));
        fadeOut.setOnFinished(e -> rootStack.getChildren().remove(toast));

        new SequentialTransition(fadeIn, fadeOut).play();
    }
}