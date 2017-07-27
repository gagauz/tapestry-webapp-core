package org.apache.tapestry5.web.server;

import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

public final class MainApplication {
    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        TomcatConfigurator.configureTomcat(tomcat);
        tomcat.start();
    }

}
