package se.unlogic.standardutils.random;

import java.util.Random;

public class RandomUtils {

	private static final Random RANDOM = new Random();

	public static String getRandomString(int minLength, int maxLength){

		//Generate new random id


		int length = RANDOM.nextInt(maxLength - minLength) + minLength;
		char[] UniqueLink = new char[length];

		for (int x = 0; x < length; x++) {
			int randDecimalAsciiVal = RANDOM.nextInt(25) + 97;
			UniqueLink[x] = (char) randDecimalAsciiVal;
		}

		return new String(UniqueLink);
	}

	public static long getRandomInt(int min, int max) {

		return RANDOM.nextInt(max) + min;
	}
}
