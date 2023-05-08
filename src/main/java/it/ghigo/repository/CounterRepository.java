package it.ghigo.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import it.ghigo.model.Counter;

public interface CounterRepository extends CrudRepository<Counter, Long> {
	public Optional<Counter> findByEventDateAndCounterTypeId(Date eventDate, long counterTypeID);

	public List<Counter> findByCounterTypeId(long counterTypeID);
}