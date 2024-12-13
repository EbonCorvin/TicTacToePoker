package com.eboncorvin.tttp.Opponents;

import com.eboncorvin.tttp.Misc;
import com.eboncorvin.tttp.Msger;
import com.eboncorvin.tttp.O.C;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Random;

public class WifiOppFinder implements Runnable{
	
	private static WifiOppFinder ubc;
	private DatagramSocket d;
	private boolean stop;
	private DatagramPacket broadcastp, unicastp;
	private Msger msg;
	private int identify=0;
	private byte[] pbyte=new byte[128];
	private ByteBuffer sendBuffer=ByteBuffer.wrap(pbyte);
	private sendThread st;
	
	public static void start(Msger msg){
		ubc=new WifiOppFinder(msg);
	}

	public static void close(){
		if(ubc!=null){
			ubc.stopSocket();
			ubc=null;
		}
	}
	
	public static void makeTCPConnect(String ip){
		ubc.sendUnicastData(10, ip);
	}
	
	public static void serverReady(String ip){
		ubc.sendUnicastData(12, ip);
	}
	
	public WifiOppFinder(Msger msg){
		st=new sendThread();
		this.msg=msg;
		stop=false;
	    broadcastp=new DatagramPacket(pbyte, 128);
	    unicastp=new DatagramPacket(pbyte, 128);
	    unicastp.setPort(6789);
	    broadcastp.setPort(6789);
	    try {
			broadcastp.setAddress(InetAddress.getByName("255.255.255.255"));
			new Thread(this).start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void sendUnicastData(int Type, String IP){
		try {
			unicastp.setAddress(InetAddress.getByName(IP));
			sendBuffer.clear();
			sendBuffer.put((byte) Type);
			sendBuffer.putInt(identify);
			st.send(unicastp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			msg.run(C.socketError);
		}
	}

	private void sendData(int type, byte[] data){
		if(data!=null && (data.length+5)>128)
			throw new IllegalArgumentException("The data is too big!(larger than 123 byte)");
		if(type>255)
			throw new IllegalArgumentException("The type agrument should be a byte!");
		sendBuffer.clear();
		sendBuffer.put((byte)type);
		sendBuffer.putInt(identify);
		if(data!=null)
			sendBuffer.put(data);
		st.send(broadcastp);
	}

	private void processPacket(DatagramPacket message){
		ByteBuffer b=ByteBuffer.wrap(message.getData());
		int type=b.get();
		int id=b.getInt();
		if(id==identify)
			return;
		byte[] data=new byte[0];
		if(message.getLength()>5){
			data=new byte[message.getLength()-5];
			b.get(data);
		}
		switch(type){
		case 0:		//"Hello msg"
			sendData(1, null);	
		case 1:		//"Hello back" msg
		case 2:		//"Goodbye" msg
		case 10:	//"I am your client" msg
		case 12:	//"My port is ready" msg
			msg.run(type,message.getAddress().toString());
			break;
		}
		
	}

	private void stopSocket(){
		new Thread(new Runnable(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				sendData(2,null);
				try {
					Thread.sleep(400);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				stop=true;
				st.stop();
				if(d==null)
					return;
				d.close();
				d.disconnect();	
			}
		}).start();
	}

	//Receiving Thread
	@Override
	public void run() {
		// TODO Auto-generated method stub
		DatagramPacket rd=new DatagramPacket(new byte[128],128);
		try {
			d = new DatagramSocket(6789);
			identify=new Random().nextInt();
			d.setBroadcast(true);
			sendData(0,null);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
		}
		while(!stop){
			try {
				if(d!=null)
					d.receive(rd);
				processPacket(rd);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
		}
	}
	
	private class sendThread implements Runnable{
		private DatagramPacket data2send;
		private Thread t;
		private boolean stop=false;
		
		public sendThread(){
			t=new Thread(this);
			t.start();
		}
		
		public void stop(){
			stop=true;
			t.interrupt();
		}
		
		public void send(DatagramPacket data){
			if(data==null)
				return;
			data2send=data;
			t.interrupt();
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				while(true){
					try {
						Thread.sleep(Long.MAX_VALUE);
					} catch (InterruptedException e) {
						if(stop){
							Misc.output("Send Packet Loop is stopping");
							return;
						}
						if(data2send==null)
							continue;
					}
					if(d==null)
						return;
					d.send(data2send);
					data2send=null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
}
