package com.rmat.fusen.bl;

import javax.ejb.Local;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Local
public interface AuthoGoogleDrive {

	public int doAuthoraization(HttpServletRequest req,HttpServletResponse res);
	public int doLogOut(HttpServletRequest req,HttpServletResponse res);
}
