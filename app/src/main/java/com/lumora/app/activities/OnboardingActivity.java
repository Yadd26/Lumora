package com.lumora.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.lumora.app.R;
import com.lumora.app.databinding.ActivityOnboardingBinding;
import com.lumora.app.preferences.PreferenceManager;
import com.lumora.app.preferences.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;
    private OnboardingAdapter adapter;
    private List<OnboardingSlide> slides;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inisialisasi daftar slide onboarding
        setupSlides();

        // Inisialisasi adapter ViewPager2
        adapter = new OnboardingAdapter(slides);
        binding.viewPagerOnboarding.setAdapter(adapter);

        // Siapkan dot indicators
        setupIndicators(slides.size());
        setCurrentIndicator(0);

        // Hubungkan event ganti halaman dengan dot indicators
        binding.viewPagerOnboarding.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
                updateButtons(position);
            }
        });

        // Hubungkan klik tombol
        binding.btnNext.setOnClickListener(v -> {
            int current = binding.viewPagerOnboarding.getCurrentItem();
            if (current < slides.size() - 1) {
                binding.viewPagerOnboarding.setCurrentItem(current + 1);
            } else {
                finishOnboarding();
            }
        });

        binding.btnSkip.setOnClickListener(v -> finishOnboarding());
    }

    private void setupSlides() {
        slides = new ArrayList<>();
        slides.add(new OnboardingSlide(
                "Selamat Datang di Lumora",
                "Selamat datang di Perpustakaan Digital Akademik Premium Anda. Jelajahi pengetahuan klasik dan modern yang terinspirasi dari arsitektur arsip kuno.",
                R.drawable.ic_logo_lumora
        ));
        slides.add(new OnboardingSlide(
                "Kurikulum Terstruktur",
                "Ikuti materi terorganisir lengkap dengan tutorial interaktif, latihan mandiri, dan evaluasi kuis terstandardisasi.",
                R.drawable.ic_learning_path
        ));
        slides.add(new OnboardingSlide(
                "Digital Reader 2.0",
                "Nikmati pengalaman membaca layaknya e-reader premium dengan mode sepia, perubahan ukuran font, bookmark, highlight warna, dan catatan akademis.",
                R.drawable.ic_open_book
        ));
        slides.add(new OnboardingSlide(
                "Komunitas Scholar 3.0",
                "Berdiskusi dengan sesama cendekiawan di forum akademik terintegrasi. Tanyakan solusi, raih reputasi poin, dan dapatkan lencana kehormatan.",
                R.drawable.ic_discussion
        ));
        slides.add(new OnboardingSlide(
                "Sertifikat Kelulusan",
                "Selesaikan jalur pembelajaran Anda untuk mendapatkan sertifikat kelulusan akademik resmi berformat PDF berstandar profesional.",
                R.drawable.ic_quill
        ));
    }

    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(8, 0, 8, 0);

        binding.layoutIndicators.removeAllViews();
        for (int i = 0; i < count; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_inactive));
            indicators[i].setLayoutParams(params);
            binding.layoutIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index) {
        int childCount = binding.layoutIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) binding.layoutIndicators.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_active));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_inactive));
            }
        }
    }

    private void updateButtons(int position) {
        if (position == slides.size() - 1) {
            binding.btnNext.setText("MULAI");
            binding.btnSkip.setVisibility(View.INVISIBLE);
        } else {
            binding.btnNext.setText("LANJUT");
            binding.btnSkip.setVisibility(View.VISIBLE);
        }
    }

    private void finishOnboarding() {
        // Simpan status onboarding selesai
        PreferenceManager.getInstance(this).saveOnboardingCompleted(true);

        // Arahkan ke LoginActivity atau MainActivity sesuai status sesi
        Intent intent;
        if (SessionManager.getInstance(this).isLoggedIn()) {
            intent = new Intent(OnboardingActivity.this, MainActivity.class);
        } else {
            intent = new Intent(OnboardingActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

    // =========================================================================
    // MODEL & ADAPTER INTERNAL
    // =========================================================================

    private static class OnboardingSlide {
        private final String title;
        private final String description;
        private final int iconRes;

        public OnboardingSlide(String title, String description, int iconRes) {
            this.title = title;
            this.description = description;
            this.iconRes = iconRes;
        }

        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public int getIconRes() { return iconRes; }
    }

    private static class OnboardingAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<OnboardingAdapter.ViewHolder> {
        private final List<OnboardingSlide> slides;

        public OnboardingAdapter(List<OnboardingSlide> slides) {
            this.slides = slides;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_onboarding_slide, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            OnboardingSlide slide = slides.get(position);
            holder.textTitle.setText(slide.getTitle());
            holder.textDescription.setText(slide.getDescription());
            holder.imageIcon.setImageResource(slide.getIconRes());
        }

        @Override
        public int getItemCount() {
            return slides.size();
        }

        static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            TextView textTitle, textDescription;
            ImageView imageIcon;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textTitle = itemView.findViewById(R.id.text_slide_title);
                textDescription = itemView.findViewById(R.id.text_slide_description);
                imageIcon = itemView.findViewById(R.id.image_slide_icon);
            }
        }
    }
}
