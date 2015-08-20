package samjung.pimonitor.com;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;

import android.app.Activity;
import android.content.Context;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;

class MyKeyboard extends KeyboardView {

	/* Keyboard */
	private MyKeyboardActionListener engKey, engCapKey, hanKey, hanCapKey, funKey;
	private Keyboard eng = null, engCap = null, han = null, hanCap = null, fun = null;
	boolean keyboardCheck = false;
	public static int mKeyCode = 0;
	private String state;
	private String before;
	
	/* Socket */
    public static String SERVERIP;
    public static int SERVERPORT_IN;
    private DataOutputStream is_SUJIN;
    //private DatagramSocket clientSocket_SUJIN;
  
    /* Else */
    private jniconvert Converting;
    private Context context;
    
	public MyKeyboard(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		eng = new Keyboard(context, R.xml.custom_key);
		engCap = new Keyboard(context, R.xml.custom_cap_key);
		han = new Keyboard(context, R.xml.custom_han_key);
		hanCap = new Keyboard(context, R.xml.custom_han_cap_key);
		fun = new Keyboard(context, R.xml.function_key);
	}
	
	/* Socket Setting */
	public void setSocket(DataOutputStream keyin,jniconvert Converting){	
		this.SERVERPORT_IN = TransferActivity.SERVERPORT_IN;
		this.is_SUJIN = keyin;
		//this.clientSocket_SUJIN = clientSocket;
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
	
	public void setActionListenerEngCapKeyboard(Activity act) {
		state = "EngCap";
		this.clearFocus();
		engCapKey = new MyKeyboardActionListener(act);
		this.setOnKeyboardActionListener(engCapKey);
		this.setKeyboard(engCap);
	}
	
	public void setActionListenerHanKeyboard(Activity act) {
		state = "Han";
		this.clearFocus();
		hanKey = new MyKeyboardActionListener(act);
		this.setOnKeyboardActionListener(hanKey);
		this.setKeyboard(han);
	}
	
	public void setActionListenerHanCapKeyboard(Activity act) {
		state = "HanCap";
		this.clearFocus();
		hanCapKey = new MyKeyboardActionListener(act);
		this.setOnKeyboardActionListener(hanCapKey);
		this.setKeyboard(hanCap);
	}
	
	public void setActionListenerFunKeyboard(Activity act) {
		state = "Fun";
		this.clearFocus();
		funKey = new MyKeyboardActionListener(act);
		this.setOnKeyboardActionListener(funKey);
		this.setKeyboard(fun);
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
						//DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr_SUJIN, SERVERPORT_IN);
						//clientSocket_SUJIN.send(sendPacket);
						is_SUJIN.write(sendData);
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
		
		final static int FUNCTION_KEY = -15;
		
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

			// Function Key
			if(primaryCode==FUNCTION_KEY){
				//setActionListenerFunKeyboard(owner);
				//before = state;
				return;
			}
			
			// Key
				/* if primaryCode is negative, key code is function key */
			    if( primaryCode < 0) {						
			    	temp = Math.abs(primaryCode) + 200;
			    	mKeyCode = mapping[temp];
			    	
			    	// Shift Key
			    	if(mKeyCode==58){
			    		if(state.compareTo("Eng")==0)
			    			setActionListenerEngCapKeyboard(owner);
			    		else if(state.compareTo("EngCap")==0)
			    			setActionListenerEngKeyboard(owner);
			    		else if(state.compareTo("Han")==0)
			    			setActionListenerHanCapKeyboard(owner);
			    		else if(state.compareTo("HanCap")==0)
			    			setActionListenerHanKeyboard(owner);
			    	}
			    	
			    	// ENG/KOR Key
			    	if(mKeyCode==122){
			    		if(state.compareTo("Eng")==0 || state.compareTo("EngCap")==0)
			    			setActionListenerHanKeyboard(owner);
			    		else if(state.compareTo("Han")==0 || state.compareTo("HanCap")==0)
			    			setActionListenerEngKeyboard(owner);
			    	}
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
			    
			    Log.d("MyKeyboard", "onKey: " + mKeyCode);
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
			
			// Negative Function Key Setting
			mapping[203] = 15;	// TAB		-3
			mapping[205] = 58;	// CAPS		-5
			mapping[206] = 42;	// SHIFT	-6
			mapping[201] = 1;	// ESC		-1
			mapping[211] = 29;	// CTRL		-11
			mapping[213] = 57;	// SPACE	-13
			mapping[212] = 56;	// ALT		-12
			mapping[204] = 28;	// ENTER	-4
			mapping[202] = 158;	// BACK		-2
			mapping[214] = 122;	// ÇÑ¿µ		-14
			
			mapping[207] = 103;	// UP		-7
			mapping[209] = 108;	// DOWN		-9
			mapping[208] = 105;	// LEFT		-8
			mapping[210] = 106;	// RIGHT	-10
			// until -19
			
			mapping[220] = 59;	// F1	-20
			mapping[221] = 60;	// F2	-21
			mapping[222] = 61;	// F3	-22
			mapping[223] = 62;	// F4	-23
			mapping[224] = 63;	// F5	-24
			mapping[225] = 64;	// F6	-25
			mapping[226] = 65;	// F7	-26
			mapping[227] = 66;	// F8	-27
			mapping[228] = 67;	// F9	-28
			mapping[229] = 68;	// F10	-29
			mapping[230] = 87;	// F11	-30
			mapping[231] = 88;	// F12	-31
			
			// function
			mapping[300] = 300;	// !
			mapping[301] = 301;	// @
			mapping[302] = 302;	// #
			mapping[303] = 303;	// $
			mapping[304] = 304;	// %
			mapping[305] = 305;	// ^
			mapping[306] = 306;	// &
			mapping[307] = 307;	// *
			mapping[308] = 308;	// (
			mapping[309] = 309;	// )
			mapping[310] = 310;	// -
			mapping[311] = 311;	// +
			
			mapping[312] = 312;	// '
			mapping[313] = 313;	// ~
			mapping[314] = 314;	// _
			mapping[315] = 315;	// =
			mapping[316] = 316;	// \
			mapping[317] = 317;	// |
			mapping[318] = 318;	// {
			mapping[319] = 319;	// }
			mapping[320] = 320;	// [
			mapping[321] = 321;	// ]
			mapping[322] = 322;	// :
			mapping[323] = 323;	// ;
			
			mapping[324] = 324;	// "
			mapping[325] = 325;	// '
			mapping[326] = 326;	// <
			mapping[327] = 327;	// >
			mapping[328] = 328;	// ?
			mapping[329] = 329;	// ,
			mapping[330] = 330;	// .
			mapping[331] = 331;	// /
			
			


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
