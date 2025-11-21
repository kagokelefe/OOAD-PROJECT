package dao.jdbc;

import dao.UserDAO;
import model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDAOImpl implements UserDAO {

    @Override
    public Optional<User> findById(long id) {
        try (java.sql.Connection conn = util.DbUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("SELECT id,full_name,address,marital_status,username,email,password_hash,role FROM users WHERE id = ?")) {
            ps.setLong(1, id);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User(rs.getLong("id"), rs.getString("username"), rs.getString("email"), rs.getString("password_hash"), rs.getString("full_name"), rs.getString("address"), rs.getString("marital_status"), model.Role.valueOf(rs.getString("role")));
                    return Optional.of(u);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try (java.sql.Connection conn = util.DbUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("SELECT id,full_name,address,marital_status,username,email,password_hash,role FROM users WHERE username = ?")) {
            ps.setString(1, username);
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User(rs.getLong("id"), rs.getString("username"), rs.getString("email"), rs.getString("password_hash"), rs.getString("full_name"), rs.getString("address"), rs.getString("marital_status"), model.Role.valueOf(rs.getString("role")));
                    return Optional.of(u);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        try (java.sql.Connection conn = util.DbUtil.getConnection();
             java.sql.Statement st = conn.createStatement();
             java.sql.ResultSet rs = st.executeQuery("SELECT id,full_name,address,marital_status,username,email,password_hash,role FROM users")) {
            while (rs.next()) {
                list.add(new User(rs.getLong("id"), rs.getString("username"), rs.getString("email"), rs.getString("password_hash"), rs.getString("full_name"), rs.getString("address"), rs.getString("marital_status"), model.Role.valueOf(rs.getString("role"))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public User save(User user) {
        try (java.sql.Connection conn = util.DbUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("INSERT INTO users(full_name,address,marital_status,username,email,password_hash,role) VALUES(?,?,?,?,?,?,?)", java.sql.Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getAddress());
            ps.setString(3, user.getMaritalStatus());
            ps.setString(4, user.getUsername());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPasswordHash());
            ps.setString(7, user.getRole() == null ? "CUSTOMER" : user.getRole().name());
            ps.executeUpdate();
            try (java.sql.ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getLong(1));
                }
            }
            return user;
        } catch (Exception e) {
            e.printStackTrace();
            return user;
        }
    }

    @Override
    public void update(User user) {
        try (java.sql.Connection conn = util.DbUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("UPDATE users SET full_name = ?, address = ?, marital_status = ?, username = ?, email = ?, password_hash = ?, role = ? WHERE id = ?")) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getAddress());
            ps.setString(3, user.getMaritalStatus());
            ps.setString(4, user.getUsername());
            ps.setString(5, user.getEmail());
            ps.setString(6, user.getPasswordHash());
            ps.setString(7, user.getRole().name());
            ps.setLong(8, user.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(long id) {
        try (java.sql.Connection conn = util.DbUtil.getConnection();
             java.sql.PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE id = ?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
