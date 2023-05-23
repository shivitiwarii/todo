package com.assignment.todo;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<TodoValue, Long> {
    Page<TodoValue> findByCompleted(boolean completed, PageRequest pageRequest);
    List<TodoValue> findByUserUsername(String username);
    Page<TodoValue> findByUser(User user, PageRequest pageable);
    Page<TodoValue> findByUserId(Long id, PageRequest pageable);
    List<TodoValue> findByUserId(Long userId);

}
