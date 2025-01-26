package todoapp.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import todoapp.core.user.application.RegisterUser;
import todoapp.core.user.application.VerifyUserPassword;
import todoapp.core.user.domain.User;
import todoapp.core.user.domain.UserNotFoundException;
import todoapp.core.user.domain.UserPasswordNotMatchedException;
import todoapp.security.UserSession;
import todoapp.security.UserSessionHolder;

import java.util.Objects;

@Controller
@SessionAttributes("user")
public class LoginController {

    private final VerifyUserPassword verifyUserPassword; //비밀번호 검증
    private final RegisterUser registerUser; //새로운 사용자 등록

    private final UserSessionHolder userSessionHolder;

    private final Logger log = LoggerFactory.getLogger(getClass());

    public LoginController(VerifyUserPassword verifyUserPassword, RegisterUser registerUser, UserSessionHolder userSessionHolder) {
        this.verifyUserPassword = Objects.requireNonNull(verifyUserPassword);
        this.registerUser = Objects.requireNonNull(registerUser);
        this.userSessionHolder = Objects.requireNonNull(userSessionHolder);
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        return "login";
    }

    @PostMapping("/login")
    public String loginProcess(@Valid LoginCommand command, BindingResult bindingResult, Model model) {
        log.debug("request command: {}", command);

        if (bindingResult.hasErrors()) {
            model.addAttribute("bindingResult", bindingResult);
            model.addAttribute("message", "입력 값이 없거나 올바르지 않습니다.");

            return "login";
        }

        User user;
        try {
            // 1. 사용자 저장소에 사용자가 있을 경우: 비밀번호 확인 후 로그인 처리
            user = verifyUserPassword.verify(command.username, command.password);
        } catch (UserNotFoundException e) {
            // 2. 사용자 저장소에 없는 경우: 회원가입 처리 후 로그인 처리
            user = registerUser.register(command.username, command.password);
        }
//        model.addAttribute("user", user); //model을 세션에서 유지하겠다..
        userSessionHolder.set(new UserSession(user));

        return "redirect:/todos"; //RedirectView로 처리
    }

    //해당 컨트롤러 안에서만 동작함, 모든 컨트롤러에서 동작하길 원하면 컨트롤러 어드바이스에.. (전역으로 처리되어야 하지 않으면 컨트롤러내에 선언하는것이 처리 흐름이 더 자연스러울 수 있다.)
    @ExceptionHandler(UserPasswordNotMatchedException.class)
    public String handleUserPasswordNotMatchedException(UserPasswordNotMatchedException error, Model model) {
        model.addAttribute("message", "비밀번호가 일치하지 않습니다.");
        return "login";
    }

    record LoginCommand(@Size(min = 4, max = 20) String username, String password) {}
}
