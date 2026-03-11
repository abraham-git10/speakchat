import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;

public class ChatFrame extends JFrame {
    JTextArea c = new JTextArea();
    JScrollPane chat = new JScrollPane(c, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    JTextArea m = new JTextArea();
    JScrollPane me = new JScrollPane(m, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    TextToSpeech tt = new TextToSpeech();
    PrintWriter pw;
    public ChatFrame(String frameName, int panelWidth, int panelHeight, PrintWriter pw) {
        super(frameName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        pack();
        ChatPanel p = new ChatPanel(panelWidth, panelHeight);
        Insets frameInsets = getInsets();
        int frameWidth = panelWidth + (frameInsets.left + frameInsets.right);
        int frameHeight = panelHeight + (frameInsets.top + frameInsets.bottom);
        setPreferredSize(new Dimension(frameWidth, frameHeight));
        this.pw = pw;
        c.setLineWrap(true);
        c.setWrapStyleWord(true);
        c.setEditable(false);
        chat.setBounds(50, 50, 800, 500);
        add(chat, BorderLayout.CENTER);
        m.setLineWrap(true);
        m.setWrapStyleWord(true);
        m.getInputMap().put(KeyStroke.getKeyStroke("ENTER"), "send");
        m.getInputMap().put(KeyStroke.getKeyStroke("shift ENTER"), "line");
        m.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), "exit");
        m.getActionMap().put("send", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String t = m.getText().trim();
                if(!t.isEmpty()) {
                    pw.println(t);
                    tt.tts("You Sent: " + t);
                    m.setText("");
                }
            }
        });
        m.getActionMap().put("line", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                m.append("\n");
            }
        });
        m.getActionMap().put("exit", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        me.setBounds(50, 575, 800, 80);
        add(me);
        m.requestFocusInWindow();
        setLayout(null);
        add(p);
        pack();
        setVisible(true);
        SwingUtilities.invokeLater(() -> {
            tt.tts("This is your chat window. To chat type what you want to chat and then press the enter key. Press Shift and Enter at the same time to start a new line in your message. Press the fn + F4 key to exit.");
        });
    }
    public void sendMsg(String msg) {
        SwingUtilities.invokeLater(() -> {
            c.append(msg + "\n");
            c.setCaretPosition(c.getDocument().getLength());
        });
    }
}