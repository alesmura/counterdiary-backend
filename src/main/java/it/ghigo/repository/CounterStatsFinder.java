package it.ghigo.repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import it.ghigo.dto.STATS_TYPE;
import it.ghigo.model.Counter;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class CounterStatsFinder {
	@Autowired
	private EntityManager entityManager;

	public List<Tuple> findByCounterTypeIdAndTypeAndPeriod(Long counterTypeId, STATS_TYPE type, String period)
			throws Exception {
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> query = builder.createTupleQuery();
		Root<Counter> c = query.from(Counter.class);
		//
		Expression<Long> expCt = c.join("counterType").get("id");
		Expression<Date> expDt = c.get("eventDate");
		Expression<Integer> expSelect = getExpressionSelect(builder, expDt, type);
		List<Predicate> predicateList = new ArrayList<>();
		predicateList.add(getPredicateDate(builder, expDt, type, period));
		predicateList.add(builder.equal(expCt, counterTypeId));
		//
		query.multiselect(expSelect, builder.sum(c.get("counter"))) //
				.where(builder.and(predicateList.toArray(new Predicate[0]))) //
				.groupBy(expSelect).orderBy(builder.asc(expSelect));
		return entityManager.createQuery(query).getResultList();
	}

	private Expression<Integer> getExpressionSelect(CriteriaBuilder builder, Expression<Date> exp, STATS_TYPE type) {
		if (type == STATS_TYPE.YEARLY)
			return builder.function("MONTH", Integer.class, exp);
		if (type == STATS_TYPE.MONTHLY)
			return builder.function("DAY", Integer.class, exp);
		if (type == STATS_TYPE.WEEKLY)
			return builder.function("DAY", Integer.class, exp);
		return null;
	}

	private Predicate getPredicateDate(CriteriaBuilder builder, Expression<Date> exp, STATS_TYPE type, String period)
			throws Exception {

		Date startDate = null;
		Date endDate = null;

		if (type == STATS_TYPE.YEARLY) {
			int anno = Integer.valueOf(period);
			//
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, anno);
			cal.set(Calendar.DAY_OF_YEAR, 1);
			startDate = cal.getTime();
			//
			cal.add(Calendar.YEAR, 1);
			cal.add(Calendar.DAY_OF_YEAR, -1);
			endDate = cal.getTime();
		}

		if (type == STATS_TYPE.MONTHLY) {
			String[] split = period.split("-");
			int anno = Integer.valueOf(split[0]);
			int mese = Integer.valueOf(split[1]);
			//
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR, anno);
			cal.set(Calendar.MONTH, mese - 1);
			cal.set(Calendar.DAY_OF_MONTH, 1);
			startDate = cal.getTime();
			//
			cal.add(Calendar.MONTH, 1);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			endDate = cal.getTime();
		}

		if (type == STATS_TYPE.WEEKLY) {
			String[] split = period.split("-");
			int anno = Integer.valueOf(split[0]);
			int settimana = Integer.valueOf(split[1]);
			//
			Calendar cal = Calendar.getInstance();
			cal.setWeekDate(anno, settimana, Calendar.MONDAY);
			startDate = cal.getTime();
			//
			cal.add(Calendar.DAY_OF_MONTH, 6);
			endDate = cal.getTime();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		startDate = sdf.parse(sdf.format(startDate));
		endDate = sdf.parse(sdf.format(endDate));
		return builder.between(exp, builder.literal(startDate), builder.literal(endDate));
	}
}