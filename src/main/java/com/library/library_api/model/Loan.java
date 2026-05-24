package com.library.library_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(
        name = "loans",
        indexes = {
                @Index(name = "idx_loans_book_id", columnList = "book_id"),
                @Index(name = "idx_loans_return_date", columnList = "return_date"),
                @Index(name = "idx_loans_book_return", columnList = "book_id, return_date")
        })
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @NotNull
    @Column(nullable = false)
    private LocalDate loanDate;

    private LocalDate returnDate;

    public Loan() {}

    public Loan(Book book) {
        this.book = book;
        this.loanDate = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public void setLoanDate(LocalDate loanDate) {
        this.loanDate = loanDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}
