public class Customer {

    private Long id;
    private String nationalId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private java.util.Set<account> accounts;

    public Customer() {
    }

    public Customer(Long id, String nationalId, String firstName, String lastName, String email, String phone) {
        this.id = id;
        this.nationalId = nationalId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }

    public Long getId() { return id; }
    public String getNationalId() { return nationalId; }
    public void setNationalId(String nationalId) { this.nationalId = nationalId; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public java.util.Set<account> getAccounts() { return accounts; }
}


