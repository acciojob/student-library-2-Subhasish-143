package com.driver.services;

import com.driver.models.Author;
import com.driver.models.Book;
import com.driver.repositories.AuthorRepository;
import com.driver.repositories.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BookService {


    @Autowired
    BookRepository bookRepository2;

    @Autowired
    AuthorRepository authorRepository;

    public void createBook(Book book){
        // add book to the list of books of author
        Author author = book.getAuthor();
        authorRepository.findById(author.getId()).get().getBooksWritten().add(book);

        // add book to book repo
        bookRepository2.save(book);
    }

    public List<Book> getBooks(String genre, boolean available, String author){
        List<Book> books = new ArrayList<>();

        if (genre==null || genre.equals("")) {
            // author and available
            books = getBooksWhenGenreNull(author, available);
        }
        else if (author==null || author.equals("")) {
            // genre and available
            books = getBooksWhenAuthorNull(genre, available);
        }
        else if (!available){
            // available
            books = getBooksWhenNotAvailable(false);
        }
        else {
            // genre , author and available
            books = getBooksWhenNoInputNull(genre, available, author);
        }

        return books;
    }

    public List<Book> getBooksWhenGenreNull(String author,boolean available){
        List<Book> books = bookRepository2.findBooksByAuthor(author,available);
        return books;
    }
    public List<Book> getBooksWhenAuthorNull(String genre, boolean available) {
        List<Book> books = bookRepository2.findBooksByGenre(genre, available);
        return books;
    }
    public List<Book> getBooksWhenNotAvailable(boolean available) {
        return bookRepository2.findByAvailability(false);
    }
    public List<Book> getBooksWhenNoInputNull(String genre, boolean available, String author) {
        return bookRepository2.findBooksByGenreAuthor(genre, author, available);
    }
}
