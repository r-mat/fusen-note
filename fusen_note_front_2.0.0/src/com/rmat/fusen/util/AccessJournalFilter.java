package com.rmat.fusen.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author r-matsumura
 *
 *
 * Layout
 *
 *
 */
public class AccessJournalFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		
		String methodname = "AccessJournalFilter.init()";
		
		String logmessage = "";

		try{
			
			logmessage = req.getProtocol() + "\t" +
		             ((HttpServletRequest) req).getRequestURL() + "\t" +
                     req.getRemoteAddr() + "\t" +
                     req.getRemoteHost() + "\t" +
                     Integer.toString(req.getRemotePort()) + "\t" +
                     req.getLocalAddr() + "\t" +
                     req.getLocalName() + "\t" +
                     Integer.toString(req.getLocalPort()) + "\t";

			LoggingUtil.journal(logmessage);
		
		}catch(Exception e){
			LoggingUtil.out(LoggingUtil.ERROR,methodname,"Journal log error");
			LoggingUtil.out(LoggingUtil.ERROR, methodname,e);
		}finally{
			chain.doFilter(req, res);
		}
		

	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
