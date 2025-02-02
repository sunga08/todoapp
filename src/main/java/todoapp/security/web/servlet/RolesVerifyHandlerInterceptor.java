package todoapp.security.web.servlet;

import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import todoapp.security.AccessDeniedException;
import todoapp.security.UnauthorizedAccessException;
import todoapp.security.UserSessionHolder;
import todoapp.security.web.RolesAllowedSupport;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Role(역할) 기반으로 사용자가 사용 권한을 확인하는 인터셉터 구현체이다.
 *
 * @author springrunner.kr@gmail.com
 */
public class RolesVerifyHandlerInterceptor implements HandlerInterceptor, RolesAllowedSupport {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public final boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            var roleAllowed = handlerMethod.getMethodAnnotation(RolesAllowed.class); //핸들러 메소드에 애노테이션이 있는지

            if (Objects.isNull(roleAllowed)) { //핸들러가 속해 있는 컨트롤러에 애노테이션이 있는지
                roleAllowed = AnnotatedElementUtils.findMergedAnnotation(handlerMethod.getBeanType(), RolesAllowed.class);
            }

            if (Objects.nonNull(roleAllowed)) { //존재한다면 보호되고 있는 핸들러
                log.debug("verify roles-allowed: {}", roleAllowed);

                //로그인 되어 있나? (인증)
                if (Objects.isNull(request.getUserPrincipal())) { //사용자 세션이 비어 있으면
                    throw new UnauthorizedAccessException(); //false를 반환하는 대신 원인을 명확하게 하기 위해 예외 반환
                }

                //역할은 적절한가? (인가) -> 사용자 세션이 역할을 가지고 있는지 확인
                var matchedRoles = Stream.of(roleAllowed.value())
                        .filter(request::isUserInRole)
                        .collect(Collectors.toSet());

                log.debug("matched roles: {}", matchedRoles);
                if (matchedRoles.isEmpty()) { //매치된 역할이 없음
                    throw new AccessDeniedException();
                }
            }
        }

        return true;
    }

}
