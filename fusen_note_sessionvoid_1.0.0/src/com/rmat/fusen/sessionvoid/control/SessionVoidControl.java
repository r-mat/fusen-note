package com.rmat.fusen.sessionvoid.control;

import javax.ejb.Remote;

@Remote
public interface SessionVoidControl {

	public void createTimer();
	
	public void stopTimer();
	
}
