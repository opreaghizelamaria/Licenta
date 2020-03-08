package com.example.opreaghizelamaria.licenta;

public class Data {
    public String time;
    public String x,y,z,G,db,speed;
    public Data(){

    }
    public Data(String time, String x, String y, String z, String G, String  db, String speed) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.db=db;
        this.speed=speed;
        this.time=time;
        this.G = G;
    }

}
