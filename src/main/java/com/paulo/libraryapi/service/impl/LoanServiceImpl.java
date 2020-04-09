package com.paulo.libraryapi.service.impl;

import com.paulo.libraryapi.api.dto.LoanFilterDTO;
import com.paulo.libraryapi.exception.BussinessException;
import com.paulo.libraryapi.model.entity.Book;
import com.paulo.libraryapi.model.entity.Loan;
import com.paulo.libraryapi.model.repository.LoanRepository;
import com.paulo.libraryapi.service.LoanService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class LoanServiceImpl implements LoanService {

    private LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        if(repository.existsByBookAndNotReturned(loan.getBook())) {
            throw new BussinessException("Book already loaned.");
        }
        return repository.save(loan);
    }

    @Override
    public Optional<Loan> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Loan update(Loan loan) {
        return repository.save(loan);
    }

    @Override
    public Page<Loan> find(LoanFilterDTO filter, Pageable pageRequest) {
        return repository.findByBookIsbnOrCustomer(filter.getCustomer(), filter.getIsbn(), pageRequest);
    }

    @Override
    public Page<Loan> getLoansByBook(Book book, Pageable pageRequest) {
        return repository.findByBook(book, pageRequest);
    }

    @Override
    public List<Loan> getAllLateLoans() {
        final Integer loanDays = 4;
        LocalDate threDaysAgo = LocalDate.now().minusDays(loanDays);
        return repository.findByLoanDateLessThanAndNotReturned(threDaysAgo);
    }
}
