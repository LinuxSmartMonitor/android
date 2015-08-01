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

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageView;

public class TransferActivity extends Activity {
    public static final String SERVERIP = "192.168.49.172";
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
/** Called when the activity is first created. */
    
    // MOUSE ######################## start 1
    private GestureDetector mDoubleTapGesture;
	int x;
	int y;
	int mouse_value;
	// MOUSE ######################## end 1 
   
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the message that the user entered. 
        Intent intent = getIntent();
        message = "Hello, Echo?";
       
        // Set the xml file to be activity layout
        //setContentView(R.layout.displaymessage);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(new Pi_View(getApplicationContext()));
        //imgv = (ImageView)findViewById(R.id.imageView1);
        //imgv2 = (ImageView)findViewById(R.id.imageView2);
        
        metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		display_width=metrics.widthPixels;
		display_height=metrics.heightPixels;
        
//        mHandler = new MyHandler();
        Converting = new jniconvert();

        // and start thread to do networking
        outputThread.start();
        openInputSocket.start();
        //inputThread.start();
        
        
        // MOUSE ######################## start 2
        Context context = this.getApplicationContext();
		mDoubleTapGesture = new GestureDetector(context, mNullListener); // 더블탭 제스쳐 생성
		mDoubleTapGesture.setOnDoubleTapListener(mDoubleTapListener); // 더블 탭 리스너 등록
		// MOUSE ######################## start 2
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
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
});

Thread inputThread = new Thread(new Runnable() {
	
	@Override
	public void run() {
		// send message to Pi
		while(true)
		{
            byte[] sendData = new byte[3072];
        
		//데이터를 보내려면 여기를 수정하세요. 1,15,3 에 int 숫자를 넣으세용       
            sendData = Converting.jniConvert(321, 15, 3);
            
//            DataOutputStream dos = new DataOutputStream(sock.getOutputStream());
//            dos.
          
            //Log.d("CONVERTED", Integer.sendData + " " + (int)sendData>>1024 + " " + (int)sendData >> 2048);
            
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


	//MOUSE ######################## start3
	//더블탭 리스너
	private OnDoubleTapListener mDoubleTapListener = new OnDoubleTapListener() {
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			Log.i("One Click Event", "onSingleTapConfirmed");
			x = (int) e.getY();
			y = (int) e.getY();
			mouse_value = 1;	// one click value
			
			//Send to Raspberry (x, y, value)
			
			
			byte[] sendData = new byte[3072];
	        
			//데이터를 보내려면 여기를 수정하세요. 1,15,3 에 int 숫자를 넣으세용       
	            sendData = Converting.jniConvert(x, y, mouse_value);
	     
	            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr, SERVERPORT_IN);
	            try {
					clientSocket.send(sendPacket);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}   
			
			
			
			
			
			
			
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
//			Log.i("OnDoubleTapListener", "onDoubleTapEvent    2");
			return false;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Log.i("Double Click Event", "onDoubleTap");
			x = (int) e.getY();
			y = (int) e.getY();
			mouse_value = 2;	// double click value
			
			// Send to Raspberry (x, y, value)
			
			byte[] sendData = new byte[3072];
	        
			//데이터를 보내려면 여기를 수정하세요. 1,15,3 에 int 숫자를 넣으세용       
				sendData = Converting.jniConvert(x, y, mouse_value);
		     
	            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr, SERVERPORT_IN);
	            try {
					clientSocket.send(sendPacket);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}   
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			return true;
		}
	};
	
	// 아무것도 안하는 제스쳐 리스너
		private OnGestureListener mNullListener = new OnGestureListener() {
			@Override
			public boolean onSingleTapUp(MotionEvent e) {
//				Log.i("OnGestureListener", "onSingleTapUp  11");
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {
//				Log.i("OnGestureListener", "onShowPress  22 ");
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2,
					float distanceX, float distanceY) {
//				Log.i("OnGestureListener", "onScroll  33");
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
//				Log.i("OnGestureListener", "onLongPress  44");
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
					float velocityY) {
//				Log.i("OnGestureListener", "onFling  55");
				return false;
			}

			@Override
			public boolean onDown(MotionEvent e) {
//				Log.i("OnGestureListener", "onDown   66");
				return false;
			}
		};

		// MOUSE ######################## end3

}
