package com.example.deteccion_de_concentracion;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ImageCapture;
//import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

//import com.google.common.util.concurrent.ListenableFuture;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static java.lang.Math.*;
import static org.opencv.imgproc.Imgproc.HoughCircles;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "OCVSample::Activity";
    private int STORAGE_PERMISSION_CODE = 23;
    private String load_data="";
    TextView text;
    private Mat input ;
    private Mat circles ;
    private Mat dstImage ;
    private Mat mask;
    private Mat grayimage;
    private Mat input2;
    Button btopen;
    Button btgaleria;
    Button cargar;
    Button colorimetria;
    Button test;
    Bitmap captureimage;
    ImageView image_capture;
    private String currentPhotoPath;
    private ArrayList<Integer> arreglo = new ArrayList<Integer>();
    private ArrayList<Integer> argb_medio = new ArrayList<Integer>();
    private ArrayList<Integer> cutdesv = new ArrayList<Integer>();

    private File photoFile;

    private Object[] arr=new Object[]{argb_medio, cutdesv};
    public static final String EXTRA_MESSAGE = "com.example.deteccion_concentracion.MESSAGE";
    public static final String EXTRA_MESSAGE2 = "com.example.deteccion_concentracion.MESSAGE2";
    public static final String EXTRA_MESSAGE3 = "com.example.deteccion_concentracion.MESSAGE3";
    public static final String EXTRA_MESSAGE4 = "com.example.deteccion_concentracion.MESSAGE4";
    private static final String FILE_NAME="/rgbtest/Lista_concentraciones_RGB.txt";
    String message="";




    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    input=new Mat() ;
                    circles=new Mat()  ;
                    dstImage=new Mat()  ;
                    mask=new Mat() ;
                    input2=new Mat() ;
                    grayimage=new Mat() ;
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text =findViewById(R.id.text2);
        text.setTextColor(Color.GREEN);
        btopen=findViewById(R.id.bt_open);
        //btopen.setVisibility(View.GONE);
        colorimetria=findViewById(R.id.colorimetria);
        cargar=findViewById(R.id.button);
        btgaleria=findViewById(R.id.bt_galeria);
        test=findViewById(R.id.camera_capture_button);

        image_capture=findViewById(R.id.viewFinder);




        // Request camera permissions
//        if (allPermissionsGranted()) {
//            startCamera();
//        } else {
//            ActivityCompat.requestPermissions(
//                    this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
//            );
//        }
//
//        // Set up the listener for take photo button
//        //camera_capture_button = findViewById<RecyclerView>(R.id.camera_capture_button);
//        camera_capture_button.setOnClickListener { takePhoto() }
//
//        outputDirectory = getOutputDirectory();
//
//        cameraExecutor = Executors.newSingleThreadExecutor();





        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }
        btgaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 80);
//                cameraintent.putExtra();
            }
        });
        btopen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraintent=new Intent();
                cameraintent=new Intent(v.getContext(),Edit_image_Act.class);
                ArrayList<Float> cutdesv=new ArrayList<Float>();
//                cameraintent.putExtra();
                startActivityForResult(cameraintent,13);
                argb_medio=cameraintent.getIntegerArrayListExtra("value");
                int[] size2={400,400};
                size2=cameraintent.getIntArrayExtra("orig. dim");
                Rect cuadrado;
                Rect imag_orig;
                cutdesv=cameraintent.getParcelableExtra("desvio");
                cuadrado=cameraintent.getParcelableExtra("recorte");
                imag_orig=cameraintent.getParcelableExtra("imag_orig");
                if (argb_medio!=null) {

                    text.setText("(R: " + String.valueOf(argb_medio.get(1)) + " ,B: " + String.valueOf(argb_medio.get(2)) + " ,G: " + String.valueOf(argb_medio.get(3)) + " )(");
                }
                //startActivityForResult(cameraintent, ); //I always put 0 for someIntValue
                //editIntent.setDataAndType(uri, "image/*");




            }
        });
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),test.class);
                load(v);



                if (load_data!=""){
                ArrayList<Float>  mol_color_rgb0=carga_data_txt(load_data);
                ArrayList<Float>  mol_color_rgb1=ordenar_lista(mol_color_rgb0);
                ArrayList<Float> grays=grayscale(mol_color_rgb1);
                ArrayList<Float> polinomio= new ArrayList<Float>();
                polinomio= trendline2(mol_color_rgb1,grays);
                intent.putExtra("poli1",polinomio.get(0));
                intent.putExtra("poli2",polinomio.get(1));//data guardada imagen
                }
                startActivityForResult(intent,400);





//                startActivity(intent);
            }
        });
        cargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load(v);
                Intent intent=new Intent(v.getContext(), Concentration_color.class);
                intent.putExtra(EXTRA_MESSAGE3,load_data);//data guardada imagen
                intent.putExtra(EXTRA_MESSAGE4,argb_medio);// rgb imagen tomada

                startActivity(intent);
            }
        });
        colorimetria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(view.getContext(), An_concent.class);


                load(view);

                intent.putExtra(EXTRA_MESSAGE, message);// media y desvio de imagen tomada
//                Bundle bundle =new Bundle();
//                bundle.putIntegerArrayList(EXTRA_MESSAGE2,arreglo);

                intent.putExtra(EXTRA_MESSAGE3,load_data);//data guardada imagenes
                intent.putExtra(EXTRA_MESSAGE2,arreglo);// media y desvio de imagen tomada
                intent.putExtra(EXTRA_MESSAGE4,argb_medio);// media y desvio de imagen tomada
                startActivity(intent);
//                startActivityForResult(intent,0);
            }
        });



    }
    @Override
    public void onResume()
    {

        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }




    public void load(View v){
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        StringBuilder sb=new StringBuilder();

        try {
            File textfile=new File(getFilesDir(),FILE_NAME);
            FileInputStream fis=new FileInputStream(textfile);
            if (fis!=null){
                InputStreamReader isr=new InputStreamReader(fis);
                BufferedReader bfr=new BufferedReader(isr);
                String line=null;
                while ((line=bfr.readLine())!=null){
                    sb.append(line+"\n");
                }
                Toast.makeText(this,"Abierto", Toast.LENGTH_LONG).show();
                fis.close();
            }
            load_data= String.valueOf(sb);
//            text.setText(sb);
        } catch (FileNotFoundException e) {
            Toast.makeText(this,"No Abierto 0 "+getFilesDir()+FILE_NAME, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this,"No Abierto 1 "+getFilesDir()+FILE_NAME, Toast.LENGTH_LONG).show();
        }


    }
    @Override
    protected void onActivityResult(int requestcode,int resultcode,@Nullable Intent data) {


        if ((requestcode==400) ){
            if(resultcode ==-1) {
                super.onActivityResult(requestcode, resultcode, data);

                argb_medio = data.getIntegerArrayListExtra("value");
                int[] size2 = {400, 400};
                size2 = data.getIntArrayExtra("orig. dim");
                Uri imag_cut;
                cutdesv = data.getParcelableExtra("desvio");
                imag_cut = data.getParcelableExtra("recorte");
                try {
                    InputStream imageStream = getContentResolver().openInputStream(imag_cut);
                    captureimage = BitmapFactory.decodeStream(imageStream);
                    image_capture.setImageBitmap(captureimage);
                    message ="/"+captureimage.getWidth()+"/"+captureimage.getHeight()+"/";
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if (argb_medio!=null) {
                    float[] hsv = new float[3];
                    Color.RGBToHSV(argb_medio.get(1), argb_medio.get(2), argb_medio.get(3), hsv);
                    text.setTextColor(Color.GREEN);
                    text.setText(" (R: " + String.valueOf(argb_medio.get(1)) + " ,B: " + String.valueOf(argb_medio.get(2)) + " ,G: " + String.valueOf(argb_medio.get(3)) + " )(H:" + String.format("%.1f", hsv[0]) + " S " + String.format("%.1f", hsv[1]) + " V " + String.format("%.1f", hsv[2]) + ")");
                }

            }


        }



        if ((requestcode==80) && (resultcode==-1) ){
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                captureimage=selectedImage;
                argb_medio= clip_image(captureimage);
                image_capture.setImageBitmap(captureimage);
                message ="/"+captureimage.getWidth()+"/"+captureimage.getHeight()+"/";
                if (argb_medio!=null) {
                    float[] hsv = new float[3];
                    Color.RGBToHSV(argb_medio.get(1), argb_medio.get(2), argb_medio.get(3), hsv);
                    text.setTextColor(Color.GREEN);
                    text.setText(" (R: " + String.valueOf(argb_medio.get(1)) + " ,B: " + String.valueOf(argb_medio.get(2)) + " ,G: " + String.valueOf(argb_medio.get(3)) + " )(H:" + String.format("%.1f", hsv[0]) + " S " + String.format("%.1f", hsv[1]) + " V " + String.format("%.1f", hsv[2]) + ")");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Algo fue mal", Toast.LENGTH_LONG).show();
            }
        }
        if ((requestcode == 100) ) {
            //captureimage = (Bitmap) data.getExtras().get("data");
            try {
                captureimage= MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(photoFile));
                image_capture.setImageBitmap(captureimage);
            } catch (IOException e) {
                e.printStackTrace();
            }


            argb_medio=clip_image(captureimage);
            ArrayList<Integer> desv=obt_desv(captureimage,argb_medio);
            message ="/"+captureimage.getWidth()+"/"+captureimage.getHeight()+"/"+desv.get(0)+"/"+desv.get(1)+"/"+desv.get(2)+"/";

            if (argb_medio!=null) {
                float[] hsv = new float[3];
                Color.RGBToHSV(argb_medio.get(1), argb_medio.get(2), argb_medio.get(3), hsv);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hhmmss");
                String date = simpleDateFormat.format(new Date());

                text.setTextColor(Color.GREEN);
                text.setText(" (R: " + String.valueOf(argb_medio.get(1)) + " ,B: " + String.valueOf(argb_medio.get(2)) + " ,G: " + String.valueOf(argb_medio.get(3)) + " )(H " + String.format("%.1f", hsv[0]) + " S " + String.format("%.1f", hsv[1]) + " V " + String.format("%.1f", hsv[2]) + ")");
                MediaStore.Images.Media.insertImage(getContentResolver(), captureimage, "Conc" + date, message);
            }



        }
        if (requestcode == 13){
//                cameraintent.putExtra();
            super.onActivityResult(requestcode, resultcode, data);
            //startActivityForResult(data,13);
            argb_medio= Objects.requireNonNull(data).getIntegerArrayListExtra("value");
            int[] size2={400,400};
            size2=data.getIntArrayExtra("orig. dim");
            if (argb_medio!=null) {
                float[] hsv = new float[3];
                Color.RGBToHSV(argb_medio.get(1),argb_medio.get(2),argb_medio.get(3), hsv);
                android.graphics.Rect cuadrado;
                android.graphics.Rect imag_orig;
                cuadrado=data.getParcelableExtra("recorte");
                imag_orig=data.getParcelableExtra("imag_orig");

                Uri imag_cut=data.getParcelableExtra("nombre_imagen");

                try {
                    InputStream imageStream = getContentResolver().openInputStream(imag_cut);
                    captureimage = BitmapFactory.decodeStream(imageStream);
                    image_capture.setImageBitmap(captureimage);
                    message ="/"+captureimage.getWidth()+"/"+captureimage.getHeight()+"/";
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                //image_capture.setImageBitmap(captureimage);

                text.setTextColor(Color.GREEN);
                text.setText(" (R: " + String.valueOf(argb_medio.get(1)) + " ,B: " + String.valueOf(argb_medio.get(2)) + " ,G: " + String.valueOf(argb_medio.get(3)) + " )(H:"+String.format("%.1f",hsv[0])+" S "+String.format("%.1f",hsv[1])+" V "+String.format("%.1f",hsv[2])+")");
                message ="corte izq:"+ String.valueOf(cuadrado.left)+" arriba:"+ String.valueOf(cuadrado.top)+"abajo"+ String.valueOf(cuadrado.bottom)+"der" + String.valueOf(cuadrado.right) +" Tamanio imagen :"+ String.valueOf(imag_orig.width())+"X" + String.valueOf(imag_orig.height()) +"(Ancho: " +String.valueOf(size2[0])+" Alto: " +String.valueOf(size2[1])+") (R: " + String.valueOf(argb_medio.get(1)) + " ,B: " + String.valueOf(argb_medio.get(2)) + " ,G: " + String.valueOf(argb_medio.get(3)) + " )(H:"+String.format("%.1f",hsv[0])+" S "+String.format("%.1f",hsv[1])+" V "+String.format("%.1f",hsv[2])+")";

                //text.setText("corte izq:"+ String.valueOf(cuadrado.left)+" arriba:"+ String.valueOf(cuadrado.top)+"abajo"+ String.valueOf(cuadrado.bottom)+"der" + String.valueOf(cuadrado.right) +" Tamanio imagen :"+ String.valueOf(imag_orig.width())+"X" + String.valueOf(imag_orig.height()) +"(Ancho: " +String.valueOf(size2[0])+" Alto: " +String.valueOf(size2[1])+") (R: " + String.valueOf(argb_medio.get(1)) + " ,B: " + String.valueOf(argb_medio.get(2)) + " ,G: " + String.valueOf(argb_medio.get(3)) + " )(H:"+String.format("%.1f",hsv[0])+" S "+String.format("%.1f",hsv[1])+" V "+String.format("%.1f",hsv[2])+")");
            }
        }
    }


    protected ArrayList<Integer> obt_desv(Bitmap operation,ArrayList<Integer> argb_medio){

        int varred=0;
        int vargreen=0;
        int varblue=0;
        int rangred=0;
        int ranggreen=0;
        int rangblue=0;
        int avred=0;
        int avblue=0;
        int avgreen=0;
        avred=argb_medio.get(1);
        avblue=argb_medio.get(2);
        avgreen=argb_medio.get(3);
        ArrayList<Integer> argb_desvio = new ArrayList<Integer>();
        for (int y = 0; y < operation.getHeight(); y++) {
            for (int x2 = 0; x2 <operation.getWidth(); x2++) {
                int color = operation.getPixel(x2,y);
                rangred= (int) pow(Color.red(color) - avred,2);
                varred=varred+rangred;


                rangblue= (int) pow(Color.blue(color) - avblue,2);
                varblue=varblue+rangblue;

                ranggreen= (int) pow(Color.green(color) - avgreen,2);
                vargreen=vargreen+ranggreen;
            }
        }
        varred= varred /(operation.getHeight()*operation.getWidth());
        vargreen= vargreen / (operation.getHeight()*operation.getWidth());
        varblue= varblue / (operation.getHeight()*operation.getWidth());

        argb_desvio.add(varred);
        argb_desvio.add(vargreen);
        argb_desvio.add(varblue);
        return  argb_desvio;
    }
    protected  ArrayList<Integer> clip_image(Bitmap operation) {
        int redBucket=0;
        int greenBucket=0;
        int blueBucket=0;
        int alphaBucket=0;
        double pixelCount= 0.1;
        ArrayList<Integer> argb_medio = new ArrayList<Integer>();

        int i=0;
        for (int y = 0; y < operation.getHeight(); y++) {
            for (int x2 = 0; x2 <operation.getWidth(); x2++) {
                int color = operation.getPixel(x2,y);


                redBucket += Color.red(color);
                greenBucket += Color.blue(color);
                blueBucket += Color.green(color);
                alphaBucket +=Color.alpha(color);
                if (Color.red(color)==0 && Color.blue(color)==0 && Color.green(color)==0){
                    pixelCount=pixelCount;
                }else{
                    pixelCount=pixelCount+1;
                }
                // alpha?
            }
        }
        int avred = (int) (redBucket / pixelCount);
        int avgreen = (int) (greenBucket / pixelCount);
        int avblue = (int) (blueBucket / pixelCount);
        int avalpha= (int) (alphaBucket / pixelCount);



        argb_medio.add( i);

        argb_medio.add(avred);
        argb_medio.add(avgreen);
        argb_medio.add(avblue);
        argb_medio.add(avalpha);
        return  argb_medio;
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

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }



    private ArrayList<Float> grayscale(ArrayList<Float>  mol_color_rgb){
        ArrayList<Float> grays = new ArrayList<>();
        int size=mol_color_rgb.size();
        Float r_color,g_color,b_color;

        for (int i=0;i<size;i=i+4){
            r_color=mol_color_rgb.get(i+1);
            g_color=mol_color_rgb.get(i+2);
            b_color=mol_color_rgb.get(i+3);
            grays.add((r_color+g_color+b_color)/3);
        }


        return grays;
    }

    private ArrayList<Float> trendline2(ArrayList<Float> mol_hsv,ArrayList<Float> xvalues){
        ArrayList<Float> xAxisValues = new ArrayList<>();
        ArrayList<Float> yAxisValues= new ArrayList<>();
        float xAxisValueSum=0;
        float yAxisValueSum=0;
        float xxsum=0;
        float xysum=0;
        float yysum=0;
        int size=mol_hsv.size();
        int count=size/4;
        int j=0;
        for (int i=0;i<(size);i=i+4){
            yAxisValues.add(mol_hsv.get(i));
            yAxisValueSum+=mol_hsv.get(i);
            xAxisValueSum+=xvalues.get(j);
            xAxisValues.add(xvalues.get(j));
            xxsum+=xvalues.get(j)*xvalues.get(j);
            xysum+=mol_hsv.get(i)*xvalues.get(j);
            yysum+=mol_hsv.get(i)*mol_hsv.get(i);
            j+=1;

        }

        float slope=((count*xysum)-(xAxisValueSum*yAxisValueSum))/((count*xxsum)-(xAxisValueSum*xAxisValueSum));
        if (Float.isNaN(slope)){
            slope= (float) 999;
        }
        float intercept=(yAxisValueSum-slope*xAxisValueSum)/count;
        float start=slope*xAxisValues.get(0)+intercept;
        float end=slope*xAxisValues.get(xAxisValues.size()-1)+intercept;
        float R= (float) ((count*xysum-xAxisValueSum*yAxisValueSum)/Math.sqrt((count*xxsum-xAxisValueSum*xAxisValueSum)*(count*yysum-yAxisValueSum*yAxisValueSum)));
        ArrayList<Float> polinomio=new ArrayList<>();
        polinomio.add(slope);
        polinomio.add(intercept);
        polinomio.add(R);
        return polinomio;
    }
    public ArrayList<Float> carga_data_txt(String info){
        String[] line=info.split("\n");
        ArrayList<Float>  mol_color= new ArrayList<Float>();
        try{

            for (int i=0;i<line.length;i++) {
                try{
                    String[] MRGB=line[i].split("/");
                    String item1=MRGB[0];
                    mol_color.add(Float.parseFloat(item1));
                    String item2=MRGB[1];
                    mol_color.add(Float.parseFloat(item2));
                    String item3=MRGB[2];
                    mol_color.add(Float.parseFloat(item3));
                    String item4=MRGB[3];
                    mol_color.add(Float.parseFloat(item4));

                }
                catch (Exception e){
                    Toast.makeText(this,"linea "+String.valueOf(i)+" no leida", Toast.LENGTH_LONG).show();
                }
            }}
        catch (Exception e){

        }

        return mol_color;
    }
    private ArrayList<Float> ordenar_lista(ArrayList<Float> mol_hsv){
        ArrayList<Float> hsv_list = new ArrayList<>();
        int sizear= mol_hsv.size();
        int i;
        float minimo=999;
        int indice=0;
        while ( hsv_list.size()!=sizear){

            for ( i=0;i< mol_hsv.size();i=i+4){
                if ( mol_hsv.get(i)<minimo){
                    indice=i;
                    minimo=mol_hsv.get(i);
                }
            }
            minimo=999;
            hsv_list.add(mol_hsv.get(indice));
            hsv_list.add(mol_hsv.get(indice+1));
            hsv_list.add(mol_hsv.get(indice+2));
            hsv_list.add(mol_hsv.get(indice+3));
            mol_hsv.remove(indice);
            mol_hsv.remove(indice);
            mol_hsv.remove(indice);
            mol_hsv.remove(indice);
        }
        return hsv_list;
    }














}
