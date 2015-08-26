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
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.example.android.wifidirect.WiFiDirectActivity;

public class TransferActivity extends Activity implements OnClickListener {

	public static final int SERVERPORT_OUT = 3490;
	public static final int SERVERPORT_IN = 3491;
	public static ServerSocket inclientSocket;
	public static Socket inclientsock;
	public static Bitmap bitmap;
	public static DataOutputStream is;
	public static DisplayMetrics metrics;
	public static int display_width, display_height;

	boolean paused = false;
	public String message;
	int height = 384;
	int width = 512;
	int n = 0;
	byte[] bufferOut;
	jniconvert Converting;
	int swit = 0;

	/* ByeongJae */
	private GestureDetector mDoubleTapGesture;
	int x;
	int y;
	int mouse_value;
	public boolean clickflag = false;
	public float minX = 2000, minY = 2000, maxX = 0, maxY = 0;
	int mouse_x = 0, mouse_y = 0;

	/* SUJIN */
	private View mView;
	private FrameLayout mSurfaceView;
	private Pi_View mPiView;
	private MyKeyboard mMyKeyboard;
	private boolean isKeyboard = false;
	public static float currentAlpha = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the message that the user entered.
		Intent intent = getIntent();
		message = "Hello, Echo?";
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		Converting = new jniconvert();

		/* View Setting */
		setContentView(R.layout.activity_main);
		mView = (View) findViewById(R.id.touchview);
		mView.setOnTouchListener(mViewTouchListener);

		/* Keyboard Setting */
		mMyKeyboard = (MyKeyboard) findViewById(R.id.my_keyboard);
		mMyKeyboard.setActionListenerEngKeyboard(this);
		goneCustomKeyboard();

		/* SurfaceView Setting */
		mSurfaceView = (FrameLayout) findViewById(R.id.surface);
		mPiView = new Pi_View(getApplicationContext());
		mSurfaceView.addView(mPiView);

		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		display_width = metrics.widthPixels;
		display_height = metrics.heightPixels;
		Log.i("Display ", "width : " + display_width + " height : "
				+ display_height);

		outputThread.start();
		openInputSocket.start();
		mouseOutputThread.start();
	}

	/* Show Keyboard */
	private void showCustomKeyboard() {
		mMyKeyboard.setVisibility(View.VISIBLE);
	}

	/* Hide Keyboard */
	private void goneCustomKeyboard() {
		mMyKeyboard.setVisibility(View.GONE);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		switch (keyCode) {

		// MENU BTN
		case KeyEvent.KEYCODE_MENU:
			Log.d("MainAcitivy", "Menu btn");
			showCustomKeyboard();
			if (!isKeyboard) {
				showCustomKeyboard();
				isKeyboard = true;
			} else {
				goneCustomKeyboard();
				isKeyboard = false;
			}
			break;

		case KeyEvent.KEYCODE_VOLUME_DOWN:
			// 1 -> 0.8 -> 0.6 -> 0.4 -> 0.2
			if (currentAlpha <= 0.4) {
				currentAlpha = 1;
			} else {
				currentAlpha -= (float) 0.2;
			}
			Log.d("PiMonitor", "Key down " + currentAlpha);
			mMyKeyboard.setAlpha(currentAlpha);
			break;

		case KeyEvent.KEYCODE_VOLUME_UP:
			Log.d("MainAcitivy", "Menu btn");
			showCustomKeyboard();
			if (!isKeyboard) {
				showCustomKeyboard();
				isKeyboard = true;
			} else {
				goneCustomKeyboard();
				isKeyboard = false;
			}
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
			Log.d("TCP", "S: Connecting...");

			inclientSocket = null;
			try {
				inclientSocket = new ServerSocket(SERVERPORT_IN);
				inclientsock = inclientSocket.accept();
				Log.d(WiFiDirectActivity.TAG, "Server: connection done");
				OutputStream tmp = inclientsock.getOutputStream();
				is = new DataOutputStream(tmp);
				mMyKeyboard.setSocket(is, Converting);
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	});

	Thread outputThread = new Thread(new Runnable() {

		@Override
		public void run() {

			/* Retrieve the ServerName */
			InetAddress outserverAddr;
			DatagramSocket outsocket = null;

			try {
				outserverAddr = InetAddress.getByName("192.168.49.1");
				outsocket = new DatagramSocket(SERVERPORT_OUT, outserverAddr);
				Log.d("UDP", "S: Connecting...");
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			/* Create new UDP-Socket */

			bufferOut = new byte[49152 * 8];
			while (true) {

				// bitmap=null;
				int ou = 0;
				for (int i = 0; i < 8; i++) {
					try {

						byte[] buf = new byte[49152];
						DatagramPacket packet = new DatagramPacket(buf,
								buf.length);

						outsocket.receive(packet);

						System.arraycopy(buf, 0, bufferOut, buf[0] * 49152,
								49152);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				bitmap = Bitmap.createBitmap(width, height,
						Bitmap.Config.RGB_565);
				bitmap.copyPixelsFromBuffer(ByteBuffer.wrap((bufferOut)));

			}

		}
	});

	// Mouse Touch Listener.
	// screen Min X value = 11, Min Y value = 14.
	View.OnTouchListener mViewTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			mouse_x = (int) event.getX() - 11;
			mouse_y = (int) event.getY() - 14;
			clickflag = true;
			int mouse_value = 1; // one click value
			Log.d("PiMonitor", "View_Touch //  x : " + mouse_x + " y : "
					+ mouse_y);
			// Send to Raspberry (x, y, value)

			// m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
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
					// 데이터를 보내려면 여기를 수정하세요. 1,15,3 에 int 숫자를 넣으세용
					sendData = Converting.jniConvert(512 * mouse_x
							/ (display_width - 25), 384 * mouse_y
							/ (display_height - 28), mouse_value);
					// DatagramPacket sendPacket = new DatagramPacket(sendData,
					// sendData.length, inserverAddr, SERVERPORT_IN);

					try {
						is.write(sendData);
						// inclientSocket.send(sendPacket);
						Log.d("PiMonitor", "Change Coord //  x : "
								+ (512 * mouse_x / (display_width - 25))
								+ " y : "
								+ (384 * mouse_y / (display_height - 28)));

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