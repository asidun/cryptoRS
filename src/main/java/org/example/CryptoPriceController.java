package org.example;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.example.pojo.CryptoStats;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cryptos")
public class CryptoPriceController {

    @Autowired
    private CryptoPriceService cryptoPriceService;

    @Operation(summary = "Get descending normalized range for all cryptos", responses = {
            @ApiResponse(responseCode = "200", description = "A list of cryptos with their normalized range", content = @Content(schema = @Schema(implementation = CryptoStats.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters", content = @Content)
    })
    @GetMapping("/normalized-range")
    public ResponseEntity<List<CryptoStats>> getDescendingNormalizedRange(
            @Parameter(description = "Start timestamp") @RequestParam("start") long start,
            @Parameter(description = "End timestamp") @RequestParam("end") long end) {
        List<CryptoStats> stats = cryptoPriceService.getDescendingNormalizedRange(start, end);
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Get the oldest, newest, min, and max values for a requested crypto", responses = {
            @ApiResponse(responseCode = "200", description = "Crypto statistics for the requested period", content = @Content(schema = @Schema(implementation = CryptoStats.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters", content = @Content),
            @ApiResponse(responseCode = "404", description = "Crypto not found", content = @Content)
    })
    @GetMapping("/{symbol}/stats")
    public ResponseEntity<CryptoStats> getCryptoStats(
            @PathVariable("symbol") String symbol,
            @Parameter(description = "Start timestamp") @RequestParam("start") long start,
            @Parameter(description = "End timestamp") @RequestParam("end") long end) {
        CryptoStats stats = cryptoPriceService.getCryptoStats(symbol, start, end);
        if (stats == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Get the crypto with the highest normalized range for a specific day", responses = {
            @ApiResponse(responseCode = "200", description = "Crypto with the highest normalized range", content = @Content(schema = @Schema(implementation = CryptoStats.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input parameters", content = @Content)
    })
    @GetMapping("/highest-normalized-range")
    public ResponseEntity<CryptoStats> getHighestNormalizedRange(
            @Parameter(description = "Timestamp for the specific day") @RequestParam("timestamp") long timestamp) {
        CryptoStats stats = cryptoPriceService.getHighestNormalizedRange(timestamp);
        if (stats == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return ResponseEntity.ok(stats);
    }
}


