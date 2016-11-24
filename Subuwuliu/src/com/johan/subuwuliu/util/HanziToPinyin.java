package com.johan.subuwuliu.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class HanziToPinyin {

	public static String[] ToPinyin(String hanzi) throws BadHanyuPinyinOutputFormatCombination {
		String[] result = new String[2];
		StringBuffer pinyinBuffer = new StringBuffer();
		StringBuffer firstLetterBuffer = new StringBuffer();
		HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();  
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);  
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);  
        for(int i = 0 ; i < hanzi.length() ; i++)  {  
            char c = hanzi.charAt(i);  
            String[] pinyinResult = PinyinHelper.toHanyuPinyinStringArray(c, format);
            pinyinBuffer.append(pinyinResult[0]);  
            firstLetterBuffer.append(pinyinResult[0].substring(0, 1));
        } 
        result[0] = pinyinBuffer.toString();
        result[1] = firstLetterBuffer.toString();
        return result;
	}
	
}
