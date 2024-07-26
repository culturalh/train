package com.jxau.train.generator.server;


import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ServerTest {
    static String toPath = "generator\\src\\main\\java\\com\\jxau\\train\\generator\\test\\";
    static String pomPath = "generator\\pom.xml";
    static {
        new File(toPath).mkdirs();
    }
    public static void main(String[] args) throws Exception {
        //读取到pom.xml文件中的文件名
        String generatorPath = getGeneratorPath();

        Document document = new SAXReader().read("generator/"+generatorPath);
        //读取对应的table
        Node table = document.selectSingleNode("//table");
        System.out.println(table);
        Node tableName = table.selectSingleNode("@tableName");
        Node domainObjectName = table.selectSingleNode("@domainObjectName");
        System.out.println(tableName.getText()+"\t"+domainObjectName.getText());


        //        FreemarkerUtil.initConfig("test.ftl");
//        Map<String, Object> param =new HashMap<>();
//        param.put("domain", "Test");
//        FreemarkerUtil.generator(toPath+"Test.java", param);

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
