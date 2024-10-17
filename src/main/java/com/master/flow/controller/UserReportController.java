package com.master.flow.controller;

import com.master.flow.model.vo.UserReport;
import com.master.flow.service.UserReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/*")
@CrossOrigin(origins = {"*"},maxAge = 6000)
public class UserReportController {
    @Autowired
    private UserReportService userReportService;

    @GetMapping("/showAllUserReport")
    public ResponseEntity showAllUserReport() {
        return ResponseEntity.status(HttpStatus.OK).body(userReportService.showAllUserReport());
    }

    @DeleteMapping("/delUserReport")
    public ResponseEntity delReportUser(@RequestParam(name="userReportCode") int userReportCode) {
        userReportService.delReportUser(userReportCode);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    //    유저 밴하기
    @PutMapping("/banUser")
    public ResponseEntity banUser(@RequestParam(name="userCode") int userCode) {
        userReportService.banUser(userCode);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // 유저 신고하기
    @PostMapping("/reportUser")
    public ResponseEntity reportUser(@RequestBody UserReport userReport) {
        userReportService.reportUser(userReport);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
