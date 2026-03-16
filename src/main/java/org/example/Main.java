package org.example;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Start");

        Scanner scanner = new Scanner(System.in);

        logger.info("Введите строку: ");
        String input = scanner.nextLine();

        String reversed = StringUtils.reverse(input);
        String capitalized = StringUtils.capitalize(input);

        logger.info("Перевернутая строка: {}", reversed);
        logger.info("Строка с заглавной буквы: {}", capitalized);

        logger.info("End");
    }
}
