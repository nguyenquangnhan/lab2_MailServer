package model;

import java.time.LocalDateTime;

public class Mail {
    private int id;
    private String sender;
    private String senderEmail;
    private String receiver;
    private String subject;
    private String content;
    private LocalDateTime timeSent;

    public Mail(String sender, String senderEmail, String receiver, String subject, String content, LocalDateTime timeSent) {
        this.sender = sender;
        this.senderEmail = senderEmail;
        this.receiver = receiver;
        this.subject = subject;
        this.content = content;
        this.timeSent = timeSent;
    }

    public Mail(int id, String sender, String senderEmail, String receiver, String subject, String content, LocalDateTime timeSent) {
        this.id = id;
        this.sender = sender;
        this.senderEmail = senderEmail;
        this.receiver = receiver;
        this.subject = subject;
        this.content = content;
        this.timeSent = timeSent;
    }

    public int getId() { return id; }
    public String getSender() { return sender; }
    public String getSenderEmail() { return senderEmail; }
    public String getReceiver() { return receiver; }
    public String getSubject() { return subject; }
    public String getContent() { return content; }
    public LocalDateTime getTimeSent() { return timeSent; }
}
