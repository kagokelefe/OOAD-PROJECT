package dao.jdbc;

import dao.BankAccountDAO;
import model.account.BankAccount;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class BankAccountDAOImpl implements BankAccountDAO {

    private static final Logger logger = Logger.getLogger(BankAccountDAOImpl.class.getName());

    @Override
    public Optional<BankAccount> findById(long id) {
        try (java.sql.Connection conn = util.DbUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("SELECT id,user_id,account_number,balance,type,extra,employer_name,employer_address,status FROM accounts WHERE id = ?")) {
            ps.setLong(1, id);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rowToAccount(rs));
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding account by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<BankAccount> findByUserId(long userId) {
        List<BankAccount> list = new ArrayList<>();
       try (java.sql.Connection conn = util.DbUtil.getConnection();
           java.sql.PreparedStatement ps = conn.prepareStatement("SELECT id,user_id,account_number,balance,type,extra,employer_name,employer_address,status FROM accounts WHERE user_id = ?")) {
            ps.setLong(1, userId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(rowToAccount(rs));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding accounts by user id: " + userId, e);
        }
        return list;
    }

    @Override
    public List<BankAccount> findByStatus(String status) {
        List<BankAccount> list = new ArrayList<>();
       try (java.sql.Connection conn = util.DbUtil.getConnection();
           java.sql.PreparedStatement ps = conn.prepareStatement("SELECT id,user_id,account_number,balance,type,extra,employer_name,employer_address,status FROM accounts WHERE status = ?")) {
            ps.setString(1, status);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(rowToAccount(rs));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding accounts by status: " + status, e);
        }
        return list;
    }

    @Override
    public BankAccount save(BankAccount account) {
        try (java.sql.Connection conn = util.DbUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("INSERT INTO accounts(user_id,account_number,balance,type,extra,employer_name,employer_address,status) VALUES(?,?,?,?,?,?,?,?)", java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, account.getUserId());
            ps.setString(2, account.getAccountNumber());
            ps.setDouble(3, account.getBalance());
            String type = account.getClass().getSimpleName().toUpperCase();
            ps.setString(4, type);
            String extra = "";
            if (account instanceof model.account.ChequeAccount c) extra = Double.toString(c.getOverdraftLimit());
            if (account instanceof model.account.SavingsAccount s) extra = Double.toString(s.getInterestRate());
            if (account instanceof model.account.InvestmentAccount i) extra = i.getInvestmentType();
            ps.setString(5, extra);
            // employer fields (only for ChequeAccount)
            String empName = "";
            String empAddr = "";
            if (account instanceof model.account.ChequeAccount c) {
                try { empName = c.getEmployer(); empAddr = c.getEmployerAddress(); } catch (Exception ignored) {}
            }
            ps.setString(6, empName);
            ps.setString(7, empAddr);
            ps.setString(8, account.getStatus() == null ? "PENDING" : account.getStatus());
            ps.executeUpdate();
            try (java.sql.ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) account.setId(keys.getLong(1));
            }
            return account;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving account: " + account.getAccountNumber(), e);
            return account;
        }
    }

    @Override
    public void update(BankAccount account) {
        try (java.sql.Connection conn = util.DbUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("UPDATE accounts SET balance = ?, extra = ?, employer_name = ?, employer_address = ?, status = ? WHERE id = ?")) {
            ps.setDouble(1, account.getBalance());
            String extra = "";
            if (account instanceof model.account.ChequeAccount c) extra = Double.toString(c.getOverdraftLimit());
            if (account instanceof model.account.SavingsAccount s) extra = Double.toString(s.getInterestRate());
            if (account instanceof model.account.InvestmentAccount i) extra = i.getInvestmentType();
            ps.setString(2, extra);
            String empName = "";
            String empAddr = "";
            if (account instanceof model.account.ChequeAccount c) {
                try { empName = c.getEmployer(); empAddr = c.getEmployerAddress(); } catch (Exception ignored) {}
            }
            ps.setString(3, empName);
            ps.setString(4, empAddr);
            ps.setString(5, account.getStatus());
            ps.setLong(6, account.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating account: " + account.getId(), e);
        }
    }

    @Override
    public void delete(long id) {
        try (java.sql.Connection conn = util.DbUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("DELETE FROM accounts WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting account: " + id, e);
        }
    }

    @Override
    public List<BankAccount> findAll() {
        List<BankAccount> list = new ArrayList<>();
        try (java.sql.Connection conn = util.DbUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("SELECT id,user_id,account_number,balance,type,extra,employer_name,employer_address,status FROM accounts")) {
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(rowToAccount(rs));
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding all accounts", e);
        }
        return list;
    }

    private BankAccount rowToAccount(java.sql.ResultSet rs) throws java.sql.SQLException {
        String type = rs.getString("type");
        long id = rs.getLong("id");
        long userId = rs.getLong("user_id");
        String acctNo = rs.getString("account_number");
        double bal = rs.getDouble("balance");
        String extra = rs.getString("extra");
        String status = rs.getString("status");
        BankAccount account;
        if ("CHEQUEACCOUNT".equalsIgnoreCase(type) || "CHEQUE".equalsIgnoreCase(type)) {
            double od = 0;
            try { od = Double.parseDouble(extra); } catch (Exception ignored) {}
            model.account.ChequeAccount c = new model.account.ChequeAccount(id, userId, acctNo, bal, od);
            // set employer fields if present
            try { c.setEmployer(rs.getString("employer_name")); c.setEmployerAddress(rs.getString("employer_address")); } catch (Exception ignored) {}
            account = c;
        } else if ("SAVINGSACCOUNT".equalsIgnoreCase(type) || "SAVINGS".equalsIgnoreCase(type)) {
            double ir = 0;
            try { ir = Double.parseDouble(extra); } catch (Exception ignored) {}
            model.account.SavingsAccount s = new model.account.SavingsAccount(id, userId, acctNo, bal, ir);
            account = s;
        } else {
            model.account.InvestmentAccount ia = new model.account.InvestmentAccount(id, userId, acctNo, bal, extra == null ? "" : extra);
            account = ia;
        }
        account.setStatus(status == null ? "PENDING" : status);
        return account;
    }
}
