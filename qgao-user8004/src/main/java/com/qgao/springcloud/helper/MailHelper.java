package com.qgao.springcloud.helper;

import com.qgao.springcloud.utils.util.EncryptUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.Base64;

@Component
@Slf4j
public class MailHelper {

    @Resource
    private JavaMailSender mailSender;
    @Value("${spring.mail.username}")
    private String fromMail;
    @Value("${qgao.mail.banner}")
    private String bannerPath;
    @Value("${qgao.frontjump}")
    private String frontJumpHtml;

    public void sendMailWithBanner(String toMail,String subject,String frontPageHost) throws Exception {

        //24h过期
        long time = System.currentTimeMillis()+24*60*60*1000;
        String deadline = String.valueOf(time);
        String applyToken = EncryptUtil.calcMD5(toMail+deadline);
        String params = "email="+toMail+"&deadline="+deadline+"&applytoken="+applyToken;

        InputStream in = new ClassPathResource(bannerPath).getInputStream();
        byte[] bannerByte = new byte[in.available()];
        in.read(bannerByte);
        String bannerBase64 = Base64.getEncoder().encodeToString(bannerByte);
        String bannerTag = "<img src=\"data:image/png;base64,"+bannerBase64+"\" /><br>";

        String contentTag = "<h2>你已经向qgao申请了加入请求，如果已经确定，请在24h内点击下方链接加入我们（或复制打开）</h2><br>" +
                "<a href='"+frontPageHost+frontJumpHtml+"?"+params+
                "'>"+frontPageHost+frontJumpHtml+"?"+params+"</a>";

        String mainContent = bannerTag+contentTag;

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromMail);
            helper.setTo(toMail);
            helper.setSubject(subject);
            helper.setText(mainContent, true);
            // 加载文件资源，作为附件
//            FileSystemResource file = new FileSystemResource(new File("C:\\Users\\吴超\\Pictures\\Camera Roll\\微信截图_20191016142536.png"));
//            // 调用MimeMessageHelper的addInline方法替代成文件('jpg[标记]', file[文件])
//            helper.addInline("jpg", file);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        mailSender.send(message);

        log.debug("send mail success");
    }
}
