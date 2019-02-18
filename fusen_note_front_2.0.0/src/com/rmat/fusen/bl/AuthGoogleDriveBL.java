package com.rmat.fusen.bl;

import java.io.IOException;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.Credential;
import com.rmat.fusen.bl.function.*;
import com.rmat.fusen.util.LoggingUtil;

/**
 * Session Bean implementation class AuthGoogleDriveBL
 */
@Stateless
public class AuthGoogleDriveBL implements AuthoGoogleDrive {

    /**
     * Default constructor. 
     */
    public AuthGoogleDriveBL() {
        // TODO Auto-generated constructor stub
    }

    
    /**
     * 
     * @param req
     * @param res
     * @return 0:authorized
     *         1:go to re-authorization
     *         2:top
     *         99: fatal error 
     */
	public int doAuthoraization(HttpServletRequest req,HttpServletResponse res){
		
		String methodname = "AuthGoogleDriveBL.doAuthoraization()";
		
		try{

			//check "code"
			// if "Not allowed" is selected in googleauth page, a user will be redirected to top page.
			if(req.getParameter("from_google") != null && req.getParameter("code") == null){
				return 2;
			}
				
			GoogleOAuthManager manager = new GoogleOAuthManager(req);
			//GoogleOAuthClient client = new GoogleOAuthClient(req);
			//Credential credential = client.getActiveCredential();

			Credential credential = manager.getActiveCredential();
			
			return 0;
		} 
		catch (GoogleOAuthManager.NoRefreshTokenException e) {
			
		    String redirecturl = e.getAuthorizationUrl();
		    req.setAttribute(GoogleOAuthManager.REDIRECT_URL_KEY, redirecturl);

		    LoggingUtil.out(LoggingUtil.INFO, methodname, "No refresh token found. Re-authorizing.");
		    return 1;

		}
		catch (IOException e) {
			String message = String.format(
		          "An error happened while reading credentials: %s", e.getMessage());
			LoggingUtil.out(LoggingUtil.ERROR, methodname, message);
			LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
		    return 99;
		}
		catch(Exception e) {

			LoggingUtil.out(LoggingUtil.ERROR, methodname, "An error happened:");
			LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
			return 99;
		}
		
	}
	
	
    /**
     * 
     * @param req
     * @param res
     * @return 0:logout success & go to top
     *         99: fatal error 
     */
	public int doLogOut(HttpServletRequest req,HttpServletResponse res){
		
		String methodname = "AuthGoogleDriveBL.doLogOut()";
		
		try{
			
			GoogleOAuthManager manager = new GoogleOAuthManager(req);
			manager.deleteActiveCredentials();
			
			req.getSession().removeAttribute(GoogleOAuthManager.USER_ID_KEY);
			req.getSession().removeAttribute(GoogleOAuthManager.USER_EMAIL_KEY);
			
			
			return 0;
		} 
		catch(Exception e) {

			LoggingUtil.out(LoggingUtil.ERROR, methodname, "An error happened:");
			LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
			return 99;
		}
		
	}
	
}
