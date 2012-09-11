package net.morodomi.lecture7;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * Activity for Android Lecture 7
 * Using Android Service class
 * @author Masahiro Morodomi <morodomi at gmail.com>
 *
 */
public class ServiceActivity extends Activity {
	/** Private Broadcast Receiver */
	class TimerReceiver extends BroadcastReceiver {
		/** called this method from service */
		@Override
		public void onReceive(Context context, Intent intent) {
			long delay = intent.getExtras().getLong("delay");
			Toast.makeText(getApplicationContext(), "Alarm:" + delay, Toast.LENGTH_SHORT).show();
			mTimerService.schedule(delay);
		}
	}

	// service object
	private TimerService mTimerService;
	// intent receiver
	private final TimerReceiver mTimerReceiver = new TimerReceiver();
	private ServiceConnection mServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mTimerService = ((TimerService.TimerBinder) service).getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mTimerService = null;
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// set timer
		final TimePicker timePicker = (TimePicker) findViewById(R.id.timepicker);
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(0);
		timePicker.setCurrentMinute(1);
		
		// set onClickListener to bind button
		findViewById(R.id.bind).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				long hour = timePicker.getCurrentHour();
				long min = timePicker.getCurrentMinute();
				mTimerService.schedule((hour * 60 + min) * 60 * 1000);
				moveTaskToBack(true);
			}
		});

		// start service
		Intent intent = new Intent(this, TimerService.class);
		startService(intent);

		// regist receiver
		IntentFilter filter = new IntentFilter(TimerService.INTENT_ACTION);
		registerReceiver(mTimerReceiver, filter);

		// bind service
		bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// unbind service
		unbindService(mServiceConnection);

		// unregist receiver
		unregisterReceiver(mTimerReceiver);

		// stop service
		mTimerService.stopSelf();
	}
}