package com.dongyun.cnucinema.service;

import com.dongyun.cnucinema.spec.entity.Customer;
import com.dongyun.cnucinema.spec.entity.Schedule;
import com.dongyun.cnucinema.spec.entity.Ticketing;
import com.dongyun.cnucinema.spec.service.MailService;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;

@Service
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    public MailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendTicketingMail(Customer customer, Schedule schedule, Ticketing ticketing) {
        MimeMessage message = mailSender.createMimeMessage();
        String content = "예매가 완료되었습니다.\n" +
                "영화 이름: %s\n" +
                "상영 시작 시각: %s\n" +
                "영화관: %s\n" +
                "예매한 좌석 수: %s\n";
        String formattedContent = String.format(
                content,
                ticketing.getMovieTitle(),
                ticketing.getScheduleShowAt().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")),
                schedule.getTname(),
                ticketing.getSeats());

        try {
            message.setSubject("[CNU 영화관] 예매가 완료되었습니다.");
            message.setText(formattedContent, "UTF-8");
            message.setFrom("\"CNU Cinema\" <noreply@cnucinema.com>");
            message.addRecipient(MimeMessage.RecipientType.TO,
                    new InternetAddress(customer.getEmail(), customer.getName(), "UTF-8"));
            mailSender.send(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
