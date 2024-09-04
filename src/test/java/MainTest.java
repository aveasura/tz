import org.junit.jupiter.api.Test;
import tz.Main;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MainTest {

    @Test
    public void testCalculateFlightDuration() {
        String departureTime = "10:30";
        String arrivalTime = "15:45";
        Duration expectedDuration = Duration.ofHours(5).plusMinutes(15);

        Duration actualDuration = Main.calculateFlightDuration(departureTime, arrivalTime);

        assertEquals(expectedDuration, actualDuration, "The flight time is calculated incorrectly");
    }

    @Test
    public void testCalculateFlightDurationCrossMidnight() {
        String departureTime = "23:15";
        String arrivalTime = "01:45";
        Duration expectedDuration = Duration.ofHours(2).plusMinutes(30);

        Duration actualDuration = Main.calculateFlightDuration(departureTime, arrivalTime);

        assertEquals(expectedDuration, actualDuration, "Flight time after midnight is calculated incorrectly");
    }

    @Test
    public void testCalculateMedianOdd() {
        List<Integer> prices = Arrays.asList(100, 200, 300);
        double expectedMedian = 200.0;

        double actualMedian = Main.calculateMedian(prices);

        assertEquals(expectedMedian, actualMedian, "The median for an odd number of elements is calculated incorrectly");
    }

    @Test
    public void testCalculateMedianEven() {
        List<Integer> prices = Arrays.asList(100, 200, 300, 400);
        double expectedMedian = 250.0;

        double actualMedian = Main.calculateMedian(prices);

        assertEquals(expectedMedian, actualMedian, "The median for an even number of elements is calculated incorrectly");
    }

    @Test
    public void testCalculateMedianSingleElement() {
        List<Integer> prices = Collections.singletonList(100);
        double expectedMedian = 100.0;

        double actualMedian = Main.calculateMedian(prices);

        assertEquals(expectedMedian, actualMedian, "The median for one element is calculated incorrectly");
    }

    @Test
    public void testCalculateMedianEmptyList() {
        List<Integer> prices = Collections.emptyList();
        double expectedMedian = 0.0;

        double actualMedian = Main.calculateMedian(prices);

        assertEquals(expectedMedian, actualMedian, "The median for an empty list is calculated incorrectly");
    }
}
