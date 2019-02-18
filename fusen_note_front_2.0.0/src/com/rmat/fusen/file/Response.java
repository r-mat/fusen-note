package com.rmat.fusen.file;

public class Response {

	//response code
	private String code;
	
	//message
	private String message;
	
	//response sub code
	private String subcode;
	
	//sub message
	private String submessage;
	
	//content
	private String content;
	
	public Response(){
		this.code = null;
		this.message = null;
		this.subcode = null;
		this.submessage = null;
		this.content = null;
	}
	
	public Response(String code, String message, String subcode, String submessage,String content){
		this.code = code;
		this.message = message;
		this.subcode = subcode;
		this.submessage = submessage;
		this.content = content;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSubcode() {
		return subcode;
	}

	public void setSubcode(String subcode) {
		this.subcode = subcode;
	}

	public String getSubmessage() {
		return submessage;
	}

	public void setSubmessage(String submessage) {
		this.submessage = submessage;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
	
	
}
