package com.example.identificationring;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements CvCameraViewListener,
		OnClickListener {

	Mat mat_rgb;
	CameraBridgeViewBase camera;
	Button button_capture;
	EditText text;

	Button button_1;
	Button button_2;
	boolean vvm_found;

	private BaseLoaderCallback loader_callback = new BaseLoaderCallback(this) {
		@Override
		public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				camera.enableView();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};

	// @Override
	// public void onCameraViewStarted(int w, int h) {
	// // mRgba = new Mat(height, width, CvType.CV_8UC4);
	// // mDetector = new ColorBlobDetector();
	// // mSpectrum = new Mat();
	// // mBlobColorRgba = new Scalar(255);
	// // mBlobColorHsv = new Scalar(255);
	// // SPECTRUM_SIZE = new Size(200, 64);
	// // CONTOUR_COLOR = new Scalar(255, 0, 0, 255);
	// }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		setContentView(R.layout.activity_main);

		button_capture = (Button) this.findViewById(R.id.button1);
		button_capture.setOnClickListener(this);
		camera = (CameraBridgeViewBase) findViewById(R.id.image_manipulations_activity_surface_view);
		camera.setCvCameraViewListener(this);
		// camera.SetCaptureFormat(Highgui.CV_CAP_ANDROID_GREY_FRAME);

		button_1 = (Button) this.findViewById(R.id.button2);
		button_2 = (Button) this.findViewById(R.id.button3);
		button_1.setOnClickListener(this);
		button_2.setOnClickListener(this);

		// text = (EditText) this.findViewById(R.id.editText1);

	}

	@Override
	public void onResume() {
		super.onResume();
		OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this,
				loader_callback);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	// public void onCameraViewStarted(int width, int height) {
	// mat_rgb = new Mat(height, width, CvType.CV_8UC4);
	// }
	boolean is_capture = false;
	Mat mat_capturing;
	Mat mat_captured;
	Mat mat_gray;
	Handler invoker = new Handler();

	@Override
	public Mat onCameraFrame(Mat arg0) {
		if (mat_captured == null) {
			mat_captured = new Mat();
		}
		arg0.copyTo(mat_captured);
		
		if (is_capture) {
			if (mat_capturing == null) {
				mat_capturing = new Mat();
				Mat mat_threshold = new Mat();
				Core.absdiff(mat1, mat2, mat_threshold);

				Imgproc.cvtColor(mat_threshold, mat_threshold,
						Imgproc.COLOR_BGR2GRAY);

				Mat mat_double_value = new Mat();
				Imgproc.threshold(mat_threshold, mat_double_value, 70, 255,
						Imgproc.THRESH_BINARY);

				Mat mat_canny = new Mat();
				Imgproc.Canny(mat_double_value, mat_canny, 0, 150);

				final List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
				Imgproc.findContours(mat_canny, contours, mat_canny,
						Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

				for (int i = contours.size() - 1; i >= 0; i--) {
					MatOfPoint point = contours.get(i);
					double area = Imgproc.contourArea(point);

					if (Math.abs(area) <= 2) {
						contours.remove(i);
					} else {
						Point center = new Point();
						float[] radius = null;

						MatOfPoint2f f2 = new MatOfPoint2f(point.toArray());
						Imgproc.minEnclosingCircle(f2, center, radius);

						Core.circle(arg0, center, 10, new Scalar(255, 0, 0), 2);
						// point.convertTo(m, rtype)
					}
				}

				invoker.post(new Runnable() {
					public void run() {
						Toast.makeText(MainActivity.this,
								"Contours count is " + contours.size(),
								Toast.LENGTH_SHORT).show();
					}
				});
				// Imgproc.drawContours(arg0, contours, -1, new Scalar(255, 0,
				// 0,
				// 255));

				// mat_double_value.copyTo(mat_capturing);

				arg0.copyTo(mat_capturing);
				// Canny
				// Mat mat_canny = new Mat();
				// Imgproc.Canny(arg0, mat_canny, 80, 100);
				//
				// // Contours
				// List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
				// Mat mat_contours = new Mat();
				//
				// Imgproc.findContours(mat_canny, contours, mat_canny,
				// Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
				//
				// // Filter by area
				// for (int i = contours.size() - 1; i >= 0; i--) {
				// double area = Imgproc.contourArea(contours.get(i));
				// if (area > 10) {
				// contours.remove(i);
				// }
				// }
				//
				// // Draw contours
				// Imgproc.drawContours(arg0, contours, -1, new Scalar(0, 0, 0,
				// 255));
				//
				// arg0.copyTo(mat_capturing);

				// String data = "";
				//
				// final String d = data;

				// invoker.post(new Runnable() {
				// public void run() {
				// text.setText(d);
				// }
				// });

				// Imgproc.HoughCircles(mat_capturing, circles,
				// Imgproc.CV_HOUGH_GRADIENT, 1, 0.1, 200, 100, 0, 2000);
				//
				// for (int i = 0; i < circles.cols(); i++) {
				// double[] vec = circles.get(0, i);
				//
				// Point center = new Point(Math.round(vec[0]),
				// Math.round(vec[1]));
				// int radius = (int) Math.round(vec[2]);
				// // draw the circle center
				// Core.circle(mat_capturing, center, 3,
				// new Scalar(255,0 , 0), -1, 8, 0);
				// // draw the circle outline
				// Core.circle(mat_capturing, center, radius, new Scalar(255, 0,
				// 0), 3, 8, 0);
				// }
			}
			return mat_capturing;
		} else {
			return arg0;
		}

		// if(mat_capturing==null){
		// mat_capturing = new Mat(arg0.height(),arg0.width(),CvType.CV_8UC4);
		// Imgproc.Canny(arg0, mat_capturing, 80, 100);
		// }

		// Imgproc.cvtColor(mat_capturing, mat_capturing,
		// Imgproc.COLOR_GRAY2BGRA, 4);

		// return mat_capturing;
	}

	@Override
	public void onCameraViewStarted(int arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCameraViewStopped() {
		// TODO Auto-generated method stub

	}

	Bitmap map1;
	Bitmap map2;

	Mat mat1;
	Mat mat2;

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.button1) {
			if (is_capture == true) {
				button_capture.setText("Capture");
				is_capture = false;
				mat_capturing = null;
				button_1.setVisibility(View.VISIBLE);
				button_2.setVisibility(View.VISIBLE);
			} else {
				button_capture.setText("Input");
				is_capture = true;
			}

			return;
		}

		if (v.getId() == R.id.button2) {
			// mat1 = new Mat(mat_captured.width(), mat_captured.height(),
			// Highgui.CV_CAP_ANDROID_GREY_FRAME);
			// Imgproc.cvtColor(mat_captured, mat1,Imgproc.c);
			mat1 = new Mat();
			mat_captured.copyTo(mat1);
			button_1.setVisibility(View.GONE);
			return;
		}

		if (v.getId() == R.id.button3) {
			mat2 = new Mat();
			mat_captured.copyTo(mat2);
			// mat2 = new Mat(mat_captured.width(), mat_captured.height(),
			// Highgui.CV_CAP_ANDROID_GREY_FRAME);
			// Imgproc.cvtColor(mat_captured, mat2, CvType.CV_8S);
			button_2.setVisibility(View.GONE);
			return;
		}

	}

	private void setImage(Bitmap map) {
		Mat m = new Mat();
		mat_captured.copyTo(m);
		map = Bitmap.createBitmap(m.width(), m.height(),
				Bitmap.Config.ARGB_8888);
		Utils.matToBitmap(m, map);

		map1 = map;
	}
}
