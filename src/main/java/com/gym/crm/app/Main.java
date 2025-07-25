package com.gym.crm.app;

import com.gym.crm.app.config.AppConfig;
import com.gym.crm.app.logging.TransactionLoggingFilter;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;
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

        TransactionLoggingFilter filter = new TransactionLoggingFilter();

        FilterDef filterDef = new FilterDef();
        filterDef.setFilterName("transactionLoggingFilter");
        filterDef.setFilter(filter);
        filterDef.setFilterClass(TransactionLoggingFilter.class.getName());
        webContext.addFilterDef(filterDef);

        FilterMap filterMap = new FilterMap();
        filterMap.setFilterName("transactionLoggingFilter");
        filterMap.addURLPattern("/*");
        webContext.addFilterMap(filterMap);

        tomcat.start();
        tomcat.getServer().await();
    }
}
