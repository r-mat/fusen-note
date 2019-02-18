package com.rmat.fusen.file;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.rmat.fusen.util.AppProperty;
import com.rmat.fusen.util.LoggingUtil;

public class NoteValidator {

	public static final String MATCH_ASCII = "^[\\u0020-\\u007E]+$";
	public static final String MATCH_HANKAKU = "^[0-9a-zA-Z]+$";
	public static final String MATCH_HANKAKU_SUUJI = "^[0-9]+$";
	public static final String MATCH_COLOR = "^#[0123456789ABCDEFabcedf]+$";
	public static final String MATCH_NOTE_ID = "^" + AppProperty.getProperty("NOTE_ID_HEAD") + "[0-9]+$";
	public static final String MATCH_POSITION = "^[0-9.]+px$";

	/**
	 * 
	 * @param note
	 * @return
	 */
	public static int checkNote(Note note){
			
		//付箋ID
		String noteid = note.getNoteid();
		int ret_noteid = checkNoteID(noteid);
		if(ret_noteid != 0){
			return 1;
		}
		
		//表示順番号
		String order = note.getOrder();
		int ret_order = checkOrder(order);
		if(ret_order != 0){
			return 1;
		}
		
		//付箋タイトル
		String title = note.getTitle();
		int ret_title = checkTitle(title);
		if(ret_title != 0){
			return 1;
		}
		
		//付箋内容
		String content = note.getContent();
		int ret_content = checkContent(content);
		if(ret_content != 0){
			return 1;
		}
		
		//付箋位置X
		String positionx = note.getPositionx();
		int ret_positionx = checkPositionX(positionx);
		if(ret_positionx != 0){
			return 1;
		}
		
		//付箋位置Y
		String positiony = note.getPositiony();
		int ret_positiony = checkPositionY(positiony);
		if(ret_positiony != 0){
			return 1;
		}
		
		//付箋サイズX
		String width = note.getWidth();
		int ret_width = checkWidth(width);
		if(ret_width != 0){
			return 1;
		}
		
		//付箋サイズY
		String height = note.getHeight();
		int ret_height = checkHeight(height);
		if(ret_height != 0){
			return 1;
		}
		
		//更新日時(YYYYMMDDHH24MISS)
		String updatetime = note.getUpdate_time();
		int ret_updtime = checkUpdateTime(updatetime);
		if(ret_updtime != 0){
			return 1;
		}
		
		return 0;
	}
	
	private static int checkNoteID(String id){
		
		//Null check
		if(id == null){
			LoggingUtil.out(LoggingUtil.ERROR, "noteid null");
			return -1;
		}
		
		//pattern check
		if(!id.matches(MATCH_NOTE_ID)){
			LoggingUtil.out(LoggingUtil.ERROR, "noteid pattern error");
			return 1;
		}
		
		/*
		//character check
		if(!id.matches(MATCH_HANKAKU)){
			LoggingUtil.out(LoggingUtil.ERROR, "noteid character type error");
			return 2;
		}
		*/
		return 0;
	}
	
	private static int checkTitle(String title){
		
		//Null check
		if(title == null){
			title = "";
		}
				
		//length check
		if(title.length() > Integer.parseInt(AppProperty.getProperty("NOTE_TITLE_SIZE"))){
			LoggingUtil.out(LoggingUtil.ERROR, "title length error");
			return 1;
		}
		
		return 0;
	}
	
	private static int checkOrder(String order){
		
		//Null check
		if(order == null){
			LoggingUtil.out(LoggingUtil.ERROR, "order null");
			return -1;
		}
				
		//numeric check
		int int_order;
		try{
			int_order = Integer.parseInt(order);
		}catch(NumberFormatException nfe){
			LoggingUtil.out(LoggingUtil.ERROR, "order not numeric");
			return 1;
		}
		
		//max check
		if(int_order > Integer.parseInt(AppProperty.getProperty("MAX_NOTE_NUMBER")) || int_order <= 0){
			LoggingUtil.out(LoggingUtil.ERROR, "order max error");
			return 2;
		}
		return 0;
	}
	
	private static int checkContent(String content){
		
		//Null check
		if(content == null){
			content = "";
		}
		
		//length check
		if(content.length() > Integer.parseInt(AppProperty.getProperty("NOTE_CONTENT_SIZE"))){
			LoggingUtil.out(LoggingUtil.ERROR, "order max error");
			return 1;
		}
		
		return 0;
	}
	
	private static int checkPositionX(String positionx){
		
		//Null check
		if(positionx == null){
			LoggingUtil.out(LoggingUtil.ERROR, "positionx null");
			return -1;
		}
		
		//pattern check
		if(!positionx.matches(MATCH_POSITION)){
					LoggingUtil.out(LoggingUtil.ERROR, "positionx pattern[numeric + px] error");
					return 1;
		}
		
		//numeric check
		double double_positionx;
		try{
			double_positionx = Double.parseDouble(positionx.substring(0, positionx.length()-2));
		}catch(NumberFormatException nfe){
			LoggingUtil.out(LoggingUtil.ERROR, "positionx not numeric (" + positionx.substring(0, positionx.length()-2) + ")");
			return 2;
		}
			
		//max check
		if(double_positionx > Double.parseDouble(AppProperty.getProperty("BOARD_WIDTH")) || double_positionx < 0){
			LoggingUtil.out(LoggingUtil.ERROR, "positionx max error");
			return 3;
		}
		return 0;
	}

	private static int checkPositionY(String positiony){
		
		//Null check
		if(positiony == null){
			LoggingUtil.out(LoggingUtil.ERROR, "positiony null");
			return -1;
		}
		
		//pattern check
		if(!positiony.matches(MATCH_POSITION)){
			LoggingUtil.out(LoggingUtil.ERROR, "positiony pattern[numeric + px] error");
			return 1;
		}
		
		//numeric check
		double double_positiony;
		try{
			double_positiony = Double.parseDouble(positiony.substring(0, positiony.length()-2));
		}catch(NumberFormatException nfe){
			LoggingUtil.out(LoggingUtil.ERROR, "positiony not numeric (" + positiony.substring(0, positiony.length()-2) + ")");
			return 2;
		}
				
		//max check
		if(double_positiony > Double.parseDouble(AppProperty.getProperty("BOARD_HEIGHT")) || double_positiony < 0){
			LoggingUtil.out(LoggingUtil.ERROR, "positiony max error");
			return 3;
		}
		return 0;
	}
	
	
	private static int checkWidth(String width){
		
		//Null check
		if(width == null){
			LoggingUtil.out(LoggingUtil.ERROR, "width null");
			return -1;
		}
		
		//pattern check
		if(!width.matches(MATCH_POSITION)){
			LoggingUtil.out(LoggingUtil.ERROR, "width pattern[numeric + px] error");
			return 1;
		}
		
		//numeric check
		double double_width;
		try{
			double_width = Double.parseDouble(width.substring(0, width.length()-2));
		}catch(NumberFormatException nfe){
			LoggingUtil.out(LoggingUtil.ERROR, "width not numeric (" + width.substring(0, width.length()-2) + ")");
			return 2;
		}
				
		//max check
		if(double_width > Double.parseDouble(AppProperty.getProperty("BOARD_WIDTH")) || double_width < 0){
			LoggingUtil.out(LoggingUtil.ERROR, "width max error");
			return 3;
		}
		return 0;
	}
	
	private static int checkHeight(String height){
		
		//Null check
		if(height == null){
			LoggingUtil.out(LoggingUtil.ERROR, "height null");
			return -1;
		}
		
		//pattern check
		if(!height.matches(MATCH_POSITION)){
			LoggingUtil.out(LoggingUtil.ERROR, "height pattern[numeric + px] error");
			return 1;
		}
		
		//numeric check
		double double_height;
		try{
			double_height = Double.parseDouble(height.substring(0, height.length()-2));
		}catch(NumberFormatException nfe){
			LoggingUtil.out(LoggingUtil.ERROR, "height not numeric (" + height.substring(0, height.length()-2) + ")");
			return 2;
		}
				
		//max check
		if(double_height > Double.parseDouble(AppProperty.getProperty("BOARD_HEIGHT")) || double_height < 0){
			LoggingUtil.out(LoggingUtil.ERROR, "height max error");
			return 3;
		}
		return 0;
	}
	

	private static int checkUpdateTime(String updatetime){
		
		//Null check
		if(updatetime == null){
			LoggingUtil.out(LoggingUtil.ERROR, "updatetime null");
			return -1;
		}
				
		//length check
		if(updatetime.length() != 14){
			LoggingUtil.out(LoggingUtil.ERROR, "updatetime length error");
			return 1;
		}
		
		//character check
		if(!updatetime.matches(MATCH_HANKAKU_SUUJI)){
			LoggingUtil.out(LoggingUtil.ERROR, "updatetime character type error");
			return 2;
		}
		
		//date check
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		format.setLenient(false);
		try{
			format.parse(updatetime);
		}catch(ParseException pe){
			LoggingUtil.out(LoggingUtil.ERROR, "updatetime invalid format error");
			return 3;
		}
		
		return 0;
	}

}
