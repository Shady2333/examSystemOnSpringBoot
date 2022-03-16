package com.exam;

import com.exam.entity.FillQuestion;
import com.exam.entity.MultiQuestion;
import com.exam.entity.Question;
import com.exam.serviceimpl.FillQuestionServiceImpl;
import com.exam.serviceimpl.MultiQuestionServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ExamsystemApplicationTests {

    @Autowired
    MultiQuestionServiceImpl multiQuestionService;
    @Autowired
    FillQuestionServiceImpl fillQuestionService;
    @Test
    public void contextLoads() {
        List<MultiQuestion> bySectionAndSubject = multiQuestionService.findBySectionAndSubject("0", "1");
        for (int i = 0; i < bySectionAndSubject.size(); i++) {
            Question question = bySectionAndSubject.get(i);
        }

    }
    @Test
    public void test2(){
        LinkedList<List<? extends Object>> objects = new LinkedList<>();
        for (int i = 0; i <10; i++) {
            LinkedList<Object> objects1 = new LinkedList<>();
            objects1.add(1);
            objects1.add(2);
            objects.add(objects1);
        }
        System.out.println(objects);
    }


}

