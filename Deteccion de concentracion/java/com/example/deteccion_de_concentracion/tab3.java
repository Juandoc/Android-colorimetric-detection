package com.example.deteccion_de_concentracion;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import org.opencv.core.Mat;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class tab3 extends AppCompatActivity {
    private int STORAGE_PERMISSION_CODE = 23;
    Color_concAdapter adapter;
    private static final String filename="example.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab3);


        // Construct the data source
        ArrayList<Color_conc> arrayOfColor_conc = new ArrayList<Color_conc>();
        // Create the adapter to convert the array to views
        adapter = new Color_concAdapter(this, arrayOfColor_conc);
        // Attach the adapter to a ListView
        ListView listView =  findViewById(R.id.shopping_listView);
        listView.setAdapter(adapter);
        Bundle extras = getIntent().getExtras();
        ArrayList<Integer> argb_medio2 = (ArrayList<Integer>) extras.get("argb_medio");
        String message= (String) extras.get("message");
        //distancia euclidia
        ArrayList<Float> dist_euclid= (ArrayList<Float>) extras.get("euclid_dist");
        ArrayList<Float> mol_color_rgb2= (ArrayList<Float>) extras.get(An_concent.RGB_values);
        //Intent intent2 = getIntent();
        //nivel de gris
        ArrayList<Float> grays= (ArrayList<Float>) extras.get("grays");
        //absorvancia

        ArrayList<Float> abs= (ArrayList<Float>) extras.get("abs");
        ArrayList<Float> argb_medio= (ArrayList<Float>) extras.get(An_concent.RGB_val);

        int size=grays.size();

        for (int i=0;i<(size);i=i+1) {
            Color_conc newColor_conc = new Color_conc(String.format("%.2f",mol_color_rgb2.get(i*4)), String.format("%.1f",grays.get(i)), String.format("%.1f",dist_euclid.get(i)), String.format("%.3f",abs.get(i)),2,false,"EST.Cargadas");
            adapter.add(newColor_conc);
        }

        float gray_medio= (float) ((argb_medio.get(0)+argb_medio.get(1)+argb_medio.get(2))/3);
        float dist_e=0;
        float abs2=0;
        try {

            dist_e= (float) Math.sqrt(Math.pow(mol_color_rgb2.get(1)-argb_medio.get(0),2)+Math.pow(mol_color_rgb2.get(2)-argb_medio.get(1),2)+Math.pow(mol_color_rgb2.get(3)-argb_medio.get(2),2));
            abs2= (float) -Math.log10(grays.get(0)/gray_medio);
        }catch (Exception e){
        }
        Color_conc newColor_conc = new Color_conc(String.format("%.2f",argb_medio.get(0)), String.format("%.1f",gray_medio), String.format("%.1f",dist_e), String.format("%.3f",abs2),2,true,"EST.muestra");
        adapter.add(newColor_conc);
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                AlertDialog.Builder adb=new AlertDialog.Builder(tab3.this);
                adb.setTitle("Eliminar?");
                adb.setMessage("Esta seguro de elimnar el elemento " + position);
                adb.setNegativeButton("Cancelar", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Color_conc item= (Color_conc) listView.getItemAtPosition(position);
                        adapter.remove(item);
                        adapter.notifyDataSetChanged();
                        ArrayList<Float> erased=erase_line(mol_color_rgb2,position);
                        ((An_concent)getParent()).load_data(argb_medio2,erased,message,2);

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

}