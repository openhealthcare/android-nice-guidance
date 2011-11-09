package uk.org.openhealthcare;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class NICEApp extends ListActivity {

	GuidelineData guidelines;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);

	  try { 
		  guidelines = new GuidelineData(this);
	  } catch (Exception elocal) {
          Toast.makeText(getApplicationContext(), 
                  "Failed to load guideline list", 
                  Toast.LENGTH_LONG).show();		  
	  }
	  
	  String folderString = pathToStorage(null);
	  File folder = new File(folderString);
	  if ( ! folder.exists() ) {
		  folder.mkdir();
	  }
	  
	  Object[] c = guidelines.GetKeys();
	  Arrays.sort(c);
	  
	  setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, (String[])c));
	  
	  ListView lv = getListView();
	  lv.setTextFilterEnabled(true);

	  lv.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int arg2,
					long arg3) {

					String key = (String) ((TextView) view).getText();
					String url = guidelines.Get(key);
					String hash  = MD5_Hash(url);
					
					String targetFile= pathToStorage( hash + ".pdf" );					
					File file = new File(targetFile);
					if ( ! file.exists() ) {
                        Toast.makeText(getApplicationContext(), 
                                "Downloading the PDF", 
                                Toast.LENGTH_LONG).show();

    					DownloadPDF p = new DownloadPDF();
    					try {
    						p.DownloadFrom(url, targetFile);
	                        Toast.makeText(getApplicationContext(), 
	                                "Download complete", 
	                                Toast.LENGTH_SHORT).show();
    					
    					} catch ( Exception exc ){
                            Toast.makeText(getApplicationContext(), 
                                    "Failed to download the PDF " + exc.toString(), 
                                    Toast.LENGTH_LONG).show();
    					}
					}
					
					// Hash it and look for it on disk, if not on disk then download locally
					
                    Uri path = Uri.fromFile(file);
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(path, "application/pdf");
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    try {
                        startActivity(intent);
                    } 
                    catch (ActivityNotFoundException e) {
                        Toast.makeText(getApplicationContext(), 
                            "No Application Available to View PDF files", 
                            Toast.LENGTH_LONG).show();
                    }
			}		  
	  });
	}

	public String MD5_Hash(String s) { 
		  MessageDigest m = null;

		  try {
	            	m = MessageDigest.getInstance("MD5");
		  } catch (NoSuchAlgorithmException e) {
	            	e.printStackTrace();
		  }

	    m.update(s.getBytes(),0,s.length());
	    String hash = new BigInteger(1, m.digest()).toString(16);
	    return hash;
	  }


	public String pathToStorage(String filename) {
		File sdcard = Environment.getExternalStorageDirectory();
		if ( filename != null )
			return sdcard.getAbsolutePath() + File.separator+ "nice_guidance" + File.separator + filename;		
		return sdcard.getAbsolutePath() + File.separator+ "nice_guidance" + File.separator;		
	}
	
	
}
