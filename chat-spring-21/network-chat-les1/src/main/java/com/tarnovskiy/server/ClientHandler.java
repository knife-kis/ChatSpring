package com.tarnovskiy.server;

import com.tarnovskiy.server.DB.AuthService;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler {
    private final String PATH = "C:\\Users\\Maks-Oks\\Desktop\\geekbrains\\Lesson02\\logChat.txt";
    private final String CENZYRA = "C:\\Users\\Maks-Oks\\Desktop\\geekbrains\\Lesson02\\cenzyra.txt";
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;
    private ServerMain server;
    private String nick;
    private List<String> blackList;
    private RandomAccessFile fileHandler ;

    public ClientHandler(ServerMain server, Socket socket) {
        try {
            this.socket = socket;
            this.server = server;
            this.in = new DataInputStream(socket.getInputStream());
            this.out = new DataOutputStream(socket.getOutputStream());
            this.blackList = new ArrayList<>();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    ClientHandler.this.authorization();
                    ClientHandler.this.readClient();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void authorization() {
        while (true) {
            try {
                String str = in.readUTF();
                String[] tokes = str.split(" ");
                String nickName = AuthService.getnickByLoginAndPass(tokes[1], tokes[2]);
                if (server.searchName(nickName)) {
                    sendMsg("Пользователь авторизован");
                } else if (str.startsWith("/auth")) {
                    if (nickName != null) {
                        sendMsg("/authok");
                        nick = nickName;
                        server.subscribe(this);
                        readlog100();
                        break;
                    } else {
                        sendMsg("Неверный логин/пароль");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readlog100() {
        StringBuilder sb = new StringBuilder();
        int line = 0;
        try {
            fileHandler  = new RandomAccessFile(PATH, "rw");
            long fileLength = fileHandler.length() - 1;
            for (long filePointer = fileLength; filePointer != -1 ; filePointer--) {
                fileHandler.seek(filePointer);
                int readByte = fileHandler.readByte();
                if (readByte == 0xA) {
                    if (filePointer < fileLength){
                        line++;
                    }
                } else if (readByte == 0xD) {
                    if (filePointer < fileLength - 1){
                        line++;
                    }
                }
                if (line >= 100){
                    break;
                }

                sb.append((char) readByte);
            }
            String log100 = sb.reverse().toString();
            server.broadcastMsg(this, log100);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                fileHandler.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void readClient() {
        try {
            while (true) {
                String str = in.readUTF();
                if (str.startsWith("/")) {
                    clearingChat(str);
                    cloasedServer(str);
                    sendPrivateMsg(str);
                    addBlackList(str);
                } else {
                    String s = cenzyra(str);
                    server.broadcastMsg(this, nick + ": " + s);
                    saveLog(nick + ": " + s);
                    System.out.println("Client: " + s);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeInAndOutAndSocket();
            server.deleteClient(this);
        }
    }

    private void clearingChat(String str) {
        if (str.startsWith("/clear")) {
            try {
                out.writeUTF("/clear");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addBlackList(String str) {
        if (str.startsWith("/blacklist ")) {
            String[] blackNick = str.split(" ");
            blackList.add(blackNick[1]);
            sendMsg("Вы добавили пользователя  " + blackNick[1] + " в черный список!");
        }
    }

    private void sendPrivateMsg(String str) {
        if (str.startsWith("/w") || str.startsWith("/W")) {
            String[] tokes = str.split(" ", 3);
            String nickname = tokes[1];
            String msg = tokes[2];
            server.privatebroadcastMsg(this, msg, nickname);
        }
    }

    public void sendMsg(String str) {
        try {
            out.writeUTF(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String cenzyra(String str) {
        try {
            fileHandler = new RandomAccessFile(CENZYRA, "rw");
            StringBuilder sbCenzyra = new StringBuilder();
            StringBuilder sbChat = new StringBuilder();
            sbCenzyra.append(fileHandler.readLine());
            String[] cenzyraWord = sbCenzyra.toString().split(" ");
            String[] strBuf = str.split(" ");
            for (int j = 0; j < strBuf.length; j++) {
                for (int i = 0; i < cenzyraWord.length ; i++) {
                    if(cenzyraWord[i].equals(strBuf[j])) {
                        strBuf[j] = "***";
                    }
                }
                if (j == strBuf.length - 1)
                sbChat.append(strBuf[j]);
                else
                sbChat.append(strBuf[j] + " ");
            }
            str = sbChat.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    private void cloasedServer(String str) {
        if (str.equals("/end")) {
            try {
                out.writeUTF("/serverClosed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeInAndOutAndSocket() {

        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void saveLog(String log){

        try {
            fileHandler  = new RandomAccessFile(PATH, "rw");
            long size = fileHandler .length();
            fileHandler .seek(size);
            if(size != 0)
                fileHandler .writeBytes("\n" + log);
            else fileHandler .writeBytes(log);
                fileHandler .close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public String getNick() {
        return nick;
    }

    public List<String> getBlackList() {
        return blackList;
    }

    public boolean checkBlackList(String nick) {
        return blackList.contains(nick);
    }

}