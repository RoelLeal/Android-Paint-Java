package com.painy.painy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.kyanogen.signatureview.SignatureView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import yuku.ambilwarna.AmbilWarnaDialog;

public class MainActivity extends AppCompatActivity {

    int defaultColor;
    SignatureView signatureView;
    ImageButton imgEraser;
    ImageButton imgColor;
    ImageButton imgSave;
    SeekBar seekBar;
    TextView penSize;

    private static String filename;
    File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/}myPaintings");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        defaultColor = ContextCompat.getColor(MainActivity.this, R.color.black);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String date = format.format(new Date());
        filename = path + "/" + date + ".png";

        if(!path.exists())
        path.mkdir();

        signatureView = findViewById(R.id.signature_view);
        seekBar = findViewById(R.id.penSize);
        penSize = findViewById(R.id.txtPenSize);
        imgEraser = findViewById(R.id.btnEraser);
        imgColor = findViewById(R.id.btnColor);
        imgSave = findViewById(R.id.btnSave);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                penSize.setText(progress + "dp");
                signatureView.setPenSize(progress);
                seekBar.setMax(50);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        imgColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirColor();
            }


        });
        imgEraser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureView.clearCanvas();
            }
        });
        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!signatureView.isBitmapEmpty()){
                    try {
                        saveImage();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(MainActivity.this, "No se pudo guardar :(", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        askPermiso();
    }

    private void saveImage() throws IOException {
        File file = new File(filename);
        Bitmap bitmap = signatureView.getSignatureBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 ,bos);
        byte[] bitmapData = bos.toByteArray();

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(bitmapData);
        fos.flush();
        fos.close();
        Toast.makeText(this, "Dibujo guardado!", Toast.LENGTH_SHORT).show();
    }
    private void askPermiso() {
        Dexter.withContext(getApplicationContext())
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
              .withListener(new MultiplePermissionsListener() {
                  @Override
                  public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                      Toast.makeText(MainActivity.this, "Granted", Toast.LENGTH_SHORT).show();
                  }

                  @Override
                  public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                  }
              }).check();
    }
    private void abrirColor() {
        AmbilWarnaDialog ambilWarnaDialog = new AmbilWarnaDialog(this, defaultColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {

                defaultColor = color;
                signatureView.setPenColor(color);

            }
        });
       ambilWarnaDialog.show();
    }

}