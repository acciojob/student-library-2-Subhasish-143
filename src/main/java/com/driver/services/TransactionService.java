package com.driver.services;

import com.driver.models.*;
import com.driver.repositories.BookRepository;
import com.driver.repositories.CardRepository;
import com.driver.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
public class TransactionService {

    @Autowired
    BookRepository bookRepository5;

    @Autowired
    CardRepository cardRepository5;

    @Autowired
    TransactionRepository transactionRepository5;

    @Value("${books.max_allowed}")
    public int max_allowed_books;

    @Value("${books.max_allowed_days}")
    public int getMax_allowed_days;

    @Value("${books.fine.per_day}")
    public int fine_per_day;

    public String issueBook(int cardId, int bookId) throws Exception {
        //check whether bookId and cardId already exist
        
        //conditions required for successful transaction of issue book:
        
        //1. book is present and available
        Optional<Book> book = bookRepository5.findById(bookId);

            // present
        boolean isBookPrsnt = book.isPresent();
            // available
        boolean isAvl = book.get().isAvailable();
        
        // If it fails: throw new Exception("Book is either unavailable or not present");
        if (!isBookPrsnt || !isAvl) {
            throw new Exception("Book is either unavailable or not present");
        }
        
        //2. card is present and activated
        Optional<Card> card = cardRepository5.findById(cardId);
        
            // present
        boolean isPrst = card.isPresent();
            // available
        CardStatus isActive = card.get().getCardStatus();
        
        // If it fails: throw new Exception("Card is invalid");
        if (!isPrst && isActive==CardStatus.DEACTIVATED) {
            throw new Exception("Card is invalid");
        }
        //3. number of books issued against the card is strictly less than max_allowed_books
        int numOfBook = card.get().getBooks().size();
        
        // If it fails: throw new Exception("Book limit has reached for this card");
        if (numOfBook >= max_allowed_books) {
            throw new Exception("Book limit has reached for this card");
        }
        
        //If the transaction is successful, save the transaction to the list of transactions and return the id
            // new transaction
        Transaction transaction = new Transaction();
        transaction.setBook(book.get());
        transaction.setCard(card.get());
        transaction.setTransactionStatus(TransactionStatus.SUCCESSFUL);
        transaction.setIssueOperation(true);

        transaction = transactionRepository5.save(transaction);

        // book is booked
        book.get().setAvailable(false);
        book.get().getTransactions().add(transaction);

        // add book to the list
        card.get().getBooks().add(book.get());

        // add into repo
        cardRepository5.save(card.get());
        bookRepository5.save(book.get());

       return transaction.getTransactionId(); //return transactionId instead
    }

    public Transaction returnBook(int cardId, int bookId) throws Exception{

        List<Transaction> transactions = transactionRepository5.find(cardId, bookId, TransactionStatus.SUCCESSFUL, true);
        Transaction transaction = transactions.get(transactions.size() - 1);

        //for the given transaction calculate the fine amount considering the book has been returned exactly when this function is called
        int days = new Date().compareTo(transaction.getTransactionDate());
        int fine = days > getMax_allowed_days ? (days * fine_per_day) : 0 ;

        //make the book available for other users
        Book book = transaction.getBook();
        Card card = transaction.getCard();

        // setting book available and card null
        book.setAvailable(true);
        book.setCard(null);

        // deleting the book present in card
        for(int i=0;i<card.getBooks().size();i++) {
            if (card.getBooks().get(i).getId() == book.getId()) {
                card.getBooks().set(i,card.getBooks().get(card.getBooks().size()-1));
                card.getBooks().remove(card.getBooks().size()-1);
            }
        }
        //make a new transaction for return book which contains the fine amount as well

        Transaction returnBookTransaction  = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .book(transaction.getBook())
                .card(transaction.getCard())
                .isIssueOperation(false)
                .fineAmount(fine)
                .transactionStatus(TransactionStatus.SUCCESSFUL)
                .build();

        // add new transaction to list of transaction
        book.getTransactions().add(returnBookTransaction);

        // saving book and card
        bookRepository5.save(book);
        cardRepository5.save(card);

        transactionRepository5.save(returnBookTransaction);
        return returnBookTransaction; //return the transaction after updating all details
    }
}
