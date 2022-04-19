package com.example.deteccion_de_concentracion;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTabHost;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class An_concent extends TabActivity {

        public static final String RGB_values = "RGB_values";
        public static final String RGB_val = "RGB_val";
        public static final String HSV_values = "HSV_values";
        public static final String euclid_dist = "Euclid_dist";
        public static final String grays = "grays";
        public static final String Pend_R = " Pend_R";
        private static final String filename="Lista_concentraciones_RGB.txt";
        private int STORAGE_PERMISSION_CODE = 23;


        @Override
        public void onCreate( Bundle savedInstanceState) {
                String Load_data="";
                super.onCreate(savedInstanceState);
                setContentView(R.layout.an_concent);
                ArrayList<Integer> argb_medio = new ArrayList<Integer>();
                Intent intent = getIntent();
                String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);//media y desvio "Media"+String.valueOf(Math.round(mediaA))+" Desvio"+String.valueOf(Math.round(da)

                Load_data = intent.getStringExtra(MainActivity.EXTRA_MESSAGE3);
                argb_medio=(intent.getIntegerArrayListExtra(MainActivity.EXTRA_MESSAGE4));


                ArrayList<Float>  mol_color_rgb0=carga_data_txt(Load_data);
                ArrayList<Float>  mol_color_rgb1=ordenar_lista(mol_color_rgb0);
                int pos=0;
                load_data(argb_medio,mol_color_rgb1,message,pos);
        }
        public void  load_data(ArrayList<Integer> argb_medio,ArrayList<Float>  mol_rgb2,String message,int pos){

                if (argb_medio.size() <2){
                        argb_medio.add(0);
                        argb_medio.add(33);
                        argb_medio.add(166);
                        argb_medio.add(166);
                        argb_medio.add(0);
                }
                ArrayList<Float> mol_rgb= new ArrayList<Float>();
                mol_rgb=mol_rgb2;

                ArrayList<Float> mol_hsv=convert_hsv(mol_rgb);

                ArrayList<Float> euclid_dis=euclid_dist(mol_rgb);
                ArrayList<Float> grays=grayscale(mol_rgb);
                ArrayList<Float> argb_medio2= new ArrayList<Float>();
                ArrayList<Float> absorbancia=absorbancia(grays);
                ArrayList<Float> polinomio= new ArrayList<Float>();
                ArrayList<Float> polinomio2= new ArrayList<Float>();
                ArrayList<Float> polinomio3= new ArrayList<Float>();
                ArrayList<Float> polinomio4= new ArrayList<Float>();
                try{
                        //absorbancia=absorbancia(mol_rgb);
                        polinomio= trendline(mol_hsv);
                        polinomio2= trendline2(mol_hsv,grays);
                        polinomio3= trendline2(mol_hsv,euclid_dis);
                        polinomio4= trendline2(mol_hsv,absorbancia);
                        float[] hsv = new float[3];
                        Color.RGBToHSV(argb_medio.get(1),argb_medio.get(2),argb_medio.get(3), hsv);

                        argb_medio2.add((polinomio.get(0)*hsv[1]+polinomio.get(1)));
                        argb_medio2.add(Float.valueOf(argb_medio.get(1)));
                        argb_medio2.add(Float.valueOf(argb_medio.get(2)));
                        argb_medio2.add(Float.valueOf(argb_medio.get(3)));
                }catch (Exception e){
                        argb_medio2.add((float) 0.0);
                        argb_medio2.add(Float.valueOf(argb_medio.get(1)));
                        argb_medio2.add(Float.valueOf(argb_medio.get(2)));
                        argb_medio2.add(Float.valueOf(argb_medio.get(3)));

                        Toast.makeText(this,"datos no calculados", Toast.LENGTH_LONG).show();

                }

                TabHost tabHost = findViewById(android.R.id.tabhost);
                tabHost.clearAllTabs();
                tabHost.clearFocus();



                TabHost.TabSpec spec; // Reusable TabSpec for each tab

                Intent intent2; // Reusable Intent for each tab


                // Create an Intent to launch an Activity for the tab (to be reused)
                intent2 = new Intent(this, tab1.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent2.removeExtra(RGB_values);


                spec = tabHost.newTabSpec("RGB"); // Create a new TabSpec using tab host
                spec.setIndicator("RGB"); // set the “HOME” as an indicator
                intent2.putExtra("argb_medio",argb_medio);
                intent2.putExtra("message",message);

                intent2.putExtra(RGB_values,mol_rgb);
                intent2.putExtra(RGB_val,argb_medio2);
                //intent2.putExtra("RGB_val",argb_medio2);
                spec.setContent(intent2);
                tabHost.addTab(spec);



                // Do the same for the other tabs
                Intent intent3; // Reusable Intent for each tab

                intent3 = new Intent(this, tab2.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                spec = tabHost.newTabSpec("HSV"); // Create a new TabSpec using tab host
                spec.setIndicator("HSV"); // set the “CONTACT” as an indicator
                // Create an Intent to launch an Activity for the tab (to be reused)


                intent3.removeExtra(RGB_values);
                intent3.putExtra(HSV_values,mol_hsv);
                intent3.putExtra(RGB_val,argb_medio2);
                intent3.putExtra(RGB_values,mol_rgb);
                intent3.putExtra("argb_medio",argb_medio);
                intent3.putExtra("message",message);
                spec.setContent(intent3);
                tabHost.addTab(spec);


                Intent intent4; // Reusable Intent for each tab

                intent4 = new Intent(this, tab3.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                spec = tabHost.newTabSpec("|v|gray|abs"); // Create a new TabSpec using tab host
                spec.setIndicator("|v|gray|abs"); // set the “ABOUT” as an indicator
                // Create an Intent to launch an Activity for the tab (to be reused)
                intent4.removeExtra(RGB_values);
                intent4.putExtra(RGB_values,mol_rgb);
                intent4.putExtra(RGB_val,argb_medio2);
                intent4.putExtra("euclid_dist",euclid_dis);
                intent4.putExtra("grays",grays);
                intent4.putExtra("abs",absorbancia);
                intent4.putExtra("argb_medio",argb_medio);
                intent4.putExtra("message",message);

                spec.setContent(intent4);
                tabHost.addTab(spec);


                Intent intent5; // Reusable Intent for each tab

                intent5 = new Intent(this, tab4.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                spec = tabHost.newTabSpec("Results:"); // Create a new TabSpec using tab host
                spec.setIndicator("Results:"); // set the “ABOUT” as an indicator
                // Create an Intent to launch an Activity for the tab (to be reused)
                intent5.putExtra("Pend_R", polinomio);
                intent5.putExtra("Pend_R_gray", polinomio2);
                intent5.putExtra("Pend_R_vector", polinomio3);
                intent5.putExtra("Pend_R_abs", polinomio4);
                intent5.putExtra("RGB_val",argb_medio2);
                intent5.putExtra(RGB_values,mol_rgb);
                intent5.putExtra("gray_nulo",argb_medio2.get(0));
                spec.setContent(intent5);
                tabHost.addTab(spec);


                Intent intent6; // Reusable Intent for each tab


                intent6 = new Intent(this, tab5.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                spec = tabHost.newTabSpec("Otros"); // Create a new TabSpec using tab host
                spec.setIndicator("Otros"); // set the “ABOUT” as an indicator

                // Create an Intent to launch an Activity for the tab (to be reused)
                intent6.putExtra("argb_medio",argb_medio);
                intent6.putExtra("specs",message);
                spec.setContent(intent6);
                tabHost.addTab(spec);


                Intent intent7; // Reusable Intent for each tab


                intent7 = new Intent(this, tab6.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                spec = tabHost.newTabSpec("CIE"); // Create a new TabSpec using tab host
                spec.setIndicator("CIE"); // set the “ABOUT” as an indicator
                // Create an Intent to launch an Activity for the tab (to be reused)

                intent7.putExtra(RGB_values,mol_rgb);
                intent7.putExtra(RGB_val,argb_medio2);
                intent7.putExtra("argb_medio",argb_medio);
                spec.setContent(intent7);
                tabHost.addTab(spec);

                //set tab which one you want to open first time 0 or 1 or 2
                tabHost.setCurrentTab(pos);
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
        private ArrayList<Float> trendline(ArrayList<Float> mol_hsv)  {
                ArrayList<Float> xAxisValues = new ArrayList<>();
                ArrayList<Float> yAxisValues= new ArrayList<>();
                float xAxisValueSum=0;
                float yAxisValueSum=0;
                float xxsum=0;
                float xysum=0;
                float yysum=0;
                int size=mol_hsv.size();
                int count=size/4;
                for (int i=0;i<(size);i=i+4){
                        yAxisValues.add(mol_hsv.get(i));
                        yAxisValueSum+=mol_hsv.get(i);
                        xAxisValueSum+=mol_hsv.get(i+2);
                        xAxisValues.add(mol_hsv.get(i+2));
                        xxsum+=mol_hsv.get(i+2)*mol_hsv.get(i+2);
                        xysum+=mol_hsv.get(i)*mol_hsv.get(i+2);
                        yysum+=mol_hsv.get(i)*mol_hsv.get(i);

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
//        private ArrayList<Float> absorbancia(ArrayList<Float>  mol_color_rgb){
//                ArrayList<Float> abs = new ArrayList<>();
//                int size=mol_color_rgb.size();
//                Float r_color,g_color,b_color;
//                Float r_base=mol_color_rgb.get(0);
//                Float g_base=mol_color_rgb.get(1);
//                Float b_base=mol_color_rgb.get(2);
//                abs.add((float)  0);
//
//                for (int i=4;i<size;i=i+4){
//                        r_color=mol_color_rgb.get(i+1);
//                        g_color=mol_color_rgb.get(i+2);
//                        b_color=mol_color_rgb.get(i+3);
//                        abs.add((float) -Math.log10((r_base+g_base+b_base)/(r_color+g_color+b_color)));
//                }
//
//
//                return abs ;
//        }
        private ArrayList<Float> absorbancia(ArrayList<Float>  grays){
                ArrayList<Float> abs = new ArrayList<>();
                int size=grays.size();
                Float g_base = Float.valueOf(0);
                if (size!=0) {
                        g_base = grays.get(0);
                }

                for (int i=0;i<size;i=i+1){
                        abs.add((float) -Math.log10((grays.get(i))/(g_base)));
                }


                return abs ;
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
        private ArrayList<Float> euclid_dist(ArrayList<Float>  mol_color_rgb){
                ArrayList<Float> euclid_dis = new ArrayList<>();
                int size=mol_color_rgb.size();
                double r_color;
                double g_color;
                double b_color;
                euclid_dis.add((float)  0);

                for (int i=4;i<size;i=i+4){
                        r_color=Math.pow(mol_color_rgb.get(1)-mol_color_rgb.get(i+1),2);
                        g_color=Math.pow(mol_color_rgb.get(2)-mol_color_rgb.get(i+2),2);
                        b_color=Math.pow(mol_color_rgb.get(3)-mol_color_rgb.get(i+3),2);
                        euclid_dis.add((float) Math.sqrt(r_color+g_color+b_color));
                }


                return euclid_dis;
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
        private ArrayList<Float> convert_hsv(ArrayList<Float> mol_color_rgb){
                ArrayList<Float> mol_hsv = new ArrayList<>();
                int size=mol_color_rgb.size();
                Float r_color,g_color,b_color;

                for (int i=0;i<size;i=i+4){
                        float[] hsv = new float[3];
                        r_color=mol_color_rgb.get(i+1);
                        g_color=mol_color_rgb.get(i+3);
                        b_color=mol_color_rgb.get(i+2);
                        mol_hsv.add( Float.valueOf(mol_color_rgb.get(i)));


                        Color.RGBToHSV(Math.round(r_color),Math.round(g_color),Math.round(b_color), hsv);
                        mol_hsv.add(  hsv[0]);
                        mol_hsv.add(hsv[1]);
                        mol_hsv.add(  hsv[2]);
                }



                return mol_hsv;
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

        public void save(String text){
                //String text= (String) adapter.getItem(size-1).conc+"/"+adapter.getItem(size-1).R_color+"/"+adapter.getItem(size-1).B_color+"/"+adapter.getItem(size-1).G_color+"\n";
                if (text.length()>1){
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        if (iswriteable() ){
                                File textfile=new File(getFilesDir().toString()+"/rgbtest/"+filename);
                                String namedir=getFilesDir().toString()+"/rgbtest";
                                File myDir=new File(namedir);
                                if (!myDir.exists()) {
                                        myDir.mkdirs();
                                }
                                FileOutputStream fos= null;

                                try {

                                        fos = new FileOutputStream(textfile, false);
                                        fos.write(text.getBytes());
                                        Toast.makeText(this,"Archivo guardado "+textfile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                                        fos.flush();
                                        fos.close();
                                } catch (IOException e){
                                        Toast.makeText(this,"Error guardando "+textfile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                                        e.printStackTrace();
                                }
                        }else{
                                Toast.makeText(this,"No se pudo guardar en almacenamiento externo", Toast.LENGTH_LONG).show();

                        }
                }else{
                        Toast.makeText(this,"No hay datos a guardar", Toast.LENGTH_LONG).show();
                }
        }
        private boolean iswriteable(){
                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
                        Log.i("state","yes is writeable");
                        //Toast.makeText(this,"Writeable", Toast.LENGTH_LONG).show();
                        return true;
                }else{
                        return false;
                }
        }

}