package com.paulo.libraryapi.service;


import com.paulo.libraryapi.exception.BussinessException;
import com.paulo.libraryapi.model.entity.Book;
import com.paulo.libraryapi.repository.BookRepository;
import com.paulo.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    void setUp() {
        this.service = new BookServiceImpl(repository);
    }

    @Test
       @DisplayName("Deve salvar um livro")
       public void saveBookTest() {
           Book book = createValidBook();
           Book saveBook = Book.builder().id(1l).isbn("123").author("Fulano").title("As aventuras").build();
        Mockito.when(repository.existsByIsbn(book.getIsbn())).thenReturn(false);
           Mockito.when( repository.save(book)).thenReturn(saveBook);

           Book savedBook = service.save(book);

           assertThat(savedBook.getId()).isNotNull();
           assertThat(savedBook.getIsbn()).isEqualTo(book.getIsbn());
           assertThat(savedBook.getAuthor()).isEqualTo(book.getAuthor());
           assertThat(savedBook.getTitle()).isEqualTo(book.getTitle());

       }


       @Test
       @DisplayName("Deve lançar erro de negocio ao tentar salvar um livro com isbn duplicado.")
       public void shouldNotSaveWithDuplicatedISBN() {
            Book book = createValidBook();
            Mockito.when(repository.existsByIsbn(book.getIsbn())).thenReturn(true);
            Throwable exception = Assertions.catchThrowable(() -> service.save(book));
            assertThat(exception)
                .isInstanceOf(BussinessException.class)
                .hasMessage("Isbn já cadastrado.");

            Mockito.verify(repository, Mockito.never()).save(book);


    }

    private Book createValidBook() {
        return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
    }

}
