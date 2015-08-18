package samjung.pimonitor.com;

import com.example.android.wifidirect.WiFiDirectActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageButton;

public class ConnectingDisplay extends Activity {

	Handler mHandler;
	Runnable mRunnable;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.connectingdisp);
		
		mRunnable = new Runnable() {
	        @Override
	        public void run() {
	        	
		        Intent intent=new Intent(ConnectingDisplay.this,TransferActivity.class);
				startActivity(intent);
				finish();
	        }
	    };
	    mHandler = new Handler();
	    mHandler.postDelayed(mRunnable, 5000);
	   
	}
	
}
