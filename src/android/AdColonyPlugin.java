package com.lkadcolony.cordova.plugin.ad.adcolony;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import android.annotation.TargetApi;
import android.app.Activity;
import android.util.Log;
//
import com.jirbo.adcolony.*;
import org.apache.cordova.PluginResult.Status;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.view.View;
import java.util.Iterator;
//md5
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
//Util
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Surface;
//
import java.util.*;//Random
//
import java.util.HashMap;//HashMap
import java.util.Map;//HashMap

class Util {

	//ex) Util.alert(cordova.getActivity(),"message");
	public static void alert(Activity activity, String message) {
		AlertDialog ad = new AlertDialog.Builder(activity).create();  
		ad.setCancelable(false); // This blocks the 'BACK' button  
		ad.setMessage(message);  
		ad.setButton("OK", new DialogInterface.OnClickListener() {  
			@Override  
			public void onClick(DialogInterface dialog, int which) {  
				dialog.dismiss();                      
			}  
		});  
		ad.show(); 		
	}
	
	//https://gitshell.com/lvxudong/A530_packages_app_Camera/blob/master/src/com/android/camera/Util.java
	public static int getDisplayRotation(Activity activity) {
	    int rotation = activity.getWindowManager().getDefaultDisplay()
	            .getRotation();
	    switch (rotation) {
	        case Surface.ROTATION_0: return 0;
	        case Surface.ROTATION_90: return 90;
	        case Surface.ROTATION_180: return 180;
	        case Surface.ROTATION_270: return 270;
	    }
	    return 0;
	}

	public static final String md5(final String s) {
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
        }
        return "";
    }
}

public class AdColonyPlugin extends CordovaPlugin {
	private static final String LOG_TAG = "AdColonyPlugin";
	private CallbackContext callbackContextKeepCallback;
	//
	protected String appId;
	protected String fullScreenAdZoneId;
	protected String rewardedVideoAdZoneId;
	
    @Override
	public void pluginInitialize() {
		super.pluginInitialize();
		//
    }
	
	//@Override
	//public void onCreate(Bundle savedInstanceState) {//build error
	//	super.onCreate(savedInstanceState);
	//	//
	//}
	
	//@Override
	//public void onStart() {//build error
	//	super.onStart();
	//	//
	//}
	
	@Override
	public void onPause(boolean multitasking) {
		super.onPause(multitasking);
		AdColony.pause();
	}
	
	@Override
	public void onResume(boolean multitasking) {
		super.onResume(multitasking);
		AdColony.resume(cordova.getActivity());
	}
	
	//@Override
	//public void onStop() {//build error
	//	super.onStop();
	//	//
	//}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		//
	}
	
	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

		if (action.equals("setUp")) {
			setUp(action, args, callbackContext);

			return true;
		}			
		else if (action.equals("showFullScreenAd")) {
			showFullScreenAd(action, args, callbackContext);
						
			return true;
		}
		else if (action.equals("showRewardedVideoAd")) {
			showRewardedVideoAd(action, args, callbackContext);
						
			return true;
		}
		
		return false; // Returning false results in a "MethodNotFound" error.
	}
	
	private void setUp(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		final String appId = args.getString(0);
		final String fullScreenAdZoneId = args.getString(1);
		final String rewardedVideoAdZoneId = args.getString(2);
		Log.d(LOG_TAG, String.format("%s", appId));			
		Log.d(LOG_TAG, String.format("%s", fullScreenAdZoneId));			
		Log.d(LOG_TAG, String.format("%s", rewardedVideoAdZoneId));			
		
		callbackContextKeepCallback = callbackContext;
			
		cordova.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				_setUp(appId, fullScreenAdZoneId, rewardedVideoAdZoneId);
			}
		});
	}
	
	private void showFullScreenAd(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

		cordova.getActivity().runOnUiThread(new Runnable(){
			@Override
			public void run() {
				_showFullScreenAd();
			}
		});
	}

	private void showRewardedVideoAd(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

		cordova.getActivity().runOnUiThread(new Runnable(){
			@Override
			public void run() {
				_showRewardedVideoAd();
			}
		});
	}
	
	private void _setUp(String appId, String fullScreenAdZoneId, String rewardedVideoAdZoneId) {
		this.appId = appId;
		this.fullScreenAdZoneId = fullScreenAdZoneId;
		this.rewardedVideoAdZoneId = rewardedVideoAdZoneId;

		String optionString = "";

		String[] zoneIds = new String[2];
		zoneIds[0] = this.fullScreenAdZoneId;
		zoneIds[1] = this.rewardedVideoAdZoneId;

		AdColony.configure(cordova.getActivity(), optionString, this.appId, zoneIds);
		AdColony.addAdAvailabilityListener(new MyAdColonyAdAvailabilityListener());
		AdColony.addV4VCListener(new MyAdColonyV4VCListener());
	}

	private void _showFullScreenAd() {
	
		AdColonyVideoAd ad = new AdColonyVideoAd(fullScreenAdZoneId);
		ad.withListener(new AdColonyAdListenerFullScreenAd());
		ad.show();
	}

	private void _showRewardedVideoAd() {
		
		AdColonyV4VCAd ad = new AdColonyV4VCAd(rewardedVideoAdZoneId);
		ad.withListener(new AdColonyAdListenerRewardedVideoAd());
		ad.show();
	}
	
	class MyAdColonyAdAvailabilityListener implements AdColonyAdAvailabilityListener {
		// Ad Availability Change Callback - update button text
		public void onAdColonyAdAvailabilityChange(boolean available, String zone_id) {
			Log.d(LOG_TAG, String.format("%s: %b", "onAdColonyAdAvailabilityChange", available));
			
			if (available) {
				if(zone_id.equals(fullScreenAdZoneId)) {
					PluginResult pr = new PluginResult(PluginResult.Status.OK, "onFullScreenAdLoaded");
					pr.setKeepCallback(true);
					callbackContextKeepCallback.sendPluginResult(pr);		
				}
				else if(zone_id.equals(rewardedVideoAdZoneId)) {
					PluginResult pr = new PluginResult(PluginResult.Status.OK, "onRewardedVideoAdLoaded");
					pr.setKeepCallback(true);
					callbackContextKeepCallback.sendPluginResult(pr);
				}
			}
		}
	}

	class MyAdColonyV4VCListener implements AdColonyV4VCListener {
		// Reward Callback
		public void onAdColonyV4VCReward(AdColonyV4VCReward reward) {
			Log.d(LOG_TAG, String.format("%s: %b", "onAdColonyV4VCReward", reward.success()));
			
			if (reward.success()) {				
				//reward.name();
				//reward.amount();
								
				PluginResult pr = new PluginResult(PluginResult.Status.OK, "onRewardedVideoAdCompleted");
				pr.setKeepCallback(true);
				callbackContextKeepCallback.sendPluginResult(pr);
				//PluginResult pr = new PluginResult(PluginResult.Status.ERROR);
				//pr.setKeepCallback(true);
				//callbackContextKeepCallback.sendPluginResult(pr);				
			}
		}		
	}
	
	class AdColonyAdListenerFullScreenAd implements AdColonyAdListener {
		// Ad Started Callback, called only when an ad successfully starts playing.
		public void onAdColonyAdStarted( AdColonyAd ad ) {
			Log.d(LOG_TAG, String.format("%s", "onAdColonyAdStarted"));
			
			PluginResult pr = new PluginResult(PluginResult.Status.OK, "onFullScreenAdShown");
			pr.setKeepCallback(true);
			callbackContextKeepCallback.sendPluginResult(pr);
		}
  
		//Ad Attempt Finished Callback - called at the end of any ad attempt - successful or not.
		public void onAdColonyAdAttemptFinished(AdColonyAd ad) {
			Log.d(LOG_TAG, String.format("%s", "onAdColonyAdAttemptFinished"));			

			// You can ping the AdColonyAd object here for more information:
			if (ad.shown()) {
				Log.d(LOG_TAG, String.format("%s", "onAdColonyAdAttemptFinished: shown"));
				
				PluginResult pr = new PluginResult(PluginResult.Status.OK, "onFullScreenAdHidden");
				pr.setKeepCallback(true);
				callbackContextKeepCallback.sendPluginResult(pr);
				//PluginResult pr = new PluginResult(PluginResult.Status.ERROR);
				//pr.setKeepCallback(true);
				//callbackContextKeepCallback.sendPluginResult(pr);				
			}
			else if (ad.notShown()) {
				Log.d(LOG_TAG, String.format("%s", "onAdColonyAdAttemptFinished: notShown"));			
			} 
			else if (ad.noFill()) {
				Log.d(LOG_TAG, String.format("%s", "onAdColonyAdAttemptFinished: noFill"));			
			} 
			else if (ad.canceled()) {
				Log.d(LOG_TAG, String.format("%s", "onAdColonyAdAttemptFinished: canceled"));			
			} 
			else {
				Log.d(LOG_TAG, String.format("%s", "onAdColonyAdAttemptFinished: else"));			
			}
		}
	}

	class AdColonyAdListenerRewardedVideoAd implements AdColonyAdListener {
		// Ad Started Callback, called only when an ad successfully starts playing.
		public void onAdColonyAdStarted( AdColonyAd ad ) {
			Log.d(LOG_TAG, String.format("%s", "onAdColonyAdStarted"));
			
			PluginResult pr = new PluginResult(PluginResult.Status.OK, "onRewardedVideoAdShown");
			pr.setKeepCallback(true);
			callbackContextKeepCallback.sendPluginResult(pr);
			//PluginResult pr = new PluginResult(PluginResult.Status.ERROR);
			//pr.setKeepCallback(true);
			//callbackContextKeepCallback.sendPluginResult(pr);			
		}
  
		//Ad Attempt Finished Callback - called at the end of any ad attempt - successful or not.
		public void onAdColonyAdAttemptFinished(AdColonyAd ad) {
			Log.d(LOG_TAG, String.format("%s", "onAdColonyAdAttemptFinished"));			

			// You can ping the AdColonyAd object here for more information:
			if (ad.shown()) {
				Log.d(LOG_TAG, String.format("%s", "onAdColonyAdAttemptFinished: shown"));
				
				PluginResult pr = new PluginResult(PluginResult.Status.OK, "onRewardedVideoAdHidden");
				pr.setKeepCallback(true);
				callbackContextKeepCallback.sendPluginResult(pr);
				//PluginResult pr = new PluginResult(PluginResult.Status.ERROR);
				//pr.setKeepCallback(true);
				//callbackContextKeepCallback.sendPluginResult(pr);				
			}
			else if (ad.notShown()) {
				Log.d(LOG_TAG, String.format("%s", "onAdColonyAdAttemptFinished: notShown"));			
			} 
			else if (ad.noFill()) {
				Log.d(LOG_TAG, String.format("%s", "onAdColonyAdAttemptFinished: noFill"));			
			} 
			else if (ad.canceled()) {
				Log.d(LOG_TAG, String.format("%s", "onAdColonyAdAttemptFinished: canceled"));			
			} 
			else {
				Log.d(LOG_TAG, String.format("%s", "onAdColonyAdAttemptFinished: else"));			
			}
		}
	}
}
