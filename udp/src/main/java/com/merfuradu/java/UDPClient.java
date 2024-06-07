package com.merfuradu.java;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

public class UDPClient {
    private DatagramSocket datagramSocket;
    private InetAddress inetAddress;
    byte buffer[];

    public UDPClient(DatagramSocket datagramSocket, InetAddress inetAddress) {
        this.datagramSocket = datagramSocket;
        this.inetAddress = inetAddress;
    }

    public void sendThenReceive() {

        Scanner scanner = new Scanner(System.in);
        while (true) {
            try {
                String messageToSend = scanner.nextLine();
                buffer = messageToSend.getBytes();
                DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, inetAddress, 1234);
                datagramSocket.send(datagramPacket);
                datagramSocket.receive(datagramPacket);
                String messageFromServer = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                System.out.println("The server got that you said: " + messageFromServer);
            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    public static void main(String[] args) {

        DatagramSocket datagramSocket = null;
        try {
            datagramSocket = new DatagramSocket();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        InetAddress inetAddress;
        try {
            inetAddress = InetAddress.getByName("localhost");
            if (datagramSocket != null) {
                UDPClient client = new UDPClient(datagramSocket, inetAddress);
                System.out.println("Send datagram packets to a server.");
                client.sendThenReceive();
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

    }
}
