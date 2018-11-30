package com.cxy.doc.util;

/**
 * Created by cxy on 17-6-29.
 */
public class TextUtils {
    public static boolean isEntry(String str) {
        return str != null && !str.equals("") ? false : true;
    }

    public static void append(int tabNum,String content,int enterNum,StringBuilder sb){
        for (int i = 0; i < tabNum; i++) {
            sb.append("\t");
        }
        sb.append(content);
        for (int i = 0; i < enterNum; i++) {
            sb.append("\n");
        }
    }

    public static void append(int tabNum,String content,StringBuilder sb){
        append(tabNum,content,1,sb);
    }
}
