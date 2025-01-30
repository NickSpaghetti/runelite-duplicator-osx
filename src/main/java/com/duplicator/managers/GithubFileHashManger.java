package com.duplicator.managers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public class GithubFileHashManger {
    private static final String GITHUB_API_BASE_URL = "https://api.github.com";
    private static final OkHttpClient client = new OkHttpClient();
    private static final Gson gson = new Gson();

    public List<String> getFileHashes(String fileName) throws Exception {
        // Get last 3 releases info
        String releasesUrl = GITHUB_API_BASE_URL + "/repos/runelite/launcher/releases?per_page=3";
        String releasesJson = makeApiRequest(releasesUrl);
        JsonArray releases = gson.fromJson(releasesJson, JsonArray.class);

        List<CompletableFuture<String>> hashFutures = new ArrayList<>();

        for (int i = 0; i < releases.size(); i++) {
            JsonObject release = releases.get(i).getAsJsonObject();
            JsonArray assets = release.getAsJsonArray("assets");
            String downloadUrl = IntStream.range(0, assets.size()).mapToObj(j -> assets.get(j).getAsJsonObject()).filter(asset -> asset.get("name").getAsString().equals(fileName)).findFirst().map(asset -> asset.get("browser_download_url").getAsString()).orElse("");

            if (!downloadUrl.isEmpty()) {
                hashFutures.add(CompletableFuture.supplyAsync(() -> {
                    try {
                        return calculateFileHash(downloadUrl);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }));
            }
        }

        CompletableFuture<Void> allOf = CompletableFuture.allOf(
                hashFutures.toArray(new CompletableFuture[0])
        );

        return allOf.thenApply(v ->
                hashFutures.stream()
                        .map(CompletableFuture::join)
                        .collect(java.util.stream.Collectors.toList())
        ).get();
    }

    private static String makeApiRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .header("Accept", "application/vnd.github.v3+json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    private String calculateFileHash(String downloadUrl) throws IOException, NoSuchAlgorithmException {
        Request request = new Request.Builder().url(downloadUrl).build();
        try (Response response = client.newCall(request).execute();
             InputStream inputStream = response.body().byteStream()) {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }

            byte[] hashBytes = digest.digest();
            StringBuilder hexString = new StringBuilder();
            for (byte hashByte : hashBytes) {
                String hex = Integer.toHexString(0xff & hashByte);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        }
    }
}

