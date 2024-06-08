package com.merfuradu.java;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;

public class ServerUDP {
    private DatagramSocket socket;

    public ServerUDP(DatagramSocket socket) {
        this.socket = socket;
    }

    public void receiveThenSend(DatagramSocket socket) {
        while (true) {
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                new Thread(new ClientHandler(packet, socket)).start();
                // socket.receive(packet);
                // InetAddress inetAddress = packet.getAddress();
                // int port = packet.getPort();
                // String messageFromClient = new String(packet.getData(), 0,
                // packet.getLength());
                // System.out.println("Message from you was: " + messageFromClient);
                // DatagramPacket packet2 = new DatagramPacket(buffer, buffer.length,
                // inetAddress, port);
                // socket.send(packet2);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public class ClientHandler implements Runnable {
        private DatagramPacket clientPacket;
        private DatagramSocket clientSocket;

        public ClientHandler(DatagramPacket clientPacket, DatagramSocket clientSocket) {
            this.clientPacket = clientPacket;
            this.clientSocket = clientSocket;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            String messageFromClient = new String(clientPacket.getData(), 0, clientPacket.getLength());
            buffer = messageFromClient.getBytes();
            System.out.println("Message from client: " + messageFromClient);
            DatagramPacket response = new DatagramPacket(buffer, buffer.length, clientPacket.getAddress(),
                    clientPacket.getPort());
            try {
                clientSocket.send(response);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws SocketException {

        DatagramSocket socket = new DatagramSocket(1234);
        ServerUDP server = new ServerUDP(socket);
        System.out.println("Server waiting for packets");

        while (true) {
            server.receiveThenSend(socket);
        }

    }
}
