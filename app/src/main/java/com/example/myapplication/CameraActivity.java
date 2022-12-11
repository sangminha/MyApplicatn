package com.example.myapplication;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;
import android.view.SurfaceView;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import static org.opencv.imgproc.Imgproc.rectangle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;


public class CameraActivity extends AppCompatActivity
        implements CameraBridgeViewBase.CvCameraViewListener2 {

    private static final String TAG = "AndroidOpenCv";
    private JavaCameraView mCameraView;
    private Mat mInputMat;
    private Mat mResultMat;
    private int mMode;



    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mMode = intent.getIntExtra("mode", 0);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_camera);

        if (!hasPermissions(PERMISSIONS)) {
            requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
        }

        mCameraView = findViewById(R.id.activity_surface_view);
        mCameraView.setVisibility(SurfaceView.VISIBLE);
        mCameraView.setCvCameraViewListener(this);
        if (mMode == 2) {
            mCameraView.setCameraIndex(0);
        } else {
            mCameraView.setCameraIndex(mMode); // rear = 0, front = 1
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mCameraView != null)
            mCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "onResume : OpenCV initialization error");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this, mLoaderCallback);
        } else {
            Log.d(TAG, "onResume : OpenCV initialization success");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private final BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    mCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mCameraView != null) {
            mCameraView.disableView();
        }
    }

    // This method is invoked when camera preview has started.
    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    // This method is invoked when camera preview has been stopped for some reason.
    @Override
    public void onCameraViewStopped() {

    }

    // This method is invoked when delivery of the frame needs to be done.
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mInputMat = inputFrame.rgba();

        if (mMode == 2) {
            if (mResultMat == null) {
                mResultMat = new Mat(mInputMat.rows(), mInputMat.cols(), mInputMat.type());
            }
            return mResultMat;
        } else {
            detectFace();
            return mInputMat;
        }
    }

    private void detectFace() {
        CascadeClassifier cascade = new CascadeClassifier();
        if (cascade.empty()) {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            cascade.load(path + "/haarcascade_frontalface_default.(2)xml");
        }

        if (cascade.empty()) {
            return;
        }
        Mat gray = new Mat();
        Mat resizingGray = new Mat();
        Imgproc.cvtColor(mInputMat, gray, Imgproc.COLOR_BGRA2GRAY);
        Imgproc.resize(gray, resizingGray, new Size(640, 360));

        MatOfRect faces = new MatOfRect();
        cascade.detectMultiScale(resizingGray, faces, 1.3, 3, 0, new Size(40, 40));
        for (int i = 0; i < faces.total(); i++) {
            Rect rc = faces.toList().get(i);
            rc.x *= 3;
            rc.y *= 3;
            rc.width *= 3;
            rc.height *= 3;
            rectangle(mInputMat, rc, new Scalar(255, 50, 100), 2);
        }
    }


    // permission
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS = {"android.permission.CAMERA"};

    private boolean hasPermissions(String[] permissions) {
        int result;
        for (String perms : permissions) {
            result = ContextCompat.checkSelfPermission(this, perms);
            if (result == PackageManager.PERMISSION_DENIED) {
                return false;
            }
        }
        return true;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0) {
                boolean cameraPermissionAccepted = grantResults[0]
                        == PackageManager.PERMISSION_GRANTED;

                if (!cameraPermissionAccepted)
                    showDialogForPermission();
            }
        }
    }


    private void showDialogForPermission() {

        AlertDialog.Builder builder = new AlertDialog.Builder(CameraActivity.this);
        builder.setTitle("알림");
        builder.setMessage("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }
}