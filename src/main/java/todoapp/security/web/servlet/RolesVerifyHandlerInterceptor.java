package todoapp.security.web.servlet;

import com.fasterxml.jackson.databind.introspect.Annotated;
import jakarta.annotation.security.RolesAllowed;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import todoapp.core.foundation.NotImplementedException;
import todoapp.security.AccessDeniedException;
import todoapp.security.UnauthorizedAccessException;
import todoapp.security.UserSessionHolder;
import todoapp.security.web.RolesAllowedSupport;

import java.util.Objects;
import java.util.Set;
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
    public final boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            var roleAllowed = handlerMethod.getMethodAnnotation(RolesAllowed.class); //핸들러 메서드의 RolesAllowed를 꺼내라

            if (Objects.isNull(roleAllowed)) {
                roleAllowed = AnnotatedElementUtils.findMergedAnnotation(
                        handlerMethod.getBeanType(), RolesAllowed.class
                );
            }

            if (Objects.nonNull(roleAllowed)) {
                log.debug("verify roles-allowed: {}", roleAllowed);

                //사용자 세션을 사용하지 않고 request를 사용하는 방식으로 변경
                //1. 로그인 되어 있나? (인증)
                if (Objects.isNull(request.getUserPrincipal())) { //로그인 안되어 있으면 세션이 비어있을 것
                    throw new UnauthorizedAccessException(); //false를 반환해도 되지만 이유를 명시하기 위해 예외 발생시킴
                }

                //2. 역할은 적절한가? (인가)
                var matchedRoles = Stream.of(roleAllowed.value())
                        .filter(request::isUserInRole) //hasRole을 사용자 세션이 가지고 있는지?
                        .collect(Collectors.toSet());

                log.debug("matched roles: {}", matchedRoles);
                if (matchedRoles.isEmpty()) {
                    throw new AccessDeniedException();
                }
            }

        }

        return true; //다음 핸들러를 실행해도 된다.
    }

}
