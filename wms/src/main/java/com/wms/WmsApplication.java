package com.wms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.oas.annotations.EnableOpenApi;

// 启用 Swagger 文档（可选，SpringFox 3.0.0 可省略，但加上更明确）
@EnableOpenApi
@SpringBootApplication
@MapperScan("com.wms.mapper")  // 扫描 Mapper 接口所在包
public class WmsApplication {
	public static void main(String[] args) {
		SpringApplication.run(WmsApplication.class, args);
	}
}