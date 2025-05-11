package com.weavict.edu.enumutil

import java.text.DecimalFormat

class MathFun
{
	String toint(BigDecimal v)
	{
		DecimalFormat df = new DecimalFormat("#");
		return df.format(v);
	}
}
