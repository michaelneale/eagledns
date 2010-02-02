package se.unlogic.standardutils.numbers;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

public class NumberUtils {

	private static final Pattern NUMBER_PATTERN = Pattern.compile("\\D*");

	public static boolean isInt(String value) {
		if (value != null) {
			try {
				Integer.parseInt(value);
				return true;
			} catch (NumberFormatException e) {
			}
		}
		return false;
	}

	public static boolean isLong(String value) {
		if (value != null) {
			try {
				Long.parseLong(value);
				return true;
			} catch (NumberFormatException e) {
			}
		}
		return false;
	}

	public static boolean isFloat(String value) {
		if (value != null) {
			try {
				Float.parseFloat(value);
				return true;
			} catch (NumberFormatException e) {
			}
		}
		return false;
	}

	public static boolean isDouble(String value) {
		if (value != null) {
			try {
				Double.parseDouble(value);
				return true;
			} catch (NumberFormatException e) {
			}
		}
		return false;
	}

	public static Integer toInt(String value) {
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
			}
		}

		return null;
	}

	public static ArrayList<Integer> toInt(Collection<String> values) {

		if (values == null) {
			return null;
		} else {
			ArrayList<Integer> validIntegers = new ArrayList<Integer>();

			for (String value : values) {
				if (value != null) {
					Integer intValue = NumberUtils.toInt(value);

					if (intValue != null) {
						validIntegers.add(intValue);
					}
				}
			}

			if (validIntegers.isEmpty()) {
				return null;
			} else {
				return validIntegers;
			}
		}
	}

	public static ArrayList<Integer> toInt(String[] values) {

		if (values == null) {
			return null;
		} else {
			ArrayList<Integer> validIntegers = new ArrayList<Integer>();

			for (String value : values) {
				if (value != null) {
					Integer intValue = NumberUtils.toInt(value);

					if (intValue != null) {
						validIntegers.add(intValue);
					}
				}
			}

			if (validIntegers.isEmpty()) {
				return null;
			} else {
				return validIntegers;
			}
		}
	}

	public static Long toLong(String value) {
		if (value != null) {
			try {
				return Long.parseLong(value);
			} catch (NumberFormatException e) {
			}
		}

		return null;
	}

	public static Float toFloat(String value) {

		if (value != null) {
			try {
				return Float.parseFloat(value);
			} catch (NumberFormatException e) {
			}
		}

		return null;
	}

	public static Double toDouble(String value) {
		if (value != null) {
			try {
				return Double.parseDouble(value);
			} catch (NumberFormatException e) {
			}
		}

		return null;
	}

	public static List<Double> toDouble(List<String> values) {

		if (values == null) {
			return null;
		} else {
			ArrayList<Double> validDoubles = new ArrayList<Double>();

			for (String value : values) {
				if (value != null) {
					Double doubleValue = NumberUtils.toDouble(value);

					if (doubleValue != null) {
						validDoubles.add(doubleValue);
					}
				}
			}

			if (validDoubles.isEmpty()) {
				return null;
			} else {
				return validDoubles;
			}
		}
	}

	public static List<Long> toLong(List<String> values) {

		if (values == null) {
			return null;
		} else {
			ArrayList<Long> validLongs = new ArrayList<Long>();

			for (String value : values) {
				if (value != null) {
					Long LongValue = NumberUtils.toLong(value);

					if (LongValue != null) {
						validLongs.add(LongValue);
					}
				}
			}

			if (validLongs.isEmpty()) {
				return null;
			} else {
				return validLongs;
			}
		}
	}

	public static Long getNumbers(String revstring) {

		String result = NUMBER_PATTERN.matcher(revstring).replaceAll("");

		return toLong(result);
	}
	
	public static String formatNumber(Object value, int minDecimals, int maxDecimals, boolean grouping){
		
		DecimalFormat formatter = new DecimalFormat();
		
		formatter.setMinimumFractionDigits(minDecimals);
		
		formatter.setMaximumFractionDigits(maxDecimals);
		
		formatter.setGroupingUsed(grouping);

		return formatter.format(value);
		
	}
	
}
