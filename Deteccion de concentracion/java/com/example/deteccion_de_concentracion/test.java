package com.example.deteccion_de_concentracion;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Region;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.ImageCaptureConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;

import org.opencv.core.Rect;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class test extends AppCompatActivity implements View.OnClickListener {
    private PreviewView previewView;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private TextView textView;
    private ImageCapture imageCapture;
    LinearLayout llBottom;

    ArrayList<Integer> rgb_medio;
    ArrayList<Integer> rgb_image;

    ArrayList<Float> poli;

    Float value;
    SeekBar barra;


    Bitmap prueba;

    TextView text;
    int val;
    int val2=0;

    SurfaceHolder holder;
    SurfaceView surfaceView;
    SurfaceView surfaceView2;
    ProcessCameraProvider cameraProvider;
    int  xOffset, yOffset, boxWidth, boxHeight;
    FloatingActionButton btnCapture, btnOk, btnCancel;

    private static final String FILE_NAME2="/rgbtest/cascsa.jpg";

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        previewView = findViewById(R.id.previewView);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        //Create the bounding box
        surfaceView = findViewById(R.id.container);

        llBottom = findViewById(R.id.llBottom);


        btnCapture = findViewById(R.id.btnCapture);
        btnOk = findViewById(R.id.btnAccept);
        btnCancel = findViewById(R.id.btnReject);

        btnOk.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnCapture.setOnClickListener(this);


        barra=findViewById(R.id.seekBar);
        val=barra.getProgress();


        text=findViewById(R.id.textView);

//        textureView = findViewById(R.id.textureView);
        poli=new ArrayList<>();




        Intent intent = getIntent();
        poli.add(intent.getFloatExtra("poli1",0));
        poli.add(intent.getFloatExtra("poli2",0));

        btnCapture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cameraProvider.unbindAll();
//                holder.setFormat(PixelFormat.OPAQUE);

//                ViewGroup.LayoutParams lp = surfaceView.getLayoutParams();
//
//
//

                holder = surfaceView.getHolder();
                holder.setFormat(PixelFormat.TRANSPARENT);
//                llBottom.setVisibility(View.VISIBLE);


                Canvas canvas = holder.lockCanvas();
                int height = previewView.getHeight();
                int width = previewView.getWidth();


                int with6;
                if (width<height){
                    with6=width;
                }else {
                    with6=height;
                }

                Path mPath = new Path();

                Paint paint = new Paint();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.BLACK);
                paint.setStrokeWidth(4.0f);
//                canvas.drawLine(0,0,canvas.getWidth(),canvas.getHeight(),paint);
//                mPath.rewind();

                mPath.addRect(width/2-with6*val/200,height/2+with6*val/200,width/2+with6*val/200,height/2-with6*val/200,Path.Direction.CCW);

                mPath.addRect(0,height,width,height,Path.Direction.CCW);
                canvas.clipPath(mPath, Region.Op.DIFFERENCE);
                canvas.drawRect(0,height,width,0,paint);
                paint.setColor(Color.WHITE);
//                canvas.drawRect(0,height,width,height-200,paint);


//                canvas.drawRect(width/2-width/4,height/2+height/4,width/2+width/4,height/2-height/4,paint);
//                canvas.clipPath(mPath, Region.Op.DIFFERENCE);
//                canvas.drawLine(0,0,canvas.getWidth(),canvas.getHeight(),paint);
                holder.unlockCanvasAndPost(canvas);


//                textureView.setSurfaceTexture((SurfaceTexture) previewView.getSurfaceProvider());


                showAcceptedRejectedButton(true);



            }
        });





        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    cameraProvider = cameraProviderFuture.get();
                    cameraProvider.unbindAll();
                    bindImageAnalysis(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }










        }, ContextCompat.getMainExecutor(this));
    }





    @SuppressLint("RestrictedApi")
    private void bindImageAnalysis(@NonNull ProcessCameraProvider cameraProvider) {
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder().setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @Override
            @SuppressLint("UnsafeOptInUsageError")
            public void analyze(@NonNull ImageProxy image) {
                if (image == null || image.getImage() == null) {
                    return;
                }
                val=barra.getProgress();
                holder = surfaceView.getHolder();

                holder.setFormat(PixelFormat.TRANSPARENT);
//                holder.setFormat(PixelFormat.OPAQUE);
                Canvas canvas = holder.lockCanvas();
                canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                prueba=previewView.getBitmap();
                if (prueba!=null){
                    rgb_image=clip_image(prueba);
                    ArrayList<Float> grays=grayscale(rgb_image);
                    text.setTextColor(Color.GREEN);

                if ((poli.get(0)!=0) && (poli.get(1)!=0)) {
                    text.setText(String.format("%.1f", poli.get(0) * grays.get(0) + poli.get(1)) + "   " +  String.valueOf(rgb_image.get(1)) + "   " +  String.valueOf(rgb_image.get(2)) + "   " +  String.valueOf(rgb_image.get(3)));
                }
                else{
                    Log.i("poli","no pudo cargar polinomio");
                    text.setText( "   " + String.valueOf(rgb_image.get(1)) + "   " + String.valueOf(rgb_image.get(2)) + "   " + String.valueOf(rgb_image.get(3)));
                }

                Canvas canvas2 = new Canvas(prueba);

                if (canvas!=null){

                    int with=canvas.getWidth();
                    int height=canvas.getHeight();
                    int with2=canvas2.getWidth();
                    int height2=canvas2.getHeight();

                    int with3=prueba.getWidth();
                    int height3=prueba.getHeight();
                    int with4;
                    int with5;

                    if (with2<height2){
                        with4=with2;
                    }else {
                        with4=height2;
                    }
                    if (with3<height3){
                        with5=with3;
                    }else {
                        with5=height3;
                    }
                    prueba=Bitmap.createBitmap(prueba,with3/2-with5*val/200,height3/2-with5*val/200,with5*val/100,with5*val/100);
//                    canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                    //border's properties
                    Paint paint = new Paint();
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setColor(Color.BLACK);
                    paint.setStrokeWidth(4.0f);
                    //canvas2.drawRect(with2/2-with4*val/200,height2/2+with4*val/200,with2/2+with4*val/200,height2/2-with4*val/200,paint);
                    canvas.drawRect(with/2-with4*val/200,height/2+with4*val/200,with/2+with4*val/200,height/2-with4*val/200,paint);
                    holder.unlockCanvasAndPost(canvas);
                    //text.append("\n"+String.format("%.1f",with5*val/100)+"X"+String.format("%.1f", with5*val/100));
                    text.append("\n"+String.valueOf(with5*val/100)+"X"+String.valueOf( with5*val/100));

                    val2=val;

                }
                }
                image.close();
            }
        });
        cameraProvider.unbindAll();





        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());






        imageCapture =
                new ImageCapture.Builder()
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();


        cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector, imageCapture, imageAnalysis, preview);

    }











    /**
     *
     * For drawing the rectangular box
     */
    private void DrawFocusRect(int color) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = previewView.getHeight();
        int width = previewView.getWidth();

        //cameraHeight = height;
        //cameraWidth = width;

        int left, right, top, bottom, diameter;

        diameter = width;
        if (height < width) {
            diameter = height;
        }

        int offset = (int) (0.05 * diameter);
        diameter -= offset;

        Canvas canvas = holder.lockCanvas();
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);
        //border's properties
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(5);

        left = width / 2 - diameter / 3;
        top = height / 2 - diameter / 3;
        right = width / 2 + diameter / 3;
        bottom = height / 2 + diameter / 3;

        xOffset = left;
        yOffset = top;
        boxHeight = bottom - top;
        boxWidth = right - left;
        //Changing the value of x in diameter/x will change the size of the box ; inversely proportionate to x
        canvas.drawRect(left, top, right, bottom, paint);
        holder.unlockCanvasAndPost(canvas);
    }
    private void showAcceptedRejectedButton(boolean acceptedRejected) {

        llBottom.bringToFront();
        if (acceptedRejected) {
//            CameraX.unbind(preview, imageAnalysis);
            llBottom.setVisibility(View.VISIBLE);

//            holder.setFormat(PixelFormat.OPAQUE);
            btnCapture.hide();
            llBottom.bringToFront();
//            surfaceView.setVisibility(View.GONE);
//            textureView.setVisibility(View.GONE);
        } else {
            btnCapture.show();
            llBottom.setVisibility(View.GONE);

//            textureView.setVisibility(View.VISIBLE);
            surfaceView.setVisibility(View.VISIBLE);


            surfaceView.post(new Runnable() {
                @Override
                public void run() {
                    bindImageAnalysis(cameraProvider);
                }
            });
            holder.setFormat(PixelFormat.OPAQUE);
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnReject:
                showAcceptedRejectedButton(false);
                break;
            case R.id.btnAccept:
                Uri image_cut = null;

                Log.i("EXCEPT","------------\n------------\n------------\n");
                try {
                    File file =createImageFile();
                    if (prueba==null){
                        Log.i("msj","imagen igual null");
                    }
                    try {

                        image_cut=saveImage(prueba); // bmp is your Bitmap instance
                        // PNG is a lossless format, the compression factor (100) is ignored
                        Log.i("save_image","image_saved");
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.i("create_file","error_file");
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i("create_out","error_createfile");
                }
                Intent intent2 = getIntent();
                rgb_medio=rgb_image;
//                if (rgb_medio==null)
//                {rgb_medio=rgb_image;}
//                else{
//                    rgb_medio.addAll(rgb_image);
//
//                }
                intent2.putExtra("value", rgb_medio);
                intent2.putExtra("recorte",image_cut);
//                intent2.putExtra("imag_orig",previewView.getBitmap());

//                setResult(Activity.RESULT_OK, intent2); //The data you want to send back
                setResult(Activity.RESULT_OK, intent2);
//                showAcceptedRejectedButton(false);
                killActivity();


        }


    }
    private void killActivity() {
        finish();
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        return image;
    }
    private Uri saveImage(Bitmap bitmap) throws IOException {
        boolean saved;
        OutputStream fos=null;
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        String name=imageFileName;
        Uri image_out=null;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentResolver resolver = this.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, name);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "concentracion");
            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            fos = resolver.openOutputStream(imageUri);
            image_out=imageUri;
        } else {
            String imagesDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM).toString() + File.separator + "concentracion";

            File file = new File(imagesDir);

            if (!file.exists()) {
                file.mkdir();
            }

            File image = new File(imagesDir, name + ".png");

            image_out=Uri.fromFile(image);


            fos = new FileOutputStream(image);

        }

        saved = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();

        return image_out;
    }
    private ArrayList<Integer> clip_image(Bitmap operation){
        ArrayList<Integer> argb_medio = new ArrayList<>();
        long redBucket = 0;
        long greenBucket = 0;
        long blueBucket = 0;
        long alphaBucket = 0;
        long pixelCount = 0;

        for (int y = 0; y < operation.getHeight(); y++)
        {
            for (int x = 0; x < operation.getWidth(); x++)
            {
                int c = operation.getPixel(x, y);

                pixelCount++;
                redBucket += Color.red(c);
                greenBucket += Color.green(c);
                blueBucket += Color.blue(c);
                alphaBucket += Color.alpha(c);
            }
        }

//        int averageColor = Color.rgb(redBucket / pixelCount,
//                greenBucket / pixelCount,
//                blueBucket / pixelCount);
        argb_medio.add((int) (alphaBucket / pixelCount));
        argb_medio.add((int) (redBucket / pixelCount));
        argb_medio.add((int) (greenBucket / pixelCount));
        argb_medio.add((int) ( blueBucket / pixelCount));



        return argb_medio;
    }
    private ArrayList<Float> grayscale(ArrayList<Integer>  mol_color_rgb){
        ArrayList<Float> grays = new ArrayList<>();
        int size=mol_color_rgb.size();
        int r_color,g_color,b_color;

        for (int i=0;i<size;i=i+4){
            r_color=mol_color_rgb.get(i+1);
            g_color=mol_color_rgb.get(i+2);
            b_color=mol_color_rgb.get(i+3);
            grays.add((float) ((r_color+g_color+b_color)/3));
        }


        return grays;
    }

}

