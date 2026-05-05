package com.library.library_api;

import com.library.library_api.model.Author;
import com.library.library_api.model.Book;
import com.library.library_api.model.Loan;
import com.library.library_api.repository.AuthorRepository;
import com.library.library_api.repository.BookRepository;
import com.library.library_api.repository.LoanRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final LoanRepository loanRepository;

    public DataSeeder(BookRepository bookRepository,
                      AuthorRepository authorRepository,
                      LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.loanRepository = loanRepository;
    }

    @Override
    public void run(String... args) {

        Author martin = new Author("Robert C. Martin");
        Author fowler = new Author("Martin Fowler");
        Author thomas = new Author("David Thomas");

        Book cleanCode = new Book("Clean Code", martin, "978-0132350884", 2008, true);
        Book cleanArch = new Book("Clean Architecture", martin, "978-0134494166", 2017, true);
        Book refactoring = new Book("Refactoring", fowler, "978-0201485677", 1999, true);
        Book patterns = new Book("Patterns of Enterprise Application Architecture", fowler, "978-0321127426", 2002, true);
        Book pragmatic = new Book("The Pragmatic Programmer", thomas, "978-0135957059", 2019, true);
        bookRepository.save(cleanCode);
        bookRepository.save(cleanArch);
        bookRepository.save(refactoring);
        bookRepository.save(patterns);
        bookRepository.save(pragmatic);

        loanRepository.save(new Loan(cleanCode));
        loanRepository.save(new Loan(refactoring));
        loanRepository.save(new Loan(pragmatic));
    }
}