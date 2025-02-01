package todoapp.security.web.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import todoapp.core.foundation.NotImplementedException;
import todoapp.security.UnauthorizedAccessException;
import todoapp.security.UserSessionHolder;
import todoapp.security.web.RolesAllowedSupport;

import java.util.Objects;

/**
 * Role(역할) 기반으로 사용자가 사용 권한을 확인하는 인터셉터 구현체이다.
 *
 * @author springrunner.kr@gmail.com
 */
public class RolesVerifyHandlerInterceptor implements HandlerInterceptor, RolesAllowedSupport {

    private final UserSessionHolder userSessionHolder;
    private final Logger log = LoggerFactory.getLogger(getClass());

    public RolesVerifyHandlerInterceptor(UserSessionHolder userSessionHolder) {
        this.userSessionHolder = Objects.requireNonNull(userSessionHolder);
    }

    @Override
    public final boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            //로그인 되어 있나?
            var userSession = userSessionHolder.get();
            if (Objects.isNull(userSession)) { //사용자 세션이 비어 있으면
                throw new UnauthorizedAccessException(); //false를 반환하는 대신 원인을 명확하게 하기 위해 예외 반환
            }
        }

        return true;
    }

}
