package se.unlogic.standardutils.dao;

import se.unlogic.standardutils.dao.enums.Order;

public class OrderByCriteria<T> {

	private Order order;
	private Column<T, ?> column;

	public OrderByCriteria(Order order, Column<T, ?> column) {

		super();
		this.order = order;
		this.column = column;
	}

	public Order getOrder() {

		return order;
	}

	public Column<T, ?> getColumn() {

		return column;
	}

}
