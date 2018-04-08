
//Master Password: Galaxy11
package com.example.biya.video;

import java.io.File;
import java.io.IOException;
import java.security.Policy;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.googlecode.mp4parser.authoring.Movie;

public class MainActivity extends Activity implements OnClickListener, SurfaceHolder.Callback {

    public static final String LOGTAG = "VIDEOCAPTURE";

    private MediaRecorder recorder;
    private SurfaceHolder holder;
    private CamcorderProfile camcorderProfile;
    private Camera camera;
    public Button recordingButton;

    boolean recording = false;
    boolean usecamera = true;
    boolean previewRunning = false;
    public int fileCheck;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        fileCheck = 0;
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT);

        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.screenBrightness = 1000;
        getWindow().setAttributes(lp);

        camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);


        setContentView(R.layout.activity_main);

        SurfaceView cameraView = (SurfaceView) findViewById(R.id.CameraView);

        recordingButton = (Button) findViewById(R.id.RecordingButton);
        holder = cameraView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        cameraView.setClickable(false);
        recordingButton.setOnClickListener(this);
    }

    private void prepareRecorder() {
        recorder = new MediaRecorder();
        camera.unlock();
        recorder.setCamera(camera);

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

        recorder.setProfile(camcorderProfile);
        recorder.setPreviewDisplay(holder.getSurface());
        recorder.setVideoEncodingBitRate(3000000);


        // This is all very sloppy
       // if (camcorderProfile.fileFormat == MediaRecorder.OutputFormat.MPEG_4)
        {
            try {
                fileCheck++;
                File directory = new File(Environment.getExternalStorageDirectory()+File.separator+"1SecApp");
                if(!directory.exists())
                    directory.mkdirs();

//                File newFile = File.createTempFile("videocapture"+fileCheck, ".mp4", Environment.getExternalStorageDirectory()+"");

                File newFile = File.createTempFile("videocapture"+fileCheck, ".mp4", directory);

                // recorder.setVideoFrameRate(100);
                recorder.setOutputFile(newFile.getAbsolutePath());
            } catch (IOException e) {
                Log.v(LOGTAG,"Couldn't create file");
                e.printStackTrace();
                finish();
            }
        }
        //recorder.setMaxDuration(50000); // 50 seconds
        //recorder.setMaxFileSize(5000000); // Approximately 5 megabytes



        try {
            recorder.prepare();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            finish();
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }

    public void onClick(View v) {

        prepareRecorder();
        recordingButton.setEnabled(false);  //Button Disabled Command Here On Click
        Log.d("Button Disabled", "Button Disabled Ok");
        recorder.start();
        Log.d("Recording Started", "Button recording Must Have started now for 1 sec");
        new Handler().postDelayed(new Runnable(){

            @Override
            public void run(){
                recordingButton.setEnabled(true);
                Log.d("Button Enabled", "Button Enabled Ok");

                recorder.stop();     // stop recording
                recorder.reset();    // set state to idle
                recorder.release();  // release resources back to the system
                recorder = null;
                Log.d("Recording Stopped", "Recording must have Stopped at this point");

                //recorder.start();
                //Log.v(LOGTAG, "Recording Started");
            }
        }, 2000);   //Resets Button Active after 1 secs;


        if (recording) {
            if (usecamera) {
               // try {
                 //   camera.reconnect();
                //} catch (IOException e) {
                 //   e.printStackTrace();
               // }
            }
            // recorder.release();
           // recording = false;
           // Log.v(LOGTAG, "Recording Stopped");
            // Let's prepareRecorder so we can record again
           // prepareRecorder();
        } else {
            //recording = true;
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.v(LOGTAG, "surfaceCreated");

        if (usecamera) {
            camera = Camera.open();
            camera.setDisplayOrientation(90);


            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
                previewRunning = true;
                
            }
            catch (IOException e) {
                Log.e(LOGTAG,e.getMessage());
                e.printStackTrace();
            }
        }

    }


    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.v(LOGTAG, "surfaceChanged");

        if (!recording && usecamera) {
            if (previewRunning){
                camera.stopPreview();
            }

            try {
                Camera.Parameters p = camera.getParameters();

                p.setPreviewSize(camcorderProfile.videoFrameWidth, camcorderProfile.videoFrameHeight);
                p.setPreviewFrameRate(camcorderProfile.videoFrameRate);

                camera.setParameters(p);

                camera.setPreviewDisplay(holder);
                camera.startPreview();
                previewRunning = true;
            }
            catch (IOException e) {
                Log.e(LOGTAG,e.getMessage());
                e.printStackTrace();
            }

           // prepareRecorder();
        }
    }


    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.v(LOGTAG, "surfaceDestroyed");
        if (recording) {
            recorder.stop();
            recording = false;
        }
       // recorder.release();
        if (usecamera) {
            previewRunning = false;
            //camera.lock();
            camera.release();
        }
        finish();
    }


}