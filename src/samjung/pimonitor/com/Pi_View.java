package samjung.pimonitor.com;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
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
	
	@SuppressWarnings("deprecation")
	public Pi_View(Context context)
	{
		super(context);
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		mHolder = holder;
		mContext = context;
		mThread = new Pi_Thread();
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
		
		public void run()
		{
			
			while(isLoop)
			{
				can = mHolder.lockCanvas();
				try{
					synchronized (mHolder)
					{
						//imgbit = UsbTest.bitmap;
						
							if(TransferActivity.bitmap!=null)
							{
								imgbit = Bitmap.createScaledBitmap((TransferActivity.bitmap), 512, 384, true);
								can.drawBitmap(imgbit,0,0,null);
							}
							else
								can.drawColor(Color.BLUE);
						
					}
				}finally{
					mHolder.unlockCanvasAndPost(can);
				}
			}
		}
		
	}

}
