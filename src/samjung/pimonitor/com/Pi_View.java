package samjung.pimonitor.com;

import java.io.IOException;
import java.net.DatagramPacket;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class Pi_View extends SurfaceView implements Callback {
	
	Display 		display;
	Context 		mContext;
	SurfaceHolder 	mHolder;
	Pi_Thread		mThread;
	Boolean			isLoop = true;
	jniconvert Converting;
	public static int mouse_x,mouse_y, mouse_value ;
	public static boolean clickflag=false;
	
	@SuppressWarnings("deprecation")
	public Pi_View(Context context)
	{
		super(context);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		mHolder = holder;
		mContext = context;
		mThread = new Pi_Thread();
		Converting = new jniconvert();
		
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		mThread.start();
		outputThread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		isLoop =false;
//		usbtest.finish();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	
	class Pi_Thread extends Thread{
		
		Bitmap imgbit;
		Canvas can = null;
		//Bitmap tmpbit;
		public void run()
		{
			
			while(isLoop)
			{
				can = mHolder.lockCanvas();
				try{
					synchronized (mHolder)
					{
						//imgbit = UsbTest.bitmap;
						
//							imgbit = (TransferActivity.bitmap);
							imgbit = Bitmap.createScaledBitmap((TransferActivity.bitmap), TransferActivity.display_width,TransferActivity.display_height, true);
							can.drawBitmap(imgbit,0,0,null);
							
					
					}
				}finally{
					mHolder.unlockCanvasAndPost(can);
				}
			}
		}
		
	}
	
	boolean isClick = false;
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		Log.i("BJ onTouchEvent", "onTouchEvent");
		
		int keyAction = event.getAction();
		mouse_x = (int)event.getX();
		mouse_y = (int)event.getY();
		
		switch (keyAction) {
		
		case MotionEvent.ACTION_UP:
			if(isClick) {
				Log.i("BJ Click Event", "x: "+mouse_x + " y : "+ mouse_y);
				isClick = false;
				clickflag = true;
			}
			
			break;
		case MotionEvent.ACTION_DOWN:
			isClick = true;
			break;
		}
		
		
		
		return true;
	}
	Thread outputThread = new Thread(new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			byte[] sendData = new byte[3072];

			while (true) {

				if (clickflag) {
					mouse_value = 1;
					sendData = Converting.jniConvert(TransferActivity.display_width / mouse_x  ,  TransferActivity.display_height / mouse_y  ,
							mouse_value);

					DatagramPacket sendPacket = new DatagramPacket(sendData,
							sendData.length, TransferActivity.serverAddr,
							TransferActivity.SERVERPORT_IN);
					try {
						TransferActivity.clientSocket.send(sendPacket);
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
