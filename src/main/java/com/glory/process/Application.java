package com.glory.process;



import com.glory.process.conf.UserAuthConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Created by CDZ on 2018/11/22.
 */
@SpringBootApplication
@MapperScan(basePackages = "com.glory.process.mapper")
//重点
@ServletComponentScan
public class Application extends WebMvcConfigurerAdapter {

    private static Logger logger = LoggerFactory.getLogger(Application.class);
    /**
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        logger.debug("启动成功");
    }
    /**
     * 跨域问题
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").
                allowedMethods("DELETE", "POST", "GET", "PUT")
                .allowedOrigins("*");
    }

    @Bean
    UserAuthConfig getUserAuthConfig(){
        return new UserAuthConfig();
    }

}