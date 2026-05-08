package cl.bch.cloud.ms.fglosas.mantenedor;

import cl.bch.cloud.spring.properties.YamlPropertySourceFactory;
import jakarta.annotation.PostConstruct;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.TimeZone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@SpringBootApplication
@EnableFeignClients
@ServletComponentScan
@ConfigurationProperties
@PropertySources({
        @PropertySource("classpath:osb.properties"),
        @PropertySource("classpath:rest.properties"),
        @PropertySource("classpath:bbdd.properties"),
        @PropertySource(value = "classpath:ms-fglosas-mantenedor.yaml", factory = YamlPropertySourceFactory.class)
})
public class Application {

  private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

  @Value("${startup.beans.inspect: false}")
  private boolean inspectBeansAtStartup;

  @Value("${timezone.default: America/Santiago}")
  private String timezoneDefault;

  /**
   * A bean that executes at application start up and retrieves all the beans that
   * were created either by the app or were automatically added by Spring Boot
   * 
   * @param ctx {@link ApplicationContext} The application context just
   *            initialized
   * @return {@link CommandLineRunner} A CommandLineRunner executed
   */
  @Bean
  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    return args -> {
      SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
      LOGGER.info(String.format("Context started up at: %s", format.format(ctx.getStartupDate())));

      if (inspectBeansAtStartup) {
        LOGGER.info(String.format("Property 'startup.beans.inspect' is set as %s. " + 
                                  "Let's inspect the beans provided by Spring Boot:", 
          inspectBeansAtStartup));

        String[] beanNames = ctx.getBeanDefinitionNames();
        Arrays.sort(beanNames);

        for (String beanName : beanNames) {
          LOGGER.info("  " + beanName);
        }
      } else {
        LOGGER.info(String.format("Property 'startup.beans.inspect' is set as %s. Let's jump the beans inspection.", 
          inspectBeansAtStartup));
      }
    };
  }

  @PostConstruct
  public void started() {
    TimeZone.setDefault(TimeZone.getTimeZone(this.timezoneDefault));
  }

  /**
   * Main routine - It starts the application context
   */
  public static void main(String[] args) { SpringApplication.run(Application.class, args); }

}