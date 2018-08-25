package pl.pilionerzy.dao;

import org.springframework.data.repository.PagingAndSortingRepository;
import pl.pilionerzy.model.Question;

public interface QuestionDao extends PagingAndSortingRepository<Question, Long> {

}
