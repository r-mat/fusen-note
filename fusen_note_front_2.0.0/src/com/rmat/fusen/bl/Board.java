package com.rmat.fusen.bl;

import javax.ejb.Local;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Local
public interface Board {
	public int doShowBoard(HttpServletRequest req,HttpServletResponse res);
}
