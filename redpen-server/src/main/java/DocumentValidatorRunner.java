import java.net.URL;
import java.security.ProtectionDomain;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

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

        server.setHandler(webapp);
        server.start();
        server.join();
    }
}
