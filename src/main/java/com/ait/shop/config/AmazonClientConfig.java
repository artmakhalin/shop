package com.ait.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class AmazonClientConfig {

    /*
    Пошаговый механизм работы спринга с нашими конфигами:
    1. Спринг видит @Configuration на нашем классе DOProperties.
    он создает объект этого класса и помещает его в Спринг контекст
    2. Спринг видит @ConfigurationProperties(prefix = "do") на нашем классе DOProperties
    и читает все переменные окружения с полями класса DOProperties
    3. Спринг сопоставляет имена переменных с полями класса DOProperties
    DO_SECRET_KEY -> secretKey ...
    4. Теперь в Спринг контексте лежит объект DOProperties со всеми пятью реквизитами доступа к бакету
    5. Спринг видит @Configuration на нашем классе AmazonClientConfig,
    он ищет в этом классе методы, помеченные @Bean
    6. Спринг сам запускает эти методы, передает в метод объект DOProperties из Спринг контекста,
    а тот объект, который метод вернул, Спринг делает бином и помещает в Спринг контекст

    То есть наша задача - правильно написать метод AmazonClient, который создает объект клиента
    с заложенными в него реквизитами доступа к бакету.
     */
    @Bean
    public S3Client amazonClient(DOProperties properties) {
        // Создаём специальный объект, который содержит оба ключа доступа к бакету.
        String accessKey = properties.getAccessKey();
        String secretKey = properties.getSecretKey();
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        // Создаём провайдер ключей (объект, который управляет ключами доступа)
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

        // Получим из объекта настроек регион (датацентр) и эндпоинт
        String region = properties.getRegion();
        String endpoint = properties.getEndpoint();

        // Создаём объект региона из строкового имени региона
        Region regionInstance = Region.of(region);

        // Создаём URI для эндпоинта
        URI endpointUri = getEndpointUri(endpoint);

        return S3Client.builder()
                .endpointOverride(endpointUri)
                .region(regionInstance)
                .credentialsProvider(credentialsProvider)
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build()
                )
                .build();

        // Стандартный минимальный конфиг формирует URL бакета так:
        //https://shop-75-bucket.digitalocean.com - работает не всегда
    }

    private static URI getEndpointUri(String endpoint) {
        return URI.create(endpoint);
    }
}
