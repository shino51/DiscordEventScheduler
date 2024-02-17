package org.shino.repository;

import org.shino.model.Event;
import org.shino.model.Frequency;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<Event, Integer> {

  List<Event> findByFrequency(Frequency frequency);
  List<Event> findByFrequencyIn(Frequency... frequencyArray);
}
