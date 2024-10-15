package com.master.flow.service;

import com.master.flow.model.dao.UserReportDAO;
import com.master.flow.model.vo.UserReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class UserReportService {
    @Autowired
    private UserReportDAO userReportDAO;

    // 신고당한 유저 전부보기
    public List<UserReport> showAllUserReport() {return userReportDAO.findAll();}

    // 신고당한 유저 글 삭제하고 밴
    public void delReportUser(int userReportCode) {
        UserReport userReport = userReportDAO.findById(userReportCode).get();
        int userCode = userReport.getUser().getUserCode();

        userReportDAO.deleteUserReportByUserCode(userCode);
    }

    //    유저 밴하기
    public void banUser(int userCode) {
        String userBanStatus = "Y";
        userReportDAO.banUser(userCode, userBanStatus);
    }

}
