package se.unlogic.standardutils.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

public class SocketUtils {

	public static Socket getSocket(String host, int port, int timeout) throws IOException{
		SocketAddress sockaddr = new InetSocketAddress(host, port);
		Socket socket = new Socket();
		socket.connect(sockaddr, timeout);

		return socket;
	}
}
