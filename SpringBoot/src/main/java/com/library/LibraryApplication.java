package com.library;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.library.mapper")
public class LibraryApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibraryApplication.class, args);
		System.out.println("========================================");
		System.out.println("  图书管理系统启动成功！");
		System.out.println("  后端地址: http://localhost:8088/api");
		System.out.println("========================================");
	}
}