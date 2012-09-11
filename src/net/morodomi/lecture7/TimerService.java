package net.morodomi.lecture7;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

public class TimerService extends Service {
	public static final String INTENT_ACTION = "TIMER WAKE UP";
	private Timer mTimer;

	/** Private Binder class */
	class TimerBinder extends Binder {
		TimerService getService() {
			return TimerService.this;
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if(mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return new TimerBinder();
	}

	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return true;
	}

	public void schedule(final long delay) {
		final int cycle = 5000;
		if(mTimer != null) {
			mTimer.cancel();
		}
		mTimer = new Timer();
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				Intent intent = new Intent(INTENT_ACTION);
				intent.putExtra("delay", delay - cycle);
				sendBroadcast(intent);
			}
		};
		mTimer.schedule(timerTask, cycle);
	}
}
