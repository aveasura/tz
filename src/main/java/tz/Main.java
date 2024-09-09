package tz;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Main {
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy H:mm");

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Path to \"tickets\".json file not specified");
            return;
        }

        String file = args[0];

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            TicketList ticketList = objectMapper.readValue(new File(file), TicketList.class);
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
                Duration duration = timeCalc(ticket.getDepartureDate(), ticket.getDepartureTime(),
                        ticket.getArrivalDate(), ticket.getArrivalTime());

                leastDurationPerCarrier.merge(ticket.getCarrier(), duration, (existingDuration, newDuration) ->
                        existingDuration.compareTo(newDuration) <= 0 ? existingDuration : newDuration);
            }

            System.out.println("Minimum flight time between Vladivostok and Tel-Aviv for each carrier: ");
            leastDurationPerCarrier.forEach((carrier, time) -> {
                long days = time.toDays();
                long hours = time.minusDays(days).toHours();
                long minutes = time.minusDays(days).minusHours(hours).toMinutes();

                if ((days | hours | minutes) < 0) {
                    System.out.println("Прибытие не может быть раньше чем отправление");
                }

                System.out.println(carrier + ": " + "Days:" + days + ", hours:"+ hours + ", min:" + minutes);
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

    private static Duration timeCalc(String departureDate, String departureTime, String arrivalDate, String arrivalTime) {
        String departureDateTime = departureDate + " " + departureTime;
        String arrivalDateTime = arrivalDate + " " + arrivalTime;

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime departure = LocalDateTime.parse(departureDateTime, TIME_FORMATTER);
        LocalDateTime arrival = LocalDateTime.parse(arrivalDateTime, TIME_FORMATTER);

        return Duration.between(departure, arrival);
    }

    private static double calcMedian(List<Integer> price) {
        if (price.isEmpty()) {
            return 0.0;
        }

        List<Integer> sortedPrice = price.stream().sorted().collect(Collectors.toList());

        int size = sortedPrice.size();

        if (size % 2 == 0) {
            return (sortedPrice.get(size / 2 - 1) + sortedPrice.get(size / 2)) / 2.0;
        } else {
            return sortedPrice.get(size / 2);
        }
    }

    // Публичные методы для тестирования
    public static Duration calculateFlightDuration(String departureDate, String departureTime, String arrivalDate, String arrivalTime) {
        return timeCalc(departureDate, departureTime, arrivalDate, arrivalTime);
    }
    public static double calculateMedian(List<Integer> prices) {
        return calcMedian(prices);
    }
}