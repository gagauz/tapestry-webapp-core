package org.apache.tapestry5.web.server;

import org.apache.catalina.startup.Tomcat;

public class TomcatConfigurator {

    public static void configureTomcat(Tomcat tomcat) {
        tomcat.setPort(8080);
    }

}
