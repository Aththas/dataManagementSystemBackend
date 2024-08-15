package com.mobitel.data_management.other.emailService;

import com.mobitel.data_management.auth.entity.user.User;
import com.mobitel.data_management.auth.repository.UserRepository;
import com.mobitel.data_management.entity.Amc;
import com.mobitel.data_management.repository.AmcRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final AmcRepository amcRepository;

    public void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    @Scheduled(cron = "0 43 20 * * THU")// Every Monday at 8.30 PM
    public void scheduledEmail(){

        LocalDate now = LocalDate.now();
        LocalDate threeMonthsLater = now.plusMonths(3);

        Date sqlNow = Date.valueOf(now);
        Date sqlThreeMonthsLater = Date.valueOf(threeMonthsLater);

        List<Amc> amcExpiringSoonList = amcRepository.findAllByEndDateBetweenAndIsAcknowledgedIsFalse(sqlNow, sqlThreeMonthsLater);

        for (Amc amc : amcExpiringSoonList) {
            if (!amc.isFirstEmailSent()) {
                sendEmailToSuperUsers(amc);
                amc.setFirstEmailSent(true);
                amcRepository.save(amc);
            } else {
                sendReminderEmailToSuperUsers(amc);
            }
        }
    }

    private void sendReminderEmailToSuperUsers(Amc amc) {
        //asynchronous email sending
        CompletableFuture.runAsync(() -> {
            List<User> superUsers = userRepository.findBySuperUserTrue();

            for (User superUser : superUsers) {
                try {
                    sendEmail(
                            superUser.getEmail(),
                            "AMC Expiry Reminder Notification",
                            "Reminder: The AMC Contract " + amc.getContractName() + ", is still pending acknowledgment."
                    );
                    log.info("Email sent to Super User: " + superUser.getEmail());
                } catch (Exception e) {
                    log.error("Failed to send email to Super User: " + superUser.getEmail(), e);
                }
            }
        });
    }

    private void sendEmailToSuperUsers(Amc amc) {

        //asynchronous email sending
        CompletableFuture.runAsync(() -> {
            List<User> superUsers = userRepository.findBySuperUserTrue();

            for (User superUser : superUsers) {
                try {
                    sendEmail(
                            superUser.getEmail(),
                            "AMC Expiry Notification",
                            "AMC Contract " + amc.getContractName() + ", is expiring soon. Please acknowledge."
                    );
                    log.info("Email sent to Super User: " + superUser.getEmail());
                } catch (Exception e) {
                    log.error("Failed to send email to Super User: " + superUser.getEmail(), e);
                }
            }
        });
    }
}
