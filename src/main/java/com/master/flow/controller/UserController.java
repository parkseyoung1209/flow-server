package com.master.flow.controller;

import com.master.flow.config.TokenProvider;
import com.master.flow.model.dto.NaverDTO;
import com.master.flow.model.vo.User;
import com.master.flow.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = {"*"}, maxAge=6000)
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;

    // 테스트용 코드
    @GetMapping("/showAllUser")
    public ResponseEntity showAllUser(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.showAllUser());
    }

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity registerUser(@RequestBody User vo){
        userService.registerUser(vo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 회원가입시 중복체크
    @GetMapping("/duplicateCheck")
    public ResponseEntity duplicateCheck(@RequestParam(name="userEmail") String userEmail, @RequestParam(name="userPlatform") String userPlatform){
        boolean check = userService.duplicateCheck(userEmail, userPlatform);
        return ResponseEntity.status(HttpStatus.OK).body(check);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody User vo){
        User user = userService.login(vo.getUserEmail(), vo.getUserPlatform());
        String token = tokenProvider.create(user);
        log.info("유저정보:"+user);
        int banCount = user.getUserBanCount();

//        밴 상태일때 로그인 금지 (4회 이상은 그냥 금지)
        if(user.getUserBanStatus().equals("Y")) {
            if(banCount == 1) {
                Duration duration = Duration.between(LocalDateTime.now(), user.getUserBanDate().plusDays(7));
                if(duration.isPositive()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(token);
                }
            } else if(banCount == 2) {
                Duration duration = Duration.between(LocalDateTime.now(), user.getUserBanDate().plusDays(15));
                if(duration.isPositive()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(token);
                }
            } else if(banCount == 3) {
                Duration duration = Duration.between(LocalDateTime.now(), user.getUserBanDate().plusDays(30));
                if(duration.isPositive()) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(token);
                }
            } else if(banCount >= 4) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(token);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(token);
    }

//    유저 신고하기
    @PutMapping("/banUser")
    public ResponseEntity banUser(@RequestParam(name="userCode") int userCode) {
        userService.banUser(userCode);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 네이버 인증코드로 api 접속해서 네이버 토큰 발급
    @GetMapping("/oauth/naver/getToken")
    public ResponseEntity getNaverToken(@RequestParam(name = "clientId") String clientId, @RequestParam(name = "clientSecret") String clientSecret,
                                        @RequestParam(name = "code") String code, @RequestParam(name = "state") String state){
        // uri 주소 + 파라미터 묶기
        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString("https://nid.naver.com/oauth2.0/token")
                .queryParam("grant_type", "authorization_code")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
                .queryParam("code", code)
                .queryParam("state", state)
                .build();

        try {
            // GET 방식으로 uri 보내기
            URL url = new URL(uriComponents.toString());
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode == 200){
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }else{
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null){
                response.append(inputLine);
            }

            br.close();

            // response로 토큰 받아오기 toString으로 변환
            return ResponseEntity.status(HttpStatus.OK).body(response.toString());
        } catch (Exception e) {}

        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 발급 받은 naver 토큰으로 유저 데이터 추출
    @GetMapping("/oauth/naver/getUserData")
    public ResponseEntity getNaverUserData(@RequestParam(name = "token") String token){

        try {
            URL url = new URL("https://openapi.naver.com/v1/nid/me");
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer " + token);

            int responseCode = con.getResponseCode();
            BufferedReader br;

            if(responseCode == 200){
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            }else{
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }

            String inputLine;

            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null){
                response.append(inputLine);
            }

            br.close();
            return ResponseEntity.status(HttpStatus.OK).body(response.toString());
        }catch (Exception e){}
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
