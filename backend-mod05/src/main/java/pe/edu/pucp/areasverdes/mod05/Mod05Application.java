package pe.edu.pucp.areasverdes.mod05;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import java.time.Clock;
import java.time.ZoneId;

@SpringBootApplication
public class Mod05Application {
    public static final ZoneId LIMA = ZoneId.of("America/Lima");
    public static void main(String[] args) { SpringApplication.run(Mod05Application.class, args); }
    @Bean Clock clock() { return Clock.system(LIMA); }
}
