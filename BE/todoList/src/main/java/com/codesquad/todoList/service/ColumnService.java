package com.codesquad.todoList.service;

import com.codesquad.todoList.entity.*;
import com.codesquad.todoList.error.exception.NotFoundCardException;
import com.codesquad.todoList.error.exception.NotFoundColumnException;
import com.codesquad.todoList.error.exception.NotFoundProjectException;
import com.codesquad.todoList.repository.ColumnRepository;
import com.codesquad.todoList.repository.NoteRepository;
import com.codesquad.todoList.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ColumnService {

    private static final Logger log = LoggerFactory.getLogger(ColumnService.class);

    private final ColumnRepository columnRepository;
    private final ProjectRepository projectRepository;
    private final NoteRepository noteRepository;

    @Transactional
    public void addColumn(Columns columns) {
        Project project = projectRepository.findById(1L).orElseThrow(NotFoundProjectException::new);
        project.addColumn(columns);
        projectRepository.save(project);
    }

    @Transactional
    public Card addCard(Long columnId, Card card) {
        Project project = projectRepository.findById(1L).orElseThrow(NotFoundProjectException::new);
        Columns columns = columnRepository.findById(columnId).orElseThrow(NotFoundColumnException::new);

        columns.addCard(card);

        saveNote(new Note(), columns, Action.CREATE, card);
        columnRepository.save(columns);
        return card;
    }

    @Transactional
    public void deleteCard(Long columnId, String cardId) {
        Project project = projectRepository.findById(1L).orElseThrow(NotFoundProjectException::new);
        Columns columns = columnRepository.findById(columnId).orElseThrow(NotFoundColumnException::new);
        Card card = columns.deleteCard(cardId);
        updateColumn(columns, project);

        saveNote(new Note(), columns, Action.DELETE, card);

        projectRepository.save(project);
    }

    @Transactional
    public void updateCard(Long columnId, String cardId, Card card) {
        Project project = projectRepository.findById(1L).orElseThrow(NotFoundProjectException::new);
        Columns columns = columnRepository.findById(columnId).orElseThrow(NotFoundColumnException::new);
        Note note = new Note();
        Card updatedCard = null;

        for(Card beforeCard : columns.getCardList()) {
            if(beforeCard.getId().equals(cardId)) {
                updatedCard = beforeCard.update(card);
            }
        }

        if(updatedCard == null) {
            throw new NotFoundCardException();
        }

        saveNote(note, columns, Action.UPDATE, updatedCard);

        updateColumn(columns, project);
        projectRepository.save(project);
    }

    @Transactional
    public void updateColumn(Long columnId, Columns columns) {
        Project project = projectRepository.findById(1L).orElseThrow(NotFoundProjectException::new);
        Columns beforeColumn = columnRepository.findById(columnId).orElseThrow(NotFoundColumnException::new);
        beforeColumn.updateColumn(columns);
        updateColumn(beforeColumn, project);
        projectRepository.save(project);
    }

    public List<Card> getColumn(Long id) {
       return columnRepository.findById(id).orElseThrow(NotFoundColumnException::new).getCardList();
    }

    private void updateColumn(Columns columns, Project project) {
        for(Columns column : project.getColumns()) {
            if(column.equals(columns)) {
                column.updateColumn(columns);
            }
        }
    }

    private void saveNote(Note note, Columns columns, Action action, Card card) {
        note.setBeforeStatus(columns.getName());
        note.setAfterStatus(columns.getName());
        note.setAction(action);
        note.setTitle(card.getTitle());
        noteRepository.save(note);
    }

}
