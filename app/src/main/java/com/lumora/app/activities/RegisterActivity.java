package com.lumora.app.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lumora.app.R;
import com.lumora.app.database.DatabaseHelper;
import com.lumora.app.databinding.ActivityRegisterBinding;
import com.lumora.app.models.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * RegisterActivity - Aktivitas untuk pendaftaran akun pengguna baru secara lokal.
 * Memvalidasi input dan menyimpan kredensial ke database SQLite di background thread.
 */
public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private DatabaseHelper databaseHelper;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Inisialisasi helper database dan executor thread background
        databaseHelper = DatabaseHelper.getInstance(this);
        executorService = Executors.newSingleThreadExecutor();

        setupListeners();
    }

    /**
     * Menyiapkan listener klik tombol daftar dan tombol kembali ke halaman login.
     */
    private void setupListeners() {
        binding.btnRegister.setOnClickListener(v -> performRegister());
        binding.btnGotoLogin.setOnClickListener(v -> finish());
    }

    /**
     * Menangani proses registrasi lokal dengan validasi input dan penyimpanan SQLite.
     */
    private void performRegister() {
        String name = binding.editName.getText() != null 
                ? binding.editName.getText().toString().trim() : "";
        String email = binding.editEmail.getText() != null 
                ? binding.editEmail.getText().toString().trim() : "";
        String password = binding.editPassword.getText() != null 
                ? binding.editPassword.getText().toString() : "";
        String confirmPassword = binding.editConfirmPassword.getText() != null 
                ? binding.editConfirmPassword.getText().toString() : "";

        // Validasi input
        boolean isValid = true;

        if (name.isEmpty()) {
            binding.inputLayoutName.setError(getString(R.string.error_name_required));
            isValid = false;
        } else {
            binding.inputLayoutName.setError(null);
        }

        if (email.isEmpty()) {
            binding.inputLayoutEmail.setError(getString(R.string.error_email_required));
            isValid = false;
        } else {
            binding.inputLayoutEmail.setError(null);
        }

        if (password.isEmpty()) {
            binding.inputLayoutPassword.setError(getString(R.string.error_password_required));
            isValid = false;
        } else if (password.length() < 6) {
            binding.inputLayoutPassword.setError(getString(R.string.error_password_length));
            isValid = false;
        } else {
            binding.inputLayoutPassword.setError(null);
        }

        if (confirmPassword.isEmpty()) {
            binding.inputLayoutConfirmPassword.setError(getString(R.string.error_password_required));
            isValid = false;
        } else if (!confirmPassword.equals(password)) {
            binding.inputLayoutConfirmPassword.setError(getString(R.string.error_password_mismatch));
            isValid = false;
        } else {
            binding.inputLayoutConfirmPassword.setError(null);
        }

        if (!isValid) return;

        // Nonaktifkan tombol saat memproses data di background thread
        setButtonsEnabled(false);

        // Periksa apakah email sudah terdaftar dan simpan data baru
        executorService.execute(() -> {
            boolean emailExists = databaseHelper.isEmailExists(email);

            runOnUiThread(() -> {
                if (emailExists) {
                    setButtonsEnabled(true);
                    binding.inputLayoutEmail.setError(getString(R.string.error_email_exists));
                } else {
                    // Masukkan data pengguna baru di background thread
                    executorService.execute(() -> {
                        String createdAt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
                        User newUser = new User(name, email, password, createdAt);
                        long result = databaseHelper.registerUser(newUser);

                        runOnUiThread(() -> {
                            setButtonsEnabled(true);
                            if (result != -1) {
                                // Tampilkan pesan berhasil dan kembali ke LoginActivity
                                Toast.makeText(RegisterActivity.this, R.string.register_success, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                }
            });
        });
    }

    /**
     * Mengatur status aktif/nonaktif tombol registrasi.
     */
    private void setButtonsEnabled(boolean enabled) {
        binding.btnRegister.setEnabled(enabled);
        binding.btnGotoLogin.setEnabled(enabled);
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
