package stellaburgers.dto;



import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    private String email;
    private String password;
    private String name;
    public User() {}
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
