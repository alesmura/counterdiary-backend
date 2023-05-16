package it.ghigo.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.ghigo.dto.CounterDTO;
import it.ghigo.model.Counter;
import it.ghigo.model.CounterType;
import it.ghigo.repository.CounterRepository;

@Component
public class CounterService {

	@Autowired
	private CounterTypeService counterTypeService;

	@Autowired
	private CounterRepository counterRepository;

	public List<CounterDTO> findByEventDate(Date eventDate) {
		List<CounterType> counterTypeList = counterTypeService.findOrderBySequence();
		return counterTypeList.stream().map(ct -> getDTO(getOrCreate(eventDate, ct))).toList();
	}

	private CounterDTO getDTO(Counter c) {
		return new CounterDTO(c.getId(), counterTypeService.getDTO(c.getCounterType()), c.getEventDate(),
				c.getCounter());
	}

	private Counter getOrCreate(Date eventDate, CounterType ct) {
		Optional<Counter> cOpt = counterRepository.findByEventDateAndCounterTypeId(eventDate, ct.getId());
		if (cOpt.isPresent())
			return cOpt.get();
		return create(eventDate, ct);
	}

	private Counter create(Date eventDate, CounterType ct) {
		Counter c = new Counter();
		c.setCounterType(ct);
		c.setEventDate(eventDate);
		c.setCounter(0);
		return counterRepository.save(c);
	}

	public void updateCounter(CounterDTO cDTO) throws Exception {
		Optional<Counter> cOpt = counterRepository.findById(cDTO.id());
		if (!cOpt.isPresent())
			throw new Exception("Counter " + cDTO.id() + " not found!");
		Counter c = cOpt.get();
		c.setCounter(cDTO.counter());
		counterRepository.save(c);
	}
}