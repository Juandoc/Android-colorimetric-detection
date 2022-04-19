package com.example.deteccion_de_concentracion;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class tab1 extends AppCompatActivity {

    private int STORAGE_PERMISSION_CODE = 23;
    Color_concAdapter adapter;
    private static final String filename="example.txt";

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab1);


        // Construct the data source
        ArrayList<Color_conc> arrayOfColor_conc = new ArrayList<Color_conc>();
        // Create the adapter to convert the array to views
        adapter = new Color_concAdapter(this, arrayOfColor_conc);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.shopping_listView);
        listView.setAdapter(adapter);
        Bundle extras = getIntent().getExtras();
        ArrayList<Integer> argb_medio2 = (ArrayList<Integer>) extras.get("argb_medio");
        String message= (String) extras.get("message");

        ArrayList<Float> mol_color_rgb2 = (ArrayList<Float>) extras.get(An_concent.RGB_values);
        //Intent intent2 = getIntent();
        ArrayList<Float> argb_medio = (ArrayList<Float>) extras.get(An_concent.RGB_val);

        int size = mol_color_rgb2.size();

        for (int i = 0; i < (size); i = i + 4) {
            Color_conc newColor_conc = new Color_conc(String.format("%.2f", mol_color_rgb2.get(i)), String.format("%.1f", mol_color_rgb2.get(i + 1)), String.format("%.1f", mol_color_rgb2.get(i + 2)), String.format("%.1f", mol_color_rgb2.get(i + 3)), 0, false, "RGB_cargadas");
            adapter.add(newColor_conc);
        }


        Color_conc newColor_conc = new Color_conc(String.format("%.2f", argb_medio.get(0)), Float.toString(argb_medio.get(1)), Float.toString(argb_medio.get(2)), Float.toString(argb_medio.get(3)), 0, true, "RGB_muestra");
        adapter.add(newColor_conc);
        adapter.notifyDataSetChanged();


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                AlertDialog.Builder adb=new AlertDialog.Builder(tab1.this);
                adb.setTitle("Eliminar?");
                adb.setMessage("Esta seguro de elimnar el elemento " + position);
                adb.setNegativeButton("Cancelar", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Color_conc item= (Color_conc) listView.getItemAtPosition(position);
                        adapter.remove(item);
                        adapter.notifyDataSetChanged();
                        ArrayList<Float> erased=erase_line(mol_color_rgb2,position);

                        ((An_concent)getParent()).load_data(argb_medio2,erased,message,0);
                        String data= array_to_str(erased);
                        ((An_concent)getParent()).save(data);
                    }});
                adb.show();
            }
        });
    }
    private ArrayList<Float> erase_line(ArrayList<Float>  load_data,int position){
        ArrayList<Float>  data = new ArrayList<>();
        ArrayList<Float>  lines = load_data;
        for (int i=0;i<lines.size();i++){
            if ((i<position*4) || (i>((position+1)*4-1))){
             data.add(lines.get(i));
            }
        }

        return data ;
    }
    private  String array_to_str(ArrayList<Float>  lines){
        String l_data="";
        for (int i=0;i<lines.size();i=i+4){
            l_data+=String.valueOf(lines.get(i))+"/";
            l_data+=String.valueOf(lines.get(i + 1))+"/";
            l_data+=String.valueOf(lines.get(i + 2))+"/";
            l_data+=String.valueOf(lines.get(i + 3)+"\n");

        }


        return l_data;
    }

}