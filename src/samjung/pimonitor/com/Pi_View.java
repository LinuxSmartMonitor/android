package samjung.pimonitor.com;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.Display;
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
						
						if(TransferActivity.bitmap != null)
						{
//							imgbit = (TransferActivity.bitmap);
							imgbit = Bitmap.createScaledBitmap((TransferActivity.bitmap), TransferActivity.display_width,TransferActivity.display_height, true);
							can.drawBitmap(imgbit,0,0,null);	
						}
					
					}
				}finally{
					mHolder.unlockCanvasAndPost(can);
				}
			}
		}
		
	}
}
