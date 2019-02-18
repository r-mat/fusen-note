package com.rmat.fusen.bl;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.Credential;

import com.rmat.fusen.bl.function.GoogleOAuthManager;
import com.rmat.fusen.util.LoggingUtil;

@Stateless
public class BoardBL implements Board {

    /**
     * 
     * @param req
     * @param res
     * @return 0:to board
     *         1:top
     *         99: fatal error 
     */
	public int doShowBoard(HttpServletRequest req, HttpServletResponse res) {

		String methodname = "BoardBL.doShowBoard";
		
		Credential credential = null;
		GoogleOAuthManager manager = null;
		
		LoggingUtil.out(LoggingUtil.DEBUG,methodname,"start");
		
		//login check
		try{
			manager = new GoogleOAuthManager(req);
			credential = manager.getActiveCredential();
			
			return 0;
		}
		catch(Exception e){
			LoggingUtil.out(LoggingUtil.INFO, methodname, "session timeout");
			return 1;
		}
		
	}

}
