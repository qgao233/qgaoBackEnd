package com.qgao.springcloud.utils.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    public static String COOKIE_SEPARATOR = "#";

    private static Pattern linePattern = Pattern.compile("_(\\w)");
    private static Pattern humpPattern = Pattern.compile("[A-Z]");

    private static final String chr32 = "0123456789ABCDEFGHIJKLMNOPQRSTUV";

    /** 下划线转驼峰 */
    public static String lineToHump(String str) {
        str = str.toLowerCase();
        Matcher matcher = linePattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /** 驼峰转下划线 */
    public static String humpToLine(String str) {
        Matcher matcher = humpPattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    public static String hex10to32(Long number) {
        String result = "";
        if (number <= 0) {
            return "0";
        } else {
            while (number != 0) {
                int remained = (int)(number & 31);
                result = chr32.substring(remained, remained+1) + result;
                number = number >> 5;
            }
            return result;
        }
    }

    public static String hex32To10(String source){
        char[] chars = source.toCharArray();
        int length = chars.length - 1;
        long number = 0;
        for(int i = length;i>=0;i--){
            number += chr32.indexOf(chars[length - i]) * Math.pow(32,i);
        }
        return String.valueOf(number);
    }

    public static boolean isMailFormat(String content){
        String regex = "^[a-z0-9A-Z]+[- | a-z0-9A-Z . _]+@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-z]{2,}$";
        return isRegexMatch(regex,content);
    }

    public static boolean isOnlyNumber(String content){
        String regex = "^[0-9]*$";
        return isRegexMatch(regex,content);
    }

    public static boolean isRegexMatch(String regex,String content){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);
        if(matcher.find()){
            return true;
        }
        return false;
    }

    public static String escapeExprSpecialWord(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            String[] fbsArr = { "\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|" };
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    public static void main(String[] args) {
        System.out.println(lineToHump("article_good"));
    }
}
