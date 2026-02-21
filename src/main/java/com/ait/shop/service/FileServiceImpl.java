package com.ait.shop.service;

import com.ait.shop.config.DOProperties;
import com.ait.shop.exceptions.types.FileUploadException;
import com.ait.shop.service.interfaces.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private final DOProperties properties;
    private final S3Client client;

    public FileServiceImpl(DOProperties properties, S3Client client) {
        this.properties = properties;
        this.client = client;
    }

    @Override
    public String uploadAndGetUrl(MultipartFile file) throws IOException {
        //Логика метода:
        //1. Необходимые проверки
        Objects.requireNonNull(file, "Multipart file cannot be null");

        if (file.isEmpty()) {
            throw new FileUploadException("File is empty");
        }

        if (file.getContentType() == null || !file.getContentType().startsWith("image")) {
            throw new FileUploadException("File is not an image");
        }

        //2. Сгенерировать уникальное имя файла
        String uniqueFileName = generateUniqueFileName(file);

        //3. Создать запрос на загрузку файла в облако под уникальным именем
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(uniqueFileName)
                .contentType(file.getContentType())
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        RequestBody requestBody = RequestBody.fromInputStream(file.getInputStream(), file.getSize());

        //4. Отправка запроса (фактическая загрузка файла в облако)
        client.putObject(request, requestBody);

        //5. Отправляем еще запрос с целью получить ссылку на загруженный файл
        //6. Возвращаем ссылку в качестве результата работы метода
        return client.utilities().getUrl(
                x -> x.bucket(properties.getBucket()).key(uniqueFileName)
        ).toString();
    }

    private String generateUniqueFileName(MultipartFile file) {
        //Генерируем случайный текст - ikhefhef4
        String randomPart = UUID.randomUUID().toString();
        String fileName = file.getOriginalFilename();

        //Какие есть варианты имени файла, которые придут на вход:
        //1. Файл пришел вообще без имени -> ikhefhef4
        if (fileName == null) {
            return randomPart;
        }

        //FAt LaZy caT.jpeg -> fat-lazy-cat.jpeg
        String normalizedFileName = fileName.trim().replace(" ", "-").toLowerCase();

        //Чтобы определить, где расширение - ищем точку с конца строки
        //fat.lazy.cat.jpeg
        //cat -> -1
        //cat.jpeg -> 3
        int dotIndex = normalizedFileName.lastIndexOf(".");

        //2. Файл пришел с именем, но вообще без расширения - cat - cat-ikhefhef4
        if (dotIndex == -1) {
            return String.format("%s-%s", normalizedFileName, randomPart);
        }

        //3. Файл пришел с именем и расширением - cat.jpeg -> cat-ikhefhef4.jpeg
        //cat.jpeg -> cat
        String fileNameWithoutExtension = normalizedFileName.substring(0, dotIndex);

        //cat.jpeg -> .jpeg
        String extension = normalizedFileName.substring(dotIndex);

        //cat.jpeg -> cat-ikhefhef4.jpeg
        return String.format("%s-%s%s", fileNameWithoutExtension, randomPart, extension);
    }
}
