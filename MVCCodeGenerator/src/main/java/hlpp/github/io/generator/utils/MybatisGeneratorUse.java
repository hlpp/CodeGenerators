package hlpp.github.io.generator.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;

public class MybatisGeneratorUse {
    public static void main(String[] args) throws Exception {
        List<String> warnings = new ArrayList<String>();
        MyBatisGenerator generator = new MyBatisGenerator(
            new ConfigurationParser(warnings).parseConfiguration(new File(MybatisGeneratorUse.class.getResource("/mybaits-generator.xml").getPath())),
            new DefaultShellCallback(true),
            warnings);
        generator.generate(null);
        for (String s : warnings) {
            System.out.println(s);
        }
        System.out.println("MyBatisGenerator Done");
    }

}
