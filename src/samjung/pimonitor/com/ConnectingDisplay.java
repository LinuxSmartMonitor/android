package samjung.pimonitor.com;

import com.example.android.wifidirect.DeviceListFragment;
import com.example.android.wifidirect.WiFiDirectActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;

public class ConnectingDisplay extends Activity {

	Handler msHandler;
	Runnable msRunnable;
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connectingdisp);
		
		msRunnable = new Runnable() {
	        @Override
	        public void run() {
	        	
		        Intent intent=new Intent(ConnectingDisplay.this,TransferActivity.class);
				startActivity(intent);
				finish();
	        }
	    }; 
	    msHandler = new Handler();
	    msHandler.postDelayed(msRunnable, 500);

	   
	}
	
}
