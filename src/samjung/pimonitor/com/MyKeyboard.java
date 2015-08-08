package samjung.pimonitor.com;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;

class MyKeyboard extends KeyboardView {

	/* Keyboard */
	private MyKeyboardActionListener engKey;
	private Keyboard eng = null;
	boolean keyboardCheck = false;
	public static int mKeyCode = 0;
	private String state;
	
	/* Socket */
    public static String SERVERIP;
    public static int SERVERPORT_IN;
    private InetAddress serverAddr_SUJIN;
    private DatagramSocket clientSocket_SUJIN;
  
    /* Else */
    private jniconvert Converting;
    private Context context;
    
	public MyKeyboard(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		eng = new Keyboard(context, R.xml.custom_key);
	}
	
	/* Socket Setting */
	public void setSocket(InetAddress serverAddr, DatagramSocket clientSocket, jniconvert Converting){	
		this.SERVERIP = TransferActivity.SERVERIP;
		this.SERVERPORT_IN = TransferActivity.SERVERPORT_IN;
		this.serverAddr_SUJIN = serverAddr;
		this.clientSocket_SUJIN = clientSocket;
		this.Converting = Converting;
		keyboardOutputThread.start();
	}
	
	public void setActionListenerEngKeyboard(Activity act) {
		state = "Eng";
		this.clearFocus();
		engKey = new MyKeyboardActionListener(act);
		this.setOnKeyboardActionListener(engKey);
		this.setKeyboard(eng);
	}
	
	
	/* Send keyboard event */
	Thread keyboardOutputThread = new Thread(new Runnable() {
		
		private int mKeyCode;
		byte[] sendData = new byte[3072];
		
		@Override
		public void run() {

			while(true){				
				while(keyboardCheck){
					keyboardCheck = false;			
					mKeyCode = MyKeyboard.mKeyCode;   
					sendData = Converting.jniConvert(-1, mKeyCode, -1);
					try {
						DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr_SUJIN, SERVERPORT_IN);
						clientSocket_SUJIN.send(sendPacket);
					} catch (IOException e1) {
						Log.d("MyKeyboard", "send error.");
						e1.printStackTrace();
					}
				}
			}
		}
	});
	
	
	/* 		MyKeyboardActionListener	*/
	private class MyKeyboardActionListener implements OnKeyboardActionListener {
		
		final int mapping[] = new int[500];
		final static int ARROW_UP = -7;
		final static int ARROW_DOWN = -9;
		final static int ARROW_LEFT = -8;
		final static int ARROW_RIGHT = -10;
		final static int CHANGE_KEYBOARD = -11;
		
		private boolean caps = false;
		private boolean change = false;
		Activity owner;
		
		public MyKeyboardActionListener(Activity activity) {
			owner = activity;
			init();
		}
		
		@Override
		public void onKey(int primaryCode, int[] keyCodes) {
			
			Log.d("MyKeyboard", "onKey");
			int temp = 0;

			/* if primaryCode is negative, key code is function key */
		    if( primaryCode < 0) {						
		    	temp = Math.abs(primaryCode) + 200;
		    	mKeyCode = mapping[temp];
		    	keyboardCheck = true;
		    }
		    
			/* if primaryCode is 97-122, key code is capital key */
		    else if ( caps && (primaryCode>=97) && (primaryCode<=122)) {
		    	temp = primaryCode+300;
		    	mKeyCode = mapping[temp];	
		    	keyboardCheck = true;
		    }
		    
			/* else key code */
		    else {
		    	temp = primaryCode;
		    	mKeyCode = mapping[temp]; 
		    	keyboardCheck = true;	    	
		    }
		}
		
		
		/* Android key code - Linux key code */		
		private void init(){
			
			/* 1-0 */
			mapping[49] = 2;	
			mapping[50] = 3;	
			mapping[51] = 4;	
			mapping[52] = 5;
			mapping[53] = 6;	
			mapping[54] = 7;
			mapping[55] = 8;	
			mapping[56] = 9;	
			mapping[57] = 10;
			mapping[48] = 11;
			
			/* a-z */
			mapping[97] = 30;	
			mapping[98] = 48;	
			mapping[99] = 46;	
			mapping[100] = 32;	
			mapping[101] = 18;	
			mapping[102] = 33;	
			mapping[103] = 34;	
			mapping[104] = 35;	
			mapping[105] = 23;	
			mapping[106] = 36;	
			mapping[107] = 37;	
			mapping[108] = 38;
			mapping[109] = 50;
			mapping[110] = 49;	
			mapping[111] = 24;
			mapping[112] = 25;	
			mapping[113] = 16;
			mapping[114] = 19;
			mapping[115] = 31;	
			mapping[116] = 20;
			mapping[117] = 22;	
			mapping[118] = 47;	
			mapping[119] = 17;
			mapping[120] = 45;	
			mapping[121] = 21;	
			mapping[122] = 44;
			
			/* capital A-Z */
			mapping[397] = 318;
			mapping[398] = 335;	
			mapping[399] = 333;	
			mapping[400] = 320;
			mapping[401] = 316;	
			mapping[402] = 321;
			mapping[403] = 322;
			mapping[404] = 323;
			mapping[405] = 311;
			mapping[406] = 324;
			mapping[407] = 325;
			mapping[408] = 326;
			mapping[409] = 337;
			mapping[410] = 336;
			mapping[411] = 312;	
			mapping[412] = 313;	
			mapping[413] = 314;	
			mapping[414] = 317;	
			mapping[415] = 319;	
			mapping[416] = 318;	
			mapping[417] = 310;
			mapping[418] = 334;	
			mapping[419] = 315;
			mapping[420] = 332;	
			mapping[421] = 319;	
			mapping[422] = 331;
			
			
			// 다시 수정
			mapping[27] = 15;	// TAB		// Android 다시 수정
			mapping[27] = 58;	// CAPS		// Android 다시 수정
			mapping[27] = 42;	// SHIFT	// Android 다시 수정	
			
			// Negative Function Key Setting (~200)
			mapping[204] = 28;	// ENTER
			mapping[205] = 158;	// BACK
			mapping[205] = 111;	// DELETE
			mapping[211] = 122;	// 한영
			
			mapping[207] = 103;	// ↑
			mapping[208] = 105;	// ←
			mapping[209] = 108;	// ↓
			mapping[210] = 106;	// →
			mapping[206] = 158;	// ?
			
			// 특수문자 -> Android는 마이너스값으로 바꾸기
			// 일단보류
			mapping[300] = 300;	// !
			mapping[300] = 301;	// @
			mapping[300] = 302;	// #
			mapping[300] = 303;	// $
			mapping[300] = 304;	// %
			mapping[300] = 305;	// ^
			mapping[300] = 306;	// &
			mapping[300] = 307;	// *
			mapping[300] = 308;	// (
			mapping[300] = 309;	// )

			mapping[27] = 1;	// ESC
			mapping[21] = 29;	// CTRL
			mapping[32] = 57;	// SPACE
			mapping[47] = 56;	// ALT
		}


		@Override
		public void onPress(int primaryCode) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onRelease(int primaryCode) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onText(CharSequence text) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void swipeLeft() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void swipeRight() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void swipeDown() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void swipeUp() {
			// TODO Auto-generated method stub
			
		}	
	}
}
