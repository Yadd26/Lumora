package com.lumora.app.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CertificateGenerator {

    public static File generateCertificate(Context context, String userName, String pathName) {
        // Create pdf document
        PdfDocument document = new PdfDocument();
        
        // Horizontal A4 size page (1000 x 700)
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(1000, 700, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        
        // 1. Background (Parchment/Cream)
        canvas.drawColor(Color.parseColor("#FAF4E8"));
        
        // 2. Gold Border Frame (Outer)
        paint.setColor(Color.parseColor("#C8A165"));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(6f);
        canvas.drawRect(30, 30, 970, 670, paint);
        
        // Gold Border Frame (Inner)
        paint.setStrokeWidth(2f);
        canvas.drawRect(38, 38, 962, 662, paint);
        
        // 3. Ornaments in corners (❦)
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(24);
        paint.setColor(Color.parseColor("#C8A165"));
        canvas.drawText("❦", 50, 65, paint);
        canvas.drawText("❦", 935, 65, paint);
        canvas.drawText("❦", 50, 650, paint);
        canvas.drawText("❦", 935, 650, paint);

        // 4. Logo Header
        paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
        paint.setTextSize(36);
        paint.setColor(Color.parseColor("#3E2723")); // Dark Academia Brown
        paint.setTextAlign(Paint.Align.CENTER);
        canvas.drawText("L U M O R A", 500, 100, paint);
        
        // Tagline
        paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.ITALIC));
        paint.setTextSize(14);
        paint.setColor(Color.parseColor("#C8A165"));
        canvas.drawText("Where Ancient Wisdom Meets Modern Learning", 500, 125, paint);
        
        // Decorative line
        paint.setColor(Color.parseColor("#EFE5D3"));
        canvas.drawLine(400, 145, 600, 145, paint);
        
        // 5. Certificate Title
        paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
        paint.setTextSize(32);
        paint.setColor(Color.parseColor("#C8A165"));
        canvas.drawText("SERTIFIKAT KELULUSAN", 500, 210, paint);
        
        // 6. Recipient intro
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(16);
        paint.setColor(Color.parseColor("#5C3D2E"));
        canvas.drawText("Dengan ini menerangkan dan menyatakan bahwa:", 500, 270, paint);
        
        // 7. Student Name
        paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
        paint.setTextSize(36);
        paint.setColor(Color.parseColor("#3E2723"));
        canvas.drawText(userName, 500, 340, paint);
        
        // Underline name
        paint.setColor(Color.parseColor("#C8A165"));
        paint.setStrokeWidth(2f);
        canvas.drawLine(300, 355, 700, 355, paint);
        
        // 8. Body text
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(16);
        paint.setColor(Color.parseColor("#5C3D2E"));
        canvas.drawText("telah menyelesaikan seluruh modul dan lulus pada jalur pembelajaran", 500, 410, paint);
        
        // 9. Path name
        paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC));
        paint.setTextSize(26);
        paint.setColor(Color.parseColor("#C8A165"));
        canvas.drawText(pathName, 500, 470, paint);
        
        // 10. Date and signature
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(14);
        paint.setColor(Color.parseColor("#8D6E63"));
        
        // Date (Left bottom)
        String dateStr = new SimpleDateFormat("dd MMMM yyyy", new Locale("id", "ID")).format(new Date());
        canvas.drawText("Tanggal: " + dateStr, 250, 560, paint);
        
        // Signature line (Right bottom)
        canvas.drawText("Dewan Akademik Lumora", 750, 560, paint);
        paint.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD_ITALIC));
        paint.setTextSize(20);
        paint.setColor(Color.parseColor("#C8A165"));
        canvas.drawText("❦ Lumora Council ❦", 750, 600, paint);
        
        document.finishPage(page);
        
        // Save file
        File pdfDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
        if (pdfDir != null && !pdfDir.exists()) {
            pdfDir.mkdirs();
        }
        
        File file = new File(pdfDir, "Sertifikat_Lumora_" + pathName.replace(" ", "_") + ".pdf");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            document.writeTo(fos);
            document.close();
            fos.close();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            document.close();
            return null;
        }
    }

    public static void openOrShareCertificate(Context context, File file) {
        if (file == null || !file.exists()) {
            Toast.makeText(context, "File sertifikat tidak ditemukan!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
        
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            // If PDF reader is not installed, share it
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(Intent.createChooser(shareIntent, "Bagikan Sertifikat PDF"));
        }
    }
}
