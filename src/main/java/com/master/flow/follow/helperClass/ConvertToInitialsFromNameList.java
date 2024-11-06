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

    // 객체의 유저닉네임을 뽑아 스트링 리스트로 리턴
    public List<String> nickNameList (List<User> users) {
        return users.stream()
                .map(User::getUserNickname)
                .toList();
    }

    // 입력받은 문자/문자열에 따라 스트링 값을 변경시켜 리스트 재구성
    public List<String> convertToInitialsFromName(List<User> users, int num, String key) {
        List<String> nickNameList = nickNameList(users);
        Set<String> nickNameSet = new HashSet<>(nickNameList); // 순회없는 비교를 위한 set 자료구조 변경
        List<String> userNickNameList = new ArrayList<>();
        for(User user : users) {
            StringBuilder initials = new StringBuilder();
            int keyIndex = 0; // 세번째 else if문에 사용할 인덱스 변수
            for (char ch : user.getUserNickname().toCharArray()) {
                int unicode = ch - 0xAC00;
                int initialIndex = unicode / (21 * 28);
                int medialIndex = (unicode % (21 * 28)) / 28;
                int finalIndex = unicode % 28;

                char combinedChar = (char) (0xAC00 + (initialIndex * 21 * 28) + (medialIndex * 28));
                if (ch >= 0xAC00 && ch <= 0xD7A3 && num == 1) { // 한글이고 문자가 초성이냐
                    // koreanStringUtil에 있는 초성 배열에서 해당 문자의 초성 인덱스에 맞는 문자 가져오기
                    initials.append(krUtil.INITIALS[initialIndex]);
                } else if(ch >= 0xAC00 && ch <= 0xD7A3 && num == 2) {
                    // 단일 문자에 초성+중성 조합이라면
                    initials.append(combinedChar);
                } else if(ch >= 0xAC00 && ch <= 0xD7A3 && num == 3) {
                    // 종성이 있거나 단일 문자가 아니라면...
                    String initialChar = krUtil.INITIALS[initialIndex];
                    if (key != null && !key.trim().isEmpty()) {
                        if (keyIndex < key.length()) {
                            char keyCh = key.charAt(keyIndex);
                            int keyUnicode = keyCh - 0xAC00;
                            int keyInitialIndex = keyUnicode / (21*28);
                            int keyMedialIndex = (keyUnicode % (21*28)) /28;
                            int keyFinalIndex = keyUnicode % 28; // 입력값들의 문자를 유니코드 추출 후 초성 중성 종성 인덱스 추출

                            // 이후 나올 모든 조건문에는 keyIndex를 1씩 더해서 다음 문자로 넘긴다
                            // 글자와 인덱스까지 일치한다면
                            if (keyCh >= 0xAC00 && keyCh <= 0xD7A3 && (nickNameSet.stream().anyMatch(s -> s.contains(String.valueOf(keyCh)))) && (keyCh == ch)) {
                                initials.append(ch);
                                keyIndex++;
                            }
                            // 키가 초성인 경우, 유저 닉네임의 초성과 비교
                            else if ((keyCh >= 0x3131 && keyCh <= 0x314E) && initialChar.charAt(0) == keyCh) {
                                initials.append(initialChar);
                                keyIndex++;
                            }
                            // 키와 매칭되지 않는 경우, 유저의 초성만 추가 // 초중성이 같다면 이후 로직으로 ㄱㄱ
                            else if((0xAC00 + (keyInitialIndex *21 *28) + (keyMedialIndex *28)) == (0xAC00 + (initialIndex * 21 * 28) + (medialIndex * 28))){
                                if(keyFinalIndex == 0 || finalIndex == 0) initials.append(combinedChar); // 입력값의 문자나 실제 닉네임의 현 인덱스의 문자가 종성없는 문자라면
                                    // 종성까지 완전 일치시 유저닉네임의 해당 인덱스의 문자를 그대로 쓴다
                                else if(krUtil.FINAL_CONSONANTS[finalIndex].equals(krUtil.FINAL_CONSONANTS[keyFinalIndex])) initials.append(ch);
                                    // 예를들어 사용자가 '좁설테'라는 닉네임을 치려고 '좂섩' 까지 쳤다고 한다면 이는 두번째 문자의 단일 종성까지 일치하는 경우이다
                                    // 해당 메서드에선 입력값의 문자가 복합종성이고 실제 닉네임의 해당 문자는 단일 종성일때를 처리한다
                                else if(krUtil.sliceKorean(String.valueOf(keyCh)).charAt(0) == ch) {
                                    if(key.length() > 1) {
                                        // 단일 문자가 아니고 입력값의 마지막 문자를 sliceKorean으로 자른 문자열의 첫번째 문자와 값이 같다면
                                        if(krUtil.sliceKorean(String.valueOf(key.charAt(key.length()-1))).charAt(0) == ch) initials.append(keyCh); // 입력값의 해당 문자를 닉네임의 문자에 그대로 넣는다
                                        else initials.append(ch); // 이건 사용자가 오타를 친 경우밖에 없으므로 원래 닉네임 문자를 그대로 넣는다...
                                    }
                                    else initials.append(keyCh); //단일 문자일 경우에는 닉네임의 해당 문자를 입력값의 해당 문자로 교체한다
                                }
                                // 여긴 반대로 실제 닉네임의 해당 문자가 복합 종성이고 입력값의 해당 인덱스에 있는 문자가 단일종성일때 처리한다
                                else if(krUtil.sliceKorean(String.valueOf(ch)).charAt(0) == keyCh) {
                                    if(key.length() > 1) {
                                        // 여기선 입력값의 마지막 문자와 슬라이스한 실제 닉네임의 문자를 비교하고 일치한다면 입력값의 해당 문자를 넣는다
                                        if(krUtil.sliceKorean(String.valueOf(ch)).charAt(0) == key.charAt(key.length()-1)) initials.append(keyCh);
                                        else initials.append(ch);
                                    }
                                    else initials.append(keyCh); // 단일 문자라면 무조건 입력값의 문자로 교체
                                }
                                else initials.append(initialChar); // 그 어떤 경우도 아니라면 초성만 넣는다
                                keyIndex++;
                            }
                        } else {
                            if(nickNameSet.stream().anyMatch(s -> s.contains(key))) initials.append(ch);
                            // 만약 입력값이 '성' 이고 찾으려는 닉네임이 '종성테스트'라면 인덱스가 맞지 않지만 글자 자체는 contain이므로 그대로 해당 문자를 넣어야한다
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
