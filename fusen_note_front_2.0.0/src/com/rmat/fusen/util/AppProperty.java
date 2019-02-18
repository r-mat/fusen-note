package com.rmat.fusen.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppProperty {

    //add property here
	private static Properties prop;
	
/*
    public static String jdbc_res_name;
    public static String jdbc_driver;
    public static String jdbc_connectionurl;
    public static String jdbc_database;
    public static String jdbc_user;
    public static String jdbc_password;
    */
    public static String googledrive_client_id;
    public static String googledrive_client_secret;
    public static String googledrive_callbackurl;

	
    static {

        InputStream namefile = null;

        try{
            namefile = AppProperty.class.getClassLoader().getResourceAsStream("/com/rmat/fusen/util/app.properties");
            if(namefile == null){
                throw new IllegalArgumentException("failed to read property-file...");
            }

            prop = new Properties();
            prop.load(namefile);

            /*
            jdbc_res_name = prop.getProperty("JDBC.RES.NAME");
            jdbc_driver = prop.getProperty("JDBC.DRIVER");
            jdbc_connectionurl = prop.getProperty("JDBC.CONNECTIONURL");
            jdbc_database = prop.getProperty("JDBC.DATABASE");
            jdbc_user = prop.getProperty("JDBC.USER");
            jdbc_password = prop.getProperty("JDBC.PASSWORD");
            */
            googledrive_client_id = prop.getProperty("GOOGLEDRIVE_CLIENT_ID");
            googledrive_client_secret = prop.getProperty("GOOGLEDRIVE_CLIENT_SECRET");
            googledrive_callbackurl = prop.getProperty("GOOGLEDRIVE_CALLBACKURL");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
    public static String getProperty(String key){
    	return prop.getProperty(key);
    }
}