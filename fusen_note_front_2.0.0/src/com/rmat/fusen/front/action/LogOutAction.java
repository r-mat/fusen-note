package com.rmat.fusen.front.action;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.rmat.fusen.bl.AuthoGoogleDrive;
import com.rmat.fusen.bl.TopPage;
import com.rmat.fusen.bl.TopPageBL;




public class LogOutAction extends Action {

	@EJB
	private AuthoGoogleDrive authgoogledrive;
	private InitialContext context;
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		context = new InitialContext();
		authgoogledrive = (AuthoGoogleDrive)context.lookup("AuthGoogleDriveBL/local");

		int ret;
		ret = authgoogledrive.doLogOut(request, response);
		switch(ret){
		case 0:
			return mapping.findForward("to_top");
		case 99:
			return mapping.findForward("system_error");
		default:
			return mapping.findForward("to_top");				
		}

	}
	
}
