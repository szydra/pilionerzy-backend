package pl.pilionerzy.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import pl.pilionerzy.model.Question;

public interface QuestionDao extends PagingAndSortingRepository<Question, Long> {

    long countByActive(Boolean active);

    Page<Question> findByActive(Boolean active, Pageable pageable);

}
