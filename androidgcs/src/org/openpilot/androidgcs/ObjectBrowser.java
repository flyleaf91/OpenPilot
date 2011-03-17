package org.openpilot.androidgcs;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.UUID;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.openpilot.androidgcs.*;
import org.openpilot.uavtalk.Telemetry;
import org.openpilot.uavtalk.TelemetryMonitor;
import org.openpilot.uavtalk.UAVObject;
import org.openpilot.uavtalk.UAVObjectManager;
import org.openpilot.uavtalk.UAVTalk;
import org.openpilot.uavtalk.uavobjects.UAVObjectsInitialize;

public class ObjectBrowser extends Activity {
	
	private final String TAG = "ObjectBrower";
	private final String DEVICE_NAME = "RN42-222D";
	private final int REQUEST_ENABLE_BT = 0;
	private UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	BluetoothAdapter mBluetoothAdapter;
	BluetoothSocket socket;
	boolean connected;
	UAVObjectManager objMngr;
	UAVTalk uavTalk;
	
	final Handler uavobjHandler = new Handler(); 
	final Runnable updateText = new Runnable() {
		public void run() {
			ToggleButton button = (ToggleButton) findViewById(R.id.toggleButton1);
			button.setChecked(!connected);

			Log.d(TAG,"HERE" + connected);
			
			TextView text = (TextView) findViewById(R.id.textView1);
			
			UAVObject obj1 = objMngr.getObject("SystemStats");
			UAVObject obj2 = objMngr.getObject("AttitudeRaw");
			UAVObject obj3 = objMngr.getObject("AttitudeActual");
			UAVObject obj4 = objMngr.getObject("SystemAlarms");

			if(obj1 == null || obj2 == null || obj3 == null || obj4 == null)
				return;

			Log.d(TAG,"And here");
			text.setText(obj1.toString() + "\n" + obj2.toString() + "\n" + obj3.toString() + "\n" + obj4.toString() );
			
		}
	};
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Log.d(TAG, "Launching Object Browser");

        connected = false;
        
        objMngr = new UAVObjectManager();
		UAVObjectsInitialize.register(objMngr);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        	Log.e(TAG, "Device does not support Bluetooth");
        	return;
        }
        
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
        	queryDevices();
        }
        
		
		UAVObject obj = objMngr.getObject("SystemStats");
		if(obj != null)
			obj.addUpdatedObserver(new Observer() {
				public void update(Observable observable, Object data) {
					uavobjHandler.post(updateText);
				}				
			});
		obj = objMngr.getObject("AttitudeRaw");
		if(obj != null)
			obj.addUpdatedObserver(new Observer() {
				public void update(Observable observable, Object data) {
					uavobjHandler.post(updateText);
				}				
			});
		obj = objMngr.getObject("AttitudeActual");
		if(obj != null)
			obj.addUpdatedObserver(new Observer() {
				public void update(Observable observable, Object data) {
					uavobjHandler.post(updateText);
				}				
			});
		obj = objMngr.getObject("SystemAlarms");
		if(obj != null)
			obj.addUpdatedObserver(new Observer() {
				public void update(Observable observable, Object data) {
					uavobjHandler.post(updateText);
				}				
			});
		

    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode == REQUEST_ENABLE_BT && resultCode == RESULT_OK) {
    		//Log.d(TAG, "Bluetooth started succesfully");
    		queryDevices();
    	}
    	if(requestCode == REQUEST_ENABLE_BT && resultCode != RESULT_OK) {
    		//Log.d(TAG, "Bluetooth could not be started");
    	}
    	
    }
    
    public void queryDevices() {
    	Log.d(TAG, "Searching for devices");
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
		    // Loop through paired devices
		    for (BluetoothDevice device : pairedDevices) {
		        // Add the name and address to an array adapter to show in a ListView
		        //mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
		    	Log.d(TAG, "Paired device: " + device.getName());
		    	if(device.getName().compareTo(DEVICE_NAME) == 0) {
		    		openTelmetryBluetooth(device);
		    		openTelmetryBluetooth(device);
		    		}
		    }
		}
    	
    }

	private void openTelmetryBluetooth(BluetoothDevice device) {
		Log.d(TAG, "Opening conncetion to " + device.getName());
		socket = null;
		connected = false;
		try {
			socket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
		} catch (IOException e) {
			Log.e(TAG,"Unable to create Rfcomm socket");
			//e.printStackTrace();			
		}
		
		mBluetoothAdapter.cancelDiscovery();
		
		try {
			socket.connect();
		}
		catch (IOException e) {
			Log.e(TAG,"Unable to connect to requested device", e);
            try {
                socket.close();
            } catch (IOException e2) {
                //Log.e(TAG, "unable to close() socket during connection failure", e2);
            }
			return;
		}

		connected = true;
		
		try {
			uavTalk = new UAVTalk(socket.getInputStream(), socket.getOutputStream(), objMngr);
		} catch (IOException e) {
			Log.e(TAG,"Error starting UAVTalk");
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return;
		}
		
		Thread inputStream = uavTalk.getInputProcessThread();
		inputStream.start();
		
		Telemetry tel = new Telemetry(uavTalk, objMngr);
		TelemetryMonitor mon = new TelemetryMonitor(objMngr,tel);

	}
}