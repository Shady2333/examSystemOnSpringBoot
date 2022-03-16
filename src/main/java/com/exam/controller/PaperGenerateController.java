package com.exam.controller;

import com.exam.entity.*;
import com.exam.service.CalQuestionService;
import com.exam.service.FillQuestionService;
import com.exam.serviceimpl.*;
import com.exam.util.ApiResultHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Zuhai Chen
 * @version 1.0
 * @date 2020/7/18
 */
@RestController
@RequestMapping("paper")
public class PaperGenerateController {
    @Autowired
    private PaperServiceImpl paperService;
    @Autowired
    private MultiQuestionServiceImpl multiQuestionService;
    @Autowired
    private FillQuestionServiceImpl fillQuestionService;
    @Autowired
    private CalQuestionServiceImpl calQuestionService;
    @Autowired
    private ProveQuestionServiceImpl proveQuestionService;

    /**
     * 随机指定范围内N个不重复的数
     * 最简单最基本的方法
     * @param min 指定范围最小值
     * @param max 指定范围最大值
     * @param n 随机数个数
     */
    public static int[] randomCommon(int min, int max, int n){
        if (n > (max - min + 1) || max < min) {
            return null;
        }
        int[] result = new int[n];
        int count = 0;
        while(count+1 < n) {
            int num = (int) (Math.random() * (max - min)) + min;
            boolean flag = true;
            for (int j = 0; j < n; j++) {
                if(num == result[j]){
                    flag = false;
                    break;
                }
            }
            if(flag){
                result[count] = num;
                count++;
            }
        }
        return result;
    }



    @PostMapping(name = "试卷生成",value = "/generate")
    public ApiResult paperGenerate(@RequestParam("size1")String size1,@RequestParam("size2")String size2,
                                   @RequestParam("size3")String size3,@RequestParam("size4")String size4
                                   ,@RequestParam("section") String section){


        Integer paperId = paperService.findOnlyPaperId()+1;

        List<MultiQuestion> MultiQuestionBySection = multiQuestionService.findBySection(section);
        List<FillQuestion> FillQuestionBySection = fillQuestionService.findBySection(section);
        List<CalQuestion> CalQuestionBySection = calQuestionService.findBySection(section);
        List<ProveQuestion> ProveQuestionBySection = proveQuestionService.findBySection(section);

        List<MultiQuestion> AnsMultiQuestion = new LinkedList<>();
        List<FillQuestion> AnsFillQuestion =  new LinkedList<>();
        List<CalQuestion> AnsCalQuestion =  new LinkedList<>();
        List<ProveQuestion> AnsProveQuestion =  new LinkedList<>();

        int SizeMultiQuestionBySection = MultiQuestionBySection.size();
        int SizeFillQuestionBySection = FillQuestionBySection.size();
        int SizeCalQuestionBySection = CalQuestionBySection.size();
        int SizeProveQuestionBySection = ProveQuestionBySection.size();



        if(SizeMultiQuestionBySection < Integer.valueOf(size1)) {
            return  ApiResultHandler.buildApiResult(415, "多选题数量不足,最大个数为"+SizeMultiQuestionBySection, null);

        }
        if(SizeFillQuestionBySection < Integer.valueOf(size2))
        {
            return  ApiResultHandler.buildApiResult(415, "填空题数量不足,最大个数为"+SizeFillQuestionBySection, null);

        }

        if(SizeCalQuestionBySection < Integer.valueOf(size3))
        {
            return  ApiResultHandler.buildApiResult(415, "计算题数量不足,最大个数为"+SizeCalQuestionBySection, null);

        }

        if(SizeProveQuestionBySection < Integer.valueOf(size4))
        {
            return  ApiResultHandler.buildApiResult(415, "证明题数量不足,最大个数为"+SizeProveQuestionBySection, null);
        }



        while(AnsMultiQuestion.size() < Integer.valueOf(size1))
        {
            int[] Ans=randomCommon(0, MultiQuestionBySection.size()-1, Integer.valueOf(size1));
            System.out.println(Ans);
            for(int i=0;i<Ans.length;i++){
                AnsMultiQuestion.add(MultiQuestionBySection.get(Ans[i]));
            }


        }

        while(AnsFillQuestion.size() < Integer.valueOf(size2))
        {
            int[] Ans=randomCommon(0, FillQuestionBySection.size(), Integer.valueOf(size2));
            for(int i=0;i<Ans.length;i++) {
                AnsFillQuestion.add(FillQuestionBySection.get(Ans[i]));
            }
        }

        while(AnsCalQuestion.size() < Integer.valueOf(size1))
        {
            int[] Ans=randomCommon(0, CalQuestionBySection.size(), Integer.valueOf(size1));
            for(int i=0;i<Ans.length;i++) {
                AnsCalQuestion.add(CalQuestionBySection.get(Ans[i]));
            }
        }

        while(AnsProveQuestion.size() < Integer.valueOf(size1))
        {
            int[] Ans=randomCommon(0, ProveQuestionBySection.size(), Integer.valueOf(size1));
            for(int i=0;i<Ans.length;i++) {
                AnsProveQuestion.add(ProveQuestionBySection.get(Ans[i]));
            }
        }

        //通过题目数量随机提取生成试卷

        //将试卷题目增加到试题管理中
        for(int i = 0;i<AnsMultiQuestion.size();i++){
            PaperManage ans = new PaperManage(paperId,1,AnsMultiQuestion.get(i).getQuestionId());
            paperService.add(ans);
        }
        for(int i = 0;i<AnsFillQuestion.size();i++){
            PaperManage ans = new PaperManage(paperId,2,AnsFillQuestion.get(i).getQuestionId());
            paperService.add(ans);
        }
        for(int i = 0;i<AnsCalQuestion.size();i++){
            PaperManage ans = new PaperManage(paperId,3,AnsCalQuestion.get(i).getQuestionId());
            paperService.add(ans);
        }
        for(int i = 0;i<AnsProveQuestion.size();i++){
            PaperManage ans = new PaperManage(paperId,4,AnsProveQuestion.get(i).getQuestionId());
            paperService.add(ans);
        }
        return  ApiResultHandler.buildApiResult(200, "生成试卷成功", 0);
    }

}
