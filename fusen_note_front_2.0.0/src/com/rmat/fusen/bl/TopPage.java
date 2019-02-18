package com.rmat.fusen.bl;

import javax.ejb.Local;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Local
public interface TopPage {

	public int doShowTopPage(HttpServletRequest req,HttpServletResponse res);
}
