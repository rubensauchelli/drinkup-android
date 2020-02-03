package com.example.drinkup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.drinkup.objects.EventsDictionary;
import com.google.zxing.WriterException;


import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class QRCodeActivity extends AppCompatActivity {
    /*
    This activity is used to display the QRCode to share
     */
    Bitmap bitmap;
    ImageView qrImage;
    TextView eventNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.transition.fadein, R.transition.fadeout);
        setContentView(R.layout.activity_qrcode);

        qrImage = (ImageView) findViewById(R.id.QR_Image);
        eventNameView=(TextView) findViewById(R.id.event_name);

        Intent i = getIntent();
        String eventID=i.getStringExtra("eventID");

        //Generate QR CODE
        WindowManager manager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        int width = point.x;
        int height = point.y;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;
        QRGEncoder qrgEncoder = new QRGEncoder(eventID, null, QRGContents.Type.TEXT, smallerDimension);

        try {
            // Getting QR-Code as Bitmap
            bitmap = qrgEncoder.encodeAsBitmap();
            // Setting Bitmap to ImageView
            qrImage.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Log.d("<<DEBUG>>", e.toString());
        }

        //SET event name
        String event_name=EventsDictionary.getInstance().get(eventID).eventName;
        eventNameView.setText(event_name.substring(0, 1).toUpperCase() + event_name.substring(1));
    }


    public void doNothing(View v) {
        /*
        Thsi function is used to avoid close the popup if clicked on some element
         */
    }

    public void closePopup(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        /*
        Override default transition effect when back button pressed
         */
        super.onBackPressed();
        overridePendingTransition(R.transition.fadein, R.transition.fadeout);
    }
}
