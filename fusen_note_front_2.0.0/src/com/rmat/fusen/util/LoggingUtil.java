package com.rmat.fusen.util;


import javax.servlet.ServletRegistration;
import javax.servlet.ServletRequest;

import org.apache.log4j.*;

public class LoggingUtil {

	private static Logger logger;
	private static Logger accessjournallogger;
	
	public static final String DEBUG ="debug";
	public static final String INFO ="info";
	public static final String WARN ="warn";
	public static final String ERROR ="error";
	/*
	ErrorLevel
	SERVERE[ERROR]
	
	WARNING[WARN]
	
	INFO[INFO]
	
	CONFIG[DEBUG]
	FINE
	FINER
	FINEST
	*/
	
	static {
		System.out.println("LogginUtil start");
		logger = org.apache.log4j.Logger.getLogger("com.rmat.fusen.applog");
		accessjournallogger = org.apache.log4j.Logger.getLogger("com.rmat.fusen.journallog");

		System.out.println("Application log logger=" + logger.getName());
		System.out.println("Application log level=" + logger.getLevel().toString());
	}
	
	public static void out(String loglevel,String methodname,String message){
		
			if(methodname == null){
				methodname = "NULL";
			}
			if(message == null ){
				message = "NULL";
			}

			if(logger != null){
				if(loglevel.equals("debug")){
					logger.debug("[" + methodname + "]" + message);
				}
				else if(loglevel.equals("info")){
					logger.info("[" + methodname + "]" + message);
				}
				else if(loglevel.equals("warn")){
					logger.warn("[" + methodname + "]" + message);
				}
				else if(loglevel.equals("error")){
					logger.error("[" + methodname + "]" + message);
				}
				else{
				
				}
			}

	}
	
	public static void out(String loglevel,String message){
		

		if(message == null ){
			message = "NULL";
		}

		if(logger != null){
			if(loglevel.equals("debug")){
				logger.debug("[]" + message);
			}
			else if(loglevel.equals("info")){
				logger.info("[]" + message);
			}
			else if(loglevel.equals("warn")){
				logger.warn("[]" + message);
			}
			else if(loglevel.equals("error")){
				logger.error("[]" + message);
			}
			else{
			
			}
		}

	}
	
	public static void out(String loglevel,String methodname, Throwable exception){
		
		if(methodname == null){
			methodname = "NULL";
		}
		
		if(logger != null){
			if(loglevel.equals("debug")){
				logger.debug("[" + methodname + "]", exception);
			}
			else if(loglevel.equals("info")){
				logger.info("[" + methodname + "]", exception);
			}
			else if(loglevel.equals("warn")){
				logger.warn("[" + methodname + "]", exception);
			}
			else if(loglevel.equals("error")){
				logger.error("[" + methodname + "]", exception);
			}
			else{
			
			}
		}
	}
	
	public static void out(String loglevel,Throwable exception){
		

		if(logger != null){
			if(loglevel.equals("debug")){
				logger.debug("[]", exception);
			}
			else if(loglevel.equals("info")){
				logger.info("[]", exception);
			}
			else if(loglevel.equals("warn")){
				logger.warn("[]", exception);
			}
			else if(loglevel.equals("error")){
				logger.error("[]", exception);
			}
			else{
			
			}
		}

	}
	
	//for access journal log
	public static void journal(String message){
		accessjournallogger.info(message);
	}
}
