package stellaburgers.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserJson {
    private String success;
    private User user;
    private String accessToken;
    private String refreshToken;
    private String message;

    public UserJson() { }

    public UserJson(String success, User user, String accessToken, String refreshToken) {
        this.success = success;
        this.user = user;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public UserJson(String success, String message) {
        this.success = success;
        this.message = message;
    }
}

