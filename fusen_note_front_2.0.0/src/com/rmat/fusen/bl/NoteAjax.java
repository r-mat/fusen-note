package com.rmat.fusen.bl;

import javax.ejb.Local;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Local
public interface NoteAjax {
	public int doDownLoad(HttpServletRequest req,HttpServletResponse res,String boardid);
	
	public int doUpLoad(HttpServletRequest req,HttpServletResponse res,String boardid);
}
