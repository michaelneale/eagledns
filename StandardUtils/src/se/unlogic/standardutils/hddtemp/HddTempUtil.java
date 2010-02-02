package se.unlogic.standardutils.hddtemp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import se.unlogic.standardutils.net.SocketUtils;
import se.unlogic.standardutils.numbers.NumberUtils;

public class HddTempUtil {

	private static final String delims = "[|]+";

	public static ArrayList<Drive> getHddTemp(String host, int port, int timeout) throws IOException {

		Socket socket = null;

		try {
			socket = SocketUtils.getSocket(host, port, timeout);

			BufferedReader bfrRd = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			int waitCount = 0;
			
			while(!bfrRd.ready()){
				
				if(waitCount >= timeout){
					
					throw new SocketTimeoutException("No HDD temp response received");
				}
				
				try {
					waitCount += 200;
					Thread.sleep(200);
				} catch (InterruptedException e) {}
			}
			
			String response = new String(bfrRd.readLine());

			ArrayList<Drive> lRet = new ArrayList<Drive>();

			String[] tokens = response.split(delims);

			int temp = 0;
			String type = null;
			String device = null;

			boolean bDev = false;
			boolean bType = false;
			boolean bTemp = false;

			for (int i = 0; i < tokens.length; i++) {
				if (i > 0) {
					if (bDev == false) {
						device = new String(tokens[i]);
						bDev = true;
					} else if (bType == false) {
						type = new String(tokens[i]);
						bType = true;
					} else if (bTemp == false) {
						temp = NumberUtils.toInt(tokens[i]);
						bTemp = true;
					} else {
						lRet.add(new Drive(temp, type, device));
						bDev = false;
						bType = false;
						bTemp = false;
					}
				}
			}
			return lRet;

		} finally {

			if (socket != null) {
				socket.close();
			}
		}
	}
}
