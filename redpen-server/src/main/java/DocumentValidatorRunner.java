import java.net.URL;
import java.security.ProtectionDomain;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

import javax.servlet.ServletContext;

public class DocumentValidatorRunner {
    public static void main(String[] args) throws Exception {
        final int port = Integer.parseInt(System.getProperty("port", "8080"));
        final String contextPath = System.getProperty("redpen.home","/");

        Server server = new Server(port);
        ProtectionDomain domain = DocumentValidatorRunner.class.getProtectionDomain();
        URL location = domain.getCodeSource().getLocation();

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath(contextPath);
        webapp.setWar(location.toExternalForm());
        ServletContext context = webapp.getServletContext();
        context.setAttribute("redpen.conf.path", "/conf/dv-conf.xml"); //TODO make path configurable
        server.setHandler(webapp);
        server.start();
        server.join();
    }
}
