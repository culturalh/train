package com.jxau.train.generator.server;


import com.jxau.train.generator.util.FreemarkerUtil;
import freemarker.template.TemplateException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ServerTest {
    static String serverPath = "[module]\\src\\main\\java\\com\\jxau\\train\\[module]\\";
    static String pomPath = "generator\\pom.xml";
    static {
        new File(serverPath).mkdirs();
    }
    public static void main(String[] args) throws Exception {
        //读取到pom.xml文件中的文件名
        String generatorPath = getGeneratorPath();
        String module = generatorPath.replace("src/main/resources/generator-config-", "").replace(".xml", "");
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
        // 表中文名
//        String tableNameCn = DbUtil.getTableComment(tableName.getText());
//        List<Field> fieldList = DbUtil.getColumnByTableName(tableName.getText());
//        Set<String> typeSet = getJavaTypes(fieldList);


        Map<String, Object> param = new HashMap<>();
        param.put("domain",domain);
        param.put("Domain",Domain);
        param.put("do_main",do_main);
        System.out.println("组装参数："+param);


        gen(Domain, param,"service");
        gen(Domain, param,"controller");

    }

    private static void gen(String Domain, Map<String, Object> param,String target) throws IOException, TemplateException {
        FreemarkerUtil.initConfig(target+".ftl");
        String toPath = serverPath + target + "\\";
        new File(toPath).mkdirs();
        String Target = target.substring(0, 1).toUpperCase() + target.substring(1);
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
}
