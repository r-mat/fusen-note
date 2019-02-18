package com.rmat.fusen.sessionvoid.client;


import java.util.Properties;

import javax.naming.InitialContext;

import com.rmat.fusen.sessionvoid.control.SessionVoidControl;


public class StartSessionVoid {
	
	public static void main(String args[]){
		
		System.out.println("[sessionvoid-start]**************************************************");
		System.out.println("[sessionvoid-start]** FusenNote SessionVoid 1.0 start");
		System.out.println("[sessionvoid-start]**************************************************");
		
		String JNDI_NAME = "fusen_sessionvoid/SessionVoidControlImpl/remote";
		//String JNDI_NAME = "fusen_sessionvoid/SessionVoidControl/remote";
		SessionVoidControl control;
		
		Properties props = new Properties();
		props.put(InitialContext.INITIAL_CONTEXT_FACTORY,"org.jnp.interfaces.NamingContextFactory");
		//props.put(InitialContext.INITIAL_CONTEXT_FACTORY,"org.jboss.naming.NamingContextFactory");
		props.put(InitialContext.URL_PKG_PREFIXES,"org.jboss.naming:org.jnp.interfaces");
		props.put(InitialContext.PROVIDER_URL, "jnp://localhost:1099");

		try{
			System.out.println("[sessionvoid-start]getting initialcontext.... : " + JNDI_NAME);
			
			InitialContext context = new InitialContext(props);
			System.out.println("AAAAAAAAAAAAAA");
			
			System.out.println("[sessionvoid-start]looking up ejb.... : " + JNDI_NAME);
			control = (SessionVoidControl)context.lookup(JNDI_NAME);
						
			System.out.println("[sessionvoid-start]starting timer...." + JNDI_NAME);
			control.createTimer();
			
		}
		catch(Exception e){
			System.out.println("[sessionvoid-start]Error happened...");
			e.printStackTrace();
		}
	}
}
