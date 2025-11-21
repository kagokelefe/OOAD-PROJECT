package service;

import dao.TransactionDAO;
import dao.jdbc.TransactionDAOImpl;
import model.Transaction;

import java.util.List;
import util.DbUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;

public class TransactionService {
    private final TransactionDAO txDAO = new TransactionDAOImpl();

    public List<Transaction> getTransactionsForAccount(long accountId) {
        return txDAO.findByAccountId(accountId);
    }

    public Transaction record(Transaction tx) {
        return txDAO.save(tx);
    }

    /**
     * Perform an atomic transfer between two accounts. This updates both account balances
     * and inserts two transaction rows (TRANSFER_OUT and TRANSFER_IN) in a single DB transaction.
     * Returns true on success, false on validation failure.
     */
    public boolean transfer(long fromAccountId, long toAccountId, double amount, String description) {
        if (fromAccountId == toAccountId) return false;
        if (amount <= 0) return false;

        try (Connection conn = DbUtil.getConnection()) {
            try {
                conn.setAutoCommit(false);

                // Lock both account rows FOR UPDATE
                PreparedStatement psFrom = conn.prepareStatement("SELECT id,balance,type,extra FROM accounts WHERE id = ? FOR UPDATE");
                psFrom.setLong(1, fromAccountId);
                ResultSet rsFrom = psFrom.executeQuery();
                if (!rsFrom.next()) { conn.rollback(); return false; }
                double fromBal = rsFrom.getDouble("balance");
                String fromType = rsFrom.getString("type");
                String fromExtra = rsFrom.getString("extra");

                PreparedStatement psTo = conn.prepareStatement("SELECT id,balance,type,extra FROM accounts WHERE id = ? FOR UPDATE");
                psTo.setLong(1, toAccountId);
                ResultSet rsTo = psTo.executeQuery();
                if (!rsTo.next()) { conn.rollback(); return false; }
                double toBal = rsTo.getDouble("balance");

                // Business rules: disallow withdrawal from SAVINGS accounts
                if (fromType != null && fromType.toUpperCase().contains("SAVINGS")) {
                    conn.rollback();
                    return false;
                }

                double overdraft = 0.0;
                if (fromType != null && fromType.toUpperCase().contains("CHEQUE")) {
                    try { overdraft = Double.parseDouble(fromExtra == null ? "0" : fromExtra); } catch (Exception ignored) {}
                }

                double newFromBal = fromBal - amount;
                if (newFromBal < -overdraft) { conn.rollback(); return false; }

                double newToBal = toBal + amount;

                PreparedStatement psUpdate = conn.prepareStatement("UPDATE accounts SET balance = ? WHERE id = ?");
                psUpdate.setDouble(1, newFromBal);
                psUpdate.setLong(2, fromAccountId);
                psUpdate.executeUpdate();

                psUpdate.setDouble(1, newToBal);
                psUpdate.setLong(2, toAccountId);
                psUpdate.executeUpdate();

                // insert transactions
                PreparedStatement psInsert = conn.prepareStatement("INSERT INTO transactions(account_id,amount,type,timestamp,description) VALUES(?,?,?,?,?)");
                psInsert.setLong(1, fromAccountId);
                psInsert.setDouble(2, amount);
                psInsert.setString(3, "TRANSFER_OUT");
                psInsert.setTimestamp(4, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                psInsert.setString(5, description == null ? "" : description);
                psInsert.executeUpdate();

                psInsert.setLong(1, toAccountId);
                psInsert.setDouble(2, amount);
                psInsert.setString(3, "TRANSFER_IN");
                psInsert.setTimestamp(4, java.sql.Timestamp.valueOf(LocalDateTime.now()));
                psInsert.setString(5, description == null ? "" : description);
                psInsert.executeUpdate();

                conn.commit();
                return true;
            } catch (Exception e) {
                try { conn.rollback(); } catch (Exception ignored) {}
                e.printStackTrace();
                return false;
            } finally {
                try { conn.setAutoCommit(true); } catch (Exception ignored) {}
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
