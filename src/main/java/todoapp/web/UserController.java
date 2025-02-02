package todoapp.web;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import todoapp.core.user.domain.ProfilePictureStorage;
import todoapp.security.UserSession;

@Controller
public class UserController {

    private final ProfilePictureStorage profilePictureStorage;

    public UserController(ProfilePictureStorage profilePictureStorage) {
        this.profilePictureStorage = profilePictureStorage;
    }

    @RolesAllowed(UserSession.ROLE_USER)
    @GetMapping("/user/profile-picture")
    public ResponseEntity<Resource> profilePicture(UserSession userSession) {
        return ResponseEntity.ok(profilePictureStorage.load(userSession.getUser().getProfilePicture().getUri())); //getUri()=저장된 경로를 반환 -> 리소스를 반환하기 위해 profilePictureStorage 사용
    }
}
