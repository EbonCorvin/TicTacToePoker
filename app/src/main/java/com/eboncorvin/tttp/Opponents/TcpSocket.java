package com.eboncorvin.tttp.Opponents;

import com.eboncorvin.tttp.Misc;
import com.eboncorvin.tttp.Msger;
import com.eboncorvin.tttp.O.C;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpSocket implements Runnable{
	private Socket s;
	private Msger msg;
	private String sip="";
	private OutputStream os;
	private boolean isClient=true;
	private boolean isRunning=true;
	private sendThread st=new sendThread();
	
	private Runnable helloPing=new Runnable(){
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Misc.output("Hello loop started");
			while(isRunning()){
				sendMsg(new byte[]{65});
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
				}
				Misc.output("Pinging");			
			}
			Misc.output("Hello loop ended");
		}
	};
	
	/**
	 * Create a NetSocket in client mode<br/>
	 * Once the socket is created, it is also connected
	 */
	public TcpSocket(String ipaddress, Msger msg){
		this.msg=msg;
		sip=ipaddress;
		new Thread(this).start();
	}
	
	/**
	 * Create a NetSocket in server mode<br/>
	 * It will enter listening mode once the socket is created
	 */
	public TcpSocket(Msger msg){
		this.msg=msg;
		new Thread(this).start();
	}

	public boolean isRunning() {
		return isRunning;
	}

	public boolean isClient() {
		return isClient;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			if(sip.equals("")){
				isClient=false;
				ServerSocket ss=new ServerSocket(9876);
				ss.setSoTimeout(10000);
				s=ss.accept();
			} else {
				s=new Socket(Inet4Address.getByName(sip),9876);
			}
			s.setSoTimeout(10000);
			InputStream is=s.getInputStream();
			os=s.getOutputStream();
			new Thread(helloPing).start();
			msg.run(C.socketConnected);
			while(isRunning){
				byte[] buff=new byte[512];
				int num=is.read(buff);
				if(num<0)
					throw new IOException("Connection Closed");
				byte[] nbuff=new byte[num];
				System.arraycopy(buff, 0, nbuff, 0, num);
				msg.run(C.socketReceived,nbuff);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			stop();
			msg.run(C.socketError, e.toString());
		}
	}
	
	public void stop(){
		isRunning=false;
		try {
			st.stop();
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.run(C.socketError,e.toString());
		}
	}
	
	public void sendMsg(byte[] msg){
		st.send(msg);	
	}
	
	private class sendThread implements Runnable{
		private byte[] data2send;
		private boolean stop=false;
		private Thread t;
		
		public sendThread(){
			t=new Thread(this);
			t.start();
		}
		
		public void send(byte[] data){
			if(data==null)
				return;
			data2send=data;
			t.interrupt();
		}
		
		public void stop(){
			stop=true;
			t.interrupt();
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(true){
				try {
					try {
						Thread.sleep(Long.MAX_VALUE);
					}catch (InterruptedException e){
						if(stop)
							return;
						if(data2send==null)
							continue;
					}
					os.write(data2send);
					data2send=null;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					msg.run(C.socketError);
				}
			}
		}
	}
}
