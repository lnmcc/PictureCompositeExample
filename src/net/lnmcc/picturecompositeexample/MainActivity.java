package net.lnmcc.picturecompositeexample;

import net.lnmcc.picturecompositeexample.R.id;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnClickListener {

	static final int PICKED_ONE = 0;
	static final int PICKED_TWO = 1;
	
	boolean onePicked = false;
	boolean twoPicked = false;
	
	Button choosePicture1, choosePicture2;
	
	ImageView compositeImageView;
	ImageView imageViewP1;
	ImageView imageViewP2;
	Bitmap bmp1, bmp2;
	Canvas canvas;
	Paint paint;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		compositeImageView = (ImageView)findViewById(R.id.imageView01);
		imageViewP1 = (ImageView)findViewById(R.id.imageViewP1);
		imageViewP2 = (ImageView)findViewById(R.id.imageViewP2);
		choosePicture1 = (Button)findViewById(R.id.choosePicture1Button);
		choosePicture2 = (Button)findViewById(R.id.choosePicture2Button);
		choosePicture1.setOnClickListener(this);
		choosePicture2.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		
		int which = -1;
		if(v == choosePicture1) {
			which = PICKED_ONE;
		} else if(v == choosePicture2) {
			which = PICKED_TWO;
		}
		Intent choosePictureIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(choosePictureIntent, which);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK) {
			Uri imageFileUri = data.getData();
			if(requestCode == PICKED_ONE) {
				bmp1 = loadBitmap(imageFileUri);
				onePicked = true;
				imageViewP1.setImageBitmap(bmp1);
			} else if(requestCode == PICKED_TWO) {
				bmp2 = loadBitmap(imageFileUri);
				twoPicked = true;
				imageViewP2.setImageBitmap(bmp2);
			}
		}
		if(onePicked && twoPicked) {
			Bitmap drawingBitmap = Bitmap.createBitmap(bmp1.getWidth(), bmp1.getHeight(), bmp1.getConfig());
			Canvas canvas = new Canvas(drawingBitmap);
			Paint paint = new Paint();
			canvas.drawBitmap(bmp1, 0, 0, paint);
			paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.MULTIPLY));
			canvas.drawBitmap(bmp2, 0, 0, paint);
			compositeImageView.setImageBitmap(drawingBitmap);
		}
	}
	
	private Bitmap loadBitmap(Uri imageFileUri) {
		
		Display currentDisplay = getWindowManager().getDefaultDisplay();
		
//		float dw = currentDisplay.getWidth();
//		float dh = currentDisplay.getHeight();
		float dw = imageViewP1.getWidth();
		float dh = imageViewP1.getHeight();
		Bitmap returnBmp = Bitmap.createBitmap((int)dw, (int)dh, Bitmap.Config.ARGB_4444);
		
		try {
			BitmapFactory.Options bmpFactoryOption = new BitmapFactory.Options();
			bmpFactoryOption.inJustDecodeBounds = true;
			returnBmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, bmpFactoryOption);
			
			int heightRatio = (int)Math.ceil(bmpFactoryOption.outHeight / dh);
			int widthRatio = (int)Math.ceil(bmpFactoryOption.outWidth / dw);
			
			if(heightRatio > 1 && widthRatio > 1) {
				bmpFactoryOption.inSampleSize = heightRatio;
			} else {
				bmpFactoryOption.inSampleSize = widthRatio;
			}
			bmpFactoryOption.inJustDecodeBounds = false;
			returnBmp = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageFileUri), null, bmpFactoryOption);
		} catch (Exception e) {
			Log.v("loadBitmap", e.getMessage());
		}
		return returnBmp;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}



















