package com.master.flow.follow.util;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KoreanStringUtil {

    // 초성 모음
    public String[] INITIALS = {
            "ㄱ", "ㄲ", "ㄴ", "ㄷ", "ㄸ", "ㄹ", "ㅁ", "ㅂ", "ㅃ", "ㅅ", "ㅆ",
            "ㅇ", "ㅈ", "ㅉ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"
    };
    // 종성 모음
    public String[] FINAL_CONSONANTS = {
            "",   "ㄱ", "ㄲ", "ㄳ", "ㄴ", "ㄵ", "ㄶ", "ㄷ", "ㄹ", "ㄺ", "ㄻ", "ㄼ", "ㄽ",
            "ㄾ", "ㄿ", "ㅀ", "ㅁ", "ㅂ", "ㅄ", "ㅅ", "ㅆ", "ㅇ", "ㅈ", "ㅊ", "ㅋ", "ㅌ", "ㅍ", "ㅎ"
    };
    // 복합 조성을 키로 갖고 그 조성을 나눈 문자열을 밸류로 갖는 맵
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
    //사용자가 입력한 문자열이 어떠한 문자열인지 판독 후 정수형 데이터 발사
    public int isKoreanConsonant(String key) {
        if (key == null || key.trim().isEmpty()) {
            return 0;
        } // 공백 or 빈문자열은 0
        for(int i=0; i<key.length(); i++) {
            int unicode = key.charAt(key.length()-1) - 0xAC00;
            int finalIndex = unicode % 28;
            if (key.length() > 1) {
                if(key.charAt(i)< 0x3131) return 0; // 한글이 아닐 시에 바로 0
                // 길이가 2 이상인 문자열의 마지막 글자가 초성이라면
                if(key.charAt(key.length()-1) >= 0x3131 && key.charAt(key.length()-1) <= 0x314E) return 3;
                // 각 문자열들의 종성이 존재하지 않는다면
                else if(finalIndex == 0) return 3;
                // 죄다 한글에 종성까지 있다면
                else return 5;
            }
        }
        //단일 문자에 초성이다
        if(key.length() == 1 && (key.charAt(0) >= 0x3131 && key.charAt(0) <= 0x314E)) {
            return 1;
        }
        //단일 문자일 때 초성+중성이냐 초성+중성+종성이냐
        if (key.length() == 1 && key.charAt(0) >= 0xAC00 && key.charAt(0) <= 0xD7A3) {
            int unicode = key.charAt(0) - 0xAC00; // 해당 문자의 유니코드 추출
            int finalIndex = unicode % 28; // 종성 인덱스 구하기(종성이 존재한다면 0이 나올 수가 없음)
            if (finalIndex == 0) {
                return 2;
            } else return 4;
        }
        return 0;
    }

    public String sliceKorean(String key) {
        if(!key.matches(".*\\s.*")) {
            // 현재 입력값이 종성이 있는 단일 문자인 경우일 때
            if (isKoreanConsonant(key) == 4) { // isKoreanConsonant의 값으로 판독
                int unicode = key.charAt(0) - 0xAC00;
                int initialIndex = unicode / (21 * 28); // 초성 인덱스 추출
                int medialIndex = (unicode % (21 * 28)) / 28; // 중성 인덱스 추출
                int finalIndex = unicode % 28; // 종성 인덱스 추출

                StringBuilder sliceTextBuilder = new StringBuilder();
                // 현재 문자에서 초성+중성만 있는 문자를 새로 만듦
                String firstChar = String.valueOf((char) (0xAC00 + (initialIndex * 21 * 28) + (medialIndex * 28)));
                // 종성 배열에서 위에 구한 인덱스 값으로 종성만 추출
                String secondChar = FINAL_CONSONANTS[finalIndex];
                // 복합종성일 시
                if (COMPLEX_CONSONANTS.containsKey(secondChar)) {
                    secondChar = COMPLEX_CONSONANTS.get(secondChar); // 종성을 두 문자열로 나눈다
                    String secondCharBefore = secondChar.substring(0, 1); // 그 중 첫번째 문자열을 추출
                    int secondCharIndex = 0;
                    for (int i = 0; i < FINAL_CONSONANTS.length; i++) {
                        if (secondCharBefore.equals(FINAL_CONSONANTS[i])) {
                            secondCharIndex = i;
                            break;
                        }
                    } // 사용자가 입력한 단일 문자의 종성과 substring한 값과 일치할 때의 i값을 추출하여 secondCharIndex에 넣는다
                    firstChar = String.valueOf((char) (firstChar.charAt(0) + secondCharIndex)); // 첫번째 글자는 그대로
                    secondChar = secondChar.substring(1, 2); // 복합종성을 나눈 값의 두번째 문자를 다시 합친다
                    // 만약 입력값이 '좂'이라면 "좁ㅅ"이라는 문자열로 바뀌는 것
                }
                // if 조건에 해당되지 않는 단일 종성이라면 그대로 도달한다
                // 예를 들어 입력값이 '종' 이라면 '조ㅇ'이 된다.
                sliceTextBuilder.append(firstChar);
                sliceTextBuilder.append(secondChar);

                String sliceText = sliceTextBuilder.toString();
                return sliceText;
            } else if (isKoreanConsonant(key) == 5) {
                // 단일문자가 아니고 마지막 문자가 초성도 아닐 경우...
                StringBuilder sliceTextBuilder = new StringBuilder();
                for (char ch : key.toCharArray()) {
                    if(!(ch < 0xAC00)) {
                        int unicode = ch - 0xAC00;
                        int initialIndex = unicode / (21 * 28); // 초성 인덱스 추출
                        int medialIndex = (unicode % (21 * 28)) / 28; // 중성 인덱스 추출
                        int finalIndex = unicode % 28;

                        if (finalIndex == 0) {
                            sliceTextBuilder.append(ch); // 문자가 초성+중성 조합일 경우 그대로
                        } else {
                            String firstChar = String.valueOf((char) (0xAC00 + (initialIndex * 21 * 28) + (medialIndex * 28)));
                            String secondChar = FINAL_CONSONANTS[finalIndex];
                            sliceTextBuilder.append(firstChar);
                            sliceTextBuilder.append(secondChar);
                        } // 아니라면 초성+중성과 종성을 별도로 합친 문자열이 탄생한다
                        //예를 들어 사용자가 입력한 값이 '테슽'이라고 한다면 '테스ㅌ'이 된다
                    }
                }
                String sliceText = sliceTextBuilder.toString();
                return sliceText;
            }
        }
        return key; // 어떤 경우에도 도달하지 않는다면 기존 입력값 그대로 리턴한다
    }
}
