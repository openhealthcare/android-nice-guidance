/*
 *    Copyright (C) 2013  Open Health Care, R.Jones, Dr. VJ Joshi
 *    Additions Copyright (C) 2011 Neil McPhail
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.Boolean;

import uk.org.openhealthcare.NICEApp.ColourArray;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
//import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.inputmethod.InputMethodManager;
import android.os.AsyncTask;

public class NICEApp extends ListActivity {

	private static final int PREFERENCES_GROUP_ID = 0;
	private static final int SHARE_ID = 0;
	private static final int GETALL_ID = 1;
	private static final int FEEDBACK_ID = 2;
	private static final int SEARCH_ID = 3;
	private static final int RELOAD_ID = 4;
	private static final int HELP_ID = 5;
	private static final int ABOUT_ID = 6;
	private static boolean downloadLock = false;
	GuidelineData guidelines;
	boolean cached[];
	int numGuidelines;
	int lastOpened;
	int lastSuccessfulCheck;
	boolean firstrunlast;
	boolean haveConnectedWifi = false;
	boolean haveConnectedMobile = false;
	boolean section[];
	boolean keyboardup = false;
	String[] gnames;

	ArrayAdapter<String> adapter = null;
	ListView lv;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		GuidelineItem itemC = guidelines.Get((String) getListAdapter().getItem(
				info.position));
		menu.setHeaderTitle(itemC.category);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.layout.item_menu, menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(PREFERENCES_GROUP_ID, SHARE_ID, 0, "Share").setIcon(
				android.R.drawable.ic_menu_share);
		menu.add(PREFERENCES_GROUP_ID, GETALL_ID, 0, "Download All").setIcon(
				android.R.drawable.ic_menu_save);
		menu.add(PREFERENCES_GROUP_ID, FEEDBACK_ID, 0, "Update + Feedback Info")
				.setIcon(android.R.drawable.ic_menu_send);
		menu.add(PREFERENCES_GROUP_ID, SEARCH_ID, 0, "Search").setIcon(
				android.R.drawable.ic_menu_search);
		menu.add(PREFERENCES_GROUP_ID, RELOAD_ID, 0, "Last File").setIcon(
				android.R.drawable.ic_menu_rotate);
		menu.add(PREFERENCES_GROUP_ID, ABOUT_ID, 0, "Help & About").setIcon(
				android.R.drawable.ic_menu_info_details);

		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case SHARE_ID:
			LayoutInflater inflater = getLayoutInflater();
			View layout = inflater.inflate(R.layout.toast_layout,
					(ViewGroup) findViewById(R.id.toast_layout_root));

			ImageView image = (ImageView) layout.findViewById(R.id.image);
			image.setImageResource(R.drawable.qrcode);

			Toast toast = new Toast(getApplicationContext());
			toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
			toast.setDuration(Toast.LENGTH_LONG);
			toast.setView(layout);
			toast.show();

			return true;
		case HELP_ID:
			Toast.makeText(
					getApplicationContext(),
					"Cached items are in bold.\nLast opened file is highlighted.\n\nMake sure you have a PDF Reader installed.",
					Toast.LENGTH_LONG).show();
			return true;
		case FEEDBACK_ID:
			Toast.makeText(getApplicationContext(),
					"http://openhealthcare.org.uk\n\nCome say hello :)",
					Toast.LENGTH_LONG).show();

			return true;
		case ABOUT_ID:
			Toast.makeText(
					getApplicationContext(),
					"Version 1.93\n-------------\n\nLead Developers:\nDr VJ Joshi & Ross Jones",
					Toast.LENGTH_LONG).show();
			return true;

		case GETALL_ID:

			StatFs stat = new StatFs(Environment.getExternalStorageDirectory()
					.getPath());
			double sdAvailSize = (double) stat.getAvailableBlocks()
					* (double) stat.getBlockSize();
			// Check for space first ***ToDo****

			if (sdAvailSize < 25000000) {
				Toast.makeText(getApplicationContext(),
						"Not enough space on SDCard", Toast.LENGTH_LONG).show();
			} else {

				if (isNetworkAvailable()) {
					if (haveConnectedWifi) {
						AlertDialog ad = new AlertDialog.Builder(this).create();
						// ad.setCancelable(false); // This blocks the 'BACK'
						// button
						ad.setTitle("This could be slow...");
						ad.setMessage("Phone will download all missing files.  Please let it do its thing\n\nDownload can take approx 3 mins over WiFi (25Mb)\n\nPress BACK to back out");

						ad.setButton("Go",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										final Toast ShortToast = Toast
												.makeText(
														getApplicationContext(),
														"Starting downloads",
														Toast.LENGTH_SHORT);

										Timer timer = new Timer();
										TimerTask task = new TimerTask() {

											@Override
											public void run() {
												// make sure to cancel the Toast
												// in UI thread
												runOnUiThread(new Runnable() {

													@Override
													public void run() {
														ShortToast.cancel();
													}
												});
											}
										};

										ShortToast.show();
										timer.schedule(task, 500);

										new AsyncDownload().execute(guidelines
												.GetKeys());
										dialog.dismiss();

									}
								});
						ad.show();
					} else {
						Toast.makeText(getApplicationContext(),
								"Inadvisable unless over a WiFi connection",
								Toast.LENGTH_LONG).show();
					}
				} else {
					Toast.makeText(getApplicationContext(),
							"No Network Connectivity", Toast.LENGTH_LONG)
							.show();
				}
			}
			return true;

		case SEARCH_ID:
			closeOptionsMenu();

			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				public void run() {
					onSearchRequested();
				}
			}, 1000);

			return true;

		case RELOAD_ID:
			Object item1 = getListAdapter().getItem(lastOpened);
			String key = (String) item1;
			new AsyncDownload().execute(key);
			return true;
		}

		return false;
	}

	public boolean onSearchRequested() {

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		keyboardup = true;

		return false; // don't go ahead and show the search box
	}

	public void onWindowFocusChanged(boolean hasFocus) {

		if (keyboardup) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
			keyboardup = false;
		}

	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		SharedPreferences settings = getPreferences(0);

		firstrunlast = settings.getBoolean("firstrunlast", true);
		lastSuccessfulCheck = settings.getInt("lastSuccessfulCheck", 0);

		String folderString = pathToStorage(null);
		File folder = new File(folderString);
		if (!folder.exists()) {
			folder.mkdir();
		}

		String targetFile = pathToStorage("xml/guidelines.xml");
		boolean exists = (new File(targetFile)).exists();
		if (exists) {
			// do nothing
		} else {
			// if (firstrunlast){
			Toast.makeText(getApplicationContext(),
					"First run\nInitialising...", Toast.LENGTH_LONG).show();
			CopyAssets("");
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
					Uri.parse("file://"
							+ Environment.getExternalStorageDirectory())));
			firstrunlast = false;
			// }
		}

		try {
			guidelines = new GuidelineData(this);
		} catch (Exception elocal) {
			Toast.makeText(getApplicationContext(),
					"Failed to load guideline list", Toast.LENGTH_LONG).show();
		}

		Object[] c = guidelines.GetKeys();
		Arrays.sort(c);

		numGuidelines = c.length;

		cached = new boolean[numGuidelines];
		section = new boolean[numGuidelines];
		String lastLetter = "";
		int count = numGuidelines;
		for (int i = 0; i < count; i++) {
			cached[i] = settings.getBoolean(Integer.toString(i), false);
			section[i] = true;
			GuidelineItem item = guidelines.GetLoc(i);
			String s = item.name.substring(0, 1);
			if (lastLetter.equals(s)) {
				section[i] = false;
			}
			lastLetter = s;
		}
		lastOpened = settings.getInt("last", -1);

		new CheckExists().execute(guidelines.GetKeys());
		gnames = (String[]) c;
		final ColourArray arrad = new ColourArray(this, gnames);
		setListAdapter(arrad);

		lv = getListView();
		lv.setFastScrollEnabled(true);
		lv.setTextFilterEnabled(true);
		registerForContextMenu(getListView());

		// handleIntent(getIntent());

		lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				GuidelineItem itemC = guidelines.Get((String) getListAdapter()
						.getItem(position));

				Object item = getListAdapter().getItem(position);
				String key = (String) item;
				new AsyncDownload().execute(key);
				if (cached[position]) {
					Toast.makeText(getApplicationContext(), "Accessing",
							Toast.LENGTH_SHORT).show();
				}
				;
				if (isNetworkAvailable()) {
					GuidelineItem item0 = guidelines.Get((String) item);
					item0.cached = true;
					lastOpened = position;
					lv.invalidateViews();
				}

			}

			public boolean onContextItemSelected(MenuItem item) {
				AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
						.getMenuInfo();

				switch (item.getItemId()) {
				case R.id.restrict:
					GuidelineItem itemC = guidelines
							.Get((String) getListAdapter().getItem(
									info.position));
					Toast.makeText(getApplicationContext(), itemC.category,
							Toast.LENGTH_LONG).show();

					int count = numGuidelines;
					for (int i = 0; i < count; i++) {
						GuidelineItem itemX = guidelines
								.Get((String) getListAdapter().getItem(i));
						if (itemX.category != itemC.category) {
							arrad.hide(i);
						}

					}
					return true;
				}

				switch (item.getItemId()) {
				case R.id.release:
					GuidelineItem itemC = guidelines
							.Get((String) getListAdapter().getItem(
									info.position));
					Toast.makeText(getApplicationContext(), itemC.subcategory,
							Toast.LENGTH_LONG).show();
					return true;
				}

				return false;
			}
		});

	}

	@Override
	protected void onStop() {
		super.onStop();

		SharedPreferences settings = getPreferences(0);
		SharedPreferences.Editor editor = settings.edit();
		int count = numGuidelines;
		for (int i = 0; i < count; i++) {
			editor.putBoolean(Integer.toString(i), cached[i]);
		}
		editor.putInt("last", lastOpened);
		editor.putInt("last", lastSuccessfulCheck);
		editor.putBoolean("firstrunlast", firstrunlast);
		editor.commit();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);
		// handleIntent(intent);
	}

	public String MD5_Hash(String s) {
		MessageDigest m = null;

		try {
			m = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		m.update(s.getBytes(), 0, s.length());
		String hash = new BigInteger(1, m.digest()).toString(16);
		return hash;
	}

	public String pathToStorage(String filename) {
		File sdcard = Environment.getExternalStorageDirectory();
		if (filename != null)
			return sdcard.getAbsolutePath() + File.separator + "nice_guidance"
					+ File.separator + filename;
		return sdcard.getAbsolutePath() + File.separator + "nice_guidance"
				+ File.separator;
	}

	public boolean download(String guideline) {
		// What is public stays public
		AsyncTask myDownload = new AsyncDownload().execute(guideline);
		try {
			Boolean success = (Boolean) myDownload.get();
			return success.booleanValue();
		} catch (InterruptedException e) {
			return false;
		} catch (java.util.concurrent.ExecutionException f) {
			return false;
		}

	}

	private class AsyncDownload extends AsyncTask<String, String, Boolean> {

		protected Boolean doInBackground(String... guidelinelist) {
			try {
				int count = guidelinelist.length;
				Boolean singlesuccess = Boolean.FALSE; // if called on a single
														// file the pdf viewer
														// may be opened
				for (int i = 0; i < count; i++) {
					GuidelineItem item = guidelines.Get(guidelinelist[i]);
					String url = item.url;
					String hash = MD5_Hash(url);
					String targetFile = pathToStorage(hash + ".pdf");
					File file = new File(targetFile);
					if (!file.exists()) {
						if (isNetworkAvailable()) {
							if (downloadLock) {
								publishProgress("Please wait for previous files to download");
								return Boolean.FALSE;
							}
							downloadLock = true;
							if (count == 1) {
								publishProgress("Downloading\n"
										+ guidelinelist[i]);
							} else {
								publishProgress("Download Progress:\n"
										+ guidelinelist[i]);
							}

							Download p = new Download();
							try {
								p.DownloadFrom(url, targetFile);
								singlesuccess = Boolean.TRUE;
								if (!haveConnectedWifi)
									publishProgress("Downloaded successfully");

							} catch (Exception exc) {
								publishProgress("Failed to download the PDF "
										+ exc.toString());
							}
							downloadLock = false;
						} else {
							publishProgress("File not cached\nNo Network Connectivity");
						}
					} else {

						singlesuccess = Boolean.TRUE;
					}
				}
				if (count == 1 && singlesuccess && !downloadLock) {
					GuidelineItem item = guidelines.Get(guidelinelist[0]);
					String url = item.url;
					String hash = MD5_Hash(url);
					String targetFile = pathToStorage(hash + ".pdf");
					File file = new File(targetFile);
					Uri path = Uri.fromFile(file);
					Intent intent = new Intent(Intent.ACTION_VIEW);
					intent.setDataAndType(path, "application/pdf");
					intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

					try {
						startActivity(intent);
					} catch (ActivityNotFoundException e) {
						publishProgress("No application available to view PDF files");
						// Can't do this in a thread.
					}
				}
				new CheckExists().execute(guidelines.GetKeys());
				if (count == 1)
					return singlesuccess;
				return Boolean.TRUE;
			} catch (Exception eee) {
				/*
				 * Toast.makeText(getApplicationContext(), eee.toString(),
				 * Toast.LENGTH_SHORT).show();
				 */
				return false;
			}
		}

		protected void onProgressUpdate(String... progress) {
			final Toast ShortToast = Toast.makeText(getApplicationContext(),
					progress[0], Toast.LENGTH_SHORT);

			Timer timer = new Timer();
			TimerTask task = new TimerTask() {

				@Override
				public void run() {
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							ShortToast.cancel();
						}
					});
				}
			};

			ShortToast.show();
			timer.schedule(task, 30);

		}
	}

	private class CheckExists extends AsyncTask<String, String, Boolean> {
		protected Boolean doInBackground(String... guidelinelist) {

			int count = guidelinelist.length;
			for (int i = 0; i < count; i++) {
				GuidelineItem item = guidelines.Get(guidelinelist[i]);
				String url = item.url;
				String hash = MD5_Hash(url);
				String targetFile = pathToStorage(hash + ".pdf");
				boolean exists = (new File(targetFile)).exists();
				if (exists) {
					cached[i] = true;
					item.cached = true;
				} else {
					cached[i] = false;
					item.cached = false;
				}
			}
			publishProgress("Done");
			return true;
		}

		protected void onProgressUpdate(String... progress) {
			lv.invalidateViews();
		}
	}

	private static class FilesViewHolder {
		public TextView separator;
		public ImageView imageView;
		public ImageView imageView2;
		public TextView textView;
		public TextView subtitleView;
	}

	public class ColourArray extends ArrayAdapter<String> implements
			SectionIndexer {

		HashMap<String, Integer> alphaIndexer;
		String[] sections;
		String[] items = null;
		boolean[] hidden = null;

		private final Activity context;
		public final String[] names;

		public void hide(int position) {
			hidden[getRealPosition(position)] = true;
			notifyDataSetChanged();
			notifyDataSetInvalidated();
		}

		public void unHide(int position) {
			hidden[getRealPosition(position)] = false;
			notifyDataSetChanged();
			notifyDataSetInvalidated();
		}

		private int getRealPosition(int position) {
			int hElements = getHiddenCountUpTo(position);
			int diff = 0;
			for (int i = 0; i < hElements; i++) {
				diff++;
				if (hidden[position + diff])
					i--;
			}
			return (position + diff);
		}

		private int getHiddenCount() {
			int count = 0;
			for (int i = 0; i < items.length; i++)
				if (hidden[i])
					count++;
			return count;
		}

		private int getHiddenCountUpTo(int location) {
			int count = 0;
			for (int i = 0; i <= location; i++) {
				if (hidden[i])
					count++;
			}
			return count;
		}

		@Override
		public int getCount() {
			return (items.length - getHiddenCount());
		}

		public ColourArray(Activity context, String[] names) {

			super(context, R.layout.list_item, names);
			this.context = context;
			this.names = names;

			items = names;
			hidden = new boolean[names.length];
			for (int i = 0; i < names.length; i++) {
				hidden[i] = false;
			}

			alphaIndexer = new HashMap<String, Integer>();
			int size = names.length;
			for (int x = 0; x < size; x++) {
				alphaIndexer.put(guidelines.GetLoc(x).name.substring(0, 1), x);
			}

			// create a list from the set to sort
			ArrayList<String> sectionList = new ArrayList<String>(
					alphaIndexer.keySet());
			Collections.sort(sectionList);

			sections = new String[sectionList.size()];

			sectionList.toArray(sections);
		}

		public View getView(int index, View convertView, ViewGroup parent) {

			int position = getRealPosition(index);
			if (convertView == null) {
				LayoutInflater vi = (LayoutInflater) getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = vi.inflate(R.layout.list_item, null);
			}

			FilesViewHolder holder = new FilesViewHolder();

			holder.textView = (TextView) convertView.findViewById(R.id.label);
			// holder.imageView = (ImageView) rowView.findViewById(R.id.icon);
			holder.imageView2 = (ImageView) convertView
					.findViewById(R.id.icon2);
			holder.subtitleView = (TextView) convertView
					.findViewById(R.id.subtitle);

			Object itemO = getListAdapter().getItem(position);
			GuidelineItem item = guidelines.Get((String) itemO);
			String code = item.code;
			String category = item.category;

			holder.textView.setText(item.name);
			holder.subtitleView
					.setText("NICE "
							+ code
							+ String.format(
									"%1$-"
											+ (52 - item.subcategory.length() - item.code
													.length()) + "s", " ")
							+ item.subcategory);

			// imageView.setImageResource(R.drawable.icon);
			// if (item.name.length()%2==0)
			// {imageView.setImageResource(R.drawable.fox);}

			holder.imageView2.setImageResource(R.drawable.id);// default image

			if (category.equals("Blood and immune system")) {
				holder.imageView2.setImageResource(R.drawable.id);
			}
			if (category.equals("Cancer")) {
				holder.imageView2.setImageResource(R.drawable.cancer);
			}
			if (category.equals("Cardiovascular")) {
				holder.imageView2.setImageResource(R.drawable.cardio);
			}
			if (category.equals("Central nervous system")) {
				holder.imageView2.setImageResource(R.drawable.neuro);
			}
			if (category.equals("Digestive system")) {
				holder.imageView2.setImageResource(R.drawable.gastro);
			}
			if (category.equals("Ear and nose")) {
				holder.imageView2.setImageResource(R.drawable.ear);
			}
			if (category.equals("Endocrine, nutritional and metabolic")) {
				holder.imageView2.setImageResource(R.drawable.endocrine);
			}
			if (category.equals("Eye")) {
				holder.imageView2.setImageResource(R.drawable.eye);
			}
			if (category.equals("Gynaecology, pregnancy and birth")) {
				holder.imageView2.setImageResource(R.drawable.gynae);
			}
			if (category.equals("Infectious diseases")) {
				holder.imageView2.setImageResource(R.drawable.id);
			}
			if (category.equals("Injuries, accidents and wounds")) {
				holder.imageView2.setImageResource(R.drawable.ed);
			}
			if (category.equals("Mental health and behavioural conditions")) {
				holder.imageView2.setImageResource(R.drawable.mental);
			}
			if (category.equals("Mouth and dental")) {
				holder.imageView2.setImageResource(R.drawable.mouth);
			}
			if (category.equals("Musculoskeletal")) {
				holder.imageView2.setImageResource(R.drawable.ms);
			}
			if (category.equals("Respiratory")) {
				holder.imageView2.setImageResource(R.drawable.endocrine);
			}
			if (category.equals("Skin")) {
				holder.imageView2.setImageResource(R.drawable.thermo);
			}
			if (category.equals("Urogenital")) {
				holder.imageView2.setImageResource(R.drawable.ug);
			}

			if (item.cached) {
				holder.textView.setTextColor(Color.rgb(255, 255, 255));
			} else {
				holder.textView.setTextColor(Color.rgb(171, 171, 191));
			}

			convertView.setTag(holder);
			return convertView;
		}

		public int getPositionForSection(int section) {
			if (section == 0)
				return 0;
			else
				return alphaIndexer.get(sections[section - 1]);
		}

		public int getSectionForPosition(int position) {
			return 1;
		}

		public Object[] getSections() {
			return sections;
		}

	}

	private boolean isNetworkAvailable() {

		haveConnectedWifi = false;
		haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.isConnected()) {
				if (ni.getTypeName().equalsIgnoreCase("WIFI"))
					haveConnectedWifi = true;
				if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
					haveConnectedMobile = true;
			}
		}
		return haveConnectedWifi || haveConnectedMobile;
	}

	private void CopyAssets(String path) {
		AssetManager assetManager = this.getAssets();
		String assets[] = null;
		try {
			Log.i("tag", "CopyAssets() " + path);
			assets = assetManager.list(path);
			if (assets.length == 0) {
				copyFile(path);
			} else {
				String fullPath = pathToStorage(path);
				Log.i("tag", "path=" + fullPath);
				File dir = new File(fullPath);
				if (!dir.exists() && !path.startsWith("images")
						&& !path.startsWith("sounds")
						&& !path.startsWith("webkit"))
					if (!dir.mkdirs())
						;
				Log.i("tag", "could not create dir " + fullPath);
				for (int i = 0; i < assets.length; ++i) {
					String p;
					if (path.equals(""))
						p = "";
					else
						p = path + "/";

					if (!path.startsWith("images")
							&& !path.startsWith("sounds")
							&& !path.startsWith("webkit"))
						CopyAssets(p + assets[i]);
				}
			}
		} catch (IOException ex) {
			Log.e("tag", "I/O Exception", ex);
		}
	}

	private void copyFile(String filename) {
		AssetManager assetManager = this.getAssets();

		InputStream in = null;
		OutputStream out = null;
		String newFileName = null;
		try {
			Log.i("tag", "copyFile() " + filename);
			in = assetManager.open(filename);
			newFileName = pathToStorage(filename);
			out = new FileOutputStream(newFileName);

			byte[] buffer = new byte[1024];
			int read;
			while ((read = in.read(buffer)) != -1) {
				out.write(buffer, 0, read);
			}
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;
		} catch (Exception e) {
			Log.e("tag", "Exception in copyFile() of " + newFileName);
			Log.e("tag", "Exception in copyFile() " + e.toString());
		}

	}

}
