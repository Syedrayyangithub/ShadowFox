package com.example.LibraryManagementSystem.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GoogleBooksAPI {
    private static final String API_BASE_URL = "https://www.googleapis.com/books/v1/volumes";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static class BookInfo {
        private String title;
        private String author;
        private String description;
        private String isbn;
        private String category;
        private String imageUrl;

        public BookInfo(String title, String author, String description, String isbn, String category,
                String imageUrl) {
            this.title = title;
            this.author = author;
            this.description = description;
            this.isbn = isbn;
            this.category = category;
            this.imageUrl = imageUrl;
        }

        // Getters
        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        public String getDescription() {
            return description;
        }

        public String getIsbn() {
            return isbn;
        }

        public String getCategory() {
            return category;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }

    public static List<BookInfo> searchBooks(String query) {
        List<BookInfo> results = new ArrayList<>();

        try (CloseableHttpClient client = HttpClients.createDefault()) {
            String encodedQuery = java.net.URLEncoder.encode(query, "UTF-8");
            HttpGet request = new HttpGet(API_BASE_URL + "?q=" + encodedQuery + "&maxResults=10");

            String response = client.execute(request, httpResponse -> EntityUtils.toString(httpResponse.getEntity()));

            JsonNode root = objectMapper.readTree(response);
            JsonNode items = root.get("items");

            if (items != null && items.isArray()) {
                for (JsonNode item : items) {
                    JsonNode volumeInfo = item.get("volumeInfo");
                    if (volumeInfo != null) {
                        String title = volumeInfo.get("title").asText();

                        // Handle authors array
                        String author = "Unknown";
                        if (volumeInfo.has("authors") && volumeInfo.get("authors").isArray()) {
                            author = volumeInfo.get("authors").get(0).asText();
                        }

                        // Handle description
                        String description = volumeInfo.has("description") ? volumeInfo.get("description").asText()
                                : "No description available";

                        // Handle ISBN
                        String isbn = "Unknown";
                        if (volumeInfo.has("industryIdentifiers") && volumeInfo.get("industryIdentifiers").isArray()) {
                            for (JsonNode identifier : volumeInfo.get("industryIdentifiers")) {
                                if (identifier.get("type").asText().equals("ISBN_13")) {
                                    isbn = identifier.get("identifier").asText();
                                    break;
                                }
                            }
                        }

                        // Handle categories
                        String category = "Fiction";
                        if (volumeInfo.has("categories") && volumeInfo.get("categories").isArray()) {
                            category = volumeInfo.get("categories").get(0).asText();
                        }

                        // Handle image
                        String imageUrl = volumeInfo.has("imageLinks") &&
                                volumeInfo.get("imageLinks").has("thumbnail")
                                        ? volumeInfo.get("imageLinks").get("thumbnail").asText()
                                        : null;

                        results.add(new BookInfo(title, author, description, isbn, category, imageUrl));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return results;
    }

    public static List<BookInfo> getRecommendations(String category) {
        return searchBooks("subject:" + category);
    }
}