package com.library.library_api.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "book_id", unique = true)
    private Book book;

    @Column(nullable = false)
    private LocalDate loandate;

    private LocalDate returndate;

    public Loan() {}

    public Loan(Book book) {
        this.book = book;
        this.loandate = LocalDate.now();
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

    public LocalDate getLoandate() {
        return loandate;
    }

    public void setLoandate(LocalDate loandate) {
        this.loandate = loandate;
    }

    public LocalDate getReturndate() {
        return returndate;
    }

    public void setReturndate(LocalDate returndate) {
        this.returndate = returndate;
    }
}
