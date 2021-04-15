package com.codesquad.todoList.controller;

import com.codesquad.todoList.dto.CardInfoDto;
import com.codesquad.todoList.entity.Card;
import com.codesquad.todoList.entity.Project;
import com.codesquad.todoList.repository.CardRepository;
import com.codesquad.todoList.service.ColumnService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/columns/{id}/cards")
@RequiredArgsConstructor
public class ApiCardController {

    private static final Logger log = LoggerFactory.getLogger(ApiCardController.class);

    private final ColumnService columnService;

    @PostMapping()
    public ResponseEntity<?> addCard(@Validated @RequestBody Card card, @PathVariable Long id, BindingResult bindingResult) {
        final Card saveCard = columnService.addCard(id, card);
        return ResponseEntity.ok().body(saveCard);
    }


    @DeleteMapping("/{cardId}")
    public ResponseEntity<?> deleteCard(@PathVariable Long id, @PathVariable Long cardId) {
        columnService.deleteCard(id, cardId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<?> updateCard(@PathVariable Long id, @PathVariable Long cardId, @Validated @RequestBody Card card, BindingResult bindingResult) {
        columnService.updateCard(id, cardId, card);
        return ResponseEntity.ok().build();
    }

}
