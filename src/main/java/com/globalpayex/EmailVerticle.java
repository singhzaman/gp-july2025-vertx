package com.globalpayex;

import com.globalpayex.dao.StudentDao;
import com.globalpayex.services.StudentsService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import io.vertx.ext.mail.StartTLSOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailVerticle extends AbstractVerticle {

    private static final Logger logger = LoggerFactory
            .getLogger(EmailVerticle.class);

    private MailClient mailClient;

    private StudentsService studentsService;

    @Override
    public void start() throws Exception {
        var config = config();

        var mailConfig = new MailConfig();
        mailConfig.setHostname(config.getString("emailHost"));
        mailConfig.setPort(config.getInteger("emailPort"));
        mailConfig.setStarttls(StartTLSOptions.REQUIRED);
        mailConfig.setUsername(config.getString("emailUsername"));
        mailConfig.setPassword(config.getString("emailPassword"));

        this.mailClient = MailClient.create(vertx, mailConfig);
        this.studentsService = new StudentsService(new StudentDao(
                vertx, config
        ));

        vertx.eventBus()
                .<JsonObject>consumer(
                        "new.student",
                        this::handleNewStudent
                );
    }

    private void handleNewStudent(Message<JsonObject> message) {
        var newStudentObj = message.body();
        var id = newStudentObj.getString("_id");
        this.studentsService.getStudentById(id)
            .onSuccess(studentJsonObject -> {
               var username = studentJsonObject.getString("username");

               var mailMessage = new MailMessage();
               mailMessage.setFrom("mehul.chopra.dev@gmail.com");
               mailMessage.setTo(username);
               mailMessage.setSubject("Registration success -- Welcome");
               mailMessage.setText("Welcome to our portal. Your registration was a success" +
                       " This is just for testing purpose");
               this.mailClient.sendMail(mailMessage)
                   .onSuccess(mailResult -> {
                       logger.info(
                               "Email to be sent to {}. Message id {}",
                               username,
                               mailResult.getMessageID());
                   })
                   .onFailure(err -> {
                      logger.error("Unable to send email. error is {}", err);
                   });
            })
            .onFailure(err -> {
               logger.error("Error in finding student. Error {}", err);
            });
    }
}
