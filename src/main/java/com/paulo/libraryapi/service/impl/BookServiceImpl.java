package com.paulo.libraryapi.service.impl;

import com.paulo.libraryapi.exception.BussinessException;
import com.paulo.libraryapi.repository.BookRepository;
import com.paulo.libraryapi.service.BookService;
import com.paulo.libraryapi.model.entity.Book;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    private BookRepository repository;

    public BookServiceImpl(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public Book save(Book book) {
        if(repository.existsByIsbn(book.getIsbn())) {
            throw  new BussinessException("Isbn já cadastrado.");
        }
        return repository.save(book);
    }

    @Override
    public Optional<Book> getById(Long id) {
        return Optional.empty();
    }

    @Override
    public void delete(Book book) {

    }

    @Override
    public Book update(Book book) {
        return null;
    }
}
