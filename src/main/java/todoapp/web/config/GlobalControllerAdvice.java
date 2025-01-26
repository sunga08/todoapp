package todoapp.web.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import todoapp.web.model.SiteProperties;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final SiteProperties siteProperties;

    public GlobalControllerAdvice(SiteProperties siteProperties) {
        this.siteProperties = siteProperties;
    }

    //모든 응답 값에 이 모델을 추가하겠다. (컨트롤러에 각각 추가하지 않아도 됨)
    @ModelAttribute("site")
    public SiteProperties siteProperties() {
        return siteProperties;
    }
}
