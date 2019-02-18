package com.rmat.fusen.front.action;

import javax.ejb.EJB;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.rmat.fusen.bl.Board;
import com.rmat.fusen.bl.TopPage;

public class BoardAction extends Action {

	@EJB
	private Board board;
	private InitialContext context;
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		context = new InitialContext();
		board = (Board)context.lookup("BoardBL/local");
		
		int ret;
		ret = board.doShowBoard(request, response);
		switch(ret){
		case 0:
			return mapping.findForward("show_board_success");
		case 1:
			return mapping.findForward("show_board_error");
		default:
			return mapping.findForward("show_board_error");
		}
	}
	
	
}
