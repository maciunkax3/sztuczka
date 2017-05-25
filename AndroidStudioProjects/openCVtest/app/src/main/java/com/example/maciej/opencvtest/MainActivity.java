package com.example.maciej.opencvtest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_IMAGE = 100;
    String pathPic="/mnt/sdcard/abc0.jpg";
    int counter;
    File dest;
    ImageView imV;
    private CascadeClassifier cascadeClassifier;
    private Mat grayscaleImage;
    private int absoluteFaceSize;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button bt=((Button)findViewById(R.id.button));
        Button bt2=((Button)findViewById(R.id.button2));
        imV=((ImageView)findViewById(R.id.imageView2));
        counter=0;
        if(!OpenCVLoader.initDebug()){
            ((TextView)findViewById(R.id.sample_text)).setText("przypau");
        }
        else
            ((TextView)findViewById(R.id.sample_text)).setText("tez przypau");
        initializeOpenCVDependencies();
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePic();
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markFace();
            }
        });
        updateImage(new File(pathPic));

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    void takePic(){
        String name = "abc"+Integer.toBinaryString(counter++);
        dest=new File(Environment.getExternalStorageDirectory(), name+".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(dest));
        pathPic=dest.getPath();
        startActivityForResult(intent, REQUEST_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK ){
            updateImage(new File(pathPic));
        }
    }
    void markFace(){
        if(grayscaleImage==null)grayscaleImage=new Mat();
        String name = Environment.getExternalStorageDirectory().toString();
        File destt=new File(Environment.getExternalStorageDirectory().toString());
        File file=new File(destt, "cba.jpg");
        Mat pictureToMark= Imgcodecs.imread(pathPic);
        Mat marked= picToMark(pictureToMark);
        String nazwa=file.toString();
        Imgcodecs.imwrite(nazwa, marked);
        updateImage(new File(nazwa));
    }



    void updateImage(File a){
        imV.setImageURI(Uri.fromFile(a));
    }
    private void initializeOpenCVDependencies() {
        try {
            // Copy the resource into a temp file so OpenCV can load it
            InputStream is = getResources().openRawResource(R.raw.lbpcascade_frontalface);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            File mCascadeFile = new File(cascadeDir, "lbpcascade_frontalface.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);


            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = is.read(buffer)) != -1)
            {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();
// Load the cascade classifier
            cascadeClassifier = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            cascadeClassifier.load(mCascadeFile.getAbsolutePath());
            if (cascadeClassifier.empty()) {
                cascadeClassifier = null;
            }
        } catch (Exception e) {

            Log.e("OpenCVActivity", "Error loading cascade", e);
        }

    }
    public Mat picToMark(Mat aInputFrame){
        Imgproc.cvtColor(aInputFrame, grayscaleImage, Imgproc.COLOR_RGBA2RGB);

        MatOfRect faces = new MatOfRect();

        // Use the classifier to detect faces
        if (cascadeClassifier != null) {
            cascadeClassifier.detectMultiScale(grayscaleImage, faces, 1.1, 2, 2,
                    new Size(absoluteFaceSize, absoluteFaceSize), new Size());
        }

        // If there are any faces found, draw a rectangle around it
        Rect[] facesArray = faces.toArray();
        for (int i = 0; i <facesArray.length; i++)
            Imgproc.rectangle(aInputFrame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0, 255), 3);

        return aInputFrame;
    }
}
