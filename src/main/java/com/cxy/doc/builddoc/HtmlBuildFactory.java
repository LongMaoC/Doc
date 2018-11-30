package com.cxy.doc.builddoc;

import com.cxy.doc.Config;
import com.cxy.doc.bean.ClassNode;
import com.cxy.doc.bean.MethodNode;

import static com.cxy.doc.util.TextUtils.append;

import java.io.File;
import java.util.List;

public abstract class HtmlBuildFactory extends BuildFactory {


     public HtmlBuildFactory(Config config) {
          super(config);
     }

     public abstract String createStyle();
     public abstract String createScript();



}
