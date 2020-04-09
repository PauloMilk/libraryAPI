package com.paulo.libraryapi.model.repository;

import com.paulo.libraryapi.model.entity.Book;
import com.paulo.libraryapi.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    @Query("select case when( count(l.id) > 0) then true else false end from Loan l where l.book = :book and (l.returned is null or l.returned is false)")
    boolean existsByBookAndNotReturned(Book book);

    @Query("select l from Loan as l join l.book as b where b.isbn = :isbn or l.customer = :customer")
    Page<Loan> findByBookIsbnOrCustomer(@Param("customer") String customer, @Param("isbn") String isbn, Pageable pageRequest);

    Page<Loan> findByBook(Book book, Pageable pageRequest);

    @Query("select l from Loan as l where l.loanDate <= :treeDaysAgo and (l.returned is null or l.returned is false)")
    List<Loan> findByLoanDateLessThanAndNotReturned(@Param("treeDaysAgo") LocalDate threDaysAgo);
}