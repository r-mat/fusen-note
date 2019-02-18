package com.rmat.fusen.front.action;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.rmat.fusen.bl.NoteAjax;
import com.rmat.fusen.util.LoggingUtil;


public class NoteAjaxDownloadAction extends Action {

	@EJB
	private NoteAjax noteajax;
	private InitialContext context;
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String methodname = "NoteAjaxDownloadAction.execute()";
		
		context = new InitialContext();
		noteajax = (NoteAjax)context.lookup("NoteAjaxBL/local");
		
		//boardid is 1 now....
		noteajax.doDownLoad(request, response,"1");
				
		return null;
		
	}
}
