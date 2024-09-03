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
            leastDurationPerCarrier.forEach((c, d) -> {
                long hours = d.toHours();
                long minutes = d.minusHours(hours).toMinutes();
                System.out.println(c + ": " + hours + " ч " + minutes + " мин");
            });

            List<Integer> price = filteredTickets.stream()
                    .map(Ticket::getPrice)
                    .sorted()
                    .collect(Collectors.toList());

            double median = calcMedian(price);
            double avg = price.stream().mapToInt(Integer::intValue).average().orElse(0);
            double diff = avg - median;

            System.out.println("Median: " + median);
            System.out.println("Average: " + avg);
            System.out.println("Difference between median price and average :" + diff);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Duration timeCalc(String departureTime, String arrivalTime) {
        LocalTime departure = LocalTime.parse(departureTime, TIME_FORMATTER);
        LocalTime arrival = LocalTime.parse(arrivalTime, TIME_FORMATTER);

        return Duration.between(departure, arrival);
    }

    private static double calcMedian(List<Integer> price) {
        int size = price.size();

        if (size % 2 == 0) {
            return (price.get(size / 2 - 1) + price.get(size / 2)) / 2.0;
        } else {
            return price.get(size / 2);
        }
    }
}