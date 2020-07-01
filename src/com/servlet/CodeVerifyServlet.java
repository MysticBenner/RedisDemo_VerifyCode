package com.servlet;

import redis.clients.jedis.Jedis;

import javax.servlet.annotation.WebServlet;
import java.io.IOException;

@WebServlet(name = "CodeVerifyServlet", urlPatterns = "/CodeVerifyServlet")
public class CodeVerifyServlet extends javax.servlet.http.HttpServlet {
    protected void doPost(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        //获取手机号码
        String phoneNo = request.getParameter("phone_no");
        //获取验证码
        String code = request.getParameter("verify_code");

        //从本地redis中获取随机生成的验证码并进行验证
        Jedis jedis = new Jedis("192.168.142.201", 6390);
        String codeV = jedis.get("Verify_code:" + phoneNo);

        if(code.equals(codeV)){
            response.getWriter().print(true);
        }
        jedis.quit();
    }
}
