package com.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.reggie.common.R;
import com.reggie.entity.User;
import com.reggie.service.UserService;
import com.reggie.utils.SMSUtils;
import com.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 发送手机验证码的短信
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpServletRequest request) {
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotBlank(phone)) {
            //生成随机的6位的验证码
            String validateCode = ValidateCodeUtils.generateValidateCode(6).toString();
            log.info("手机验证码：{}", validateCode);
            //调用阿里云提供的短信服务API完成发送短息
//            SMSUtils.sendMessage("阿里云短信测试", "SMS_154950909", phone, validateCode);
            //需要将生成的验证码保存到Session当中
            request.getSession().setAttribute(phone, validateCode);
            return R.success("手机验证码短信发送成功");
        }
        return R.error("短信发送失败");
    }

    /**
     * 移动端手机验证码登录
     * @param map
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpServletRequest request) {
        log.info(map.toString());
        //获取手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从Session中获取保存的验证码
        String codeInSession = request.getSession().getAttribute(phone).toString();
        //进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
        if (codeInSession != null && code.equals(codeInSession)) {
            //如果能够对比成功，说明登录成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User user = userService.getOne(queryWrapper);
            if (user == null) {//说明该用户是新用户
                //判断当前的手机号对应的用户是否为新用户，如果是新用户就自动完成注册
                user = new User();
                user.setPhone(phone);
//                user.setStatus(1);//这里不设置状态也可，数据库有默认值
                userService.save(user);
            }
            request.getSession().setAttribute("user", user.getId());
            return R.success(user);
        }

        return R.error("登录失败");
    }
}
