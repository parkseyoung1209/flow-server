package com.master.flow.service;

import com.master.flow.config.TokenProvider;
import com.master.flow.model.dao.PostDAO;
import com.master.flow.model.dao.PostImgDAO;
import com.master.flow.model.dao.UserDAO;
import com.master.flow.model.dto.UserUpdateDTO;
import com.master.flow.model.vo.Post;
import com.master.flow.model.vo.PostImg;
import com.master.flow.model.vo.QUser;
import com.master.flow.model.vo.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserDAO userDao;
    @Autowired
    private PostDAO postDAO;
    @Autowired
    private PostImgDAO postImgDAO;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private JPAQueryFactory queryFactory;

    private final QUser qUser = QUser.user;
    private final String userImgDir = "\\\\192.168.10.51\\flow\\userImg\\";
    private final String postImgpath = "\\\\192.168.10.51\\flow\\postImg\\";

    // 사용자 정보 가져오는 메서드
    private User getUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth != null && auth.isAuthenticated()){
            return (User) auth.getPrincipal();
        }

        return null;
    }
    
    // 회원가입
    public void registerUser(User vo){
        vo.setUserBanStatus("N");
        vo.setUserManagerCode("N");
        userDao.save(vo);
    }

    // 회원가입시 중복체크
    public boolean duplicateCheck(String userEmail, String userPlatform){
        User user = userDao.duplicateCheck(userEmail, userPlatform).orElse(null);

        if(user == null) return true;
        return false;
    }

    // 로그인
    public Map<String, String> login(User vo){
        User user = userDao.duplicateCheck(vo.getUserEmail(), vo.getUserPlatform()).get();

        Map<String, String> response = new HashMap<>();
        String token = tokenProvider.create(user);
        response.put("token", token);

        int banCount = user.getUserBanCount();

//        밴 상태일때 로그인 금지 (4회 이상은 그냥 금지)
        if(user.getUserBanStatus().equals("Y")) {
            if(banCount == 1) {
                Duration duration = Duration.between(LocalDateTime.now(), user.getUserBanDate().plusDays(7));
                if(duration.isPositive()) response.put("ban", "Y");
                else if(duration.isNegative()) response.put("ban", "N");
            } else if(banCount == 2) {
                Duration duration = Duration.between(LocalDateTime.now(), user.getUserBanDate().plusDays(15));
                if(duration.isPositive()) response.put("ban", "Y");
                else if(duration.isNegative()) response.put("ban", "N");
            } else if(banCount == 3) {
                Duration duration = Duration.between(LocalDateTime.now(), user.getUserBanDate().plusMinutes(1));
                if(duration.isPositive()) response.put("ban", "Y");
                else if(duration.isNegative()) response.put("ban", "N");
            } else if(banCount >= 4) response.put("ban", "Y");
        } else if(user.getUserBanStatus().equals("N")) response.put("ban", "N");

        if(response.get("ban").equals("N")){
            user.setUserBanStatus("N");
            user.setUserBanDate(null);
            userDao.save(user);
        }

        return response;
    }

    // user 정보 가져오기
    public User findUser(int code){
        return userDao.findById(code).get();
    }
    public User findUser(){
        return userDao.findById(getUser().getUserCode()).get();
    }

    // 유저 탈퇴
    public void deleteUser(){
        User user = userDao.findById(getUser().getUserCode()).get();
        // 유저가 생성한 게시글 조회
        List<Post> post = postDAO.findByUser_UserCode(user.getUserCode());

        // 이미지 서버에 존재하는 유저의 게시물 이미지들 삭제
        for(Post p : post){
            // 게시물 마다의 이미지 객체 받아옴
            List<PostImg> imgs = postImgDAO.findByPost_PostCode(p.getPostCode());
            for(PostImg postImg : imgs){
                String url = postImg.getPostImgUrl();
                String fileNmae = url.substring(url.lastIndexOf("\\") + 1);
                File file = new File(postImgpath + fileNmae);
                file.delete();
            }
        }

        if(!user.getUserProfileUrl().equals("http://192.168.10.51:8081/userImg/defaultUser.png")){
            String url = user.getUserProfileUrl();
            String fileName = url.substring(url.lastIndexOf("/") + 1);
            File file = new File(userImgDir + fileName);
            file.delete();
        }

        userDao.deleteById(getUser().getUserCode());
    }

    // 유저 정보 수정
    public void updateUser(UserUpdateDTO dto) throws IOException {

        User user = userDao.findById(dto.getUserCode()).get();

        // 이미지 변경을 했으면 실행
        if(dto.getImgFile() != null){
            if(!user.getUserProfileUrl().equals("http://192.168.10.51:8081/userImg/defaultUser.png")){
                File deleteFile =  new File(userImgDir + new File(user.getUserProfileUrl()).getName());
                deleteFile.delete();
            }
            // 파일이름 랜덤으로 생성
            UUID uuid = UUID.randomUUID();
            String fileName = uuid.toString() + "_" + dto.getImgFile().getOriginalFilename();
            File copyFile = new File(userImgDir + fileName);
            // 이미지 서버에 이미지 저장
            dto.getImgFile().transferTo(copyFile);

            user.setUserProfileUrl("http://192.168.10.51:8081/userImg/" + fileName);
        }else{
            // 프로필 이미지가 기본이미지로 바뀜
            if(!dto.getUserProfileUrl().equals(user.getUserProfileUrl()) && !user.getUserProfileUrl().equals("http://192.168.10.51:8081/userImg/defaultUser.png")){
                File deleteFile =  new File(userImgDir + new File(user.getUserProfileUrl()).getName());
                deleteFile.delete();

                user.setUserProfileUrl("http://192.168.10.51:8081/userImg/defaultUser.png");
            }
        }
        user.setUserNickname(dto.getUserNickname());
        user.setUserJob(dto.getUserJob());
        user.setUserHeight(dto.getUserHeight());
        user.setUserWeight(dto.getUserWeight());
        user.setUserBodySpecYn(dto.getUserBodySpecYn());

        userDao.save(user);
    }

    // 유저 닉네임 중복 체크
    public boolean nicknameCheck(String userNickname){
        List<User> user = queryFactory
                .selectFrom(qUser)
                .where(qUser.userNickname.eq(userNickname))
                .fetch();

        if(user.size() == 0) return true;
        return false;
    }
}
