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
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class TransferActivity extends Activity {
    public static final String SERVERIP = "192.168.49.172";
    public static final int SERVERPORT_OUT = 3490;
    public static final int SERVERPORT_IN = 3491;
    public String message;
    int height = 384;
    int width = 512;
    int n=0;
    public static Bitmap bitmap;
    ImageView imgv;
    Handler mHandler;
    byte[] bufferOut;
    jniconvert Converting;
/** Called when the activity is first created. */
   
@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get the message that the user entered. 
        Intent intent = getIntent();
        message = "Hello, Echo?";
       
        // Set the xml file to be activity layout
        setContentView(R.layout.displaymessage);
        //setContentView(new Pi_View(getApplicationContext()));
        imgv = (ImageView)findViewById(R.id.imageView1);
        mHandler = new MyHandler();
        Converting = new jniconvert();

        // and start thread to do networking
        outputThread.start();
        inputThread.start();
        
        
        
    }

Thread inputThread = new Thread(new Runnable() {
	
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
	        /*
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
		}*/
	
			
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
       
		while(true)
		{
				       
			bufferOut = new byte[49152*8];
			bitmap=null;
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
				mHandler.sendMessage(mHandler.obtainMessage(1, 0, 0, 0));
				
			}
			
		}
});


public class MyHandler extends Handler {
	@Override
	public void handleMessage(Message msg) {

		
		imgv.setImageBitmap(bitmap);
	}
}





}
