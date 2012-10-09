package com.antoniobusrod;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.util.List;

public class FlashLight extends Activity {

    private static final String TAG=FlashLight.class.getSimpleName();
    private boolean debugOn=false;

    Camera cam;
    boolean lightOn;
    TextView text_view_warn;
    TextView text_view_info;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button button=(Button)findViewById(R.id.button);
        button.setPadding(5,10,5,10);
        button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                if (lightOn) {
                    turnOffFlashLight();
                } else {
                    turnOnFlashLight();
                }
            }
        });
        text_view_warn=(TextView)findViewById(R.id.text_view_warn);
        text_view_info=(TextView)findViewById(R.id.text_view_info);
        lightOn=false;
    }

    @Override
    protected void onResume() {
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
        turnOnFlashLight();
    }

    @Override
    protected void onPause() {
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
        if(lightOn){
            turnOffFlashLight();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_app:
                about_app_OptionsItemSelected();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void about_app_OptionsItemSelected() {
        Intent mIntent=new Intent(this, About.class);
        startActivity(mIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater mMenuInflate =getMenuInflater();
        mMenuInflate.inflate(R.menu.flashlight_menu, menu);
        return true;
    }

    /**
     * Turn ON the flash light
     */
    private int turnOnFlashLight() {
        if(!(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))){
            text_view_warn.setText(R.string.warn_flash_not_available);
            cam = Camera.open();
            getInfoFlashModes(cam);
            cam.release();
            return 0;
        }else{
            cam = Camera.open();
            Camera.Parameters p = cam.getParameters();
            List<String> flash_modes_supported=p.getSupportedFlashModes();
            if(flash_modes_supported.contains("torch")){
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                p.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
                cam.setParameters(p);
                lightOn=true;
                getInfoFlashModes(cam);
                cam.startPreview();
            }else{
                text_view_warn.setText(R.string.warn_flash_mode_torches_na);
            }
            return 1;
        }
    }

    /**
     * Turn OFF the flash light
     */
    private int turnOffFlashLight() {
        if(!(this.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH))){
            return 0;
        }else{
            Camera.Parameters p = cam.getParameters();
            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            cam.setParameters(p);
            getInfoFlashModes(cam);
            cam.stopPreview();
            cam.release();
            lightOn=false;
            return 1;
        }
    }

    /**
     * Display useful information about current flash mode and flash modes
     * available
     * @param cam Camera global variable
     * @return Integer state method execution
     */
    private int getInfoFlashModes(Camera cam){
        if(!debugOn)
            return 0;
        Camera.Parameters p = cam.getParameters();
        List<String> flashModes = p.getSupportedFlashModes();
        String flashMode = p.getFlashMode();
        Log.d(TAG, "Flash modes: "+flashModes);
        Log.d(TAG, "Flash mode: "+flashMode);
        text_view_info.setText("Flash mode: " + flashMode + "\n");
        text_view_info.append("Flash modes available: " + flashModes);
        return 1;
    }
}
