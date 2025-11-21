package dao.jdbc;

import dao.TransactionDAO;
import model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class TransactionDAOImpl implements TransactionDAO {

    private static final Logger logger = Logger.getLogger(TransactionDAOImpl.class.getName());

    @Override
    public Optional<Transaction> findById(long id) {
        try (java.sql.Connection conn = util.DbUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("SELECT id,account_id,amount,type,timestamp,description FROM transactions WHERE id = ?")) {
            ps.setLong(1, id);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Transaction t = new Transaction(rs.getLong("id"), rs.getLong("account_id"), rs.getDouble("amount"), rs.getString("type"), rs.getTimestamp("timestamp").toLocalDateTime(), rs.getString("description"));
                    return Optional.of(t);
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding transaction by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Transaction> findByAccountId(long accountId) {
        List<Transaction> list = new ArrayList<>();
        try (java.sql.Connection conn = util.DbUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("SELECT id,account_id,amount,type,timestamp,description FROM transactions WHERE account_id = ? ORDER BY timestamp DESC")) {
            ps.setLong(1, accountId);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Transaction(rs.getLong("id"), rs.getLong("account_id"), rs.getDouble("amount"), rs.getString("type"), rs.getTimestamp("timestamp").toLocalDateTime(), rs.getString("description")));
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding transactions by account id: " + accountId, e);
        }
        return list;
    }

    @Override
    public Transaction save(Transaction tx) {
        try (java.sql.Connection conn = util.DbUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("INSERT INTO transactions(account_id,amount,type,timestamp,description) VALUES(?,?,?,?,?)", java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, tx.getAccountId());
            ps.setDouble(2, tx.getAmount());
            ps.setString(3, tx.getType());
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(tx.getTimestamp()));
            ps.setString(5, tx.getDescription());
            ps.executeUpdate();
            try (java.sql.ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) tx.setId(keys.getLong(1)); }
            return tx;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error saving transaction: " + tx.getDescription(), e);
            return tx;
        }
    }

    @Override
    public void update(Transaction tx) {
        try (java.sql.Connection conn = util.DbUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("UPDATE transactions SET amount = ?, type = ?, timestamp = ?, description = ? WHERE id = ?")) {
            ps.setDouble(1, tx.getAmount());
            ps.setString(2, tx.getType());
            ps.setTimestamp(3, java.sql.Timestamp.valueOf(tx.getTimestamp()));
            ps.setString(4, tx.getDescription());
            ps.setLong(5, tx.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating transaction: " + tx.getId(), e);
        }
    }

    public List<Transaction> findAll() {
        List<Transaction> list = new ArrayList<>();
        try (java.sql.Connection conn = util.DbUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("SELECT id,account_id,amount,type,timestamp,description FROM transactions ORDER BY timestamp DESC")) {
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Transaction(rs.getLong("id"), rs.getLong("account_id"), rs.getDouble("amount"), rs.getString("type"), rs.getTimestamp("timestamp").toLocalDateTime(), rs.getString("description")));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}
