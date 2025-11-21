package service;

import dao.UserDAO;
import dao.jdbc.UserDAOImpl;
import model.User;
import util.HashUtil;

import java.util.Optional;

public class AuthService {
    private final UserDAO userDAO = new UserDAOImpl();
    private final dao.BankAccountDAO accountDAO = new dao.jdbc.BankAccountDAOImpl();

    public Optional<User> authenticate(String username, String password) {
        Optional<User> userOpt = userDAO.findByUsername(username);
        if (userOpt.isEmpty()) return Optional.empty();
        User user = userOpt.get();
        String hashed = HashUtil.sha256(password);
        if (hashed.equals(user.getPasswordHash())) {
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public User register(String fullName, String address, String maritalStatus, String username, String email, String password) {
        String pwHash = HashUtil.sha256(password);
        User user = new User(0, username, email, pwHash, fullName, address, maritalStatus, model.Role.CUSTOMER);
        User saved = userDAO.save(user);
        model.account.ChequeAccount acct = new model.account.ChequeAccount(0, saved.getId(), generateAccountNumber(), 0.0, 0.0);
        acct.setStatus("PENDING");
        accountDAO.save(acct);
        return saved;
    }

    private String generateAccountNumber() {
        return "ACCT" + System.currentTimeMillis();
    }
}
