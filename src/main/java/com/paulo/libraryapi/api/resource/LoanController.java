package com.paulo.libraryapi.api.resource;

import com.paulo.libraryapi.api.dto.BookDTO;
import com.paulo.libraryapi.api.dto.LoanDTO;
import com.paulo.libraryapi.api.dto.LoanFilterDTO;
import com.paulo.libraryapi.api.dto.ReturnedLoanDTO;
import com.paulo.libraryapi.model.entity.Book;
import com.paulo.libraryapi.model.entity.Loan;
import com.paulo.libraryapi.service.BookService;
import com.paulo.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService service;
    private final BookService bookService;
    private final ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody @Valid LoanDTO dto) {
        Book book = bookService.getBookByIsbn(dto.getIsbn()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn.")
        );
        Loan entity = Loan.builder().book(book).customer(dto.getCustomer()).loanDate(LocalDate.now()).customerEmail(dto.getEmail()).build();
        entity = service.save(entity);
        return entity.getId();
    }

    @PatchMapping("{id}")
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanDTO dto) {
        Loan loan = service.getById(id).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND)
        );
        loan.setReturned(dto.getReturned());
        service.update(loan);
    }

    @GetMapping
    public Page<LoanDTO> find(LoanFilterDTO dto, Pageable pageRequest) {
        Page<Loan> result = service.find(dto, pageRequest);

        List<LoanDTO> list = result.getContent().stream()
                .map( entity -> {
                    Book book = entity.getBook();
                    BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
                    LoanDTO loanDTO = modelMapper.map(entity, LoanDTO.class);
                    loanDTO.setBook(bookDTO);
                    return loanDTO;
                })
                .collect(Collectors.toList());

        return new PageImpl<LoanDTO>(list, pageRequest, result.getTotalElements());
    }

}
