package model;

/**
 * Customer entity representing a bank customer. This is a simple model with
 * personal attributes; existing code still uses `User` for persistence but
 * Customer can be adopted where a richer domain model is needed.
 */
public class Customer {
    private long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String address;
    private String maritalStatus;
    private String email;
    private String username;
    private String employer;
    private String employerAddress;

    public Customer() {}

    public Customer(long id, String firstName, String lastName, String address, String maritalStatus, String email, String username) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = (firstName == null ? "" : firstName) + (lastName == null ? "" : " " + lastName);
        this.address = address;
        this.maritalStatus = maritalStatus;
        this.email = email;
        this.username = username;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getMaritalStatus() { return maritalStatus; }
    public void setMaritalStatus(String maritalStatus) { this.maritalStatus = maritalStatus; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmployer() { return employer; }
    public void setEmployer(String employer) { this.employer = employer; }

    public String getEmployerAddress() { return employerAddress; }
    public void setEmployerAddress(String employerAddress) { this.employerAddress = employerAddress; }
}
