package todoapp.web;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import todoapp.core.user.application.RegisterUser;
import todoapp.core.user.application.VerifyUserPassword;
import todoapp.core.user.domain.UserNotFoundException;
import todoapp.core.user.domain.UserPasswordNotMatchedException;

import java.util.Objects;

@Controller
public class LoginController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final VerifyUserPassword verifyUserPassword;
    private final RegisterUser registerUser;

    public LoginController(VerifyUserPassword verifyUserPassword, RegisterUser registerUser) {
        this.verifyUserPassword = Objects.requireNonNull(verifyUserPassword);
        this.registerUser = Objects.requireNonNull(registerUser);
    }

    @GetMapping("/login")
    public void loginPage() {
    }

    @PostMapping("/login")
    public String loginPage(@Valid LoginCommand loginCommand, BindingResult bindingResult, Model model) {
        logger.info(loginCommand.username + " : " + loginCommand.password);

        //입력값 검증 실패시 login 페이지로 돌려보내고, 오류 메시지 노출
        if (bindingResult.hasErrors()) {
            model.addAttribute("bindingResult", bindingResult); //입력칸 하단에 어떤 오류인지 나타내기 위해 필요
            model.addAttribute("message", "입력 값이 없거나 올바르지 않아요."); //login 버튼 하단 오류 메시지
            return "login";
        }

        try {
            //비밀번호 일치시 /todos로 리다이렉트
            verifyUserPassword.verify(loginCommand.username, loginCommand.password);
        } catch (UserNotFoundException e) {
            //사용자가 없으면 신규 사용자로 등록 후 /todos로 리다이렉트
            registerUser.register(loginCommand.username, loginCommand.password);
        } catch (UserPasswordNotMatchedException e) {
            //비밀번호 미일치시 login 페이지로 돌려보내기
            return "login";
        }

        return "redirect:/todos";
    }

    record LoginCommand(@Size(min = 4, max = 20) String username, String password) {}
}
