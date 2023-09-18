package com.proyectozoo.zoo.service.impl;

import com.proyectozoo.zoo.components.MessageComponent;
import com.proyectozoo.zoo.service.IUploadFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class UploadFilesServiceImpl implements IUploadFileService {
    @Autowired
    private MessageComponent message;
    /**
     * Este metodo permite subir una imagen al servidor
     * @param file es la imagen que se va a subir
     * @param directory es el directorio al que se va a subir
     * @return un mensaje indicando si se ha subido la imagen correctamente o no
     */
    public String subirImagen(MultipartFile file, String directory) {
        try{
            //obtenemos un random uuid para la foto
            String fileName = UUID.randomUUID().toString();
            byte[] bytes = file.getBytes();
            //obtenemos el nombre original de la foto
            String fileOriginalnName = file.getOriginalFilename();

            //comprobamos que el tamaño del archivo sea el correcto
            long fileSize = file.getSize();
            long maxSize = 5 * 1024 * 1024;
            if(fileSize > maxSize){
                return message.getMessage("error.foto.tamano");
            }
            if(!file.getOriginalFilename().endsWith(".jpg") && !file.getOriginalFilename().endsWith(".jpeg") && !file.getOriginalFilename().endsWith(".png")){
                return message.getMessage("error.foto.formato");
            }
            //creamos el nombre del archivo
            String fileExtension = fileOriginalnName.substring(fileOriginalnName.lastIndexOf("."));
            String newFileName = fileName + fileExtension;
            //creamos la carpeta y comprobamos que existe
            File folder = new File(directory);
            if(!folder.exists()){
                folder.mkdirs();
            }
            Path path = Paths.get(folder + "//" + newFileName);
            Files.write(path,bytes);
            return String.valueOf(path);

        }catch (Exception exception){
            exception.printStackTrace();
        }
        return "Fallo";
    }
}
