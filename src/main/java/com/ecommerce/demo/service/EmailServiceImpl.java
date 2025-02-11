package com.ecommerce.demo.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.ecommerce.demo.model.Address;
import com.ecommerce.demo.model.Order;
import com.ecommerce.demo.model.OrderItem;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendEmail(String to, String subject, String message) {
        log.info("Sending email to: {}, message: {}", to, message);
        // Implement email sending logic
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(to);
            mailMessage.setSubject(subject);
            mailMessage.setText(message);
            
            javaMailSender.send(mailMessage);
            log.info("Email sent successfully to: {}", to);
        } catch (MailException e) {
            log.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public void sendOrderConfirmationEmail(String to, Order order) {
        try {
            Context context = new Context();
            
             // Format customer name (remove email domain)
            String customerName = order.getCustomerEmail().split("@")[0];
            customerName = customerName.substring(0, 1).toUpperCase() + customerName.substring(1);

            context.setVariable("customerName", customerName);
            context.setVariable("customerEmail", order.getCustomerEmail());
            context.setVariable("orderId", order.getId());
            context.setVariable("orderDate", order.getCreatedAt().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")));
            //context.setVariable("totalAmount", order.getTotalAmount().toString());
            context.setVariable("totalAmount", 
                order.getTotalAmount().setScale(2, RoundingMode.HALF_UP));

             // Format shipping address
            Address address = order.getShippingAddress();
            String formattedAddress = String.format("%s<br>%s, %s %s",
                address.getStreet(),
                address.getCity(),
                address.getState(),
                address.getZipCode());
            context.setVariable("shippingAddress", formattedAddress);
            
            // Format items
            StringBuilder itemsHtml = new StringBuilder();
            for (OrderItem item : order.getItems()) {
                itemsHtml.append("<tr>")
                        .append("<td><div class='item-name'>").append(item.getProductName()).append("</div></td>")
                        .append("<td>").append(item.getQuantity()).append("</td>")
                        .append("<td>$").append(item.getPrice().multiply(new BigDecimal(item.getQuantity())).setScale(2, RoundingMode.HALF_UP)).append("</td>")
                        .append("</tr>");
            }
            context.setVariable("items", itemsHtml.toString());

            // Process template and send email
            String emailContent = templateEngine.process("order-confirmation", context);
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Order Confirmation #" + order.getId());
            helper.setText(emailContent, true);

            javaMailSender.send(message);
            log.info("Order confirmation email sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send order confirmation email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
