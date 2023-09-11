package com.proyectozoo.zoo.service;

import org.springframework.web.multipart.MultipartFile;

public interface IUploadFileService {
     String subirImagen(MultipartFile file, String directory);
}
