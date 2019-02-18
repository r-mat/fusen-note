package com.rmat.fusen.front.action;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.rmat.fusen.bl.TopPage;
import com.rmat.fusen.bl.TopPageBL;




public class TopAction extends Action {

	@EJB
	private TopPage toppage;
	private InitialContext context;
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		context = new InitialContext();
		toppage = (TopPage)context.lookup("TopPageBL/local");
		
		int ret;
		ret = toppage.doShowTopPage(request, response);
		switch(ret){
		case 0:
			return mapping.findForward("to_top");
		case 1:
			return mapping.findForward("to_top");
		case 99:
			return mapping.findForward("system_error");
		default:
			return mapping.findForward("to_top");				
		}

	}
	
}
