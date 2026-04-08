package com.example.anew;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

/**
 * AI-powered travel chatbot screen.
 * Uses OpenAI's Chat Completions API to provide personalised travel advice
 * based on the user's GPS location and saved place preferences.
 *
 * The OpenAI API key is stored locally in SharedPreferences and requested
 * via a dialog on first launch.
 */
public class ChatActivity extends AppCompatActivity {

    private RecyclerView chatRecyclerView;
    private EditText chatInput;
    private ImageButton btnSend;
    private ImageButton btnBack;
    private ImageButton btnSettings;
    private TextView typingIndicator;

    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages;
    private OpenAIHelper openAIHelper;

    private double userLatitude = 0.0;
    private double userLongitude = 0.0;
    private String userPreferences = "";

    private static final String PREFS_NAME = "WanderlyPrefs";
    private static final String KEY_OPENAI_API = "openai_api_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.your_color));
        }
        setContentView(R.layout.activity_chat);

        // Retrieve user location passed from the calling activity
        userLatitude = getIntent().getDoubleExtra("userLat", 0.0);
        userLongitude = getIntent().getDoubleExtra("userLng", 0.0);

        initViews();
        setupRecyclerView();
        setupListeners();
        loadUserPreferences();

        // Prompt for API key if not yet configured
        String apiKey = getApiKey();
        if (apiKey.isEmpty()) {
            showApiKeyDialog();
        } else {
            openAIHelper = new OpenAIHelper(apiKey);
            addWelcomeMessage();
        }
    }

    private void initViews() {
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        chatInput = findViewById(R.id.chatInput);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        btnSettings = findViewById(R.id.btnSettings);
        typingIndicator = findViewById(R.id.typingIndicator);
    }

    private void setupRecyclerView() {
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(this, messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnSettings.setOnClickListener(v -> showApiKeyDialog());
        btnSend.setOnClickListener(v -> sendMessage());
    }

    /**
     * Sends the user's typed message to the OpenAI API and displays the reply.
     */
    private void sendMessage() {
        String text = chatInput.getText().toString().trim();
        if (text.isEmpty()) {
            return;
        }

        String apiKey = getApiKey();
        if (apiKey.isEmpty()) {
            showApiKeyDialog();
            return;
        }

        if (openAIHelper == null) {
            openAIHelper = new OpenAIHelper(apiKey);
        }

        // Add user message to the list and scroll to it
        messages.add(new ChatMessage(text, ChatMessage.TYPE_USER));
        chatAdapter.notifyItemInserted(messages.size() - 1);
        chatRecyclerView.scrollToPosition(messages.size() - 1);
        chatInput.setText("");

        // Show loading state
        typingIndicator.setVisibility(View.VISIBLE);
        btnSend.setEnabled(false);

        // Call OpenAI
        openAIHelper.sendMessage(messages, userLatitude, userLongitude, userPreferences,
                new OpenAIHelper.ChatCallback() {
                    @Override
                    public void onSuccess(String response) {
                        typingIndicator.setVisibility(View.GONE);
                        btnSend.setEnabled(true);

                        messages.add(new ChatMessage(response, ChatMessage.TYPE_BOT));
                        chatAdapter.notifyItemInserted(messages.size() - 1);
                        chatRecyclerView.scrollToPosition(messages.size() - 1);
                    }

                    @Override
                    public void onError(String error) {
                        typingIndicator.setVisibility(View.GONE);
                        btnSend.setEnabled(true);

                        messages.add(new ChatMessage(
                                "Sorry, I encountered an error: " + error,
                                ChatMessage.TYPE_BOT));
                        chatAdapter.notifyItemInserted(messages.size() - 1);
                        chatRecyclerView.scrollToPosition(messages.size() - 1);
                    }
                });
    }

    /**
     * Displays a welcome message from the AI assistant when the chat opens.
     */
    private void addWelcomeMessage() {
        String welcome = "Hi! I'm Wanderly AI, your personal travel assistant. "
                + "Ask me anything about nearby places, travel tips, or let me help you "
                + "plan an itinerary. For example, try:\n\n"
                + "\u2022 \"What's fun to do near me?\"\n"
                + "\u2022 \"Plan a 2-hour walking tour\"\n"
                + "\u2022 \"Best restaurants nearby\"\n"
                + "\u2022 \"Tell me about local history\"";
        messages.add(new ChatMessage(welcome, ChatMessage.TYPE_BOT));
        chatAdapter.notifyItemInserted(messages.size() - 1);
    }

    /**
     * Fetches the logged-in user's place preferences from Firestore
     * so the AI can tailor its recommendations.
     */
    private void loadUserPreferences() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance()
                    .collection("UserPreferences")
                    .document(user.getUid())
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            List<String> places = (List<String>) doc.get("selectedPlaces");
                            if (places != null && !places.isEmpty()) {
                                userPreferences = TextUtils.join(", ", places);
                            }
                        }
                    });
        }
    }

    // ---- API Key Management ------------------------------------------------

    private String getApiKey() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getString(KEY_OPENAI_API, "");
    }

    private void saveApiKey(String key) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(KEY_OPENAI_API, key).apply();
    }

    /**
     * Shows a dialog where the user can enter or update their OpenAI API key.
     */
    private void showApiKeyDialog() {
        EditText input = new EditText(this);
        input.setHint("sk-...");
        input.setText(getApiKey());
        input.setPadding(48, 32, 48, 16);
        input.setSingleLine(true);

        new AlertDialog.Builder(this)
                .setTitle("OpenAI API Key")
                .setMessage("Enter your OpenAI API key to use the AI travel assistant.\n\n"
                        + "Get one at: platform.openai.com")
                .setView(input)
                .setPositiveButton("Save", (dialog, which) -> {
                    String key = input.getText().toString().trim();
                    if (!key.isEmpty()) {
                        saveApiKey(key);
                        openAIHelper = new OpenAIHelper(key);
                        if (messages.isEmpty()) {
                            addWelcomeMessage();
                        }
                        Toast.makeText(this, "API key saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "API key cannot be empty",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    if (messages.isEmpty()) {
                        messages.add(new ChatMessage(
                                "Please set your OpenAI API key to start chatting. "
                                        + "Tap the gear icon in the top-right corner.",
                                ChatMessage.TYPE_BOT));
                        chatAdapter.notifyItemInserted(messages.size() - 1);
                    }
                })
                .setCancelable(false)
                .show();
    }
}
