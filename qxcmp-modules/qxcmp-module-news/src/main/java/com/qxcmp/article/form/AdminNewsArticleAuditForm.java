package com.qxcmp.article.form;

import com.qxcmp.web.view.annotation.form.Form;
import com.qxcmp.web.view.annotation.form.TextInputField;
import com.qxcmp.web.view.annotation.form.TextSelectionField;
import lombok.Data;

import static com.qxcmp.web.view.support.utils.FormHelper.SELF_ACTION;

/**
 * @author Aaric
 */
@Form(action = SELF_ACTION)
@Data
public class AdminNewsArticleAuditForm {

    @TextSelectionField("审核结果")
    private String operation = "通过文章";

    @TextInputField(value = "原因", required = true, autoFocus = true, placeholder = "审核通过或者驳回原因")
    private String response;
}
