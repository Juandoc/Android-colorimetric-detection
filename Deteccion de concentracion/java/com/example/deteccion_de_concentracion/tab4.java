package com.example.deteccion_de_concentracion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class tab4 extends AppCompatActivity {
    private int STORAGE_PERMISSION_CODE = 23;
    Color_concAdapter adapter;
    private static final String filename="example.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab4);

        // Construct the data source
        ArrayList<Color_conc> arrayOfColor_conc = new ArrayList<Color_conc>();
        // Create the adapter to convert the array to views
        adapter = new Color_concAdapter(this, arrayOfColor_conc);
        // Attach the adapter to a ListView
        ListView listView = (ListView) findViewById(R.id.shopping_listView);
        listView.setAdapter(adapter);
        Bundle extras = getIntent().getExtras();

        ArrayList<Float> argb_medio= (ArrayList<Float>) extras.get(An_concent.RGB_val);
        int gray_medio= (int) ((argb_medio.get(0)+argb_medio.get(1)+argb_medio.get(2))/3);
        try{
        ArrayList<Float> polinomio= (ArrayList<Float>) extras.get("Pend_R");
        ArrayList<Float> polinomio2= (ArrayList<Float>) extras.get("Pend_R_gray");
        ArrayList<Float> polinomio3= (ArrayList<Float>) extras.get("Pend_R_vector");
        ArrayList<Float> polinomio4= (ArrayList<Float>) extras.get("Pend_R_abs");
        ArrayList<Float> mol_color_rgb2= (ArrayList<Float>) extras.get(An_concent.RGB_values);
        float gray_nulo= (float) extras.get("gray_nulo");


        float abs2= (float) -Math.log10(gray_nulo/gray_medio);
        int dist_e= (int) Math.sqrt(Math.pow(mol_color_rgb2.get(1)-argb_medio.get(0),2)+Math.pow(mol_color_rgb2.get(2)-argb_medio.get(1),2)+Math.pow(mol_color_rgb2.get(3)-argb_medio.get(2),2));
        Color_conc newColor_conc = new Color_conc(String.format("%.2f",argb_medio.get(0)), String.format("%.2f",polinomio.get(0)), String.format("%.1f",polinomio.get(1)), String.format("%.1f",polinomio.get(2)),3,true,"Sat ");
        adapter.add(newColor_conc);
        Color_conc newColor_conc1 = new Color_conc(String.format("%.2f",polinomio2.get(0)*gray_medio+polinomio2.get(1)), String.format("%.2f",polinomio2.get(0)), String.format("%.1f",polinomio2.get(1)), String.format("%.1f",polinomio2.get(2)),3,true,"Gray ");
        adapter.add(newColor_conc1);
        Color_conc newColor_conc2 = new Color_conc(String.format("%.2f",polinomio3.get(0)*dist_e+polinomio3.get(1)), String.format("%.2f",polinomio3.get(0)), String.format("%.1f",polinomio3.get(1)), String.format("%.1f",polinomio3.get(2)),3,true,"Dist ");
        adapter.add(newColor_conc2);
        Color_conc newColor_conc3 = new Color_conc(String.format("%.2f",polinomio4.get(0)*abs2+polinomio4.get(1)), String.format("%.2f",polinomio4.get(0)), String.format("%.1f",polinomio4.get(1)), String.format("%.1f",polinomio4.get(2)),3,true,"Abs ");
        adapter.add(newColor_conc3);}catch (Exception e){

            Color_conc newColor_conc = new Color_conc(String.format("%.2f",argb_medio.get(0)), "No data", "No data", "No data",3,false,"Sat ");
            adapter.add(newColor_conc);
            Color_conc newColor_conc1 = new Color_conc("No data", "No data", "No data", "No data",3,false,"Gray ");
            adapter.add(newColor_conc1);
            Color_conc newColor_conc2 = new Color_conc("No data", "No data","No data", "No data",3,false,"Dist ");
            adapter.add(newColor_conc2);
            Color_conc newColor_conc3 = new Color_conc("No data", "No data", "No data","No data",3,false,"Abs ");

        }
    }
}
