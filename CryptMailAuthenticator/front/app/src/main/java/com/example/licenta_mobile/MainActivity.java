package com.example.licenta_mobile;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.API.APIResponseCallback;
import com.example.API.APIService;
import com.example.Crypto.RSAKeyGenerator;
import com.example.Crypto.SignatureSignerJava;
import com.example.Entities.AccountAdapter;
import com.example.Entities.AccountViewModel;
import com.example.Entities.Accounts;
import com.example.Entities.AccountsWeb;
import com.example.Entities.AccountsWebViewModel;
import com.example.Entities.AppDatabase;
import com.example.Login.SaveEmail;
import com.example.Login.SendCredentials;
import com.example.Login.SendZPK;
import com.example.Login.TrueBasicResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.journeyapps.barcodescanner.ScanIntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity implements AccountAdapter.OnAccountActionListener {
    private static final String TAG = "MainActivity";
    private Socket socket;
    private FloatingActionButton addButton;
    private BiometricHelper biometricHelper;
    private SecretKey encryptionKey;
    private AccountAdapter accountAdapter;
    private AccountViewModel accountViewModel;
    private Cipher encryptionCipher;
    private RecyclerView recyclerView;
    private TextInputEditText searchBar;
    private MaterialButton searchButton;
    private APIService apiService = new APIService(this);
    private TextView emptyView;
    private String pendingEmail; // Store QR code email
    private String pendingWebPublicKey; // Store QR code public key

    // QR Scanner Launcher with proper handler
    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if (result.getContents() != null) {
                    handleScanResult(result);
                } else {
                    Toast.makeText(MainActivity.this, "Scan cancelled", Toast.LENGTH_SHORT).show();
                    clearPendingData();
                }
            }
    );

    private void setupSearchBar() {
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim().toLowerCase();
                filterAccounts(query);
            }
        });

        searchButton.setOnClickListener(v -> {
            String query = searchBar.getText().toString().trim().toLowerCase();
            filterAccounts(query);
        });
    }

    private void filterAccounts(String query) {
        accountAdapter.filter(query);
        emptyView.setVisibility(accountAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(accountAdapter.getItemCount() == 0 ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onRequestHandled(Accounts account, boolean approved) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            account.setHasPendingRequest(false);
            AppDatabase.getDatabase(this).accountsDao().update(account);
        });
        // Notify server
        // apiService.sendRequestResolution(account.getEmailAddress(), approved);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(authRequestReceiver,
                new IntentFilter("com.example.AUTH_REQUEST_RECEIVED"));
        System.out.println("ON RESUME");
        connectSocket();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(authRequestReceiver);
        System.out.println("ON PAUSE");
    }

    private void connectSocket() {
        try {
            if (socket == null || !socket.connected()) {
                IO.Options options = new IO.Options();
                options.forceNew = true;
                options.reconnection = true;
                options.transports = new String[] { "websocket" };
                options.timeout = 10000;

                socket = IO.socket("http://192.168.216.15:6000", options);
                System.out.println(socket.isActive());
                socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
                    Log.e("SOCKET", "Connection error: " + Arrays.toString(args));
                });

                socket.on(Socket.EVENT_CONNECT, args -> {
                    Log.d("SOCKET", "Connected");
                    String userEmail = getSharedPreferences("AuthPrefs", MODE_PRIVATE)
                            .getString("userEmail", null);
                    if (userEmail != null) {
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("email", userEmail);
                            socket.emit("register", obj);
                            Log.d("SOCKET", "Registered email: " + userEmail);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                socket.on("encrypted_data", args -> {
                    if (args.length > 0) {
                        JSONObject data = (JSONObject) args[0];
                        Log.d("SOCKET", "Received encrypted data: " + data.toString());
                        String targetEmail = null;
                        try {
                            targetEmail = data.getString("email");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        final String someEmail = targetEmail;
                        runOnUiThread(() -> {
                            accountAdapter.setHighlightedEmail(someEmail, data);
                        });
                    }
                });

                socket.connect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isUserLoggedIn()) {
            startLoginActivity();
            finish();
            return;
        }

        setContentView(R.layout.activity_main);
        initializeViews();
        setupRecyclerView();
        setupBiometricAuthentication();
        setupClickListeners();
        setupMenuButton();
        setupSearchBar();
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.email_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        AccountsWebViewModel accountsWebViewModel = new ViewModelProvider(this).get(AccountsWebViewModel.class);
        AccountViewModel anotherone = new ViewModelProvider(this).get(AccountViewModel.class);
        accountAdapter = new AccountAdapter(this, this, recyclerView, accountsWebViewModel, anotherone, biometricHelper);
        recyclerView.setAdapter(accountAdapter);
        accountViewModel = new ViewModelProvider(this).get(AccountViewModel.class);
        accountViewModel.getUserAccounts().observe(this, accounts -> {
            accountAdapter.setAccounts(accounts);
        });
    }

    @Override
    public void onBiometricRequest(String email, String ownerEmail, String encryptedPrivateKey, String ivBase64, String encData,
                                   String requestId, String c, String publicKeyWeb) {
        runOnUiThread(() -> {
            byte[] iv = ivBase64 != null ? Base64.decode(ivBase64, Base64.NO_WRAP) : null;
            biometricHelper.authenticate(Cipher.DECRYPT_MODE, iv, new BiometricHelper.BiometricCallback() {
                @Override
                public void onSuccess(SecretKey secretKey, Cipher cipher) {
                    try {
                        byte[] decoded = Base64.decode(encryptedPrivateKey, Base64.NO_WRAP);
                        byte[] decrypted = cipher.doFinal(decoded);
                        String privateKeyPem = new String(decrypted, StandardCharsets.UTF_8);

                        if (!encData.matches("^[0-9a-fA-F]+$")) {
                            throw new Exception("Invalid hex string for encM");
                        }
                        if (!c.matches("^[0-9a-fA-F]+$")) {
                            throw new Exception("Invalid hex string for c");
                        }

                        String[] zkpResponses = SignatureSignerJava.generateZKPResponses(privateKeyPem, encData, publicKeyWeb, c, email);
                        System.out.println(Arrays.toString(zkpResponses));
                        apiService.sendZPK(new SendZPK(zkpResponses, email, ownerEmail, requestId), new APIResponseCallback<TrueBasicResponse>() {
                            @Override
                            public void onSuccess(TrueBasicResponse result) {
                                System.out.println(result.getMessage());
                            }
                            @Override
                            public void onError(String errorMessage) {
                                System.out.println(errorMessage);
                            }
                        });
                    } catch (Exception e) {
                        Log.e(TAG, "Decryption error: " + e.getMessage(), e);
                        showAuthenticationError(email, "Failed to decrypt key: " + e.getMessage());
                    } finally {
                        encryptionKey = null;
                        encryptionCipher = null;
                    }
                }

                @Override
                public void onError(Exception error) {
                    showAuthenticationError(email, "Biometric failed: " + error.getMessage());
                }

                private void showAuthenticationError(String email, String message) {
                    runOnUiThread(() -> {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle("Error")
                                .setMessage(message + " for " + email)
                                .setPositiveButton("OK", null)
                                .show();
                    });
                }
            });
        });
    }

    private final BroadcastReceiver authRequestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.hasExtra("email")) {
                String email = intent.getStringExtra("email");
                if (email != null) {
                    accountAdapter.setHighlightedEmail(email, null);
                    Accounts account = findAccountByEmail(email);
                    if (account != null && account.hasPendingRequest()) {
                        showAuthenticationDialog(account);
                    }
                }
            }
        }
    };

    private Accounts findAccountByEmail(String email) {
        if (accountAdapter != null && accountAdapter.getAccounts() != null) {
            for (Accounts account : accountAdapter.getAccounts()) {
                if (account.getEmailAddress().equals(email)) {
                    return account;
                }
            }
        }
        return null;
    }

    private void showAuthenticationDialog(Accounts account) {
        new AlertDialog.Builder(this)
                .setTitle("Authentication Request")
                .setMessage("Approve login for " + account.getEmailAddress() + "?")
                .setPositiveButton("Approve", (dialog, which) -> {
                    account.setHasPendingRequest(false);
                    onRequestHandled(account, true);
                })
                .setNegativeButton("Deny", (dialog, which) -> {
                    account.setHasPendingRequest(false);
                    onRequestHandled(account, false);
                })
                .setCancelable(false)
                .show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    private void setupMenuButton() {
        MaterialButton menuButton = findViewById(R.id.menu_button);
        MaterialCardView menuPopup = findViewById(R.id.menu_popup);
        TextView accountDetails = findViewById(R.id.account_details);
        TextView logout = findViewById(R.id.logout);

        menuButton.setOnClickListener(v -> {
            if (menuPopup.getVisibility() == View.VISIBLE) {
                menuPopup.setVisibility(View.GONE);
            } else {
                menuPopup.setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.main).setOnClickListener(v -> {
            menuPopup.setVisibility(View.GONE);
        });

        menuPopup.setOnClickListener(v -> {});

        accountDetails.setOnClickListener(v -> {
            menuPopup.setVisibility(View.GONE);
            showAccountDetails();
        });

        logout.setOnClickListener(v -> {
            menuPopup.setVisibility(View.GONE);
            performLogout();
        });
    }

    private void showAccountDetails() {
        String userEmail = getSharedPreferences("AuthPrefs", MODE_PRIVATE)
                .getString("userEmail", "");
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("USER_EMAIL", userEmail);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void performLogout() {
        SharedPreferences prefs = getSharedPreferences("AuthPrefs", MODE_PRIVATE);
        prefs.edit().clear().apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private boolean isUserLoggedIn() {
        SharedPreferences prefs = getSharedPreferences("AuthPrefs", MODE_PRIVATE);
        return prefs.contains("authToken");
    }

    private void startLoginActivity() {
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void initializeViews() {
        addButton = findViewById(R.id.add_button);
        searchBar = findViewById(R.id.search_bar);
        searchButton = findViewById(R.id.search_button);
        emptyView = findViewById(R.id.empty_view);
    }

    private void setupBiometricAuthentication() {
        biometricHelper = new BiometricHelper(this);
        promptForBiometricAuthentication();
    }

    private void setupClickListeners() {
        addButton.setOnClickListener(view -> {
            if (encryptionKey != null) {
                scanQRCode();
            } else {
                promptForBiometricAuthentication();
            }
        });
    }

    private void promptForBiometricAuthentication() {
        biometricHelper.authenticate(Cipher.ENCRYPT_MODE, null, new BiometricHelper.BiometricCallback() {
            @Override
            public void onSuccess(SecretKey secretKey, Cipher cipher) {
                encryptionKey = secretKey;
                encryptionCipher = cipher;
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Authentication successful", Toast.LENGTH_SHORT).show();
                    // Retry storing account if pending data exists
                    if (pendingEmail != null && pendingWebPublicKey != null) {
                        storeNewAccount(pendingEmail, pendingWebPublicKey);
                    }
                });
            }

            @Override
            public void onError(Exception error) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this,
                            "Authentication failed: " + error.getMessage(),
                            Toast.LENGTH_LONG).show();
                    clearPendingData();
                });
            }
        });
    }

    private void scanQRCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan a QR Code");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        barcodeLauncher.launch(options);
    }

    private void handleScanResult(ScanIntentResult result) {
        if (result.getContents() == null) {
            clearPendingData();
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject(result.getContents());
            String email = jsonObject.getString("email");
            String publicKey = jsonObject.getString("publicKey");
            publicKey = publicKey.replace("\n", "\\n");

            Log.d(TAG, "Scanned email: " + email);
            Log.d(TAG, "Public key (first 5 chars): " + publicKey.substring(0, 5));

            // Store QR code data
            pendingEmail = email;
            pendingWebPublicKey = publicKey;

            storeNewAccount(email, publicKey);
        } catch (JSONException e) {
            Log.e(TAG, "QR parsing error", e);
            Toast.makeText(this, "Invalid QR Code format", Toast.LENGTH_SHORT).show();
            clearPendingData();
        } catch (Exception e) {
            Log.e(TAG, "Account storage error", e);
            Toast.makeText(this, "Failed to save account", Toast.LENGTH_SHORT).show();
            clearPendingData();
        }
    }

    private void clearPendingData() {
        pendingEmail = null;
        pendingWebPublicKey = null;
        encryptionKey = null;
        encryptionCipher = null;
    }

    private void storeNewAccount(String email, String webPublicKey) {
        if (encryptionKey == null || encryptionCipher == null) {
            Toast.makeText(this, "Please authenticate first", Toast.LENGTH_SHORT).show();
            promptForBiometricAuthentication();
            return;
        }

        AppDatabase.databaseWriteExecutor.execute(() -> {
            try {
                // Generate new RSA key pair
                KeyPair keyPair = RSAKeyGenerator.generateKeyPair();
                String privateKeyPem = RSAKeyGenerator.convertToPEM(keyPair.getPrivate());
                String generatedPublicKey = RSAKeyGenerator.convertToPEM(keyPair.getPublic());

                // Encrypt the private key
                byte[] encryptedBytes = encryptionCipher.doFinal(privateKeyPem.getBytes(StandardCharsets.UTF_8));
                String encryptedPrivateKey = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);

                // Get the IV
                byte[] iv = encryptionCipher.getIV();
                String ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP);

                Log.d(TAG, "Encrypted private key: " + encryptedPrivateKey);
                Log.d(TAG, "IV (Base64): " + ivBase64);

                // Create account object
                SharedPreferences prefs = getSharedPreferences("AuthPrefs", MODE_PRIVATE);
                String ownerEmail = prefs.getString("userEmail", "");

                // Send credentials to server only after encryption succeeds
                apiService.sendUserCredentials(
                        new SendCredentials(email, ownerEmail, generatedPublicKey, "save"),
                        new APIResponseCallback<TrueBasicResponse>() {
                            @Override
                            public void onSuccess(TrueBasicResponse result) {
                                System.out.println(result.getMessage());
                            }

                            @Override
                            public void onError(String errorMessage) {
                                System.out.println(errorMessage);
                                runOnUiThread(() -> {
                                    Toast.makeText(MainActivity.this,
                                            "Failed to send credentials: " + errorMessage,
                                            Toast.LENGTH_LONG).show();
                                });
                            }
                        });

                // Create new account with both emails
                Accounts newAccount = new Accounts(
                        email,
                        ownerEmail,
                        generatedPublicKey,
                        encryptedPrivateKey,
                        ivBase64
                );
                newAccount.setHasPendingRequest(false);

                // Save the email and public key from QR code in AccountsWeb table
                AccountsWeb newWebAccount = new AccountsWeb(email, webPublicKey);

                // Insert into database
                AppDatabase db = AppDatabase.getDatabase(MainActivity.this);
                SendEmailToServer(ownerEmail, email);
                db.runInTransaction(() -> {
                    db.accountsDao().insert(newAccount);
                    db.accountsWebDao().insert(newWebAccount);
                    return null;
                });

                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Account saved", Toast.LENGTH_SHORT).show();
                    clearPendingData();
                });
            } catch (Exception e) {
                Log.e(TAG, "Encryption failed", e);
                if (e instanceof IllegalStateException ||
                        (e.getCause() != null && e.getCause() instanceof IllegalStateException)) {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Session expired, please authenticate again", Toast.LENGTH_LONG).show();
                        promptForBiometricAuthentication();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Save failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        clearPendingData();
                    });
                }
            }
        });
    }

    private void SendEmailToServer(String myemail, String emailToBeSaved) {
        apiService.sendEmail(new SaveEmail(myemail, emailToBeSaved), new APIResponseCallback<TrueBasicResponse>() {
            @Override
            public void onSuccess(TrueBasicResponse result) {
                System.out.println(result.getMessage());
            }

            @Override
            public void onError(String errorMessage) {
                System.out.println(errorMessage);
            }
        });
    }

    @Override
    public void onDeleteAccount(Accounts account) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            AppDatabase.getDatabase(this).accountsDao().delete(account);
            runOnUiThread(() -> accountAdapter.removeAccount(account));
        });
    }

    @Override
    public void onAccountClick(Accounts account) {
        Toast.makeText(this, "Showing details for: " + account.getEmailAddress(), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clearPendingData();
    }
}