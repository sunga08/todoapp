package todoapp.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractView;
import todoapp.core.todo.application.FindTodos;
import todoapp.core.todo.domain.Todo;
import todoapp.core.todo.domain.support.SpreadsheetConverter;
import todoapp.web.model.SiteProperties;

import java.util.*;

@Controller
public class TodoController {

    private final FindTodos findTodos;
    private final SiteProperties siteProperties;

    public TodoController(SiteProperties siteProperties, FindTodos findTodos) {
        this.siteProperties = Objects.requireNonNull(siteProperties);
        this.findTodos = Objects.requireNonNull(findTodos);
    }

    @RequestMapping("/todos")
    public void todos(Model model) {
        model.addAttribute("site", siteProperties);

//        var siteProperties = new SiteProperties();
//        siteProperties.setAuthor("SungA Cho");
//        siteProperties.setAuthor(environment.getProperty("todoapp.site.author"));
//        siteProperties.setDescription("할일 관리 앱 만들기 워크숍");

//        var mav = new ModelAndView();
//        mav.addObject("site", siteProperties);
//        mav.setViewName("todos");

        //ThymeleafViewResolver -> 내부적으로 접두사(prefix), 접미사(suffix)
        // prefix = classpath:/templates/
        // suffix = .html

        // 접두사+뷰 네임+접미사를 이어붙인 파일 이름을 resource loader가 가져옴
    }

    @RequestMapping(path = "/todos", produces = "text/csv")
    public void downloadTodos(Model model) {
        model.addAttribute(SpreadsheetConverter.convert(findTodos.all()));
    }

    public static class TodoCsvViewResolver implements ViewResolver {

        @Override
        public View resolveViewName(String viewName, Locale locale) throws Exception {
            if ("todos".equals(viewName)) {
                return new TodoCsvView();
            }

            return null;
        }

    }

    public static class TodoCsvView extends AbstractView implements View {

        final Logger log = LoggerFactory.getLogger(getClass());

        public TodoCsvView() {
            setContentType("text/csv");
        }

        @Override
        protected boolean generatesDownloadContent() {
            return true;
        }


        @Override
        protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

            response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"todos.csv\"");
            response.getWriter().println("id,text,completed");

            var todos = (List<Todo>) model.getOrDefault("todos", Collections.emptyList());
            for (var todo : todos) {
                var line = "%s,%s,%s".formatted(todo.getId(), todo.getText(), todo.isCompleted());
                response.getWriter().println(line);
            }

            response.flushBuffer();
        }
    }
}
