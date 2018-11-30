package com.cxy.doc.bean;

import java.util.List;

public class ClassNode {
    public List<String> classDesc ;
    public String classUrl ;
    public List<MethodNode> methodNodeList ;

    @Override
    public String toString() {
        return "ClassNode{" +
                "classDesc='" + classDesc + '\'' +
                ", classUrl='" + classUrl + '\'' +
                ", methodNodeList=" + methodNodeList +
                '}';
    }
}
