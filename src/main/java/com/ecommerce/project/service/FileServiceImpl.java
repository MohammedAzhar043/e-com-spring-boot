package com.ecommerce.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService{

    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {

        //File names of the current file/original file
        String orignalFileName = file.getOriginalFilename();
        //renaming the file / generate a unique file name
        String randomId = UUID.randomUUID().toString();
        String fileName = randomId.concat(orignalFileName.substring(orignalFileName.lastIndexOf('.')));

        String filePath = path + File.separator + fileName;
        //check if path exist and create

        File folder = new File(path);
        if(!folder.exists()){
            folder.mkdirs();
        }
        //upload to server

        Files.copy(file.getInputStream(), Paths.get(filePath));

        //returning file name
        return fileName;
    }
}
