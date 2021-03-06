package com.qxcmp.admin.form;

import com.qxcmp.web.view.annotation.form.EmailField;
import com.qxcmp.web.view.annotation.form.Form;
import com.qxcmp.web.view.annotation.form.ImageCaptchaField;
import lombok.Data;

import javax.validation.constraints.Email;

/**
 * @author Aaric
 */
@Form(value = "邮箱绑定", submitText = "发送验证码")
@Data
public class AdminProfileSecurityEmailForm {

    @Email
    @EmailField(value = "邮箱", required = true, autoFocus = true)
    private String email;

    @ImageCaptchaField(value = "验证码", required = true)
    private String captcha;
}
