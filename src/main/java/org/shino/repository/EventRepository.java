package org.shino.repository;

import org.shino.repository.model.Event;
import org.shino.repository.model.Frequency;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<Event, Integer> {

  List<Event> findByFrequency(Frequency frequency);
}
