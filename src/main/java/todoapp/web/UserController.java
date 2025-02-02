package todoapp.web;

import jakarta.annotation.security.RolesAllowed;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import todoapp.core.user.domain.ProfilePicture;
import todoapp.core.user.domain.ProfilePictureStorage;
import todoapp.security.UserSession;

@Controller
public class UserController {

    @RolesAllowed(UserSession.ROLE_USER)
    @GetMapping("/user/profile-picture")
    public ProfilePicture profilePicture(UserSession userSession) {
//        var resource = profilePictureStorage.load(userSession.getUser().getProfilePicture().getUri()); //User의 profilepicture에는 이미지가 어디에 저장되어 있는지 uri를 가지고 있음
//        return ResponseEntity.ok(resource);
        return userSession.getUser().getProfilePicture();
    }
}
