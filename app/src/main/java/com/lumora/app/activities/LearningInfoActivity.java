package com.lumora.app.activities;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.lumora.app.R;
import com.lumora.app.databinding.ActivityLearningInfoBinding;
import com.lumora.app.utils.LearningInfoProvider;

/**
 * LearningInfoActivity - Menampilkan kurikulum pembelajaran terstruktur (silabus),
 * kompetensi, estimasi waktu belajar, dan tingkat kesulitan untuk buku yang dipilih.
 */
public class LearningInfoActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK_TITLE = "extra_book_title";
    public static final String EXTRA_BOOK_AUTHOR = "extra_book_author";
    public static final String EXTRA_BOOK_COVER = "extra_book_cover";
    public static final String EXTRA_BOOK_SUBJECT = "extra_book_subject";

    private ActivityLearningInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLearningInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Setup Toolbar
        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Dapatkan data dari intent
        String title = getIntent().getStringExtra(EXTRA_BOOK_TITLE);
        String author = getIntent().getStringExtra(EXTRA_BOOK_AUTHOR);
        String cover = getIntent().getStringExtra(EXTRA_BOOK_COVER);
        String subject = getIntent().getStringExtra(EXTRA_BOOK_SUBJECT);

        // Bind data buku dasar
        binding.textInfoTitle.setText(title);
        binding.textInfoAuthor.setText(author != null ? author : "Penulis Tidak Diketahui");

        // Load cover dengan Glide
        if (cover != null && !cover.isEmpty()) {
            Glide.with(this)
                    .load(cover)
                    .placeholder(R.drawable.ic_bookmark_outline)
                    .error(R.drawable.ic_bookmark_outline)
                    .into(binding.imageInfoCover);
        } else {
            binding.imageInfoCover.setImageResource(R.drawable.ic_bookmark_outline);
        }

        // Dapatkan data rencana belajar
        LearningInfoProvider.LearningData data = LearningInfoProvider.getLearningData(title, subject);

        // Set kesulitan & estimasi waktu
        binding.textInfoDifficulty.setText(data.getDifficulty());
        binding.textInfoTime.setText(data.getEstimation());

        // Populasikan materi pelajaran
        populateList(binding.containerWhatWillLearn, data.getWhatWillBeLearned());

        // Populasikan kompetensi
        populateList(binding.containerCompetencies, data.getCompetencies());
    }

    /**
     * Membantu mempopulasikan string list ke dalam LinearLayout sebagai poin-poin bullet.
     */
    private void populateList(LinearLayout container, java.util.List<String> items) {
        container.removeAllViews();
        for (String item : items) {
            TextView tv = new TextView(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 6, 0, 6);
            tv.setLayoutParams(params);
            tv.setText("•  " + item);
            tv.setTextSize(15);
            
            // Set style dan warna teks sekunder yang rapi
            tv.setTextColor(getResources().getColor(android.R.color.darker_gray));
            
            container.addView(tv);
        }
    }
}
