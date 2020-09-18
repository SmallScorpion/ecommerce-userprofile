package com.warehouse.ecommerceuserprofile.app.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.warehouse.ecommerceuserprofile.app.service.EsQueryService;
import com.warehouse.ecommerceuserprofile.app.support.EsTagQuery;
import com.warehouse.ecommerceuserprofile.app.support.MemberTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;



@Controller
public class EsQueryController {
    @Autowired
    EsQueryService service;

    @RequestMapping("/gen")
    public void genUserInfo(HttpServletResponse response, @RequestBody String data){
        // 1. 首先将页面提交的表单数据中，标签的查询条件，包装成EsTagQuery类的对象
        JSONObject object = JSON.parseObject(data);
        JSONArray selectedTags = object.getJSONArray("selectedTags");
        List<EsTagQuery> list = selectedTags.toJavaList(EsTagQuery.class);

        // 2. 调用服务，按照查询条件，圈人，存成MemberTag
        List<MemberTag> tags = service.getUserInfo(list);

        // 3. 将用户信息进行提取，用response返回给前端
        String content = toContent(tags);
        String fileName = "member.txt";

        response.setContentType("application/octet-stream");
        try {
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            ServletOutputStream sos = response.getOutputStream();
            BufferedOutputStream bos = new BufferedOutputStream(sos);
            bos.write(content.getBytes("UTF-8"));
            bos.flush();    // 直接将缓冲池中数据刷出，默认是要填满才发
            bos.close();
            sos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private String toContent(List<MemberTag> tags) {
        StringBuilder sb = new StringBuilder();
        for (MemberTag tag : tags) {
            sb.append("[" + tag.getMemberId() + "," + tag.getPhone() + "]\r\n");
        }
        return sb.toString();
    }

}
