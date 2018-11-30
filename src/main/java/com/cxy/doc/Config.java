package com.cxy.doc;

import com.cxy.doc.bean.Version;
import com.cxy.doc.util.L;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Config {
    private String suffix = "";
    private String prefix = "";

    private String controllerPath = "";
    private String docOutPath = "";
    private List<Version> versionList;

    public Version getVersion(Float key) {
        if (versionList != null) {
            for (int i = 0; i < versionList.size(); i++) {
                Version v = versionList.get(i);
                if (Math.abs(v.key - key) == 0) {
                    return v;
                }
            }
        }
        return null;
    }

    public boolean isNew(Float key) {
        if (versionList != null) {
            Version v = versionList.get(versionList.size() - 1);
            if (Math.abs(v.key - key) == 0) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }


    private Config(String suffix,  String controllerPath, String docOutPath, List<Version> versionList,String prefix) {
        this.suffix = suffix;
        this.controllerPath = controllerPath;
        this.docOutPath = docOutPath;
        this.versionList = versionList;
        this.prefix=prefix;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getControllerPath() {
        return controllerPath;
    }

    public String getDocOutPath() {
        return docOutPath;
    }

    public List<Version> getVersionList() {
        return versionList;
    }

    public static class ConfigBuilder {
        private String suffix = "";
        private String prefix = "";

        private String controllerPath = "";
        private String docOutPath = "";
        private List<Version> versionList = null;

        public ConfigBuilder suffix(String suffix) {
            if (suffix != null) {
                if (suffix.startsWith(".")) {
                    throw new RuntimeException("[suffix]属性不可以以[.]开头");
                }
            }
            this.suffix = suffix;
            return this;
        }

        public ConfigBuilder prefix(String prefix) {
            if (prefix== null ) {
                prefix="";
            }
            if(prefix.length()>0 && (prefix.endsWith("/") || prefix.endsWith("\\"))){
                prefix = prefix.substring(0,prefix.length()-1);
            }
            this.prefix = prefix;
            return this;
        }



        public ConfigBuilder controllerPath(String controllerPath) {
            if (controllerPath == null || controllerPath.equals("")) {
                throw new RuntimeException("controller 路径不能为空");
            }
            this.controllerPath = controllerPath;
            return this;
        }

        public ConfigBuilder docOutPath(String docOutPath) {

            if (docOutPath == null || docOutPath.equals("")) {
                if (docOutPath != null && docOutPath.indexOf(File.separator + "src" + File.separator) != -1) {
                    String s = docOutPath.substring(0, docOutPath.indexOf(File.separator + "src"));
                    docOutPath = s + File.separator + "接口文档";
                }
            }

            docOutPath = docOutPath + File.separator + "接口文档";
            this.docOutPath = docOutPath;
            return this;
        }

        public Config build() {
            if (versionList == null || versionList.size() == 0) {
                versionList = new ArrayList<Version>() {
                    {
                        add(new Version(0.0f, "默认版本"));
                    }
                };
            }
            return new Config(suffix,  controllerPath, docOutPath, versionList,prefix);
        }

        public ConfigBuilder versionList(List<Version> versionList) {
            versionList.add(new Version(0.0f, "默认版本"));
            Collections.sort(versionList);
            this.versionList = versionList;
            return this;
        }
    }
}
