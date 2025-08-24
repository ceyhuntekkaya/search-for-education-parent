package com.genixo.education.search;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.io.PrintStream;

@SpringBootApplication
public class SearchForEducationParentApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SearchForEducationParentApplication.class);

        app.setBanner(new Banner() {
            @Override
            public void printBanner(Environment environment, Class<?> sourceClass, PrintStream out) {
                out.println("   _____  ______ _   _ _______   ______      ");
                out.println("  / ____||  ____| \\ | |_   _\\ \\ / / __ \\     ");
                out.println(" | |  __ | |__  |  \\| | | |  \\ V / |  | |    ");
                out.println(" | | |_ ||  __| | . ` | | |   > <| |  | |    ");
                out.println(" | |__| || |____| |\\  |_| |_ / . \\ |__| |    ");
                out.println("  \\_____||______|_| \\_|_____/_/ \\_\\____/     ");
                out.println(" ");
            }
        });

        app.run(args);

    }

}

