package com.lumora.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.lumora.app.activities.DetailActivity;
import com.lumora.app.adapters.ResourceAdapter;
import com.lumora.app.databinding.FragmentLearningResourcesBinding;
import com.lumora.app.models.ResourceItem;

import java.util.ArrayList;
import java.util.List;

/**
 * LearningResourcesFragment - Menyediakan daftar materi pembelajaran digital yang dibagi
 * menjadi tiga kategori (Buku, Tutorial, Referensi Akademik) menggunakan TabLayout & RecyclerView.
 */
public class LearningResourcesFragment extends Fragment implements ResourceAdapter.OnResourceClickListener {

    private FragmentLearningResourcesBinding binding;
    private ResourceAdapter resourceAdapter;
    private List<ResourceItem> allResources = new ArrayList<>();
    private List<ResourceItem> filteredResources = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentLearningResourcesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initResourcesData();
        setupTabLayout();
        setupRecyclerView();

        // Default: filter tab ke-0 (Buku)
        filterResources(0);
    }

    /**
     * Menginisialisasi data statis (mock data) untuk sumber belajar.
     */
    private void initResourcesData() {
        // Tab 1: Buku
        allResources.add(new ResourceItem(
                "Java: A Beginner's Guide",
                "Buku",
                "Buku panduan lengkap dasar-dasar pemrograman Java oleh Herbert Schildt.",
                "Herbert Schildt",
                "800 Halaman"
        ));
        allResources.add(new ResourceItem(
                "Clean Code: A Handbook of Agile Software Craftsmanship",
                "Buku",
                "Buku fundamental tentang seni menulis kode pemrograman yang bersih dan maintainable.",
                "Robert C. Martin",
                "464 Halaman"
        ));
        allResources.add(new ResourceItem(
                "Database System Concepts",
                "Buku",
                "Buku teks utama untuk konsep arsitektur basis data, SQL, normalisasi, dan transaksi.",
                "Abraham Silberschatz",
                "1376 Halaman"
        ));
        allResources.add(new ResourceItem(
                "Computer Networking: A Top-Down Approach",
                "Buku",
                "Buku pembelajaran jaringan komputer dari layer aplikasi ke physical layer.",
                "James F. Kurose",
                "864 Halaman"
        ));
        allResources.add(new ResourceItem(
                "Artificial Intelligence: A Modern Approach",
                "Buku",
                "Buku rujukan utama tentang konsep kecerdasan buatan, agen cerdas, dan logika fuzzy.",
                "Stuart Russell",
                "1152 Halaman"
        ));

        // Tab 2: Tutorial
        allResources.add(new ResourceItem(
                "Pemrograman Java Dasar",
                "Tutorial",
                "Pelajari variabel, percabangan, loop, OOP, dan collection di Java secara gratis.",
                "Lumora Developer Academy",
                "10 Jam Belajar"
        ));
        allResources.add(new ResourceItem(
                "Perancangan Basis Data untuk Pemula",
                "Tutorial",
                "Menguasai pemodelan data (ERD), normalisasi, dan implementasi SQL praktis.",
                "Dicoding Indonesia",
                "12 Jam Belajar"
        ));
        allResources.add(new ResourceItem(
                "Pengembangan Aplikasi Android Modern (Jetpack Compose)",
                "Tutorial",
                "Membangun antarmuka aplikasi Android deklaratif yang modern menggunakan Kotlin.",
                "Android Developers Group",
                "25 Jam Belajar"
        ));
        allResources.add(new ResourceItem(
                "Dasar Machine Learning dengan Python",
                "Tutorial",
                "Pengenalan regresi, klasifikasi, klasterisasi, dan prapemrosesan data.",
                "Google AI Studio",
                "20 Jam Belajar"
        ));
        allResources.add(new ResourceItem(
                "Keamanan Siber: Pengenalan Penetration Testing",
                "Tutorial",
                "Dasar etika hacking, scanning jaringan, eksploitasi celah, dan pengamanan sistem.",
                "Cyber Security Association",
                "18 Jam Belajar"
        ));

        // Tab 3: Referensi Akademik
        allResources.add(new ResourceItem(
                "A Relational Model of Data for Large Shared Data Banks",
                "Referensi Akademik",
                "Jurnal akademik bersejarah karya Edgar F. Codd yang memperkenalkan model basis data relasional.",
                "ACM Communications (E.F. Codd)",
                "14 Halaman"
        ));
        allResources.add(new ResourceItem(
                "The Ethernet: Local Computer Networks",
                "Referensi Akademik",
                "Kertas kerja fundamental tentang konsep transmisi ethernet dan jaringan area lokal.",
                "IEEE Transactions (Metcalfe & Boggs)",
                "22 Halaman"
        ));
        allResources.add(new ResourceItem(
                "Deep Learning: History and State of the Art",
                "Referensi Akademik",
                "Tinjauan komprehensif tentang perkembangan model Deep Learning, CNN, dan Transformer.",
                "MIT Press (Y. Bengio)",
                "35 Halaman"
        ));
        allResources.add(new ResourceItem(
                "Information Systems Security Audit Framework",
                "Referensi Akademik",
                "Panduan standar internasional untuk pelaksanaan audit keamanan sistem informasi.",
                "NIST Publications",
                "45 Halaman"
        ));
    }

    /**
     * Konfigurasi TabLayout dengan 3 kategori.
     */
    private void setupTabLayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Buku"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Tutorial"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Referensi"));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterResources(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    /**
     * Konfigurasi RecyclerView.
     */
    private void setupRecyclerView() {
        resourceAdapter = new ResourceAdapter(filteredResources, this);
        binding.recyclerResources.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerResources.setAdapter(resourceAdapter);
    }

    /**
     * Memfilter data berdasarkan index tab yang dipilih.
     */
    private void filterResources(int tabPosition) {
        filteredResources.clear();
        String targetType;
        switch (tabPosition) {
            case 1:
                targetType = "Tutorial";
                break;
            case 2:
                targetType = "Referensi Akademik";
                break;
            case 0:
            default:
                targetType = "Buku";
                break;
        }

        for (ResourceItem item : allResources) {
            if (item.getType().equals(targetType)) {
                filteredResources.add(item);
            }
        }
        resourceAdapter.updateData(filteredResources);
    }

    @Override
    public void onResourceClick(ResourceItem item) {
        if (item.getType().equals("Buku")) {
            // Arahkan ke DetailActivity dengan mock data yang relevan
            Intent intent = new Intent(requireContext(), DetailActivity.class);
            
            // Map data buku berdasarkan judul
            String mockKey = "/works/OL1708112W"; // Default mock key
            String mockCoverUrl = "";
            String mockSubject = "Programming, Computer Science";
            int mockEditions = 3;
            String mockYear = "2020";

            if (item.getTitle().contains("Java")) {
                mockKey = "/works/OL257922W";
                mockCoverUrl = "https://covers.openlibrary.org/b/id/8315182-L.jpg";
                mockSubject = "Java, Programming, OOP";
                mockEditions = 8;
                mockYear = "2018";
            } else if (item.getTitle().contains("Clean Code")) {
                mockKey = "/works/OL273891W";
                mockCoverUrl = "https://covers.openlibrary.org/b/id/11495914-L.jpg";
                mockSubject = "Software Engineering, Clean Code, Agile";
                mockEditions = 2;
                mockYear = "2008";
            } else if (item.getTitle().contains("Database")) {
                mockKey = "/works/OL180371W";
                mockCoverUrl = "https://covers.openlibrary.org/b/id/8313361-L.jpg";
                mockSubject = "Database, SQL, Relational";
                mockEditions = 7;
                mockYear = "2019";
            } else if (item.getTitle().contains("Networking")) {
                mockKey = "/works/OL156828W";
                mockCoverUrl = "https://covers.openlibrary.org/b/id/11181297-L.jpg";
                mockSubject = "Computer Networks, TCP/IP, Internet";
                mockEditions = 8;
                mockYear = "2020";
            } else if (item.getTitle().contains("Artificial")) {
                mockKey = "/works/OL136281W";
                mockCoverUrl = "https://covers.openlibrary.org/b/id/10207198-L.jpg";
                mockSubject = "Artificial Intelligence, Algorithms, Logic";
                mockEditions = 4;
                mockYear = "2021";
            }

            intent.putExtra(DetailActivity.EXTRA_BOOK_KEY, mockKey);
            intent.putExtra(DetailActivity.EXTRA_BOOK_TITLE, item.getTitle());
            intent.putExtra(DetailActivity.EXTRA_BOOK_AUTHOR, item.getAuthorOrProvider());
            intent.putExtra(DetailActivity.EXTRA_BOOK_YEAR, mockYear);
            intent.putExtra(DetailActivity.EXTRA_BOOK_COVER, mockCoverUrl);
            intent.putExtra(DetailActivity.EXTRA_BOOK_SUBJECT, mockSubject);
            intent.putExtra(DetailActivity.EXTRA_BOOK_EDITION_COUNT, mockEditions);
            startActivity(intent);
        } else {
            // Untuk tutorial/referensi, tampilkan informasi interaktif
            Toast.makeText(requireContext(), 
                    "Materi \"" + item.getTitle() + "\" telah ditambahkan ke rencana belajar digital Anda.", 
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
