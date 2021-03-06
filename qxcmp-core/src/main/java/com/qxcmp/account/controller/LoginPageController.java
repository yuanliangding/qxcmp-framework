package com.qxcmp.account.controller;

import com.qxcmp.account.auth.AuthenticationFailureHandler;
import com.qxcmp.account.form.LoginForm;
import com.qxcmp.account.form.LoginFormWithCaptcha;
import com.qxcmp.account.page.LoginPage;
import com.qxcmp.web.QxcmpController;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * 平台登录页面
 *
 * @author Aaric
 */
@Controller
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginPageController extends QxcmpController {

    @GetMapping
    public ModelAndView login(@RequestParam(required = false) String callback,
                              final LoginForm loginForm,
                              final LoginFormWithCaptcha loginFormWithCaptcha) {

        if (StringUtils.isNotBlank(callback)) {
            loginForm.setCallback(callback);
            loginFormWithCaptcha.setCaptcha(callback);
        }

        boolean showCaptcha = isShowCaptcha();

        return page(LoginPage.class, request, loginForm, loginFormWithCaptcha, showCaptcha);
    }

    /**
     * 是否需要生成验证码
     *
     * @return 是否需要生成验证码
     */
    private boolean isShowCaptcha() {
        boolean showCaptcha = false;

        if (request.getSession().getAttribute(AuthenticationFailureHandler.AUTHENTICATION_SHOW_CAPTCHA) != null) {
            showCaptcha = (boolean) request.getSession().getAttribute(AuthenticationFailureHandler.AUTHENTICATION_SHOW_CAPTCHA);
        }
        return showCaptcha;
    }
}
