package com.cxy.doc.builddoc;

import com.cxy.doc.Config;
import com.cxy.doc.bean.ClassNode;
import com.cxy.doc.bean.ErrorNode;
import com.cxy.doc.bean.MethodNode;
import com.cxy.doc.bean.ParameNote;
import com.cxy.doc.util.TextUtils;

import java.util.List;

import static com.cxy.doc.util.TextUtils.append;

public class HtmlBuildFactoryImpl extends HtmlBuildFactory {
    private String fileName = "index";

    public HtmlBuildFactoryImpl(Config config) {
        super(config);
    }


    @Override
    public String createIndex(List<ClassNode> classNodeList) {
        StringBuilder sb = new StringBuilder();
        append(0, "<html lang=\"en\">", sb);
        append(1, "<head >", sb);
        append(2, "<meta charset=\"UTF-8\">", sb);
        append(2, "<title>文档索引页</title>", sb);
        append(1, "</head>", 2, sb);

        append(1, "<body>", sb);

        append(2, "<table width=\"100%\" border=\"1\" cellpadding=\"0\" cellspacing=\"0\" bordercolorlight=\"#183789\" bordercolordark=\"#FFFFFF\">", sb);
        append(3, "<tr>", sb);
        append(4, "<td width=\"20%\">一级目录</td>", sb);
        append(4, "<td width=\"35%\">描述</td>", sb);
        append(4, "<td >下级接口描述</td>", sb);
        append(3, "</tr>", sb);

        int index = 0 ;

        for (int i = 0, size = classNodeList.size(); i < size; i++) {
            ClassNode classNode = classNodeList.get(i);
            append(3, "<tr>", sb);
            append(4, "<td >", sb);
            append(5, "<a href=\"" + classNode.classUrl + "." + BuildFactory.SUFFIX_HTML + "\">" + classNode.classUrl + "</a>", sb);
            append(4, "</td>", sb);
            append(4, "<td >", sb);
            String temp = "";
            if(classNode.classDesc!=null){
                for (int j = 0; j < classNode.classDesc.size(); j++) {
                    String s = classNode.classDesc.get(j) ;
                    if(!TextUtils.isEntry(s)){
                        temp+=s+"<br>";
                    }
                }
            }
            append(5,TextUtils.isEntry(temp)?"暂无描述":temp, sb);
            append(4, "</td>", sb);

            append(4, "<td >", sb);
            for (int j = 0; j < classNode.methodNodeList.size(); j++) {
                MethodNode methodNode = classNode.methodNodeList.get(j);
                boolean isNew = false;

                if (methodNode.version != null) {
                    isNew = config.isNew(methodNode.version.key);
                }


                append(4,
                        String.format("%2d",(j + 1)) + ". "  +
                                (methodNode.methodDesc != null && methodNode.methodDesc.size() > 0 ? methodNode.methodDesc.get(0) : methodNode.methodUrl) +
                                ("<span style=\"background-color: #BDBDBD;color: #FFFFFF;padding:0px 3px ;\">"+methodNode.version.value+"</span>")+
                                "<br>" +
                                (j != classNode.methodNodeList.size() - 1 ? "<hr>" : "")
                        ,sb
                );

//                append(4,
//                        (j + 1) + ". " +
//                                (isNew ? "<span style=\"background-color: #10A54A;color: #FFFFFF;padding:0px 3px ;\">最新</span>" : "") +
//                                (methodNode.methodDesc != null && methodNode.methodDesc.size() > 0 ? methodNode.methodDesc.get(0) : methodNode.methodUrl) + "<br>" +
//                                (j != classNode.methodNodeList.size() - 1 ? "<hr>" : "")
//                        ,sb
//                );
                index++;
            }
            append(4, "</td >", sb);
            append(3, "</tr>", sb);
        }

        append(2, "</table>", sb);

        append(2,"<span>共"+index+"接口</span>",sb);

        append(1, "</body>", sb);
        append(0, "</html>", sb);

        return sb.toString();
    }

    @Override
    public String getFileName() {
        return fileName + "." + BuildFactory.SUFFIX_HTML;
    }

    @Override
    public String createControllerContext(ClassNode classNode) {
        fileName = classNode.classUrl;
        StringBuilder sb = new StringBuilder();
        append(0, "<html lang=\"en\">", sb);
        append(1, "<head >", sb);
        append(2, "<meta charset=\"UTF-8\">", sb);
        append(2, "<title>" + classNode.classDesc + "</title>", sb);
        append(2, createStyle(), sb);
        append(2, createScript(), sb);
        append(1, "</head>", 2, sb);

        append(1, "<body>", sb);

        append(2, "<div class=\"list_v\">", sb);
        for (int i = 0; i < classNode.methodNodeList.size(); i++) {
            MethodNode methodNode = classNode.methodNodeList.get(i);
            append(3, "<div class=\"list_v div_item\" >", sb);
            append(4, "<div class=\"list_h\"  onclick=\"divItemTitleClick('div_item_" + classNode.classUrl + "_" + methodNode.methodUrl + "')\">", sb);

            String modelTypeStr = "";
            if (methodNode.methodType == null || methodNode.methodType.size() == 0) {
                modelTypeStr = "ALL";
            } else {
                for (int z = 0; z < methodNode.methodType.size(); z++) {
                    if (z == methodNode.methodType.size() - 1) {
                        modelTypeStr += methodNode.methodType.get(z);
                    } else {
                        modelTypeStr += methodNode.methodType.get(z) + "/";
                    }
                }
            }


            append(5, "<span class=\"span_method_post\">" + modelTypeStr + "</span>", sb);
            append(5, "<span class=\"span_method_url\" style=\"flex-grow:2\">" +config.getPrefix()+ "/" + classNode.classUrl + "/" + methodNode.methodUrl + (!TextUtils.isEntry(config.getSuffix() )?"." + config.getSuffix() :"")+ "</span>", sb);
            append(5, "<span class=\"span_method_des text_title\">" + (methodNode.methodDesc != null && methodNode.methodDesc.size() > 0 ? methodNode.methodDesc.get(0) : "暂无描述") + "</span>", sb);
            append(4, "</div>", sb);

            append(4, "<div class=\"div_item_content\" " + (i != 0 ? "style=\"display:none\"" : "") + "id=\"div_item_" + classNode.classUrl + "_" + methodNode.methodUrl + "\">", sb);

            append(5, "<div class=\"list_h\">", sb);
            append(6, "<span class=\"text_title\">接口说明</span>", sb);
            append(5, "</div >", sb);

            append(5, "<div class=\"list_v\" style=\"padding-left: 10px;margin-top: 5px;\">", sb);
            if (methodNode.methodDesc != null && methodNode.methodDesc.size() >= 0) {
                for (int q = 0; q < methodNode.methodDesc.size(); q++) {
                    append(5, "<span >" + methodNode.methodDesc.get(q) + "</span>", sb);
                }
            } else {
                append(5, "<span >暂无</span>", sb);
            }
            append(5, "</div >", sb);
            append(5, "<br >", sb);



            //参数说明
            append(5, "<div class=\"list_h\">", sb);
            append(6, "<span class=\"text_title\">参数说明</span>", sb);
            append(5, "</div >", sb);

            if (methodNode.parameNoteList != null && methodNode.parameNoteList.size() > 0) {
                append(5, "<table style=\"padding-left: 10px;margin-top: 5px;width: 100%\">", sb);
                append(6, "<tr>", sb);
                append(7, "<td style=\"width: 15%\">参数名</td>", sb);
                append(7, "<td style=\"width: 8%\">类型</td>", sb);
                append(7, "<td>描述</td>", sb);
                append(6, "<tr>", sb);
                for (int j = 0; j < methodNode.parameNoteList.size(); j++) {
                    ParameNote parameNote = methodNode.parameNoteList.get(j);
                    append(6, "<tr>", sb);
                    append(7, "<td >" + parameNote.parameterName + "</td>", sb);
                    append(7, "<td >" + parameNote.parameterType + "</td>", sb);
                    append(7, "<td >" + parameNote.parameterDesc + "</td>", sb);
                    append(6, "</tr>", sb);
                }
                append(5, "</table>", sb);
            } else {
                append(6, "<tr>", sb);
                append(7, "<span style=\"padding-left: 10px;margin-top: 5px;width: 100%\">暂无</span>", sb);
                append(6, "</tr>", sb);
            }


            //错误码说明
            append(5, "<div class=\"list_h\">", sb);
            append(6, "<span class=\"text_title\">异常说明</span>", sb);
            append(5, "</div >", sb);

            if (methodNode.errorNoteList != null && methodNode.errorNoteList.size() > 0) {
                append(5, "<table style=\"padding-left: 10px;margin-top: 5px;width: 100%\">", sb);
                append(6, "<tr>", sb);
                append(7, "<td style=\"width: 15%\">错误码</td>", sb);
                append(7, "<td>说明</td>", sb);
                append(6, "<tr>", sb);
                for (int j = 0; j < methodNode.errorNoteList.size(); j++) {
                    ErrorNode errorNode = methodNode.errorNoteList.get(j);
                    append(6, "<tr>", sb);
                    append(7, "<td >" + errorNode.errorCode + "</td>", sb);
                    append(7, "<td >" + errorNode.errorMessage + "</td>", sb);
                    append(6, "</tr>", sb);
                }
                append(5, "</table>", sb);
            } else {
                append(6, "<tr>", sb);
                append(7, "<span style=\"padding-left: 10px;margin-top: 5px;width: 100%\">暂无</span>", sb);
                append(6, "</tr>", sb);
            }


            append(4, "</div>", sb);
            append(3, "</div>", sb);
            append(3, "", sb);
        }

        append(2, "</div>", sb);
        append(1, "</body>", sb);
        append(0, "</html>", sb);
        return sb.toString();
    }

    @Override
    public String createStyle() {
        String style = "<style>\n" +
                "        .list_h {\n" +
                "            display: flex;\n" +
                "            flex-direction: row;\n" +
                "        }\n" +
                "        .list_v {\n" +
                "            display: flex;\n" +
                "            flex-direction: column;\n" +
                "        }\n" +
                "        .span_method_post {\n" +
                "            height: 30px;\n" +
                "            line-height: 30px;\n" +
                "            background: #10A54A;\n" +
                "            color: #FFF;\n" +
                "            width: 150px;\n" +
                "            padding: 5px 5px;\n" +
                "        }\n" +
                "        .span_method_url {\n" +
                "            height: 30px;\n" +
                "            line-height: 30px;\n" +
                "            color: #333333;\n" +
                "            padding: 5px 20px;\n" +
                "        }\n" +
                "        .span_method_des {\n" +
                "            height: 30px;\n" +
                "            line-height: 30px;\n" +
                "            padding: 5px 15px;\n" +
                "        }\n" +
                "        .div_item {\n" +
                "            background: #EBF7F0;\n" +
                "            margin-bottom: 10px;\n" +
                "        }\n" +
                "        .div_item_content {\n" +
                "            padding: 5px 15px;\n" +
                "        }\n" +
                "        .text_title {\n" +
                "            color: #10A54A;\n" +
                "        }\n" +
                "    </style>";
        return style;
    }

    @Override
    public String createScript() {
        StringBuilder sb = new StringBuilder();
        append(1, "<script src=\"http://libs.baidu.com/jquery/2.1.4/jquery.min.js\"></script>", sb);
        append(1, "<script >", sb);
        append(2, "function divItemTitleClick(objId) {", sb);
        append(3, "if($(\"#\"+objId).is(\":hidden\")){", sb);
        append(4, "$(\"#\"+objId).show();", sb);
        append(3, "}else{", sb);
        append(4, "$(\"#\"+objId).hide();", sb);
        append(3, "}", sb);
        append(2, "}", sb);
        append(1, "</script>", sb);
        return sb.toString();
    }
}
