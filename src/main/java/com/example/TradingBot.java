package com.example;

import com.example.model.security.Credentials;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import static java.util.concurrent.TimeUnit.SECONDS;

public class TradingBot {
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private static final Logger logger = LoggerFactory.getLogger(TradingBot.class);

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        logger.info("Starting the application");
        try (final var credentialsResource = TradingBot.class.getResourceAsStream("/credentials/default.json")) {
            final var credentials = objectMapper.readValue(credentialsResource, Credentials.class);
            final var marketPlugin = new HackathonPlatform(credentials);

            final var ordersController = new OrdersController(marketPlugin);

            final var beeperHandle = scheduler.scheduleAtFixedRate(ordersController, 10, 60, SECONDS);
        } catch (Exception exception) {
            logger.error("Something bad happened", exception);
        }
    }
}
