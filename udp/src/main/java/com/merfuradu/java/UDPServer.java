package com.merfuradu.java;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UDPServer {
    private static DatagramSocket datagramSocket;
    private ExecutorService executorService;

    public UDPServer(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    public UDPServer(int port, int threadPoolSize) throws SocketException {
        this.datagramSocket = new DatagramSocket(port);
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
    }

    public void start() {
        System.out.println("Server started. Waiting for incoming packets...");
        while (true) {
            try {
                byte[] buffer = new byte[1024];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(packet);
                ClientHandler clientHandler = new ClientHandler(datagramSocket, packet);
                executorService.execute(clientHandler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ClientHandler implements Runnable {
        private DatagramPacket packet;
        private DatagramSocket socket;

        public ClientHandler(DatagramSocket socket, DatagramPacket packet) {
            this.socket = socket;
            this.packet = packet;
        }

        @Override
        public void run() {
            try {
                InetAddress inetAddress = packet.getAddress();
                int port = packet.getPort();
                String messageFromClient = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Message from client: " + messageFromClient);
                DatagramPacket responsPacket = new DatagramPacket(packet.getData(), packet.getLength(), inetAddress,
                        port);
                socket.send(responsPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void receiveThenSend() {
        while (true) {
            try {
                byte[] buffer = new byte[1054];
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length);
                datagramSocket.receive(datagramPacket);
                new Thread(new ClientHandler(datagramSocket, datagramPacket)).start();
                // InetAddress inetAddress = datagramPacket.getAddress();
                // int port = datagramPacket.getPort();
                // String messageFromClient = new String(datagramPacket.getData(), 0,
                // datagramPacket.getLength());
                // System.out.println("Message from client: " + messageFromClient);
                // datagramPacket = new DatagramPacket(buffer, buffer.length, inetAddress,
                // port);
                // datagramSocket.send(datagramPacket);

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main(String[] args) {
        int port = 1234;
        int threadPoolSize = 10;
        try {
            UDPServer server = new UDPServer(port, threadPoolSize);
            server.start();
        } catch (SocketException socketException) {
            socketException.printStackTrace();
        }
    }// end main
}// end class
