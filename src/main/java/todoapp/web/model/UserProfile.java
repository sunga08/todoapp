package todoapp.web.model;

import todoapp.core.user.domain.User;

import java.util.Objects;

/**
 * 사용자 프로필 모델
 *
 * @author springrunner.kr@gmail.com
 */
public class UserProfile {

    private static final String DEFAULT_PROFILE_PICTURE_URL = "/profile-picture.png";
    private static final String USER_PROFILE_PICTURE_URL = "/user/profile-picture";

    private final User user;

    public UserProfile(User user) {
        this.user = Objects.requireNonNull(user, "user object must be not null");
    }

    public String getName() {
        return user.getUsername();
    }

    public String getProfilePictureUrl() {
        if (user.hasProfilePicture()) { //프로필 이미지를 변경한 적이 있다면 저장소에 프로필 이미지 URI가 있을 것
            return USER_PROFILE_PICTURE_URL;
        }

        // 프로필 이미지가 없으면 기본 프로필 이미지를 사용한다. (static resource에 있는 이미지)
        return DEFAULT_PROFILE_PICTURE_URL;
    }

    @Override
    public String toString() {
        return "UserProfile [name=%s, profilePictureUrl=%s]".formatted(getName(), getProfilePictureUrl());
    }

}
