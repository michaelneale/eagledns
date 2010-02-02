package se.unlogic.standardutils.dao;

import java.util.Comparator;

import se.unlogic.standardutils.dao.annotations.OrderBy;

public class OrderByComparator implements Comparator<OrderBy> {

	public int compare(OrderBy o1, OrderBy o2) {

		if (o1.priority() < o2.priority()) {
			
			return 1;
			
		} else if (o1.priority() == o2.priority()) {
			
			return 0;
			
		} else {
			
			return -1;
		}
	}
}
