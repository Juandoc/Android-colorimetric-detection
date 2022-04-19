package com.example.deteccion_de_concentracion;

public class Color_conc {
    public String conc;
    public String R_color;
    public String B_color;
    public String G_color;
    public int op;
    public Boolean last;
    public String name;

    public Color_conc(String concentracion, String R_color,String B_color,String G_color,int op,Boolean last,String name) {
        this.conc = concentracion ;
        this.R_color = R_color ;
        this.B_color = B_color;
        this.G_color = G_color;
        this.op=op;
        this.last=last;
        this.name= name;
    }

}
