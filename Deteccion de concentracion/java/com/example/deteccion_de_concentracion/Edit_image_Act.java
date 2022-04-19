package com.example.deteccion_de_concentracion;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Edit_image_Act extends AppCompatActivity {
    ArrayList<Integer>  cutcircle=new ArrayList<Integer>();
    ArrayList<Float> cutdesv=new ArrayList<Float>();
    Object[] arr=new Object[]{cutcircle, cutdesv};;
    Integer ancho_orig=0;
    Integer alto_orig=0;
    int[] size2={400,400};
    Rect cuadrado;
    Rect dim_orig;
    Uri name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_image);
    }

    /** Start pick image activity with chooser. */
    public void onSelectImageClick(View view) {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("Cortar")
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setCropMenuCropButtonTitle("Listo")
                .setRequestedSize(400, 400)
                .setCropMenuCropButtonIcon(R.drawable.ic_baseline_check_circle_24)
                .start(this);
    }
    public void exit(View view) {

        //After clicking on "save"
        Intent intent = new Intent();
        intent.putExtra("nombre_imagen",name);
        intent.putExtra("value",cutcircle);
        intent.putExtra("orig. dim",size2);
        intent.putExtra("recorte",cuadrado);
        intent.putExtra("imag_orig",dim_orig);
        intent.putExtra("desvio",cutdesv);
        setResult(13, intent); //The data you want to send back
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,@Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap operation = null;
        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                operation = result.getBitmap();
                ((ImageView) findViewById(R.id.quick_start_cropped_image)).setImageURI(result.getUri());
                cuadrado=result.getCropRect();
                dim_orig=result.getWholeImageRect();
                Toast.makeText(
                        this, "Corte exitoso, muestra: " + result.getSampleSize(), Toast.LENGTH_LONG)
                        .show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Corte fallado: " + result.getError(), Toast.LENGTH_LONG).show();
            }
        }

        ImageView ala = findViewById(R.id.quick_start_cropped_image);
        BitmapDrawable drawable = (BitmapDrawable) ala.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        if (bitmap != null) {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hhmmss");
            String date=simpleDateFormat.format(new Date());
            arr= clip_image(bitmap);
            cutcircle= (ArrayList<Integer>) arr[0];
            cutdesv= (ArrayList<Float>) arr[1];
            size2[0]=bitmap.getWidth();
            size2[1]=bitmap.getHeight();
            String message="(R "+String.valueOf(cutcircle.get(1))+"G "+String.valueOf(cutcircle.get(2))+"B "+String.valueOf(cutcircle.get(3))+")";
            name=Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Img_cut"+date+message ,message ));

        }

    }

//    public static final String insertImage(ContentResolver cr,
//                                           Bitmap source,
//                                           String title,
//                                           String description) {
//
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.TITLE, title);
//        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
//        values.put(MediaStore.Images.Media.DESCRIPTION, description);
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//        // Add the date meta data to ensure the image is added at the front of the gallery
//        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
//        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
//
//        Uri url = null;
//        String stringUrl = null;    /* value to be returned */
//
//        try {
//            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//
//            if (source != null) {
//                OutputStream imageOut = cr.openOutputStream(url);
//                try {
//                    source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
//                } finally {
//                    imageOut.close();
//                }
//
//                long id = ContentUris.parseId(url);
//                // Wait until MINI_KIND thumbnail is generated.
//                Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
//                // This is for backward compatibility.
//                storeThumbnail(cr, miniThumb, id, 50F, 50F, MediaStore.Images.Thumbnails.MICRO_KIND);
//            } else {
//                cr.delete(url, null, null);
//                url = null;
//            }
//        } catch (Exception e) {
//            if (url != null) {
//                cr.delete(url, null, null);
//                url = null;
//            }
//        }
//
//        if (url != null) {
//            stringUrl = url.toString();
//        }
//
//        return stringUrl;
//    }
//    private static final Bitmap storeThumbnail(
//            ContentResolver cr,
//            Bitmap source,
//            long id,
//            float width,
//            float height,
//            int kind) {
//
//        // create the matrix to scale it
//        Matrix matrix = new Matrix();
//
//        float scaleX = width / source.getWidth();
//        float scaleY = height / source.getHeight();
//
//        matrix.setScale(scaleX, scaleY);
//
//        Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
//                source.getWidth(),
//                source.getHeight(), matrix,
//                true
//        );
//
//        ContentValues values = new ContentValues(4);
//        values.put(MediaStore.Images.Thumbnails.KIND,kind);
//        values.put(MediaStore.Images.Thumbnails.IMAGE_ID,(int)id);
//        values.put(MediaStore.Images.Thumbnails.HEIGHT,thumb.getHeight());
//        values.put(MediaStore.Images.Thumbnails.WIDTH,thumb.getWidth());
//
//        Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);
//
//        try {
//            OutputStream thumbOut = cr.openOutputStream(url);
//            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
//            thumbOut.close();
//            return thumb;
//        } catch (FileNotFoundException ex) {
//            return null;
//        } catch (IOException ex) {
//            return null;
//        }
//    }
//
//




    protected  Object[]clip_image(Bitmap operation) {
        int redBucket=0;
        int greenBucket=0;
        int blueBucket=0;
        int alphaBucket=0;
        double pixelCount= 0.1;
        ArrayList<Integer> argb_medio = new ArrayList<Integer>();
        ArrayList<Float> argb_desvio = new ArrayList<Float>();

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
                // does alpha matter?
            }
        }
        int avred = (int) (redBucket / pixelCount);
        int avgreen = (int) (greenBucket / pixelCount);
        int avblue = (int) (blueBucket / pixelCount);
        int avalpha= (int) (alphaBucket / pixelCount);


        float varred=0;
        float vargreen=0;
        float varblue=0;
        float rangred=0;
        float ranggreen=0;
        float rangblue=0;
        for (int y = 0; y < operation.getHeight(); y++) {
            for (int x2 = 0; x2 <operation.getWidth(); x2++) {
                int color = operation.getPixel(x2,y);
                rangred= (float) Math.pow(Color.red(color) - avred,2f);
                varred=varred+rangred;


                rangblue= (float) Math.pow(Color.blue(color) - avblue,2f);
                varblue=varblue+rangblue;

                ranggreen= (float) Math.pow(Color.green(color) - avgreen,2f);
                vargreen=vargreen+ranggreen;
            }
        }
        varred= varred /(operation.getHeight()*operation.getWidth());
        vargreen= vargreen / (operation.getHeight()*operation.getWidth());
        varblue= varblue / (operation.getHeight()*operation.getWidth());

        argb_desvio.add(varred);
        argb_desvio.add(vargreen);
        argb_desvio.add(varblue);


        argb_medio.add( i);

        argb_medio.add(avred);
        argb_medio.add(avgreen);
        argb_medio.add(avblue);
        argb_medio.add(avalpha);
        return  new Object[]{argb_medio,argb_desvio};
    }



}