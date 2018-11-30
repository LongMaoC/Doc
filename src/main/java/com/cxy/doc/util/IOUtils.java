package com.cxy.doc.util;


import okio.*;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IOUtils {
    public static final String DEFAULT_REG = "\\$\\{(.*?)\\}";

    public static String readUTF8(String filePath) {
        File file = new File(filePath);
        if (file.isFile()) {
            Source source = null;
            BufferedSource bufferedSource = null;
            try {
                source = Okio.source(file);
                bufferedSource = Okio.buffer(source);
                return bufferedSource.readUtf8();
            } catch (Exception e) {
                L.e(e.toString());
                return "";
            } finally {
                try {
                    if (bufferedSource != null) bufferedSource.close();
                    if (source != null) source.close();
                } catch (Exception e) {
                }
            }
        }
        return "";
    }

    public static boolean writeUTF8(String fileContent, String fileName, String outFilePath) {
//        L.e(outFilePath + File.separator + fileName);

        File outDir = new File(outFilePath);
        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        Sink sink = null;
        BufferedSink bufferedSink = null;
        try {
            File file = new File(outFilePath + File.separator + fileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            sink = Okio.sink(file);
            bufferedSink = Okio.buffer(sink);
            bufferedSink.writeUtf8(fileContent);
            return true;
        } catch (Exception e) {
            L.e(e.toString());
            return false;
        } finally {
            try {
                bufferedSink.close();
                sink.close();
            } catch (Exception e) {
            }
        }

    }

    public static Set<String> parseText(String context, String reg) {
        Set<String> set = new HashSet<String>();
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(context);
        while (m.find()) {
            set.add(m.group());
        }
        return set;
    }

    public static Set<String> parseText(String context) {
        return parseText(context, DEFAULT_REG);
    }

    public static void main(String[] sss) {
        String context = readUTF8("J:\\AutoCreateCode\\config\\DialogFragment\\java\\fragmen\\MyDialogFragment.java");
        Set<String> set = parseText(context);
//        Lj.e(set);
//        Lj.e(writeUTF8("hello world","text.txt","F:\\1\\测试文件夹"));
    }
}
