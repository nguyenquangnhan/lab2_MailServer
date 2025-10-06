package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import model.Database;

/*
 MailServer: nhận UDP payload có format:
    sender|senderEmail|receiver|subject|content
  Các ký tự '|' trong dữ liệu được escape thành '\|' và newline '\n' được mã hóa '\n' trước khi gửi.
  Server sẽ:
    - parse payload
    - insert vào DB (table mails)
    - append vào mails.txt
    - gửi lại ACK cho client
*/
public class MailServer {
    private static final int PORT = 8888;
    private static final int BUF = 64 * 1024;

    public static void main(String[] args) {
        System.out.println("MailServer UDP running on port " + PORT);
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            byte[] buf = new byte[BUF];
            while (true) {
                DatagramPacket packet = new DatagramPacket(buf, buf.length);
                socket.receive(packet);

                String payload = new String(packet.getData(), 0, packet.getLength(), "UTF-8");
                // parse
                String[] parts = splitPayload(payload);
                String sender = unescape(parts.length > 0 ? parts[0] : "");
                String senderEmail = unescape(parts.length > 1 ? parts[1] : "");
                String receiver = unescape(parts.length > 2 ? parts[2] : "");
                String subject = unescape(parts.length > 3 ? parts[3] : "");
                String content = unescape(parts.length > 4 ? parts[4] : "");

                LocalDateTime now = LocalDateTime.now();

                // save to DB
                boolean dbOk = insertMailToDB(sender, senderEmail, receiver, subject, content, now);

                // append to mails.txt
                try (PrintWriter pw = new PrintWriter(new FileWriter("mails.txt", true))) {
                    pw.println("=== " + now + " ===");
                    pw.println("From: " + sender + " <" + senderEmail + ">");
                    pw.println("To: " + receiver);
                    pw.println("Subject: " + subject);
                    pw.println("Content:");
                    pw.println(content);
                    pw.println();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // ACK
                String ack = dbOk ? "OK: saved" : "OK: received (DB failed)";
                byte[] ackBytes = ack.getBytes("UTF-8");
                InetAddress addr = packet.getAddress();
                int port = packet.getPort();
                DatagramPacket ackP = new DatagramPacket(ackBytes, ackBytes.length, addr, port);
                socket.send(ackP);

                System.out.println("Mail received from " + sender + " -> " + receiver + " at " + now);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static boolean insertMailToDB(String sender, String senderEmail, String receiver, String subject, String content, LocalDateTime time) {
        String sql = "INSERT INTO mails(sender,sender_email,receiver,subject,content,time_sent) VALUES(?,?,?,?,?,?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sender);
            ps.setString(2, senderEmail);
            ps.setString(3, receiver);
            ps.setString(4, subject);
            ps.setString(5, content);
            ps.setTimestamp(6, Timestamp.valueOf(time));
            ps.executeUpdate();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // split by '|' but ignoring escaped '\|'
    private static String[] splitPayload(String s) {
        java.util.List<String> parts = new java.util.ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean esc = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (esc) {
                cur.append(c);
                esc = false;
            } else {
                if (c == '\\') {
                    esc = true;
                } else if (c == '|') {
                    parts.add(cur.toString());
                    cur.setLength(0);
                } else {
                    cur.append(c);
                }
            }
        }
        parts.add(cur.toString());
        return parts.toArray(new String[0]);
    }

    private static String unescape(String s) {
        if (s == null) return "";
        return s.replace("\\n", "\n").replace("\\|", "|");
    }
}
