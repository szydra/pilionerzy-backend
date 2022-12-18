package pl.pilionerzy.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.PagingAndSortingRepository;
import pl.pilionerzy.model.Question;

public interface QuestionRepository extends PagingAndSortingRepository<Question, Long> {

    Integer countByActive(Boolean active);

    Slice<Question> findByActive(Boolean active, Pageable pageable);
}
