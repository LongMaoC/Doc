package com.cxy.doc.parser;

import com.cxy.doc.Config;
import com.cxy.doc.bean.ParameNote;
import com.cxy.doc.bean.Version;
import com.cxy.doc.util.L;
import com.cxy.doc.util.ParseUtils;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ParserClass {

    public static void main(String[] args) {
//        List<File> list = new ArrayList<>();
//        controllerList("J:\\Outsourcing\\jihe\\jihe\\src\\main\\java\\cn\\com\\action", list);
//        L.e(list);


       List versionList =  new ArrayList<Version>() {
            {
                add(new Version(1.0f, "第一版"));
                add(new Version(3.0f, "第三版"));
                add(new Version(2.0f, "第二版"));
            }
        };
        L.e(versionList);
        Collections.sort(versionList);
        L.e(versionList);

    }

    /**
     * 根据路径获取所有Controller的类
     *
     * @param path
     * @return
     */
    public static List<File> controllerList(String path, List<File> list) {
        File[] files = new File(path).listFiles();

        for (int i = 0; i < files.length; i++) {
//            获取Controller类，过滤包含@Controller的类
            if (files[i].isDirectory()) {
                controllerList(files[i].getAbsolutePath(), list);
            } else {
                int finalI = i;
                ParseUtils.compilationUnit(files[i])
                        .getChildNodesByType(ClassOrInterfaceDeclaration.class)
                        .stream()
                        .anyMatch(new Predicate<ClassOrInterfaceDeclaration>() {
                            @Override
                            public boolean test(ClassOrInterfaceDeclaration cd) {
                                boolean b = cd.getAnnotationByName("Controller").isPresent() || cd.getAnnotationByName("RestController").isPresent();
                                if (b) {
                                    list.add(files[finalI]);
                                }
                                return b;
                            }
                        });
            }

        }
        return list;
    }


    public static ParameNote parseMethodParam(JavadocBlockTag javadocBlockTag) {
        ParameNote parameNote = new ParameNote();
        if (javadocBlockTag.getType() == JavadocBlockTag.Type.PARAM) {
            parameNote.parameterName = javadocBlockTag.getName().get();
            parameNote.parameterDesc = javadocBlockTag.getContent().toText();
        }
        return parameNote;
    }

    public static List<ParameNote> parseMethodsParam(MethodDeclaration methodDeclaration) {
        List<ParameNote> parameNoteList = new ArrayList<>();

        methodDeclaration.getJavadoc().ifPresent(new Consumer<Javadoc>() {
            @Override
            public void accept(Javadoc javadoc) {
                List<JavadocBlockTag> docParams = javadoc.getBlockTags();
                docParams.stream().forEach(new Consumer<JavadocBlockTag>() {
                    @Override
                    public void accept(JavadocBlockTag javadocBlockTag) {
                        if (javadocBlockTag.getType() == JavadocBlockTag.Type.PARAM) {
                            ParameNote parameNote = parseMethodParam(javadocBlockTag);
                            parameNoteList.add(parameNote);
                        }
                    }
                });
            }
        });


        Map<String, String> parameMap = new HashMap<>();

        methodDeclaration.getParameters().stream().forEach(new Consumer<Parameter>() {
            @Override
            public void accept(Parameter parameter) {
                parameMap.put(parameter.getName().asString(), parameter.getType().asString());
            }
        });

        parameNoteList.stream().forEach(new Consumer<ParameNote>() {
            @Override
            public void accept(ParameNote parameNote) {
                parameNote.parameterType = parameMap.get(parameNote.parameterName);
            }
        });
        return parameNoteList;
    }


}
