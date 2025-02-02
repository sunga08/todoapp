package todoapp.web;

import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import todoapp.security.UserSession;
import todoapp.web.model.UserProfile;

@RestController
public class UserRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());


    @RolesAllowed(UserSession.ROLE_USER)
    @GetMapping("/api/user/profile")
    public UserProfile getUserProfile(UserSession userSession) {
        return new UserProfile(userSession.getUser());
    }

    @PostMapping("/api/user/profile-picture")
    public UserProfile changeProfilePicture(@RequestParam("profilePicture") MultipartFile profilePicture, UserSession userSession) {
        log.debug("profilePicture: {}, {}", profilePicture.getOriginalFilename(), profilePicture.getContentType());

        return new UserProfile(userSession.getUser());
    }
}
