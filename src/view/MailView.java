package view;

import controller.MailController;
import model.Mail;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MailView extends JFrame {
    private User user;
    private JTextField tfTo, tfSubject;
    private JTextArea taContent;
    private JButton btnSend, btnRefresh, btnView;
    private JTable table;
    private DefaultTableModel tableModel;

    public MailView(User user) {
        this.user = user;
        setTitle("Mail - " + user.getUsername());
        setSize(800,500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // top: send panel
        JPanel sendPanel = new JPanel(new BorderLayout());
        JPanel fields = new JPanel(new GridLayout(2,4,8,8));
        fields.add(new JLabel("From:"));
        fields.add(new JLabel(user.getUsername() + " <" + user.getEmail() + ">"));
        fields.add(new JLabel("To (Email):"));
        tfTo = new JTextField(); fields.add(tfTo);
        fields.add(new JLabel("Subject:"));
        tfSubject = new JTextField(); fields.add(tfSubject);
        fields.add(new JLabel());
        btnSend = new JButton("Send");
        fields.add(btnSend);
        sendPanel.add(fields, BorderLayout.NORTH);

        taContent = new JTextArea(6,60);
        sendPanel.add(new JScrollPane(taContent), BorderLayout.CENTER);

        add(sendPanel, BorderLayout.NORTH);

        // center: inbox table
        tableModel = new DefaultTableModel(new Object[]{"ID","From","Subject","Time"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // bottom: controls
        JPanel bottom = new JPanel();
        btnRefresh = new JButton("Refresh Inbox");
        btnView = new JButton("View Mail");
        bottom.add(btnRefresh);
        bottom.add(btnView);
        add(bottom, BorderLayout.SOUTH);

        // actions
        btnSend.addActionListener(e -> handleSend());
        btnRefresh.addActionListener(e -> loadInbox());
        btnView.addActionListener(e -> handleView());

        loadInbox();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void handleSend() {
        String to = tfTo.getText().trim();
        String subject = tfSubject.getText().trim();
        String content = taContent.getText();

        if (to.isEmpty() || subject.isEmpty() || content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng điền To, Subject, Content.");
            return;
        }

        Mail m = new Mail(user.getUsername(), user.getEmail(), to, subject, content, java.time.LocalDateTime.now());
        boolean ok = MailController.sendMail(m);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Mail sent (to server).");
            taContent.setText("");
            tfSubject.setText("");
            tfTo.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Gửi mail thất bại.");
        }
    }

    private void loadInbox() {
        tableModel.setRowCount(0);
        List<Mail> list = MailController.listMailsFor(user.getUsername());
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (Mail m : list) {
            String t = m.getTimeSent() != null ? m.getTimeSent().format(fmt) : "";
            tableModel.addRow(new Object[]{m.getId(), m.getSender(), m.getSubject(), t});
        }
    }

    private void handleView() {
        int sel = table.getSelectedRow();
        if (sel < 0) {
            JOptionPane.showMessageDialog(this, "Chọn 1 mail để xem.");
            return;
        }
        int id = (int) tableModel.getValueAt(sel, 0);
        Mail m = MailController.getMailById(id);
        if (m == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy mail.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("From: ").append(m.getSender()).append(" <").append(m.getSenderEmail()).append(">\n");
        sb.append("To: ").append(m.getReceiver()).append("\n");
        sb.append("Time: ").append(m.getTimeSent()).append("\n");
        sb.append("Subject: ").append(m.getSubject()).append("\n\n");
        sb.append(m.getContent());
        JTextArea ta = new JTextArea(sb.toString());
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(true);
        JOptionPane.showMessageDialog(this, new JScrollPane(ta), "Mail #" + id, JOptionPane.INFORMATION_MESSAGE);
    }
}
