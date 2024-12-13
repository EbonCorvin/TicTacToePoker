package com.eboncorvin.tttp;

import com.eboncorvin.tttp.R;

import java.util.Set;
import android.app.*;
import android.bluetooth.*;
import android.content.*;
import android.os.Bundle;
import android.widget.*;
import android.view.*;

public class ActBluetooth extends Activity 
		implements DialogInterface.OnClickListener, View.OnClickListener{
	private BluetoothAdapter bta=BluetoothAdapter.getDefaultAdapter();
	private ArrayAdapter<String> listitem;
	private ArrayAdapter<String> discovered;
	private boolean registered=false;
	
	private IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
	    public void onReceive(Context context, Intent intent) {
	        String action = intent.getAction();
	        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
	            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
	            discovered.add(device.getName() + "\n" + device.getAddress());
	        }
	    }
	};

	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bluetooth);
		listitem=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		discovered=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
		ListView lv=(ListView)findViewById(R.id.pairedlist);
		lv.setAdapter(listitem);
		initBT();
		setResult(RESULT_CANCELED, getIntent().putExtra("info", "User canceled"));
		((Button)findViewById(R.id.btn_scan)).setOnClickListener(this);
	}
	
	private void initBT(){
		if(bta==null){
			setResult(RESULT_CANCELED, getIntent().putExtra("info", "No BT Device"));
			listitem.add(getString(R.string.inf_nobtdevice));
			return;
		}
		if(!bta.isEnabled()){
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, 50);
			return;
		}
		displayPairedList();
	}
	
	private void displayPairedList(){
		Set<BluetoothDevice> paired=bta.getBondedDevices();
		if(paired.size()!=0){
			for(BluetoothDevice b:paired){
				listitem.add(b.getName());
			}
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		bta.cancelDiscovery();
		dialog.dismiss();
	}
	
	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		if(requestCode==50){
			if(resultCode==RESULT_CANCELED)
				listitem.add("You must enable Bluetooth first!");
			else
				displayPairedList();
		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if(arg0.getId()==R.id.btn_scan){
			registered=true;
			bta.startDiscovery();
			registerReceiver(mReceiver, filter);
			ListView v=new ListView(this);
			v.setAdapter(discovered);
			new AlertDialog.Builder(this).setView(v)
			.setNegativeButton(android.R.string.cancel,this)
			.setTitle("Discoverying Bluetooth Device...")
			.show();
		}
	}
	
	@Override
	public void onDestroy(){
		if(registered)
			unregisterReceiver(mReceiver);
		Misc.output("Destroyed");
		super.onDestroy();
	}
}
