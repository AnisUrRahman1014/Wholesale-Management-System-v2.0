package Model;

/**
 *
 * @author Anis Ur Rahman
 */
public class Role {
    private String roleName;
    private int payPerHour;

    public Role(String roleName, int payPerHour) {
        this.roleName = roleName;
        this.payPerHour = payPerHour;
    }

    public String getRoleName() {
        return roleName;
    }

    public int getPayPerHour() {
        return payPerHour;
    }
    
    
}
