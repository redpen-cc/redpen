/**
 * redpen: a text inspection tool
 * Copyright (c) 2014-2015 Recruit Technologies Co., Ltd. and contributors
 * (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import org.apache.commons.cli.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.ServletContext;
import java.net.URL;
import java.security.ProtectionDomain;

public class RedPenRunner {

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("h", "help", false, "help");

        OptionBuilder.withLongOpt("port");
        OptionBuilder.withDescription("port number");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("PORT");
        options.addOption(OptionBuilder.create("p"));

        OptionBuilder.withLongOpt("conf");
        OptionBuilder.withDescription("configuration file");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("CONFFILE");
        options.addOption(OptionBuilder.create("c"));


        options.addOption("v", "version", false,
                "print the version information and exit");

        CommandLineParser parser = new BasicParser();
        CommandLine commandLine = null;

        try {
            commandLine = parser.parse(options, args);
        } catch (Exception e) {
            printHelp(options);
            System.exit(-1);
        }

        String configFileName = "/conf/redpen-conf.xml";
        int portNum = 8080;

        if (commandLine.hasOption("h")) {
            printHelp(options);
            System.exit(0);
        }
        if (commandLine.hasOption("v")) {
            System.out.println("1.0");
            System.exit(0);
        }
        if (commandLine.hasOption("c")) {
            configFileName = commandLine.getOptionValue("c");
        }

        if (commandLine.hasOption("p")) {
            portNum = Integer.parseInt(commandLine.getOptionValue("p"));
        }

        final String contextPath = System.getProperty("redpen.home", "/");

        Server server = new Server(portNum);
        ProtectionDomain domain = RedPenRunner.class.getProtectionDomain();
        URL location = domain.getCodeSource().getLocation();

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath(contextPath);
        webapp.setWar(location.toExternalForm());
        ServletContext context = webapp.getServletContext();
        context.setAttribute("redpen.conf.path", configFileName);
        server.setHandler(webapp);
        server.start();
        server.join();
    }

    private static void printHelp(Options opt) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("redpen-server", opt);
    }
}
