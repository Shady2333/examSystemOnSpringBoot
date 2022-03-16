package com.exam.service;

import com.exam.ExamsystemApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author Chen Zuhai
 * @Date 2021-05-27 17:38
 * @email: chenzuhai@induschain.cn
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ScoreServiceTest {
    @Autowired
    private ScoreService scoreService;
    @Test
    public void delete(){
//        scoreService.deleteScoreById("56");
    }
}
