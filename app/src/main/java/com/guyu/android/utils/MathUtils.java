package com.guyu.android.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import android.util.Log;

public class MathUtils {
	private static final String TAG = "MathUtils";

	/**
	 * 提供精确的小数位四舍五入功能
	 * @param v 需要四舍五入的数字
	 * @param scale 小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static double GetAccurateNumber(double v,int scale) {

		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).stripTrailingZeros().doubleValue();
	}

	/**
	 * 提供精确的小数位四舍五入功能
	 * @param number 需要四舍五入的数字
	 * @param scale 小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static String round(double number,int scale) {
		NumberFormat nf = NumberFormat.getInstance();
		nf.setRoundingMode(RoundingMode.HALF_UP);//设置四舍五入
		nf.setGroupingUsed(false);
		nf.setMinimumFractionDigits(scale);//设置最小保留几位小数
		nf.setMaximumFractionDigits(scale);//设置最大保留几位小数
		return nf.format(number);
	}
	
}
