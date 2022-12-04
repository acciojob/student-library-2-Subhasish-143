package com.driver.services;

import com.driver.models.Book;
import com.driver.models.Student;
import com.driver.models.Card;
import com.driver.models.CardStatus;
import com.driver.repositories.BookRepository;
import com.driver.repositories.CardRepository;
import com.driver.repositories.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CardService {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    CardRepository cardRepository3;

    @Autowired
    BookRepository bookRepository;

    public Card createAndReturn(Student student){
        Card card = cardRepository3.save(student.getCard());
        // this card formed after saving into memory of card repo
        return card;
    }

    public void deactivateCard(int student_id){
        // making all the books present in card available
        Optional<Student> student = studentRepository.findById(student_id);
        Card card = student.get().getCard();
        for(Book book : card.getBooks()) {
            bookRepository.findById(book.getId()).get().setAvailable(true);
        }

        // deleting card
        cardRepository3.deactivateCard(student_id, CardStatus.DEACTIVATED.toString());
    }
}
