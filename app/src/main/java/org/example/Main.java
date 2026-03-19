package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Scanner;
import java.io.InputStream;
import java.util.Properties;

import org.example.StringProcessor;


public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Start");

        Scanner scanner = new Scanner(System.in);

        logger.info("Введите строку: ");
        String inputLine = scanner.nextLine();

        String reversed = StringProcessor.reverse(inputLine);
        String capitalized = StringProcessor.capitalize(inputLine);

        logger.info("Перевернутая строка: {}", reversed);
        logger.info("Строка с заглавной буквы: {}", capitalized);

        logger.info("End");

        Properties props = new Properties();

        try (InputStream input = Main.class.getClassLoader().getResourceAsStream("build-passport.properties")) {

            if (input != null) {
                props.load(input);

                logger.info("Сборку выполнил: {}", props.getProperty("user"));
                logger.info("ОС: {}", props.getProperty("os"));
                logger.info("Java: {}", props.getProperty("javaVersion"));
                logger.info("Время сборки: {}", props.getProperty("buildTime"));
                logger.info("Сообщение: {}", props.getProperty("message"));
            }

        } catch (Exception e) {
            logger.error("Ошибка чтения build-passport.properties", e);
        }
    }
}

