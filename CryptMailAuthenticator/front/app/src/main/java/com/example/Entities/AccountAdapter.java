package com.example.Entities;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Crypto.SignatureVerifier;
import com.example.licenta_mobile.BiometricHelper;
import com.example.licenta_mobile.MainActivity;
import com.example.licenta_mobile.R;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class AccountAdapter extends RecyclerView.Adapter<AccountAdapter.EmailViewHolder> {
    private List<Accounts> accounts = new ArrayList<>();
    private List<Accounts> filteredAccounts = new ArrayList<>();
    private final OnAccountActionListener listener;
    private final Activity context;
    private RecyclerView recyclerView; // Reference to the RecyclerView
    private String highlightedEmail = null;
    private AccountsWebViewModel viewModel;

    private AccountViewModel viewModelForAccount;
    private Map<String, JSONObject> notificationDataMap = new HashMap<>();
    private SecretKey encryptionKey;
    private Cipher encryptionCipher;
    private BiometricHelper biometricHelper;

    public interface OnAccountActionListener {
        void onDeleteAccount(Accounts account);
        void onAccountClick(Accounts account);
        void onRequestHandled(Accounts account, boolean approved);
        void onBiometricRequest(String email, String ownerEmail, String encryptedPrivateKey,String ivBase64, String encData, String requestId, String c, String publicKeyWeb);
    }

    public AccountAdapter(Activity context, OnAccountActionListener listener, RecyclerView recyclerView,AccountsWebViewModel viewModel, AccountViewModel viewModel2, BiometricHelper b) {
        this.context = context;
        this.listener = listener;
        this.recyclerView = recyclerView;
        this.viewModel = viewModel;
        this.viewModelForAccount=viewModel2;
        this.biometricHelper=b;
    }

    public void setHighlightedEmail(String email, JSONObject data) {
        for (Accounts account : accounts) {
            account.setHasPendingRequest(false);
        }

        this.highlightedEmail = email;
        notificationDataMap.put(email, data);
        int position = findAccountPosition(email);
        if (position != -1) {
            accounts.get(position).setHasPendingRequest(true);
            notifyItemChanged(position);

            // Auto-scroll to the item
            recyclerView.postDelayed(() -> {
                recyclerView.smoothScrollToPosition(position);
            }, 300);

            // Auto-remove highlight after delay
            recyclerView.postDelayed(() -> {
                if (position < accounts.size() &&
                        accounts.get(position).getEmailAddress().equals(email)) {
                    accounts.get(position).setHasPendingRequest(false);
                    notifyItemChanged(position);
                }
            }, 60000); // 30 seconds
        }
    }

    private int findAccountPosition(String email) {
        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getEmailAddress().equals(email)) {
                return i;
            }
        }
        return -1;
    }

    @NonNull
    @Override
    public EmailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.email_item, parent, false);
        return new EmailViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EmailViewHolder holder, int position) {
        Accounts currentAccount = filteredAccounts.get(position);
        holder.bind(currentAccount);

        // Enhanced visual treatment
        if (currentAccount.hasPendingRequest()) {
            // 1. Enhanced red dot with entrance animation
            holder.redDot.setVisibility(View.VISIBLE);
            startPulseAnimation(holder.redDot);

            // 2. Card highlight with animated border
            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.notification_highlight));

            ValueAnimator borderAnim = ValueAnimator.ofInt(0, 4);
            borderAnim.setDuration(300);
            borderAnim.addUpdateListener(animation -> {
                holder.cardView.setStrokeWidth((int)animation.getAnimatedValue());
                holder.cardView.setStrokeColor(
                        ContextCompat.getColor(context, R.color.notification_highlight_accent));
            });
            borderAnim.start();

            // 3. Text emphasis with animation
            holder.emailText.setTypeface(null, Typeface.BOLD);
            holder.emailText.setTextColor(ContextCompat.getColor(context, R.color.notification_text));
            holder.emailText.animate()
                    .scaleX(1.05f)
                    .scaleY(1.05f)
                    .setDuration(200)
                    .start();
        } else {
            // Reset animations and styles
            holder.redDot.clearAnimation();
            holder.redDot.setVisibility(View.GONE);

            holder.cardView.setCardBackgroundColor(
                    ContextCompat.getColor(context, R.color.card_normal));
            holder.cardView.setStrokeWidth(0);

            holder.emailText.setTypeface(null, Typeface.NORMAL);
            holder.emailText.setTextColor(ContextCompat.getColor(context, R.color.primary_text));
            holder.emailText.setScaleX(1f);
            holder.emailText.setScaleY(1f);
        }
    }

    private void startPulseAnimation(View view) {
        Animation pulse = AnimationUtils.loadAnimation(context, R.anim.pulse);
        view.startAnimation(pulse);

        // Add slight overshoot effect when first appearing
        view.setScaleX(0f);
        view.setScaleY(0f);
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(300)
                .setInterpolator(new OvershootInterpolator(1.5f))
                .start();
    }

    @Override
    public int getItemCount() {
        return filteredAccounts.size();
    }

    public Accounts getItem(int position) {
        return filteredAccounts.get(position);
    }

    public void setAccounts(List<Accounts> accounts) {
        this.accounts = new ArrayList<>(accounts);
        this.filteredAccounts = new ArrayList<>(accounts);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        filteredAccounts.clear();
        if (query.isEmpty()) {
            filteredAccounts.addAll(accounts);
        } else {
            for (Accounts account : accounts) {
                if (account.getEmailAddress().toLowerCase().contains(query)) {
                    filteredAccounts.add(account);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void removeAccount(Accounts account) {
        int position = accounts.indexOf(account);
        if (position != -1) {
            accounts.remove(position);
            notifyItemRemoved(position);
        }
    }

    public List<Accounts> getAccounts() {
        return new ArrayList<>(accounts);
    }

    class EmailViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final TextView emailText;
        private final ImageButton deleteButton;
        private final View redDot;

        public EmailViewHolder(View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.email_item_card);
            emailText = itemView.findViewById(R.id.email_text_1);
            deleteButton = itemView.findViewById(R.id.delete_button_1);
            redDot = itemView.findViewById(R.id.red_dot);
        }

        public void bind(final Accounts account) {
            emailText.setText(account.getEmailAddress());

            // Add ripple effect
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                cardView.setForeground(ContextCompat.getDrawable(
                        context, R.drawable.ripple_effect));
            }

            cardView.setOnClickListener(v -> {
                // Get stored data
                JSONObject notificationData = notificationDataMap.get(account.getEmailAddress());

                if (notificationData != null) {

                    Log.e("BLABLABLA", "Some shit");
                    processNotificationData(notificationData);
                    notificationDataMap.remove(account.getEmailAddress());
                    account.setHasPendingRequest(false);
                    notifyItemChanged(getAdapterPosition());
                } else {
                    listener.onAccountClick(account);
                }
            });
            deleteButton.setOnClickListener(v -> showDeleteConfirmation(account));
        }

        public void processNotificationData(JSONObject data)
        {
            System.out.println("hgevdfebwid.......");
            try {
                String ownerEmail= data.getString("ownerEmail");
                String email = data.getString("email");
                System.out.println("This is what i am working with: email and ownerEmail "+email+" "+ownerEmail);
                String signedHash = data.getString("signedHash");
                String encM  = data.getString("encM");
                String randomString=data.getString("randomString");
                String requestId=data.getString("requestId");
                String c=data.getString("c");

                viewModel.getPublicKeyForEmail(email, publicKey -> {
                    if (publicKey != null) {
                        try {
                            // Verify signature
                            boolean isValid = SignatureVerifier.verifyRSAPSSSignature(
                                    publicKey,
                                    signedHash,
                                    randomString,
                                    32
                            );
                            if (isValid) {
                                // Process valid data
                                System.out.println("You can proceed further..."+email);
                                System.out.println("We're trying something"+ ownerEmail);

                                // i have to extract the private key now
                                viewModelForAccount.getPrvKey(email, ownerEmail, result -> {
                                    if (result != null) {
                                        String encryptedPrivateKey = result.getPrivateKey();
                                        String ivBase64 = result.getIv();
                                        ((Activity) context).runOnUiThread(() -> {
                                            //System.out.println("My private key encrypted: " + encryptedPrivateKey);
                                            listener.onBiometricRequest(email, ownerEmail, encryptedPrivateKey, ivBase64,encM,requestId,c,publicKey);
                                        });
                                    } else {
                                        showAuthenticationDialog(email, "No private key found for ");
                                    }
                                });


                            } else {
                                // Handle invalid signature
                                showAuthenticationDialog(email,"Login request failed for ");
                            }
                        } catch (Exception e) {
                            // Handle decompression/parsing errors
                        }
                    } else {
                        // Handle case where public key wasn't found
                    }
                });

                System.out.println("The compressed string: ");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        public void showAuthenticationDialog(String email, String message) {
            new AlertDialog.Builder(context)
                    .setTitle("Alert")
                    .setMessage(message + email)
                    .setPositiveButton("OK",null)
                    .show();
        }

        private void handleRequest(Accounts account, boolean approved) {
            account.setHasPendingRequest(false);
            notifyItemChanged(accounts.indexOf(account));
            listener.onRequestHandled(account, approved);
        }

        private void showDeleteConfirmation(Accounts account) {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Account")
                    .setMessage("Are you sure you want to delete " + account.getEmailAddress() + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        listener.onDeleteAccount(account);
                        Toast.makeText(context, "Account deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .setIcon(R.drawable.ic_warning)
                    .show();
        }
    }
}