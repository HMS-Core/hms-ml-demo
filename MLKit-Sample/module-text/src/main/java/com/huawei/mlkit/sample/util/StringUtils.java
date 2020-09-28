/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.huawei.mlkit.sample.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
    private static Map<String, String> letterNumberMap = new HashMap<>();
    private static Map<String, String> numberLetterMap = new HashMap<>();

    static {
        letterNumberMap.put("i", "1");
        letterNumberMap.put("I", "1");
        letterNumberMap.put("o", "0");
        letterNumberMap.put("O", "0");
        letterNumberMap.put("z", "2");
        letterNumberMap.put("Z", "2");

        numberLetterMap.put("1", "I");
        numberLetterMap.put("0", "O");
        numberLetterMap.put("2", "Z");
        numberLetterMap.put("8", "B");
    }

    /**
     * Filter strings based on regular expressions
     */

    public static String filterString(String origin, String filterStr) {
        if (origin == null || origin.isEmpty()) {
            return "";
        }
        if (filterStr == null || filterStr.isEmpty()) {
            return origin;
        }

        Pattern pattern = Pattern.compile(filterStr);
        Matcher matcher = pattern.matcher(origin);
        return matcher.replaceAll("").trim();
    }

    /**
     * Get date in specified date format.
     */
    public static String getCorrectDate(String origin, String splitter, int[] formatter) {
        if (origin == null || origin.isEmpty()) {
            return "";
        }
        if (splitter == null) {
            return "";
        }

        int targetLength = 0;
        for (int i : formatter) {
            targetLength += i;
        }

        // Convert letters to numbers first.
        String newStr = correctLetterToNumber(origin);

        // Filter strings, keeping only numbers and separators.
        newStr = filterString(newStr, "[^0-9,.-]");
        // If the length is less than the minimum length of the date 8, return the empty string directly.
        if (newStr.length() < targetLength) {
            return "";
        }

        int length = formatter.length;
        String[] strings = newStr.split(splitter);

        if (strings.length < 2 || strings.length > 3) {
            if (splitter.equals("\\.")) {
                strings = newStr.split(",");
                if (strings.length < 2 || strings.length > 3) {
                    return "";
                }
            } else {
                return "";
            }
        }

        // If both the length and the number of delimiters are satisfied, the result is returned directly.
        if (strings.length == length && newStr.length() == (targetLength + 2)) {
            return newStr;
        }

        // Fill in the separator.
        char target = splitter.toCharArray()[1];
        return fixMissingDelimiter(newStr, target, formatter);
    }

    /**
     * Completion of missing delimiters in the specified format.
     */
    public static String fixMissingDelimiter(String origin, char target, int[] format) {
        if (origin == null || origin.isEmpty()) {
            return "";
        }
        if (format == null ) {
            return "";
        }

        int newCharsLen = 0;
        for (int temp : format) {
            newCharsLen += temp;
        }

        String temp = filterString(origin, "[^0-9a-zA-Z]");
        if (temp.length() < newCharsLen) {
            return "";
        }

        char[] oldChars = origin.toCharArray();
        char[] newChars = new char[newCharsLen + (format.length - 1)];

        if (oldChars.length < newCharsLen) {
            return "";
        }

        int oldIndex = 0;
        int newIndex = 0;

        for (int i = 0; i < format.length; i++) {
            int tmp = format[i];

            while (tmp-- > 0) {
                newChars[newIndex++] = oldChars[oldIndex++];
            }
            if (i != format.length - 1) {
                if (blurMatchDelimiter(oldChars[oldIndex], target)) {
                    oldIndex++;
                    if (oldIndex >= oldChars.length) {
                        return "";
                    }
                }
                newChars[newIndex++] = target;
            }
        }
        return String.valueOf(newChars);
    }

    /**
     * Get a string of validity in the format xxxx.xx.xx-xxxx.xx.xx.
     */
    public static String getCorrectValidDate(String origin) {
        if (origin == null || origin.isEmpty()) {
            return "";
        }

        // Convert letters to numbers.
        String newStr = correctLetterToNumber(origin);
        newStr = newStr.replaceAll("\\s{1,}", " ");
        String[] strings = null;

        if (newStr.split("-").length == 2) {
            // Standard case, with '-' split between validity periods.
            newStr = filterString(newStr, "[^0-9,.-]");
            if (newStr.length() < 18) {
                return "";
            }
            strings = newStr.split("-");
        }

        if (newStr.split(" ").length == 2) {
            // Exception, missing separator between validity periods.
            strings = newStr.split(" ");
        }

        if (strings == null || strings.length != 2) {
            return "";
        }

        int[] formatter = {4, 2, 2};
        String startDate = getCorrectDate(strings[0], "\\.", formatter);
        String endDate = getCorrectDate(strings[1], "\\.", formatter);
        if (startDate.isEmpty() || endDate.isEmpty()) {
            return "";
        }
        return startDate + " - " + endDate;
    }

    /**
     * Get the ID number of Hong Kong, Macau, Taiwan Pass.
     */
    public static String getPassCardNumber(String origin) {
        if (origin == null || origin.isEmpty()) {
            return "";
        }

        String newStr = origin.trim();
        newStr = newStr.toUpperCase(Locale.ENGLISH);
        newStr = filterString(newStr, "[^0-9A-Z<]");

        if (newStr.length() < 27 || newStr.length() > 30) {
            return "";
        }

        String[] splits = newStr.split("[<]");
        if (splits.length == 4) {
            // Meet the requirements and return the results directly.
            return newStr;
        }

        int[] formatter =  {12, 7, 7, 1};
        return fixMissingDelimiter(newStr, '<', formatter);
    }

    /**
     * Get the ID number of Hong Kong Resident Permanent Identity Card.
     */
    public static String getHKIdCardNum(String origin) {
        if (origin == null || origin.isEmpty()) {
            return "";
        }

        origin = filterString(origin,"[^0-9a-zA-Z()]" );

        if (origin.length() < 10) {
            return "";
        }

        // The first letter must be a letter.
        String firstChar = origin.substring(0, 1);
        if (!Character.isLowerCase(firstChar.charAt(0)) && !Character.isUpperCase(firstChar.charAt(0))) {
            if (firstChar.equalsIgnoreCase("2")) {
                firstChar = "Z";
            } else {
                return "";
            }
        }

        // 6 digits behind.
        String number = origin.substring(1, 7);
        number = correctLetterToNumber(number);

        // Finally contains ().
        String firstField = origin.substring(7, 8);
        String middleField = origin.substring(8, 9);
        String lastField = origin.substring(9, 10);

        if (!firstField.equals("(") && !lastField.equals(")")) {
            return "";
        }
        if (!firstField.equals("(")) {
            if (firstField.equalsIgnoreCase("C") || firstField.equalsIgnoreCase("G")) {
                firstField = "(";
            } else {
                return "";
            }
        }

        String ret = firstChar + number + firstField + middleField + lastField;
        return ret.toUpperCase(Locale.ENGLISH);
    }

    /**
     * Get the ID number of the Homecoming Permit, Hong Kong, Macau and Taiwan have different rules.
     */
    public static String getHomeCardNumber(String origin) {
        if (origin == null || origin.isEmpty()) {
            return "";
        }

        // Remove blank space.
        origin = filterString(origin, "[\\s]");
        if (origin.length() != 8 && origin.length() != 9) {
            return "";
        }

        // Determine whether the first character is a letter.
        String firstLetter = origin.substring(0, 1);
        if (Character.isLetter(firstLetter.charAt(0))) {
            if (firstLetter.equalsIgnoreCase("H") || firstLetter.equalsIgnoreCase("M")) {
                if (origin.length() < 9) {
                    return "";
                }
                firstLetter = firstLetter.toUpperCase(Locale.ENGLISH);
                String number = origin.substring(1, 9);
                // Keep numbers only.
                number = filterString(number, "[^0-9]");
                number = correctLetterToNumber(number);
                if (number.length() != 8) {
                    return "";
                }
                return firstLetter + number;
            } else {
                return "";
            }
        }

        // Taiwan cell card has no initial, only 8 digits.
        if (origin.length() != 8) {
            return "";
        }

        String number = origin.substring(0, 8);
        number = filterString(number, "[^0-9]");
        number = correctLetterToNumber(number);
        if (number.length() != 8) {
            return "";
        }
        return number;
    }

    /**
     * Letters corrected to numbers.
     */
    public static String correctLetterToNumber(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        char[] chars = str.toCharArray();

        int length = chars.length;
        for (int index = 0; index < length; index++) {
            String tmp = letterNumberMap.get(Character.toString(chars[index]));
            if (tmp != null) {
                char[] tempChars = tmp.toCharArray();
                chars[index] = tempChars[0];
            }
        }

        return String.valueOf(chars);
    }

    /**
     * Numbers corrected to letters.
     */
    public static String correctNumberToLetter(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        char[] chars = str.toCharArray();

        int length = chars.length;
        for (int index = 0; index < length; index++) {
            String tmp = numberLetterMap.get(Character.toString(chars[index]));
            if (tmp != null) {
                char[] tempChars = tmp.toCharArray();
                chars[index] = tempChars[0];
            }
        }

        return String.valueOf(chars);
    }

    /**
     * Fuzzy match delimiter.
     */
    private static boolean blurMatchDelimiter(char origin, char target) {
        if (target == '.') {
            if (origin == '.' || origin == ',') {
                return true;
            }
        }
        if (target == '<') {
            if (origin == 'K' || origin == 'X') {
                return true;
            }
        }
        return origin == target;
    }
}