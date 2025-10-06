import javax.swing.SwingUtilities;
import view.RegisterView;

public class Main {
    public static void main(String[] args) {
        // Mở Register (từ đây user có thể chuyển qua Login)
        SwingUtilities.invokeLater(() -> new RegisterView());
    }
}
