package com.soholy.cb.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.soholy.cb.common.DynamicDataSource;
import com.soholy.cb.common.DynamicDataSourceHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DruidConfig {

    @Value("${spring.wifiDataSource.username}")
    private String wifiDbUName;

    @Value("${spring.wifiDataSource.password}")
    private String wifiDbPwd;

    @Value("${spring.wifiDataSource.url}")
    private String wifiDbUrl;


    @ConfigurationProperties(prefix = "spring.datasource")
    @Bean
    public DataSource defaultDataSource() {
        return new DruidDataSource();
    }

    @Bean
    public DataSource wifiDataSource(@Qualifier("defaultDataSource") DataSource defaultDataSource) {
        DruidDataSource dataSource = new DruidDataSource();
        BeanUtils.copyProperties(defaultDataSource, dataSource);
        dataSource.setUsername(wifiDbUName);
        dataSource.setPassword(wifiDbPwd);
        dataSource.setUrl(wifiDbUrl);
        return dataSource;
    }


    @Bean
    @Primary
//    @DependsOn({"defaultDataSource", "wifiDataSource"})
    public DynamicDataSource dynamicDataSource(
            @Qualifier("defaultDataSource") @Lazy DataSource defaultDataSource,
            @Qualifier("wifiDataSource") @Lazy DataSource wifiDataSource) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DynamicDataSourceHolder.DbType.DEFAUALT.getDbType(), defaultDataSource);
        targetDataSources.put(DynamicDataSourceHolder.DbType.WIFI.getDbType(), wifiDataSource);

        DynamicDataSource dataSource = new DynamicDataSource();
        // 该方法是AbstractRoutingDataSource的方法
        dataSource.setTargetDataSources(targetDataSources);
        // 默认的datasource设置为myTestDbDataSource
        dataSource.setDefaultTargetDataSource(defaultDataSource);

        return dataSource;
    }


    /**
     * 根据数据源创建SqlSessionFactory
     */
//    @Bean
//    public SqlSessionFactory sqlSessionFactory( @Qualifier("dataSource")DynamicDataSource dynamicDataSource) throws Exception {
//        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
//        // 指定数据源(这个必须有，否则报错)
//        bean.setDataSource(dynamicDataSource);
//        //private Environment env;
//        // 下边两句仅仅用于*.xml文件，如果整个持久层操作不需要使用到xml文件的话（只用注解就可以搞定），则不加
////        fb.setTypeAliasesPackage(env.getProperty("mybatis.typeAliasesPackage"));// 指定基包
////        fb.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(env.getProperty("mybatis.mapperLocations")));
//        return bean.getObject();
//    }

//    /**
//     * 配置事务管理器
//     */
//    @Bean
//    public DataSourceTransactionManager transactionManager(DynamicDataSource dynamicDataSource) throws Exception {
//        return new DataSourceTransactionManager(dynamicDataSource);
//    }
    @Bean
    public ServletRegistrationBean druidServlet() {
        ServletRegistrationBean bean = new ServletRegistrationBean(new StatViewServlet(), "/druid/*");
        Map<String, String> map = new HashMap<>();
        map.put("loginUsername", "admin");
        map.put("loginPassword", "admin");
        map.put("allow", "localhost");
        bean.setInitParameters(map);
        return bean;
    }

    @Bean
    public FilterRegistrationBean webStatFilter() {
        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new WebStatFilter());
        Map<String, String> map = new HashMap<>();
        map.put("exclusions", "*.js,*.css,/durid/*");
        bean.setInitParameters(map);
        bean.setUrlPatterns(Arrays.asList("/*"));
        return bean;
    }
}
