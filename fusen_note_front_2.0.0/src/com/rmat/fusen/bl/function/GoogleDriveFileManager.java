package com.rmat.fusen.bl.function;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.Drive.Files;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.ParentReference;
import com.rmat.fusen.util.LoggingUtil;


/**
 * Object that handle the files of googledrive.
 * @author r-matsumura
 *
 */

public class GoogleDriveFileManager {

	/**
	 * JsonFactory to use in parsing JSON.
	 */
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();

	/**
	 * HttpTransport to use for external requests.
	 */
	private static final HttpTransport TRANSPORT = new NetHttpTransport();
	
	/**
	 * Googledrive HTTPRequest handler
	 */
	private Drive driveservice;
	
	//***************************************************************
	//Private Method
	//***************************************************************
	private Drive getDriveService(HttpServletRequest req, HttpServletResponse res,Credential credential) throws Exception{
		
		String methodname = "GoogleDriveFileManager.getDriveService()";
		
		LoggingUtil.out(LoggingUtil.DEBUG, methodname, "start");
		
		try {
			if(credential != null){
				
				return new Drive.Builder(TRANSPORT, JSON_FACTORY,credential).build();
			}else{
				return null;
			}
		} catch (Exception e) {
			LoggingUtil.out(LoggingUtil.ERROR, methodname, "Drive object can't be obtained.");
			throw e;
		}
	}
	

	
	
	//***************************************************************
	//Constructor 
	//***************************************************************
	public GoogleDriveFileManager(HttpServletRequest req, HttpServletResponse res,Credential credential){
		
		String methodname = "GoogleDriveFileManager()";
		driveservice = null;
		
		LoggingUtil.out(LoggingUtil.DEBUG, methodname, "start");
		
		try{
			driveservice = getDriveService(req, res,credential);
		}
		catch(Exception e){
			LoggingUtil.out(LoggingUtil.ERROR, methodname, e);

		}
	}
	
	//***************************************************************
	//Public Method
	//***************************************************************
	/**
	 * 
	 * 
	 * @param file_id
	 * @return
	 * @throws IOException
	 */
	public InputStream getFileFromID(String file_id) throws IOException{

		String methodname = "GoogleDriveFileManager.getFileFromID()";
		LoggingUtil.out(LoggingUtil.DEBUG, methodname, "start");
		
		File file = null;
		try{
			// get file's metadata
			if(!file_id.equals("") && file_id != null){
				if(driveservice != null){
					file = driveservice.files().get(file_id).execute();
				}
				else{
					LoggingUtil.out(LoggingUtil.INFO, methodname, "driveservice is null.");
					return null;
				}
			}
			else{
				LoggingUtil.out(LoggingUtil.INFO, methodname, "file_id is null.");
				return null;
			}
			
			// get file
			if (file.getDownloadUrl() != null && file.getDownloadUrl().length() > 0) {
				HttpResponse resp = driveservice.getRequestFactory().buildGetRequest(new GenericUrl(file.getDownloadUrl())).execute();

				return resp.getContent();
				 
			}
			else {
				LoggingUtil.out(LoggingUtil.INFO, methodname, "can't obtain file's metadata.");
				return null;
			}
		}
		catch(IOException ioe){
			LoggingUtil.out(LoggingUtil.INFO, methodname, "can't obtain file.");
			throw ioe;
		}
	    
	}
	
	/**
	 * 
	 * @param filename
	 * @param mimetype
	 * @param maxResults
	 * @return
	 * @throws IOException
	 */
	public List<File> searchFile(String filename , String mimetype, int maxResults) throws IOException{
		
		String methodname = "GoogleDriveFileManager.searchFile()";
		LoggingUtil.out(LoggingUtil.DEBUG, methodname, "start");
		
		List<File> result = new ArrayList<File>();
		
		if(maxResults <= 0){
			return result;
		}
		
		
		if(filename != null && mimetype != null ){
			Files.List request = driveservice.files().list().setQ("title = '" + filename + "' and mimeType = '" + mimetype + "' and trashed = false");
			
			List<File> subfiles;
			do {
		        try {
		          FileList files = request.execute();
		          subfiles = files.getItems();
		          for(int i=0;i<subfiles.size();i++){
		        	  if(result.size() < maxResults){
		        		  result.add(subfiles.get(i));
		        	  }else{
		        		  break;
		        	  }
		        	  
		          }
		          
		          request.setPageToken(files.getNextPageToken());
		        } catch (IOException e) {
		          System.out.println("An error occurred: " + e);
		          request.setPageToken(null);
		        }
		      } while (request.getPageToken() != null &&
		               request.getPageToken().length() > 0 &&
		               result.size() < maxResults);

		    
		    return result;
		}
		else{
			throw new NullPointerException();
		}
	    
	    
	}
	
	/**
	 * 
	 * @param filename
	 * @param mimetype
	 * @param maxResults
	 * @param parentdirId
	 * @return
	 * @throws IOException
	 */
	public List<File> searchFile(String filename , String mimetype, String parentdirId, int maxResults) throws IOException{
		
		String methodname = "GoogleDriveFileManager.searchFile()";
		LoggingUtil.out(LoggingUtil.DEBUG, methodname, "start");
		
		List<File> result = new ArrayList<File>();
		
		if(maxResults <= 0 || parentdirId == null){
			return result;
		}		
		
		if(filename != null && mimetype != null ){
			Files.List request = driveservice.files().list().setQ("title = '" + filename + "' and mimeType = '" + mimetype + " ' and '" + parentdirId + "' in parents and trashed = false");
			
			List<File> subfiles;
			do {
		        try {
		          FileList files = request.execute();
		          subfiles = files.getItems();
		          for(int i=0;i<subfiles.size();i++){
		        	  if(result.size() < maxResults){
		        		  result.add(subfiles.get(i));
		        	  }else{
		        		  break;
		        	  }
		        	  
		          }
		          
		          request.setPageToken(files.getNextPageToken());
		        } catch (IOException e) {
		          System.out.println("An error occurred: " + e);
		          request.setPageToken(null);
		        }
		      } while (request.getPageToken() != null &&
		               request.getPageToken().length() > 0 &&
		               result.size() < maxResults);

		    
		    return result;
		}
		else{
			throw new NullPointerException();
		}
	    
	    
	}
	
	/**
	 * 
	 * @param title
	 * @param description
	 * @param mimetype
	 * @param parentdirId
	 * @param content
	 * @return File
	 * @throws IOException
	 */
	public File uploadFile(String title, String description, String mimetype, InputStreamContent content) throws IOException {
		
		String methodname = "GoogleDriveFileManager.uploadFile()";
		LoggingUtil.out(LoggingUtil.DEBUG, methodname, "start");
		
		File body = new File();
		body.setTitle(title);
		body.setDescription(description);
		body.setMimeType(mimetype);
		body.setEditable(false);
		 // Set the parent folder.
		/*
	    if (parentdirId != null && parentdirId.length() > 0) {
	      body.setParents(
	          Arrays.asList(new ParentReference().setId(parentdirId)));
	    }
	    */
	    
		try{
			File file = driveservice.files().insert(body, content).execute();
			
			LoggingUtil.out(LoggingUtil.DEBUG, methodname, "end: Title:" + file.getTitle() + " MIME:" + file.getMimeType() + " FileID:" + file.getId());
			return file;
		}catch(IOException ioe){
			LoggingUtil.out(LoggingUtil.ERROR, methodname,"File upload failed...");
			throw ioe;
		}
		
	}
	
	/**
	 * 
	 * @param file
	 * @param newTitle
	 * @param newDescription
	 * @param newMimeType
	 * @param newRevision
	 * @param newContent
	 * @return
	 * @throws IOException
	 */
	public File updateExistingFile(File file, String newTitle,
		      String newDescription, String newMimeType, boolean newRevision,InputStreamContent newContent) throws IOException {
		
		String methodname = "GoogleDriveFileManager.updateExistingFile()";
		LoggingUtil.out(LoggingUtil.DEBUG, methodname, "start");
		
		String id = file.getId();
		
		file.setTitle(newTitle);
		file.setDescription(newDescription);
		file.setMimeType(newMimeType);
		file.setEditable(false);
		
		 // File's new content.
		
		try{
			// Send the request to the API.
		    File updatedFile = driveservice.files().update(id, file, newContent).execute();

		    LoggingUtil.out(LoggingUtil.DEBUG, methodname, "end: Title:" + updatedFile.getTitle() + " MIME:" + updatedFile.getMimeType() + " FileID:" + updatedFile.getId());
		    
			return updatedFile;
		}catch(IOException ioe){
			LoggingUtil.out(LoggingUtil.ERROR, methodname,"File upload failed...");
			throw ioe;
		}
		
	}
	
	/**
	 * 
	 * @param file_id
	 * @throws IOException
	 */
	public void deleteFile(String file_id) throws IOException{
		String methodname = "GoogleDriveFileManager.deleteFile()";
		LoggingUtil.out(LoggingUtil.DEBUG, methodname, "start");
		
		try{
			driveservice.files().delete(file_id).execute();
		}
		catch(IOException ioe){
			LoggingUtil.out(LoggingUtil.ERROR, methodname,"File upload failed...");
			throw ioe;
		}
	}
}
