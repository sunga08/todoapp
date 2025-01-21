package todoapp.data;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import todoapp.core.todo.domain.Todo;
import todoapp.core.todo.domain.TodoIdGenerator;
import todoapp.core.todo.domain.TodoRepository;

//매번 데이터가 생성되는 것 인지
@ConditionalOnProperty(name = "todoapp.data.initialize", havingValue = "true")
@Component
public class TodoDataInitializer implements InitializingBean, ApplicationRunner, CommandLineRunner {

    private final TodoIdGenerator todoIdGenerator; //DB가 없어도 실행하기 위해 필요
    private final TodoRepository todoRepository;

    public TodoDataInitializer(TodoIdGenerator todoIdGenerator, TodoRepository todoRepository) {
        this.todoIdGenerator = todoIdGenerator;
        this.todoRepository = todoRepository;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //1. InitializingBean
        // 프로퍼티가 빈에 설정된 다음에 호출되는 메서드 -> 값이 잘 셋팅되어 있는지 검증하려면 이곳에서..

        todoRepository.save(Todo.create("Task one", todoIdGenerator));
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //2.ApplicationRunner
        todoRepository.save(Todo.create("Task two", todoIdGenerator));
    }

    @Override
    public void run(String... args) throws Exception {
        //3.CommandLineRunner
        todoRepository.save(Todo.create("Task three", todoIdGenerator));
    }
}
