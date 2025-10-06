package controller;

import model.Mail;
import model.Database;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MailController {
    // server config (server phải chạy)
    public static final String SERVER_HOST = "localhost";
    public static final int SERVER_PORT = 8888;

    // Gửi mail đến MailServer qua UDP. Format: sender|senderEmail|receiver|subject|content
    public static boolean sendMail(Mail mail) {
        try (DatagramSocket socket = new DatagramSocket()) {
            String payload = escape(mail.getSender()) + "|" + escape(mail.getSenderEmail()) + "|"
                    + escape(mail.getReceiver()) + "|" + escape(mail.getSubject()) + "|" + escape(mail.getContent());
            byte[] data = payload.getBytes("UTF-8");
            DatagramPacket packet = new DatagramPacket(data, data.length, InetAddress.getByName(SERVER_HOST), SERVER_PORT);
            socket.send(packet);

            // receive ACK (optional)
            byte[] buf = new byte[2048];
            DatagramPacket ack = new DatagramPacket(buf, buf.length);
            socket.setSoTimeout(3000);
            try {
                socket.receive(ack);
                String r = new String(ack.getData(), 0, ack.getLength(), "UTF-8");
                // we accept any non-empty reply as success
                return r != null && !r.isEmpty();
            } catch (Exception e) {
                // no ack — still return true to indicate we sent (you may want to retry in real app)
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Escape '|' and newline to avoid breaking format
    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("|", "\\|").replace("\n", "\\n");
    }

    private static String unescape(String s) {
        if (s == null) return "";
        return s.replace("\\n", "\n").replace("\\|", "|");
    }

    // Lấy danh sách mail cho 1 người (truy vấn trực tiếp DB vì MailServer đã ghi vào DB)
    public static List<Mail> listMailsFor(String username) {
        List<Mail> res = new ArrayList<>();
        String sql = "SELECT id,sender,sender_email,receiver,subject,content,time_sent FROM mails WHERE receiver=? ORDER BY time_sent DESC";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Mail m = new Mail(
                            rs.getInt("id"),
                            rs.getString("sender"),
                            rs.getString("sender_email"),
                            rs.getString("receiver"),
                            rs.getString("subject"),
                            rs.getString("content"),
                            rs.getTimestamp("time_sent").toLocalDateTime()
                    );
                    res.add(m);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return res;
    }

    public static Mail getMailById(int id) {
        String sql = "SELECT id,sender,sender_email,receiver,subject,content,time_sent FROM mails WHERE id=?";
        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Mail(
                            rs.getInt("id"),
                            rs.getString("sender"),
                            rs.getString("sender_email"),
                            rs.getString("receiver"),
                            rs.getString("subject"),
                            rs.getString("content"),
                            rs.getTimestamp("time_sent").toLocalDateTime()
                    );
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
