package com.paulo.libraryapi.api.resource;

import com.paulo.libraryapi.api.dto.BookDTO;
import com.paulo.libraryapi.api.dto.LoanDTO;
import com.paulo.libraryapi.model.entity.Book;
import com.paulo.libraryapi.model.entity.Loan;
import com.paulo.libraryapi.service.BookService;
import com.paulo.libraryapi.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Api("Book API")
public class BookController {

    private final BookService service;
    private final ModelMapper modelMapper;
    private final LoanService loanService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("CREATE A BOOK")
    public BookDTO create(@RequestBody @Valid BookDTO dto) {
        Book entity = modelMapper.map(dto, Book.class);

        entity = service.save(entity);

        return modelMapper.map(entity, BookDTO.class);
    }

    @GetMapping("{id}")
    @ApiOperation("GET A BOOK DETAILS BY ID")
    public BookDTO get(@PathVariable Long id) {
        return service.getById(id)
                .map( book -> modelMapper.map(book, BookDTO.class))
                .orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("DELETE A BOOK BY ID")
    public void delete(@PathVariable Long id) {
        Book book = service.getById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        service.delete(book);
    }

    @PutMapping("{id}")
    @ApiOperation("UPDATE A BOOK BY ID")
    public BookDTO update(@PathVariable Long id, @RequestBody @Valid BookDTO dto) {
        Book book = service.getById(id).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        book.setAuthor(dto.getAuthor());
        book.setTitle(dto.getTitle());
        book = service.update(book);
        return modelMapper.map(book, BookDTO.class);
    }

    @GetMapping
    @ApiOperation("FIND BOOKS")
    public PageImpl<BookDTO> find(BookDTO dto, Pageable pageRequest) {
        Book filter = modelMapper.map(dto, Book.class);
        Page<Book> result = service.find(filter, pageRequest);
        List<BookDTO> list = result.getContent().stream()
                .map(entity -> modelMapper.map(entity, BookDTO.class))
                .collect(Collectors.toList());
        return new PageImpl<BookDTO>(list,pageRequest, result.getTotalElements());
    }

    @GetMapping("{id}/loans")
    @ApiOperation("FIND LOANS BY BOOK ID")
    public Page<LoanDTO> loansByBook(@PathVariable Long id, Pageable pageRequest) {
        Book book = service.getById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );

        Page<Loan> result = loanService.getLoansByBook(book, pageRequest);
        List<LoanDTO> list = result.getContent().stream()
                .map( entity -> {
                    Book book1 = entity.getBook();
                    BookDTO bookDTO = modelMapper.map(book1, BookDTO.class);
                    LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
                    loanDTO.setBook(bookDTO);
                    return  loanDTO;
                })
                .collect(Collectors.toList());

        return new PageImpl<LoanDTO>(list,pageRequest, result.getTotalElements());

    }

}
