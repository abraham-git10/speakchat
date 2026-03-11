import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.net.Socket;
import java.util.*;
import javax.swing.Timer;

public class RoloFrame extends JFrame implements KeyListener {
    ChatFrame cF;
    ArrayList<RoloContact> cla = new ArrayList<>();
    boolean r;
    JList cl = new JList(cla.toArray());
    JScrollPane sp = new JScrollPane(cl, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
    JLabel f = new JLabel("First Name:");
    JLabel l = new JLabel("Last Name:");
    JLabel pn = new JLabel("Phone Number:");
    JLabel a = new JLabel("Address:");
    JTextField fi = new JTextField();
    JTextField li = new JTextField();
    JTextField pni = new JTextField();
    JTextField ai = new JTextField();
    JButton s = new JButton("Save");
    JButton n = new JButton("New");
    JButton sc = new JButton("Save Changes");
    JButton dc = new JButton("Delete Contact");
    JButton c = new JButton("Enter a Chat");
    TextToSpeech tt = new TextToSpeech();
    String lm = "";
    boolean talk = true;
    volatile boolean connecting = true;
    int o = 0;
    Timer timer;
    int si = 0;
    BufferedReader input;
    PrintWriter pw;
    Socket soc;
    String p = "";
    File uC;
    ArrayList<String> pns = new ArrayList<>();
    public RoloFrame(String frameName, int panelWidth, int panelHeight) {
        super(frameName);
        new Thread(() -> {
            try {
                connection();
            }
            catch(IOException e) {
                e.printStackTrace();
            }
            finally {
                connecting = false;
                SwingUtilities.invokeLater(() ->
                    tt.tts("This is your contact list. If you already have contacts saved, you can chat with those contacts by pressing the fn + f1 key. Use Tab to navigate the application. To repeat this information press the fn +f2 key.")
                );
                fi.requestFocusInWindow();
            }
        }).start();
        setResizable(false);
        pack();
        r = true;
        RoloPanel p = new RoloPanel(panelWidth, panelHeight);
        Insets frameInsets = getInsets();
        int frameWidth = panelWidth + (frameInsets.left + frameInsets.right);
        int frameHeight = panelHeight + (frameInsets.top + frameInsets.bottom);
        setPreferredSize(new Dimension(frameWidth, frameHeight));
        cl.setListData(cla.toArray());
        sp.setBounds(10, 50, 400, 600);
        cl.addListSelectionListener(e -> hn());
        add(sp);
        f.setFont(new Font("Calibre", Font.BOLD, 15));
        f.setBounds(420, 70, 200, 15);
        add(f);
        l.setFont(new Font("Calibre", Font.BOLD, 15));
        l.setBounds(420, 170, 200, 15);
        add(l);
        pn.setFont(new Font("Calibre", Font.BOLD, 15));
        pn.setBounds(420, 270, 200, 15);
        add(pn);
        a.setFont(new Font("Calibre", Font.BOLD, 15));
        a.setBounds(420, 370, 200, 15);
        add(a);
        fi.setFont(new Font("Calibre", Font.ITALIC, 12));
        fi.setBounds(620, 70, 250, 20);
        add(fi);
        fi.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if(connecting == false) {
                    tt.tts("First name");
                }
            }
        });
        li.setFont(new Font("Calibre", Font.ITALIC, 12));
        li.setBounds(620, 170, 250, 20);
        add(li);
        li.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if(connecting == false) {
                    tt.tts("Last name");
                }
            }
        });
        pni.setFont(new Font("Calibre", Font.ITALIC, 12));
        pni.setBounds(620, 270, 250, 20);
        add(pni);
        pni.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if(connecting == false) {
                    tt.tts("Phone Number");
                }
            }
        });
        ai.setFont(new Font("Calibre", Font.ITALIC, 12));
        ai.setBounds(620, 370, 250, 20);
        add(ai);
        ai.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if(connecting == false) {
                    tt.tts("Address");
                }
            }
        });
        s.setFont(new Font("Calibre", Font.BOLD, 15));
        s.setBounds(540, 420, 75, 30);
        s.addActionListener(e -> save());
        s.setVisible(true);
        add(s);
        s.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if(connecting == false) {
                    tt.tts("Save Contact");
                }
            }
        });
        n.setFont(new Font("Calibre", Font.BOLD, 15));
        n.setBounds(665, 420, 75, 30);
        n.addActionListener(e -> newC());
        n.setVisible(true);
        add(n);
        n.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if(connecting == false) {
                    tt.tts("Create New Contact");
                }
            }
        });
        sc.setFont(new Font("Calibre", Font.BOLD, 15));
        sc.setBounds(540, 500, 200, 30);
        sc.addActionListener(e -> saveC());
        sc.setVisible(false);
        add(sc);
        sc.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if(connecting == false) {
                    tt.tts("Save Changes");
                }
            }
        });
        dc.setFont(new Font("Calibre", Font.BOLD, 15));
        dc.setBounds(540, 560, 200, 30);
        dc.setVisible(false);
        dc.addActionListener(e -> deleteC());
        add(dc);
        dc.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if(connecting == false) {
                    tt.tts("Delete Contact");
                }
            }
        });
        c.setFont(new Font("Calibre", Font.BOLD, 15));
        c.setBounds(540, 470, 200, 30);
        c.addActionListener(e -> c());
        c.setVisible(false);
        add(c);
        c.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if(connecting == false) {
                    tt.tts("Enter Chat");
                }
            }
        });
        cl.addKeyListener(this);
        fi.addKeyListener(this);
        li.addKeyListener(this);
        pni.addKeyListener(this);
        ai.addKeyListener(this);
        s.addKeyListener(this);
        n.addKeyListener(this);
        sc.addKeyListener(this);
        dc.addKeyListener(this);
        c.addKeyListener(this);
        setLayout(null);
        add(p);
        pack();
        setVisible(true);
        addKeyListener(this);
        setFocusable(true);
        talk = false;
        pns.clear();
    }
    public void lC() {
        cla.clear();
        try(BufferedReader r = new BufferedReader(new FileReader(uC))) {
            String c = "";
            while((c = r.readLine()) != null) {
                String[] info = c.split(",");
                cla.add(new RoloContact(info[0], info[1], info[2], info[3]));
            }
            cl.setListData(cla.toArray());
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
    public void rc() {
        if(cla.isEmpty()) {
            tt.tts("Your contacts list is empty");
            return;
        }
        talk = true;
        si = 0;
        tt.tts("Your contacts will be read to you in alphabetical order, when you hear the contact you want to chat with, press the fn + F3 key");
        timer = new Timer(3000, e -> {
            if(!talk || si >= cla.size()) {
                ((Timer) e.getSource()).stop();
                return;
            }
            tt.tts(cla.get(si).getf() + " " + cla.get(si).getl());
            o = si;
            si++;
        });
        timer.start();
    }
    public void save() {
        if(fi.getText().equals("") || li.getText().equals("")) {
            talk = true;
            tt.tts("Your contact must have a first and last name.");
            talk = false;
            JOptionPane.showMessageDialog(null, "Your contact must have a first and last name.");
        }
        else {
            RoloContact c = new RoloContact(li.getText(), fi.getText(), pni.getText(), ai.getText());
            cla.add(c);
            setAlphabetical();
            write(true);
            cl.setListData(cla.toArray());
            fi.setText("");
            li.setText("");
            pni.setText("");
            ai.setText("");
            talk = true;
            tt.tts("Your Contact has been saved");
            talk = false;
            fi.requestFocusInWindow();
        }
    }
    public void newC() {
        fi.setText("");
        li.setText("");
        pni.setText("");
        ai.setText("");
        cl.clearSelection();
        cl.setListData(cla.toArray());
    }
    public void saveC() {
        cla.get(cl.getSelectedIndex()).setl(li.getText());
        cla.get(cl.getSelectedIndex()).setf(fi.getText());
        cla.get(cl.getSelectedIndex()).setp(pni.getText());
        cla.get(cl.getSelectedIndex()).seta(ai.getText());
        cl.clearSelection();
        fi.setText("");
        li.setText("");
        pni.setText("");
        ai.setText("");
        sc.setVisible(false);
        dc.setVisible(false);
        s.setVisible(true);
        n.setVisible(true);
        setAlphabetical();
        cl.setListData(cla.toArray());
        write(true);
        r = true;
    }
    public void deleteC() {
        if(cla.size() == 1)  {
            cl.clearSelection();
            cla.clear();
            cl.clearSelection();
            fi.setText("");
            li.setText("");
            pni.setText("");
            ai.setText("");
            sc.setVisible(false);
            dc.setVisible(false);
            s.setVisible(true);
            n.setVisible(true);
            setAlphabetical();
            cl.setListData(cla.toArray());
            write(true);
            r = true;
        }
        else {
            cla.remove(cl.getSelectedIndex());
            fi.setText("");
            li.setText("");
            pni.setText("");
            ai.setText("");
            cl.clearSelection();
            sc.setVisible(false);
            dc.setVisible(false);
            s.setVisible(true);
            n.setVisible(true);
            setAlphabetical();
            cl.setListData(cla.toArray());
            write(true);
            r = true;
        }
    }
    public void hn() {
        if(r) {
            s.setVisible(false);
            n.setVisible(false);
            sc.setVisible(true);
            dc.setVisible(true);
            c.setVisible(true);
            r = false;
            fi.setText(cla.get(cl.getSelectedIndex()).getf());
            li.setText(cla.get(cl.getSelectedIndex()).getl());
            pni.setText(cla.get(cl.getSelectedIndex()).getp());
            ai.setText(cla.get(cl.getSelectedIndex()).geta());
        }
    }
    public void c() {
        RoloContact a = cla.get(o);
        if(!pns.contains(cla.get(o).getp().trim())) {
            talk = true;
            tt.tts(cla.get(o).getf() + " " + cla.get(o).getl() + " is not online at the moment.");
            talk = false;
            return;
        }
        talk = true;
        tt.tts("Chat with " + cla.get(o).getf() + " " + cla.get(o).getl());
        talk = false;
        cF = new ChatFrame("Chat with " + cla.get(o).getf() + " " + cla.get(o).getl(), 900, 750, pw);
        setVisible(false);
    }
    public void setAlphabetical() {
        ArrayList<String> arr = new ArrayList<>();
        for(int i = 0;i < cla.size();i++) {
            arr.add(cla.get(i).getl());
        }
        Collections.sort(arr);
        ArrayList<RoloContact> a = new ArrayList<>();
        for(int i = 0;i < arr.size();i++) {
            for (int k = 0; k < arr.size(); k++) {
                if (cla.get(k).getl().equals(arr.get(i))) {
                    a.add(cla.get(k));
                    k = arr.size();
                }
            }
        }
        cla = a;
    }
    public void write(boolean b) {
        if(b) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(uC, false))) {
                for (int i = 0; i < cla.size(); i++) {
                    writer.write(cla.get(i).getl() + "," + cla.get(i).getf() + "," + cla.get(i).getp() + "," + cla.get(i).geta());
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(uC, false))) {
                for (int i = 0; i < cla.size(); i++) {
                    writer.write("");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void connection() throws IOException {
        while(true) {
            lm = "Enter the I P address of the server your chat is running on";
            tt.tts(lm);
            String add = JOptionPane.showInputDialog(this, "Server IP: ");
            if(add == null) continue;
            soc = new Socket(add.trim(), 8001);
            input = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            pw = new PrintWriter(soc.getOutputStream(), true);
            String sadd = input.readLine();
            if(!"PHONENUMBER".equals(sadd)) {
                talk = true;
                lm = "Phone Number already taken";
                tt.tts(lm);
                talk = false;
                throw new IOException("Server Bad Error");
            }
            while(true) {
                lm = "Enter your Phone Number";
                tt.tts(lm);
                String un = JOptionPane.showInputDialog(this, "Phone Number");
                if(un == null) continue;
                pw.println(un.trim());
                p = un;
                String r = input.readLine();
                if("OK".contentEquals(r)) {
                    break;
                }
                else if("TAKEN".equals(r)) {
                    talk = true;
                    lm = "Error entering the server";
                    tt.tts(lm);
                    talk = false;
                }
            }
            break;
        }
        listen();
    }
    private void listen() {
        new Thread(() -> {
            try {
                String l;
                while((l = input.readLine()) != null) {
                    String msg = l;
                    if(l.startsWith("Users:")) {
                        String[] u = msg.substring(6).split(",");
                        pns.clear();
                        for(String user : u) {
                            pns.add(user);
                        }
                        SwingUtilities.invokeLater(() -> {
                            for(String user : u) {
                                if(user.equals(p)) {
                                    uC = new File(user + " contacts.txt");
                                    try {
                                        if (!uC.exists()) {
                                            uC.createNewFile();
                                        }
                                        lC();
                                    }
                                    catch(IOException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                            }
                        });
                    }
                    else {
                        if(cF != null) {
                            cF.sendMsg(msg);
                        }
                    }
                }
            }
            catch(IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public static void main(String[] args) {
        new RoloFrame("Contacts List", 900, 750);
    }
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_F1) {
            rc();
        }
        else if(e.getKeyCode() == KeyEvent.VK_F3) {
            talk = false;
            if(timer != null) {
                timer.stop();
            }
            c();
        }
        else if(e.getKeyCode() == KeyEvent.VK_F2) {
            if(timer != null) {
                timer.stop();
            }
            talk = true;
            tt.tts(lm);
        }
        else if(e.getKeyCode() == KeyEvent.VK_F4) {
            dispose();
        }
    }
    public void keyReleased(KeyEvent e) {

    }
    public void keyTyped(KeyEvent e) {

    }
}