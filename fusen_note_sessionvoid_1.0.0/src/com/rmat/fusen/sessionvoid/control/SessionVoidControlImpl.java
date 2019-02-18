package com.rmat.fusen.sessionvoid.control;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.rmat.fusen.sessionvoid.bl.SessionVoidMain;
import com.rmat.fusen.sessionvoid.util.AppProperty;
import com.rmat.fusen.sessionvoid.util.LoggingUtil;

@Stateless
public class SessionVoidControlImpl implements SessionVoidControl {

	private static Timer globaltimer;
	
	@Resource
	private SessionContext context;
	
	@EJB
	private SessionVoidMain mainbl;
	private InitialContext initcontext;
	
	public void createTimer(){
		
		String methodname = "SessionVoidControlImpl.createTimer()";
		
		Long duration = Long.parseLong(AppProperty.getProperty("TIMER_INTERVAL")) * 1000;
		System.out.println("[SessionVoidControlImpl]createTimer() ....GOT the injected TimerService");
		System.out.println("[SessionVoidControlImpl]createTimer() ....interval:" + AppProperty.getProperty("TIMER_INTERVAL") + " sec");
		globaltimer = context.getTimerService().createTimer(0,duration, null);
		
		//LoggingUtil.out(LoggingUtil.INFO, methodname,"started...");
	}
	
	public void stopTimer(){
	
		String methodname = "SessionVoidControlImpl.stopTimer()";
		if(globaltimer != null){
			globaltimer.cancel();
			//LoggingUtil.out(LoggingUtil.INFO, methodname, "Timer is normally canceled.");
		}
		else{
			//LoggingUtil.out(LoggingUtil.INFO, methodname, "Timer is already canceled.");
		}
	}
	
	@Timeout
	public void timeOutHandler(Timer timer){
		
		String methodname = "SessionVoidControlImpl.timeOutHandler()";
		System.out.println("AAAAAAAAAAAAAAAAAAAA");
		try{
			initcontext = new InitialContext();
			mainbl = (SessionVoidMain)initcontext.lookup(AppProperty.getProperty("APPNAME") + "/SessionVoidMainImpl/local");
			
			mainbl.execute();
		}
		catch(Exception e){
			//LoggingUtil.out(LoggingUtil.ERROR, methodname, "Error occurred... Timer is canceled.");
			e.printStackTrace();
			timer.cancel();
		}
	}
}
