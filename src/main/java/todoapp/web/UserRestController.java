package todoapp.web;

import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import todoapp.core.user.application.ChangeUserProfilePicture;
import todoapp.core.user.domain.ProfilePicture;
import todoapp.core.user.domain.ProfilePictureStorage;
import todoapp.security.UserSession;
import todoapp.security.UserSessionHolder;
import todoapp.web.model.UserProfile;

@RestController
public class UserRestController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final ProfilePictureStorage profilePictureStorage;
    private final ChangeUserProfilePicture changeUserProfilePicture;
    private final UserSessionHolder userSessionHolder; //변경된 프로필 이미지를 세션으로 갱신 필요

    public UserRestController(ProfilePictureStorage profilePictureStorage, ChangeUserProfilePicture changeUserProfilePicture, UserSessionHolder userSessionHolder) {
        this.profilePictureStorage = profilePictureStorage;
        this.changeUserProfilePicture = changeUserProfilePicture;
        this.userSessionHolder = userSessionHolder;
    }


    @RolesAllowed(UserSession.ROLE_USER)
    @GetMapping("/api/user/profile")
    public UserProfile getUserProfile(UserSession userSession) {
        return new UserProfile(userSession.getUser());
    }

    @PostMapping("/api/user/profile-picture")
    public UserProfile changeProfilePicture(@RequestParam("profilePicture") MultipartFile profilePicture, UserSession userSession) {
        log.debug("profilePicture: {}, {}", profilePicture.getOriginalFilename(), profilePicture.getContentType());

        //1. 업로드된 프로필 이미지 파일 저장하기
        var profilePictureUri = profilePictureStorage.save(profilePicture.getResource()); //멀티파트파일은 리소스 타입으로 핸들링 가능

        //2. 프로필 이미지 변경 후 세션 갱신하기
        var updatedUser = changeUserProfilePicture.change(userSession.getName(), new ProfilePicture(profilePictureUri)); //변경된 사용자 정보가 돌아옴
        userSessionHolder.set(new UserSession(updatedUser));

        return new UserProfile(updatedUser);
    }
}
