package com.eboncorvin.tttp;

import com.eboncorvin.tttp.R;
import com.eboncorvin.tttp.O.C;
import com.eboncorvin.tttp.Opponents.OppWifi;
import com.eboncorvin.tttp.Opponents.WifiOppFinder;

import java.util.ArrayList;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;

public class ActWifi extends Activity
	implements Msger.MsgCallBack, OnClickListener, OnItemClickListener{
	
	private EditText txt_ip;
	private Msger msg=new Msger(this);
	private ListView iplist;
	private ArrayList<String> ipl=new ArrayList<String>();
	private ArrayAdapter<String> aas;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setResult(RESULT_CANCELED);
        setContentView(R.layout.oppfinder);
        iplist=(ListView)this.findViewById(R.id.lv_opp);
        aas=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, ipl);
        //((Button)this.findViewById(R.id.btn)).setOnClickListener(this);
        iplist.setOnItemClickListener(this);
        iplist.setAdapter(aas);
        WifiManager x=(WifiManager) getSystemService(WIFI_SERVICE);
        int ip=x.getDhcpInfo().ipAddress;
        String ipString = String.format(
	   "%d.%d.%d.%d",
	   (ip & 0xff),
	   (ip >> 8 & 0xff),
	   (ip >> 16 & 0xff),
	   (ip >> 24 & 0xff));
        ((TextView)findViewById(R.id.txt_ip)).setText("Your IP: "+ipString);
        txt_ip=(EditText)findViewById(R.id.ipinput);
        ((Button)findViewById(R.id.btn_connect)).setOnClickListener(this);
        
        WifiOppFinder.start(msg);
    }
    
    public void onDestroy(){
    	super.onDestroy();
    	WifiOppFinder.close();
    }

	@Override
	public void doMsg(int StatusCode, Object Attachment) {
		// TODO Auto-generated method stub
		String ips="";
		if(StatusCode!=20){
			ips=(String) Attachment;
			if(ips!=null)
				ips=ips.substring(1);
		}
		switch(StatusCode){
		case 0:		//Someone say hello
		case 1:
			if(!ipl.contains(ips))
				aas.add(ips);
			break;
		case 2:		//Someone say goodbyt
			//Misc.output(ips);
			if(ipl.contains(ips))
				aas.remove(ips);
			break;
		case 10:		//Someone want to connect
			WifiOppFinder.serverReady(ips);
			OppWifi.createOppWifi(null,msg);
			break;
		case 12:		//Someone is ready to be connected
			//WifiOppFinder.close();
			OppWifi.createOppWifi(ips,msg);
			break;
		case C.socketConnected:
			WifiOppFinder.close();
			setResult(RESULT_OK,new Intent().putExtra
					("game.poker.tttp.isServer", (Boolean)Attachment));
			this.finish();
			break;
		case C.socketError:
			if(this.isFinishing())
				return;
			new AlertDialog.Builder(this).setMessage("Cannot establish the connection!").show();
			WifiOppFinder.close();
			WifiOppFinder.start(msg);
			break;
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0.getId()==R.id.btn_connect){
			WifiOppFinder.makeTCPConnect(txt_ip.getText().toString());
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		 WifiOppFinder.makeTCPConnect(ipl.get(arg2));
	}
}
