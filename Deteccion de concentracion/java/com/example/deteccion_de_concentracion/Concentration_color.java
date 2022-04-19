package com.example.deteccion_de_concentracion;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Concentration_color extends AppCompatActivity {

    private ListView mShoppingList;
    private EditText mItemEdit;
    private EditText mItemR;
    private EditText mItemG;
    private EditText mItemB;
    private Button mAddButton;
    private Button mClearButton;
    private Button exit;
    private int STORAGE_PERMISSION_CODE = 23;
    String Load_data="";
    Color_concAdapter adapter;
    private static final String filename="Lista_concentraciones_RGB.txt";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.carga_conc);

        mShoppingList = (ListView) findViewById(R.id.shopping_listView);
        mItemEdit = (EditText) findViewById(R.id.item_editText);
        mItemR= (EditText) findViewById(R.id.item_R);
        mItemG= (EditText) findViewById(R.id.item_B);
        mItemB= (EditText) findViewById(R.id.item_G);
        mAddButton = (Button) findViewById(R.id.add_button);
        mClearButton= (Button) findViewById(R.id.elim);
        exit=(Button) findViewById(R.id.salir);
        ArrayList<Integer> argb_medio = new ArrayList<Integer>();


        // Construct the data source
        ArrayList<Color_conc> arrayOfColor_conc = new ArrayList<Color_conc>();
        // Create the adapter to convert the array to views
        adapter = new Color_concAdapter(this, arrayOfColor_conc);
        // Attach the adapter to a ListView
        mShoppingList.setAdapter(adapter);

        Intent intent = getIntent();
        Load_data=intent.getStringExtra(MainActivity.EXTRA_MESSAGE3);
        argb_medio=(intent.getIntegerArrayListExtra(MainActivity.EXTRA_MESSAGE4));
        if (argb_medio!=null) {
            if (argb_medio.size() > 2) {
                mItemR.setText(String.valueOf(argb_medio.get(1)));
                mItemB.setText(String.valueOf(argb_medio.get(2)));
                mItemG.setText(String.valueOf(argb_medio.get(3)));
            }
        }


        //save(Load_data);
        save_clear();
        if (Load_data!="")
        {
            carga_data_txt(Load_data);}





        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = mItemEdit.getText().toString();
                String item2 = mItemR.getText().toString();
                String item3 = mItemB.getText().toString();
                String item4 = mItemG.getText().toString();
                mItemEdit.setText("");
                mItemR.setText("");
                mItemB.setText("");
                mItemG.setText("");


                Color_conc newColor_conc = new Color_conc(item, item2,item3,item4,0,false,"Muestra a añadir ");
                adapter.add(newColor_conc);

            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    int size= adapter.getCount();
                    for (int i=0;i<size;i++){
                    String text= (String) adapter.getItem(i).conc+"/"+adapter.getItem(i).R_color+"/"+adapter.getItem(i).B_color+"/"+adapter.getItem(i).G_color+"\n";
                    save(text);
                }
                finish();

               // v.setVisibility(View.GONE);
            }
        });
        mClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i=adapter.getCount();
                if (!adapter.isEmpty()) {
                    adapter.remove(adapter.getItem(adapter.getCount() - 1));
                    i=adapter.getCount();

                }

            }
        });

    }
    public void save_clear(){
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

                    fos = new FileOutputStream(textfile, true);
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
            Log.i("state","se puede escribir");
            //Toast.makeText(this,"Writeable", Toast.LENGTH_LONG).show();
            return true;
        }else{
            return false;
        }
    }
    public void carga_data_txt(String info){
        String[] line=info.split("\n");
        try{

        for (int i=0;i<line.length;i++) {
            try{
            String[] MRGB=line[i].split("/");
            String item1=MRGB[0];
            String item2=MRGB[1];
            String item3=MRGB[2];
            String item4=MRGB[3];
            Color_conc newColor_conc = new Color_conc(item1, item2, item3, item4,0,false,String.valueOf(i));
            adapter.add(newColor_conc);}
            catch (Exception e){
                Toast.makeText(this,"Linea "+String.valueOf(i)+" no leida", Toast.LENGTH_LONG).show();
            }
        }}
        catch (Exception e){
            Toast.makeText(this,"Archivo no trasladado", Toast.LENGTH_LONG).show();

        }
    }
}