package com.rmat.fusen.bl;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.DailyRollingFileAppender;

import com.rmat.fusen.util.LoggingUtil;

/**
 * Session Bean implementation class TopPageBL
 */
@Stateless
public class TopPageBL implements TopPage {

	
    /**
     * Default constructor. 
     */
    public TopPageBL() {

    }
    
    /**
     * 
     * @param req
     * @param res
     * @return 0 1 99
     */
    public int doShowTopPage(HttpServletRequest req,HttpServletResponse res){
    	
    	String methodname = "TopPageBL.doShowTopPage()";
    	LoggingUtil.out(LoggingUtil.DEBUG, methodname, "start");
    	
    	try{
    		HttpSession session = req.getSession(true);
    		
    		String islogin = (String)session.getAttribute("login_status");
    		if(islogin == null || !islogin.equals("1")){
    			LoggingUtil.out(LoggingUtil.DEBUG, methodname, "Login check:not login");
				return 1;
			}
    	}
    	catch(Exception e){
    		LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
    		return 99;
    	}
    	LoggingUtil.out(LoggingUtil.DEBUG, methodname, "Login check:login");
		return 0;
    	
    }
    
}
