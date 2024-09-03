package tz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

import java.io.IOException;

import java.time.Duration;

import java.time.LocalTime;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import java.util.List;

import java.util.Map;

import java.util.stream.Collectors;


public class Main {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");

    public static void main(String[] args) {
        String filePath = "src/main/java/tz/json/tickets.json";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TicketList ticketList = objectMapper.readValue(new File(filePath), TicketList.class);
            List<Ticket> tickets = ticketList.getTickets();

            // Оставим только рейсы между Владивостоком и Тель-Авив
            List<Ticket> filteredTickets = tickets.stream()
                    .filter(ticket -> "VVO".equals(ticket.getOrigin()) && "TLV".equals(ticket.getDestination()))
                    .collect(Collectors.toList());

            if (filteredTickets.isEmpty()) {
                System.out.println("There are no flights between Vladivostok and Tel-Aviv");
                return;
            }

            Map<String, Duration> leastDurationPerCarrier = new HashMap<>();
            for (Ticket ticket : filteredTickets) {
                Duration duration = timeCalc(ticket.getDepartureTime(), ticket.getArrivalTime());

                leastDurationPerCarrier.merge(ticket.getCarrier(), duration, (existingDuration, newDuration) ->
                        existingDuration.compareTo(newDuration) <= 0 ? existingDuration : newDuration);
            }

            System.out.println("Minimum flight time between Vladivostok and Tel-Aviv for each carrier: ");
            leastDurationPerCarrier.forEach((c, d) ->
                    System.out.println(c + ": " + d));



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Duration timeCalc(String departureTime, String arrivalTime) {
        LocalTime departure = LocalTime.parse(departureTime, TIME_FORMATTER);
        LocalTime arrival = LocalTime.parse(arrivalTime, TIME_FORMATTER);

        return Duration.between(departure, arrival);
    }
}