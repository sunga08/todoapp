package todoapp.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import todoapp.security.UserSessionHolder;
import todoapp.web.model.UserProfile;

import java.util.Objects;

@RestController
public class UserRestController {
    private final UserSessionHolder userSessionHolder;

    public UserRestController(UserSessionHolder userSessionHolder) {
        this.userSessionHolder = userSessionHolder;
    }

    @GetMapping("/api/user/profile")
    public ResponseEntity<UserProfile> getUserProfile(HttpSession session) {
        var userSession = userSessionHolder.get();
        if (Objects.nonNull(userSession)) {
            return ResponseEntity.ok(new UserProfile(userSession.getUser()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
