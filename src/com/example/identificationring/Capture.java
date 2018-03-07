package com.example.identificationring;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class Capture extends Activity implements OnClickListener {

	Button button_back;
	static Bitmap map;
	ImageView image;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);
		button_back = (Button) this.findViewById(R.id.button1);
		button_back.setOnClickListener(this);
		image = (ImageView) this.findViewById(R.id.imageView1);
		if (image != null) {
			image.setImageBitmap(map);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_capture, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.button1) {
			map = null;
			this.finish();
		}

	}
}
