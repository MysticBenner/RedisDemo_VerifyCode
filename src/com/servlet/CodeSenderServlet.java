package com.servlet;

import redis.clients.jedis.Jedis;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.util.Random;

@WebServlet(name = "CodeSenderServlet", urlPatterns = "/CodeSenderServlet")
public class CodeSenderServlet extends javax.servlet.http.HttpServlet {

    // 生成随机验证码
    private String getCode(int len){
        String code = "";
        Random random = new Random();

        for(int i = 0; i < len; i++){
            code += random.nextInt(10);
        }

        return code;
    }
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        //获取手机号码
        String phoneNo = request.getParameter("phone_no");
        //获取生成的6位数随机验证码
        String code = getCode(6);
        //连接本机Redis
        Jedis jedis = new Jedis("192.168.142.201", 6390);

        //将这个键值对存入Redis中
        //首先拼接一个唯一键
        String codeKey = "Verify_code:" + phoneNo + ":code";
        //同样拼接一个次数键
        String countKey = "Verify_code" + phoneNo + ":count";

        //验证发送的次数
        String count = jedis.get(countKey);
        if(count == null) {
            //代表第一次
            jedis.setex(countKey, 24*60*60, "1");
        }else if(Integer.parseInt(count) <= 4) {
            jedis.incr(countKey);
        }else if(Integer.parseInt(count) > 4) {
            response.getWriter().print("limit");
            jedis.quit();
            return ;
        }

        //将验证码键值对存入Redis
        jedis.setex(codeKey, 120, code);
        jedis.quit();
        response.getWriter().print(true);
    }
}
