package se.unlogic.standardutils.dao.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import se.unlogic.standardutils.dao.enums.Order;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface OrderBy{

	Order order() default Order.ASC;
	int priority() default 0;
}
