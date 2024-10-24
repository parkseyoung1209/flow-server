package com.master.flow.service;

import com.master.flow.model.dao.TagDAO;
import com.master.flow.model.vo.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TagService {

    @Autowired
    private TagDAO tagDao;

    // 태그 코드로 태그정보 추출
    public List<Tag> findTagByTagCode(List<Integer> tagCodes){
        List<Tag> result = new ArrayList<>();
        for(int tagCode : tagCodes){
            result.add(tagDao.findById(tagCode).get());
        }
        return result;
    }
}
