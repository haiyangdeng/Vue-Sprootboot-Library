# 图书馆管理系统（library-system）



## 本地快捷预览项目

第一步：新建数据库`springboot-vue`, 运行 sql 文件夹下的`Dump20260417.sql`,
第二步：进入SpringBoot文件夹，安装maven依赖，在application.properties中配置数据库及密码，运行成功会运行在`后端地址: http://localhost:8088/api`
第三步：进入vue文件夹，node24版本，执行`yarn`安装依赖，成功后执行`yarn dev`, `o + enter`,打开默认浏览器,可看到登录界面
第四步：测试账号在sys_user表中，管理员账号:admin, 密码:123456


## 主要技术

SpringBoot、Mybatis-Plus、MySQL、Vue3、ElementPlus等



## 主要功能

管理员模块：注册、登录、书籍管理、读者管理、借阅管理、借阅状态、修改个人信息、修改密码

读者模块：注册、登录、查询图书信息、借阅和归还图书、查看个人借阅记录、修改个人信息、修改密码



## 主要功能截图

### 登录



### 展示板页面

### 管理员界面

#### 图书管理

- 图书表格列表

- 添加图书

- 编辑图书

- 删除图书


#### 读者管理

- 读者管理的增删查改类似图书管理，不再赘述

  

#### 借阅管理

- 借阅记录查询


- 借阅记录编辑


  #### 借阅状态


### 读者界面

#### 读者信息

- 编辑个人信息



- 修改密码

  点击修改密码按钮，修改密码





#### 图书查询



#### 借阅图书





#### 归还图书



#### 借阅记录



#### 借阅状态




## 代码结构




### 后端

maven项目结构

```shell
library-serve
├─java
│  └─com
│      └─admin
│          └─library
│              ├─common			// 通用类
│              │  ├─base		// 基础类
│              │  └─config		// 配置类
│              ├─controller		// 控制层
│              ├─domain			// 实体类
│              ├─mapper			// 持久层
│              └─service		// 业务层
└─resources	// maven资源配置
```


## 数据库

