package todoapp.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import todoapp.core.user.application.RegisterUser;
import todoapp.core.user.application.VerifyUserPassword;
import todoapp.core.user.domain.User;
import todoapp.core.user.domain.UserNotFoundException;
import todoapp.core.user.domain.UserPasswordNotMatchedException;
import todoapp.web.model.SiteProperties;

import java.util.Objects;

@Controller
public class LoginController {

    private final VerifyUserPassword verifyUserPassword; //비밀번호 검증
    private final RegisterUser registerUser; //새로운 사용자 등록

    private final Logger log = LoggerFactory.getLogger(getClass());

    public LoginController(VerifyUserPassword verifyUserPassword, RegisterUser registerUser) {
        this.verifyUserPassword = Objects.requireNonNull(verifyUserPassword);
        this.registerUser = Objects.requireNonNull(registerUser);
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

        try {
            // 1. 사용자 저장소에 사용자가 있을 경우: 비밀번호 확인 후 로그인 처리
            verifyUserPassword.verify(command.username, command.password);
        } catch (UserNotFoundException e) {
            // 2. 사용자 저장소에 없는 경우: 회원가입 처리 후 로그인 처리
            registerUser.register(command.username, command.password);
        }

        return "redirect:/todos"; //RedirectView로 처리
    }

    record LoginCommand(@Size(min = 4, max = 20) String username, String password) {}
}
