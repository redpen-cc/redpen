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

import cc.redpen.RedPen;
import org.apache.commons.cli.*;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.security.ProtectionDomain;

public class RedPenRunner {

    public static final String STOP_KEY = "STOP_KEY";

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption("h", "help", false, "help");

        OptionBuilder.withLongOpt("port");
        OptionBuilder.withDescription("port number");
        OptionBuilder.hasArg();
        OptionBuilder.withArgName("PORT");
        options.addOption(OptionBuilder.create("p"));

        options.addOption("v", "version", false,
                "print the version information and exit");

        options.addOption(new Option(STOP_KEY, true, "stop key"));

        CommandLineParser parser = new BasicParser();
        CommandLine commandLine = null;

        try {
            commandLine = parser.parse(options, args);
        } catch (Exception e) {
            printHelp(options);
            System.exit(-1);
        }

        int portNum = 8080;

        if (commandLine.hasOption("h")) {
            printHelp(options);
            System.exit(0);
        }
        if (commandLine.hasOption("v")) {
            System.out.println(RedPen.VERSION);
            System.exit(0);
        }
        if (commandLine.hasOption("p")) {
            portNum = Integer.parseInt(commandLine.getOptionValue("p"));
        }

        if (isPortTaken(portNum)) {
            System.err.println("port is taken...");
            System.exit(1);
        }

        final String contextPath = System.getProperty("redpen.home", "/");

        Server server = new Server(portNum);
        ProtectionDomain domain = RedPenRunner.class.getProtectionDomain();
        URL location = domain.getCodeSource().getLocation();

        HandlerList handlerList = new HandlerList();
        if(commandLine.hasOption(STOP_KEY)) {
            // add Shutdown handler only when STOP_KEY is specified
            ShutdownHandler shutdownHandler = new ShutdownHandler(commandLine.getOptionValue(STOP_KEY));
            handlerList.addHandler(shutdownHandler);
        }

        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath(contextPath);
        webapp.setWar(location.toExternalForm());

        handlerList.addHandler(webapp);
        server.setHandler(handlerList);

        server.start();
        server.join();
    }

    private static void printHelp(Options opt) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("redpen-server", opt);
    }

    private static boolean isPortTaken(int portNum) {
        boolean portTaken = false;
        try (ServerSocket socket = new ServerSocket(portNum)) {
            // do nothing
        } catch (IOException e) {
            System.err.println("Detect: port is taken"); // TODO: use logger
            portTaken = true;
        }
        return portTaken;
    }
}
