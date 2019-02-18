package com.rmat.fusen.sessionvoid.bl;

import javax.ejb.Stateless;

import com.rmat.fusen.sessionvoid.db.GoogleCredentialVoidDAO;
import com.rmat.fusen.sessionvoid.util.AppProperty;
import com.rmat.fusen.sessionvoid.util.LoggingUtil;

/**
 * 業務ロジック
 * @author r-matsumura
 *
 */
@Stateless
public class SessionVoidMainImpl implements SessionVoidMain {

	public void execute() throws Exception{
		String methodname = "SessionVoidMainImpl.execute()";
		
		//レコード保持時間=データ削除間隔を取得
		String timediff = AppProperty.getProperty("SESSION_DELETE_TIME");
		
		//レコード削除
		try{
			GoogleCredentialVoidDAO dao = new GoogleCredentialVoidDAO();
			dao.deleteAll(timediff);
			//System.out.println("DEBUGGING!!!!");
		}
		  catch(Exception e){
			 // LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
			  throw e;
		}
	}
}
