import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class server extends JFrame implements ActionListener {
	static ServerSocket server;
	static Socket conn;
	static Vector<Socket> Clients;
	JPanel panel;
	JTextField NewMsg;
	JTextArea ChatHistory;
	JButton Send, Start;
	DataInputStream dis;
	DataOutputStream dos;

	public server() throws UnknownHostException, IOException {

		panel = new JPanel();
		NewMsg = new JTextField();
		ChatHistory = new JTextArea();
		Send = new JButton("Send");
		Start = new JButton("Start");
		this.setSize(500, 500);
		this.setVisible(true);
		this.setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		panel.setLayout(null);
		this.add(panel);
		ChatHistory.setBounds(20, 20, 450, 360);
		panel.add(ChatHistory);
		NewMsg.setBounds(20, 400, 340, 30);
		panel.add(NewMsg);
		Send.setBounds(375, 385, 95, 30);
		Start.setBounds(375, 425, 95, 30);
		panel.add(Send);
		panel.add(Start);
		this.setTitle("Server");
		Start.addActionListener(this);
		Send.addActionListener(this);

		server = new ServerSocket(2000, 1, InetAddress.getByName("localhost"));
		Clients = new Vector<Socket>();
		ChatHistory.setText("Waiting for Client");
		while (true) {
			try {
				conn = server.accept();
				HandlerClient handleClient = new HandlerClient(conn);
				ChatHistory.setText(ChatHistory.getText() + '\n' + "New client Found" + conn);
			}
			catch (Exception e1) {
				ChatHistory.setText(ChatHistory.getText() + '\n'
						+ "Message sending fail:Network Error");
				try {
					Thread.sleep(3000);
					System.exit(0);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	class HandlerClient extends Thread {
		Socket ClientSocket;
		DataInputStream dis;
		DataOutputStream dos;
		HandlerClient(Socket client) throws IOException {
				ClientSocket = client;
				dis = new DataInputStream(ClientSocket.getInputStream());
				dos = new DataOutputStream(ClientSocket.getOutputStream());
				Clients.add(ClientSocket);
				start();
		}
		public void run() {
						try {
							while (true) {
								String string = dis.readUTF();
								ChatHistory.setText(ChatHistory.getText() + '\n' + "Client:" + string);
							}
						} 
						catch (IOException e) {
								e.printStackTrace();
						}
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if ((e.getSource() == Send) && (NewMsg.getText() != "")) {
			ChatHistory.setText(ChatHistory.getText() + '\n' + "ME:"
					+ NewMsg.getText());
			try {
				for (int i = 0; i < Clients.size(); i++) {
					Socket pSocket = (Socket) Clients.elementAt(i);
					dos = new DataOutputStream(pSocket.getOutputStream());
					dos.writeUTF(NewMsg.getText());
				}
			} catch (Exception e1) {
				try {
					Thread.sleep(3000);
					System.exit(0);
				} catch (InterruptedException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
			}
			NewMsg.setText("");
		}
	}
	public static void main(String[] args) throws UnknownHostException, IOException {
		new server();
	}
}