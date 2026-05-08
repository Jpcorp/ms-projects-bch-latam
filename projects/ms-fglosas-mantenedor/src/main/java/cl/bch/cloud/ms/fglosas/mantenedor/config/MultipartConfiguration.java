package cl.bch.cloud.ms.fglosas.mantenedor.config;

import feign.codec.Encoder;
import feign.form.ContentType;
import feign.form.FormEncoder;
import feign.form.MultipartFormContentProcessor;
import feign.form.spring.SpringManyMultipartFilesWriter;
import feign.form.spring.SpringSingleMultipartFileWriter;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.JsonFormWriter;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(JsonFormWriter.class)
public class MultipartConfiguration {

    @Bean
    public Encoder feignEncoder(ObjectFactory<HttpMessageConverters> converters, JsonFormWriter jsonFormWriter) {
        return new FormJsonEncoder(new SpringEncoder(converters), jsonFormWriter);
    }

    public static class FormJsonEncoder extends FormEncoder {
        public FormJsonEncoder(Encoder delegate, JsonFormWriter jsonFormWriter) {
            super(delegate);
            var processor = (MultipartFormContentProcessor) this.getContentProcessor(ContentType.MULTIPART);
            processor.addFirstWriter(jsonFormWriter);
            processor.addFirstWriter(new SpringSingleMultipartFileWriter());
            processor.addFirstWriter(new SpringManyMultipartFilesWriter());
        }
    }
}