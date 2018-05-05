
//Master Password: Galaxy11
package com.example.biya.video;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.security.Policy;
import java.util.LinkedList;
import java.util.List;

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

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

//import com.googlecode.mp4parser.authoring.Movie;

public class MainActivity extends Activity implements OnClickListener, SurfaceHolder.Callback {

    public static final String LOGTAG = "VIDEOCAPTURE";
    public static final String TAG = "VIDEOCAPTURE";
private Mp4ParserWrapper mp4ParserWrapper;
    private MediaRecorder recorder;
    private SurfaceHolder holder;
    private CamcorderProfile camcorderProfile;
    private Camera camera;
    public Button recordingButton;
    public Button mergeButton;
    boolean recording = false;
    boolean usecamera = true;
    boolean previewRunning = false;
    public int fileCheck;
    int count =0;

public Button merge;
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
        mergeButton = (Button) findViewById(R.id.MergeButton);
        holder = cameraView.getHolder();
        holder.addCallback(this);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        cameraView.setClickable(false);
        recordingButton.setOnClickListener(this);
        mergeButton.setOnClickListener(this);


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

        if(v.getId() == R.id.MergeButton)
        {

            Log.d("Button Disabled", "you click textview");
            String video1 = Environment.getExternalStorageDirectory() + "/1SecApp/aa.mp4";
            String video2 = Environment.getExternalStorageDirectory() + "/1SecApp/bb.mp4";
            String video3 = Environment.getExternalStorageDirectory() + "/1SecApp/cc.mp4";
            String video4 = Environment.getExternalStorageDirectory() + "/1SecApp/dd.mp4";
            String video5 = Environment.getExternalStorageDirectory() + "/1SecApp/ee.mp4";
            String video6 = Environment.getExternalStorageDirectory() + "/1SecApp/gg.mp4";
            String video7 = Environment.getExternalStorageDirectory() + "/1SecApp/hh.mp4";
            String video8 = Environment.getExternalStorageDirectory() + "/1SecApp/ff.mp4";

            String video9 = Environment.getExternalStorageDirectory() + "/1SecApp/ii.mp4";

            Log.d("Button Disabled", "in onclick video1 == " + video1);
            String[] videos = new String[]{video1, video2,video3,video4,video5,video6,video7,video8,video1,video2,video9};
            try {
                appendVideo(videos);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else
        {
            prepareRecorder();
            recordingButton.setEnabled(false);  //Button Disabled Command Here On Click
            Log.d("Button Disabled", "Button Disabled Ok");
            recorder.start();
            Log.d("Recording Started", "Button recording Must Have started now for 1 sec");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
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

        }




    }

    private String appendVideo(String[] videos) throws IOException{
        Log.v(TAG, "in appendVideo() videos length is " + videos.length);
        Movie[] inMovies = new Movie[videos.length];
        int index = 0;
        for(String video: videos)
        {
            Log.i(TAG, "    in appendVideo one video path = " + video);
            inMovies[index] = MovieCreator.build(video);
            index++;
        }
        List<Track> videoTracks = new LinkedList<Track>();
        List<Track> audioTracks = new LinkedList<Track>();
        for (Movie m : inMovies) {
            for (Track t : m.getTracks()) {
                if (t.getHandler().equals("soun")) {
                    audioTracks.add(t);
                }
                if (t.getHandler().equals("vide")) {
                    videoTracks.add(t);
                }
            }
        }

        Movie result = new Movie();
        Log.v(TAG, "audioTracks size = " + audioTracks.size()
                + " videoTracks size = " + videoTracks.size());
        if (audioTracks.size() > 0) {
            result.addTrack(new AppendTrack(audioTracks.toArray(new Track[audioTracks.size()])));
        }
        if (videoTracks.size() > 0) {
            result.addTrack(new AppendTrack(videoTracks.toArray(new Track[videoTracks.size()])));
        }
        String videoCombinePath = RecordUtil.createFinalPath(MainActivity.this);
        Container out = new DefaultMp4Builder().build(result);
        FileChannel fc = new RandomAccessFile(videoCombinePath, "rw").getChannel();
        out.writeContainer(fc);
        fc.close();
        Log.v(TAG, "after combine videoCombinepath = " + videoCombinePath);
        return videoCombinePath;
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