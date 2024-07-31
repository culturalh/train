package com.jxau.train.generator.gen;


import com.jxau.train.generator.util.DbUtil;
import com.jxau.train.generator.util.Field;
import com.jxau.train.generator.util.FreemarkerUtil;
import freemarker.template.TemplateException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ServerTest {
    static boolean readOnly = true;
    static String vuePath = "admin/src/views/main/";
    static String serverPath = "[module]/src/main/java/com/jxau/train/[module]/";
    static String pomPath = "generator/pom.xml";
    static String module = "";
    static {
        new File(serverPath).mkdirs();
    }
    public static void main(String[] args) throws Exception {
        //读取到pom.xml文件中的文件名
        String generatorPath = getGeneratorPath();
        module = generatorPath.replace("src/main/resources/generator-config-", "").replace(".xml", "");
        System.out.println("module:"+module);
        Document document = new SAXReader().read("generator/"+generatorPath);
        serverPath = serverPath.replace("[module]", module);
        new File(serverPath).mkdirs();
        System.out.println("servicePath:"+serverPath);
        //读取对应的table
        Node table = document.selectSingleNode("//table");
        System.out.println(table);
        Node tableName = table.selectSingleNode("@tableName");
        Node domainObjectName = table.selectSingleNode("@domainObjectName");
        System.out.println(tableName.getText()+"\t"+domainObjectName.getText());
         // Domain = JiawaTest
        String Domain = domainObjectName.getText();
        // domain = jiawaTest
        String domain = Domain.substring(0, 1).toLowerCase() + Domain.substring(1);
        // do_main = jiawa-test
        String do_main = tableName.getText().replaceAll("_", "-");

        // 为DbUtil设置数据源
        Node connectionURL = document.selectSingleNode("//@connectionURL");
        Node userId = document.selectSingleNode("//@userId");
        Node password = document.selectSingleNode("//@password");
        System.out.println("url: " + connectionURL.getText());
        System.out.println("user: " + userId.getText());
        System.out.println("password: " + password.getText());
        DbUtil.url = connectionURL.getText();
        DbUtil.user = userId.getText();
        DbUtil.password = password.getText();

        // 表中文名
        String tableNameCn = DbUtil.getTableComment(tableName.getText());
        List<Field> fieldList = DbUtil.getColumnByTableName(tableName.getText());
        Set<String> typeSet =getJavaTypes(fieldList);
        System.out.println("表名："+tableNameCn);
        System.out.println("字段："+fieldList);

        Map<String, Object> param = new HashMap<>();
        param.put("domain",domain);
        param.put("Domain",Domain);
        param.put("do_main",do_main);
        param.put("tableNameCn",tableNameCn);
        param.put("fieldList",fieldList);
        param.put("typeSet",typeSet);
        param.put("module",module);
        param.put("readOnly",readOnly);
        System.out.println("组装参数："+param);


        gen(Domain, param,"service","service");
        gen(Domain, param,"controller/admin","adminController");
        gen(Domain, param,"req","saveReq");
        gen(Domain, param,"req","queryReq");
        gen(Domain, param,"resp","queryResp");
        gen1(Domain, param,"serviceImpl");

        genVue(do_main, param);
    }

    private static void gen(String Domain, Map<String, Object> param,String packageName,String target) throws IOException, TemplateException {
        FreemarkerUtil.initConfig(target+".ftl");
        String toPath = serverPath + packageName + "/";
        new File(toPath).mkdirs();
        String Target = target.substring(0, 1).toUpperCase() + target.substring(1);
        String fileName = toPath + Domain + Target + ".java";
        System.out.println("开始生成："+fileName);
        FreemarkerUtil.generator(fileName, param);
    }
    private static void gen1(String Domain, Map<String, Object> param,String target) throws IOException, TemplateException {
        FreemarkerUtil.initConfig(target+".ftl");
        target = target.substring(0, 7);
        String toPath = serverPath + target + "/impl/";
        new File(toPath).mkdirs();
        String Target = target.substring(0, 1).toUpperCase() + target.substring(1)+"Impl";
        String fileName = toPath + Domain + Target + ".java";
        System.out.println("开始生成："+fileName);
        FreemarkerUtil.generator(fileName, param);
    }
    private static String getGeneratorPath() throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Map<String, String> map = new HashMap<>();
        map.put("pom", "http://maven.apache.org/POM/4.0.0");
        saxReader.getDocumentFactory().setXPathNamespaceURIs(map);
        Document document = saxReader.read(new File(pomPath));
        Node node = document.selectSingleNode("//pom:configurationFile");
        System.out.println(node.getText());
        return node.getText();
    }

        private static void genVue(String do_main, Map<String, Object> param) throws IOException, TemplateException {
        FreemarkerUtil.initConfig("vue.ftl");
        new File(vuePath + module).mkdirs();
        String fileName = vuePath + module + "/" + do_main + ".vue";
        System.out.println("开始生成：" + fileName);
        FreemarkerUtil.generator(fileName, param);
    }

    /**
     * 获取所有的Java类型，使用Set去重
     */
    private static Set<String> getJavaTypes(List<Field> fieldList) {
        Set<String> set = new HashSet<>();
        for (int i = 0; i < fieldList.size(); i++) {
            Field field = fieldList.get(i);
            set.add(field.getJavaType());
        }
        return set;
    }
}
