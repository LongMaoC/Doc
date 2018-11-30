package com.cxy.doc.builddoc;

import com.cxy.doc.Config;
import com.cxy.doc.bean.ClassNode;
import com.cxy.doc.bean.MethodNode;
import com.cxy.doc.util.IOUtils;

import java.io.File;
import java.util.List;

public abstract class BuildFactory {
     Config config;

    public static final String SUFFIX_HTML = "html";
    public static final String SUFFIX_MD = "md";

    public abstract String createIndex(List<ClassNode> classNodeList);

    public abstract String getFileName();

    public abstract String createControllerContext(ClassNode classNode);

    public BuildFactory(Config config) {
        this.config = config;
    }
}
