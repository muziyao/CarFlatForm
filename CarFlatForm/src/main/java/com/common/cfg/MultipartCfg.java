//package com.common.cfg;
//
//import java.io.File;
//
//import javax.servlet.MultipartConfigElement;
//
//import org.springframework.boot.web.servlet.MultipartConfigFactory;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.smokeroom.entity.GlobalParam;
//
//@Configuration
//public class MultipartCfg {
//	
//	@Bean
//    public MultipartConfigElement multipartConfigElement() {
//        MultipartConfigFactory factory = new MultipartConfigFactory();
//        factory.setMaxFileSize("2048000KB");
//        factory.setMaxRequestSize("2048000KB");
//        String location = GlobalParam.temp_file;
//        System.err.println("location=="+location);
//        File tmpFile = new File(location);
//        if (!tmpFile.exists()) {
//            tmpFile.mkdirs();
//        }
//        factory.setLocation(location);
//        return factory.createMultipartConfig();
//    } 
//}
