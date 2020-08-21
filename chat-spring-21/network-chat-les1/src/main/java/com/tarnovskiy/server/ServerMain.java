package com.tarnovskiy.server;

import org.apache.log4j.Logger;

import com.tarnovskiy.server.DB.AuthService;

import javax.sql.DataSource;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class ServerMain {
//    private DataSource dataSource;
    private Vector<ClientHandler> clients;
    private static final Logger log = Logger.getLogger(ServerMain.class);

    public ServerMain() {
        clients = new Vector<>();
        ServerSocket server = null;
        Socket socket = null;

        try {

            AuthService.connect();
            server = new ServerSocket(8189);
            System.out.println("Сервер запущен!");
            log.info("Сервер запущен!");


            while (true) {
                socket = server.accept();
                System.out.println("Клиент подключился ");
                new ClientHandler(this, socket);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSocketAndServer(socket, server);
            AuthService.disconnect();
        }
    }

    public void closeSocketAndServer(Socket socket, ServerSocket server) {
        try {
            socket.close();
            System.out.println("Сокет закрылся ");
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            server.close();
            System.out.println("Сервер закрылся ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(ClientHandler client) {
        clients.add(client);
        broadcastClientList();
    }

    public void deleteClient(ClientHandler client) {
        clients.remove(client);
        broadcastClientList();
    }

    private void broadcastClientList() {
        StringBuilder sb = new StringBuilder();
        sb.append("/clientlist ");
        for(ClientHandler o : clients){
            sb.append(o.getNick() + " ");
        }
        String out = sb.toString();
        for (ClientHandler o : clients){
            o.sendMsg(out);
            }
    }

    public void broadcastMsg(ClientHandler from, String msg) {
        for (ClientHandler o: clients) {
            if (!o.checkBlackList(from.getNick())){
                o.sendMsg(msg);
            }
        }
    }

    public void privatebroadcastMsg(ClientHandler from, String msg, String nickTo){///////////////////////////////////
        for (ClientHandler o : clients) {
            if (o.getNick().equals(nickTo)) {
                o.sendMsg("from " + from.getNick() + ": " + msg);
                from.sendMsg("to " + nickTo + ": " + msg);
                return;
            }
        }
        from.sendMsg("Клиент с ником " + nickTo + " не найден в чате");
    }

    public boolean searchName(String nick) {
        boolean check = false;
        for (ClientHandler o: clients) {
            if (o.getNick().equals(nick)){
                check = true;
            }
        }
        return check;
    }
}
