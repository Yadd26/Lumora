package com.lumora.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lumora.app.R;
import com.lumora.app.database.DatabaseHelper;
import com.lumora.app.databinding.ActivityLoginBinding;
import com.lumora.app.models.User;
import com.lumora.app.preferences.SessionManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * LoginActivity - Aktivitas untuk menangani login pengguna secara lokal.
 * Memvalidasi input dan memeriksa kredensial terhadap database SQLite di background thread.
 */
public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private DatabaseHelper databaseHelper;
    private SessionManager sessionManager;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inisialisasi helper database, manajer sesi, dan executor thread background
        databaseHelper = DatabaseHelper.getInstance(this);
        sessionManager = SessionManager.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        setupListeners();
    }

    /**
     * Menyiapkan listener klik tombol login dan tombol pendaftaran.
     */
    private void setupListeners() {
        binding.btnLogin.setOnClickListener(v -> performLogin());
        binding.btnGotoRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Menangani proses login lokal dengan validasi input dan pengecekan database SQLite.
     */
    private void performLogin() {
        String email = binding.editEmail.getText() != null 
                ? binding.editEmail.getText().toString().trim() : "";
        String password = binding.editPassword.getText() != null 
                ? binding.editPassword.getText().toString() : "";

        // Validasi input
        boolean isValid = true;

        if (email.isEmpty()) {
            binding.inputLayoutEmail.setError(getString(R.string.error_email_required));
            isValid = false;
        } else {
            binding.inputLayoutEmail.setError(null);
        }

        if (password.isEmpty()) {
            binding.inputLayoutPassword.setError(getString(R.string.error_password_required));
            isValid = false;
        } else {
            binding.inputLayoutPassword.setError(null);
        }

        if (!isValid) return;

        // Tampilkan loading state sederhana (opsional, menonaktifkan tombol)
        setButtonsEnabled(false);

        // Lakukan query database di background thread
        executorService.execute(() -> {
            boolean isSuccessful = databaseHelper.loginUser(email, password);

            runOnUiThread(() -> {
                if (isSuccessful) {
                    // Ambil detail pengguna untuk disimpan ke session
                    executorService.execute(() -> {
                        User user = databaseHelper.getUserByEmail(email);
                        runOnUiThread(() -> {
                            if (user != null) {
                                // Simpan status masuk ke session
                                sessionManager.login(user.getId(), user.getName(), user.getEmail());
                                
                                // Pindah ke halaman utama
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish(); // Tutup LoginActivity
                            } else {
                                setButtonsEnabled(true);
                                Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                } else {
                    setButtonsEnabled(true);
                    // Tampilkan toast error sesuai spesifikasi
                    Toast.makeText(LoginActivity.this, R.string.login_failed, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    /**
     * Mengatur status tombol login/register agar terhindar dari klik ganda saat memproses data.
     */
    private void setButtonsEnabled(boolean enabled) {
        binding.btnLogin.setEnabled(enabled);
        binding.btnGotoRegister.setEnabled(enabled);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Hentikan ExecutorService untuk mencegah kebocoran memori
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}
