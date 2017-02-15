package com.lollywollydoodle.jmxmpserver;

import java.lang.management.ManagementFactory;
import java.io.IOException;
import java.util.Properties;

import javax.management.remote.JMXServiceURL;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;

public class Agent {
	private static Properties args;
	
	public static void premain(String agentArgs) throws Exception {
		try {
			parseArgs(agentArgs);
			
			final JMXServiceURL jmxUrl = new JMXServiceURL("jmxmp", null, 
					Integer.parseInt(args.getProperty("port")));
			final JMXConnectorServer jmxServer = JMXConnectorServerFactory.newJMXConnectorServer(jmxUrl, null, ManagementFactory.getPlatformMBeanServer());
			jmxServer.start();
			
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					try {
						jmxServer.stop();
					}
					catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private static void parseArgs(String agentArgs) {
		args = new Properties();
		
		String[] argsList = agentArgs.split(",");
		for (int i = 0; i < argsList.length; i++) {
			String[] argValue = argsList[i].split("=", 2);
			args.setProperty(argValue[0], argValue[1]);
		}
	}
}
