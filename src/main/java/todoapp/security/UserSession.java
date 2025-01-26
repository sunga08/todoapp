package todoapp.security;

import todoapp.core.user.domain.User;

import java.security.Principal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 사용자 세션 모델이다.
 *
 * @author springrunner.kr@gmail.com
 */
public class UserSession implements Principal { //Principal: 인증된 사용자정보 임을 알려주기 위한 인터페이스

    public static final String ROLE_USER = "ROLE_USER";

    private final User user;
    private final Set<String> roles = new HashSet<>();

    public UserSession(User user) {
        this.user = Objects.requireNonNull(user, "user object must be not null");
        this.roles.add(ROLE_USER);
    }

    public User getUser() {
        return user;
    }

    public String getName() {
        return user.getUsername();
    }

    public Set<String> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        return Objects.equals(user, ((UserSession) obj).user);
    }

    @Override
    public String toString() {
        return "UserSession [username=%s, roles=%s]".formatted(user.getUsername(), roles);
    }

}
