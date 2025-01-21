package todoapp;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

/**
 * @author springrunner.kr@gmail.com
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class TodoApplication {

    @Autowired
    private ServletContext servletContext;

    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }

    @PostConstruct
    public void init() {
        System.out.println("Servlet Context Path: " + servletContext.getRealPath("/"));
        System.out.println("Working Directory: " + System.getProperty("user.dir"));

        ResourceLoader resourceLoader = new DefaultResourceLoader();
        Resource resourceUrl = resourceLoader.getResource("classpath:assets/");
        System.out.println("Classpath Resource URL: " + resourceUrl);
    }

}
