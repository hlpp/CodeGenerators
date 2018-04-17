package hulang1024.github.io.generator;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import org.apache.commons.lang.SystemUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import hulang1024.github.io.generator.db.Column;
import hulang1024.github.io.generator.tpldata.MClass;
import hulang1024.github.io.generator.tpldata.MPackage;
import hulang1024.github.io.generator.tpldata.Table;
import hulang1024.github.io.generator.tpldata.TableHeadDataOption;
import hulang1024.github.io.generator.utils.StrUtils;

public class MVCCodeGenerator {
    private ConfigManager config;
    
    private static ApplicationContext applicationContext = null;  
    private static JdbcTemplate jdbcTemplate = null;  
    
    private VelocityEngine engine;
    
    
    static {
        applicationContext = new ClassPathXmlApplicationContext("application.xml");  
        jdbcTemplate = (JdbcTemplate) applicationContext.getBean("jdbcTemplate");  
    }
    
    @SuppressWarnings("resource")
    public void doConfig(String path) {
        List<String> warnings = new ArrayList<String>();
        MyBatisGenerator generator;
        try {
            generator = new MyBatisGenerator(
                new ConfigurationParser(warnings).parseConfiguration(new File(MVCCodeGenerator.class.getResource("/mybaits-generator.xml").getPath())),
                new DefaultShellCallback(true),
                warnings);
            generator.generate(null);
            for (String s : warnings) {
                System.out.println(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("model生成完成, 请刷新目录（并确保已编译）后回车继续");
        new Scanner(System.in).nextLine();
        System.out.println("继续...");

        config = new ConfigManager(getClass().getResource("/config.properties").getPath() + path);
        config.set("mybaits-generator-config-path", getClass().getResource("/mybaits-generator.xml").getPath());

        VelocityContext context = new VelocityContext();

        initContext(context);
        
        generate(context);
    }

    private void initContext(VelocityContext context) {
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
        String tableName = tableElem.attribute("tableName").getValue();
        String objectName = tableElem.attribute("domainObjectName").getValue();
        String modelTargetProject = modelElem.attribute("targetProject").getValue();
        context.put("modelTargetProject", modelTargetProject);
        
        Class<?> poClass = null;
        try {
            URL[] urls = new URL[]{new URL("file:" + SystemUtils.FILE_SEPARATOR + config.get("classesDir"))};
            @SuppressWarnings("resource")
            ClassLoader loader = new URLClassLoader(urls);
            poClass = loader.loadClass(modelTagetPackage + "." + objectName);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        
        //mysql
        List<Column> columnInfos = jdbcTemplate.query(
            String.format("select column_name name, column_comment comment, column_key `key`"
                + " from information_schema.columns where table_name = '%s' and table_schema = '%s'", tableName, config.get("databaseName")),
            new BeanPropertyRowMapper<Column>(Column.class));
        if (columnInfos.isEmpty()) {
            throw new RuntimeException("查询表列信息失败");
        }
        
        // 查询表主键
        Column primaryKeyColumn = null;
        for (Column col : columnInfos) {
            if ("PRI".equals(col.getKey())) {
                primaryKeyColumn = col;
                break;
            }
        }
        String primaryFieldVarName = NameUtils.underlineToCamelCase(primaryKeyColumn.getName());
        context.put("primaryFieldVarName", primaryFieldVarName);
        context.put("primaryGetterName", "get" + StrUtils.firstLetterUpper(primaryFieldVarName));
        context.put("primaryFieldJavaType", "Integer");//FIXME
        
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
        

        initTableToContext(columnInfos, tableName, context);
    }

    private void initTableToContext(List<Column> columnInfos, String tableName, VelocityContext context) {
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
        
        // 注释转换为标题名
        Map<String, String> columnTitleMap = new HashMap<String, String>();
        String terminators = "：:,-. ";
        for (Column col : columnInfos) {
            String title = col.getComment();
            for (int i = 0; i < terminators.length(); i++) {
                char t = terminators.charAt(i);
                if (title.indexOf(t) != -1) {
                    title = title.substring(0, title.indexOf(t));
                    break;
                }
            }
            columnTitleMap.put(NameUtils.underlineToCamelCase(col.getName()), title);
        }
        
        for (String fieldName : fieldNames) {
            TableHeadDataOption opt = new TableHeadDataOption();
            opt.setField(fieldName);
            ///opt.setWidth(avgWidthPercent + "%");
            opt.setWidth("100");
            opt.setAlign("center");
            opt.setTitle(columnTitleMap.containsKey(fieldName) ? columnTitleMap.get(fieldName) : fieldName);
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
        File dir = new File(config.get("javaOutputDir") + packageDir);
        File file = new File(dir, ((MClass)context.get("controllerClass")).getSimpleName() + ".java");
        execTemplateToFile(context, "/java/Controller.vm", file);
        System.out.println("生成Controller完成");
    }
    
    private void genService(VelocityContext context) {
        String packageDir = ((MClass)context.get("serviceClass")).getPackage().getName().replaceAll("\\.", "/");
        File dir = new File(config.get("javaOutputDir") + packageDir);
        File file = new File(dir, ((MClass)context.get("serviceClass")).getSimpleName() + ".java");
        execTemplateToFile(context, "/java/Service.vm", file);
        System.out.println("生成Service完成");
    }
    
    private void genJSs(VelocityContext context) {
        File dir = new File(config.get("jsOutputDir")
            + StrUtils.firstLetterLower(((Class)context.get("poClass")).getSimpleName()));
        File file = new File(dir, "index.js");
        execTemplateToFile(context, "/js/index.js.vm", file);
        System.out.println("生成JS完成");
    }

    private void genJSPs(VelocityContext context) {
        File dir = new File(config.get("jspOutputDir")
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
