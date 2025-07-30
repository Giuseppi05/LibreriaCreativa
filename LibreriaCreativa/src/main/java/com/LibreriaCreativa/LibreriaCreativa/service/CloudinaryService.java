package com.LibreriaCreativa.LibreriaCreativa.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Autowired
    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        Map<String, Object> options = new HashMap<>();
        options.put("folder", folderName); // carpeta configurable

        Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), options);
        return result.get("secure_url").toString();
    }
    
    public String uploadBytesAsRaw(byte[] bytes, String folderName, String fileName) throws IOException {
        Map<String, Object> options = new HashMap<>();
        options.put("resource_type", "raw");
        options.put("folder", folderName);
        
        // Extraer nombre sin extensión para el public_id
        String nameWithoutExt = fileName.contains(".") ? 
            fileName.substring(0, fileName.lastIndexOf(".")) : fileName;
        
        options.put("public_id", nameWithoutExt);
        
        // Configurar el tipo de contenido para PDFs
        options.put("content_type", "application/pdf");
        
        // Permitir que Cloudinary maneje la extensión
        options.put("use_filename", true);
        options.put("unique_filename", false);

        @SuppressWarnings("unchecked")
        Map<String, Object> result = cloudinary.uploader().upload(bytes, options);
        
        // Construir URL con extensión explícita
        String baseUrl = result.get("secure_url").toString();
        
        // Si la URL no termina en .pdf, agregarla
        if (!baseUrl.endsWith(".pdf")) {
            // Cloudinary permite agregar extensiones a las URLs de archivos raw
            baseUrl = baseUrl + ".pdf";
        }
        
        return baseUrl;
    }

    public String uploadPdfBytes(byte[] pdfBytes, String folderName, String fileName) throws IOException {
        Map<String, Object> options = new HashMap<>();
        options.put("resource_type", "raw");
        options.put("folder", folderName);
        
        options.put("type", "upload");
        options.put("access_mode", "public");
        
        if (!fileName.toLowerCase().endsWith(".pdf")) {
            fileName = fileName + ".pdf";
        }
        
        String publicId = fileName.substring(0, fileName.lastIndexOf("."));
        options.put("public_id", publicId);
        
        options.put("format", "pdf");
        options.put("content_type", "application/pdf");

        @SuppressWarnings("unchecked")
        Map<String, Object> result = cloudinary.uploader().upload(pdfBytes, options);
        
        String url = result.get("secure_url").toString();
        
        if (!url.contains(".pdf")) {
            url = url + ".pdf";
        }
        
        return url;
    }
    
    
    /**
     * (Opcional) Borra la imagen de Cloudinary dado su public_id.
     */
    public void deleteFile(String publicId) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    public String getPublicId(String imageUrl) {
        try {
            String[] parts = imageUrl.split("/");
            String fileWithExt = parts[parts.length - 1];
            String fileName = fileWithExt.split("\\.")[0];
            String folder = parts[parts.length - 2];
            return folder + "/" + fileName;
        } catch (Exception e) {
            return null;
        }
    }
}
