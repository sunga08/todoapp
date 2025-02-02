package todoapp.web;

import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import todoapp.core.user.application.ChangeUserProfilePicture;
import todoapp.core.user.domain.ProfilePicture;
import todoapp.core.user.domain.ProfilePictureStorage;
import todoapp.security.UserSession;
import todoapp.security.UserSessionHolder;
import todoapp.web.model.UserProfile;

import java.util.Objects;

@RestController
public class UserRestController {

    private final ChangeUserProfilePicture changeUserProfilePicture;
    private final ProfilePictureStorage profilePictureStorage;
    private final UserSessionHolder userSessionHolder; //변경된 프로필 갱신을 위함
    private final Logger log = LoggerFactory.getLogger(getClass());

    public UserRestController(ChangeUserProfilePicture changeUserProfilePicture, ProfilePictureStorage profilePictureStorage, UserSessionHolder userSessionHolder) {
        this.changeUserProfilePicture = changeUserProfilePicture;
        this.profilePictureStorage = profilePictureStorage;
        this.userSessionHolder = userSessionHolder;
    }

    @RolesAllowed(UserSession.ROLE_USER)  //로그인된 사용자가 아니면 이 컨트롤러에 접근 불가
    @GetMapping("/api/user/profile") //상황에 따라 응답이 달라진다면 ResponseEntity 직접 이용
    public UserProfile userProfile(UserSession userSession) { //UserSession은 더이상 null이 될 수 없음
        return new UserProfile(userSession.getUser());
    }

    @PostMapping("/api/user/profile-picture")
    public UserProfile changeProfilePicture(UserSession userSession, MultipartFile profilePicture) {
        log.debug("profile-picture: {}", profilePicture);

        //업로드된 프로필 이미지 파일 저장하기
        var profilepPictureUri = profilePictureStorage.save(profilePicture.getResource()); //임시 저장소에 저장된 리소스가 전달됨

        //프로필 이미지 변경 후 세션 갱신하기
        var updatedUser = changeUserProfilePicture.change(userSession.getName(), new ProfilePicture(profilepPictureUri)); //변경된 사용자 정보 반환
        userSessionHolder.set(new UserSession(updatedUser));//유저 세션 업데이트

        return new UserProfile(updatedUser);
    }
}
