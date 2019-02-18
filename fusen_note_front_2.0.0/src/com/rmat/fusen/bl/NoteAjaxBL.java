package com.rmat.fusen.bl;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.io.BufferDateCache;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.model.App;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.rmat.fusen.bl.function.GoogleDriveFileManager;
import com.rmat.fusen.bl.function.GoogleOAuthManager;
import com.rmat.fusen.file.Note;
import com.rmat.fusen.file.NoteValidator;
import com.rmat.fusen.file.Response;
import com.rmat.fusen.util.AppProperty;
import com.rmat.fusen.util.LoggingUtil;


@Stateless
public class NoteAjaxBL implements NoteAjax {


	/**
	 * 
	 * @param req
	 * @param res
	 * @param boardid
	 * @return (00): normal end (return content itself not Responseobj)
	 *         01: more than 1 files exist
	 *         02: file not found
	 *         99: error
	 *            01: login error
	 *            02: file download error
	 *            03: contents check error
	 */
	public int doDownLoad(HttpServletRequest req,HttpServletResponse res, String boardid){
		
		String methodname = "NoteAjaxBL.doDownLoad()";
		LoggingUtil.out(LoggingUtil.DEBUG, methodname, "start");
		
		res.setContentType("text/html; charset=utf-8");
		Gson gson;
		ArrayList<Note> notes;
		List<File> files;
		String content;
		String content_decoded;
		
		GoogleOAuthManager omanager = null;
		com.rmat.fusen.bl.function.GoogleDriveFileManager fmanager = null;
		Credential credential = null;
		
		//for json response
		Response responseobj = new Response();
		
		//boardid の存在チェックはAction側で実施
		
		//ログインチェック
		try{
			omanager = new GoogleOAuthManager(req);
			credential = omanager.getActiveCredential();
		}catch(Exception e){
			LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
			responseobj.setCode("99");
			responseobj.setMessage("error");
			responseobj.setSubcode("01");
			responseobj.setSubmessage("login error");
			try{
				writeJsonResponse(res, responseobj);
			}
			catch(Exception e2){
				LoggingUtil.out(LoggingUtil.ERROR, methodname, "failed to write json response...");
			}

			return 99;
		}
		
		//属性の取得
		//String appdirid = AppProperty.getProperty("GOOGLEDRIVE_APP_DIR_ID");
		String filename = AppProperty.getProperty("GOOGLEDRIVE_USER_CONTENTS_FILE_NAME");
		String fileextention = AppProperty.getProperty("GOOGLEDRIVE_USER_CONTENTS_FILE_EXTENTION");
		String filemime = AppProperty.getProperty("GOOGLEDRIVE_USER_CONTENTS_FILE_MIME");

		//ファイル名の組立て
		String searchfile = filename + boardid + fileextention;
		
		//Applicationフォルダよりファイルダウンロード
		try{
			
			fmanager = new GoogleDriveFileManager(req, res, credential);
			
			//ファイルの存在確認
			//files = fmanager.searchFile(searchfile, filemime,appdirid, 2);
			files = fmanager.searchFile(searchfile, filemime, 2);
			
			//2個以上ヒットした場合
			if(files.size() > 1){
				LoggingUtil.out(LoggingUtil.INFO, methodname, "more than 1 files exist...");
				
				//最初の１つを削除する
				fmanager.deleteFile(files.get(0).getId());
				LoggingUtil.out(LoggingUtil.INFO, methodname, "deleted 1 file.");
				
				responseobj.setCode("01");
				responseobj.setMessage("more than 1 files exist");

				try{
					writeJsonResponse(res, responseobj);
				}
				catch(Exception e2){
					LoggingUtil.out(LoggingUtil.ERROR, methodname, "failed to download file...");
					return 99;
				}
				
				return 0;
			}
			
			//既存ファイルがなかった場合
			else if(files.size() < 1 || files == null){
				responseobj.setCode("02");
				responseobj.setMessage("file not found");

				try{
				writeJsonResponse(res, responseobj);
				}
				catch(Exception e2){
					LoggingUtil.out(LoggingUtil.ERROR, methodname, "failed to download file...");
					return 99;
				}
			
				return 0;
			}
			
		}
		catch(Exception e){
			LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
			responseobj.setCode("99");
			responseobj.setMessage("error");
			responseobj.setSubcode("02");
			responseobj.setSubmessage("file search error");
			try{
				writeJsonResponse(res, responseobj);
			}
			catch(Exception e2){
				LoggingUtil.out(LoggingUtil.ERROR, methodname, "failed to download file...");
				return 99;
			}
			return 99;
		}
			
		//download file
		try{
			File file = files.get(0);
			InputStream inputstream = fmanager.getFileFromID(file.getId());
			//URLデコーディング
			content = new String(readAll(inputstream),"UTF-8");
			content_decoded = URLDecoder.decode(content,"UTF-8");
			LoggingUtil.out(LoggingUtil.DEBUG, methodname,"json array3:" + content_decoded);

		}	
		catch(Exception e){
			LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
			responseobj.setCode("99");
			responseobj.setMessage("error");
			responseobj.setSubcode("03");
			responseobj.setSubmessage("file download error");
			try{
				writeJsonResponse(res, responseobj);
			}
			catch(Exception e2){
				LoggingUtil.out(LoggingUtil.ERROR, methodname, "failed to download file...");
				return 99;
			}
			return 99;
		}
		
		//translate into json to object
		try{
			notes = new ArrayList<Note>();
			
			gson = new Gson();
			JsonParser parser = new JsonParser();
		    JsonArray array = parser.parse(content_decoded).getAsJsonArray();
		    for(int i=0;i < array.size();i++){
				LoggingUtil.out(LoggingUtil.DEBUG, "start to object:" + Integer.toString(i));
				Note note = gson.fromJson(array.get(i), Note.class);
				notes.add(note);
			}
		    
		}
		catch(Exception e){
			LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
			responseobj.setCode("99");
			responseobj.setMessage("error");
			responseobj.setSubcode("04");
			responseobj.setSubmessage("json parse error");
			try{
				writeJsonResponse(res, responseobj);
			}
			catch(Exception e2){
				LoggingUtil.out(LoggingUtil.ERROR, methodname, "failed to download file...");
			}

			return 99;
		}

		
		//付箋情報の入力値チェック
		int checkresult = 0;
		try{
			for(int i=0;i < notes.size(); i++){
				//check!
				checkresult = NoteValidator.checkNote(notes.get(i));
				if(checkresult != 0){
					throw new Exception();
				}
			}
			
		}
		catch(Exception e){
			LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
			responseobj.setCode("99");
			responseobj.setMessage("error");
			responseobj.setSubcode("03");
			responseobj.setSubmessage("contents check error");
			try{
				writeJsonResponse(res, responseobj);
			}
			catch(Exception e2){
				LoggingUtil.out(LoggingUtil.ERROR, methodname, "failed to write json response...");
			}

			return 99;
		}
		
		//付箋情報の応答
		PrintWriter writer = null;
		try{
			//URLエンコーディングaaaaaa
			//content_decoded = URLEncoder.encode(content, "UTF-8");
			
			LoggingUtil.out(LoggingUtil.DEBUG, methodname,"json array4:" + content);
			
			writer = res.getWriter();
			writer.print(content);
	
		}
		catch(Exception e2){
			LoggingUtil.out(LoggingUtil.ERROR, methodname, "failed to download file...");
		}
		finally{
			if(writer != null){
				writer.close();
			}
		}

		return 0;

	}
	
	/**
	 * 
	 * @param req
	 * @param res
	 * @param boardid
	 * @return 00: nomal end
	 *            01: insert new file
	 *            02: update existing file
	 *         01: more than 1 files exist
	 *         99: error
	 *            01: login error
	 *            02: json parse error
	 *            03: content check error
	 */
	public int doUpLoad(HttpServletRequest req,HttpServletResponse res, String boardid){
		
		String methodname = "NoteAjaxBL.doUpLoad()";
		LoggingUtil.out(LoggingUtil.DEBUG, methodname,"start");
		
		res.setContentType("text/html; charset=utf-8");
		PrintWriter writer;
		BufferedReader bufferedreader;
		InputStreamReader reader;
		Gson gson;
		ArrayList<Note> notes;
		
		GoogleOAuthManager omanager = null;
		GoogleDriveFileManager fmanager = null;
		Credential credential = null;
		
		//for json response
		Response responseobj = new Response();
		
		//boardid の存在チェックはAction側で実施
		
		//ログインチェック
		try{
			omanager = new GoogleOAuthManager(req);
			credential = omanager.getActiveCredential();
		}catch(Exception e){
			LoggingUtil.out(LoggingUtil.ERROR, methodname,"Exception in Login Check.");
			LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
			responseobj.setCode("99");
			responseobj.setMessage("error");
			responseobj.setSubcode("01");
			responseobj.setSubmessage("login error");
			try{
				writeJsonResponse(res, responseobj);
			}
			catch(Exception e2){
				LoggingUtil.out(LoggingUtil.ERROR, methodname, "failed to write json response...");
			}

			return 99;
		}
		
		
		//送信された付箋情報Json文字列をオブジェクトリストへ変換
		try{
			gson = new Gson();
			writer = res.getWriter();

			
			bufferedreader = new BufferedReader(new InputStreamReader(req.getInputStream(), "UTF-8"));
			StringBuffer buf = new StringBuffer();
		    String str;
		    while ((str = bufferedreader.readLine()) != null) {
		            buf.append(str);
		            buf.append("\n");
		    }

		    //URLデコーディング    
		    String data = URLDecoder.decode(buf.toString(),"UTF-8");
		    LoggingUtil.out(LoggingUtil.DEBUG, methodname,"json array1:" + data);
			
			notes = new ArrayList<Note>();
			
			JsonParser parser = new JsonParser();
			JsonArray array = parser.parse(data).getAsJsonArray();
			for(int i=0;i < array.size();i++){
				LoggingUtil.out(LoggingUtil.DEBUG, "start to object:" + Integer.toString(i));
				Note note = gson.fromJson(array.get(i), Note.class);
				notes.add(note);
			}
			
		}
		catch(Exception e){
			LoggingUtil.out(LoggingUtil.ERROR, methodname,"Exception in translation from json string to Object.");
			LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
			responseobj.setCode("99");
			responseobj.setMessage("error");
			responseobj.setSubcode("02");
			responseobj.setSubmessage("json parse error");
			try{
				writeJsonResponse(res, responseobj);
			}
			catch(Exception e2){
				LoggingUtil.out(LoggingUtil.ERROR, methodname, "failed to write json response...");
			}

			return 99;
		}
		
		//付箋情報の入力値チェック
		int checkresult = 0;
		try{
			for(int i=0;i < notes.size(); i++){
				//check!
				checkresult = NoteValidator.checkNote(notes.get(i));
				if(checkresult != 0){
					throw new Exception();
				}
			}
			
		}
		catch(Exception e){
			LoggingUtil.out(LoggingUtil.ERROR, methodname,"Exception in data validation.");
			LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
			responseobj.setCode("99");
			responseobj.setMessage("error");
			responseobj.setSubcode("03");
			responseobj.setSubmessage("contents check error");
			try{
				writeJsonResponse(res, responseobj);
			}
			catch(Exception e2){
				LoggingUtil.out(LoggingUtil.ERROR, methodname, "failed to write json response...");
			}

			return 99;
		}
		
		//属性の取得
		//String appdirid = AppProperty.getProperty("GOOGLEDRIVE_APP_DIR_ID");
		String filename = AppProperty.getProperty("GOOGLEDRIVE_USER_CONTENTS_FILE_NAME");
		String fileextention = AppProperty.getProperty("GOOGLEDRIVE_USER_CONTENTS_FILE_EXTENTION");
		String filemime = AppProperty.getProperty("GOOGLEDRIVE_USER_CONTENTS_FILE_MIME");
		String filedescription = AppProperty.getProperty("GOOGLEDRIVE_USER_CONTENTS_FILE_DESCRIPTION");
		
		//ファイル名の組立て
		String searchfile = filename + boardid + fileextention;
		String description = filedescription + " : " + boardid;
		
		//ファイルロード
		try{
			//String contents = gson.toJson(notes);
			//URLエンコーディング
			String content = URLEncoder.encode(gson.toJson(notes), "UTF-8");
			LoggingUtil.out(LoggingUtil.DEBUG, methodname,"json array2:" + content);
			
			fmanager = new GoogleDriveFileManager(req, res, credential);
			
			//ファイルの存在確認
			//List<File> files = fmanager.searchFile(searchfile, filemime,appdirid, 2);
			List<File> files = fmanager.searchFile(searchfile, filemime, 2);
			
			//2個以上ヒットした場合
			if(files.size() > 1){
				LoggingUtil.out(LoggingUtil.DEBUG, methodname, "more than 1 files exist");
				
				responseobj.setCode("01");
				responseobj.setMessage("more than 1 files exist");
				
				try{
					writeJsonResponse(res, responseobj);
				}
				catch(Exception e2){
					LoggingUtil.out(LoggingUtil.ERROR, methodname, "failed to write json response...");
					return 99;
				}
				
				return 0;
			}
			//1個ヒットした場合
			else if(files.size() == 1){
				LoggingUtil.out(LoggingUtil.DEBUG, methodname, "update the existing file(" + files.get(0).getTitle() + ")");
				
				File oldfile = files.get(0);
				
				//GoogleDriveの既存ファイルをupdate
				InputStreamContent inputstreamcontent = new InputStreamContent(filemime, new ByteArrayInputStream(content.getBytes()));
				//To set length is necessary.
				inputstreamcontent.setLength(content.getBytes().length);
				File file = fmanager.updateExistingFile(oldfile, 
						searchfile, 
						description, 
						filemime, 
						true, 
						inputstreamcontent);
				
				responseobj.setCode("00");
				responseobj.setMessage("normal end");
				responseobj.setSubcode("02");
				responseobj.setSubmessage("update existing file");
				try{
				writeJsonResponse(res, responseobj);
				}
				catch(Exception e2){
					LoggingUtil.out(LoggingUtil.ERROR, methodname, "failed to write json response...");
					return 99;
				}
			
				return 0;
			}
			//既存ファイルがなかった場合
			else{
				LoggingUtil.out(LoggingUtil.DEBUG, methodname, "create a new file");
				
				//GoogleDriveへ新しいファイルをインサート
				InputStreamContent inputstreamcontent = new InputStreamContent(filemime, new ByteArrayInputStream(content.getBytes()));
				//To set length is necessary.
				inputstreamcontent.setLength(content.getBytes().length);
			
				File file = fmanager.uploadFile(searchfile, 
						description, 
						filemime,
						inputstreamcontent);

			
				responseobj.setCode("00");
				responseobj.setMessage("normal end");
				responseobj.setSubcode("01");
				responseobj.setSubmessage("insert new file");
				try{
				writeJsonResponse(res, responseobj);
				}
				catch(Exception e2){
					LoggingUtil.out(LoggingUtil.ERROR, methodname, "failed to write json response...");
					return 99;
				}
			
				return 0;
			}
			
			
		}
		catch(Exception e){
			LoggingUtil.out(LoggingUtil.ERROR, methodname,"Exception in file upload.");
			LoggingUtil.out(LoggingUtil.ERROR, methodname, e);
			responseobj.setCode("99");
			responseobj.setMessage("google drive file upload error");
			try{
				writeJsonResponse(res, responseobj);
			}
			catch(Exception e2){
				LoggingUtil.out(LoggingUtil.ERROR, methodname, "failed to write json response...");
				return 99;
			}
			return 99;
		}
		
	}
	
	private void writeJsonResponse(HttpServletResponse res,Response responseobj) throws Exception{
		if(responseobj != null){
			Gson gson = new Gson();
			PrintWriter writer = null;
			try{
				writer = res.getWriter();
				writer.print(gson.toJson(responseobj));
			}
			catch(Exception e){
				throw e;
			}
			finally{
				if(writer != null){
					writer.close();
				}
			}
		}
		else{
			throw new NullPointerException();
		}
	}
	
	private byte[] readAll(InputStream inputStream) throws IOException {
		
	    ByteArrayOutputStream bout = new ByteArrayOutputStream();
	    byte [] buffer = new byte[1024];
	    while(true) {
	        int len = inputStream.read(buffer);
	        if(len < 0) {
	            break;
	        }
	        bout.write(buffer, 0, len);
	    }
	    return bout.toByteArray();
	}
	
}
