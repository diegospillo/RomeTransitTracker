package view;

import auth.AuthenticationService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import app.AppLauncher;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class LoginView extends JDialog {
    private String username;

    private LoginView(AuthenticationService auth) {
        super((Frame) null, "Login", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // ---------- Header ----------
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(new EmptyBorder(20, 20, 0, 20));
        header.setOpaque(true);
        header.setBackground(new Color(249, 250, 252));

        JLabel title = new JLabel("Benvenuto su Rome Transit Tracker");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));

        JLabel subtitle = new JLabel("Accedi o registrati");
        subtitle.setFont(subtitle.getFont().deriveFont(14f));
        subtitle.setForeground(new Color(95, 95, 95));

        JPanel titleBox = new JPanel();
        titleBox.setOpaque(false);
        titleBox.setLayout(new BoxLayout(titleBox, BoxLayout.Y_AXIS));
        titleBox.add(title);
        titleBox.add(Box.createVerticalStrut(6));
        titleBox.add(subtitle);

        header.add(titleBox, BorderLayout.CENTER);
        add(header, BorderLayout.NORTH);

        // ---------- Form ----------
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel userLabel = new JLabel("Username");
        JTextField userField = new JTextField(24);
        userField.setFont(userField.getFont().deriveFont(16f));

        JLabel passLabel = new JLabel("Password");
        JPasswordField passField = new JPasswordField(24);
        passField.setFont(passField.getFont().deriveFont(16f));
        final char defaultEcho = passField.getEchoChar();
        JCheckBox showPassword = new JCheckBox("Mostra password");
        showPassword.setOpaque(false);

        // Riga 1: label user
        gbc.gridx = 0; gbc.gridy = 0;
        form.add(userLabel, gbc);
        // Riga 2: campo user
        gbc.gridy = 1;
        form.add(userField, gbc);
        // Riga 3: label pass
        gbc.gridy = 2;
        form.add(passLabel, gbc);
        // Riga 4: campo pass
        gbc.gridy = 3;
        form.add(passField, gbc);
        // Riga 5: toggle show/hide
        gbc.gridy = 4;
        form.add(showPassword, gbc);

        add(form, BorderLayout.CENTER);

        // ---------- Footer (azioni + feedback) ----------
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(new EmptyBorder(0, 20, 20, 20));

        JLabel feedback = new JLabel(" ");
        feedback.setForeground(new Color(160, 0, 0));
        footer.add(feedback, BorderLayout.NORTH);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton registerBtn = new JButton("Registrati");
        JButton loginBtn = new JButton("Accedi");
        loginBtn.setPreferredSize(new Dimension(120, 36));
        actions.add(registerBtn);
        actions.add(loginBtn);
        footer.add(actions, BorderLayout.SOUTH);

        add(footer, BorderLayout.SOUTH);

        // ---------- Comportamento ----------
        // Mostra/Nascondi password
        showPassword.addActionListener(e ->
                passField.setEchoChar(showPassword.isSelected() ? (char) 0 : defaultEcho));

        // ENTER = Login
        getRootPane().setDefaultButton(loginBtn);

        // ESC = Chiudi (annulla)
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        // Azione Login
        loginBtn.addActionListener(e -> {
            setBusy(true, userField, passField, loginBtn, registerBtn, showPassword);
            feedback.setText(" ");
            String u = userField.getText().trim();
            char[] p = passField.getPassword();

            if (u.isEmpty() || p.length == 0) {
                feedback.setText("Inserisci username e password.");
                setBusy(false, userField, passField, loginBtn, registerBtn, showPassword);
                return;
            }

            try {
                boolean ok = auth.login(u, p); // tua firma: (String, char[])
                if (ok) {
                    username = u;
                    dispose();
                } else {
                    feedback.setText("Credenziali non valide.");
                }
            } catch (Exception ex) {
                feedback.setText(ex.getMessage() != null ? ex.getMessage() : "Errore di autenticazione.");
            } finally {
                // igiene: azzera il buffer password
                java.util.Arrays.fill(p, '\0');
                setBusy(false, userField, passField, loginBtn, registerBtn, showPassword);
            }
        });

        // Azione Registrazione
        registerBtn.addActionListener(e -> {
            setBusy(true, userField, passField, loginBtn, registerBtn, showPassword);
            feedback.setText(" ");
            String u = userField.getText().trim();
            char[] p = passField.getPassword();

            if (u.isEmpty() || p.length == 0) {
                feedback.setText("Inserisci username e password per registrarti.");
                setBusy(false, userField, passField, loginBtn, registerBtn, showPassword);
                return;
            }

            try {
                auth.register(u, p); // tua firma: (String, char[]) che può lanciare eccezione
                JOptionPane.showMessageDialog(this, "Registrazione completata", "Ok", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        ex.getMessage() != null ? ex.getMessage() : "Errore durante la registrazione",
                        "Errore",
                        JOptionPane.ERROR_MESSAGE);
            } finally {
                java.util.Arrays.fill(p, '\0');
                setBusy(false, userField, passField, loginBtn, registerBtn, showPassword);
            }
        });

        // ---------- Dimensioni & posizionamento ----------
        setResizable(false);
        setMinimumSize(new Dimension(460, 380));
        pack(); // calcola in base ai preferiti
        setSize(new Dimension(520, 420)); // un po' più ampia per respiro
        setLocationRelativeTo(null); // centra sullo schermo
        getContentPane().setBackground(new Color(249, 250, 252));
    }

    private void setBusy(boolean busy, JComponent... comps) {
        for (JComponent c : comps) c.setEnabled(!busy);
        setCursor(busy ? Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR) : Cursor.getDefaultCursor());
    }

    public static String showLogin(AuthenticationService auth) {
        LoginView dialog = new LoginView(auth);
     // ICONA APP
        List<Image> icons = loadIcons();
        if (!icons.isEmpty()) {
        	dialog.setIconImages(icons); // title bar e taskbar
            try {
                Taskbar.getTaskbar().setIconImage(icons.get(icons.size() - 1)); // dock macOS / taskbar
            } catch (UnsupportedOperationException | SecurityException ignored) {}
        }
        dialog.setVisible(true);
        return dialog.username; // null se chiusa o login fallito
    }
    
    private static List<Image> loadIcons() {
        String base = "/icon/app/"; // cartella in src/main/resources/icon/
        String[] names = {
            "appIcon-16.png", "appIcon-32.png", "appIcon-48.png",
            "appIcon-64.png", "appIcon-128.png", "appIcon-256.png"
        };
        List<Image> out = new ArrayList<>();
        for (String n : names) {
            try (InputStream in = AppLauncher.class.getResourceAsStream(base + n)) {
                if (in == null) {
                    System.err.println("Icona non trovata: " + base + n);
                    continue;
                }
                BufferedImage img = ImageIO.read(in);
                if (img != null) out.add(img);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return out;
    }
    
}
