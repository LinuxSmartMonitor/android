package samjung.pimonitor.com;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.Keyboard.Key;
import android.util.AttributeSet;
import android.util.Log;

class MyKeyboard extends KeyboardView {

	/* Keyboard */
	private MyKeyboardActionListener engKey, engCapKey, hanKey, funKey, shiKey, hanShiKey;
	private Keyboard eng = null, engCap = null, han = null, fun = null, shi = null, hanShi = null;
	boolean keyboardCheck = false;
	public static int mKeyCode = 0;
	private String state;
	private String before;

	private final int HangulKey = -14;
	private final int ShiftKey = -6;
	private final int CtrlKey = -11;
	private final int AltKey = -12;
	private final int CapslockKey = -5;
	private final int FunctionKey = -15;

	private boolean isfunction = false;
	private boolean isShift = false;
	private boolean isCtrl = false;
	private boolean isAlt = false;
	private boolean isCapslock = false;

	/* Socket */
	public static String SERVERIP;
	public static int SERVERPORT_IN;
	private DataOutputStream is_SUJIN;

    private DatagramSocket inso_SUJIN;
	// private DatagramSocket clientSocket_SUJIN;

	/* Else */
	private jniconvert Converting;
	private Context context;

	public MyKeyboard(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		eng = new Keyboard(context, R.xml.custom_key);
		engCap = new Keyboard(context, R.xml.custom_cap_key);
		han = new Keyboard(context, R.xml.custom_han_key);
		fun = new Keyboard(context, R.xml.custom_function_key);
		shi = new Keyboard(context, R.xml.custom_shift_key);
		hanShi = new Keyboard(context, R.xml.custom_han_shift_key);
	}

	/* Socket Setting */
	public void setSocket(DatagramSocket inso,jniconvert Converting){	
		this.SERVERPORT_IN = TransferActivity.SERVERPORT_IN;
		this.inso_SUJIN = inso;
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

	public void setActionListenerFunKeyboard(Activity act) {
		state = "Fun";
		this.clearFocus();
		funKey = new MyKeyboardActionListener(act);
		this.setOnKeyboardActionListener(funKey);
		this.setKeyboard(fun);
	}
	
	public void setActionListenerShiftKeyboard(Activity act) {
		this.clearFocus();
		shiKey = new MyKeyboardActionListener(act);
		this.setOnKeyboardActionListener(shiKey);
		this.setKeyboard(shi);
	}
	
	public void setActionListenerHanShiftKeyboard(Activity act) {
		this.clearFocus();
		hanShiKey = new MyKeyboardActionListener(act);
		this.setOnKeyboardActionListener(hanShiKey);
		this.setKeyboard(hanShi);
	}

	
	/* Change the keyboardview's background */
	/* To solve, draw rect over the view(keyboardview) */
	@Override
	public void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);

		Log.d("MyKeyboard", "ondraw()");
		Paint paint = new Paint();
		paint.setColor(getColorWithAlpha(Color.DKGRAY, 0.4f));

		List<Key> keys = getKeyboard().getKeys();
		for (Key key : keys) {
			if ((key.codes[0] == ShiftKey) && isShift) {
				Log.d("KEY", "Drawing shift key " + key.codes[0]);
				canvas.drawRect(key.x, key.y, key.x + key.width, key.y
						+ key.height, paint);
			} else if ((key.codes[0] == CtrlKey) && isCtrl) {
				Log.d("KEY", "Drawing Ctrl key " + key.codes[0]);
				canvas.drawRect(key.x, key.y, key.x + key.width, key.y
						+ key.height, paint);
			} else if ((key.codes[0] == AltKey) && isAlt) {
				Log.d("KEY", "Drawing Alt key " + key.codes[0]);
				canvas.drawRect(key.x, key.y, key.x + key.width, key.y
						+ key.height, paint);
			} else if ((key.codes[0] == CapslockKey) && isCapslock) {
				Log.d("KEY", "Drawing Capslock key " + key.codes[0]);
				canvas.drawRect(key.x, key.y, key.x + key.width, key.y
						+ key.height, paint);
			}

		}
	}

	/* 투명함수 정의 */
	public static int getColorWithAlpha(int color, float ratio) {
		int newColor = 0;
		int alpha = Math.round(Color.alpha(color) * ratio);
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		newColor = Color.argb(alpha, r, g, b);
		return newColor;
	}

	/* Send keyboard event */
	Thread keyboardOutputThread = new Thread(new Runnable() {

		private int mKeyCode;
		byte[] sendData = new byte[3072];

		@Override
		public void run() {

			while (true) {
				while (keyboardCheck) {
					keyboardCheck = false;
					mKeyCode = MyKeyboard.mKeyCode;
					sendData = Converting.jniConvert(-1, mKeyCode, -1);
					try {
						//DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddr_SUJIN, SERVERPORT_IN);
						//clientSocket_SUJIN.send(sendPacket);
						InetAddress inserverAddr;
		            	inserverAddr = InetAddress.getByName(TransferActivity.SERVERIPADDR);
						DatagramPacket dpack = new DatagramPacket(sendData, sendData.length,inserverAddr,3491);   
						DatagramPacket readtmp = null;
						//inso_SUJIN.receive(readtmp);
		            	inso_SUJIN.send(dpack);
					} catch (IOException e1) {
						Log.d("MyKeyboard", "send error.");
						e1.printStackTrace();
					}
				}
			}
		}
	});

	
	/* MyKeyboardActionListener */
	private class MyKeyboardActionListener implements OnKeyboardActionListener {

		final int mapping[] = new int[500];
		private boolean caps = false;
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
			if (primaryCode == FunctionKey) {
				Log.d("MyKeyboard", "Function key");
				isfunction = !isfunction;
				
				if(isfunction){
					before = state;
					setActionListenerFunKeyboard(owner);
				}
				else {
					if(before.compareTo("Eng")==0){
						setActionListenerEngKeyboard(owner);
					}
					else if (before.compareTo("EngCap")==0){
						setActionListenerEngCapKeyboard(owner);
					}
					else if(before.compareTo("Han")==0){
						setActionListenerHanKeyboard(owner);
					}
				}
				return;
			}

			/* key code is negative key */
			if (primaryCode < 0) {

				switch (primaryCode) {
				case CapslockKey:
					isCapslock = !isCapslock;
					if (state.compareTo("Eng") == 0)
						setActionListenerEngCapKeyboard(owner);
					else if (state.compareTo("EngCap") == 0)
						setActionListenerEngKeyboard(owner);
					break;

				case HangulKey:
					if (state.compareTo("Eng") == 0
							|| state.compareTo("EngCap") == 0)
						setActionListenerHanKeyboard(owner);
					else if (state.compareTo("Han") == 0)
						setActionListenerEngKeyboard(owner);
					break;

				case ShiftKey:
					isShift = !isShift;
					if(isShift){
						 if(state.compareTo("Eng")==0){
							 setActionListenerShiftKeyboard(owner);
						 }
						 else if(state.compareTo("Han")==0){
							 // shift 한글키로 변경
							 setActionListenerHanShiftKeyboard(owner);
						 }
					}
					else {
						if(state.compareTo("Eng")==0){
							setActionListenerEngKeyboard(owner);
						}
						else if(state.compareTo("Han")==0){
							setActionListenerHanKeyboard(owner);
						}
					}
					break;

				case CtrlKey:
					isCtrl = !isCtrl;
					break;

				case AltKey:
					isAlt = !isAlt;
					break;

				default:
					break;
				}

				temp = Math.abs(primaryCode) + 200;
				mKeyCode = mapping[temp];
				keyboardCheck = true;
			}

			/* if primaryCode is 97-122, key code is capital key */
			else if (caps && (primaryCode >= 97) && (primaryCode <= 122)) {
				temp = primaryCode + 300;
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
		private void init() {

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
			mapping[203] = 15;	// TAB -3
			mapping[205] = 58;	// CAPS -5
			mapping[206] = 54;	// RIGHT SHIFT -6
			mapping[201] = 1;	// ESC -1
			mapping[211] = 97;	// RIGHT CTRL -11
			mapping[213] = 57;	// SPACE -13
			mapping[212] = 100;	// RIGHT ALT -12
			mapping[204] = 28;	// ENTER -4
			mapping[202] = 14;	// BACK -2
			mapping[214] = 122;	// 한영 -14
			mapping[218] = 111;	// DELETE -18
			
			mapping[207] = 103;	// UP -7
			mapping[209] = 108;	// DOWN -9
			mapping[208] = 105;	// LEFT -8
			mapping[210] = 106;	// RIGHT -10
			// until -19

			mapping[220] = 59; // F1 -20
			mapping[221] = 60; // F2 -21
			mapping[222] = 61; // F3 -22
			mapping[223] = 62; // F4 -23
			mapping[224] = 63; // F5 -24
			mapping[225] = 64; // F6 -25
			mapping[226] = 65; // F7 -26
			mapping[227] = 66; // F8 -27
			mapping[228] = 67; // F9 -28
			mapping[229] = 68; // F10 -29
			mapping[230] = 87; // F11 -30
			mapping[231] = 88; // F12 -31

			// function
			mapping[300] = 300; // !
			mapping[301] = 301; // @
			mapping[302] = 302; // #
			mapping[303] = 303; // $
			mapping[304] = 304; // %
			mapping[305] = 305; // ^
			mapping[306] = 306; // &
			mapping[307] = 307; // *
			mapping[308] = 308; // (
			mapping[309] = 309; // )
			mapping[310] = 311; // +
			
			mapping[311] = 41; // '
			mapping[312] = 339; // ~
			mapping[313] = 53; // /
			mapping[314] = 13; // =
			mapping[315] = 43; // \
			mapping[316] = 341; // |
			mapping[317] = 324; // {
			mapping[318] = 325; // }
			mapping[319] = 26; // [
			mapping[320] = 27; // ]
			
			mapping[321] = 12; // -
			mapping[322] = 40; // '
			mapping[323] = 349; // <
			mapping[324] = 350; // >
			mapping[325] = 351; // ?
			mapping[326] = 51; // ,
			mapping[327] = 52; // .
			mapping[328] = 337; // :
			mapping[329] = 39; // ;

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
