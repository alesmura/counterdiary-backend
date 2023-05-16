package it.ghigo.service;

import java.time.DayOfWeek;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.ghigo.dto.CounterStatsDTO;
import it.ghigo.dto.CounterTypeDTO;
import it.ghigo.dto.DatasetCounterStatsDTO;
import it.ghigo.dto.STATS_TYPE;
import it.ghigo.repository.CounterStatsFinder;
import jakarta.persistence.Tuple;

@Component
public class CounterStatsService {

	@Autowired
	private CounterTypeService counterTypeService;
	@Autowired
	private CounterStatsFinder counterStatsFinder;

	public DatasetCounterStatsDTO findByCounterTypeIdAndTypeAndPeriod(Long counterTypeId, STATS_TYPE type,
			String period) throws Exception {
		List<Tuple> tupleList = counterStatsFinder.findByCounterTypeIdAndTypeAndPeriod(counterTypeId, type, period);
		//
		String labelDataset = getLabelDataset(type, period);
		List<CounterStatsDTO> csDTOList = generateStats(type, period, tupleList);
		CounterTypeDTO ctDTO = counterTypeService.getDTO(counterTypeId);
		DatasetCounterStatsDTO dto = new DatasetCounterStatsDTO(labelDataset, ctDTO, csDTOList);
		return dto;
	}

	private List<CounterStatsDTO> generateStats(STATS_TYPE type, String period, List<Tuple> tupleList) {

		return getEntryList(type, period).stream().map(it -> {
			int id = it.getKey();
			String label = it.getValue();
			Optional<Long> countOpt = tupleList.stream().filter(t -> id == (int) t.get(0)).findFirst()
					.map(t -> (long) t.get(1));
			CounterStatsDTO csDTO = new CounterStatsDTO(id, label, countOpt.orElse(0l));
			return csDTO;
		}).toList();
	}

	private List<SimpleEntry<Integer, String>> getEntryList(STATS_TYPE type, String period) {
		if (type == STATS_TYPE.YEARLY) {
			return Stream.of(Month.values()).map(it -> new SimpleEntry<>(it.getValue(),
					it.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()))).toList();
		}
		if (type == STATS_TYPE.MONTHLY) {
			String[] split = period.split("-");
			YearMonth yearMonthObject = YearMonth.of(Integer.valueOf(split[0]), Integer.valueOf(split[1]));
			int daysInMonth = yearMonthObject.lengthOfMonth();
			List<SimpleEntry<Integer, String>> retList = new ArrayList<>(daysInMonth);
			while (retList.size() < daysInMonth) {
				int i = retList.size() + 1;
				retList.add(new SimpleEntry<>(i, String.valueOf(i)));
			}
			return retList;
		}
		if (type == STATS_TYPE.WEEKLY) {
			String[] split = period.split("-");
			int anno = Integer.valueOf(split[0]);
			int settimana = Integer.valueOf(split[1]);
			//
			Calendar cal = Calendar.getInstance();
			cal.setWeekDate(anno, settimana, Calendar.MONDAY);
			List<SimpleEntry<Integer, String>> retList = new ArrayList<>(7);
			while (retList.size() < 7) {
				int day = cal.get(Calendar.DAY_OF_MONTH);
				int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
				if (dayOfWeek == Calendar.SUNDAY)
					dayOfWeek = DayOfWeek.SUNDAY.getValue();
				else
					--dayOfWeek;
				retList.add(new SimpleEntry<>(day, //
						DayOfWeek.of(dayOfWeek).getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()) //
				));
				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
			//
			return retList;
		}
		return null;
	}

	private String getLabelDataset(STATS_TYPE type, String period) {
		if (type == STATS_TYPE.YEARLY)
			return "Year " + period;
		if (type == STATS_TYPE.MONTHLY) {
			String[] split = period.split("-");
			return "Month " + Month.values()[Integer.valueOf(split[1]) - 1] //
					.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault()) + " year " + split[0];
		}
		if (type == STATS_TYPE.WEEKLY) {
			String[] split = period.split("-");
			return "Week " + split[1] + " year " + split[0];
		}
		return "";
	}
}