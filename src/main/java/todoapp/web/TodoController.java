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

    public TodoController(FindTodos findTodos) {
        this.findTodos = Objects.requireNonNull(findTodos);
    }

    @RequestMapping("/todos")
    public void todos() {
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

    @RequestMapping(path = "/todos", produces = "text/csv") //produces는 클라이언트 요청의 Accept 헤더에 따라 적합한 핸들러 메서드를 결정
    public void downloadTodos(Model model) {
        model.addAttribute(SpreadsheetConverter.convert(findTodos.all()));
//        model.addAttribute("todos", findTodos.all());
    }

    //커스텀 뷰 리졸버
    public static class TodoCsvViewResolver implements ViewResolver {

        @Override
        public View resolveViewName(String viewName, Locale locale) throws Exception {
            if ("todos".equals(viewName)) { //컨트롤러에서 todos 뷰 이름이 전달되면 TodoCsvView 객체를 반환해 CSV 응답을 처리
                return new TodoCsvView();
            }

            return null;
        }

    }

    //CSV 생성 및 다운로드 처리
    public static class TodoCsvView extends AbstractView implements View {

        private final Logger log = LoggerFactory.getLogger(TodoController.class);

        public TodoCsvView() {
            setContentType("text/csv"); //응답 컨텐트 타입 설정 (브라우저가 CSV 파일로 인식하도록 함)
        }

        @Override
        protected boolean generatesDownloadContent() {
            return true;
        }


        //컨트롤러에서 제공한 모델(todos 객체 리스트)을 사용해 CSV 응답 본문 생성
        @Override
        @SuppressWarnings("unchecked")
        protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
            log.info("render model as csv content");

            response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"todos.csv\""); //브라우저가 CSV 파일을 다운로드하도록 설정
            response.getWriter().println("id,text,completed"); //CSV 파일의 헤더

            //본문 내용
            var todos = (List<Todo>) model.getOrDefault("todos", Collections.emptyList());
            for (var todo : todos) {
                var line = "%s,%s,%s".formatted(todo.getId(), todo.getText(), todo.isCompleted());
                response.getWriter().println(line);
            }

            response.flushBuffer();
        }
    }
}
