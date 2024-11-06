package com.master.flow.follow.util;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KoreanStringUtil {

    public String[] INITIALS = {
            "ㄱ", "ㄲ", "ㄴ", "ㄷ", "ㄸ", "ㄹ", "ㅁ", "ㅂ", "ㅃ", "ㅅ", "ㅆ",
            "ㅇ", "ㅈ", "ㅉ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"
    };
    public String[] FINAL_CONSONANTS = {
            "",   "ㄱ", "ㄲ", "ㄳ", "ㄴ", "ㄵ", "ㄶ", "ㄷ", "ㄹ", "ㄺ", "ㄻ", "ㄼ", "ㄽ",
            "ㄾ", "ㄿ", "ㅀ", "ㅁ", "ㅂ", "ㅄ", "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"
    };

    public Map<String, String> COMPLEX_CONSONANTS = Map.ofEntries(
            Map.entry(FINAL_CONSONANTS[3], "ㄱㅅ"),
            Map.entry(FINAL_CONSONANTS[5], "ㄴㅈ"),
            Map.entry(FINAL_CONSONANTS[6], "ㄴㅎ"),
            Map.entry(FINAL_CONSONANTS[9], "ㄹㄱ"),
            Map.entry(FINAL_CONSONANTS[10], "ㄹㅁ"),
            Map.entry(FINAL_CONSONANTS[11], "ㄹㅂ"),
            Map.entry(FINAL_CONSONANTS[12], "ㄹㅅ"),
            Map.entry(FINAL_CONSONANTS[13], "ㄹㅌ"),
            Map.entry(FINAL_CONSONANTS[14], "ㄹㅍ"),
            Map.entry(FINAL_CONSONANTS[15], "ㄹㅎ"),
            Map.entry(FINAL_CONSONANTS[18], "ㅂㅅ")
    );

    public int isKoreanConsonant(String key) {
        if (key == null || key.trim().isEmpty()) {
            return 0;
        }
        for(int i=0; i<key.length(); i++) {
            int unicode = key.charAt(key.length()-1) - 0xAC00;
            int finalIndex = unicode % 28;
            if (key.length() > 1) {
                if(key.charAt(i)< 0x3131) return 0;

                if(key.charAt(key.length()-1) >= 0x3131 && key.charAt(key.length()-1) <= 0x314E) return 3;
                else if(finalIndex == 0) return 3;
                else return 5;
            }
        }
        if(key.length() == 1 && (key.charAt(0) >= 0x3131 && key.charAt(0) <= 0x314E)) {
            return 1;
        }
        if (key.length() == 1 && key.charAt(0) >= 0xAC00 && key.charAt(0) <= 0xD7A3) {
            int unicode = key.charAt(0) - 0xAC00;
            int finalIndex = unicode % 28;
            if (finalIndex == 0) {
                return 2;
            } else return 4;
        }
        return 0;
    }

    public String sliceKorean(String key) {
        if(!key.matches(".*\\s.*")) {
            if (isKoreanConsonant(key) == 4) {
                int unicode = key.charAt(0) - 0xAC00;
                int initialIndex = unicode / (21 * 28); // 초성 인덱스 추출
                int medialIndex = (unicode % (21 * 28)) / 28; // 중성 인덱스 추출
                int finalIndex = unicode % 28;

                StringBuilder sliceTextBuilder = new StringBuilder();
                String firstChar = String.valueOf((char) (0xAC00 + (initialIndex * 21 * 28) + (medialIndex * 28)));
                String secondChar = FINAL_CONSONANTS[finalIndex];
                if (COMPLEX_CONSONANTS.containsKey(secondChar)) {
                    secondChar = COMPLEX_CONSONANTS.get(secondChar);
                    // secondChar 을 잘라서 배열의 무언가와 비교를 하고 그 배열에 맞는 인덱스를 저기 더해줘야함
                    String secondCharBefore = secondChar.substring(0, 1);
                    int secondCharIndex = 0;
                    for (int i = 0; i < FINAL_CONSONANTS.length; i++) {
                        if (secondCharBefore.equals(FINAL_CONSONANTS[i])) {
                            secondCharIndex = i;
                            break;
                        }
                    }
                    firstChar = String.valueOf((char) (firstChar.charAt(0) + secondCharIndex));
                    secondChar = secondChar.substring(1, 2);
                }
                sliceTextBuilder.append(firstChar);
                sliceTextBuilder.append(secondChar);

                String sliceText = sliceTextBuilder.toString();
                return sliceText;
            } else if (isKoreanConsonant(key) == 5) {
                StringBuilder sliceTextBuilder = new StringBuilder();
                for (char ch : key.toCharArray()) {
                    if(!(ch < 0xAC00)) {
                        int unicode = ch - 0xAC00;
                        int initialIndex = unicode / (21 * 28); // 초성 인덱스 추출
                        int medialIndex = (unicode % (21 * 28)) / 28; // 중성 인덱스 추출
                        int finalIndex = unicode % 28;

                        if (finalIndex == 0) {
                            sliceTextBuilder.append(ch);
                        } else {
                            String firstChar = String.valueOf((char) (0xAC00 + (initialIndex * 21 * 28) + (medialIndex * 28)));
                            String secondChar = FINAL_CONSONANTS[finalIndex];
                            sliceTextBuilder.append(firstChar);
                            sliceTextBuilder.append(secondChar);
                        }
                    }
                }
                String sliceText = sliceTextBuilder.toString();
                return sliceText;
            }
        }
        return key;
    }
}
