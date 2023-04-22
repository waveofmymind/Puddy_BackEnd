package com.team.puddy.global.email;

import com.team.puddy.domain.user.domain.User;
import com.team.puddy.domain.user.repository.UserRepository;
import com.team.puddy.domain.user.service.UserService;
import com.team.puddy.global.error.exception.BusinessException;
import com.team.puddy.global.error.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.naming.Context;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.Random;

import static com.team.puddy.global.error.ErrorCode.NOT_MATCH_INFO;
import static com.team.puddy.global.error.ErrorCode.SEND_EMAIL_FAIL;
import static org.springframework.security.core.context.SecurityContextHolder.setContext;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JavaMailSender javaMailSender;
    private final UserService userService;

    private static final String title = "퍼디 임시 비밀번호 안내 메일입니다.";
    private static final String message = """
            안녕하세요. 퍼디 임시 비밀번호 안내 메일입니다.\s
            회원님의 임시 비밀번호는 아래와 같습니다.  로그인 후 반드시 비밀번호를 변경해주세요.
            """;
    private static final String fromAddress = "sjun0913@gmail.com";


    @Transactional
    public void findPassword(String account, String username, String email) {
        String tmpPassword = createTmpPassword();
        User findUser = userRepository.findByAccountAndUsernameAndEmail(account, username, email)
                .orElseThrow(() -> new NotFoundException(NOT_MATCH_INFO));
        findUser.updatePassword(passwordEncoder.encode(tmpPassword));
        EmailDto mail = createMail(tmpPassword, findUser.getEmail());
        sendMail(mail);
    }

    @Transactional
    public void sendMail(EmailDto email) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(email.toAddress());
        mailMessage.setSubject(email.title());
        mailMessage.setFrom(email.fromAddress());
        mailMessage.setText(email.message());

        javaMailSender.send(mailMessage);
        log.info("메일전송완료");
    }

    @Transactional
    public EmailDto createMail(String tmpPassword, String email) {
        return EmailDto.builder().
                toAddress(email)
                .message(message + tmpPassword)
                .title(title)
                .fromAddress(fromAddress)
                .build();
    }

    @Transactional
    // 인증번호 및 임시 비밀번호 생성 메서드
    public String createTmpPassword() {
        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            key.append(random.nextInt(10));
        }
        return key.toString();
    }


    public void sendDocs(MultipartFile file,Long userId) {
        User findUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(NOT_MATCH_INFO));

        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo("puddyofficial1@gmail.com");
            helper.setSubject("퍼디 문서 전송 메일입니다.");
            helper.setText("퍼디 문서 전송 메일입니다.");
            helper.addAttachment(Objects.requireNonNull(file.getOriginalFilename()), file);
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new BusinessException(SEND_EMAIL_FAIL);
        }
    }
}
