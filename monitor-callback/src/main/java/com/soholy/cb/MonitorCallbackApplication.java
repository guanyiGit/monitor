package com.soholy.cb;

import com.soholy.cb.config.DruidConfig;
import lombok.extern.java.Log;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Import({DruidConfig.class})
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.soholy.cb.dao*")
@Log
@EnableJms
public class MonitorCallbackApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitorCallbackApplication.class, args);
        log.info("===============================  启动完成  ===============================");
    }

}
