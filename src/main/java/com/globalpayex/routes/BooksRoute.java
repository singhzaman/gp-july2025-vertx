package com.globalpayex.routes;

import com.globalpayex.MyHttpServerVerticle;
import com.globalpayex.domain.Book;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BooksRoute {
    private static final Logger logger = LoggerFactory
            .getLogger(BooksRoute.class);

    private static final ArrayList<Book> books =
            new ArrayList<>(Arrays.asList(
                    new Book(1, "Book 1", 900, 1000),
                    new Book(2, "Book 2", 700, 400)
            ));

    private static int lastUsedId = 2;

    public static Router init(
            Vertx vertx,
            Router router) {
        router.get("/books")
                .handler(BooksRoute::handleGetAllBooks);
        router.get("/books/:id")
                .handler(BooksRoute::handleGetBook);
        router.post("/books")
                .handler(BooksRoute::handleCreateBook);

        return router;
    }

    private static void handleCreateBook(RoutingContext routingContext) {
        // var bookObj = routingContext.body().asJsonObject();

        // jackson-databind library makes this binding from json to
        // pojo possible
        var book = routingContext.body().asPojo(Book.class);

        book.setId(++lastUsedId);
        books.add(book);

        routingContext
                .response()
                .putHeader("Content-Type", "application/json")
                .setStatusCode(201)
                .end(JsonObject.mapFrom(book).encode());
    }

    private static void handleGetBook(RoutingContext routingContext) {
        int bookId;
        try {
            bookId = Integer.parseInt(routingContext.pathParam("id"));
        } catch (NumberFormatException e) {
            routingContext
                    .response()
                    .setStatusCode(400)
                    .end("id should be only integer in path param");
            return;
        }

        var foundBooks = books
                .stream()
                .filter(book -> book.getId() == bookId)
                .collect(Collectors.toList());
        if (foundBooks.isEmpty()) {
            routingContext
                    .response()
                    .setStatusCode(404)
                    .end("Book with id " + bookId + " not found");
        } else {
            routingContext
                    .response()
                    .putHeader("Content-Type", "application/json")
                    .end(JsonObject.mapFrom(foundBooks.get(0)).encode());
        }
    }

    private static void handleGetAllBooks(RoutingContext routingContext) {
        var booksArray = new JsonArray(books);
        routingContext
                .response()
                // by default status code sent was 200
                .putHeader("Content-Type", "application/json")
                .end(booksArray.encode());
    }

}
