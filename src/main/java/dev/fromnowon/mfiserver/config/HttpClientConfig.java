package dev.fromnowon.mfiserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ResourceUtils;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * 注入 HttpClient
 *
 * @author hansai
 */
@Configuration
public class HttpClientConfig {

    @Value("classpath:mfi/mfi.pem")
    private Resource serverCertificateResource;

    private final MfiProperties mfiProperties;

    private final ResourceLoader resourceLoader;

    public HttpClientConfig(MfiProperties mfiProperties,
                            ResourceLoader resourceLoader) {
        this.mfiProperties = mfiProperties;
        this.resourceLoader = resourceLoader;
    }

    @Bean
    public HttpClient httpClient() {
        HttpClient httpClient;
        try {
            httpClient = getHttpClient();
        } catch (Exception e) {
            throw new RuntimeException("构造 HttpClient 异常! " + e.getMessage(), e);
        }
        return httpClient;
    }

    private HttpClient getHttpClient() throws KeyStoreException,
            IOException,
            UnrecoverableKeyException,
            NoSuchAlgorithmException,
            CertificateException,
            KeyManagementException {
        // 客户端证书相关信息
        InputStream keyStoreInputStream = resourceLoader.getResource(ResourceUtils.CLASSPATH_URL_PREFIX + "mfi/mfi.jks").getInputStream();
        InputStream serverCertificateInputStream = serverCertificateResource.getInputStream();
        String keystorePassword = mfiProperties.getKeystorePassword();

        // 构造 KeyStore 对象并加载客户端证书
        KeyStore keyStore = KeyStore.getInstance("JKS");
        keyStore.load(keyStoreInputStream, keystorePassword.toCharArray());

        // 获取私钥
        String alias = mfiProperties.getAlias();
        Key privateKey = keyStore.getKey(alias, keystorePassword.toCharArray());

        // 从 .pem 文件中加载服务端颁发的证书
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        X509Certificate serverCertificate = (X509Certificate) certificateFactory.generateCertificate(serverCertificateInputStream);

        // 创建一个包含私钥和证书的新的 KeyStore
        KeyStore newKeyStore = KeyStore.getInstance("JKS");
        newKeyStore.load(null, null);
        // 设置私钥条目
        newKeyStore.setKeyEntry(alias, privateKey, keystorePassword.toCharArray(), new Certificate[]{serverCertificate});

        // 构造 KeyManagerFactory 并初始化 KeyStore
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(newKeyStore, keystorePassword.toCharArray());

        // 构造 SSLContext 并初始化 KeyManagerFactory
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), null, null);

        // 构造 HttpClient
        return HttpClient.newBuilder()
                .sslContext(sslContext)
                .build();
    }

}
