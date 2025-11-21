package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.account.BankAccount;
import model.account.ChequeAccount;
import model.account.SavingsAccount;
import model.account.InvestmentAccount;
import service.AccountService;
import dao.UserDAO;
import dao.jdbc.UserDAOImpl;


public class OpenAccountDialogController {

    @FXML private TextField usernameField;
    @FXML private ChoiceBox<String> typeChoice;
    @FXML private TextField initialDepositField;
    @FXML private Button createButton;

    private final AccountService accountService = new AccountService();
    private final UserDAO userDAO = new UserDAOImpl();

    @FXML
    private void initialize() {
        typeChoice.getItems().addAll("Cheque", "Savings", "Investment");
        typeChoice.setValue("Cheque");
    }

    @FXML
    private void onCreate() {
        String username = usernameField.getText();
        java.util.Optional<model.User> u = userDAO.findByUsername(username);
        if (u.isEmpty()) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("User not found");
            a.setHeaderText("Cannot open account");
            a.setContentText("No user with username '" + username + "' was found.");
            a.showAndWait();
            return;
        }
        long userId = u.get().getId();
        String type = typeChoice.getValue();
        double deposit = 0;
        try { deposit = Double.parseDouble(initialDepositField.getText()); } catch (Exception ignored) {}

        BankAccount acct;
        String acctNo = "A" + System.currentTimeMillis();
        if ("Cheque".equalsIgnoreCase(type)) {
            ChequeAccount c = new ChequeAccount();
            c.setAccountNumber(acctNo);
            c.setUserId(userId);
            c.setBalance(deposit);
            c.setOverdraftLimit(500);
            c.setStatus("APPROVED");
            acct = c;
        } else if ("Savings".equalsIgnoreCase(type)) {
            SavingsAccount s = new SavingsAccount();
            s.setAccountNumber(acctNo);
            s.setUserId(userId);
            s.setBalance(deposit);
            s.setInterestRate(0.01);
            s.setStatus("APPROVED");
            acct = s;
        } else {
            InvestmentAccount i = new InvestmentAccount();
            i.setAccountNumber(acctNo);
            i.setUserId(userId);
            i.setBalance(deposit);
            i.setInvestmentType("GENERAL");
            i.setStatus("APPROVED");
            acct = i;
        }

        try {
            accountService.createAccount(acct);
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("Account created");
            a.setHeaderText("Account opened");
            a.setContentText("Account " + acct.getAccountNumber() + " created for user " + username + ".");
            a.showAndWait();
            Stage stage = (Stage) createButton.getScene().getWindow();
            stage.close();
        } catch (Exception e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Create failed");
            a.setHeaderText("Could not create account");
            a.setContentText("An error occurred while creating the account. Please try again.");
            a.showAndWait();
        }
    }

    @FXML
    private void onCancel() {
        Stage stage = (Stage) initialDepositField.getScene().getWindow();
        stage.close();
    }
}
