package com.cxy.doc.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static List<File> findAllFileOnPath(String path){
        List<File> list = new ArrayList<File>();
        File fileDir = new File(path);
        if(fileDir.isDirectory()){
            String[] fileArray = fileDir.list();
            for (int i = 0; i < fileArray.length; i++) {
                File f = new File(path+File.separator+fileArray[i]);
                if(f.isFile()){
                    list.add(f);
                }
            }
        }
        return list ;
    }
}
