package com.gym.crm.app;

import com.gym.crm.app.config.AppConfig;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.io.File;

public class Main {
    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector();

        Context webContext = tomcat.addContext("", new File(".").getAbsolutePath());

        AnnotationConfigWebApplicationContext appContext = new AnnotationConfigWebApplicationContext();
        appContext.register(AppConfig.class);
        appContext.setServletContext(webContext.getServletContext());
        appContext.refresh();

        DispatcherServlet servlet = new DispatcherServlet(appContext);
        Tomcat.addServlet(webContext, "dispatcher", servlet);
        webContext.addServletMappingDecoded("/", "dispatcher");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = "$2a$10$QXB1QANM/95QETWLqUQAuOEOyw.B/XBWs/x2DGHk5/G8wjskmifiq";
        System.out.println(encoder.matches("qwerty1234", hash));

        tomcat.start();
        tomcat.getServer().await();
    }
}
