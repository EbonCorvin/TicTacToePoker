package com.eboncorvin.tttp.Opponents;


import com.eboncorvin.tttp.Msger;
import com.eboncorvin.tttp.R;
import com.eboncorvin.tttp.O.C;
import com.eboncorvin.tttp.Abstracts.AbsCardPlacer;
import com.eboncorvin.tttp.Abstracts.AbsCardPool;
import com.eboncorvin.tttp.Abstracts.AbsHandCard;
import com.eboncorvin.tttp.Abstracts.AbsHands;
import com.eboncorvin.tttp.Abstracts.AbstractLayer;

import java.nio.ByteBuffer;

public class OppWifi extends Opponent{
	
	private static OppWifi created;
	public static OppWifi getOpp(Msger gamemsg){
		created.mgr=gamemsg;
		return created;
	}

	/**
	 * Prepare an OppWifi, status will be returned with the help of Msger
	 * @param ip Null if the phone will act as server
	 * @param notify A Msger for AppWifi to acknowledge the status of connection
	 */
	public static void createOppWifi(String ip,Msger notify){
		if(ip!=null)
			created=new OppWifi(ip,notify);
		else
			created=new OppWifi(notify);
	}

	private boolean isServer=true;
	private Msger mvMsg;	//Msger of main view
	private Msger tcpMsg;	//Msger of TCP Socket
	private TcpSocket s;
	private AbstractLayer al;
	
	private Msger.MsgCallBack tcpCallback=new Msger.MsgCallBack(){
		@Override
		public void doMsg(int StatusCode, Object Attachment) {
			// TODO Auto-generated method stub
			switch(StatusCode){
			case C.socketConnected:	//Connected
				if(isServer){
					//al=new AbstractLayer(2);
					AbsCardPool acp=new AbsCardPool();
					sendCardPool(acp);
					al=new AbstractLayer(acp);
					mvMsg.run(C.socketConnected, true);
					mvMsg=null;
				}
				break;
			case C.socketReceived:
				processMsg((byte[])Attachment);
				break;
			case C.socketError:
				if(mvMsg!=null){
					mvMsg.run(C.socketError);
					break;
				}
				tellMV(C.socketError, (String)Attachment);
				break;
			}
		}
	};
	
	public OppWifi(String ip, Msger actNotify) {
		super(null);
		tcpMsg=new Msger(tcpCallback);	
		mvMsg=actNotify;
		isServer=false;
		s=new TcpSocket(ip, tcpMsg);
	}

	public OppWifi(Msger actNotify) {
		super(null);
		tcpMsg=new Msger(tcpCallback);	
		mvMsg=actNotify;
		s=new TcpSocket(tcpMsg);
	}

	//Process message received, not to confuse with doMsg
	private void processMsg(byte[] msg){
		ByteBuffer b=ByteBuffer.wrap(msg);
		switch(b.get()){
		/*case 10:
			s.sendMsg(new byte[]{12});
		case 12:
			tellMV(C.hideMessage);
			break;*/
		case 15:		//Cardpool received
			byte[] data=new byte[msg.length-1];
			b.get(data);
			AbsCardPool acp=new AbsCardPool(data);
			AbsHandCard opp=new AbsHandCard(acp, false);
			AbsHandCard my=new AbsHandCard(acp, true);
			al=new AbstractLayer(acp, my, opp, new AbsCardPlacer());
			mvMsg.run(C.socketConnected, false);
			mvMsg=null;
			break;
		case 30:		//Card2place received
			int[] hand=new int[msg.length-1];
			for(int i=0;i<msg.length-1;i++){
				hand[i]=b.get();
				al.getOpphc().selectCard(i);
			}
			al.getOpphc().commitCard();
			al.setCard2place(new AbsHands(hand, false));
			tellMV(C.hideMessage);
			tellMV(C.playerTurn);
			break;
		case 21:		//Card placed
			al.drawCard(2, true);
			tellMV(C.hideMessage);
			tellMV(C.showUnclosableMsg, R.string.msg_oppselectingcard);
			tellMV(C.placeCard,(int)b.get());
			break;
		}
	}
	
	private void sendCardPool(AbsCardPool acp){
		byte[] cp=acp.getByteArray();
		byte[] b=new byte[1+cp.length];
		b[0]=15;
		System.arraycopy(cp, 0, b, 1, cp.length);
		s.sendMsg(b);
		//al.prepareHC();
	}

	public void notifyCardPlacing(int position){
		s.sendMsg(new byte[]{21, (byte) position});
	}

	@Override
	public AbstractLayer getAL(){
		return al;
	}

	@Override
	public void initOpponent() {
		// TODO Auto-generated method stub
		/*tellMV(C.showUnclosableMsg,R.string.msg_oppselectingcard);
		s.sendMsg(new byte[]{10});*/
	}

	@Override
	public void placeCard(AbsHands cards) {
		// TODO Auto-generated method stub
		al.setCard2place(cards);
		int[] list=AbsHands.getCardList(cards);
		ByteBuffer b=ByteBuffer.allocate(1+list.length);
		b.put((byte) 30);
		for(int ls:list)
			b.put((byte) ls);
		s.sendMsg(b.array());
		tellMV(C.showUnclosableMsg, R.string.msg_oppplacingcard);
	}
	
	@Override
	public void selectCard() {
		// TODO Auto-generated method stub
		tellMV(C.hideMessage);
		tellMV(C.showUnclosableMsg, R.string.msg_oppselectingcard);
	}

	@Override
	public void exitCleanup() {
		// TODO Auto-generated method stub
		s.stop();
	}

	@Override
	public void playerTurn() {
		// TODO Auto-generated method stub
	
	}

	@Override
	public void oppTurn() {
		// TODO Auto-generated method stub
	
	}

	@Override
	public void drawCard() {
		// TODO Auto-generated method stub
	
	}

}
