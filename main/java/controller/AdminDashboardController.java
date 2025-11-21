package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.SimpleStringProperty;
import model.account.BankAccount;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import util.Session;
import app.Navigator;

public class AdminDashboardController {

    @FXML
    private TableView<BankAccount> usersTable;
    @FXML private TableColumn<BankAccount,String> colAcctNo;
    @FXML private TableColumn<BankAccount,Long> colUserId;
    @FXML private TableColumn<BankAccount,String> colOwner;
    @FXML private TableColumn<BankAccount,String> colEmail;
    @FXML private TableColumn<BankAccount,String> colType;
    @FXML private TableColumn<BankAccount,Double> colBalance;
    @FXML
    private Button approveButton;
    @FXML private Button openAcctButton;
    @FXML private Button refreshButton;
    @FXML private Button logoutButton;

    private dao.BankAccountDAO accountDAO = new dao.jdbc.BankAccountDAOImpl();
    

    @FXML
    private void initialize() {
        colAcctNo.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        colUserId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        // owner and email are resolved dynamically from the user DAO
        colOwner.setCellValueFactory(cell -> {
            BankAccount acct = cell.getValue();
            try {
                java.util.Optional<model.User> u = new dao.jdbc.UserDAOImpl().findById(acct.getUserId());
                return new SimpleStringProperty(u.map(model.User::getFullName).orElse("(unknown)"));
            } catch (Exception e) { return new SimpleStringProperty("(error)"); }
        });
        colEmail.setCellValueFactory(cell -> {
            BankAccount acct = cell.getValue();
            try {
                java.util.Optional<model.User> u = new dao.jdbc.UserDAOImpl().findById(acct.getUserId());
                return new SimpleStringProperty(u.map(model.User::getEmail).orElse(""));
            } catch (Exception e) { return new SimpleStringProperty(""); }
        });
        colType.setCellValueFactory(new PropertyValueFactory<>("class"));
        colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        usersTable.setItems(FXCollections.observableArrayList());
        // add an auto-refresh when the admin window gains focus
        usersTable.sceneProperty().addListener((sObs, oldScene, newScene) -> {
            if (newScene == null) return;
            newScene.windowProperty().addListener((wObs, oldWindow, newWindow) -> {
                if (newWindow == null) return;
                newWindow.focusedProperty().addListener((fObs, wasFocused, isNowFocused) -> {
                    if (isNowFocused) refreshPending();
                });
            });
        });
        refreshPending();
    }

    @FXML
    private void onRefresh() {
        refreshPending();
    }

    private void refreshPending() {
        try {
            java.util.List<BankAccount> pending = accountDAO.findByStatus("PENDING");
            usersTable.setItems(FXCollections.observableArrayList(pending));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onApprove() {
        BankAccount selected = usersTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("No selection");
            a.setHeaderText("No account selected");
            a.setContentText("Please select a pending account to approve.");
            a.showAndWait();
            return;
        }
        selected.setStatus("APPROVED");
        accountDAO.update(selected);
        usersTable.getItems().remove(selected);
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Account approved");
        a.setHeaderText("Account approved");
        a.setContentText("Account " + selected.getAccountNumber() + " has been approved.");
        a.showAndWait();
    }

    @FXML
    private void onOpenAccount() {
        // open modal dialog to create account for a user
        app.Navigator.openDialog("/fxml/OpenAccountDialog.fxml", "Open Account");
    }

    @FXML
    private void onLogout() {
        Session.setCurrentUser(null);
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        Navigator.navigateTo(stage, "/fxml/Login.fxml", "Login");
    }
}
