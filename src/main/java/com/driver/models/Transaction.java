package com.driver.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Getter
@Setter
@Builder
//@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "Transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String transactionId = UUID.randomUUID().toString(); // externalId

    @ManyToOne
    @JoinColumn
    @JsonIgnoreProperties("books")
    private Card card;

    @ManyToOne
    @JoinColumn
    @JsonIgnoreProperties("transactions")
    private Book book;

    private int fineAmount;

    @Column(columnDefinition = "TINYINT(1)")
    private boolean isIssueOperation;

    @Enumerated(value = EnumType.STRING)
    private TransactionStatus transactionStatus;

    public Transaction(int fineAmount, boolean isIssueOperation, TransactionStatus transactionStatus) {
        this.fineAmount = fineAmount;
        this.isIssueOperation = isIssueOperation;
        this.transactionStatus = transactionStatus;
    }

    @CreationTimestamp
    private Date transactionDate;
}

