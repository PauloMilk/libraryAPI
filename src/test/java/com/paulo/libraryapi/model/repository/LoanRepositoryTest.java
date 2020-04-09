package com.paulo.libraryapi.model.repository;

import com.paulo.libraryapi.model.entity.Book;
import com.paulo.libraryapi.model.entity.Loan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LoanRepositoryTest {

    @Autowired
    LoanRepository repository;

    @Autowired
    EntityManager entityManager;

    @Test
    @DisplayName("Deve verificar se existe um emprestimo para livro nao devolvido.")
    public void existByBookAndNotReturnedTest() {

        Loan loan = createAndPersistLoan();

        Boolean exists = repository.existsByBookAndNotReturned(loan.getBook());

        assertThat(exists).isTrue();

    }


    @Test
    @DisplayName("Deve buscas emprestimo pelo isbn do livro ou customer.")
    public void findLoanByIsbnOrCustomerTest() {

        Loan loan = createAndPersistLoan();

        Page<Loan> result = repository.findByBookIsbnOrCustomer("Fulano", "123", PageRequest.of(0, 10));

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent()).contains(loan);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("Deve obter emprestimos cuja data de emprestimo for menor ou igual a tres dias.")
    public void  findByLoanDateLessThanAndNotReturnedTest() {
        Loan loan = createAndPersistLoan();
        loan.setLoanDate(LocalDate.now().minusDays(5));
        entityManager.persist(loan);

        List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(result).hasSize(1).contains(loan);

    }

    @Test
    @DisplayName("Deve nao obter emprestimos cuja data de emprestimo for menor ou igual a tres dias.")
    public void  notfindByLoanDateLessThanAndNotReturnedTest() {
        Loan loan = createAndPersistLoan();

        List<Loan> result = repository.findByLoanDateLessThanAndNotReturned(LocalDate.now().minusDays(4));

        assertThat(result).isEmpty();

    }
    private Book createNewBook() {
        return Book.builder().title("As aventuras").author("Artur").isbn("123").build();
    }

    private Loan createAndPersistLoan() {
        Book book = createNewBook();
        entityManager.persist(book);

        Loan loan = Loan.builder()
                    .book(book)
                    .customer("Fulano")
                    .loanDate(LocalDate.now())
                    .build();

        entityManager.persist(loan);
        return loan;
    }

}
