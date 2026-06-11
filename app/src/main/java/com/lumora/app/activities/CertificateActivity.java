package com.lumora.app.activities;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.lumora.app.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CertificateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);
        
        findViewById(R.id.btn_back).setOnClickListener(v -> finish());
        
        findViewById(R.id.btn_download_pdf).setOnClickListener(v -> generatePdf());
    }

    private void generatePdf() {
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create(); // A4 size
        PdfDocument.Page page = document.startPage(pageInfo);

        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        
        // Draw Background
        paint.setColor(Color.parseColor("#FFF8F0")); // Parchment
        canvas.drawRect(0, 0, 595, 842, paint);
        
        // Draw Border
        paint.setColor(Color.parseColor("#C8A165")); // Gold
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        canvas.drawRect(20, 20, 575, 822, paint);
        paint.setStrokeWidth(3);
        canvas.drawRect(30, 30, 565, 812, paint);

        // Draw Text
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor("#5C3D2E"));
        paint.setTextSize(36);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
        canvas.drawText("CERTIFICATE OF COMPLETION", 595 / 2f, 200, paint);
        
        paint.setTextSize(18);
        paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
        canvas.drawText("This is to certify that", 595 / 2f, 300, paint);
        
        paint.setTextSize(28);
        paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC));
        canvas.drawText("Lumora Scholar", 595 / 2f, 380, paint);
        
        paint.setTextSize(18);
        paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.NORMAL));
        canvas.drawText("has successfully completed the course", 595 / 2f, 460, paint);
        
        paint.setTextSize(24);
        paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
        canvas.drawText("Ancient History: Beginner", 595 / 2f, 540, paint);
        
        String date = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(new Date());
        paint.setTextSize(14);
        canvas.drawText("Date: " + date, 595 / 2f, 650, paint);

        document.finishPage(page);

        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Lumora");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, "Certificate_" + System.currentTimeMillis() + ".pdf");
        
        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(this, "Certificate saved to Downloads/Lumora", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to generate PDF", Toast.LENGTH_SHORT).show();
        }
        document.close();
    }
}