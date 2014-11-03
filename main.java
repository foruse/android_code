package com.gbksoft.greethumb;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView.RecyclerListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.gbksoft.greethumb.R;
import com.gbksoft.kurchenko.database.dataSource;

public class HistoryActivity extends Activity {
	ListView lv;
	HistoryAdapter ha;
	dataSource db;
	int h, w;
	String[] img, title, res, text;
	ImageView im;
	ProgressBar pb1, pb2, pb3, pb4;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_history);
		lv = (ListView) findViewById(R.id.history_list);
		im = (ImageView) findViewById(R.id.history_img);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		w = dm.widthPixels;
		h = dm.heightPixels;
		pb1 = (ProgressBar) findViewById(R.id.hprogressBar1);
		pb2 = (ProgressBar) findViewById(R.id.hprogressBar2);
		pb3 = (ProgressBar) findViewById(R.id.hprogressBar3);
		pb4 = (ProgressBar) findViewById(R.id.hprogressBar4);

		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "Error! No SDCARD Found!", Toast.LENGTH_LONG)
					.show();
		} else {
			dataset();
		}
	}

	public void dataset() {
		try {

			File[] cou;
			File sdDir = getApplicationContext().getFilesDir();
			List<HashMap<String, String>> getdat = new ArrayList<HashMap<String, String>>();
			db = new dataSource(getApplicationContext());
			db.open();
			getdat = db.GetAllHistory();
			Log.d("NEW", "db.GetAllHistory() is " + db.GetAllHistory());
			if (getdat != null) {
				int sizes = getdat.size();
				img = new String[sizes];
				title = new String[sizes];
				text = new String[sizes];

				for (int g = 0; g < sizes; g++) {
					img[g] = getdat.get(g).get("img");
					text[g] = getdat.get(g).get("title");
					title[g] = getdat.get(g).get("text");

				}

				File f = new File(sdDir, "Android/data/greenthumb/images");
				cou = f.listFiles();
				if (getdat != null) {
					if (f.listFiles().length != text.length) {
						MyAsink as = new MyAsink();
						as.execute(img);
					} else {
						res = new String[cou.length];
						for (int i = 0; i < cou.length; i++) {
							res[i] = cou[i].getName();
						}
						ha = new HistoryAdapter(getApplicationContext(), title,
								res, text);
						lv.setAdapter(ha);
						pb1.setVisibility(View.GONE);
						pb2.setVisibility(View.GONE);
						pb3.setVisibility(View.GONE);
						pb4.setVisibility(View.GONE);
						lv.setRecyclerListener(rl);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			e.printStackTrace();
			MyAsink as = new MyAsink();
			as.execute(img);
		}
		db.close();
	}

	RecyclerListener rl = new RecyclerListener() {

		@Override
		public void onMovedToScrapHeap(View view) {

			final ImageView imageView = (ImageView) view
					.findViewById(R.id.history_img);
			imageView.setImageBitmap(null);
		}
	};

	public File getCacheFolder(Context context) {
		File cacheDir = null;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			cacheDir = new File(Environment.getExternalStorageDirectory(),
					"cachefolder");
			if (!cacheDir.isDirectory()) {
				cacheDir.mkdirs();
			}
		}

		if (!cacheDir.isDirectory()) {
			cacheDir = context.getCacheDir(); // get system cache folder
		}

		return cacheDir;
	}

	public class MyAsink extends AsyncTask<String, Void, Bitmap> {
		private void writeFile(Bitmap bmp, File f) {
			FileOutputStream out = null;

			try {
				out = new FileOutputStream(f);
				bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (out != null)
						out.close();
				} catch (Exception ex) {
				}
			}
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			File sdDir = getApplicationContext().getFilesDir();
			Bitmap mybm = null;
			File cacheDir;
			String filename = null;
			cacheDir = new File(sdDir, "Android/data/greenthumb/images");
			File f = null;
			cacheDir.mkdirs();
			int a = params.length;
			res = new String[a];
			for (int i = 0; i < a; i++) {
				try {
					filename = title[i] + ".jpeg";
					f = new File(cacheDir, filename);
					mybm = BitmapFactory.decodeFile(params[i]);
					Bitmap resized = Bitmap.createScaledBitmap(mybm, w / 2,
							h / 3, true);
					writeFile(resized, f);
					res[i] = filename;
				} catch (Exception e) {
					e.printStackTrace();
					Log.d("NEW", "" + e);
				} catch (OutOfMemoryError e) {
					mybm.recycle();
					Log.d("NEW", "" + e);
					e.printStackTrace();
				}

			}

			return null;
		}

		@Override
		protected void onPostExecute(Bitmap result) {

			try {
				ha = new HistoryAdapter(getApplicationContext(), title, res,
						text);
				lv.setAdapter(ha);
				pb1.setVisibility(View.GONE);
				pb2.setVisibility(View.GONE);
				pb3.setVisibility(View.GONE);
				pb4.setVisibility(View.GONE);
				lv.setRecyclerListener(rl);
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
	}
}

