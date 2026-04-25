package com.tasktopia.controller;

import com.tasktopia.MainApp;
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
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HomeController {

    // ── FXML ─────────────────────────────────────────────────
    @FXML private HBox      topbarNav;
    @FXML private Label     pageTitle;
    @FXML private Label     pageSubtitle;
    @FXML private Label     headerDate;
    @FXML private VBox      tasksCard;
    @FXML private StackPane rootStack;

    // ── State ─────────────────────────────────────────────────
    private String currentCategory = "all";
    private Task   selectedTask    = null;

    // Overlays
    private StackPane manualOverlay;
    private StackPane aiOverlay;
    private StackPane detailOverlay;
    private StackPane settingsOverlay;

    // Detail modal labels (need updating when task selected)
    private Label  detailNameLbl;
    private Label  detailTimeLbl;
    private Label  detailDateLbl;
    private Label  detailDescLbl;
    private HBox   detailBadges;
    private Button detailDoneBtn;

    // ── Initialise ────────────────────────────────────────────
    @FXML
    public void initialize() {
        headerDate.setText(LocalDate.now().format(
                DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")));
        buildOverlays();
        renderTasks();
    }

    // ══════════════════════════════════════════════════════════
    //  SIDEBAR ACTIONS
    // ══════════════════════════════════════════════════════════
    @FXML private void onAllTasks()    { selectCategory("all",      "Today's Tasks",  "All your tasks for today"); }
    @FXML private void onWork()        { selectCategory("work",     "Work Tasks",     "Tasks in the Work category"); }
    @FXML private void onGrocery()     { selectCategory("grocery",  "Grocery Tasks",  "Tasks in the Grocery category"); }
    @FXML private void onPersonal()    { selectCategory("personal", "Personal Tasks", "Tasks in the Personal category"); }
    @FXML private void onSchool()      { selectCategory("school",   "School Tasks",   "Tasks in the School category"); }
    @FXML private void onMedical()     { selectCategory("medical",  "Medical Tasks",  "Tasks in the Medical category"); }
    @FXML private void onSocial()      { selectCategory("social",   "Social Tasks",   "Tasks in the Social category"); }
    @FXML private void onFitness()     { selectCategory("fitness",  "Fitness Tasks",  "Tasks in the Fitness category"); }
    @FXML private void onNewCategory() { showToast("Feature coming soon!"); }
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
        for (var node : topbarNav.getChildren()) {
            if (node instanceof Button btn && btn.getId() != null) {
                boolean active = btn.getId().equals("nav-" + activeKey);
                btn.setStyle(Styles.navItem(active));
                if (!active) {
                    btn.setOnMouseEntered(e -> btn.setStyle(Styles.navItemHover()));
                    btn.setOnMouseExited(e  -> btn.setStyle(Styles.navItem(false)));
                }
            }
        }
    }

    // ══════════════════════════════════════════════════════════
    //  RENDER TASK LIST
    // ══════════════════════════════════════════════════════════
    private void renderTasks() {
        tasksCard.getChildren().clear();

        List<Task> filtered = TaskStore.getInstance().getTasks().stream()
                .filter(t -> currentCategory.equals("all") ||
                        t.getCategory().name().equalsIgnoreCase(currentCategory))
                .toList();

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
        card.setStyle(Styles.card(task.getPriority())); // style for tasks
        HBox.setHgrow(card, Priority.ALWAYS);

        // Checkbox circle
        Button check = new Button(task.isDone() ? "✓" : "");
        check.setStyle(Styles.checkCircle(task.isDone()));
        check.setOnAction(e -> {
            task.setDone(!task.isDone());
            renderTasks();
            showToast(task.isDone() ? "✅ Task completed!" : "Task marked incomplete");
        });

        // Info
        VBox info = new VBox(5);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label name = new Label(task.getName());
        name.setStyle(Styles.taskName(task.isDone()));

        HBox meta = new HBox(8);
        meta.setAlignment(Pos.CENTER_LEFT);

        Label catBadge = new Label(task.getCategoryLabel());
        catBadge.setStyle(Styles.badge("category"));

        Label priBadge = new Label("Priority: " + task.getPriorityLabel());
        priBadge.setStyle(Styles.badge(task.getPriority().name().toLowerCase()));

        meta.getChildren().addAll(catBadge, priBadge);

        if (task.getDate() != null) {
            Label dateLbl = new Label("📅  " + task.getDate().format(
                    DateTimeFormatter.ofPattern("EEE, d MMM yyyy")));
            dateLbl.setStyle(Styles.taskMeta());
            meta.getChildren().add(dateLbl);
        }

        info.getChildren().addAll(name, meta);

        // Time
        Label timeLbl = new Label(task.getTimeString());
        timeLbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; "
                + "-fx-font-family: 'Segoe UI'; -fx-text-fill: " + Styles.TEXT_MUTED + ";");

        card.getChildren().addAll(check, info, timeLbl);

        // Hover
        card.setOnMouseEntered(e -> card.setStyle(
                Styles.card(task.getPriority()).replace(
                        "rgba(74,108,247,0.10), 16", "rgba(74,108,247,0.18), 24")));
        card.setOnMouseExited(e  -> card.setStyle(Styles.card(task.getPriority())));
        card.setOnMouseClicked(e -> openDetail(task));

        return card;
    }

    // ══════════════════════════════════════════════════════════
    //  BUILD OVERLAYS
    // ══════════════════════════════════════════════════════════
    private void buildOverlays() {
        manualOverlay   = buildManualModal();
        aiOverlay       = buildAiModal();
        detailOverlay   = buildDetailModal();
        settingsOverlay = buildSettingsModal();
    }

    private void openOverlay(StackPane overlay) {
        if (!rootStack.getChildren().contains(overlay))
            rootStack.getChildren().add(overlay);
        overlay.setVisible(true);

        // Pop-in animation on the inner card
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

    /** Wraps a card VBox in a dimmed full-screen overlay */
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

        // Header
        HBox header = modalHeader("Add Task");
        Button closeBtn = (Button) ((HBox) header).getChildren().get(1);

        // Fields
        TextField nameField  = field("Task name");
        TextArea  descField  = area("Task description...");
        TextField dateField  = field("Date  (yyyy-MM-dd)");
        TextField timeField  = field("Time  (HH:mm)");
        ComboBox<String> catBox = combo("Select Category",
                "Work","Grocery","Personal","School","Medical","Social","Fitness");
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
            String name = nameField.getText().trim();
            if (name.isEmpty()) { showToast("Please enter a task name"); return; }

            LocalDate date = null;
            LocalTime time = null;
            try { if (!dateField.getText().isBlank())
                date = LocalDate.parse(dateField.getText().trim()); }
            catch (DateTimeParseException ignored) { showToast("Date format: yyyy-MM-dd"); return; }
            try { if (!timeField.getText().isBlank())
                time = LocalTime.parse(timeField.getText().trim()); }
            catch (DateTimeParseException ignored) { showToast("Time format: HH:mm"); return; }

            Task.Category cat = Task.Category.PERSONAL;
            if (catBox.getValue() != null)
                try { cat = Task.Category.valueOf(catBox.getValue().toUpperCase()); }
                catch (Exception ignored) {}

            Task.Priority pri = Task.Priority.MEDIUM;
            if (priBox.getValue() != null)
                try { pri = Task.Priority.valueOf(priBox.getValue().toUpperCase()); }
                catch (Exception ignored) {}

            TaskStore.getInstance().addTask(
                    new Task(name, descField.getText().trim(), date, time, cat, pri));
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

            // Strip tags to get name
            String name = raw.replaceAll("#\\w+", "").trim();

            // Category
            Task.Category cat = Task.Category.PERSONAL;
            String lower = raw.toLowerCase();
            if      (lower.contains("#work"))    cat = Task.Category.WORK;
            else if (lower.contains("#grocery")) cat = Task.Category.GROCERY;
            else if (lower.contains("#school"))  cat = Task.Category.SCHOOL;
            else if (lower.contains("#medical")) cat = Task.Category.MEDICAL;
            else if (lower.contains("#social"))  cat = Task.Category.SOCIAL;
            else if (lower.contains("#fitness")) cat = Task.Category.FITNESS;

            // Priority
            Task.Priority pri = Task.Priority.MEDIUM;
            if      (lower.contains("#high")) pri = Task.Priority.HIGH;
            else if (lower.contains("#low"))  pri = Task.Priority.LOW;

            // Date
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

            // Time
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

            TaskStore.getInstance().addTask(new Task(name, "", date, time, cat, pri));
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

        Button deleteBtn = new Button("🗑️  Delete");
        deleteBtn.setStyle(Styles.dangerButton());
        deleteBtn.setMaxWidth(Double.MAX_VALUE);

        HBox btnRow = new HBox(12, detailDoneBtn, deleteBtn);
        HBox.setHgrow(detailDoneBtn, Priority.ALWAYS);
        HBox.setHgrow(deleteBtn, Priority.ALWAYS);

        StackPane shell = wrapOverlay(card);
        closeBtn.setOnAction(e -> closeOverlay(shell));

        detailDoneBtn.setOnAction(e -> {
            if (selectedTask != null) {
                selectedTask.setDone(!selectedTask.isDone());
                renderTasks();
                closeOverlay(shell);
                showToast(selectedTask.isDone() ? "✅ Task completed!" : "Task marked incomplete");
            }
        });

        deleteBtn.setOnAction(e -> {
            if (selectedTask != null) {
                TaskStore.getInstance().removeTask(selectedTask);
                selectedTask = null;
                renderTasks();
                closeOverlay(shell);
                showToast("🗑️  Task deleted");
            }
        });

        card.getChildren().addAll(header, detailTimeLbl, detailDateLbl,
                detailBadges, detailDescLbl, btnRow);
        return shell;
    }

    private void openDetail(Task task) {
        selectedTask = task;
        detailNameLbl.setText(task.getName());
        detailTimeLbl.setText(task.getTimeString().isEmpty() ? "—" : task.getTimeString());
        detailDateLbl.setText(task.getDate() != null
                ? task.getDate().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy")) : "—");
        detailDescLbl.setText(task.getDescription().isEmpty()
                ? "No description provided." : task.getDescription());

        detailBadges.getChildren().clear();
        Label cat = new Label(task.getCategoryLabel());
        cat.setStyle(Styles.badge("category"));
        Label pri = new Label("Priority: " + task.getPriorityLabel());
        pri.setStyle(Styles.badge(task.getPriority().name().toLowerCase()));
        detailBadges.getChildren().addAll(cat, pri);
        if (task.isDone()) {
            Label done = new Label("✓ Completed");
            done.setStyle(Styles.badge("low"));
            detailBadges.getChildren().add(done);
        }

        detailDoneBtn.setText(task.isDone() ? "Mark as Incomplete" : "Mark as Done");
        openOverlay(detailOverlay);
    }

    // ══════════════════════════════════════════════════════════
    //  SETTINGS MODAL
    // ══════════════════════════════════════════════════════════
    private StackPane buildSettingsModal() {
        VBox card = new VBox(14);
        card.setStyle(Styles.modalCard());
        card.setMaxWidth(420);
        card.setMaxHeight(Region.USE_PREF_SIZE);

        HBox header = modalHeader("⚙️  Help & Settings");
        Button closeBtn = (Button) header.getChildren().get(1);

        Label accLabel = new Label("ACCOUNT");
        accLabel.setStyle(Styles.formLabel());
        Label userRow = new Label("Username:   " + TaskStore.getInstance().getLoggedInUser());
        userRow.setStyle("-fx-font-family: 'Segoe UI'; -fx-font-size: 13px; "
                + "-fx-text-fill: " + Styles.TEXT + ";");

        Label helpLabel = new Label("HELP");
        helpLabel.setStyle(Styles.formLabel());
        Label h1 = new Label("📖   How to add tasks");
        Label h2 = new Label("🤖   Using Smart Tasks — type naturally with #tags");
        Label h3 = new Label("📞   Contact Support");
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

    /** Creates a modal header with a title label + close button */
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

    /** Wraps a label + field together in a VBox with a form label above */
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