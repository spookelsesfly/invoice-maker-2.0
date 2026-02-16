package cz.spookelsesfly.invoice_maker.model.util;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

// Creates an HTTP request to generate a QR code for invoice payment
@Component
@Scope("singleton")
public class QrApiClient {

    public QrApiClient() {}

    public byte[] getQRCode(long accountNumber,
                            int bankCode,
                            int amount,
                            int variableSymbol,
                            String constantSymbol,
                            String message) throws Exception {
        String url = String.format(
                "https://api.paylibo.com/paylibo/generator/czech/image?accountNumber=%s&bankCode=%s&amount=%s&currency=CZK&vs=%s&ks=%s&message=%s",
                URLEncoder.encode(String.valueOf(accountNumber), StandardCharsets.UTF_8),
                URLEncoder.encode(String.valueOf(bankCode), StandardCharsets.UTF_8),
                URLEncoder.encode(String.valueOf(amount), StandardCharsets.UTF_8),
                URLEncoder.encode(String.valueOf(variableSymbol), StandardCharsets.UTF_8),
                URLEncoder.encode(constantSymbol, StandardCharsets.UTF_8),
                URLEncoder.encode(message, StandardCharsets.UTF_8)
        );

        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error while downloading QR code: HTTP " + response.statusCode());
        }

        return response.body();
    }
}
