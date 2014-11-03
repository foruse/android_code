package com.gbksoft.greethumb;

import java.io.File;

import com.gbksoft.greethumb.R;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

public class HistoryAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] title, text, img;
	ImageView im;

	public HistoryAdapter(Context context, String[] title, String[] text,
			String[] img) {
		super(context, R.layout.history_lay, title);
		this.context = context;
		this.img = img;
		this.title = title;
		this.text = text;
	}

	/**
	 * Define the ViewHolder for adapter
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		// ---------------
		// View inflation and ViewHolder code
		// ---------------
		View rowView = inflater.inflate(R.layout.history_lay, parent, false);
		TextView tv = (TextView) rowView.findViewById(R.id.title_histor);
		im = (ImageView) rowView.findViewById(R.id.history_img);
		Button inf = (Button) rowView.findViewById(R.id.bt_inf_gist);
		try {
			if (this.title[position].toString().length() > 10) {
				tv.setTextSize(17);
			}

			else if (this.title[position].toString().length() > 15) {
				tv.setTextSize(12);
			} else if (this.title[position].toString().length() > 20) {
				tv.setTextSize(5);
			} else {
				tv.setTextSize(20);
			}

			tv.setText(this.title[position].toString());
			File sdDir = context.getFilesDir();
			File f = new File(sdDir, "Android/data/greenthumb/images/"
					+ text[position]);
			Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
			Drawable d = new BitmapDrawable(context.getResources(), bmp);

			im.setScaleType(ScaleType.CENTER_CROP);
			im.setImageDrawable(d);

		} catch (NullPointerException e1) {
			Log.d("NEW", "NullPointerException e1 get view" + e1);
		} catch (Exception e) {
			Log.d("NEW", "Exception e1 get view" + e);
		}
		// ---------------
		// Write listenear for buttons
		// ---------------
		inf.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String title1, image1, descr1, url1;
				try {
					String[] parts = img[position].split("TITLE");
					image1 = parts[0];
					String[] parts1 = parts[1].split("DESCR");
					title1 = parts1[0];
					String[] parts2 = parts1[1].split("URL");
					descr1 = parts2[0];
					url1 = parts2[1];

					Log.e("NEW", "My list is" + title1 + image1 + descr1 + url1);
					Intent intent_wiki = new Intent(context,
							ResultActivity.class);
					intent_wiki.putExtra("title", title1);
					intent_wiki.putExtra("img", image1);
					intent_wiki.putExtra("descr", descr1);
					intent_wiki.putExtra("url", url1);
					intent_wiki.putExtra("activ", "history");
					intent_wiki.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent_wiki);

				} catch (IndexOutOfBoundsException e) {
					Log.d("NEW", "IndexOutOfBoundsException  get view" + e);
					e.printStackTrace();
				} catch (NullPointerException e) {
					Log.d("NEW", "NullPointerException get view" + e);
				}

			}
		});
		return rowView;
	}

}
