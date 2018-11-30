package com.cxy.doc;

import com.cxy.doc.annotation.JDocDescribe;
import com.cxy.doc.annotation.JDocErrors;
import com.cxy.doc.annotation.JDocReturns;
import com.cxy.doc.bean.*;
import com.cxy.doc.builddoc.BuildFactory;
import com.cxy.doc.builddoc.HtmlBuildFactoryImpl;
import com.cxy.doc.parser.ParserClass;
import com.cxy.doc.util.IOUtils;
import com.cxy.doc.util.L;
import com.cxy.doc.util.ParseUtils;
import com.cxy.doc.util.TextUtils;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.metamodel.SingleMemberAnnotationExprMetaModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * NormalAnnotationExpr 表示key value的形式
 */
public class Doc {

    public static void main(String[] args) {
        Config config = new Config.ConfigBuilder()
                .suffix("do")
//                .prefix("http://localhost:8180/")
                        .prefix("http://116.62.234.29:8197/")
                .controllerPath("J:\\Outsourcing\\douke\\douke_front\\src\\main\\java\\cn\\com\\douke\\front\\controller")
                .docOutPath("J:\\Outsourcing\\douke\\douke_front")
                .versionList(new ArrayList<Version>() {
                    {
//                        add(new Version(2.0f, "第二版"));
                        add(new Version(1.0f, "v20181011"));
                    }
                })
                .build();
        Doc.run(config);
    }

    public static void run(Config config) {

        List<File> ctrlFileList = new ArrayList<File>();
        ParserClass.controllerList(config.getControllerPath(), ctrlFileList);

        List<ClassNode> classNodeList = new ArrayList<ClassNode>();
        for (File f : ctrlFileList) {
            String fileName = f.getName().substring(0, f.getName().lastIndexOf("."));
            ParseUtils.compilationUnit(f)
                    .getClassByName(fileName)
//                    .getClassByName("StudentclassController")
                    .ifPresent(new Consumer<ClassOrInterfaceDeclaration>() {
                        @Override
                        public void accept(ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
                            ClassNode classNode = new ClassNode();
                            classNode.methodNodeList = new ArrayList<>();
                            classNode.classDesc = new ArrayList<>();
                            //解析类上的RequestMapping
                            parse_RequestMapping(classNode, classOrInterfaceDeclaration);
                            //解析类上的JDocDescribe
                            parse_JDocDescribe(classNode, classOrInterfaceDeclaration);

                            //解析方法上的数据
                            classOrInterfaceDeclaration.getMethods().stream().forEach(new Consumer<MethodDeclaration>() {

                                @Override
                                public void accept(MethodDeclaration methodDeclaration) {
                                    MethodNode methodNode = new MethodNode();

                                    List<ParameNote> parameNoteList = ParserClass.parseMethodsParam(methodDeclaration);
                                    //解析所有参数
                                    methodNode.parameNoteList = parameNoteList;

                                    parse_Method_RequestMapping(methodNode, methodDeclaration);
                                    parse_Method_JDocVersion(methodNode, methodDeclaration, config);
                                    parse_Method_JDocDescribe(methodNode, methodDeclaration);
                                    parse_Method_JDocError(methodNode, methodDeclaration);

                                    if (methodNode.methodDesc != null && methodNode.methodDesc.size() > 0) {
                                        classNode.methodNodeList.add(methodNode);
                                    }
                                }
                            });
                            classNodeList.add(classNode);
                        }
                    });
        }

        BuildFactory buildFactory = new HtmlBuildFactoryImpl(config);

        String context = buildFactory.createIndex(classNodeList);
        IOUtils.writeUTF8(context, buildFactory.getFileName(), config.getDocOutPath());

        for (int i = 0; i < classNodeList.size(); i++) {
            String ctrlContext = buildFactory.createControllerContext(classNodeList.get(i));
            IOUtils.writeUTF8(ctrlContext, buildFactory.getFileName(), config.getDocOutPath());
        }

        System.out.println("完成！");
    }

    private static void parse_Method_JDocError(MethodNode methodNode, MethodDeclaration methodDeclaration) {
        methodDeclaration
                .getAnnotationByClass(JDocErrors.class)
                .ifPresent(new Consumer<AnnotationExpr>() {
                    @Override
                    public void accept(AnnotationExpr an) {
                        List<ErrorNode> list = new ArrayList<>();
                        if (an instanceof SingleMemberAnnotationExpr) {
                            SingleMemberAnnotationExpr normalAnExpr = (SingleMemberAnnotationExpr) an;
                            Expression memberValue = normalAnExpr.getMemberValue();
                            if (memberValue instanceof ArrayInitializerExpr) {
                                ArrayInitializerExpr arrayInitializerExpr = (ArrayInitializerExpr) memberValue;
                                arrayInitializerExpr.getValues().forEach(new Consumer<Expression>() {
                                    @Override
                                    public void accept(Expression expression) {
                                        if (expression instanceof NormalAnnotationExpr) {
                                            ErrorNode errorNode = parse_Method_JDocError_NormalAnnotationExpr((NormalAnnotationExpr) expression);
                                            if (errorNode.errorMessage != null && errorNode.errorCode != null) {
                                                list.add(errorNode);
                                            }
                                        }
                                    }
                                });
                            } else if (memberValue instanceof NormalAnnotationExpr) {
                                ErrorNode errorNode = parse_Method_JDocError_NormalAnnotationExpr((NormalAnnotationExpr) memberValue);
                                if (errorNode.errorMessage != null && errorNode.errorCode != null) {
                                    list.add(errorNode);
                                }
                            }

                            methodNode.errorNoteList = list;

                        } else {
                            L.e("其他");
                        }

                    }
                });
    }

    private static ErrorNode parse_Method_JDocError_NormalAnnotationExpr(NormalAnnotationExpr normalAnnotationExpr) {
        ErrorNode errorNode = new ErrorNode();
        for (MemberValuePair mvPair : normalAnnotationExpr.getPairs()) {
            if (mvPair.getNameAsString().equals("message")) {
                mvPair.getValue().toStringLiteralExpr().ifPresent(new Consumer<StringLiteralExpr>() {
                    @Override
                    public void accept(StringLiteralExpr stringLiteralExpr) {
                        errorNode.errorMessage = stringLiteralExpr.getValue();
                    }
                });
            } else if (mvPair.getNameAsString().equals("code")) {
                mvPair.getValue().toStringLiteralExpr().ifPresent(new Consumer<StringLiteralExpr>() {
                    @Override
                    public void accept(StringLiteralExpr stringLiteralExpr) {
                        errorNode.errorCode = stringLiteralExpr.getValue();
                    }
                });
            }
        }
        return errorNode;
    }

    private static void parse_Method_JDocDescribe(MethodNode methodNode, MethodDeclaration methodDeclaration) {
        methodDeclaration
                .getAnnotationByClass(JDocDescribe.class)
                .ifPresent(new Consumer<AnnotationExpr>() {
                    @Override
                    public void accept(AnnotationExpr an) {
                        List<String> list = new ArrayList<>();

                        if(an instanceof SingleMemberAnnotationExpr){
                            SingleMemberAnnotationExpr singleMemberAnnotationExpr = (SingleMemberAnnotationExpr) an;
                            Expression memberValue = singleMemberAnnotationExpr.getMemberValue();

                            if(memberValue instanceof NormalAnnotationExpr){
                                ((NormalAnnotationExpr) memberValue).getPairs()
                                        .stream()
                                        .filter(new Predicate<MemberValuePair>() {
                                            @Override
                                            public boolean test(MemberValuePair memberValuePair) {
                                                return memberValuePair.getNameAsString().equals("value");
                                            }
                                        })
                                        .findFirst().get().getValue().toStringLiteralExpr()
                                        .ifPresent(new Consumer<StringLiteralExpr>() {
                                            @Override
                                            public void accept(StringLiteralExpr stringLiteralExpr) {
                                                String str = stringLiteralExpr.asString();
                                                if(str!=null && !str.equals(""))
                                                    list.add(str);
                                            }
                                        });
                            }else if(memberValue instanceof ArrayInitializerExpr){
                                ArrayInitializerExpr arrayInitializerExpr = (ArrayInitializerExpr) memberValue;
                                arrayInitializerExpr.getValues().stream().forEach(new Consumer<Expression>() {
                                    @Override
                                    public void accept(Expression expression) {
                                        String str = expression.toStringLiteralExpr().get().asString() ;
                                        if(str!=null && !str.equals("")){
                                            list.add(str);
                                        }
                                    }
                                });
                            }else if(memberValue instanceof StringLiteralExpr){
                                StringLiteralExpr stringLiteralExpr = (StringLiteralExpr) memberValue;
                                String str = stringLiteralExpr.asString();
                                if(str!=null && !str.equals("")){
                                    list.add(str);
                                }
                            }
                            methodNode.methodDesc = list;
                        }
                    }
                });
    }

    private static void parse_Method_JDocVersion(MethodNode methodNode, MethodDeclaration methodDeclaration, Config config) {
        //解析@JDocVersion注解
        methodDeclaration
                .getAnnotationByName("JDocVersion")
                .ifPresent(new Consumer<AnnotationExpr>() {
                    @Override
                    public void accept(AnnotationExpr an) {
                        if(an instanceof SingleMemberAnnotationExpr){
                            SingleMemberAnnotationExpr singleMemberAnnotationExpr = (SingleMemberAnnotationExpr) an;
                            Expression memberValue = singleMemberAnnotationExpr.getMemberValue();
                            if(memberValue instanceof NormalAnnotationExpr){
                                ((NormalAnnotationExpr) memberValue).getPairs()
                                        .stream()
                                        .filter(new Predicate<MemberValuePair>() {
                                            @Override
                                            public boolean test(MemberValuePair memberValuePair) {
                                                return memberValuePair.getNameAsString().equals("value");
                                            }
                                        })
                                        .findFirst()
                                        .get()
                                        .getValue()
                                        .toStringLiteralExpr()
                                        .ifPresent(new Consumer<StringLiteralExpr>() {
                                            @Override
                                            public void accept(StringLiteralExpr stringLiteralExpr) {
                                                float key = 0;
                                                key = Float.valueOf(stringLiteralExpr.asString());

                                                Version v = config.getVersion(key);
                                                if (v == null) {
                                                    methodNode.version = new Version(0f, "默认版本");
                                                } else {
                                                    methodNode.version = v;
                                                }
                                            }
                                        });


                            }
                        }
                    }
                });

        if (methodNode.version == null) {
            methodNode.version = new Version(0f, "默认版本");
        }
    }

    private static void parse_Method_RequestMapping(MethodNode methodNode, MethodDeclaration methodDeclaration) {
        //解析@RequestMapping注解
        methodDeclaration
                .getAnnotationByName("RequestMapping")
                .ifPresent(new Consumer<AnnotationExpr>() {
                    @Override
                    public void accept(AnnotationExpr an) {
                        if (an instanceof SingleMemberAnnotationExpr) {
                            String value = ((SingleMemberAnnotationExpr) an).getMemberValue().toString();
                            methodNode.methodUrl = value.substring(1, value.length() - 1);
                            if (methodNode.methodUrl.substring(0, 1).equals("\\") || methodNode.methodUrl.substring(0, 1).equals("/")) {
                                methodNode.methodUrl = methodNode.methodUrl.substring(1, methodNode.methodUrl.length());
                            }
                        } else if (an instanceof NormalAnnotationExpr) {

                            NormalAnnotationExpr normalAnExpr = (NormalAnnotationExpr) an;
                            for (MemberValuePair mvPair : normalAnExpr.getPairs()) {
                                String name = mvPair.getName().asString();
                                if ("value".equalsIgnoreCase(name)) {
                                    Expression expression = mvPair.getValue();
                                    if (expression instanceof ArrayInitializerExpr) {
                                        if (((ArrayInitializerExpr) expression).getValues().size() > 0) {
                                            ((ArrayInitializerExpr) expression).getValues().get(0).toStringLiteralExpr().ifPresent(new Consumer<StringLiteralExpr>() {
                                                @Override
                                                public void accept(StringLiteralExpr stringLiteralExpr) {
                                                    methodNode.methodUrl = stringLiteralExpr.asString();
                                                }
                                            });
                                        }
                                    } else if (expression instanceof StringLiteralExpr) {
                                        methodNode.methodUrl = ((StringLiteralExpr) expression).getValue();
                                    } else {
                                        System.out.println("代码补充--》");
                                        String value = expression.toString();
                                        methodNode.methodUrl = value.substring(1, value.length() - 1);
                                        if (methodNode.methodUrl.substring(0, 1).equals("\\") || methodNode.methodUrl.substring(0, 1).equals("/")) {
                                            methodNode.methodUrl = methodNode.methodUrl.substring(1, methodNode.methodUrl.length());
                                        }
                                    }
                                }
                                if ("method".equalsIgnoreCase(name)) {
                                    if (methodNode.methodType == null) {
                                        methodNode.methodType = new ArrayList<>();
                                    }
                                    mvPair.getValue().getChildNodes().forEach(new Consumer<Node>() {
                                        @Override
                                        public void accept(Node node) {
                                            String value = node.toString();
                                            methodNode.methodType.add(value.substring(value.lastIndexOf(".") + 1, value.length()));
                                        }
                                    });
                                }
                            }
                        }
                    }
                });
    }

    private static void parse_RequestMapping(ClassNode classNode, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        classOrInterfaceDeclaration.getAnnotationByName("RequestMapping")
                .ifPresent(new Consumer<AnnotationExpr>() {
                    @Override
                    public void accept(AnnotationExpr an) {
                        if (an instanceof SingleMemberAnnotationExpr) {
                            Expression memberValue = ((SingleMemberAnnotationExpr) an).getMemberValue();
                            String value = ((SingleMemberAnnotationExpr) an).getMemberValue().toString();
                            classNode.classUrl = value.substring(1, value.length() - 1);
                            if (classNode.classUrl.substring(0, 1).equals("\\") || classNode.classUrl.substring(0, 1).equals("/")) {
                                classNode.classUrl = classNode.classUrl.substring(1, classNode.classUrl.length());
                            }
                        } else if (an instanceof NormalAnnotationExpr) {
                            NormalAnnotationExpr normalAnExpr = (NormalAnnotationExpr) an;
                            for (MemberValuePair mvPair : normalAnExpr.getPairs()) {
                                String name = mvPair.getName().asString();
                                if ("value".equalsIgnoreCase(name)) {
                                    Expression expression = mvPair.getValue();
                                    if (expression instanceof StringLiteralExpr) {
                                        classNode.classUrl = ((StringLiteralExpr) expression).getValue();
                                    } else {
                                        System.out.println("代码补充--》");
                                        String value = (mvPair.getValue().toString());
                                        classNode.classUrl = value.substring(1, value.length() - 1);
                                        if (classNode.classUrl.substring(0, 1).equals("\\") || classNode.classUrl.substring(0, 1).equals("/")) {
                                            classNode.classUrl = classNode.classUrl.substring(1, classNode.classUrl.length());
                                        }
                                    }
                                }
                            }

                        }
                    }
                });
    }

    private static void parse_JDocDescribe(ClassNode classNode, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        classOrInterfaceDeclaration
                .getAnnotationByClass(JDocDescribe.class)
                .ifPresent(new Consumer<AnnotationExpr>() {
                    @Override
                    public void accept(AnnotationExpr an) {
                        if (an instanceof SingleMemberAnnotationExpr) {
                            Expression memberValue = ((SingleMemberAnnotationExpr) an).getMemberValue();
                            if (memberValue instanceof StringLiteralExpr) {
                                String sss = ((StringLiteralExpr) memberValue).getValue();
                                classNode.classDesc.add(sss);
                            } else if (memberValue instanceof ArrayInitializerExpr) {
                                if (((ArrayInitializerExpr) memberValue).getValues() != null) {
                                    ((ArrayInitializerExpr) memberValue).getValues().stream().forEach(new Consumer<Expression>() {
                                        @Override
                                        public void accept(Expression expression) {
                                            expression.toStringLiteralExpr().ifPresent(new Consumer<StringLiteralExpr>() {
                                                @Override
                                                public void accept(StringLiteralExpr stringLiteralExpr) {
                                                    classNode.classDesc.add(stringLiteralExpr.asString());
                                                }
                                            });
                                        }
                                    });
                                }
                            } else {
                                L.e("需要增强解析");
                            }
                        } else if (an instanceof NormalAnnotationExpr) {
                            NormalAnnotationExpr normalAnExpr = (NormalAnnotationExpr) an;
                            for (MemberValuePair mvPair : normalAnExpr.getPairs()) {
                                String name = mvPair.getName().asString();
                                if ("value".equalsIgnoreCase(name)) {
                                    Expression expression = mvPair.getValue();
                                    if (expression instanceof StringLiteralExpr) {
                                        classNode.classDesc.add(((StringLiteralExpr) expression).getValue());
                                    } else if (expression instanceof ArrayInitializerExpr) {
                                        if (((ArrayInitializerExpr) expression).getValues() != null) {
                                            ((ArrayInitializerExpr) expression).getValues().stream().forEach(new Consumer<Expression>() {
                                                @Override
                                                public void accept(Expression expression) {
                                                    expression.toStringLiteralExpr().ifPresent(new Consumer<StringLiteralExpr>() {
                                                        @Override
                                                        public void accept(StringLiteralExpr stringLiteralExpr) {
                                                            classNode.classDesc.add(stringLiteralExpr.asString());
                                                        }
                                                    });
                                                }
                                            });
                                        }
                                    }
                                }
                            }
                        } else {
                            L.e("需要增强解析");
                        }
                    }
                });
    }


}
