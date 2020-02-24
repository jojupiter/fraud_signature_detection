package com.example.fofe.fraud_detection;

public class Recognition {
    String name;
    float probability;

    public Recognition(String name, float probability) {
        this.name = name;
        this.probability = probability;
    }

    public String Resutat(){
        String res = ""+name+"  : "+probability*100+" %";
        return res;
    }
}
