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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Optional;

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

    @Test
    @DisplayName("Deve obter um livro por id")
    public void getByIdTest() {
        Long id = 1l;
        Book book = createValidBook();
        book.setId(id);
        Mockito.when(repository.findById(id)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = service.getById(id);

        assertThat( foundBook.isPresent() ).isTrue();
        assertThat( foundBook.get().getId() ).isEqualTo(id);
        assertThat( foundBook.get().getAuthor() ).isEqualTo(book.getAuthor());
        assertThat( foundBook.get().getTitle() ).isEqualTo(book.getTitle());
        assertThat( foundBook.get().getIsbn() ).isEqualTo(book.getIsbn());
    }

    @Test
    @DisplayName("Deve lançar resource not found quando buscar por livro inexistente.")
    public void getByIdNotFoundBookTest() {
        Long id = 1l;

        Mockito.when(repository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Optional<Book> foundBook = service.getById(id);

        assertThat( foundBook.isPresent() ).isFalse();

    }

    @Test
    @DisplayName("Deve remover um livro")
    public void deleteByBookIdTest() {
        Book book = createValidBook();
        book.setId(1l);
        service.delete(book);

        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve lançar uma exception ao tentar remover um livro sem id.")
    public void deleteByIdBookNotIdTest() {
        Book book = createValidBook();
        Throwable exception = Assertions.catchThrowable(() -> service.delete(book));
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book id cant be null.");

        Mockito.verify(repository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro.")
    public void updateBookTest() {
        Book book = createValidBook();
        book.setId(1l);
        service.update(book);

        Mockito.verify(repository, Mockito.times(1)).save(book);
    }

    @Test
    @DisplayName("Deve lançar uma exception ao tentar atualizar um livro sem id.")
    public void updateBookNotIdTest() {
        Book book = createValidBook();
        Throwable exception = Assertions.catchThrowable(() -> service.update(book));
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Book id cant be null.");

        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve filtrar livros pelas propriedades")
    public void findBookTest() {
        Book book = createValidBook();

        Page<Book> page = new PageImpl<Book>(Arrays.asList(book), PageRequest.of(0,10), 1);
        Mockito.when(repository.findAll(Mockito.any(Example.class), Mockito.any(PageRequest.class)))
                .thenReturn(page);
        Page<Book> result = service.find(book, PageRequest.of(0,10));
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(Arrays.asList(book));
        assertThat(result.getPageable().getPageNumber()).isEqualTo(0);
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);

    }

    private Book createValidBook() {
        return Book.builder().isbn("123").author("Fulano").title("As aventuras").build();
    }

}
