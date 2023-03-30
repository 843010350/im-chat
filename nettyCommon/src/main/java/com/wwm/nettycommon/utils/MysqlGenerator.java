package com.wwm.nettycommon.utils;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.rules.DbColumnType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

public class MysqlGenerator {

    private static final String[]  tables =  new String[] { "im_group_user" };
    private static final String prefix = "im_";
    private static final String mapper = "com.wwm.nettycommon.mapper";
    private static final String xml = "resource.xml";
    private static final String service = "com.wwm.nettycommon.service";
    private static final String serviceImpl = "com.wwm.nettycommon.service.impl";
    private static final String entity = "com.wwm.nettycommon.entity";
    private static final String auth = "";


    public static void main(String[] args) {
        AutoGenerator mpg = new AutoGenerator();

        // 全局配置
        GlobalConfig gc = new GlobalConfig();

        gc.setOutputDir("/Users/wangwenming/IdeaProjects/openSource/mynetty/nettyCommon/src/main/java");
        gc.setFileOverride(false);
        gc.setActiveRecord(true);
        // XML 二级缓存
        gc.setEnableCache(false);
        // XML ResultMap
        gc.setBaseResultMap(true);
        // XML columList
        gc.setBaseColumnList(true);
        gc.setAuthor(auth);

        // 自定义文件命名，注意 %s 会自动填充表实体属性！
        gc.setMapperName("%sMapper");
        gc.setXmlName("%sMapper");
        gc.setServiceName("%sService");
        gc.setServiceImplName("%sServiceImpl");
        gc.setAuthor(auth);
        mpg.setGlobalConfig(gc);

        // 数据源配置
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setDbType(DbType.MYSQL);
       /* dsc.setTypeConvert(new MySqlTypeConvert(){
            // 自定义数据库表字段类型转换【可选】
            @Override
            public DbColumnType processTypeConvert(String fieldType) {
                System.out.println("转换类型：" + fieldType);
                return super.processTypeConvert(fieldType);
            }
        });*/


        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUrl("jdbc:mysql://localhost:3306/netty_chat?useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useAffectedRows=true&useServerPrepStmts=false&rewriteBatchedStatements=true&serverTimezone=Asia/Shanghai&allowMultiQueries=true");
        dsc.setUsername("root");
        //dsc.setPassword("8#sOM6putB3HUy1Z")
        dsc.setPassword("12345678");
        mpg.setDataSource(dsc);

        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        strategy.setCapitalMode(true);// 全局大写命名 ORACLE 注意
        // 此处可以修改为您的表前缀

        strategy.setTablePrefix(new String[]{prefix});
        strategy.setEntityLombokModel(true);

        // 表名生成策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        // 需要生成的表
        strategy.setInclude(tables);
        // strategy.setExclude(new String[]{"test"}); // 排除生成的表
        mpg.setStrategy(strategy);

        // 包配置
        PackageConfig pc = new PackageConfig();
        pc.setParent("");
        pc.setModuleName("");
        pc.setMapper(mapper);
        pc.setXml(xml);
        pc.setEntity(entity);
        pc.setService(service);
        pc.setServiceImpl(serviceImpl);
        mpg.setPackageInfo(pc);

        TemplateConfig tc = new TemplateConfig();
        tc.setController(null);
        mpg.setTemplate(tc);

        // 执行生成
        mpg.execute();
    }

}
