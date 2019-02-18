package com.rmat.fusen.file;


public class Note {

	//付箋ID
	private String noteid; 

	//表示順番号
	private String order;
	
	//付箋タイトル
	private String title;
	
	//付箋内容
	private String content;
	
	//付箋位置X
	private String positionx;
	
	//付箋位置Y
	private String positiony;
	
	//付箋サイズX
	private String width;
	
	//付箋サイズY
	private String height;
	
	//更新日時(YYYYMMDDHH24MISS)
	private String update_time;

	public String getNoteid() {
		return noteid;
	}

	public void setNoteid(String noteid) {
		this.noteid = noteid;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPositionx() {
		return positionx;
	}

	public void setPositionx(String positionx) {
		this.positionx = positionx;
	}

	public String getPositiony() {
		return positiony;
	}

	public void setPositiony(String positiony) {
		this.positiony = positiony;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	
	
	
}
