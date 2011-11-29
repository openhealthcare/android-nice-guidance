package uk.org.openhealthcare;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadPDF {
	
	private final int SOCKET_TIMEOUT = 30000;
	
	public void DownloadFrom( String url, String target) throws IOException {
		URL murl = new URL(url);
        URLConnection connection = murl.openConnection();
        byte[] buff = new byte[5 * 1024];
        int len;

        connection.setReadTimeout(SOCKET_TIMEOUT);
        connection.setConnectTimeout(SOCKET_TIMEOUT);

        InputStream is = connection.getInputStream();
        BufferedInputStream inStream  = new BufferedInputStream(is, 1024 * 5);
        FileOutputStream 	outStream = new FileOutputStream(target);

        while ((len = inStream.read(buff)) != -1) {
            outStream.write(buff,0,len);
        }

        outStream.flush();
        outStream.close();
        inStream.close();		
	}
}
