package controller;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import util.Session;
import app.Navigator;
import service.TransactionService;
import model.Transaction;
import model.account.BankAccount;
import service.AccountService;


public class CustomerDashboardController {

    @FXML
    private TableView<BankAccount> accountsTable;
    @FXML
    private TableColumn<BankAccount, String> colAccountNo;
    @FXML
    private TableColumn<BankAccount, String> colType;
    @FXML
    private TableColumn<BankAccount, Double> colBalance;

    private AccountService accountService = new AccountService();
    private TransactionService txService = new TransactionService();

    @FXML
    private void initialize() {
        colAccountNo.setCellValueFactory(new PropertyValueFactory<>("accountNumber"));
        colType.setCellValueFactory(new PropertyValueFactory<>("class"));
        colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        if (Session.getCurrentUser() != null) {
            accountsTable.setItems(FXCollections.observableArrayList(accountService.getAccountsForUser(Session.getCurrentUser().getId())));
        }
    }

    @FXML
    private Button withdrawButton;
    @FXML
    private Button depositButton;
    @FXML
    private Button transferButton;
    @FXML
    private Button closeButton;
    @FXML
    private Button requestAcctButton;
    @FXML
    private Button logoutButton;

    @FXML
    private void onWithdraw() {
        BankAccount a = accountsTable.getSelectionModel().getSelectedItem();
        if (a == null) return;
        TextInputDialog d = new TextInputDialog();
        d.setHeaderText("Withdraw amount");
        d.setContentText("Amount:");
        java.util.Optional<String> res = d.showAndWait();
        if (res.isPresent()) {
            try {
                double amt = Double.parseDouble(res.get());
                boolean ok = accountService.withdraw(a.getId(), amt);
                if (ok) {
                    // record transaction
                    txService.record(new Transaction(0, a.getId(), -amt, "WITHDRAW", java.time.LocalDateTime.now(), "Withdraw"));
                    Alert aa = new Alert(Alert.AlertType.INFORMATION);
                    aa.setTitle("Withdrawn");
                    aa.setHeaderText("Withdraw successful");
                    aa.setContentText("Withdrawn " + amt + " from " + a.getAccountNumber());
                    aa.showAndWait();
                    refresh();
                } else System.out.println("Withdraw failed");
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    @FXML
    private void onDeposit() {
        BankAccount a = accountsTable.getSelectionModel().getSelectedItem();
        if (a == null) return;
        TextInputDialog d = new TextInputDialog();
        d.setHeaderText("Deposit amount");
        d.setContentText("Amount:");
        java.util.Optional<String> res = d.showAndWait();
        if (res.isPresent()) {
            try {
                double amt = Double.parseDouble(res.get());
                // deposit by increasing balance
                a.setBalance(a.getBalance() + amt);
                new dao.jdbc.BankAccountDAOImpl().update(a);
                txService.record(new Transaction(0, a.getId(), amt, "DEPOSIT", java.time.LocalDateTime.now(), "Deposit"));
                Alert aa = new Alert(Alert.AlertType.INFORMATION);
                aa.setTitle("Deposit");
                aa.setHeaderText("Deposit successful");
                aa.setContentText("Deposited " + amt + " to " + a.getAccountNumber());
                aa.showAndWait();
                refresh();
            } catch (Exception e) { e.printStackTrace(); }
        }
    }

    @FXML
    private void onTransfer() {
        BankAccount a = accountsTable.getSelectionModel().getSelectedItem();
        if (a == null) return;
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/TransferDialog.fxml"));
            javafx.scene.Parent root = loader.load();
            controller.TransferDialogController ctrl = loader.getController();
            ctrl.setFromAccount(a);
            Stage dialog = new Stage();
            dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            try { scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm()); } catch (Exception ignored) {}
            dialog.setScene(scene);
            dialog.setTitle("Transfer");
            dialog.showAndWait();
            // after dialog closes, refresh accounts to show updated balances
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onRequestAccount() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/RequestAccountDialog.fxml"));
            javafx.scene.Parent root = loader.load();
            controller.RequestAccountDialogController ctrl = loader.getController();
            Stage dialog = new Stage();
            dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            try { scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm()); } catch (Exception ignored) {}
            dialog.setScene(scene);
            dialog.setTitle("Request Account");
            // call onShown after scene/window are ready
            dialog.setOnShown(ev -> { try { ctrl.onShown(); } catch (Exception ignored) {} });
            dialog.showAndWait();
            refresh();
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void onLogout() {
        Session.setCurrentUser(null);
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        Navigator.navigateTo(stage, "/fxml/Login.fxml", "Login");
    }

    @FXML
    private void onCloseAccount() {
        BankAccount a = accountsTable.getSelectionModel().getSelectedItem();
        if (a == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Close account");
        confirm.setHeaderText("Close account " + a.getAccountNumber());
        confirm.setContentText("Are you sure you want to close this account?");
        java.util.Optional<javafx.scene.control.ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == javafx.scene.control.ButtonType.OK) {
            a.setStatus("CLOSED");
            new dao.jdbc.BankAccountDAOImpl().update(a);
            refresh();
        }
    }

    private void refresh() {
        if (Session.getCurrentUser() != null) {
            accountsTable.setItems(FXCollections.observableArrayList(accountService.getAccountsForUser(Session.getCurrentUser().getId())));
        }
    }

}
