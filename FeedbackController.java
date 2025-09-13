package com.simulator;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

/**
 * 📧 FEEDBACK CONTROLLER
 * Professional feedback form with email sending capabilities
 */
public class FeedbackController implements Initializable {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField locationField;
    @FXML private ComboBox<String> osComboBox;
    @FXML private ComboBox<String> issueTypeComboBox;
    @FXML private TextArea descriptionArea;
    @FXML private Button attachButton;
    @FXML private ListView<String> attachmentList;
    @FXML private Button cancelButton;
    @FXML private Button sendButton;
    @FXML private ProgressBar progressBar;

    private final ObservableList<File> attachments = FXCollections.observableArrayList();
    private final ObservableList<String> attachmentNames = FXCollections.observableArrayList();
    private EmailService emailService;
    private Stage dialogStage;

    // Constants
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final String EMAIL_PATTERN =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupComponents();
        setupValidation();
        emailService = EmailService.getInstance();
    }

    /**
     * 🎨 SETUP: Initialize form components
     */
    private void setupComponents() {
        // Initialize OS ComboBox
        osComboBox.setItems(FXCollections.observableArrayList(
                System.getProperty("os.name") + " " + System.getProperty("os.version"),
                "Windows 10", "Windows 11", "macOS", "Ubuntu", "Other Linux", "Other"
        ));
        osComboBox.getSelectionModel().selectFirst();

        // Initialize Issue Type ComboBox
        issueTypeComboBox.setItems(FXCollections.observableArrayList(
                "🐛 Bug Report", "💡 Feature Request", "❓ Question",
                "🚀 Performance Issue", "📚 Documentation", "🎨 UI/UX Feedback", "🔧 Other"
        ));
        issueTypeComboBox.getSelectionModel().selectFirst();

        // Setup attachment list
        attachmentList.setItems(attachmentNames);
        attachmentList.setCellFactory(TextFieldListCell.forListView());

        // Setup context menu for attachments
        setupAttachmentContextMenu();

        // Set tooltips
        attachButton.setTooltip(new Tooltip("Add files (Images, Videos, Documents)"));
        sendButton.setTooltip(new Tooltip("Send feedback via email"));

        System.out.println("✅ Feedback form components initialized");
    }

    /**
     * 📋 SETUP: Input validation
     */
    private void setupValidation() {
        // Real-time validation
        nameField.textProperty().addListener((obs, old, text) -> updateSendButtonState());
        emailField.textProperty().addListener((obs, old, text) -> updateSendButtonState());
        descriptionArea.textProperty().addListener((obs, old, text) -> updateSendButtonState());

        // Email format validation
        emailField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (!isFocused && !emailField.getText().isEmpty()) {
                validateEmailFormat();
            }
        });
    }

    /**
     * 📎 SETUP: Attachment context menu
     */
    private void setupAttachmentContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem removeItem = new MenuItem("Remove");
        removeItem.setOnAction(e -> {
            int selectedIndex = attachmentList.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                attachments.remove(selectedIndex);
                attachmentNames.remove(selectedIndex);
                System.out.println("📎 Attachment removed: " + selectedIndex);
            }
        });
        contextMenu.getItems().add(removeItem);
        attachmentList.setContextMenu(contextMenu);
    }

    /**
     * 📁 ACTION: Select files for attachment
     */
    @FXML
    private void selectFiles() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Select Files to Attach");

            // Set extension filters
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("All Supported",
                            "*.jpg", "*.jpeg", "*.png", "*.gif", "*.mp4", "*.mov", "*.avi",
                            "*.pdf", "*.doc", "*.docx", "*.txt", "*.log"),
                    new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.gif"),
                    new FileChooser.ExtensionFilter("Videos", "*.mp4", "*.mov", "*.avi"),
                    new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.doc", "*.docx", "*.txt", "*.log"),
                    new FileChooser.ExtensionFilter("All Files", "*.*")
            );

            List<File> selectedFiles = fileChooser.showOpenMultipleDialog(attachButton.getScene().getWindow());

            if (selectedFiles != null) {
                for (File file : selectedFiles) {
                    if (file.length() > MAX_FILE_SIZE) {
                        showAlert("File Too Large",
                                "File '" + file.getName() + "' exceeds 10MB limit.");
                        continue;
                    }

                    if (!attachments.contains(file)) {
                        attachments.add(file);
                        attachmentNames.add(file.getName() + " (" + formatFileSize(file.length()) + ")");
                        System.out.println("📎 Attachment added: " + file.getName());
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("❌ Error selecting files: " + e.getMessage());
            showAlert("Error", "Failed to select files: " + e.getMessage());
        }
    }

    /**
     * ✅ VALIDATION: Update send button state
     */
    private void updateSendButtonState() {
        boolean isValid = !StringUtils.isBlank(nameField.getText()) &&
                !StringUtils.isBlank(emailField.getText()) &&
                !StringUtils.isBlank(descriptionArea.getText()) &&
                isValidEmail(emailField.getText());

        sendButton.setDisable(!isValid);
    }

    /**
     * 📧 VALIDATION: Email format
     */
    private boolean isValidEmail(String email) {
        return Pattern.compile(EMAIL_PATTERN).matcher(email).matches();
    }

    /**
     * ⚠️ VALIDATION: Show email format error
     */
    private void validateEmailFormat() {
        if (!isValidEmail(emailField.getText())) {
            emailField.setStyle("-fx-border-color: red;");
            showAlert("Invalid Email", "Please enter a valid email address.");
        } else {
            emailField.setStyle("");
        }
    }

    /**
     * 📨 ACTION: Send feedback email
     */
    @FXML
    private void sendFeedback() {
        if (!validateForm()) {
            return;
        }

        // Disable form during sending
        setFormEnabled(false);
        progressBar.setVisible(true);

        // Create email data
        FeedbackData feedbackData = createFeedbackData();

        // Send email asynchronously
        Task<Boolean> emailTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return emailService.sendFeedbackEmail(feedbackData);
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    showAlert("Feedback Sent!",
                            "Thank you for your feedback! We'll get back to you soon.");
                    closeDialog();
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    setFormEnabled(true);
                    progressBar.setVisible(false);
                    showAlert("Send Failed",
                            "Failed to send feedback: " + getException().getMessage());
                });
            }
        };

        new Thread(emailTask).start();
        System.out.println("📨 Feedback email sending initiated");
    }

    /**
     * 📋 DATA: Create feedback data object
     */
    private FeedbackData createFeedbackData() {
        FeedbackData data = new FeedbackData();
        data.setName(nameField.getText().trim());
        data.setEmail(emailField.getText().trim());
        data.setLocation(locationField.getText().trim());
        data.setOperatingSystem(osComboBox.getValue());
        data.setIssueType(issueTypeComboBox.getValue());
        data.setDescription(descriptionArea.getText().trim());
        data.setAttachments(new ArrayList<>(attachments));

        // Add system information
        data.setAppVersion(AlgorithmSimulatorApplication.getVersion());
        data.setJavaVersion(System.getProperty("java.version"));
        data.setJavaVendor(System.getProperty("java.vendor"));

        return data;
    }

    /**
     * ✅ VALIDATION: Validate entire form
     */
    private boolean validateForm() {
        List<String> errors = new ArrayList<>();

        if (StringUtils.isBlank(nameField.getText())) {
            errors.add("Name is required");
        }

        if (StringUtils.isBlank(emailField.getText())) {
            errors.add("Email is required");
        } else if (!isValidEmail(emailField.getText())) {
            errors.add("Invalid email format");
        }

        if (StringUtils.isBlank(descriptionArea.getText())) {
            errors.add("Issue description is required");
        }

        if (!errors.isEmpty()) {
            showAlert("Validation Error", String.join("\n", errors));
            return false;
        }

        return true;
    }

    /**
     * 🔄 UTILITY: Enable/disable form components
     */
    private void setFormEnabled(boolean enabled) {
        nameField.setDisable(!enabled);
        emailField.setDisable(!enabled);
        locationField.setDisable(!enabled);
        osComboBox.setDisable(!enabled);
        issueTypeComboBox.setDisable(!enabled);
        descriptionArea.setDisable(!enabled);
        attachButton.setDisable(!enabled);
        sendButton.setDisable(!enabled);
    }

    /**
     * 📐 UTILITY: Format file size
     */
    private String formatFileSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return (bytes / 1024) + " KB";
        return (bytes / (1024 * 1024)) + " MB";
    }

    /**
     * ⚠️ UTILITY: Show alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Apply theme to alert
        try {
            if (alert.getDialogPane().getScene() != null) {
                ThemeManager.getInstance().registerScene(alert.getDialogPane().getScene());
            }
        } catch (Exception e) {
            System.out.println("ℹ️ Could not apply theme to alert");
        }

        alert.showAndWait();
    }

    /**
     * ❌ ACTION: Cancel feedback
     */
    @FXML
    private void cancel() {
        closeDialog();
    }

    /**
     * 🚪 UTILITY: Close dialog
     */
    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    /**
     * 🎯 SETTER: Set dialog stage reference
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}
