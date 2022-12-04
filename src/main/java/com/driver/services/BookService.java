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
            books = bookRepository2.findBooksByAuthor(author,available);
        }
        else if (author==null || author.equals("")) {
            // genre and available
            books = bookRepository2.findBooksByGenre(genre, available);
        }
        else if ((genre==null || genre.equals("")) && (author==null || author.equals(""))){
            // available
            books = bookRepository2.findByAvailability(available);
        }
        else {
            // genre , author and available
            books = bookRepository2.findBooksByGenreAuthor(genre, author, available);
        }

        return books;
    }
}
