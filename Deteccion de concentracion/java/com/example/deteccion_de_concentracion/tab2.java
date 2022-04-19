package com.example.deteccion_de_concentracion;

import android.content.DialogInterface;
import android.graphics.Color;
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

import java.util.ArrayList;

public class tab2 extends AppCompatActivity {
    private int STORAGE_PERMISSION_CODE = 23;
    Color_concAdapter adapter;
    private static final String filename="example.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab2);


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
        ArrayList<Float> mol_hsv= (ArrayList<Float>) extras.get(An_concent.HSV_values);
        ArrayList<Float> mol_color_rgb2 = (ArrayList<Float>) extras.get(An_concent.RGB_values);
        //Intent intent2 = getIntent();
        ArrayList<Float> argb_medio= (ArrayList<Float>) extras.get(An_concent.RGB_val);
        float[] hsv = new float[3];

        int size=mol_hsv.size();

        for (int i=0;i<(size);i=i+4) {
            Color_conc newColor_conc = new Color_conc(String.format("%.2f",mol_hsv.get(i)), String.format("%.1f",mol_hsv.get(i+1)), String.format("%.1f",mol_hsv.get(i+2)), String.format("%.1f",mol_hsv.get(i+3)),1,false,"HSV_cargadas");
            adapter.add(newColor_conc);
        }

        Color.RGBToHSV(Math.round(argb_medio.get(1)),Math.round(argb_medio.get(3)),Math.round(argb_medio.get(2)), hsv);
        Color_conc newColor_conc = new Color_conc(String.format("%.2f",argb_medio.get(0)),String.format("%.1f",hsv[0]),String.format("%.1f",hsv[1]),String.format("%.1f",hsv[2]),1,true,"HSV_muestra");
        adapter.add(newColor_conc);
        adapter.notifyDataSetChanged();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                AlertDialog.Builder adb=new AlertDialog.Builder(tab2.this);
                adb.setTitle("Eliminar?");
                adb.setMessage("Esta seguro de elimnar el elemento " + position);
                adb.setNegativeButton("Cancelar", null);
                adb.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Color_conc item= (Color_conc) listView.getItemAtPosition(position);
                        adapter.remove(item);
                        adapter.notifyDataSetChanged();
                        ArrayList<Float> erased=erase_line(mol_color_rgb2,position);
                        ((An_concent)getParent()).load_data(argb_medio2,erased,message,1);

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