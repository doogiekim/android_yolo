package com.samsung.android.app.yolo;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private YoloModelRunner mYoloModelRunner;
    private CameraInput mCameraInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (!allPermissionsGranted()) {
            requestPermissions(getRequiredPermissions(), 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        initCamera();
        initModel(mCameraInput);
        initBoxView();
    }

    private void initModel(ImageSource source) {
        mYoloModelRunner = new YoloModelRunner(this);
        mYoloModelRunner.setImageSource(source);
        mYoloModelRunner.start();
    }

    private void initCamera() {
        Log.v(TAG, "initializing CameraInput");

        mCameraInput = new CameraInput(this);
        mCameraInput.start();
    }


    private void initBoxView() {
        YoloBoxView view = findViewById(R.id.yolo_view);
        mYoloModelRunner.setOnObjectDetectedListener(view);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mCameraInput != null) {
            mCameraInput.stop();
        }
        mYoloModelRunner.stop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mYoloModelRunner = null;
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info = getPackageManager()
                            .getPackageInfo(getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (Exception e) {
            return new String[0];
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
