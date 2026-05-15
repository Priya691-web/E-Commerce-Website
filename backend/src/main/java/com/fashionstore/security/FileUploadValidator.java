package com.fashionstore.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * File Upload Security Validator
 * Validates file uploads for security threats
 */
public class FileUploadValidator {
    private static final Logger logger = LoggerFactory.getLogger(FileUploadValidator.class);

    // Maximum file size: 10MB
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    // Allowed image file types
    private static final Set<String> ALLOWED_IMAGE_TYPES = new HashSet<>(Arrays.asList(
        "image/jpeg",
        "image/jpg",
        "image/png",
        "image/gif",
        "image/webp"
    ));
    
    // Allowed image extensions
    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = new HashSet<>(Arrays.asList(
        ".jpg", ".jpeg", ".png", ".gif", ".webp"
    ));
    
    // Allowed document file types
    private static final Set<String> ALLOWED_DOCUMENT_TYPES = new HashSet<>(Arrays.asList(
        "application/pdf",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
    ));
    
    // Allowed document extensions
    private static final Set<String> ALLOWED_DOCUMENT_EXTENSIONS = new HashSet<>(Arrays.asList(
        ".pdf", ".doc", ".docx"
    ));
    
    // Blocked file signatures (magic numbers)
    private static final byte[][] BLOCKED_SIGNATURES = {
        new byte[] {0x4D, 0x5A}, // MZ - EXE
        new byte[] {0x50, 0x4B}, // PK - ZIP (can contain executables)
        new byte[] {(byte) 0x7F, 0x45, 0x4C, 0x46}, // ELF
        new byte[] {(byte) 0xCA, (byte) 0xFE, (byte) 0xBA, (byte) 0xBE}, // Mach-O
        new byte[] {0x25, 0x50, 0x44, 0x46} // %PDF - PDF (if not in allowed list)
    };
    
    /**
     * Validate uploaded file
     */
    public static ValidationResult validateFile(Part part, UploadType uploadType) {
        ValidationResult result = new ValidationResult();
        
        try {
            // Check if file part exists
            if (part == null || part.getSize() == 0) {
                result.addError("No file provided");
                return result;
            }
            
            // Check file size
            if (part.getSize() > MAX_FILE_SIZE) {
                result.addError("File size exceeds maximum limit of 10MB");
                return result;
            }
            
            // Get file name
            String fileName = part.getSubmittedFileName();
            if (fileName == null || fileName.isEmpty()) {
                result.addError("Invalid file name");
                return result;
            }
            
            // Sanitize file name
            String sanitizedFileName = InputValidator.sanitizeFileName(fileName);
            if (!sanitizedFileName.equals(fileName)) {
                result.addError("File name contains invalid characters");
                return result;
            }
            
            // Check for double extensions
            if (hasDoubleExtension(fileName)) {
                result.addError("File has double extension");
                return result;
            }
            
            // Check file extension
            String fileExtension = getFileExtension(fileName).toLowerCase();
            Set<String> allowedExtensions = uploadType == UploadType.IMAGE 
                ? ALLOWED_IMAGE_EXTENSIONS 
                : ALLOWED_DOCUMENT_EXTENSIONS;
            
            if (!allowedExtensions.contains(fileExtension)) {
                result.addError("File type not allowed. Allowed types: " + allowedExtensions);
                return result;
            }
            
            // Check content type
            String contentType = part.getContentType();
            if (contentType == null || contentType.isEmpty()) {
                result.addError("Content type not provided");
                return result;
            }
            
            Set<String> allowedTypes = uploadType == UploadType.IMAGE 
                ? ALLOWED_IMAGE_TYPES 
                : ALLOWED_DOCUMENT_TYPES;
            
            if (!allowedTypes.contains(contentType.toLowerCase())) {
                result.addError("Content type not allowed: " + contentType);
                return result;
            }
            
            // Validate file signature (magic number)
            if (!validateFileSignature(part, uploadType)) {
                result.addError("File signature does not match declared type");
                return result;
            }
            
            // Check for embedded scripts in images
            if (uploadType == UploadType.IMAGE && containsEmbeddedScripts(part)) {
                result.addError("File contains embedded scripts");
                return result;
            }
            
            result.setValid(true);
            result.setSanitizedFileName(sanitizedFileName);
            
        } catch (Exception e) {
            logger.error("Error validating file upload: {}", e.getMessage(), e);
            result.addError("Error validating file");
        }
        
        return result;
    }
    
    /**
     * Validate file signature (magic number)
     */
    private static boolean validateFileSignature(Part part, UploadType uploadType) {
        try (InputStream inputStream = part.getInputStream()) {
            byte[] header = new byte[12];
            int bytesRead = inputStream.read(header);
            
            if (bytesRead < 4) {
                return false;
            }
            
            // Check for blocked signatures
            for (byte[] blocked : BLOCKED_SIGNATURES) {
                if (bytesRead >= blocked.length && startsWith(header, blocked)) {
                    logger.warn("Blocked file signature detected");
                    return false;
                }
            }
            
            // Validate image signatures
            if (uploadType == UploadType.IMAGE) {
                return isValidImageSignature(header, bytesRead);
            }
            
            return true;
            
        } catch (IOException e) {
            logger.error("Error reading file signature: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Check if byte array starts with given prefix
     */
    private static boolean startsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (data[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Check if file has valid image signature
     */
    private static boolean isValidImageSignature(byte[] header, int bytesRead) {
        // JPEG: FF D8 FF
        if (bytesRead >= 3 && 
            (header[0] & 0xFF) == 0xFF && 
            (header[1] & 0xFF) == 0xD8 && 
            (header[2] & 0xFF) == 0xFF) {
            return true;
        }
        
        // PNG: 89 50 4E 47
        if (bytesRead >= 4 && 
            header[0] == 0x89 && 
            header[1] == 0x50 && 
            header[2] == 0x4E && 
            header[3] == 0x47) {
            return true;
        }
        
        // GIF: GIF8
        if (bytesRead >= 4 && 
            header[0] == 'G' && 
            header[1] == 'I' && 
            header[2] == 'F' && 
            header[3] == '8') {
            return true;
        }
        
        // WebP: RIFF .... WEBP
        if (bytesRead >= 12 && 
            header[0] == 'R' && 
            header[1] == 'I' && 
            header[2] == 'F' && 
            header[3] == 'F' &&
            header[8] == 'W' && 
            header[9] == 'E' && 
            header[10] == 'B' && 
            header[11] == 'P') {
            return true;
        }
        
        return false;
    }
    
    /**
     * Check for embedded scripts in images
     */
    private static boolean containsEmbeddedScripts(Part part) {
        try (InputStream inputStream = part.getInputStream()) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            StringBuilder content = new StringBuilder();
            
            // Read first 8KB to check for script patterns
            bytesRead = inputStream.read(buffer);
            if (bytesRead > 0) {
                content.append(new String(buffer, 0, bytesRead, java.nio.charset.StandardCharsets.ISO_8859_1));
            }
            
            String fileContent = content.toString().toLowerCase();
            
            // Check for script patterns
            if (fileContent.contains("<script") || 
                fileContent.contains("javascript:") || 
                fileContent.contains("vbscript:") ||
                fileContent.contains("<?php") ||
                fileContent.contains("<%")) {
                logger.warn("Embedded script detected in image file");
                return true;
            }
            
            return false;
            
        } catch (IOException e) {
            logger.error("Error checking for embedded scripts: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Check for double extension
     */
    private static boolean hasDoubleExtension(String fileName) {
        int dotCount = 0;
        for (char c : fileName.toCharArray()) {
            if (c == '.') {
                dotCount++;
            }
        }
        return dotCount > 1;
    }
    
    /**
     * Get file extension
     */
    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex);
        }
        return "";
    }
    
    /**
     * Upload type enum
     */
    public enum UploadType {
        IMAGE,
        DOCUMENT
    }
    
    /**
     * Validation result class
     */
    public static class ValidationResult {
        private boolean valid;
        private String sanitizedFileName;
        private final java.util.List<String> errors = new java.util.ArrayList<>();
        
        public boolean isValid() {
            return valid;
        }
        
        public void setValid(boolean valid) {
            this.valid = valid;
        }
        
        public String getSanitizedFileName() {
            return sanitizedFileName;
        }
        
        public void setSanitizedFileName(String sanitizedFileName) {
            this.sanitizedFileName = sanitizedFileName;
        }
        
        public java.util.List<String> getErrors() {
            return errors;
        }
        
        public void addError(String error) {
            this.errors.add(error);
        }
        
        public boolean hasErrors() {
            return !errors.isEmpty();
        }
        
        public String getErrorMessage() {
            return String.join(", ", errors);
        }
    }
}
