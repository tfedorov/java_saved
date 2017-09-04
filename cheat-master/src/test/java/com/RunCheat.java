package com;

import java.util.Scanner;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.DefaultHandler;
import org.mortbay.jetty.handler.HandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.webapp.WebAppContext;

public class RunCheat {

	/**
	 * Runner for web app by embedded Jetty web container
	 * 
	 * @param argsm
	 */
	public static void main(String[] args) {
		Server server = new Server();

		Connector connector = new SelectChannelConnector();
		connector.setPort(8080);
		server.setConnectors(new Connector[] { connector });

		WebAppContext webappcontext = new WebAppContext();
		webappcontext.setContextPath("/");
		webappcontext.setWar("./src/main/webapp");

		HandlerCollection handlers = new HandlerCollection();
		handlers.setHandlers(new Handler[] { webappcontext,
				new DefaultHandler() });

		server.setHandler(handlers);

		try {
			server.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// launch browser and display (this is optional)
		// BrowserManager.displayUrlInDefault("http://localhost:" +
		// connector.getPort());

		// wait until user presses enter
		Scanner in = new Scanner(System.in);
		in.nextLine();

		try {
			server.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.exit(0);

	}
}
