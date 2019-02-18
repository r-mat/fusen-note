package com.rmat.fusen.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;

import javax.ejb.Stateless;

import com.rmat.fusen.util.DBResourceUtil;
import com.rmat.fusen.util.LoggingUtil;

public class GoogleCredentialDAO {

	private static String SELECT_FROM_USERID_SQL = "select userid,access_token,refresh_token,to_char(update_time,'YYYY/MM/DD HH24:MI:SS') as upddate from fusen.fu_google_credential where userid = ?";
	private static String INSERT_SQL = "insert into fusen.fu_google_credential values(?,?,?,to_timestamp(?,'YYYY/MM/DD HH24:MI:SS'))";
	private static String DELETE_SQL = "delete from fusen.fu_google_credential where userid = ?";
	private static String UPDATE_SQL = "update fusen.fu_google_credential set access_token = ? , refresh_token = ? , update_time = to_timestamp(?,'YYYY/MM/DD HH24:MI:SS') where userid = ?";
	
	/**
	 * 
	 * @param userid
	 * @return
	 * @throws Exception
	 */
	public GoogleCredentialEntity select(String userid) throws Exception{
		
		String methodname ="GoogleCredentialDAO.select()";
		
		GoogleCredentialEntity entity = null;
		Connection con = null;
		PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        LoggingUtil.out(LoggingUtil.DEBUG, methodname, "start");
        
        if(userid == null){
        	return null;
        }
        
		try{

			con = DBResourceUtil.getConnection();
			preparedStatement = con.prepareStatement(SELECT_FROM_USERID_SQL);
			preparedStatement.setString(1, userid);
			resultSet = preparedStatement.executeQuery();
			
			while(resultSet.next()){

				SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				entity = new GoogleCredentialEntity(resultSet.getString("userid"),
						resultSet.getString("access_token"), 
						resultSet.getString("refresh_token"),
						format.parse(resultSet.getString("upddate")));
				LoggingUtil.out(LoggingUtil.DEBUG, methodname, "userId=" + entity.getUserId() + ";access_token=" + entity.getAccessToken() + ";refresh_token=" + entity.getRefreshToken());

            }


            return entity;
            
		}
		catch(Exception e){
			LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
			throw e;
		}
		finally{
       	    DBResourceUtil.close(con);
            DBResourceUtil.close(preparedStatement);
            DBResourceUtil.close(resultSet);
       }
	}
	
	public int insert(GoogleCredentialEntity entity) throws Exception{
		String methodname = "GoogleCredentialDAO.insert()";
		
		Connection con = null;
		PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        LoggingUtil.out(LoggingUtil.DEBUG, methodname, "start");
        
        if(entity == null || entity.getUserId() == null || entity.getAccessToken() == null || entity.getRefreshToken() == null || entity.getUpd_date() == null){
        	return 1;
        }
        
        try{
        	con = DBResourceUtil.getConnection();
			preparedStatement = con.prepareStatement(INSERT_SQL);
			preparedStatement.setString(1, entity.getUserId());
			preparedStatement.setString(2, entity.getAccessToken());
			preparedStatement.setString(3, entity.getRefreshToken());
			preparedStatement.setString(4, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(entity.getUpd_date()));
			
			preparedStatement.execute();
			
			//con.commit();
			
			return 0;
        }
        catch(Exception e){
        	//con.rollback();
        	LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
        	throw e;
        }
        finally{
        	 DBResourceUtil.close(con);
             DBResourceUtil.close(preparedStatement);
             DBResourceUtil.close(resultSet);
        }
        
	}
	
	/**
	 * 
	 * @param userid
	 * @return
	 * @throws Exception
	 */
	public int delete(String userid) throws Exception{
		String methodname = "GoogleCredentialDAO.delete()";
		
		Connection con = null;
		PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        LoggingUtil.out(LoggingUtil.DEBUG, methodname, "start");
        
        if(userid == null){
        	return 1;
        }
        
        try{
        	con = DBResourceUtil.getConnection();
			preparedStatement = con.prepareStatement(DELETE_SQL);
			preparedStatement.setString(1, userid);
		
			preparedStatement.execute();
			
			//con.commit();
			
			return 0;
        }
        catch(Exception e){
        	//con.rollback();
        	LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
        	throw e;
        }
        finally{
        	 DBResourceUtil.close(con);
             DBResourceUtil.close(preparedStatement);
             DBResourceUtil.close(resultSet);
        }
	}
	
	public int update(GoogleCredentialEntity entity) throws Exception{
		String methodname = "GoogleCredentialDAO.update()";
		
		Connection con = null;
		PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        LoggingUtil.out(LoggingUtil.DEBUG, methodname, "start");
        
        if(entity == null || entity.getUserId() == null || entity.getAccessToken() == null || entity.getRefreshToken() == null || entity.getUpd_date() == null){
        	return 1;
        }
        
        try{
        	con = DBResourceUtil.getConnection();
			preparedStatement = con.prepareStatement(UPDATE_SQL);
			preparedStatement.setString(1, entity.getAccessToken());
			preparedStatement.setString(2, entity.getRefreshToken());
			preparedStatement.setString(3, new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(entity.getUpd_date()));
			preparedStatement.setString(4, entity.getUserId());
		
			preparedStatement.execute();
			
			//con.commit();
			
			return 0;
        }
        catch(Exception e){
        	//con.rollback();
        	LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
        	throw e;
        }
        finally{
        	 DBResourceUtil.close(con);
             DBResourceUtil.close(preparedStatement);
             DBResourceUtil.close(resultSet);
        }
	}
	
	public int insertOrUpdate(GoogleCredentialEntity entity) throws Exception{
		String methodname = "GoogleCredentialDAO.insertOrUpdate()";
        
		LoggingUtil.out(LoggingUtil.DEBUG, methodname, "start");
		
        if(entity == null || entity.getUserId() == null || entity.getAccessToken() == null || entity.getRefreshToken() == null || entity.getUpd_date() == null){
        	return 1;
        }
        
        try{
        	GoogleCredentialEntity rtnentity = select(entity.getUserId());
        	if(rtnentity == null){
        		insert(entity);
        	}
        	else{
        		update(entity);
        	}

			return 0;
        }
        catch(Exception e){
        	LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
        	throw e;
        }
        finally{

        }
	}
}
