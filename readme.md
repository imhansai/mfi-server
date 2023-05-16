# MFi 获取 token 相关数据

## 介绍
这是使用 springboot 搭建的一个简易服务，主要用于获取 MFi token 相关数据，供工厂烧录到产品。
> 果子官网: https://mfi.apple.com/ 

## 技术相关
- springboot 
- jdk HttpClient 用于发送请求、处理响应
- jackson-dataformat-csv 用于处理 csv 文件
- poi-ooxml 导出 excel 文件

## 如何使用
1. 下载源码到任意IDE中
2. 添加 application-dev.properties 文件，配置相关参数(见 dev.fromnowon.mfiserver.config.MfiProperties)
3. 在 resources 资源目录下创建 mfi 目录，将 mfi.jks 和 mfi.pem 放到这里
4. 运行 MfiServerApplication.java 启动服务

## [LICENSE](LICENSE)