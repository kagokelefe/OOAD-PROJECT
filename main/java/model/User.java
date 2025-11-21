package model;

public class User {
    private long id;
    private String username;
    private String email;
    private String passwordHash;
    private String fullName;
    private String address;
    private String maritalStatus;
    private Role role;

    public User() {}

    public User(long id, String username, String email, String passwordHash, Role role) {
        this(id, username, email, passwordHash, null, null, null, role);
    }

    public User(long id, String username, String email, String passwordHash, String fullName, String address, String maritalStatus, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.fullName = fullName;
        this.address = address;
        this.maritalStatus = maritalStatus;
        this.role = role;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
