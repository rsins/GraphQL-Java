package com.example.graphql.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import graphql.schema.DataFetcher;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/*
    Example:
    * Get Schema
      curl http://localhost:8080/graphql -H 'Content-Type: application/json' -H 'Accept: application/json' --data '{ "query":" { __schema{ queryType { fields{ name } } } }" }'
    * Get one Book by ID
      curl http://localhost:8080/graphql -H 'Content-Type: application/json' -H 'Accept: application/json' --data '{ "query":" { bookById(id: \"book-1\") { author { id } } }" }'
    * Get all books
      curl http://localhost:8080/graphql -H 'Content-Type: application/json' -H 'Accept: application/json' --data '{ "query":" { allBooks { id name pageCount author { id firstName  lastName } } }" }'
 */
@Component
public class GraphQLDataFetchers {

    private static List<Map<String, String>> books = Arrays.asList(
            ImmutableMap.of("id", "book-1",
                    "name", "Harry Potter and the Philosopher's Stone",
                    "pageCount", "223",
                    "authorId", "author-1"),
            ImmutableMap.of("id", "book-2",
                    "name", "Moby Dick",
                    "pageCount", "635",
                    "authorId", "author-2"),
            ImmutableMap.of("id", "book-3",
                    "name", "Interview with the vampire",
                    "pageCount", "371",
                    "authorId", "author-3")
    );

    private static List<Map<String, String>> authors = Arrays.asList(
            ImmutableMap.of("id", "author-1",
                    "firstName", "Joanne",
                    "lastName", "Rowling"),
            ImmutableMap.of("id", "author-2",
                    "firstName", "Herman",
                    "lastName", "Melville"),
            ImmutableMap.of("id", "author-3",
                    "firstName", "Anne",
                    "lastName", "Rice")
    );

    /*
    // --------------------- This is using a map. ----------------------------
    public DataFetcher getBookByIdDataFetcher() {
        return dataFetchingEnvironment -> {
            String bookId = dataFetchingEnvironment.getArgument("id");
            return books
                    .stream()
                    .filter(book -> book.get("id").equals(bookId))
                    .findFirst()
                    .orElse(null);
        };
    }

    public DataFetcher getBooksDataFetcher() {
        return dataFetchingEnvironment -> {
            return books;
        };
    }

    public DataFetcher getAuthorDataFetcher() {
        return dataFetchingEnvironment -> {
            Map<String,String> book = dataFetchingEnvironment.getSource();
            String authorId = book.get("authorId");

            return authors
                    .stream()
                    .filter(author -> author.get("id").equals(authorId))
                    .findFirst()
                    .orElse(null);
        };
    }
    */

    // ------------------ This is using pojo classes. ---------------------
    public static class Book {
        public String id;
        public String name;
        public String pageCount;
        public String authorId;

        public Book() {}
        public Book(String id, String name, String pageCount, String authorId) {
            this.id = id;
            this.name = name;
            this.pageCount = pageCount;
            this.authorId = authorId;
        }
    }

    public static class Author {
        public String id;
        public String firstName;
        public String lastName;

        public Author() {}
        public Author(String id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }
    }

    private static List<Book> booksPojos = new ArrayList<>();
    private static List<Author> authorsPojos = new ArrayList<>();

    static {
        books.stream().forEach(bookMap -> {
            ObjectMapper mapper = new ObjectMapper();
            Book bookPojo = mapper.convertValue(bookMap, Book.class);
            booksPojos.add(bookPojo);
        });
        authors.stream().forEach(authorMap -> {
            ObjectMapper mapper = new ObjectMapper();
            Author authorPojo = mapper.convertValue(authorMap, Author.class);
            authorsPojos.add(authorPojo);
        });
    }

    public DataFetcher getBookByIdDataFetcher() {
        return dataFetchingEnvironment -> {
            String bookId = dataFetchingEnvironment.getArgument("id");
            return booksPojos
                    .stream()
                    .filter(book -> book.id.equals(bookId))
                    .findFirst()
                    .orElse(null);
        };
    }

    public DataFetcher getBooksDataFetcher() {
        return dataFetchingEnvironment -> {
            return booksPojos;
        };
    }

    public DataFetcher getAuthorDataFetcher() {
        return dataFetchingEnvironment -> {
            Book book = dataFetchingEnvironment.getSource();
            String authorId = book.authorId;

            return authorsPojos
                    .stream()
                    .filter(author -> author.id.equals(authorId))
                    .findFirst()
                    .orElse(null);
        };
    }
}
