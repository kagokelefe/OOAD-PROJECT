package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import service.AuthService;

public class RegisterController {

    @FXML private TextField fullNameField;
    @FXML private TextField addressField;
    @FXML private ChoiceBox<String> maritalChoice;
    @FXML private TextField emailField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button registerButton;

    private final AuthService auth = new AuthService();

    @FXML
    private void initialize() {
        maritalChoice.getItems().addAll("Single","Married","Divorced","Widowed","Other");
        maritalChoice.setValue("Single");
    }

    @FXML
    private void onRegister() {
        String fullName = fullNameField.getText();
        String address = addressField.getText();
        String marital = maritalChoice.getValue();
        String email = emailField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (fullName == null || fullName.isBlank() || username == null || username.isBlank() || password == null || password.isBlank() || email == null || email.isBlank()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Validation error");
            a.setHeaderText("Missing required fields");
            a.setContentText("Please fill in Full name, Username, Email and Password.");
            a.showAndWait();
            return;
        }

        try {
            auth.register(fullName, address, marital, username, email, password);
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Registration submitted");
            a.setHeaderText("Registration successful");
            a.setContentText("Your account has been created and is pending approval by an administrator.");
            a.showAndWait();
            Stage st = (Stage) registerButton.getScene().getWindow();
            st.close();
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Registration failed");
            a.setHeaderText("Could not register");
            a.setContentText("An error occurred while creating your account. Please try again later.");
            a.showAndWait();
        }
    }

    @FXML
    private void onCancel() {
        Stage st = (Stage) registerButton.getScene().getWindow();
        st.close();
    }

    @FXML
    private void onBackToLogin() {
        // when opened as a dialog, simply close the dialog window
        Stage st = (Stage) registerButton.getScene().getWindow();
        st.close();
    }
}
