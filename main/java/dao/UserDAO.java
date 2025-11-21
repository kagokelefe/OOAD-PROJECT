package dao;

import model.User;

import java.util.List;
import java.util.Optional;

public interface UserDAO {
    Optional<User> findById(long id);
    Optional<User> findByUsername(String username);
    List<User> findAll();
    User save(User user);
    void update(User user);
    void delete(long id);
}
