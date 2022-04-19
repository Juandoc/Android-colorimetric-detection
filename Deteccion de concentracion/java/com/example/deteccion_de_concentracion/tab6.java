package com.example.deteccion_de_concentracion;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.SimpleOnScaleGestureListener;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;

public class tab6 extends AppCompatActivity {
    private int STORAGE_PERMISSION_CODE = 23;
    Color_concAdapter adapter;
    Drawable gra;
    zoom touch;

    ImageView gradientesv2;
    private static final String filename = "example.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab7);


        // Construct the data source
        ArrayList<Color_conc> arrayOfColor_conc = new ArrayList<Color_conc>();
        // Create the adapter to convert the array to views
        adapter = new Color_concAdapter(this, arrayOfColor_conc);
        // Attach the adapter to a ListView
        ListView listView = findViewById(R.id.shopping_listView);
        listView.setAdapter(adapter);
        Bundle extras = getIntent().getExtras();
        ArrayList<Integer> argb_medio = (ArrayList<Integer>) extras.get("argb_medio");
        ArrayList<Float> argb_medio2=(ArrayList<Float>) extras.get(An_concent.RGB_val);
        ArrayList<Float> mol_color_rgb2 = (ArrayList<Float>) extras.get(An_concent.RGB_values);
        String message = (String) extras.get("message");
        //Intent intent2 = getIntent();
        double[] lab = new double[3];
        double r, g, b, X, Y, Z, xr, yr, zr;
        double Xr, Yr, Zr;
        ArrayList<Float> xyz_list = new ArrayList<Float>();


        int size = mol_color_rgb2.size();
        for (int i = 0; i < (size); i = i + 4) {
            ArrayList<Float> rgb = new ArrayList<>();
            rgb.add(mol_color_rgb2.get(i + 1));
            rgb.add(mol_color_rgb2.get(i + 2));
            rgb.add(mol_color_rgb2.get(i + 3));
            ArrayList<Float> xyz = rgb2xyz(rgb);
            xyz_list.add(xyz.get(0));
            xyz_list.add(xyz.get(1));
            xyz_list.add(xyz.get(2));


            Color_conc newColor_conc = new Color_conc(String.valueOf( mol_color_rgb2.get(i)), String.format("%.2f", xyz.get(0)), String.format("%.2f", xyz.get(1)), String.format("%.2f", xyz.get(2)), 5, false, "CIE-L*ab");
            adapter.add(newColor_conc);
        }

        ArrayList<Float> rgb = new ArrayList<>();
        rgb.add(Float.valueOf(argb_medio.get(1)));
        rgb.add(Float.valueOf(argb_medio.get(2)));
        rgb.add(Float.valueOf(argb_medio.get(3)));
        ArrayList<Float> xyz = rgb2xyz(rgb);
        ArrayList<Float> rgb2 = new ArrayList<>();
        rgb2.add(Float.valueOf(255));
        rgb2.add(Float.valueOf(255));
        rgb2.add(Float.valueOf(255));

        ArrayList<Float> xyz2 = rgb2xyz(rgb2);
        Color_conc newColor_conc = new Color_conc(String.format("%.2f",argb_medio2.get(0)), String.format("%.2f", xyz.get(0)), String.format("%.2f", xyz.get(1)), String.format("%.2f", xyz.get(2)), 5, true, "CIE-L*ab_muestra");
        adapter.add(newColor_conc);
        adapter.notifyDataSetChanged();

        //gradientesv2 = findViewById(R.id.img);

        touch = (zoom) findViewById(R.id.img);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.lab_color_at_luminance_75_);
        touch.setImageBitmap(bitmap);
        gra = getResources().getDrawable(R.drawable.lab_color_at_luminance_75_, null);
        touch.resources(gra);

        //gradientesv2.setImageResource(R.drawable.lab_color_at_luminance_75_);

        //gra = getResources().getDrawable(R.drawable.lab_color_at_luminance_75_, null);






//        ArrayList<Float>  xyz_list1=traslate_coor(xyz_list);
//
//        ArrayList<Float>  xyz1=traslate_coor(xyz);


        if (!mol_color_rgb2.isEmpty() && !xyz.isEmpty() && !xyz_list.isEmpty())
            trendline(mol_color_rgb2, xyz, xyz_list);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                AlertDialog.Builder adb = new AlertDialog.Builder(tab6.this);
                adb.setTitle("Eliminar?");
                adb.setMessage("Esta seguro de elimnar el elemento " + position);
                adb.setNegativeButton("Cancelar", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Color_conc item = (Color_conc) listView.getItemAtPosition(position);
                        adapter.remove(item);
                        adapter.notifyDataSetChanged();
                        ArrayList<Float> erased = erase_line(mol_color_rgb2, position);
                        ((An_concent) getParent()).load_data(argb_medio, erased, message, 5);

                    }
                });
                adb.show();
            }
        });


    }




    private ArrayList<Float> erase_line(ArrayList<Float> load_data, int position) {
        ArrayList<Float> data = new ArrayList<>();
        ArrayList<Float> lines = load_data;
        for (int i = 0; i < lines.size(); i++) {
            if ((i < position * 4) || (i > ((position + 1) * 4 - 1))) {
                data.add(lines.get(i));
            }
        }

        return data;
    }

    private ArrayList<Float> rgb2xyz(ArrayList<Float> argb_medio) {
        double r, g, b, fx, fy, fz, X, Y, Z, xr, yr, zr;
        ArrayList<Float> rgb = new ArrayList<>();
        float Ls, as, bs;

        r = argb_medio.get(0) / 255.0;
        g = argb_medio.get(1) / 255.0;
        b = argb_medio.get(2) / 255.0;

        if (r > 0.04045) {
            r = Math.pow((r + 0.055) / 1.055, 2.4);
        } else {
            r = r / 12.92;
        }

        if (g > 0.04045) {
            g = Math.pow((g + 0.055) / 1.055, 2.4);
        } else {
            g = g / 12.92;
        }

        if (b > 0.04045) {
            b = Math.pow((b + 0.055) / 1.055, 2.4);
        } else {
            b = b / 12.92;
        }

        r *= 100;
        g *= 100;
        b *= 100;
        String TAG = "RGB";
        Log.d(TAG, "R:" + r + " G:" + g + " B:" + b);
        X = 0.4124 * r + 0.3576 * g + 0.1805 * b;
        Y = 0.2126 * r + 0.7152 * g + 0.0722 * b;
        Z = 0.0193 * r + 0.1192 * g + 0.9505 * b;
//        xr=( X/0.964221f);
//        yr=( Y/1.0f);
//        zr=( Z/0.825211f);
//        float eps = 216.f / 24389.f;
//        float k = 24389.f / 27.f;
//
//
//        if (xr > eps)
//            fx = (float) Math.pow(xr, 1 / 3.);
//        else
//            fx = (float) ((k * xr + 16.) / 116.);
//
//        if (yr > eps)
//            fy = (float) Math.pow(yr, 1 / 3.);
//        else
//            fy = (float) ((k * yr + 16.) / 116.);
//
//        if (zr > eps)
//            fz = (float) Math.pow(zr, 1 / 3.);
//        else
//            fz = (float) ((k * zr + 16.) / 116);
////
//        Ls = (float) ((116 * fy) - 16);
//        as = (float) (500 * (fx - fy));
//        bs = (float) (200 * (fy - fz));
//
//        int[] lab = new int[3];
//        lab[0] = (int) (2.55 * Ls + .5);
//        lab[1] = (int) (as + .5);
//        lab[2] = (int) (bs + .5);
        float[] XYZ = {0, 0, 0};
        XYZ[0] = (float) (Math.round(X * 10000) / 10000.0);
        XYZ[1] = (float) (Math.round(Y * 10000) / 10000.0);
        XYZ[2] = (float) (Math.round(Z * 10000) / 10000.0);


        XYZ[0] = (float) (XYZ[0] / 95.047);
        XYZ[1] = (float) (XYZ[1] / 100.0);
        XYZ[2] = (float) (XYZ[2] / 108.883);

        int num = 0;
        for (int i = 0; i < XYZ.length; i++) {
            if (XYZ[i] > 0.008856) {
                XYZ[i] = (float) Math.pow(XYZ[i], 0.3333333333333333);
            } else {
                XYZ[i] = (float) ((7.787 * XYZ[i]) + (16 / 116));

                XYZ[num] = XYZ[i];
                num = num + 1;
            }
        }

        float[] Lab = {0, 0, 0};

        float L = (116 * XYZ[1]) - 16;
        float a = 500 * (XYZ[0] - XYZ[1]);
        float b2 = 200 * (XYZ[1] - XYZ[2]);

        Lab[0] = (float) (Math.round(L * 10000) / 10000.0);
        Lab[1] = (float) (Math.round(a * 10000) / 10000.0);
        Lab[2] = (float) (Math.round(b2 * 10000) / 10000.0);


        rgb.add((float) Math.round(Lab[0]));
        rgb.add((float) (Lab[1] + 128) / 255);
        rgb.add((float) (Lab[2] + 128) / 255);
//        +128)/255)


        return rgb;
    }


    private void trendline(ArrayList<Float> mol_rgb, ArrayList<Float> xyz_medio, ArrayList<Float> xyz) {
//        ArrayList<Float>  mol_color_rgb=carga_data_txt(load_data);
//        ArrayList<Float> mol_hsv0=convert_hsv(mol_color_rgb);
//        ArrayList<Float> mol_hsv= ordenar_lista(mol_hsv0);


        ArrayList<Float> xAxisValues = new ArrayList<>();
        ArrayList<Float> yAxisValues = new ArrayList<>();
        float xAxisValueSum = 0;
        float yAxisValueSum = 0;
        float xxsum = 0;
        float xysum = 0;
        float yysum = 0;
//        int size=xyz.size()-xyz.size()/3;
        int size = xyz.size();
        int count = size / 3;
        for (int i = 0; i < (size); i = i + 3) {
            yAxisValues.add(Float.valueOf(xyz.get(i + 1)));
            yAxisValueSum += xyz.get(i + 1);
            xAxisValueSum += xyz.get(i + 2);
            xAxisValues.add(Float.valueOf(xyz.get(i + 2)));
            xxsum += xyz.get(i + 2) * xyz.get(i + 2);
            xysum += xyz.get(i + 1) * xyz.get(i + 2);
            yysum += xyz.get(i + 1) * xyz.get(i + 1);

        }

        float slope = ((count * xysum) - (xAxisValueSum * yAxisValueSum)) / ((count * xxsum) - (xAxisValueSum * xAxisValueSum));
        float intercept = (yAxisValueSum - slope * xAxisValueSum) / count;
        float start = slope * xAxisValues.get(0) + intercept;
        float end = slope * xAxisValues.get(xAxisValues.size() - 1) + intercept;
        float R = (float) ((count * xysum - xAxisValueSum * yAxisValueSum) / Math.sqrt((count * xxsum - xAxisValueSum * xAxisValueSum) * (count * yysum - yAxisValueSum * yAxisValueSum)));
        //ImageView gradientesv2=findViewById(R.id.imageView2);
        //Drawable gra = getResources().getDrawable( R.drawable.gradientsv ,null);
        Bitmap aux2 = Bitmap.createBitmap(900, 900, Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(aux2);


        Paint paint2 = new Paint();
        paint2.setColor(Color.RED);
        paint2.setStrokeWidth(2);
        int offset2 = 35;


        gra.setBounds(0, 0, canvas2.getWidth(), canvas2.getHeight());
        gra.draw(canvas2);

//        gra.setBounds(0, 0, canvas2.getWidth(), canvas2.getHeight());
//        gra.draw(canvas2);
        paint2.setTextSize(30);
        float maximox = 98.254f;
        float minimox = -86.185f;
        int maximo = 1000;

        float maximoy = 94.482f;
        float minimoy = -107.863f;


//        maximo= (float) (xyz.get(xyz.size()-4)*1.3);
        ArrayList<Float> polinomio = new ArrayList<>();
        polinomio.add(slope);
        polinomio.add(intercept);
        polinomio.add(R);

        //canvas2.drawText(String.format("%.1f", (hsv[1]*100*0.86-9.45))+"10-6M ",canvas2.getWidth()*hsv[1],canvas2.getHeight()*hsv[2]-30,paint2);
        if (xyz.size() > 3) {
            Log.i("cac", String.valueOf((polinomio.get(0) * xyz_medio.get(1) + polinomio.get(1))));
            int h = canvas2.getHeight();
            int w = canvas2.getWidth();
            paint2.setColor(Color.GREEN);

//            canvas2.drawText(String.format("%.1f", (polinomio.get(0)*xyz_medio.get(1)+polinomio.get(1))),canvas2.getWidth()*xyz_medio.get(1),canvas2.getHeight()-(canvas2.getHeight()*(polinomio.get(0)*xyz_medio.get(1)+polinomio.get(1))),paint2);
//            canvas2.drawLine( canvas2.getWidth()*xyz_medio.get(1), canvas2.getHeight()-(canvas2.getHeight()*(polinomio.get(0)*xyz_medio.get(1)+polinomio.get(1)))-offset2 , canvas2.getWidth()*xyz_medio.get(1), canvas2.getHeight()-canvas2.getHeight()*(polinomio.get(0)*xyz_medio.get(1)+polinomio.get(1))+offset2, paint2);

//            canvas2.drawLine( canvas2.getWidth()*xAxisValues.get(0), canvas2.getHeight()-(canvas2.getHeight()*start) , canvas2.getWidth()*xAxisValues.get(xAxisValues.size()-1), canvas2.getHeight()-canvas2.getHeight()*end, paint2);
            paint2.setColor(Color.RED);
            float[] hsvf = new float[3];
            String maximorgb = null;
            int ax = 1;

            for (int i = 0; i < (size - 3); i = i + 3) {
                paint2.setTextSize(30);
//                canvas2.drawText(String.format("%.1f", xyz.get(i)),canvas2.getWidth()*xyz.get(i+1),canvas2.getHeight()-canvas2.getHeight()*xyz.get(i+2),paint2);
                paint2.setStrokeWidth(1);
                paint2.setColor(Color.BLACK);
                canvas2.drawCircle(canvas2.getWidth() * xyz.get(i + 1), canvas2.getHeight() - canvas2.getHeight() * xyz.get(i + 2), 4, paint2);
//                canvas2.drawLine( canvas2.getWidth()*xyz.get(i+1),canvas2.getHeight()-canvas2.getHeight()*xyz.get(i+2),canvas2.getWidth()*xyz.get(i+1),canvas2.getHeight()-canvas2.getHeight()*xyz.get(i+2)+30, paint2);
                paint2.setColor(Color.RED);
                canvas2.drawLine(canvas2.getWidth() * xyz.get(i + 1), canvas2.getHeight() - canvas2.getHeight() * xyz.get(i + 2), canvas2.getWidth() * xyz.get(i + 4), canvas2.getHeight() - canvas2.getHeight() * xyz.get(i + 5), paint2);
                paint2.setTextSize(30);
//                canvas2.drawLine( canvas2.getWidth()*xyz.get(i+4),canvas2.getHeight()-canvas2.getHeight()*xyz.get(i+5) ,canvas2.getWidth()*xyz.get(i+4),canvas2.getHeight()-canvas2.getHeight()*xyz.get(i+5)+30, paint2);
//                canvas2.drawText(String.format("%.1f", xyz.get(i+5)),canvas2.getWidth()*xyz.get(i+4),canvas2.getHeight()-canvas2.getHeight()*xyz.get(i+5),paint2);
                paint2.setColor(Color.BLACK);
                canvas2.drawCircle(canvas2.getWidth() * xyz.get(i + 4), canvas2.getHeight() - canvas2.getHeight() * xyz.get(i + 5), 4, paint2);
//                hsvf= new float[]{mol_hsv.get(i + 5), mol_hsv.get(i + 6), mol_hsv.get(i + 7)};
//                maximorgb=("(" + Float.toString(mol_color_rgb.get(i +4)) + "," + Float.toString(mol_color_rgb.get(i +5))+ "," +Float.toString(mol_color_rgb.get(i +6)) + ")");
                ax = -1;
            }


            //   rbg_mid.setText("(" + Float.toString(r1)+ "," +Float.toString(g1)+ "," +Float.toString(b1)+ ")");

        }
//        try {
//            save(hsv,polinomio,load_data, mol_hsv);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        touch.setImageBitmap(aux2);
        //gradientesv2.setImageBitmap(aux2);
    }

    private ArrayList<Float> traslate_coor(ArrayList<Integer> xyz) {
        ArrayList<Float> traslxyz = new ArrayList<>();
        int size = xyz.size();
        float maximox = 98.254f;
        float minimox = -86.185f;
        float maximoy = 94.482f;
        float minimoy = -107.863f;

        for (int i = 0; i < (size); i = i + 3) {
            traslxyz.add(xyz.get(i) / 100.0f);
            traslxyz.add((xyz.get(i + 1) - minimox) / (maximox - minimox));
            traslxyz.add((xyz.get(i + 2) - minimoy) / (maximoy - minimoy));
        }
        return traslxyz;
    }


}
