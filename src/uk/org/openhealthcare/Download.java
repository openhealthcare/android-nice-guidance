/*
*    Copyright (C) 2011  Open Health Care, R.Jones, Dr. VJ Joshi
*
*    This program is free software: you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation, either version 3 of the License, or
*    (at your option) any later version.
*
*    This program is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*    You should have received a copy of the GNU General Public License
*    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package uk.org.openhealthcare;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class Download {
	
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