package my.project.productservice.persistence.repository;

import my.project.productservice.persistence.entity.ProcessedEvent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProcessedEventRepository extends CrudRepository<ProcessedEvent, Long> {

    boolean existsByMessageId(String messageId);
}
