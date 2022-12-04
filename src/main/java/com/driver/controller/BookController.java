package com.driver.controller;

import com.driver.models.Book;
import com.driver.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

//Add required annotations
@RestController
@RequestMapping("book")
public class BookController {

    @Autowired
    BookService bookService;

    @PostMapping("createBook")
    public ResponseEntity createBook(@RequestBody()Book book) {
        bookService.createBook(book);
        return new ResponseEntity<>("Success",HttpStatus.CREATED);
    }
    @GetMapping("getBooks")
    public ResponseEntity getBooks(@RequestParam(value = "genre", required = false) String genre,
                                   @RequestParam(value = "available", required = false, defaultValue = "false") boolean available,
                                   @RequestParam(value = "author", required = false) String author){

        List<Book> bookList = new ArrayList<>();

        if (genre==null || genre.equals("")) {
            // author and available
            bookList = bookService.getBooksWhenGenreNull(author,available);
        }
        else if (author==null || author.equals("")) {
            // genre and available
            bookList = bookService.getBooksWhenAuthorNull(genre, available);
        }
        else if (!available) {
            // Not available
            bookList = bookService.getBooksWhenNotAvailable(available);
        }
        else {
            // genre , author & available
            bookList = bookService.getBooksWhenNoInputNull(genre, available, author);
        }

        return new ResponseEntity<>(bookList, HttpStatus.OK);

    }
}
