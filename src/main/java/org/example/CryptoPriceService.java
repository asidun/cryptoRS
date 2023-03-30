package org.example;

import jakarta.annotation.PostConstruct;
import org.example.exception.WrongParametersException;
import org.example.pojo.CryptoPrice;
import org.example.pojo.CryptoStats;
import org.example.util.CsvUtil;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CryptoPriceService {
    public static final long DAY = 86400000L;
    private final Map<String, List<CryptoPrice>> cryptoPrices = new HashMap<>();

    @PostConstruct
    public void init() {
        try {
            Files.walk(Paths.get("src/main/resources/prices"))
                    .filter(Files::isRegularFile)
                    .filter(file -> file.getFileName().toString().endsWith(".csv"))
                    .forEach(this::loadCryptoData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadCryptoData(Path filePath) {
        String fileName = filePath.getFileName().toString();
        String cryptoName = fileName.substring(0, fileName.lastIndexOf("_values.csv"));
        List<CryptoPrice> prices = CsvUtil.readCsv(filePath.toString());
        cryptoPrices.put(cryptoName, prices);
    }

    private double calculateNormalizedRange(double min, double max) {
        if (min == 0) {
            return 0;
        }
        return (max - min) / min;
    }

    private static void checkDates(long start, long end) {
        if (start > end) {
            throw new WrongParametersException();
        }
    }

    //Calculates the normalized range for all cryptos and sorts them in descending order
    public List<CryptoStats> getDescendingNormalizedRange(long start, long end) {
        checkDates(start, end);
        return cryptoPrices.entrySet().stream()
                .map(entry -> getCryptoStats(entry.getKey(), start, end))
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingDouble((CryptoStats stats) -> calculateNormalizedRange(stats.getMin(), stats.getMax())).reversed())
                .collect(Collectors.toList());
    }

    //Calculates the oldest, newest, min, and max values for the given crypto symbol and time period.
    public CryptoStats getCryptoStats(String symbol, long start, long end) {
        checkDates(start, end);
        List<CryptoPrice> prices = cryptoPrices.get(symbol);
        if (prices == null) {
            return null;
        }
        List<CryptoPrice> filteredPrices = prices.stream()
                .filter(price -> price.getTimestamp() >= start && price.getTimestamp() <= end)
                .collect(Collectors.toList());
        if (filteredPrices.isEmpty()){
            return null;
        }

        long oldest = Collections.min(filteredPrices, Comparator.comparingLong(CryptoPrice::getTimestamp))
                .getTimestamp();
        long newest = Collections.max(filteredPrices, Comparator.comparingLong(CryptoPrice::getTimestamp))
                .getTimestamp();
        double min = Collections.min(filteredPrices, Comparator.comparingDouble(CryptoPrice::getPrice))
                .getPrice();
        double max = Collections.max(filteredPrices, Comparator.comparingDouble(CryptoPrice::getPrice))
                .getPrice();

        return new CryptoStats(symbol, oldest, newest, min, max);
    }

    //Finds the crypto with the highest normalized range for a specific day.
    public CryptoStats getHighestNormalizedRange(long timestamp) {
        long start = timestamp;
        long end = timestamp + DAY;

        return getDescendingNormalizedRange(start, end).stream()
                .findFirst()
                .orElse(null);
    }
}
