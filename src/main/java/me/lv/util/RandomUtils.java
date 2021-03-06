package me.lv.util;

import org.apache.commons.lang.StringUtils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomUtils {
	private static final Random random = new Random();

	private RandomUtils() {
	}

	/**
	 * 生成num个长度为length的字符串（字符串各不相同）,字符串只包含字母
	 * 
	 * @param length
	 *            字符串的长度
	 * @param num
	 *            字符串的个数
	 * @return
	 */
	public static String[] random(final int length, final int num) {
		return new RandomUtils().buildRandom(length, num);
	}

	/**
	 * 生成长度为length的字符串,字符串只包含数字
	 * 
	 * @param length
	 *            字符串的长度
	 * @return
	 */
	public static String random(final int length) {
		return new RandomUtils().buildRandom(length);
	}

	/**
	 * 生成num个长度为length的字符串，组成如 123-123-123 格式(只包含数字)
	 * 
	 * @param length
	 * @param num
	 * @return
	 *
	 */
	public static String randombunch(int length, int num) {
		StringBuffer str = new StringBuffer();
		for (int i = 0; i < num; i++) {
			str.append(RandomUtils.random(length));
			if (i != num - 1) {
				str.append("-");
			}
		}
		return str.toString();
	}

	/**
	 * 生成num个长度为length的字符串（字符串各不相同）,字符串只包含字母
	 * 
	 * @param length
	 *            字符串的长度
	 * @param num
	 *            字符串的个数
	 * @return
	 */
	private String[] buildRandom(final int length, final int num) {
		if (num < 1 || length < 1) {
			return null;
		}
		Set<String> tempRets = new HashSet<String>(num); // 存放临时结果，以避免重复值的发生

		// 生成num个不相同的字符串
		while (tempRets.size() < num) {
			tempRets.add(buildRandom(length));
		}

		String[] rets = new String[num];
		rets = tempRets.toArray(rets);
		return rets;
	}

	/**
	 * 返回指定位数的整数
	 * 
	 * @param length
	 * @return
	 * 
	 */
	public static int buildIntRandom(final int length) {
		String maxStr = StringUtils.rightPad("1", length + 1, '0');
		long max = Long.parseLong(maxStr);
		long i = Math.abs(random.nextLong()) % max;
		String rand = String.valueOf(i);
		return Integer.parseInt(rand);
	}

	/**
	 * 取小于指定范围内的整数
	 * 
	 * @param length
	 * @return
	 * 
	 */
	public static int buildIntRandomBy(final int length) {
		return (int) (Math.random() * length);
	}

	/**
	 * 生成长度为length的字符串,字符串只包含数字
	 * 
	 * @param length
	 *            字符串的长度
	 * @return
	 */
	private String buildRandom(final int length) {
		// 长度为length的最多整数
		String maxStr = StringUtils.rightPad("1", length + 1, '0');
		long max = Long.parseLong(maxStr);
		// 取得随机数
		long i = random.nextLong();
		// 取正数，并限制其长度
		i = Math.abs(i) % max;
		String value = StringUtils.leftPad(String.valueOf(i), length, '0');
		return value;
	}

}