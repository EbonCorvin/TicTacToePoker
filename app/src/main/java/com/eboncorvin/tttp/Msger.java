package com.eboncorvin.tttp;

import android.os.Handler;
import android.os.Message;

public class Msger extends Handler{

	private MsgCallBack sci;

	public Msger(MsgCallBack thehandle){
		sci=thehandle;
	}
	
	public void handleMessage(Message msg){
		if(sci!=null)
			sci.doMsg(msg.what, msg.obj);
	}
	
	/**
	 * Perform the message by add the message into the message loop<br/>
	 * This method allow message go across threads
	 */
	public void run(int message, Object Attachment){
		Message msg=Message.obtain();
		msg.obj=Attachment;
		msg.what=message;
		this.sendMessage(msg);
	}
	/**
	 * Do the same thing as run(int, Object) does<br/>
	 * But no object will be included into the message.
	 */
	public void run(int StatusCode){
		if(sci!=null)
			run(StatusCode,null);
	}

	/**
	 * Perform the message immediately <I>WITHOUT</I> go through the message loop <br/>
	 * It means that the message can't go across threads
	 */
	public void runNow(int StatusCode, Object Attachment){
		if(sci!=null)
			sci.doMsg(StatusCode, Attachment);
	}
	
	public interface MsgCallBack{
		public void doMsg(int status, Object attachment);
	}
}
