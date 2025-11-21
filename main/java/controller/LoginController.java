package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import service.AuthService;
import app.Navigator;
import model.User;
import util.Session;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    @FXML
    private void initialize() {}

    @FXML
    private void onLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        AuthService auth = new AuthService();
        java.util.Optional<User> u = auth.authenticate(username, password);
        if (u.isPresent()) {
            User user = u.get();
            Session.setCurrentUser(user);
            Stage stage = (Stage) loginButton.getScene().getWindow();
            if (user.getRole() == model.Role.ADMIN) {
                Navigator.navigateTo(stage, "/fxml/AdminDashboard.fxml", "Admin Dashboard");
            } else {
                Navigator.navigateTo(stage, "/fxml/CustomerDashboard.fxml", "Customer Dashboard");
            }
        } else {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Authentication Failed");
            a.setHeaderText("Invalid credentials");
            a.setContentText("The username or password you entered is incorrect. Please try again.");
            a.showAndWait();
            passwordField.clear();
            usernameField.requestFocus();
        }
    }

    @FXML
    private void onRegister() {
        // open register as a modal dialog so the main app stays running
        Navigator.openDialog("/fxml/Register.fxml", "Register");
    }
}
