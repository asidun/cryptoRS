package org.example.util;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.example.pojo.CryptoPrice;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CsvUtil {
    public static List<CryptoPrice> readCsv(String fileName) {
        CSVFormat.Builder csvFormatBuilder = CSVFormat.Builder.create()
                .setHeader("timestamp", "symbol", "price")
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setTrim(true)
                .setIgnoreSurroundingSpaces(true);

        try (Reader reader = Files.newBufferedReader(Paths.get(fileName))) {
            CSVParser csvParser = new CSVParser(reader, csvFormatBuilder.build());

            return csvParser.getRecords().stream().map(record -> {
                CryptoPrice cryptoPrice = new CryptoPrice();
                cryptoPrice.setTimestamp(Long.parseLong(record.get("timestamp")));
                cryptoPrice.setSymbol(record.get("symbol"));
                cryptoPrice.setPrice(Double.parseDouble(record.get("price")));
                return cryptoPrice;
            }).collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
