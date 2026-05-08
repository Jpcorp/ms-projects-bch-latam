package cl.bch.cloud.ms.fglosas.mantenedor.utils;

import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import static org.junit.jupiter.api.Assertions.*;

class CustomMultipartFileUtilsTest {

    private final byte[] content = "contenido de prueba".getBytes();
    private final String name = "archivo";
    private final String originalFilename = "archivo.txt";
    private final String contentType = "text/plain";

    private final MultipartFile file = new CustomMultipartFileUtils(content, name, originalFilename, contentType);

    @Test
    void testGetName() {
        assertEquals(name, file.getName());
    }

    @Test
    void testGetOriginalFilename() {
        assertEquals(originalFilename, file.getOriginalFilename());
    }

    @Test
    void testGetContentType() {
        assertEquals(contentType, file.getContentType());
    }

    @Test
    void testIsEmpty_false() {
        assertFalse(file.isEmpty());
    }

    @Test
    void testGetSize() {
        assertEquals(content.length, file.getSize());
    }

    @Test
    void testGetBytes() throws IOException {
        assertArrayEquals(content, file.getBytes());
    }

    @Test
    void testGetInputStream() throws IOException {
        InputStream inputStream = file.getInputStream();
        assertNotNull(inputStream);
        assertEquals(content.length, inputStream.available());
    }

    @Test
    void testTransferTo() throws IOException {
        File tempFile = File.createTempFile("test", ".txt");
        file.transferTo(tempFile);

        byte[] fileContent = new byte[(int) tempFile.length()];
        try (FileInputStream fis = new FileInputStream(tempFile)) {
            fis.read(fileContent);
        }

        assertArrayEquals(content, fileContent);
        assertTrue(tempFile.delete()); // Limpieza
    }
}