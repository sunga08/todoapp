package todoapp.web;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import todoapp.core.user.domain.ProfilePicture;
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
    public ProfilePicture profilePicture(UserSession userSession) {
        return userSession.getUser().getProfilePicture();
    }
}
