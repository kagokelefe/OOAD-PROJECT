package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ChoiceBox;
import javafx.stage.Stage;
import model.account.BankAccount;
import service.TransactionService;
import java.util.List;
import util.Session;

public class TransferDialogController {

    @FXML
    private Label lblFromAccount;
    @FXML
    private TextField tfToAccount;
    @FXML
    private ChoiceBox<String> cbToAccounts;
    @FXML
    private TextField tfAmount;
    @FXML
    private TextArea taNote;
    @FXML
    private Button btnTransfer;
    @FXML
    private Button btnCancel;

    private BankAccount fromAccount;
    private final TransactionService txService = new TransactionService();

    public void setFromAccount(BankAccount a) {
        this.fromAccount = a;
        if (a != null) lblFromAccount.setText(a.getAccountNumber());
        // populate recipient choicebox with user's own accounts (exclude fromAccount)
        try {
            if (Session.getCurrentUser() != null) {
                java.util.List<BankAccount> mine = new dao.jdbc.BankAccountDAOImpl().findByUserId(Session.getCurrentUser().getId());
                cbToAccounts.getItems().clear();
                for (BankAccount b : mine) {
                    if (a != null && b.getId() == a.getId()) continue;
                    cbToAccounts.getItems().add(b.getAccountNumber());
                }
                if (!cbToAccounts.getItems().isEmpty()) cbToAccounts.setValue(cbToAccounts.getItems().get(0));
                cbToAccounts.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
                    if (newV != null) tfToAccount.setText(newV);
                });
            }
        } catch (Exception ignored) {}
    }

    @FXML
    private void initialize() {
        btnCancel.setOnAction(evt -> {
            Stage s = (Stage) btnCancel.getScene().getWindow();
            s.close();
        });

        btnTransfer.setOnAction(evt -> doTransfer());
    }

    private void doTransfer() {
        if (fromAccount == null) return;
        String toAcct = tfToAccount.getText();
        String amtS = tfAmount.getText();
        if (toAcct == null || toAcct.isBlank()) {
            javafx.scene.control.Alert a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR, "Please enter destination account number");
            a.showAndWait();
            return;
        }
        double amt;
        try { amt = Double.parseDouble(amtS); } catch (Exception e) { javafx.scene.control.Alert a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR, "Invalid amount"); a.showAndWait(); return; }

        // find target account
        List<BankAccount> all = new dao.jdbc.BankAccountDAOImpl().findByStatus("APPROVED");
        BankAccount target = null;
        for (BankAccount b : all) if (b.getAccountNumber().equals(toAcct)) { target = b; break; }
        if (target == null) { javafx.scene.control.Alert a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR, "Target account not found"); a.showAndWait(); return; }

        boolean ok = txService.transfer(fromAccount.getId(), target.getId(), amt, taNote.getText());
        if (ok) {
            javafx.scene.control.Alert a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION, "Transfer completed");
            a.showAndWait();
            Stage s = (Stage) btnTransfer.getScene().getWindow();
            s.close();
        } else {
            javafx.scene.control.Alert a = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR, "Transfer failed (check rules or balance)");
            a.showAndWait();
        }
    }
}
