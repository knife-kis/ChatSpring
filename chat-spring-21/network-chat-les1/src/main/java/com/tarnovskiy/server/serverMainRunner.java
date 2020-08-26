package com.tarnovskiy.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class serverMainRunner {
    public static void main(String[] args) {
//        ApplicationContext context = new ClassPathXmlApplicationContext("spring-context.xml");
        ApplicationContext context = new AnnotationConfigApplicationContext(SpringConfig.class);
        ServerMain serverMain = context.getBean("serverMain", ServerMain.class);
        serverMain.start(8189);
    }
}
