package apivk;

public class UserFioDr {
    private String firstName;
    private String lastName;
    private String birthUser;

    public UserFioDr() {

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBirthUser() {
        return birthUser;
    }

    public void setBirthUser(String birthUser) {
        this.birthUser = birthUser;
    }

    public String getFullName() {
        return "" + firstName + "" + lastName;
    }
}
