package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.account.BankAccount;
import util.Session;
import javafx.scene.control.Alert;

public class RequestAccountDialogController {

    @FXML private ChoiceBox<String> typeChoice;
    @FXML private TextField initialDepositField;
    @FXML private TextArea noteArea;
    @FXML private VBox employerBox;
    @FXML private TextField employerNameField;
    @FXML private TextField employerAddressField;
    @FXML private Button btnRequest;
    @FXML private Button btnCancel;

    @FXML
    private void initialize() {
        // will populate available types on showing
        btnCancel.setOnAction(e -> ((Stage)btnCancel.getScene().getWindow()).close());
        btnRequest.setOnAction(e -> doRequest());
    }

    private void doRequest() {
        String type = typeChoice.getValue();
        if (type == null || type.isBlank()) {
            new Alert(Alert.AlertType.WARNING, "Please select an account type").showAndWait();
            return;
        }
        double initial = 0.0;
        try { if (initialDepositField.getText() != null && !initialDepositField.getText().isBlank()) initial = Double.parseDouble(initialDepositField.getText()); } catch (Exception ex) { new Alert(Alert.AlertType.ERROR, "Invalid initial deposit").showAndWait(); return; }
        long userId = Session.getCurrentUser().getId();
        dao.BankAccountDAO dao = new dao.jdbc.BankAccountDAOImpl();
        BankAccount acct = null;
        String acctNo = "ACCT" + System.currentTimeMillis();
        try {
            // business rules: investment minimum
            if (type.toLowerCase().contains("investment") && initial < 500.0) {
                new Alert(Alert.AlertType.WARNING, "Investment accounts require a minimum opening deposit of BWP500").showAndWait();
                return;
            }
            if (type.toLowerCase().contains("cheque")) {
                // require employer info
                String emp = employerNameField.getText();
                String empAddr = employerAddressField.getText();
                if (emp == null || emp.isBlank() || empAddr == null || empAddr.isBlank()) {
                    new Alert(Alert.AlertType.WARNING, "Cheque accounts require employer name and address").showAndWait();
                    return;
                }
                acct = new model.account.ChequeAccount(0, userId, acctNo, initial, 0.0);
            } else if (type.toLowerCase().contains("saving")) {
                acct = new model.account.SavingsAccount(0, userId, acctNo, initial, 0.0);
            } else {
                acct = new model.account.InvestmentAccount(0, userId, acctNo, initial, "GENERAL");
            }
            acct.setStatus("PENDING");
            dao.save(acct);
            new Alert(Alert.AlertType.INFORMATION, "Account request submitted").showAndWait();
            ((Stage)btnRequest.getScene().getWindow()).close();
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Could not create account request").showAndWait();
        }
    }

    // populate available types excluding those the user already has
    @FXML
    public void onShown() {
        try {
            typeChoice.getItems().clear();
            long userId = Session.getCurrentUser().getId();
            java.util.List<BankAccount> list = new dao.jdbc.BankAccountDAOImpl().findByUserId(userId);
            java.util.Set<String> owned = new java.util.HashSet<>();
            for (BankAccount b : list) {
                String t = b.getClass().getSimpleName().toLowerCase();
                owned.add(t);
            }
            if (!owned.contains("chequeaccount") && !owned.contains("cheque")) typeChoice.getItems().add("Cheque");
            if (!owned.contains("savingsaccount") && !owned.contains("savings")) typeChoice.getItems().add("Savings");
            if (!owned.contains("investmentaccount") && !owned.contains("investment")) typeChoice.getItems().add("Investment");
                if (typeChoice.getItems().isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "You already have all available account types").showAndWait();
                ((Stage)typeChoice.getScene().getWindow()).close();
            } else {
                typeChoice.setValue(typeChoice.getItems().get(0));
                // show/hide employer fields based on selection
                boolean isCheque = typeChoice.getValue().toLowerCase().contains("cheque");
                employerBox.setVisible(isCheque);
                employerBox.setManaged(isCheque);
                typeChoice.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
                    boolean isC = newV != null && newV.toLowerCase().contains("cheque");
                    employerBox.setVisible(isC);
                    employerBox.setManaged(isC);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
