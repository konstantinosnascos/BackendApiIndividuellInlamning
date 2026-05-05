package com.library.library_api.repository;

import com.library.library_api.model.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface AuthorRepository extends JpaRepository<Author,Long> {
    Optional<Author> findByNameIgnoreCase(String name);
}
