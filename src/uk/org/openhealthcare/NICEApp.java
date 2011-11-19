package uk.org.openhealthcare;

import java.io.File;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu; 


public class NICEApp extends ListActivity {

	private static final int PREFERENCES_GROUP_ID = 0;
	private static final int SETTINGS_ID = 0;
	private static final int HELP_ID = 1;
	private static final int FEEDBACK_ID = 2;
	private static final int ABOUT_ID = 3;
	private static final int GETALL_ID = 4;
	GuidelineData guidelines;
	ArrayAdapter<String> arrad;
	ArrayAdapter<String> adapter = null;

	
	@Override



public boolean onCreateOptionsMenu(Menu menu)
	{
	super.onCreateOptionsMenu(menu);

	menu.add(PREFERENCES_GROUP_ID, SETTINGS_ID, 0, "settings")
	.setIcon(android.R.drawable.ic_menu_preferences);
	menu.add(PREFERENCES_GROUP_ID, HELP_ID, 0, "help")
	.setIcon(android.R.drawable.ic_menu_help);
	menu.add(PREFERENCES_GROUP_ID, FEEDBACK_ID, 0, "feedback")
	.setIcon(android.R.drawable.ic_menu_send);
	menu.add(PREFERENCES_GROUP_ID, ABOUT_ID, 0, "about")
	.setIcon(android.R.drawable.ic_menu_info_details);
	menu.add(PREFERENCES_GROUP_ID, GETALL_ID, 0, "download all")
	.setIcon(android.R.drawable.ic_menu_save);

	return true;
	} 
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
	switch (item.getItemId()) {
	   case SETTINGS_ID: 
		   Toast.makeText(getApplicationContext(), 
	               "Coming soon...", 
	               Toast.LENGTH_LONG).show();
	   			return true;
	   case HELP_ID: Toast.makeText(getApplicationContext(), 
               "Choose an item.\nMake sure you have a PDF Reader installed.", 
               Toast.LENGTH_LONG).show();
				return true;
	   case FEEDBACK_ID: Toast.makeText(getApplicationContext(), 
               "http://openhealthcare.org.uk\n\nCome say hello :)", 
               Toast.LENGTH_LONG).show();
	   			return true;	
	   case ABOUT_ID: Toast.makeText(getApplicationContext(), 
               "Authors:\nRoss Jones / Dr VJ Joshi", 
               Toast.LENGTH_LONG).show();
				return true;
	   case GETALL_ID: 
		   
		    AlertDialog ad = new AlertDialog.Builder(this).create();  
		    //ad.setCancelable(false); // This blocks the 'BACK' button  
		    ad.setTitle("This will be SLOW");
		    ad.setMessage("Phone may appear to freeze\nPlease let it do its thing\n\nDownload will take approx 3 mins over WiFi (25Mb)");  
		    ad.setButton("Go", new DialogInterface.OnClickListener() {  
		        @Override  
		        public void onClick(DialogInterface dialog, int which) {  
		           for ( String s : guidelines.GetKeys() ) {
		               download(s);
		           }
/*		           download("Acutely ill patients in hospital");		   
		 		   download("Alcohol dependence and harmful alcohol use");
		 		   download("Alcohol-use disorders: physical complications");
		 		   download("Anaemia management in people with chronic kidney disease");
		 		   download("Antenatal and postnatal mental health");
		 		   download("Antenatal care");
		 		   download("Antisocial personality disorder");
		 		   download("Anxiety");
		 		   download("Atopic eczema in children");
		 		   download("Atrial fibrillation");
		 		   download("Attention deficit hyperactivity disorder (ADHD)");
		 		   download("Autism spectrum disorders in children and young people");
		 		   download("Bacterial meningitis and meningococcal septicaemia");
		 		   download("Barrett's oesophagus - ablative therapy");
		 		   download("Bipolar disorder");
		 		   download("Borderline personality disorder (BPD)");
		 		   download("Breast cancer (advanced)");
//		 		   download("Breast cancer (early & locally advanced)");
		 		   download("Caesarean section");
		 		   download("Chest pain of recent onset");
		 		   download("Chronic fatigue syndrome / Myalgic encephalomyelitis");
		 		   download("Chronic heart failure");
		 		   download("Chronic kidney disease");
		 		   download("Chronic obstructive pulmonary disease (updated)");
		 		   download("Coeliac disease");
		 		   download("Colonoscopic surveillance for prevention of colorectal cancerin people with ulcerative colitis, Crohn's disease adenomas");
		 		   download("Common mental health disorders");
		 		   download("Constipation in children and young people");
		 		   download("Critical illness rehabilitation");
		 		   download("Delirium");
		 		   download("Dementia");
		 		   download("Dental recall");
		 		   download("Depression in adults (update)");
		 		   download("Depression in children and young people");
		 		   download("Depression with a chronic physical health problem");
		 		   download("Diabetes in pregnancy");
		 		   download("Diabetic foot problems - inpatient management");
		 		   download("Diarrhoea and vomiting in children under 5");
		 		   download("Donor breast milk banks");
		 		   download("Drug misuse: opioid detoxification");
		 		   download("Drug misuse: psychosocial interventions");
		 		   download("Dyspepsia");
		 		   download("Eating disorders");
		 		   download("Epilepsy - Adults");
		 		   download("Epilepsy - Children");
		 		   download("Faecal incontinence");
		 		   download("Falls");
		 		   download("Familial breast cancer");
		 		   download("Familial hypercholesterolaemia");
		 		   download("Fertility");
		 		   download("Feverish illness in children");
		 		   download("Food allergy in children and young people");
		 		   download("Glaucoma");
		 		   download("Head injury");
		 		   download("Heavy menstrual bleeding");
		 		   download("Hip fracture");
		 		   download("Hypertension");
		 		   download("Hypertension in pregnancy");
		 		   download("Induction of labour");
		 		   download("Infection control");
		 		   download("Intrapartum care");
		 		   download("Irritable bowel syndrome");
		 		   download("Lipid modification");
		 		   download("Long-acting reversible contraception");
		 		   download("Low back pain");
		 		   download("Lower urinary tract symptoms");
		 		   download("Lung cancer");
		 		   download("MI: secondary prevention");
		 		   download("Medicines adherence");
		 		   download("Metastatic malignant disease of unknown primary origin");
		 		   download("Metastatic spinal cord compression");
		 		   download("Motor neurone disease - non-invasive ventilation");
		 		   download("Multiple pregnancy");
		 		   download("Multiple sclerosis");
		 		   download("Neonatal jaundice");
		 		   download("Neuropathic pain - pharmacological management");
		 		   download("Nocturnal enuresis - the management of bedwetting in children and young people");
		 		   download("Nutrition support in adults");
		 		   download("Obesity");
		 		   download("Obsessive compulsive disorder (OCD) and body dysmorphic disorder (BDD)");
		 		   download("Osteoarthritis");
		 		   download("Ovarian cancer");
		 		   download("Parkinson's disease");
		 		   download("Perioperative hypothermia (inadvertent)");
		 		   download("Peritoneal dialysis");
		 		   download("Post-traumatic stress disorder (PTSD)");
		 		   download("Postnatal care");
		 		   download("Pregnancy and complex social factors");
		 		   download("Preoperative tests");
		 		   download("Pressure relieving devices");
		 		   download("Pressure ulcer management");
		 		   download("Prophylaxis against infective endocarditis");
		 		   download("Prostate cancer");
		 		   download("Psychosis with coexisting substance misuse");
		 		   download("Referral for suspected cancer");
		 		   download("Respiratory tract infections");
		 		   download("Rheumatoid arthritis");
		 		   download("Schizophrenia (update)");
		 		   download("Sedation in children and young people");
		 		   download("Self-harm");
		 		   download("Stable angina");
		 		   download("Stroke");
		 		   download("Surgical management of OME");
		 		   download("Surgical site infection");
		 		   download("Transient loss of consciousness in adults and young people");
		 		   download("Tuberculosis");
		 		   download("Type 1 diabetes");
		 		   download("Type 2 Diabetes - newer agents (partial update of CG66)");
		 		   download("Type 2 diabetes - footcare");
		 		   download("Unstable angina and NSTEMI");
		 		   download("Urinary incontinence");
		 		   download("Urinary tract infection in children");
		 		   download("Venous thromboembolism - reducing the risk");
		 		   download("Violence");
		 		   download("When to suspect child maltreatment");
*/ 		   
				   dialog.dismiss();
		 		   
//		 		  AlertDialog ad2 = new AlertDialog.Builder(this).create();  
//				    //ad.setCancelable(false); // This blocks the 'BACK' button  
//				    ad2.setTitle("Done");
//				    ad2.setMessage("Success");  
//				    ad2.setButton("Thanks", new DialogInterface.OnClickListener() {  
//				        @Override  
//				        public void onClick(DialogInterface dialog, int which) {  
//				            dialog.dismiss();				      
//				        ad2.show(); }  
//				    }); 
		        }  
		    });  
		    ad.show();  
		   
		   return true;
			}
		   
	return false;
	}   

	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);
	  
	    Intent intent = getIntent();
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	      String query = intent.getStringExtra(SearchManager.QUERY);
	      doMySearch(query);
	    }

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
					
					download(key);
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

	private void doMySearch(String query) {
	
		
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
	
	public boolean download(String guideline) {
		String url = guidelines.Get(guideline);
		String hash  = MD5_Hash(url);

		String targetFile= pathToStorage( hash + ".pdf" );					
		File file = new File(targetFile);
		if ( ! file.exists() ) {
			DownloadPDF p = new DownloadPDF();
			try {
				p.DownloadFrom(url, targetFile);
                Toast.makeText(getApplicationContext(), 
                        "Downloaded "+ guideline+" successfully", 
                        Toast.LENGTH_SHORT).show();
			
			} catch ( Exception exc ){
                Toast.makeText(getApplicationContext(), 
                        "Failed to download the PDF " + exc.toString(), 
                        Toast.LENGTH_LONG).show();
                		return false;
			}}
		return true;	
	}
}
