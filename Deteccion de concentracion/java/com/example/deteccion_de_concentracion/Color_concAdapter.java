package com.example.deteccion_de_concentracion;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class Color_concAdapter extends ArrayAdapter<com.example.deteccion_de_concentracion.Color_conc> {
    public Color_concAdapter(Context context, ArrayList<com.example.deteccion_de_concentracion.Color_conc>colors) {
        super(context, 0, colors);
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        com.example.deteccion_de_concentracion.Color_conc Color_concentration = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_row, parent, false);
        }
        // Lookup view for data population
        TextView color_Edit = (TextView) convertView.findViewById(R.id.Color_Conc);
        TextView concentacion = (TextView) convertView.findViewById(R.id.Concentracion);
        TextView R_color = (TextView) convertView.findViewById(R.id.R_color);
        TextView B_color = (TextView) convertView.findViewById(R.id.B_color);
        TextView G_color = (TextView) convertView.findViewById(R.id.G_color);
        if (Color_concentration.last){
            concentacion.setTextColor(Color.BLUE);
            R_color.setTextColor(Color.BLUE);
            B_color.setTextColor(Color.BLUE);
            G_color.setTextColor(Color.BLUE);
        }


        // Populate the data into the template view using the data object
        concentacion.setText(Color_concentration.conc);
        R_color.setText(Color_concentration.R_color);
        B_color.setText(Color_concentration.B_color);
        G_color.setText(Color_concentration.G_color);


        if (Color_concentration.op==0){

            float r= Float.parseFloat(Color_concentration.R_color);
            float g=  Float.parseFloat(Color_concentration.G_color);
            float b= Float.parseFloat(Color_concentration.B_color);
            color_Edit.setBackgroundColor(Color.rgb( (int) r,(int) g,(int) b));
            color_Edit.setText("            ");
        }else if (Color_concentration.op==1) {

            float r=  Float.parseFloat(Color_concentration.R_color);
            float g=  Float.parseFloat(Color_concentration.G_color);
            float b=  Float.parseFloat(Color_concentration.B_color);
            color_Edit.setText("            ");
            TextView name_1 = (TextView) convertView.findViewById(R.id.item_R_color);
            name_1.setText("|H:");
            TextView name_2 = (TextView) convertView.findViewById(R.id.item_B_color);
            name_2.setText("S:");
            TextView name_3 = (TextView) convertView.findViewById(R.id.item_G_color);
            name_3.setText("V:");
            float[] hsv = new float[3];
            int rgb ;
            hsv[0]=r;
            hsv[1]=b;
            hsv[2]=g;
            rgb=Color.HSVToColor(hsv);
            color_Edit.setBackgroundColor(rgb);
        }else if (Color_concentration.op==2){

            float r=  Float.parseFloat(Color_concentration.R_color);
            float g=  Float.parseFloat(Color_concentration.G_color);
            float b=  Float.parseFloat(Color_concentration.B_color);
            float[] hsv = new float[3];
            hsv[0]=0;
            hsv[1]=0;
            hsv[2]=r/255;
            color_Edit.setText("            ");
            color_Edit.setBackgroundColor(Color.HSVToColor(hsv));

            TextView name_1 = (TextView) convertView.findViewById(R.id.item_R_color);
            name_1.setText("|G:");
            TextView name_2 = (TextView) convertView.findViewById(R.id.item_B_color);
            name_2.setText("|||C||:");
            TextView name_3= (TextView) convertView.findViewById(R.id.item_G_color);
            name_3.setText("|abs:");

        }else if (Color_concentration.op==3){
            if (Color_concentration.last){
                concentacion.setTextColor(Color.BLUE);
                R_color.setTextColor(Color.GRAY);
                B_color.setTextColor(Color.GRAY);
                G_color.setTextColor(Color.GRAY);
            }

            color_Edit.setTextSize(18);
            color_Edit.setText(Color_concentration.name);
            color_Edit.setBackgroundColor(Color.rgb(200,200,200));
            TextView name_1 = (TextView) convertView.findViewById(R.id.item_R_color);
            name_1.setText("|a:");
            TextView name_2 = (TextView) convertView.findViewById(R.id.item_B_color);
            name_2.setText("|b:");
            TextView name_3= (TextView) convertView.findViewById(R.id.item_G_color);
            name_3.setText("|r:");

        }else if (Color_concentration.op==4){
            color_Edit.setTextSize(18);
            color_Edit.setText(Color_concentration.name);
            TextView name_0 = (TextView) convertView.findViewById(R.id.Concentracionmoles);
            name_0.setText("");
            TextView name_1 = (TextView) convertView.findViewById(R.id.item_R_color);
            name_1.setText("");
            TextView name_2 = (TextView) convertView.findViewById(R.id.item_B_color);
            name_2.setText("");
            TextView name_3= (TextView) convertView.findViewById(R.id.item_G_color);
            name_3.setText("");

        }else if (Color_concentration.op==5){
            color_Edit.setTextSize(18);
            color_Edit.setText(Color_concentration.name);
//            TextView name_0 = (TextView) convertView.findViewById(R.id.Concentracionmoles);
//            name_0.setText("");
            TextView name_1 = (TextView) convertView.findViewById(R.id.item_R_color);
            name_1.setText("|L");
            TextView name_2 = (TextView) convertView.findViewById(R.id.item_B_color);
            name_2.setText("|a");
            TextView name_3= (TextView) convertView.findViewById(R.id.item_G_color);
            name_3.setText("|b");









        }

        // Return the completed view to render on screen
        return convertView;
    }
}