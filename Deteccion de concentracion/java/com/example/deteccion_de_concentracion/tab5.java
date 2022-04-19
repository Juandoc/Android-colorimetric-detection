package com.example.deteccion_de_concentracion;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.ArrayList;

public class tab5 extends AppCompatActivity {

    private int STORAGE_PERMISSION_CODE = 23;
    Color_concAdapter adapter;
    private static final String filename="example.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab5);
        // Construct the data source
        ArrayList<Color_conc> arrayOfColor_conc = new ArrayList<Color_conc>();
        // Create the adapter to convert the array to views
        adapter = new Color_concAdapter(this, arrayOfColor_conc);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.shopping_listView);
        listView.setAdapter(adapter);
        Bundle extras = getIntent().getExtras();
        String especs= (String) extras.get("specs");
        try{
        ArrayList<Integer> dime=carga_data_txt(especs);
        if (dime.size()>1) {
            Color_conc newColor_conc = new Color_conc(String.valueOf(dime.get(0)), "X", String.valueOf(dime.get(1)), " Pixeles", 4, true, "Dimen ");
            adapter.add(newColor_conc);
        if (dime.size()>3){
            Color_conc newColor_conc1 = new Color_conc("V_R: "+String.valueOf(dime.get(2)),"V_G: "+String.valueOf(dime.get(3)), "V_B: "+String.valueOf(dime.get(4)), " Pixeles", 4, true, "Varianza. ");
            adapter.add(newColor_conc1);}
        }else {
            Color_conc newColor_conc = new Color_conc("", "X", "", " Pixeles", 4, true, "Dimen ");
            adapter.add(newColor_conc);
        }}
        catch (IOException e){
            e.printStackTrace();
            Log.i("Adapter error","Imagen_error");

        }

    }
    public ArrayList<Integer> carga_data_txt(String info) throws IOException{
        String[] line=info.split("\n");
        ArrayList<Integer>  mol_color= new ArrayList<Integer>();
        try{

            for (int i=0;i<line.length;i++) {
                try{
                    String[] MRGB=line[i].split("/");

                    String item1=MRGB[1];
                    mol_color.add(Integer.parseInt(item1));
                    String item2=MRGB[2];
                    mol_color.add(Integer.parseInt(item2));
                    String item3=MRGB[3];
                    mol_color.add((int) Math.sqrt(Math.abs(Integer.parseInt(item3))));
                    String item4=MRGB[4];
                    mol_color.add((int) Math.sqrt(Math.abs(Integer.parseInt(item4))));
                    String item5=MRGB[5];
                    mol_color.add((int) Math.sqrt(Math.abs(Integer.parseInt(item5))));

                }
                catch (Exception e){
                    Toast.makeText(this,"linea "+String.valueOf(i)+" no leida", Toast.LENGTH_LONG).show();
                }
            }}
        catch (Exception e){

        }

        return mol_color;
    }
}