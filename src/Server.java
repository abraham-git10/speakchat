import java.io.*;
import java.net.*;
import java.util.*;
public class Server {
    private static List<String> users = Collections.synchronizedList(new ArrayList<>());
    private static List<ClientsMain> cM = Collections.synchronizedList(new ArrayList<ClientsMain>());
    private static TextToSpeech tt = new TextToSpeech();
    public static void main(String[] args) {
        try(ServerSocket s = new ServerSocket(8001, 50, InetAddress.getByName("0.0.0.0"))) {
            while (true) {
                Socket cS = s.accept();
                ClientsMain main = new ClientsMain(cS);
                new Thread(main).start();
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }
    static void update() {
        StringBuilder uL = new StringBuilder("Users:");
        synchronized (users) {
            for(String n : users) {
                uL.append(n).append(",");
            }
        }
        synchronized (cM) {
            for(ClientsMain m : cM) {
                m.pw.println(uL.toString());
            }
        }
    }
    static class ClientsMain implements Runnable {
        private Socket s;
        private PrintWriter pw;
        private String un;
        ClientsMain(Socket s) {
            this.s = s;
        }
        public void run() {
            try(
                    BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
            ) {
                pw = new PrintWriter(s.getOutputStream(), true);
                pw.println("PHONENUMBER");
                while(true) {
                    un = input.readLine();
                    if(un == null) return;
                    synchronized (users) {
                        if(!users.contains(un)) {
                            users.add(un);
                            break;
                        }
                        else {
                            pw.println("TAKEN");
                        }
                    }
                }
                pw.println("OK");
                cM.add(this);
                synchronized (cM) {
                    for(ClientsMain m : cM) {
                        m.pw.println(un + " has joined the conversation.");
                    }
                }
                update();
                String m = "";
                while ((m = input.readLine()) != null) {
                    if(m.equalsIgnoreCase("EXIT")) {
                        break;
                    }
                    synchronized (cM) {
                        for(ClientsMain y : cM) {
                            y.pw.println(un + ": " + m);
                        }
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    if(un != null) {
                        users.remove(un);
                        cM.remove(this);
                        synchronized (cM) {
                            for(ClientsMain y : cM) {
                                y.pw.println(un + " has left the conversation.");
                                tt.tts(un + " has left the conversation");
                                update();
                            }
                            s.close();
                        }
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}