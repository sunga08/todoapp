package todoapp.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import todoapp.core.user.application.RegisterUser;
import todoapp.core.user.application.VerifyUserPassword;
import todoapp.core.user.domain.User;
import todoapp.core.user.domain.UserNotFoundException;
import todoapp.core.user.domain.UserPasswordNotMatchedException;
import todoapp.security.UserSession;
import todoapp.security.UserSessionHolder;

import java.util.Objects;

@Controller
public class LoginController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final VerifyUserPassword verifyUserPassword;
    private final RegisterUser registerUser;

    private final UserSessionHolder userSessionHolder;

    public LoginController(VerifyUserPassword verifyUserPassword, RegisterUser registerUser, UserSessionHolder userSessionHolder) {
        this.verifyUserPassword = Objects.requireNonNull(verifyUserPassword);
        this.registerUser = Objects.requireNonNull(registerUser);
        this.userSessionHolder = Objects.requireNonNull(userSessionHolder);
    }

    @GetMapping("/login")
    public String loginPage() {
        if (Objects.nonNull(userSessionHolder.get())) {
            return "redirect:/todos";
        }

        return "login";
    }

    @PostMapping("/login")
    public String loginPage(@Valid LoginCommand loginCommand, BindingResult bindingResult, Model model) { //BindingResult를 컨트롤러에서 받으면 유효성 오류를 발생시키지 않음(컨트롤러 내에서 처리 필요)
        logger.info(loginCommand.username + " : " + loginCommand.password);

        //입력값 검증 실패시 login 페이지로 돌려보내고, 오류 메시지 노출
        if (bindingResult.hasErrors()) {
            model.addAttribute("bindingResult", bindingResult);
            model.addAttribute("message", "입력 값이 없거나 올바르지 않아요.");
            return "login";
        }

        User user;
        try {
            //비밀번호 일치시 /todos로 리다이렉트
            user = verifyUserPassword.verify(loginCommand.username, loginCommand.password);
        } catch (UserNotFoundException e) {
            //사용자가 없으면 신규 사용자로 등록 후 /todos로 리다이렉트
            user = registerUser.register(loginCommand.username, loginCommand.password);
        }
        userSessionHolder.set(new UserSession(user));

        return "redirect:/todos";
    }

    //비밀번호 미일치시 login 페이지로 돌려보내고, 오류 메시지 노출
    @ExceptionHandler(UserPasswordNotMatchedException.class)
    public String handleUserPasswordNotMatchedException(UserPasswordNotMatchedException error, Model model) {
        model.addAttribute("message", error.getMessage());

        return "login";
    }

    record LoginCommand(@Size(min = 4, max = 20) String username, String password) {}
}
