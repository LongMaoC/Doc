package com.cxy.doc.bean;

import java.util.List;

public class MethodNode {
    public List<String> methodDesc ;
    public String methodUrl ;
    public List<String> methodType ;
    public String requestType ;
    public List<ParameNote> parameNoteList ;
    public List<ErrorNode> errorNoteList ;
    public Version version;

    @Override
    public String toString() {
        return "MethodNode{" +
                "methodDesc=" + methodDesc +
                ", methodUrl='" + methodUrl + '\'' +
                ", requestType='" + requestType + '\'' +
                ", errorNoteList='" + errorNoteList + '\'' +
                ", parameNoteList=" + parameNoteList +
                '}';
    }
}
