package it.ghigo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.ghigo.dto.CounterTypeDTO;
import it.ghigo.model.Counter;
import it.ghigo.model.CounterType;
import it.ghigo.repository.CounterRepository;
import it.ghigo.repository.CounterTypeRepository;

@Component
public class CounterTypeService {

	@Autowired
	private CounterTypeRepository counterTypeRepository;

	@Autowired
	private CounterRepository counterRepository;

	public List<CounterTypeDTO> findDTOOrderBySequence() {
		return findOrderBySequence().stream().map(ct -> getDTO(ct)).toList();
	}

	public List<CounterType> findOrderBySequence() {
		return counterTypeRepository.findAllByOrderBySeqAsc();
	}

	public void save(CounterTypeDTO ctDTO) {
		Optional<CounterType> ctOpt = counterTypeRepository.findById(ctDTO.id());
		CounterType ct = ctOpt.orElse(new CounterType());
		ct.setName(ctDTO.name());
		ct.setDescription(ctDTO.description());
		ct.setSeq(ctDTO.seq());
		counterTypeRepository.save(ct);
		//
		compactSequence();
	}

	private void compactSequence() {
		int seq = 1;
		List<CounterType> ctList = counterTypeRepository.findAllByOrderBySeqAsc();
		for (CounterType ct : ctList) {
			ct.setSeq(seq++);
			counterTypeRepository.save(ct);
		}
	}

	public CounterTypeDTO getDTO(CounterType ct) {
		return new CounterTypeDTO(ct.getId(), ct.getSeq(), ct.getName(), ct.getDescription());
	}

	public void delete(Long id) throws Exception {
		Optional<CounterType> ctOpt = counterTypeRepository.findById(id);
		if (!ctOpt.isPresent())
			throw new Exception("Counter type ID: " + id + " not found");
		// Delete counter by type
		List<Counter> counterList = counterRepository.findByCounterTypeId(id);
		for (Counter c : counterList)
			counterRepository.delete(c);
		// Delete counter type
		counterTypeRepository.delete(ctOpt.get());
		// Compact sequence
		compactSequence();
	}
}