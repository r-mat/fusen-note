package com.rmat.fusen.sessionvoid.client;


import java.util.Properties;

import javax.naming.InitialContext;

import com.rmat.fusen.sessionvoid.control.SessionVoidControl;

public class StopSessionVoid {
	
	public static void main(String args[]){
		
		System.out.println("**************************************************");
		System.out.println("** FusenNote SessionVoid 1.0 stop");
		System.out.println("**************************************************");
		
		String JNDI_NAME = "fusen_sessionvoid/SessionVoidControlImpl/remote";
		//String JNDI_NAME = "fusen_sessionvoid/SessionVoidControl/remote";
		SessionVoidControl control;
		
		Properties props = new Properties();
		props.put(InitialContext.INITIAL_CONTEXT_FACTORY,"org.jnp.interfaces.NamingContextFactory");
		//props.put(InitialContext.INITIAL_CONTEXT_FACTORY,"org.jboss.naming.NamingContextFactory");
		props.put(InitialContext.URL_PKG_PREFIXES,"org.jboss.naming:org.jnp.interfaces");
		props.put(InitialContext.PROVIDER_URL, "jnp://localhost:1099");
		
		try{
			System.out.println("remote call for ejb.... : " + JNDI_NAME);
			
			InitialContext context = new InitialContext(props);
			control = (SessionVoidControl)context.lookup(JNDI_NAME);
			
			System.out.println("successfully called" + JNDI_NAME);
			
			System.out.println("stopping timer...." + JNDI_NAME);
			control.stopTimer();
			
		}
		catch(Exception e){
			System.out.println("Error happened...");
			e.printStackTrace();
		}
	}
}
