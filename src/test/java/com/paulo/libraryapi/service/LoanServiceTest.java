package com.paulo.libraryapi.service;

import com.paulo.libraryapi.api.dto.LoanFilterDTO;
import com.paulo.libraryapi.exception.BussinessException;
import com.paulo.libraryapi.model.entity.Book;
import com.paulo.libraryapi.model.entity.Loan;
import com.paulo.libraryapi.model.repository.LoanRepository;
import com.paulo.libraryapi.service.impl.LoanServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class LoanServiceTest {

    @MockBean
    LoanRepository repository;

    LoanService service;

    @BeforeEach
    void setUp() {
        this.service = new LoanServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um emprestimo")
    public void saveLoanTest() {
        Loan loan = createLoan();

        Loan savedLoan = loan;
        savedLoan.setId(1l);
        Mockito.when(repository.existsByBookAndNotReturned(loan.getBook())).thenReturn(false);
        Mockito.when(repository.save(loan)).thenReturn(savedLoan);

        Loan loanSaved = service.save(loan);

        assertThat(loanSaved.getId()).isNotNull();
        assertThat(loanSaved.getBook().getId()).isEqualTo(loan.getBook().getId());
        assertThat(loanSaved.getLoanDate()).isEqualTo(loan.getLoanDate());
        assertThat(loanSaved.getCustomer()).isEqualTo(loan.getCustomer());

        Mockito.verify(repository, Mockito.times(1)).save(loan);

    }

    @Test
    @DisplayName("Deve lançar um erro ao tentar realizar um emprestimo quando o livro ja estiver emprestado.")
    public void loanedBookTest() {
        Loan savingLoan = createLoan();

        Mockito.when(repository.existsByBookAndNotReturned(savingLoan.getBook())).thenReturn(true);
        Throwable exception = catchThrowable(() -> service.save(savingLoan));

        assertThat(exception).isInstanceOf(BussinessException.class)
                .hasMessage("Book already loaned.");

        Mockito.verify(repository, Mockito.never()).save(savingLoan);

    }


    @Test
    @DisplayName("Deve obter as informaçoes de emprestipo pelo ID")
    public void getLoanDetailsTest() {
        Long id = 1l;
        Loan loan = createLoan();
        loan.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(loan));

        Optional<Loan> loanFound = service.getById(id);


        assertThat(loanFound.isPresent()).isTrue();
        assertThat(loanFound.get().getId()).isEqualTo(id);
        assertThat(loanFound.get().getCustomer()).isEqualTo(loan.getCustomer());
        assertThat(loanFound.get().getBook().getId()).isEqualTo(loan.getBook().getId());
        assertThat(loanFound.get().getLoanDate()).isEqualTo(loan.getLoanDate());

        Mockito.verify(repository, Mockito.times(1)).findById(id);

    }

    @Test
    @DisplayName("Deve atualizar emprestimo")
    public void updateLoanTest() {
        Loan loan = createLoan();
        loan.setId(1l);
        loan.setReturned(true);
        Mockito.when(repository.save(loan)).thenReturn(loan);
        Loan updatedLoan = service.update(loan);
        assertThat(updatedLoan.getReturned()).isTrue();
        Mockito.verify(repository, Mockito.times(1)).save(loan);
    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBookTest() {
        LoanFilterDTO loanFilterDTO = LoanFilterDTO.builder().customer("Fulano").isbn("123").build();
        Loan loan = createLoan();
        loan.setId(1l);

        Page<Loan> page = new PageImpl<Loan>(Arrays.asList(loan), PageRequest.of(0,10), 1);
        Mockito.when(repository.findByBookIsbnOrCustomer(Mockito.anyString(),Mockito.anyString(), Mockito.any(PageRequest.class)))
                .thenReturn(page);
        Page<Loan> result = service.find(loanFilterDTO, PageRequest.of(0,10));

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(Arrays.asList(loan));
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);

    }

    private Loan createLoan() {
        return Loan.builder()
                .book(
                    Book.builder().id(1l).isbn("123").build()
                )
                .loanDate(LocalDate.now())
                .customer("Fulano")
                .build();
    }
 }
