package todoapp.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import todoapp.web.TodoController;
import todoapp.web.support.servlet.view.CommaSeparatedValuesView;

import java.util.ArrayList;

/**
 * Spring Web MVC 설정 정보이다.
 *
 * @author springrunner.kr@gmail.com
 */
@Configuration
class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //서블릿 루트 경로에서 제공
        registry.addResourceHandler("/assets/**").addResourceLocations("assets/");

        //파일 경로에서 제공
        registry.addResourceHandler("/assets/**").addResourceLocations("file:./files/assets/");

        //클래스패스 경로에서 제공
        registry.addResourceHandler("/assets/**").addResourceLocations("classpath:assets/");

        //여러 경로에서 제공
        registry.addResourceHandler("/assets/**").addResourceLocations("assets/", "file:./files/assets/", "classpath:assets/");
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.viewResolver(new TodoController.TodoCsvViewResolver()); //View Resolver 직접 등록 (컨트롤러가 반환한 뷰 이름을 기반으로 해당 뷰를 찾아 렌더링)

        //registry.enableContentNegotiation();
        // 위와 같이 직접 설정하면, 스프링부트가 구성한 ContentNegotiatingViewResolver 전략이 무시된다.
    }

    /**
     * 스프링부트가 생성한 ContentNegotiatingViewResolver를 조작할 목적으로 작성된 설정 정보이다.
     */
    @Configuration
    public static class ContentNegotiationCustomizer {

        @Autowired
        public void configure(ContentNegotiatingViewResolver viewResolver) {
            // TODO ContentNegotiatingViewResolver 조작하기
            var defaultViews = new ArrayList<>(viewResolver.getDefaultViews());
            defaultViews.add(new CommaSeparatedValuesView());

            viewResolver.setDefaultViews(defaultViews);
        }

    }

}
