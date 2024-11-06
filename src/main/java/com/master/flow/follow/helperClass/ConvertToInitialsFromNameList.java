package com.master.flow.follow.helperClass;

import com.master.flow.follow.util.KoreanStringUtil;
import com.master.flow.model.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ConvertToInitialsFromNameList {

    @Autowired
    private KoreanStringUtil krUtil;

    public List<String> nickNameList (List<User> users) {
        return users.stream()
                .map(User::getUserNickname)
                .toList();
    }

    public List<String> convertToInitialsFromName(List<User> users, int num, String key) {
        List<String> nickNameList = nickNameList(users);
        Set<String> nickNameSet = new HashSet<>(nickNameList);
        List<String> userNickNameList = new ArrayList<>();
        for(User user : users) {
            StringBuilder initials = new StringBuilder();
            int keyIndex = 0;
            for (char ch : user.getUserNickname().toCharArray()) {
                int unicode = ch - 0xAC00;
                int initialIndex = unicode / (21 * 28);
                int medialIndex = (unicode % (21 * 28)) / 28; // 중성 인덱스 추출
                int finalIndex = unicode % 28;

                char combinedChar = (char) (0xAC00 + (initialIndex * 21 * 28) + (medialIndex * 28));
                if (ch >= 0xAC00 && ch <= 0xD7A3 && num == 1) { // 한글이냐 여부
                    initials.append(krUtil.INITIALS[initialIndex]);
                } else if(ch >= 0xAC00 && ch <= 0xD7A3 && num == 2) {
                    initials.append(combinedChar);
                } else if(ch >= 0xAC00 && ch <= 0xD7A3 && num == 3) {
                    String initialChar = krUtil.INITIALS[initialIndex];
                    if (key != null && !key.trim().isEmpty()) {
                        if (keyIndex < key.length()) {
                            char keyCh = key.charAt(keyIndex);
                            int keyUnicode = keyCh - 0xAC00;
                            int keyInitialIndex = keyUnicode / (21*28);
                            int keyMedialIndex = (keyUnicode % (21*28)) /28;
                            int keyFinalIndex = keyUnicode % 28;

                            // 키가 온전한 한글 음절인 경우
                            if (keyCh >= 0xAC00 && keyCh <= 0xD7A3 && (nickNameSet.stream().anyMatch(s -> s.contains(String.valueOf(keyCh)))) && (keyCh == ch)) {
                                initials.append(ch);
                                keyIndex++;
                            }
                            // 키가 초성인 경우, 유저 닉네임의 초성과 비교
                            else if ((keyCh >= 0x3131 && keyCh <= 0x314E) && initialChar.charAt(0) == keyCh) {
                                initials.append(initialChar);
                                keyIndex++;
                            }
                            // 키와 매칭되지 않는 경우, 유저의 초성만 추가 // 초중성이 같다면 초중성만 남기기
                            else if((0xAC00 + (keyInitialIndex *21 *28) + (keyMedialIndex *28)) == (0xAC00 + (initialIndex * 21 * 28) + (medialIndex * 28))){
                                if(keyFinalIndex == 0 || finalIndex == 0) initials.append(combinedChar);
                                else if(krUtil.FINAL_CONSONANTS[finalIndex].equals(krUtil.FINAL_CONSONANTS[keyFinalIndex])) initials.append(ch);
                                else if(krUtil.sliceKorean(String.valueOf(keyCh)).charAt(0) == ch) {
                                    if(key.length() > 1) {
                                        if(krUtil.sliceKorean(String.valueOf(key.charAt(key.length()-1))).charAt(0) == ch) initials.append(keyCh);
                                        else initials.append(ch);
                                    }
                                    else initials.append(keyCh);
                                }
                                else if(krUtil.sliceKorean(String.valueOf(ch)).charAt(0) == keyCh) {
                                    if(key.length() > 1) {
                                        if(krUtil.sliceKorean(String.valueOf(ch)).charAt(0) == key.charAt(key.length()-1)) initials.append(keyCh);
                                        else initials.append(ch);
                                    }
                                    else initials.append(keyCh);
                                }
                                else initials.append(initialChar);
                                keyIndex++;
                            }
                        } else {
                            if(nickNameSet.stream().anyMatch(s -> s.contains(key))) initials.append(ch);
                            else initials.append(initialChar);
                        }
                    } else {
                        initials.append(ch);  // 한글이 아닌 경우 그대로 추가
                    }
                }
            }
            userNickNameList.add(initials.toString());
        }
        return userNickNameList; // 한글 닉네임의 초성 문자열이 나옴 홍길동-> ㅎㄱㄷ || 홍ㄱㄷ
    }
}
