package Model;

/**
 *
 * @author Anis Ur Rahman
 */
public class Employee {
    private String name;
    private String contact;
    private String address;
    private String cnic;
    private String role;
    private int payPerHour;

    public Employee(String name, String contact, String address, String cnic, String role, int payPerHour) {
        this.name = name;
        this.contact = contact;
        this.address = address;
        this.cnic = cnic;
        this.role = role;
        this.payPerHour = payPerHour;
    }

    public String getName() {
        return name;
    }

    public String getContact() {
        return contact;
    }

    public String getAddress() {
        return address;
    }

    public String getCnic() {
        return cnic;
    }

    public String getRole() {
        return role;
    }

    public int getPayPerHour() {
        return payPerHour;
    }
}
