package com.rmat.fusen.sessionvoid.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;


public class DBResourceUtil {


    public static Connection getConnection() throws SQLException {
    	String methodname = "DBResourceUtil.getConnection()";
        try{

            //Class.forName(PropertiesUtil.jdbc_driver);
            InitialContext initCon = new InitialContext();
            //LoggingUtil.out(LoggingUtil.DEBUG, methodname, "JDBC name:" + "java:comp/env/" + AppProperty.getProperty("JDBC.RES.NAME"));
             DataSource ds
                = (DataSource)initCon.lookup("java:comp/env/" + AppProperty.getProperty("JDBC.RES.NAME"));
            Connection con = ds.getConnection();
            
            return con;
        }
        catch(NamingException ne){
            throw new SQLException(ne);
        }
        catch(SQLException sqle){
            throw sqle;
        }
    }

    public static void close(Connection con) throws SQLException{

        if(con != null){
            con.close();
        }
    }

    public static void close(PreparedStatement preparedStatement) throws SQLException{

        if(preparedStatement != null){
            preparedStatement.close();
        }
    }

    public static void close(ResultSet resultSet) throws SQLException{

        if(resultSet != null){
            resultSet.close();
        }
    }

}