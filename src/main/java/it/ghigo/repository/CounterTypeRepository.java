package it.ghigo.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.ghigo.model.CounterType;

public interface CounterTypeRepository extends CrudRepository<CounterType, Long> {

	public List<CounterType> findAllByOrderBySeqAsc();
}