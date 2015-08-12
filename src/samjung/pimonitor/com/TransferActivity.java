/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package samjung.pimonitor.com;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

public class TransferActivity extends Activity implements OnClickListener {
	
	public boolean clickflag =false;
	public float minX=2000, minY=2000, maxX=0, maxY=0;
	int mouse_x = 0, mouse_y = 0;

    public static final String SERVERIP = "192.168.49.164";
    public static final int SERVERPORT_OUT = 3490;
    public static final int SERVERPORT_IN = 3491;
    
    public String message;
    int height = 384;
    int width = 512;
    int n=0;
    public static DatagramSocket clientSocket;
    public static InetAddress serverAddr;
    public static Bitmap bitmap;
    //ImageView imgv;
    //ImageView imgv2;
    Handler mHandler;
    byte[] bufferOut;
    jniconvert Converting;
    int swit = 0;
    public static DisplayMetrics metrics;
    public static int display_width,display_height ;
    
    // MOUSE ######################## start 1
    private GestureDetector mDoubleTapGesture;
	int x;
	int y;
	int mouse_value;
	// MOUSE ######################## end 1 
    

	/* SUJIN */
	private View mView;
	private FrameLayout mSurfaceView;
	private Pi_View mPiView;
	private MyKeyboard mMyKeyboard;
	private boolean isKeyboard = false;
	
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the message that the user entered. 
        Intent intent = getIntent();
        message = "Hello, Echo?";
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        Converting = new jniconvert();
        // Set the xml file to be activity layout
        //setContentView(R.layout.displaymessage);
        
        
        /* View Setting */
		setContentView(R.layout.activity_main);
		mView = (View)findViewById(R.id.touchview);
		mView.setOnTouchListener(mViewTouchListener);	
		
		/* Keyboard Setting */
		mMyKeyboard = (MyKeyboard)findViewById(R.id.my_keyboard);
		mMyKeyboard.setActionListenerEngKeyboard(this);
		goneCustomKeyboard();
			
		/* SurfaceView Setting */
		mSurfaceView = (FrameLayout)findViewById(R.id.surface);
		mPiView = new Pi_View(getApplicationContext());
		mSurfaceView.addView(mPiView);
			

        //imgv = (ImageView)findViewById(R.id.imageView1);
        //imgv2 = (ImageView)findViewById(R.id.imageView2);
		
        metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		display_width=metrics.widthPixels;
		display_height=metrics.heightPixels;
		Log.i("Display " ,"width : " + display_width + " height : " +  display_height);

        outputThread.start();
        openInputSocket.start();
        mouseOutputThread.start();
	}
	
	
	/* Show Keyboard */
	private void showCustomKeyboard(){
		mMyKeyboard.setVisibility(View.VISIBLE);
	}

	/* Hide Keyboard */
	private void goneCustomKeyboard() {
		mMyKeyboard.setVisibility(View.GONE);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode){
		
		// MENU BTN
		case KeyEvent.KEYCODE_MENU:
			Log.d("MainAcitivy", "Menu btn");
			showCustomKeyboard();
			if(!isKeyboard){
				showCustomKeyboard();
				isKeyboard=true;
			}
			else {
				goneCustomKeyboard();
				isKeyboard=false;
			}
			break;
			
		// 투명성 적용
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			break;
		case KeyEvent.KEYCODE_VOLUME_UP:
			break;
		default:
			Log.d("MainAcitivy", "other btn");
		}
		return super.onKeyDown(keyCode, event);
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}	
	

Thread openInputSocket = new Thread(new Runnable() {
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		clientSocket = null;
		serverAddr = null;
		try {
			serverAddr = InetAddress.getByName(SERVERIP);
			clientSocket = new DatagramSocket();
			
			mMyKeyboard.setSocket(serverAddr, clientSocket, Converting);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
});


// 이거 쓰레드 누가 씀? 안쓰면 지우기
Thread inputThread = new Thread(new Runnable() {
	
	@Override
	public void run() {
		// send message to Pi
		while(true)
		{
            byte[] sendData = new byte[3072];
		//데이터를 보내려면 여기를 수정하세요. 1,15,3 에 int 숫자를 넣으세용       
            sendData = Converting.jniConvert(321, 15, 3);
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr, SERVERPORT_IN);
            try {
				clientSocket.send(sendPacket);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}   
		}
	}
});


Thread outputThread = new Thread(new Runnable() {
	
	@Override
	public void run() {
		
		DatagramSocket clientSocket = null;
		 InetAddress serverAddr = null;
		try {
			serverAddr = InetAddress.getByName(SERVERIP);
			clientSocket = new DatagramSocket();
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	        
		 // send message to Pi
		
        byte[] sendData = new byte[2];
    
        String sentence = "1";
        sendData = sentence.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr, SERVERPORT_OUT);
        try {
			clientSocket.send(sendPacket);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}   
        
       	bufferOut = new byte[49152*8];
		while(true)
		{
			//bitmap=null;
			int ou=0;
			for(int i=0; i<8; i++){
					try {
	
			            // get reply back from Pi
							
						  byte[] receiveData1 = new byte[49152];
				            
				            DatagramPacket receivePacket = new DatagramPacket(receiveData1, 49152);
				            clientSocket.receive(receivePacket);
				           
				            System.arraycopy(receiveData1, 
				                    0,
				                    bufferOut,
				                    receiveData1[0]*49152,
				                    49152);
				           /* System.arraycopy(receiveData1, 
				                    0,
				                    bufferOut,
				                    i*49152,
				                    49152);*/
			        } 
			        catch (Exception e) {
			            e.printStackTrace();
			        }
				}
				bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
				bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(( bufferOut )));
				//mHandler.sendMessage(mHandler.obtainMessage(1, 0, 0, 0));
			}
			
		}
});

/*
public class MyHandler extends Handler {
	@Override
	public void handleMessage(Message msg) {

		if(swit == 0)
		{
			imgv.setImageBitmap(bitmap);
			swit = -1;
		}
		else
		{
			imgv2.setImageBitmap(bitmap);
			swit = 0;
		}
	}
}
*/
		// Mouse Touch Listener.
		// screen Min X value = 11, Min Y value = 14.
		View.OnTouchListener mViewTouchListener = new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				mouse_x = (int) event.getX() - 11;
				mouse_y = (int) event.getY() - 14;
				clickflag = true;
				int mouse_value = 1;	// one click value
				Log.d("PiMonitor", "View_Touch //  x : " + mouse_x + " y : " + mouse_y );
				//Send to Raspberry (x, y, value)

				//m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				return false;
			}
			
		};
		
		// Thread to Send Mouse data
		Thread mouseOutputThread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				byte[] sendData = new byte[3072];

				while (true) {

					if (clickflag) {
						mouse_value = 1;
						//데이터를 보내려면 여기를 수정하세요. 1,15,3 에 int 숫자를 넣으세용       
				            sendData = Converting.jniConvert(512 * mouse_x / (display_width - 25), 384 * mouse_y / (display_height - 28), mouse_value);
				            Log.d("PiMonitor", "Change Coord //  x : " + (512 * mouse_x / (display_width - 25)) + " y : " + (384 * mouse_y / (display_height - 28)) );
				            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr, SERVERPORT_IN);
				            try {
								clientSocket.send(sendPacket);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}   
						clickflag = false;
					}
				}
			}
		});

}
