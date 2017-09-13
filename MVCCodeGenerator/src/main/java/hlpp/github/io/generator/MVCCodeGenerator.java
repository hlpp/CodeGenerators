package hlpp.github.io.generator;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import hlpp.github.io.generator.data.MClass;
import hlpp.github.io.generator.data.MPackage;
import hlpp.github.io.generator.data.Table;
import hlpp.github.io.generator.data.TableHeadDataOption;
import hlpp.github.io.generator.utils.StrUtils;

public class MVCCodeGenerator {
    private VelocityEngine engine;
    private ConfigManager config;
    
    public void doConfig(String path) {
        config = new ConfigManager(getClass().getResource("/config.xml").getPath() + path);
        config.set("mybaits-generator-config-path", getClass().getResource("/mybaits-generator.xml").getPath());

        VelocityContext context = new VelocityContext();

        initContext(config, context);
        
        generate(context);
    }

    private void initContext(ConfigManager config, VelocityContext context) {
        // 解析 mybaits-generator-config 中的model元素的targetPackage属性值
        SAXReader reader = new SAXReader();
        Document doc = null;
        try {
            doc = reader.read(config.get("mybaits-generator-config-path"));
            if (doc == null)
                return;
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = doc.getRootElement();
        Element contextElem = root.element("context");
        Element modelElem = contextElem.element("javaModelGenerator");
        Element tableElem = contextElem.element("table");
        String modelTagetPackage = modelElem.attribute("targetPackage").getValue();
        String objectName = tableElem.attribute("domainObjectName").getValue();
        
        context.put("modelTargetProject", modelElem.attribute("targetProject").getValue());
        
        Class<?> poClass = null;
        try {
            poClass = Class.forName(modelTagetPackage + "." + objectName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        
        context.put("poClass", poClass);
        context.put("pagePOClass", poClass);
        String poVarName = StrUtils.firstLetterLower(poClass.getSimpleName());
        context.put("poVarName", poVarName);
        context.put("pagePOVarName", poVarName);

        String poClassPackageName = poClass.getPackage().getName();
        String parentOfPackageOfPOClass = poClassPackageName.substring(0, poClassPackageName.lastIndexOf("."));
            
        MClass serviceClass = new MClass();
        serviceClass.setPackage( new MPackage( parentOfPackageOfPOClass + ".service") );
        serviceClass.setSimpleName(poClass.getSimpleName() + "Service");
        context.put("serviceClass", serviceClass);
        context.put("serviceVarName", StrUtils.firstLetterLower(serviceClass.getSimpleName()));
        
        MClass controllerClass = new MClass();
        controllerClass.setPackage( new MPackage( parentOfPackageOfPOClass + ".controller") );
        controllerClass.setSimpleName(poClass.getSimpleName() + "Controller");
        context.put("controllerClass", controllerClass);
        
        String controllerRequestMappingPathName = StrUtils.firstLetterLower(poClass.getSimpleName());
        context.put("controllerRequestMappingPath", "/" + controllerRequestMappingPathName);
        context.put("controllerRequestMappingPathName", controllerRequestMappingPathName);
        
        context.put("indexJSPath", StrUtils.firstLetterLower(poClass.getSimpleName()));
        
        initTable(context);
    }

    private void initTable(VelocityContext context) {
        List<String> fieldNames = new ArrayList<String>();
        Class<?> pagePOClass = (Class<?>) context.get("pagePOClass");
        Table table = new Table();
        BeanInfo beanInfo;
        try {
            beanInfo = Introspector.getBeanInfo(pagePOClass);
            PropertyDescriptor[] propDescs = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor desc : propDescs) {
                if (!desc.getName().equals("class"))
                    fieldNames.add(desc.getName());
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        }
        
        int avgWidthPercent = (int)(100 * (1.0f / fieldNames.size()));
        
        for (String fieldName : fieldNames) {
            TableHeadDataOption opt = new TableHeadDataOption();
            opt.setField(fieldName);
            opt.setWidth(avgWidthPercent + "%");
            opt.setAlign("center");
            opt.setTitle(fieldName);
            table.getHeadDataOptions().add(opt);
        }
        
        context.put("table", table);
    }

    private void generate(VelocityContext context) {
        // 初始化
        Properties props = new Properties();
        props.put("input.encoding", "UTF-8");
        props.put("output.encoding", "UTF-8");
        props.put("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.Log4JLogChute");
        props.put("runtime.log.logsystem.log4j.logger", "velocity");
        props.put("file.resource.loader.path", getClass().getResource("/templates").getPath());
        
        engine = new VelocityEngine(props);
        
        genJSs(context);
        genJSPs(context);
        genController(context);
        genService(context);
    }
    
    private void genController(VelocityContext context) {
        String packageDir = ((MClass)context.get("controllerClass")).getPackage().getName().replaceAll("\\.", "/");
        File dir = new File(System.getProperty("user.dir").replaceAll("\\\\", "/")
            + "/" + context.get("modelTargetProject") + "/" + packageDir);
        File file = new File(dir, ((MClass)context.get("controllerClass")).getSimpleName() + ".java");
        execTemplateToFile(context, "/java/Controller.vm", file);
        System.out.println("生成Controller完成");
    }
    
    private void genService(VelocityContext context) {
        String packageDir = ((MClass)context.get("serviceClass")).getPackage().getName().replaceAll("\\.", "/");
        File dir = new File(System.getProperty("user.dir").replaceAll("\\\\", "/")
            + "/" + context.get("modelTargetProject") + "/" + packageDir);
        File file = new File(dir, ((MClass)context.get("serviceClass")).getSimpleName() + ".java");
        execTemplateToFile(context, "/java/Service.vm", file);
        System.out.println("生成Service完成");
    }
    
    private void genJSs(VelocityContext context) {
        File dir = new File(System.getProperty("user.dir").replaceAll("\\\\", "/")
            + "/src/main/webapp/js/"
            + StrUtils.firstLetterLower(((Class)context.get("poClass")).getSimpleName()));
        File file = new File(dir, "index.js");
        execTemplateToFile(context, "/js/index.js.vm", file);
        System.out.println("生成JS完成");
    }

    private void genJSPs(VelocityContext context) {
        File dir = new File(System.getProperty("user.dir").replaceAll("\\\\", "/")
            + "/src/main/webapp/WEB-INF/jsp/"
            + StrUtils.firstLetterLower(((Class)context.get("poClass")).getSimpleName()));

        List<String> fileNames = new ArrayList<String>();
        fileNames.add("index.jsp");
        fileNames.add("form.jsp");
        
        for (String fileName : fileNames) {
            execTemplateToFile(context, "/jsp/" + fileName + ".vm", new File(dir, fileName));
        }
        
        System.out.println("生成JSP完成");
    }

    private void execTemplateToFile(VelocityContext context, String tplName, File file) {
        Template template = engine.getTemplate(tplName, "UTF-8");
        file.getParentFile().mkdirs();
        Writer writer;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(file));
            template.merge(context, writer);
            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
