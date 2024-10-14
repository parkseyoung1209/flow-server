package com.master.flow.controller;

import com.master.flow.service.UserReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
