package com.rmat.fusen.sessionvoid.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import com.rmat.fusen.sessionvoid.util.DBResourceUtil;
import com.rmat.fusen.sessionvoid.util.LoggingUtil;

public class GoogleCredentialVoidDAO {

	private static String DELETE_SQL_PART1 = "delete from fusen.fu_google_credential where update_time + '";
	private static String DELETE_SQL_PART2 = " seconds' < current_timestamp";
	//条件の部分は　where update_time + 'timediff seconds' < current_timestamp となる。
	
	/**
	 * This function deletes all records with the difference between update_time and current_time larger than "timediff"
	 * @param timediff
	 * @return
	 * @throws Exception
	 */
	public int deleteAll(String timediff) throws Exception{
		String methodname = "GoogleCredentialVoidDAO.deleteAll()";
		
		Connection con = null;
		PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        //LoggingUtil.out(LoggingUtil.DEBUG, methodname, "start");
        
        if(timediff == null){
        	return 1;
        }
        
        try{
        	con = DBResourceUtil.getConnection();
			preparedStatement = con.prepareStatement(DELETE_SQL_PART1 + timediff + DELETE_SQL_PART2);
		
			preparedStatement.execute();
			
			//con.commit();
			
			return 0;
        }
        catch(Exception e){
        	//con.rollback();
        	//LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
        	throw e;
        }
        finally{
        	 DBResourceUtil.close(con);
             DBResourceUtil.close(preparedStatement);
             DBResourceUtil.close(resultSet);
        }
	}

}
