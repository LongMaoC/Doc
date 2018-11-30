package com.cxy.doc.bean;

public class ParameNote {
    public String parameterName ;
    public String parameterDesc ;
    public String parameterType ;

    @Override
    public String toString() {
        return "ParameNote{" +
                "parameterName='" + parameterName + '\'' +
                ", parameterDesc='" + parameterDesc + '\'' +
                ", parameterType='" + parameterType + '\'' +
                '}';
    }

}
