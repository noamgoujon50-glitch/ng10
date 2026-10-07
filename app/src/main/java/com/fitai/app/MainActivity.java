package com.fitai.app;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final String PREFS = "fitai_profile";

    private final int bgTop = Color.rgb(5, 12, 18);
    private final int bgBottom = Color.rgb(8, 19, 28);
    private final int card = Color.rgb(17, 27, 36);
    private final int cardSoft = Color.rgb(23, 38, 48);
    private final int stroke = Color.rgb(48, 74, 86);
    private final int text = Color.rgb(246, 250, 252);
    private final int muted = Color.rgb(177, 193, 201);
    private final int teal = Color.rgb(27, 214, 196);
    private final int blue = Color.rgb(45, 156, 255);
    private final int green = Color.rgb(161, 241, 83);

    private final Handler handler = new Handler(Looper.getMainLooper());

    private SharedPreferences prefs;
    private String userName = "Sportif";
    private String goal = "Prendre du muscle";
    private String level = "Débutant";
    private String frequency = "3 fois par semaine";
    private String place = "À la maison";
    private String equipment = "Haltères";
    private String sessionTime = "35 minutes";
    private String eating = "Repas irréguliers";
    private String foodChange = "Petit à petit";
    private String height = "175";
    private String weight = "75";
    private String weightTarget = "70";
    private String weightHistory = "";
    private String weightChartRange = "Semaine";
    private String gender = "Non précisé";
    private String gymStrengthProfile = "";
    private String exercisePerformanceProfile = "";
    private String accountUser = "";
    private String accountPass = "";
    private String aiServerUrl = "http://192.168.1.15:8787";
    private String serverAuthToken = "";
    private String serverUserId = "";
    private String serverPlanJson = "";
    private String serverPlanWeek = "";
    private String serverConnectionStatus = "Non testé";
    private boolean serverAiKeyConfigured = false;
    private String serverAiModel = "gpt-5";
    private String customPlanJson = "";
    private String customPlanWeek = "";
    private boolean customPlanActive = false;
    private boolean rememberConnection = false;
    private boolean tutorialSeen = false;
    private int planAdjustment = 0;
    private int xp = 0;
    private int valhallaCoins = 0;
    private String avatarType = "Viking";
    private String avatarOutfit = "Armure d'entraînement";
    private String avatarAccessory = "Aucun";
    private String avatarBackdrop = "Prairie";
    private String ownedHeroes = "Viking|Valkyrie|Magicien|Magicienne";
    private String inventoryItems = "training_outfit|bg_prairie";
    private String equippedItems = "training_outfit|bg_prairie";
    private String sessionHistory = "";
    private String trackedSessionDate = "";
    private long trackedSessionSince = 0L;
    private String sessionCoachNotice = "";
    private String battleFriends = "";
    private String battleGroups = "";
    private String battleMessages = "";
    private String battleFriendMeta = "";
    private String battleGroupMeta = "";
    private String activeBattleRoom = "";
    private String pendingGroupName = "";
    private String pendingGroupMembers = "";
    private String videoReturnRoom = "";
    private String profilePhotoUri = "";
    private String feedPhotos = "";
    private String lastPlanSurveyWeek = "";
    private String lastPlanSurveyAnswer = "";
    private boolean surveyPending = false;
    private String officialPlanWeek = "";
    private int officialPlanAdjustment = 0;
    private boolean soundEnabled = true;
    private String notificationSound = "Clair";
    private int pendingCoachPlanAdjustment = 0;
    private boolean hasPendingCoachPlan = false;
    private boolean coachWaitingForPlanFeedback = false;
    private boolean previewPlanActive = false;
    private int previewPlanAdjustment = 0;
    private String freeCoinAdDate = "";
    private int freeCoinAdsToday = 0;
    private int pendingScrollRestoreY = -1;
    private String pendingScrollRestoreScreen = "";
    private Uri selectedChallengeVideo;
    private static final int VIDEO_PICK_REQUEST = 801;
    private static final int PROFILE_PHOTO_REQUEST = 802;
    private static final int FEED_PHOTO_REQUEST = 803;
    private static final int BATTLE_PHOTO_REQUEST = 804;

    private int questionIndex = 0;
    private LinearLayout chatLog;
    private EditText chatInput;

    private final Question[] questions = new Question[] {
            new Question(
                    "goal",
                    "Quel est ton objectif principal ?",
                    "VALHALLA RAGE va construire le programme autour de cette priorité.",
                    new String[] {"Prendre du muscle", "Perdre du poids", "Être plus tonique", "Reprendre le sport"}
            ),
            new Question(
                    "level",
                    "Quel est ton niveau aujourd'hui ?",
                    "Choisis honnêtement : le programme doit rester motivant, pas impossible.",
                    new String[] {"Débutant", "Intermédiaire", "Avancé"}
            ),
            new Question(
                    "frequency",
                    "Combien de fois veux-tu t'entraîner ?",
                    "On adapte le volume de travail à ton rythme réel.",
                    new String[] {"1 à 2 fois par semaine", "3 fois par semaine", "4 fois par semaine", "5 fois ou plus"}
            ),
            new Question(
                    "place",
                    "Où fais-tu ton sport ?",
                    "Maison, salle, ou les deux : les exercices changeront selon ton environnement.",
                    new String[] {"À la maison", "En salle", "Les deux"}
            ),
            new Question(
                    "equipment",
                    "Quel matériel as-tu ?",
                    "Le coach évite les exercices impossibles à faire avec ce que tu as.",
                    new String[] {"Aucun matériel", "Haltères", "Banc + haltères", "Salle complète"}
            ),
            new Question(
                    "sessionTime",
                    "Combien de temps par séance ?",
                    "Mieux vaut un plan réaliste que parfait sur le papier.",
                    new String[] {"20 minutes", "35 minutes", "50 minutes", "60 minutes ou plus"}
            ),
            new Question(
                    "eating",
                    "Tes habitudes alimentaires ?",
                    "Ça aide VALHALLA RAGE à donner des conseils simples et progressifs.",
                    new String[] {"Plutôt équilibrées", "Repas rapides", "Sucré fréquent", "Repas irréguliers"}
            ),
            new Question(
                    "foodChange",
                    "Es-tu prêt à changer ton alimentation ?",
                    "On peut y aller fort, ou très doucement.",
                    new String[] {"Oui, je suis prêt", "Petit à petit", "Pas maintenant"}
            )
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(bgTop);
        getWindow().setNavigationBarColor(bgBottom);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        loadProfile();
        if (rememberConnection && accountUser.length() > 0 && prefs.getBoolean("onboardingDone", false)) {
            if (tutorialSeen) {
                showDashboard();
            } else {
                showTutorial(0);
            }
        } else if (rememberConnection && accountUser.length() > 0) {
            showQuestion(0);
        } else {
            showLogin();
        }
    }

    private void loadProfile() {
        userName = prefs.getString("userName", userName);
        goal = prefs.getString("goal", goal);
        level = prefs.getString("level", level);
        frequency = prefs.getString("frequency", frequency);
        place = prefs.getString("place", place);
        equipment = prefs.getString("equipment", equipment);
        sessionTime = prefs.getString("sessionTime", sessionTime);
        eating = prefs.getString("eating", eating);
        foodChange = prefs.getString("foodChange", foodChange);
        height = prefs.getString("height", height);
        weight = prefs.getString("weight", weight);
        weightTarget = prefs.getString("weightTarget", weightTarget);
        weightHistory = prefs.getString("weightHistory", weightHistory);
        weightChartRange = prefs.getString("weightChartRange", weightChartRange);
        gender = prefs.getString("gender", gender);
        gymStrengthProfile = prefs.getString("gymStrengthProfile", gymStrengthProfile);
        exercisePerformanceProfile = prefs.getString("exercisePerformanceProfile", exercisePerformanceProfile);
        accountUser = prefs.getString("accountUser", "");
        accountPass = prefs.getString("accountPass", "");
        aiServerUrl = prefs.getString("aiServerUrl", aiServerUrl);
        serverAuthToken = prefs.getString("serverAuthToken", "");
        serverUserId = prefs.getString("serverUserId", "");
        serverPlanJson = prefs.getString("serverPlanJson", "");
        serverPlanWeek = prefs.getString("serverPlanWeek", "");
        serverConnectionStatus = prefs.getString("serverConnectionStatus", serverConnectionStatus);
        serverAiKeyConfigured = prefs.getBoolean("serverAiKeyConfigured", serverAiKeyConfigured);
        serverAiModel = prefs.getString("serverAiModel", serverAiModel);
        customPlanJson = prefs.getString("customPlanJson", "");
        customPlanWeek = prefs.getString("customPlanWeek", "");
        customPlanActive = prefs.getBoolean("customPlanActive", false);
        rememberConnection = prefs.getBoolean("rememberConnection", false);
        tutorialSeen = prefs.getBoolean("tutorialSeen", false);
        planAdjustment = prefs.getInt("planAdjustment", 0);
        xp = prefs.getInt("xp", 0);
        valhallaCoins = prefs.getInt("valhallaCoins", 0);
        avatarType = prefs.getString("avatarType", avatarType);
        avatarOutfit = prefs.getString("avatarOutfit", avatarOutfit);
        avatarAccessory = prefs.getString("avatarAccessory", avatarAccessory);
        avatarBackdrop = prefs.getString("avatarBackdrop", avatarBackdrop);
        ownedHeroes = prefs.getString("ownedHeroes", ownedHeroes);
        ownedHeroes = addToken(ownedHeroes, "Viking");
        ownedHeroes = addToken(ownedHeroes, "Valkyrie");
        ownedHeroes = addToken(ownedHeroes, "Magicien");
        ownedHeroes = addToken(ownedHeroes, "Magicienne");
        inventoryItems = prefs.getString("inventoryItems", inventoryItems);
        equippedItems = prefs.getString("equippedItems", equippedItems);
        inventoryItems = addToken(inventoryItems, "bg_prairie");
        equippedItems = addToken(equippedItems, "bg_prairie");
        sessionHistory = prefs.getString("sessionHistory", "");
        trackedSessionDate = prefs.getString("trackedSessionDate", "");
        trackedSessionSince = prefs.getLong("trackedSessionSince", 0L);
        sessionCoachNotice = prefs.getString("sessionCoachNotice", "");
        battleFriends = prefs.getString("battleFriends", "");
        battleGroups = prefs.getString("battleGroups", "");
        battleMessages = prefs.getString("battleMessages", "");
        battleFriendMeta = prefs.getString("battleFriendMeta", "");
        battleGroupMeta = prefs.getString("battleGroupMeta", "");
        activeBattleRoom = prefs.getString("activeBattleRoom", "");
        profilePhotoUri = prefs.getString("profilePhotoUri", "");
        feedPhotos = prefs.getString("feedPhotos", "");
        lastPlanSurveyWeek = prefs.getString("lastPlanSurveyWeek", "");
        lastPlanSurveyAnswer = prefs.getString("lastPlanSurveyAnswer", "");
        surveyPending = prefs.getBoolean("surveyPending", false);
        officialPlanWeek = prefs.getString("officialPlanWeek", "");
        officialPlanAdjustment = prefs.getInt("officialPlanAdjustment", 0);
        soundEnabled = prefs.getBoolean("soundEnabled", true);
        notificationSound = prefs.getString("notificationSound", notificationSound);
        freeCoinAdDate = prefs.getString("freeCoinAdDate", "");
        freeCoinAdsToday = prefs.getInt("freeCoinAdsToday", 0);
        cleanAvatarEquipment();
        ensureAvatarBaseItem();
        syncLegacyAvatarLabels();
        if (accountUser.length() > 0) {
            userName = prefs.getString("userName", accountUser);
        }
    }

    private void saveProfile(boolean done) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userName", userName);
        editor.putString("goal", goal);
        editor.putString("level", level);
        editor.putString("frequency", frequency);
        editor.putString("place", place);
        editor.putString("equipment", equipment);
        editor.putString("sessionTime", sessionTime);
        editor.putString("eating", eating);
        editor.putString("foodChange", foodChange);
        editor.putString("height", height);
        editor.putString("weight", weight);
        editor.putString("weightTarget", weightTarget);
        editor.putString("weightHistory", weightHistory);
        editor.putString("weightChartRange", weightChartRange);
        editor.putString("gender", gender);
        editor.putString("gymStrengthProfile", gymStrengthProfile);
        editor.putString("exercisePerformanceProfile", exercisePerformanceProfile);
        editor.putInt("planAdjustment", planAdjustment);
        editor.putInt("xp", xp);
        editor.putInt("valhallaCoins", valhallaCoins);
        editor.putString("avatarType", avatarType);
        editor.putString("avatarOutfit", avatarOutfit);
        editor.putString("avatarAccessory", avatarAccessory);
        editor.putString("avatarBackdrop", avatarBackdrop);
        editor.putString("ownedHeroes", ownedHeroes);
        editor.putString("inventoryItems", inventoryItems);
        editor.putString("equippedItems", equippedItems);
        editor.putString("serverAuthToken", serverAuthToken);
        editor.putString("serverUserId", serverUserId);
        editor.putString("serverPlanJson", serverPlanJson);
        editor.putString("serverPlanWeek", serverPlanWeek);
        editor.putString("serverConnectionStatus", serverConnectionStatus);
        editor.putBoolean("serverAiKeyConfigured", serverAiKeyConfigured);
        editor.putString("serverAiModel", serverAiModel);
        editor.putString("customPlanJson", customPlanJson);
        editor.putString("customPlanWeek", customPlanWeek);
        editor.putBoolean("customPlanActive", customPlanActive);
        editor.putString("sessionHistory", sessionHistory);
        editor.putString("trackedSessionDate", trackedSessionDate);
        editor.putLong("trackedSessionSince", trackedSessionSince);
        editor.putString("sessionCoachNotice", sessionCoachNotice);
        editor.putString("battleFriends", battleFriends);
        editor.putString("battleGroups", battleGroups);
        editor.putString("battleMessages", battleMessages);
        editor.putString("battleFriendMeta", battleFriendMeta);
        editor.putString("battleGroupMeta", battleGroupMeta);
        editor.putString("activeBattleRoom", activeBattleRoom);
        editor.putString("profilePhotoUri", profilePhotoUri);
        editor.putString("feedPhotos", feedPhotos);
        editor.putString("lastPlanSurveyWeek", lastPlanSurveyWeek);
        editor.putString("lastPlanSurveyAnswer", lastPlanSurveyAnswer);
        editor.putBoolean("surveyPending", surveyPending);
        editor.putString("officialPlanWeek", officialPlanWeek);
        editor.putInt("officialPlanAdjustment", officialPlanAdjustment);
        editor.putBoolean("soundEnabled", soundEnabled);
        editor.putString("notificationSound", notificationSound);
        editor.putString("freeCoinAdDate", freeCoinAdDate);
        editor.putInt("freeCoinAdsToday", freeCoinAdsToday);
        editor.putBoolean("tutorialSeen", tutorialSeen);
        editor.putBoolean("onboardingDone", done);
        editor.apply();
    }

    private void saveAccount(String name, String password, boolean remember) {
        accountUser = name;
        accountPass = password;
        rememberConnection = remember;
        userName = name;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("accountUser", accountUser);
        editor.putString("accountPass", accountPass);
        editor.putString("userName", userName);
        editor.putBoolean("rememberConnection", rememberConnection);
        editor.apply();
    }

    private void saveServerSession(String name, String password, boolean remember, JSONObject result) {
        saveAccount(name, password, remember);
        serverAuthToken = result.optString("token", serverAuthToken);
        serverConnectionStatus = "Connecté";
        JSONObject user = result.optJSONObject("user");
        if (user != null) {
            serverUserId = user.optString("id", serverUserId);
            xp = Math.max(xp, user.optInt("xp", xp));
            valhallaCoins = Math.max(valhallaCoins, user.optInt("coins", valhallaCoins));
            JSONObject profile = user.optJSONObject("profile");
            if (profile != null) {
                applyServerProfile(profile);
            }
            applyServerShopState(user);
        }
        prefs.edit()
                .putString("serverAuthToken", serverAuthToken)
                .putString("serverUserId", serverUserId)
                .putString("serverConnectionStatus", serverConnectionStatus)
                .putInt("xp", xp)
                .putInt("valhallaCoins", valhallaCoins)
                .apply();
        saveAvatarLocal();
        fetchCurrentProgramFromServerAsync();
        fetchBattleSocialFromServerAsync(false);
        fetchProgressFromServerAsync();
    }

    private void applyServerProfile(JSONObject profile) {
        userName = profile.optString("userName", userName);
        goal = profile.optString("goal", goal);
        level = profile.optString("level", level);
        frequency = profile.optString("frequency", frequency);
        place = profile.optString("place", place);
        equipment = profile.optString("equipment", equipment);
        sessionTime = profile.optString("sessionTime", sessionTime);
        eating = profile.optString("eating", eating);
        foodChange = profile.optString("foodChange", foodChange);
        height = profile.optString("height", height);
        weight = profile.optString("weight", weight);
        weightTarget = profile.optString("weightTarget", weightTarget);
        gender = profile.optString("gender", gender);
        gymStrengthProfile = profile.optString("gymStrengthProfile", gymStrengthProfile);
        exercisePerformanceProfile = profile.optString("exercisePerformanceProfile", exercisePerformanceProfile);
    }

    private void applyServerShopState(JSONObject user) {
        valhallaCoins = Math.max(valhallaCoins, user.optInt("coins", valhallaCoins));
        JSONArray inventory = user.optJSONArray("inventory");
        if (inventory != null) inventoryItems = mergeTokenStrings(inventoryItems, jsonArrayToTokens(inventory));
        JSONObject avatar = user.optJSONObject("avatar");
        if (avatar != null) {
            JSONArray owned = avatar.optJSONArray("ownedHeroes");
            if (owned != null) ownedHeroes = mergeTokenStrings(ownedHeroes, jsonArrayToTokens(owned));
            String hero = avatar.optString("hero", "");
            if (hero.length() > 0) {
                ownedHeroes = addToken(ownedHeroes, hero);
                avatarType = hero;
            }
            JSONArray equipped = avatar.optJSONArray("equipped");
            if (equipped != null && equipped.length() > 0) {
                equippedItems = jsonArrayToTokens(equipped);
            }
        }
        ensureAvatarBaseItem();
        syncLegacyAvatarLabels();
    }

    private JSONArray tokenJsonArray(String source) {
        JSONArray array = new JSONArray();
        if (source == null || source.length() == 0) return array;
        String[] values = source.split("\\|");
        for (int i = 0; i < values.length; i++) {
            String value = values[i].trim();
            if (value.length() > 0) array.put(value);
        }
        return array;
    }

    private String jsonArrayToTokens(JSONArray array) {
        String result = "";
        if (array == null) return result;
        for (int i = 0; i < array.length(); i++) {
            String value = array.optString(i, "").trim();
            if (value.length() > 0) result = addToken(result, value);
        }
        return result;
    }

    private String mergeTokenStrings(String first, String second) {
        String result = first == null ? "" : first;
        if (second == null || second.length() == 0) return result;
        String[] values = second.split("\\|");
        for (int i = 0; i < values.length; i++) {
            String value = values[i].trim();
            if (value.length() > 0) result = addToken(result, value);
        }
        return result;
    }

    private void loginWithServer(final String typedName, final String typedPass, final boolean remember, final TextView status) {
        status.setText("Connexion au serveur...");
        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("username", typedName);
                    payload.put("password", typedPass);
                    final JSONObject result = callServerJson("POST", "/auth/login", payload, "");
                    handler.post(new Runnable() {
                        public void run() {
                            saveServerSession(typedName, typedPass, remember, result);
                            saveProfile(false);
                            navigateAfterLogin();
                        }
                    });
                } catch (final Exception error) {
                    handler.post(new Runnable() {
                        public void run() {
                            loginLocalFallback(typedName, typedPass, remember, status, error.getMessage());
                        }
                    });
                }
            }
        }).start();
    }

    private void loginLocalFallback(String typedName, String typedPass, boolean remember, TextView status, String serverError) {
        serverConnectionStatus = "Hors ligne";
        prefs.edit().putString("serverConnectionStatus", serverConnectionStatus).apply();
        if (accountUser.length() > 0 && (!typedName.equals(accountUser) || !typedPass.equals(accountPass))) {
            status.setText("Compte ou mot de passe incorrect. Serveur indisponible ou compte non trouvé.");
            return;
        }
        if (accountUser.length() == 0) {
            saveAccount(typedName, typedPass, remember);
        } else {
            rememberConnection = remember;
            prefs.edit().putBoolean("rememberConnection", rememberConnection).apply();
            userName = typedName;
        }
        saveProfile(false);
        status.setText("Connexion locale utilisée. Serveur : " + serverError);
        tryCreateServerAccountFromLocalLogin(typedName, typedPass, remember);
        navigateAfterLogin();
    }

    private void tryCreateServerAccountFromLocalLogin(final String typedName, final String typedPass, final boolean remember) {
        if (typedPass.length() < 6 || serverAuthToken.length() > 0) return;
        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("username", typedName);
                    payload.put("password", typedPass);
                    final JSONObject result = callServerJson("POST", "/auth/register", payload, "");
                    handler.post(new Runnable() {
                        public void run() {
                            saveServerSession(typedName, typedPass, remember, result);
                            saveProfile(true);
                            syncProfileToServerAsync();
                        }
                    });
                } catch (Exception ignored) {
                }
            }
        }).start();
    }

    private void resetPasswordWithServer(final String name, final String password, final TextView status) {
        status.setText("Réinitialisation en cours...");
        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject ask = new JSONObject();
                    ask.put("username", name);
                    JSONObject recovery = callServerJson("POST", "/auth/forgot-password", ask, "");
                    String code = recovery.optString("devResetCode", "");
                    if (code.length() == 0) {
                        throw new IllegalStateException("Code de récupération non disponible sur ce serveur.");
                    }
                    JSONObject reset = new JSONObject();
                    reset.put("username", name);
                    reset.put("code", code);
                    reset.put("newPassword", password);
                    callServerJson("POST", "/auth/reset-password", reset, "");
                    handler.post(new Runnable() {
                        public void run() {
                            accountUser = name;
                            accountPass = password;
                            prefs.edit().putString("accountUser", accountUser).putString("accountPass", accountPass).apply();
                            status.setText("Mot de passe modifié sur le serveur. Tu peux maintenant te connecter.");
                            handler.postDelayed(new Runnable() {
                                public void run() { showLogin(); }
                            }, 800);
                        }
                    });
                } catch (final Exception error) {
                    handler.post(new Runnable() {
                        public void run() {
                            if (accountUser.length() > 0 && name.equals(accountUser)) {
                                accountPass = password;
                                prefs.edit().putString("accountPass", accountPass).apply();
                                status.setText("Serveur indisponible : mot de passe modifié en local.");
                                handler.postDelayed(new Runnable() {
                                    public void run() { showLogin(); }
                                }, 900);
                            } else {
                                status.setText("Impossible de réinitialiser : " + error.getMessage());
                            }
                        }
                    });
                }
            }
        }).start();
    }

    private void signupWithServer(final String typedName, final String typedPass, final boolean remember, final TextView status) {
        status.setText("Création du compte sur le serveur...");
        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("username", typedName);
                    payload.put("password", typedPass);
                    final JSONObject result = callServerJson("POST", "/auth/register", payload, "");
                    handler.post(new Runnable() {
                        public void run() {
                            saveServerSession(typedName, typedPass, remember, result);
                            prefs.edit().putBoolean("onboardingDone", false).apply();
                            showQuestion(0);
                        }
                    });
                } catch (final Exception error) {
                    handler.post(new Runnable() {
                        public void run() {
                            serverConnectionStatus = "Hors ligne";
                            prefs.edit().putString("serverConnectionStatus", serverConnectionStatus).apply();
                            saveAccount(typedName, typedPass, remember);
                            prefs.edit().putBoolean("onboardingDone", false).apply();
                            status.setText("Serveur indisponible : compte créé en local pour continuer le test.");
                            handler.postDelayed(new Runnable() {
                                public void run() { showQuestion(0); }
                            }, 900);
                        }
                    });
                }
            }
        }).start();
    }

    private void navigateAfterLogin() {
        if (prefs.getBoolean("onboardingDone", false)) {
            if (tutorialSeen) {
                showDashboard();
            } else {
                showTutorial(0);
            }
        } else {
            showQuestion(0);
        }
    }

    private void showLogin() {
        LinearLayout body = page();
        body.setGravity(Gravity.CENTER_HORIZONTAL);
        body.setPadding(dp(24), dp(30), dp(24), dp(28));

        body.addView(mascot(dp(205)));
        body.addView(space(8));
        body.addView(kicker("VALHALLA RAGE"));
        body.addView(title("Connecte-toi"));
        body.addView(subtitle("Entre ton compte VALHALLA RAGE, ou crée ton espace sportif en quelques secondes."));

        final EditText nameInput = input("Utilisateur", false, false);
        final EditText passwordInput = input("Mot de passe", true, false);
        if (rememberConnection && accountUser.length() > 0) {
            nameInput.setText(accountUser);
        }
        final CheckBox remember = rememberBox("Enregistrer la connexion pour la prochaine fois");
        remember.setChecked(rememberConnection);
        final TextView status = small("");
        body.addView(space(18));
        addWithMargins(body, nameInput, 0, 0, 0, 10);
        addWithMargins(body, passwordInput, 0, 0, 0, 10);
        addWithMargins(body, remember, 0, 0, 0, 10);
        addWithMargins(body, status, 0, 0, 0, 14);

        Button login = primaryButton("Se connecter");
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String typedName = nameInput.getText().toString().trim();
                String typedPass = passwordInput.getText().toString();
                if (typedName.length() == 0 || typedPass.length() == 0) {
                    status.setText("Entre ton utilisateur et ton mot de passe.");
                    return;
                }
                loginWithServer(typedName, typedPass, remember.isChecked(), status);
            }
        });
        addWithMargins(body, login, 0, 0, 0, 10);

        Button signup = secondaryButton("S'inscrire");
        signup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showSignup();
            }
        });
        addWithMargins(body, signup, 0, 0, 0, 12);

        Button forgot = quietButton("Mot de passe oublié ?");
        forgot.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showForgotPassword();
            }
        });
        body.addView(forgot);

        body.addView(small("Prototype local : tes réponses restent dans l'application de test."));
        mount(body, false, "");
    }

    private void showForgotPassword() {
        LinearLayout body = page();
        body.setGravity(Gravity.CENTER_HORIZONTAL);
        body.setPadding(dp(24), dp(42), dp(24), dp(28));
        body.addView(kicker("ACCÈS AU COMPTE"));
        body.addView(title("Réinitialiser ton mot de passe"));
        body.addView(subtitle("Sur cette version de test, la récupération se fait localement avec ton nom d'utilisateur."));
        body.addView(space(18));

        final EditText nameInput = input("Nom d'utilisateur", false, false);
        final EditText passwordInput = input("Nouveau mot de passe", true, false);
        final EditText confirmInput = input("Confirmer le mot de passe", true, false);
        final TextView status = small("");
        addWithMargins(body, nameInput, 0, 0, 0, 10);
        addWithMargins(body, passwordInput, 0, 0, 0, 10);
        addWithMargins(body, confirmInput, 0, 0, 0, 12);
        addWithMargins(body, status, 0, 0, 0, 14);

        Button reset = primaryButton("Enregistrer le nouveau mot de passe");
        reset.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = nameInput.getText().toString().trim();
                String password = passwordInput.getText().toString();
                String confirm = confirmInput.getText().toString();
                if (accountUser.length() == 0 || !name.equals(accountUser)) {
                    status.setText("Ce nom d'utilisateur ne correspond pas au compte enregistré sur ce téléphone.");
                    return;
                }
                if (password.length() < 4 || !password.equals(confirm)) {
                    status.setText("Choisis un mot de passe d'au moins 4 caractères et confirme-le correctement.");
                    return;
                }
                resetPasswordWithServer(name, password, status);
            }
        });
        addWithMargins(body, reset, 0, 0, 0, 10);

        Button back = quietButton("Retour à la connexion");
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showLogin(); }
        });
        body.addView(back);
        mount(body, false, "");
    }

    private void showSignup() {
        LinearLayout body = page();
        body.setGravity(Gravity.CENTER_HORIZONTAL);
        body.setPadding(dp(24), dp(30), dp(24), dp(28));

        body.addView(mascot(dp(190)));
        body.addView(space(8));
        body.addView(kicker("NOUVEAU COMPTE"));
        body.addView(title("Crée ton accès"));
        body.addView(subtitle("Choisis un nom d'utilisateur et un mot de passe. Ensuite VALHALLA RAGE te pose les questions pour générer ton programme."));

        final EditText nameInput = input("Nom d'utilisateur", false, false);
        final EditText passwordInput = input("Mot de passe", true, false);
        final EditText confirmInput = input("Confirmer le mot de passe", true, false);
        final CheckBox remember = rememberBox("Enregistrer la connexion pour la prochaine fois");
        remember.setChecked(true);
        final TextView status = small("");

        body.addView(space(18));
        addWithMargins(body, nameInput, 0, 0, 0, 10);
        addWithMargins(body, passwordInput, 0, 0, 0, 10);
        addWithMargins(body, confirmInput, 0, 0, 0, 10);
        addWithMargins(body, remember, 0, 0, 0, 10);
        addWithMargins(body, status, 0, 0, 0, 14);

        Button create = primaryButton("Créer mon compte");
        create.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String typedName = nameInput.getText().toString().trim();
                String typedPass = passwordInput.getText().toString();
                String confirm = confirmInput.getText().toString();
                if (typedName.length() < 3) {
                    status.setText("Choisis un nom d'utilisateur d'au moins 3 caractères.");
                    return;
                }
                if (typedPass.length() < 6) {
                    status.setText("Choisis un mot de passe d'au moins 6 caractères.");
                    return;
                }
                if (!typedPass.equals(confirm)) {
                    status.setText("Les deux mots de passe ne sont pas identiques.");
                    return;
                }
                signupWithServer(typedName, typedPass, remember.isChecked(), status);
            }
        });
        addWithMargins(body, create, 0, 0, 0, 10);

        Button back = quietButton("J'ai déjà un compte");
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showLogin();
            }
        });
        body.addView(back);
        mount(body, false, "");
    }

    private void showQuestion(int index) {
        questionIndex = index;
        final Question question = questions[index];

        LinearLayout body = page();
        body.setPadding(dp(24), dp(30), dp(24), dp(28));
        body.addView(kicker("QUESTION " + (index + 1) + " / " + (questions.length + 1)));
        body.addView(progress(index + 1, questions.length + 1));
        body.addView(space(18));
        body.addView(title(question.title));
        body.addView(subtitle(question.subtitle));
        body.addView(space(18));

        for (int i = 0; i < question.options.length; i++) {
            final String option = question.options[i];
            Button choice = optionButton(option);
            choice.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    saveAnswer(question.key, option);
                    if (questionIndex + 1 < questions.length) {
                        showQuestion(questionIndex + 1);
                    } else {
                        showBodyQuestion();
                    }
                }
            });
            addWithMargins(body, choice, 0, 0, 0, 10);
        }

        if (index > 0) {
            Button back = quietButton("Retour");
            back.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showQuestion(questionIndex - 1);
                }
            });
            addWithMargins(body, back, 0, 8, 0, 0);
        }
        mount(body, false, "");
    }

    private boolean needsGymStrengthProfile() {
        String placeText = place == null ? "" : place.toLowerCase(Locale.FRANCE);
        String equipmentText = equipment == null ? "" : equipment.toLowerCase(Locale.FRANCE);
        return placeText.indexOf("salle") >= 0 || equipmentText.indexOf("salle") >= 0;
    }

    private String[] gymExerciseNames() {
        return new String[] {
                "Développé couché",
                "Développé incliné",
                "Presse à cuisses",
                "Squat guidé",
                "Soulevé de terre roumain",
                "Tirage vertical",
                "Tirage horizontal",
                "Développé épaules machine",
                "Leg curl",
                "Leg extension",
                "Curl biceps machine",
                "Extension triceps poulie",
                "Élévations latérales",
                "Pec deck",
                "Hip thrust",
                "Mollets à la machine",
                "Abdos à la machine",
                "Rowing machine"
        };
    }

    private void showGymStrengthQuestion() {
        LinearLayout body = page();
        body.setPadding(dp(20), dp(24), dp(20), dp(28));
        body.addView(kicker("REPÈRES DE FORCE"));
        body.addView(progress(5, questions.length + 1));
        body.addView(space(16));
        body.addView(title("Tes charges en salle"));
        body.addView(subtitle("Pour chaque exercice que tu connais, indique la charge utilisée et le nombre de répétitions maximum avant l'échec. Tu peux laisser vide ce que tu ne pratiques pas encore."));
        body.addView(space(12));

        final ArrayList<EditText> loadInputs = new ArrayList<EditText>();
        final ArrayList<EditText> repInputs = new ArrayList<EditText>();
        final String[] names = gymExerciseNames();
        for (int i = 0; i < names.length; i++) {
            LinearLayout row = card();
            row.setPadding(dp(12), dp(12), dp(12), dp(12));
            row.addView(sectionTitle(names[i]));
            LinearLayout fields = new LinearLayout(this);
            fields.setOrientation(LinearLayout.HORIZONTAL);
            final EditText load = input("Charge (kg)", false, true);
            final EditText reps = input("Échec (reps)", false, true);
            fields.addView(load, new LinearLayout.LayoutParams(0, dp(56), 1));
            LinearLayout.LayoutParams repsParams = new LinearLayout.LayoutParams(0, dp(56), 1);
            repsParams.setMargins(dp(8), 0, 0, 0);
            fields.addView(reps, repsParams);
            row.addView(fields);
            addWithMargins(body, row, 0, 0, 0, 8);
            loadInputs.add(load);
            repInputs.add(reps);
        }

        Button next = primaryButton("Enregistrer mes repères");
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StringBuilder profile = new StringBuilder();
                for (int i = 0; i < names.length; i++) {
                    String load = loadInputs.get(i).getText().toString().trim();
                    String reps = repInputs.get(i).getText().toString().trim();
                    if (load.length() == 0 || reps.length() == 0) continue;
                    if (profile.length() > 0) profile.append(";");
                    profile.append(names[i]).append("|").append(load).append("|").append(reps);
                }
                gymStrengthProfile = profile.toString();
                saveProfile(false);
                if (questionIndex + 1 < questions.length) {
                    showQuestion(questionIndex + 1);
                } else {
                    showBodyQuestion();
                }
            }
        });
        addWithMargins(body, next, 0, 10, 0, 10);

        Button skip = quietButton("Je renseignerai plus tard");
        skip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gymStrengthProfile = "";
                if (questionIndex + 1 < questions.length) showQuestion(questionIndex + 1); else showBodyQuestion();
            }
        });
        body.addView(skip);
        mount(body, false, "");
    }

    private void showBodyQuestion() {
        LinearLayout body = page();
        body.setPadding(dp(24), dp(30), dp(24), dp(28));
        body.addView(kicker("DERNIÈRE ÉTAPE"));
        body.addView(progress(questions.length + 1, questions.length + 2));
        body.addView(space(18));
        body.addView(title("Ton profil physique"));
        body.addView(subtitle("Ces infos servent à doser le programme. Tu pourras les modifier plus tard."));
        body.addView(space(18));

        body.addView(sectionLabel("Taille"));
        final EditText heightInput = input("Taille en cm", false, true);
        heightInput.setText(height);
        addWithMargins(body, heightInput, 0, 0, 0, 10);

        body.addView(sectionLabel("Poids actuel"));
        final EditText weightInput = input("Poids en kg", false, true);
        weightInput.setText(weight);
        addWithMargins(body, weightInput, 0, 0, 0, 16);

        body.addView(sectionLabel("Objectif de poids"));
        final EditText targetWeightInput = input("Objectif de poids en kg", false, true);
        targetWeightInput.setText(weightTarget);
        addWithMargins(body, targetWeightInput, 0, 0, 0, 16);

        body.addView(sectionLabel("Genre"));
        final Button woman = optionButton("Femme");
        final Button man = optionButton("Homme");
        final Button other = optionButton("Autre / non précisé");
        final Button[] genderButtons = new Button[] {woman, man, other};
        setSelectedGender(genderButtons, gender);

        View.OnClickListener genderClick = new View.OnClickListener() {
            public void onClick(View v) {
                gender = ((Button) v).getText().toString();
                setSelectedGender(genderButtons, gender);
            }
        };
        woman.setOnClickListener(genderClick);
        man.setOnClickListener(genderClick);
        other.setOnClickListener(genderClick);
        addWithMargins(body, woman, 0, 4, 0, 8);
        addWithMargins(body, man, 0, 0, 0, 8);
        addWithMargins(body, other, 0, 0, 0, 18);

        Button generate = primaryButton("Renseigner mes performances");
        generate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (heightInput.getText().toString().trim().length() > 0) {
                    height = heightInput.getText().toString().trim();
                }
                if (weightInput.getText().toString().trim().length() > 0) {
                    weight = weightInput.getText().toString().trim();
                }
                if (targetWeightInput.getText().toString().trim().length() > 0) {
                    weightTarget = targetWeightInput.getText().toString().trim();
                }
                saveProfile(false);
                showPerformanceQuestion();
            }
        });
        addWithMargins(body, generate, 0, 0, 0, 10);

        Button back = quietButton("Retour");
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showQuestion(questions.length - 1);
            }
        });
        body.addView(back);
        mount(body, false, "");
    }

    private String[] performanceExerciseNames() {
        ArrayList<String> names = new ArrayList<String>();
        String[] common = new String[] {
                "Pompes",
                "Pompes murales",
                "Pompes inclinées",
                "Pompes contrôlées",
                "Pompes tempo lentes",
                "Pompes serrées",
                "Dips",
                "Tractions",
                "Tirage vertical",
                "Tirage horizontal",
                "Rowing haltères",
                "Rowing un bras",
                "Rowing avec sac",
                "Développé épaules",
                "Pike push-up",
                "Curl biceps",
                "Extension triceps",
                "Élévations latérales",
                "Développé couché",
                "Développé incliné",
                "Squat poids du corps",
                "Squat assisté",
                "Squat haltères",
                "Squat tempo",
                "Presse à cuisses",
                "Fentes alternées",
                "Fentes arrière",
                "Soulevé de terre roumain",
                "Pont fessier",
                "Hip thrust",
                "Mollets debout",
                "Gainage face",
                "Gainage dynamique",
                "Planche latérale",
                "Mountain climbers",
                "Crunch contrôlé",
                "Chaise au mur",
                "Farmer walk",
                "Burpees",
                "Marche rapide",
                "Course",
                "Vélo"
        };
        String[] gym = gymExerciseNames();
        for (int i = 0; i < common.length; i++) if (!names.contains(common[i])) names.add(common[i]);
        for (int i = 0; i < gym.length; i++) if (!names.contains(gym[i])) names.add(gym[i]);
        return names.toArray(new String[names.size()]);
    }

    private String[] coachExerciseNames() {
        ArrayList<String> names = new ArrayList<String>();
        String[] home = new String[] {
                "Pompes murales",
                "Pompes inclinées",
                "Pompes",
                "Pompes serrées",
                "Pike push-up",
                "Dips",
                "Tractions",
                "Rowing avec sac",
                "Rowing haltères",
                "Curl biceps",
                "Extension triceps",
                "Élévations latérales",
                "Squat poids du corps",
                "Squat haltères",
                "Fentes arrière",
                "Fentes alternées",
                "Soulevé de terre roumain",
                "Pont fessier",
                "Mollets debout",
                "Gainage face",
                "Planche latérale",
                "Gainage dynamique",
                "Crunch contrôlé",
                "Mountain climbers",
                "Chaise au mur",
                "Farmer walk",
                "Burpees",
                "Marche rapide",
                "Course",
                "Mobilité épaules et hanches",
                "Étirements bas du corps"
        };
        String[] gym = gymExerciseNames();
        for (int i = 0; i < home.length; i++) if (!names.contains(home[i])) names.add(home[i]);
        for (int i = 0; i < gym.length; i++) if (!names.contains(gym[i])) names.add(gym[i]);
        return names.toArray(new String[names.size()]);
    }

    private String fullExerciseAdvice(String exercise) {
        return "Conseil exercice : " + exercise + "\n\n"
                + "Muscles travaillés : " + musclesForExercise(exercise) + "\n\n"
                + "Posture : " + postureForExercise(exercise) + "\n\n"
                + "À surveiller : " + warningForExercise(exercise) + "\n\n"
                + "Astuce du coach : " + extraTipForExercise(exercise) + "\n\n"
                + "Repère simple : " + techniqueTipForExercise(exercise);
    }

    private String musclesForExercise(String exercise) {
        String lower = exercise.toLowerCase(Locale.FRANCE);
        if (lower.indexOf("pompe") >= 0 || lower.indexOf("dips") >= 0 || lower.indexOf("développé") >= 0 || lower.indexOf("developpe") >= 0 || lower.indexOf("pec") >= 0) return "pectoraux, triceps, avant des épaules, gainage.";
        if (lower.indexOf("tirage") >= 0 || lower.indexOf("rowing") >= 0 || lower.indexOf("traction") >= 0) return "dos, dorsaux, milieu du dos, biceps, arrière des épaules.";
        if (lower.indexOf("curl") >= 0 || lower.indexOf("biceps") >= 0) return "biceps, avant-bras, gainage léger.";
        if (lower.indexOf("triceps") >= 0 || lower.indexOf("extension") >= 0) return "triceps, stabilité des épaules, gainage.";
        if (lower.indexOf("épaule") >= 0 || lower.indexOf("epaule") >= 0 || lower.indexOf("latérales") >= 0 || lower.indexOf("laterales") >= 0 || lower.indexOf("pike") >= 0) return "épaules, triceps, haut des pectoraux, trapèzes en soutien.";
        if (lower.indexOf("squat") >= 0 || lower.indexOf("presse") >= 0 || lower.indexOf("fente") >= 0 || lower.indexOf("leg extension") >= 0 || lower.indexOf("chaise") >= 0) return "quadriceps, fessiers, adducteurs, gainage.";
        if (lower.indexOf("soulevé") >= 0 || lower.indexOf("souleve") >= 0 || lower.indexOf("leg curl") >= 0 || lower.indexOf("hip thrust") >= 0 || lower.indexOf("pont") >= 0) return "ischios, fessiers, bas du dos en stabilisation.";
        if (lower.indexOf("mollet") >= 0) return "mollets, cheville, stabilité du pied.";
        if (lower.indexOf("gainage") >= 0 || lower.indexOf("planche") >= 0) return "abdominaux profonds, transverse, épaules, fessiers.";
        if (lower.indexOf("crunch") >= 0 || lower.indexOf("abdos") >= 0) return "grand droit de l'abdomen, obliques en soutien.";
        if (lower.indexOf("mountain") >= 0 || lower.indexOf("burpee") >= 0) return "cardio, abdos, épaules, jambes.";
        if (lower.indexOf("marche") >= 0 || lower.indexOf("course") >= 0 || lower.indexOf("vélo") >= 0 || lower.indexOf("velo") >= 0) return "cardio, jambes, mollets, endurance.";
        return "muscles principaux de l'exercice, gainage et muscles stabilisateurs.";
    }

    private String postureForExercise(String exercise) {
        String lower = exercise.toLowerCase(Locale.FRANCE);
        if (lower.indexOf("curl") >= 0) return "dos droit, épaules basses, coudes proches du corps, poignets neutres.";
        if (lower.indexOf("pompe") >= 0) return "corps en ligne droite, ventre serré, mains stables, épaules loin des oreilles.";
        if (lower.indexOf("squat") >= 0 || lower.indexOf("presse") >= 0) return "pieds stables, genoux dans l'axe des pieds, dos neutre, descente contrôlée.";
        if (lower.indexOf("tirage") >= 0 || lower.indexOf("rowing") >= 0) return "buste stable, dos neutre, épaules basses, tire les coudes vers les hanches.";
        if (lower.indexOf("développé") >= 0 || lower.indexOf("developpe") >= 0) return "omoplates stables, poignets droits, trajectoire régulière, descente contrôlée.";
        if (lower.indexOf("gainage") >= 0 || lower.indexOf("planche") >= 0) return "épaules, bassin et chevilles alignés, fessiers serrés, respiration calme.";
        if (lower.indexOf("fente") >= 0) return "pas assez long, buste droit, genou avant dans l'axe, appui dans le talon avant.";
        return techniqueTipForExercise(exercise);
    }

    private String warningForExercise(String exercise) {
        String lower = exercise.toLowerCase(Locale.FRANCE);
        if (lower.indexOf("curl") >= 0) return "ne balance pas le buste et ne casse pas les poignets.";
        if (lower.indexOf("presse") >= 0) return "ne verrouille pas violemment les genoux et ne laisse pas le bassin décoller.";
        if (lower.indexOf("squat") >= 0 || lower.indexOf("fente") >= 0) return "si les genoux rentrent ou si le dos s'arrondit, baisse la difficulté.";
        if (lower.indexOf("développé") >= 0 || lower.indexOf("developpe") >= 0) return "si l'épaule pince, réduis l'amplitude ou la charge.";
        if (lower.indexOf("gainage") >= 0 || lower.indexOf("planche") >= 0) return "arrête avant que le bas du dos s'affaisse.";
        if (lower.indexOf("tirage") >= 0 || lower.indexOf("rowing") >= 0) return "évite l'élan et ne tire pas avec le cou.";
        return "la série doit s'arrêter dès que la posture devient sale ou douloureuse.";
    }

    private String extraTipForExercise(String exercise) {
        String lower = exercise.toLowerCase(Locale.FRANCE);
        if (lower.indexOf("curl") >= 0) return "appuie ton dos contre un mur ou un poteau pour éviter de tricher, puis ralentis la descente.";
        if (lower.indexOf("pompe") >= 0) return "si c'est trop dur, mets les mains sur une table ou un banc et garde le même gainage.";
        if (lower.indexOf("squat") >= 0) return "utilise une chaise derrière toi pour apprendre la profondeur sans perdre l'équilibre.";
        if (lower.indexOf("fente") >= 0) return "commence par des fentes arrière : elles sont souvent plus faciles à contrôler.";
        if (lower.indexOf("tirage") >= 0 || lower.indexOf("rowing") >= 0) return "marque une micro-pause quand les omoplates sont serrées.";
        if (lower.indexOf("gainage") >= 0) return "filme-toi de côté : tu dois voir une ligne épaules-bassin-chevilles.";
        if (lower.indexOf("course") >= 0) return "garde une allure où tu peux parler, puis augmente la durée avant la vitesse.";
        return "ralentis la phase de descente : c'est souvent là que tu gagnes le plus de contrôle.";
    }

    private void showPerformanceQuestion() {
        LinearLayout body = page();
        body.setPadding(dp(20), dp(24), dp(20), dp(28));
        body.addView(kicker("QUESTION " + (questions.length + 2) + " / " + (questions.length + 2)));
        body.addView(progress(questions.length + 2, questions.length + 2));
        body.addView(space(16));
        body.addView(title("Tes performances actuelles"));
        body.addView(subtitle("Choisis les exercices que tu connais. Pour chacun, indique ton poids soulevé quand il y en a un, puis soit ton maximum total, soit tes répétitions et séries à l'échec. Tu peux t'arrêter quand la liste est assez précise pour toi."));
        body.addView(space(12));

        LinearLayout entry = cardAccent();
        entry.addView(sectionTitle("Ajouter un exercice"));
        final Spinner exerciseSpinner = new Spinner(this);
        String[] exerciseNames = performanceExerciseNames();
        String[] spinnerNames = new String[exerciseNames.length + 1];
        spinnerNames[0] = "Choisir un exercice";
        System.arraycopy(exerciseNames, 0, spinnerNames, 1, exerciseNames.length);
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseSpinner.setAdapter(adapter);
        entry.addView(exerciseSpinner, new LinearLayout.LayoutParams(-1, dp(54)));

        final EditText loadInput = input("Poids soulevé en kg", false, true);
        final EditText totalInput = input("Maximum total de répétitions", false, true);
        final EditText repsInput = input("Répétitions par série", false, true);
        final EditText seriesInput = input("Nombre de séries", false, true);
        addWithMargins(entry, loadInput, 0, 12, 0, 8);
        addWithMargins(entry, totalInput, 0, 0, 0, 6);
        TextView orLabel = centerText("OU", 13, teal, Typeface.BOLD);
        addWithMargins(entry, orLabel, 0, 0, 0, 6);
        addWithMargins(entry, repsInput, 0, 0, 0, 8);
        addWithMargins(entry, seriesInput, 0, 0, 0, 8);
        entry.addView(centerText("Pour les exercices avec haltères, barre ou machine, le poids soulevé est obligatoire pour calculer une charge précise.", 12, muted, Typeface.NORMAL));

        final TextView entryStatus = small("");
        addWithMargins(entry, entryStatus, 0, 8, 0, 8);
        Button validate = primaryButton("Valider cet exercice");
        validate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int selected = exerciseSpinner.getSelectedItemPosition();
                if (selected <= 0) {
                    entryStatus.setText("Choisis d'abord un exercice dans la liste.");
                    return;
                }
                String total = totalInput.getText().toString().trim();
                String reps = repsInput.getText().toString().trim();
                String series = seriesInput.getText().toString().trim();
                String load = loadInput.getText().toString().trim();
                boolean totalMode = total.length() > 0;
                boolean seriesMode = reps.length() > 0 && series.length() > 0;
                if (!totalMode && !seriesMode) {
                    entryStatus.setText("Indique un maximum total, ou les répétitions et les séries.");
                    return;
                }
                if (totalMode && seriesMode) {
                    entryStatus.setText("Choisis un seul format pour cette ligne.");
                    return;
                }
                if (isWeightedExerciseName(exerciseNames[selected - 1]) && load.length() == 0) {
                    entryStatus.setText("Ajoute le poids soulevé pour cet exercice.");
                    return;
                }
                try {
                    if (load.length() > 0 && Float.parseFloat(load.replace(",", ".")) <= 0f) {
                        throw new NumberFormatException();
                    }
                    if (totalMode) {
                        if (Integer.parseInt(total) <= 0) throw new NumberFormatException();
                    } else if (Integer.parseInt(reps) <= 0 || Integer.parseInt(series) <= 0) {
                        throw new NumberFormatException();
                    }
                } catch (NumberFormatException error) {
                    entryStatus.setText("Utilise uniquement des nombres positifs.");
                    return;
                }
                savePerformanceEntry(exerciseNames[selected - 1], total, reps, series, load);
                showPerformanceQuestion();
            }
        });
        entry.addView(validate);
        addWithMargins(body, entry, 0, 0, 0, 14);

        LinearLayout saved = card();
        saved.addView(sectionTitle("Exercices validés"));
        addSavedPerformanceRows(saved);
        addWithMargins(body, saved, 0, 0, 0, 14);

        Button generate = primaryButton("Générer mon programme adapté");
        generate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                saveProfile(true);
                showLoading();
            }
        });
        addWithMargins(body, generate, 0, 0, 0, 10);

        final boolean onboardingDone = prefs.getBoolean("onboardingDone", false);
        Button back = quietButton(onboardingDone ? "Retour accueil" : "Modifier mon profil physique");
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (onboardingDone) showDashboard(); else showBodyQuestion();
            }
        });
        body.addView(back);
        mount(body, false, "");
    }

    private void savePerformanceEntry(String name, String total, String reps, String series, String load) {
        StringBuilder result = new StringBuilder();
        String[] entries = exercisePerformanceProfile.length() == 0 ? new String[0] : exercisePerformanceProfile.split(";");
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].length() == 0) continue;
            String[] parts = entries[i].split("\\|", -1);
            if (parts.length > 0 && name.equalsIgnoreCase(parts[0])) continue;
            if (result.length() > 0) result.append(";");
            result.append(entries[i]);
        }
        if (result.length() > 0) result.append(";");
        result.append(name).append("|").append(total).append("|").append(reps).append("|").append(series).append("|").append(load);
        exercisePerformanceProfile = result.toString();
        saveProfile(false);
    }

    private void addSavedPerformanceRows(LinearLayout parent) {
        if (exercisePerformanceProfile.length() == 0) {
            parent.addView(centerText("Aucun exercice ajouté pour le moment. Tu peux aussi générer un programme sans les renseigner.", 14, muted, Typeface.NORMAL));
            return;
        }
        String[] entries = exercisePerformanceProfile.split(";");
        for (int i = 0; i < entries.length; i++) {
            String[] parts = entries[i].split("\\|", -1);
            if (parts.length < 4) continue;
            String value;
            if (parts[1].length() > 0) {
                value = "Maximum total : " + parts[1] + " répétitions";
            } else {
                value = parts[2] + " répétitions x " + parts[3] + " séries";
            }
            if (parts.length >= 5 && parts[4].length() > 0) {
                value += " • " + parts[4].replace(".", ",") + " kg";
            }
            parent.addView(infoLine(parts[0], value));
        }
    }

    private void showLoading() {
        LinearLayout body = page();
        body.setGravity(Gravity.CENTER_HORIZONTAL);
        body.setPadding(dp(24), dp(48), dp(24), dp(28));
        body.addView(mascot(dp(270)));
        body.addView(space(12));
        body.addView(title("Je prépare ton plan"));
        body.addView(subtitle("Objectif, niveau, matériel, temps et alimentation : tout est pris en compte."));
        body.addView(space(18));
        ProgressBar loader = new ProgressBar(this);
        body.addView(loader, new LinearLayout.LayoutParams(dp(58), dp(58)));
        mount(body, false, "");

        syncProfileToServerAsync();
        requestServerProgramAsync();

        handler.postDelayed(new Runnable() {
            public void run() {
                if (tutorialSeen) {
                    showDashboard();
                } else {
                    showTutorial(0);
                }
            }
        }, 1350);
    }

    private void showTutorial(final int step) {
        final String[] titles = new String[] {
                "Bienvenue dans VALHALLA RAGE",
                "L'accueil",
                "Ton tableau",
                "Valider une séance",
                "Le coach IA",
                "XP et profil"
        };
        final String[] texts = new String[] {
                "Je suis ton viking coach. Je t'aide à comprendre l'app, suivre tes séances et progresser sans te perdre.",
                "Sur l'accueil, tu vois la séance du jour. Le bouton Voir mon tableau affiche la semaine complète. Le bouton Valider ma séance du jour sert à dire ce que tu as vraiment fait.",
                "Dans Plan, chaque jour affiche les exercices, les séries, les répétitions et les temps de repos. Le tableau change selon ton niveau, ton matériel, ton objectif et les retours du coach.",
                "Quand tu valides, tu peux confirmer toute la séance, supprimer un exercice non fait, ou modifier les répétitions réalisées. Ensuite VALHALLA RAGE adapte la suite si besoin.",
                "Dans Coach, tu peux demander des conseils, dire qu'un exercice est trop dur ou trop facile, ou demander une modification du programme.",
                "Chaque séance validée donne de l'XP. Une séance complète donne plus d'XP. Dans Profil, tu retrouves tes infos perso, ton mot de passe et le serveur IA."
        };

        LinearLayout body = page();
        body.setGravity(Gravity.CENTER_HORIZONTAL);
        body.setPadding(dp(24), dp(28), dp(24), dp(28));
        body.addView(mascot(dp(220)));
        body.addView(kicker("TUTORIEL " + (step + 1) + " / " + titles.length));
        body.addView(progress(step + 1, titles.length));
        body.addView(space(18));
        body.addView(title(titles[step]));
        body.addView(subtitle(texts[step]));
        addWithMargins(body, tutorialPointer(step), 0, 16, 0, 0);
        body.addView(space(22));

        Button next = primaryButton(step == titles.length - 1 ? "Commencer VALHALLA RAGE" : "Suivant");
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (step + 1 < titles.length) {
                    showTutorial(step + 1);
                } else {
                    tutorialSeen = true;
                    prefs.edit().putBoolean("tutorialSeen", true).apply();
                    showDashboard();
                }
            }
        });
        addWithMargins(body, next, 0, 0, 0, 10);

        Button skip = quietButton("Passer le tutoriel");
        skip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tutorialSeen = true;
                prefs.edit().putBoolean("tutorialSeen", true).apply();
                showDashboard();
            }
        });
        body.addView(skip);
        mount(body, false, "");
    }

    private LinearLayout tutorialPointer(int step) {
        String pointer;
        if (step == 0) {
            pointer = "\u2191  Ton viking coach est ici";
        } else if (step == 1) {
            pointer = "\u2193  Accueil : séance du jour et validation";
        } else if (step == 2) {
            pointer = "\u2192  Onglet Plan : tableau de la semaine";
        } else if (step == 3) {
            pointer = "\u2192  Accueil : Valider ma séance du jour";
        } else if (step == 4) {
            pointer = "\u2192  Onglet Coach : parler à l'IA";
        } else {
            pointer = "\u2192  Profil : XP, avatar et réglages";
        }
        LinearLayout target = cardAccent();
        target.addView(centerText(pointer, 15, text, Typeface.BOLD));
        return target;
    }

    private void showDashboard() {
        processSessionDeadline();
        trackCurrentSessionDeadline();
        saveProfile(true);
        LinearLayout body = page();
        body.setPadding(dp(20), dp(22), dp(20), dp(22));

        body.addView(header("Bonjour " + userName, "Ton plan est prêt"));

        LinearLayout hero = card();
        hero.addView(avatarStage(dp(220)));
        hero.addView(sectionTitle("Séance du jour"));
        if ("complete".equals(historyStatus(dateKey(Calendar.getInstance())))) {
            hero.addView(centerText("La séance d'aujourd'hui a été complétée. Félicitations !", 15, green, Typeface.BOLD));
            hero.addView(space(6));
        }
        hero.addView(centerText(todayWorkout(), 16, text, Typeface.BOLD));
        hero.addView(space(6));
        hero.addView(centerText("Repos conseillé : " + restTime() + " entre les séries", 14, muted, Typeface.NORMAL));
        hero.addView(centerText(adjustmentLabel(), 13, planAdjustment == 0 ? muted : green, Typeface.BOLD));
        Button start = primaryButton("Voir mon tableau");
        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showPlan();
            }
        });
        addWithMargins(hero, start, 0, 16, 0, 0);

        Button validate = secondaryButton("Valider ma séance du jour");
        validate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showSessionValidation();
            }
        });
        addWithMargins(hero, validate, 0, 10, 0, 0);
        addWithMargins(body, hero, 0, 12, 0, 14);

        if (sessionCoachNotice.length() > 0) {
            LinearLayout notice = cardAccent();
            notice.addView(sectionTitle("Message du coach"));
            notice.addView(centerText(sessionCoachNotice, 15, text, Typeface.NORMAL));
            addWithMargins(body, notice, 0, 0, 0, 14);
            sessionCoachNotice = "";
            saveProfile(true);
        }

        addWeightTracking(body);
        addPerformanceShortcut(body);

        LinearLayout avatar = cardAccent();
        avatar.addView(sectionTitle("Avatar du Valhalla"));
        avatar.addView(centerText(avatarType + " • " + avatarOutfit + "\nFond : " + avatarBackdrop + "\n" + coinAmount(valhallaCoins) + " disponibles", 15, text, Typeface.NORMAL));
        Button customizeAvatar = secondaryButton("Personnaliser mon avatar");
        customizeAvatar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showAvatar(); }
        });
        addWithMargins(avatar, customizeAvatar, 0, 12, 0, 0);
        addWithMargins(body, avatar, 0, 0, 0, 14);

        LinearLayout notifications = card();
        notifications.addView(sectionTitle("Notifications"));
        notifications.addView(centerText(notificationSummary(), 15, muted, Typeface.NORMAL));
        Button openBattle = secondaryButton("Voir Battle");
        openBattle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showBattle(); }
        });
        addWithMargins(notifications, openBattle, 0, 12, 0, 0);
        addWithMargins(body, notifications, 0, 0, 0, 14);

        LinearLayout ai = cardAccent();
        ai.addView(sectionTitle("Coach IA"));
        ai.addView(centerText("Demande un tableau, une séance plus facile ou plus difficile, ou des conseils pour un exercice.", 15, text, Typeface.NORMAL));
        Button ask = secondaryButton("Parler au coach");
        ask.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showCoach();
            }
        });
        addWithMargins(ai, ask, 0, 14, 0, 0);
        addWithMargins(body, ai, 0, 0, 0, 14);

        LinearLayout history = card();
        history.addView(sectionTitle("Historique"));
        history.addView(centerText(historySummary(), 15, muted, Typeface.NORMAL));
        Button historyButton = secondaryButton("Voir mon historique");
        historyButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showHistory(); }
        });
        addWithMargins(history, historyButton, 0, 12, 0, 0);
        addWithMargins(body, history, 0, 0, 0, 14);

        mount(body, true, "home");
    }

    private void addWeightTracking(LinearLayout body) {
        LinearLayout tracking = card();
        tracking.addView(sectionTitle("Suivi du poids"));
        tracking.addView(centerText("Note ton poids une fois par jour pour voir ta tendance et suivre ton objectif.", 14, muted, Typeface.NORMAL));
        LinearLayout ranges = new LinearLayout(this);
        ranges.setOrientation(LinearLayout.HORIZONTAL);
        addWeightRangeButton(ranges, "Semaine");
        addWeightRangeButton(ranges, "Mois");
        addWeightRangeButton(ranges, "Année");
        addWithMargins(tracking, ranges, 0, 10, 0, 10);

        WeightChartView chart = new WeightChartView(this);
        tracking.addView(chart, new LinearLayout.LayoutParams(-1, dp(190)));

        LinearLayout fields = new LinearLayout(this);
        fields.setOrientation(LinearLayout.HORIZONTAL);
        fields.addView(sectionLabel("Poids actuel"), new LinearLayout.LayoutParams(0, -2, 1));
        fields.addView(sectionLabel("Objectif"), new LinearLayout.LayoutParams(0, -2, 1));
        tracking.addView(fields);
        fields = new LinearLayout(this);
        fields.setOrientation(LinearLayout.HORIZONTAL);
        final EditText todayInput = input("Poids aujourd'hui", false, true);
        todayInput.setText(weight);
        final EditText targetInput = input("Objectif kg", false, true);
        targetInput.setText(weightTarget);
        fields.addView(todayInput, new LinearLayout.LayoutParams(0, dp(56), 1));
        LinearLayout.LayoutParams targetParams = new LinearLayout.LayoutParams(0, dp(56), 1);
        targetParams.setMargins(dp(8), 0, 0, 0);
        fields.addView(targetInput, targetParams);
        tracking.addView(fields);

        Button saveWeight = primaryButton("Enregistrer le poids du jour");
        saveWeight.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String today = todayInput.getText().toString().trim();
                String target = targetInput.getText().toString().trim();
                if (today.length() == 0) return;
                try {
                    Float.parseFloat(today.replace(",", "."));
                    if (target.length() > 0) Float.parseFloat(target.replace(",", "."));
                } catch (NumberFormatException error) {
                    Toast.makeText(MainActivity.this, "Entre un poids valide en kilos.", Toast.LENGTH_SHORT).show();
                    return;
                }
                weight = today.replace(",", ".");
                if (target.length() > 0) weightTarget = target.replace(",", ".");
                recordTodayWeight(weight);
                saveProfile(true);
                showDashboard();
            }
        });
        addWithMargins(tracking, saveWeight, 0, 12, 0, 0);
        addWithMargins(body, tracking, 0, 0, 0, 14);
    }

    private void addWeightRangeButton(LinearLayout parent, final String range) {
        Button button = "Semaine".equals(range) ? quietButton(range) : quietButton(range);
        if (range.equals(weightChartRange)) {
            button.setTextColor(Color.rgb(4, 18, 24));
            button.setBackground(gradient(teal, green, dp(16)));
        }
        button.setTextSize(13);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                weightChartRange = range;
                prefs.edit().putString("weightChartRange", weightChartRange).apply();
                showDashboard();
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, dp(46), 1);
        params.setMargins(dp(3), 0, dp(3), 0);
        parent.addView(button, params);
    }

    private void recordTodayWeight(String value) {
        String today = dateKey(Calendar.getInstance());
        StringBuilder updated = new StringBuilder();
        String[] entries = weightHistory.length() == 0 ? new String[0] : weightHistory.split(";");
        boolean replaced = false;
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].length() == 0) continue;
            int cut = entries[i].indexOf("=");
            if (cut <= 0) continue;
            String date = entries[i].substring(0, cut);
            String saved = entries[i].substring(cut + 1);
            if (date.equals(today)) {
                saved = value;
                replaced = true;
            }
            if (updated.length() > 0) updated.append(";");
            updated.append(date).append("=").append(saved);
        }
        if (!replaced) {
            if (updated.length() > 0) updated.append(";");
            updated.append(today).append("=").append(value);
        }
        weightHistory = updated.toString();
        syncWeightToServerAsync(today, value);
    }

    private void addPerformanceShortcut(LinearLayout body) {
        LinearLayout performances = cardAccent();
        performances.addView(sectionTitle("Performances sportives"));
        int count = performanceEntryCount();
        String summary = count == 0
                ? "Ajoute tes exercices et tes maximums pour que le tableau soit vraiment personnel."
                : count + " exercice(s) renseigné(s). Le tableau utilise ces données pour choisir les bonnes variantes, séries et répétitions.";
        performances.addView(centerText(summary, 14, text, Typeface.NORMAL));
        Button edit = secondaryButton(count == 0 ? "Renseigner mes performances" : "Mettre à jour mes performances");
        edit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showPerformanceQuestion(); }
        });
        addWithMargins(performances, edit, 0, 12, 0, 0);
        addWithMargins(body, performances, 0, 0, 0, 14);
    }

    private int performanceEntryCount() {
        if (exercisePerformanceProfile == null || exercisePerformanceProfile.length() == 0) return 0;
        int count = 0;
        String[] entries = exercisePerformanceProfile.split(";");
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].trim().length() > 0) count++;
        }
        return count;
    }

    private String notificationSummary() {
        if (battleMessages.length() == 0) {
            return "Aucun message ni défi pour le moment.";
        }
        String[] entries = battleMessages.split("\n");
        int messages = 0;
        int challenges = 0;
        String latest = "";
        for (int i = entries.length - 1; i >= 0; i--) {
            String entry = entries[i].trim();
            if (entry.length() == 0) continue;
            String textOnly = entry;
            int roomCut = textOnly.indexOf("] ");
            if (roomCut >= 0 && roomCut + 2 < textOnly.length()) {
                textOnly = textOnly.substring(roomCut + 2);
            }
            if (textOnly.indexOf("Défi") >= 0 || textOnly.indexOf("défi") >= 0) {
                challenges++;
            } else {
                messages++;
            }
            if (latest.length() == 0) latest = textOnly;
        }
        if (latest.length() == 0) return "Aucun message ni défi pour le moment.";
        return challenges + " défi(s) • " + messages + " message(s)\nDernier : " + latest;
    }

    private void showHistory() {
        Calendar month = Calendar.getInstance();
        int year = month.get(Calendar.YEAR);
        int monthIndex = month.get(Calendar.MONTH);
        int today = month.get(Calendar.DAY_OF_MONTH);
        month.set(Calendar.DAY_OF_MONTH, 1);
        int firstColumn = (month.get(Calendar.DAY_OF_WEEK) + 5) % 7;
        int daysInMonth = month.getActualMaximum(Calendar.DAY_OF_MONTH);

        LinearLayout body = page();
        body.setPadding(dp(20), dp(22), dp(20), dp(22));
        body.addView(header("Suivi", "Mon historique"));
        body.addView(subtitle(historySummary()));
        body.addView(space(10));

        LinearLayout legend = card();
        legend.addView(infoLine("Vert", "séance réalisée"));
        legend.addView(infoLine("Rouge", "séance passée non réalisée"));
        legend.addView(infoLine("Bleu", "séance partiellement réalisée"));
        addWithMargins(body, legend, 0, 0, 0, 12);

        GridLayout calendarGrid = new GridLayout(this);
        calendarGrid.setColumnCount(7);
        calendarGrid.setRowCount(7);
        calendarGrid.setPadding(dp(8), dp(8), dp(8), dp(8));
        calendarGrid.setBackground(round(card, dp(22), stroke, 1));
        String[] labels = new String[] {"L", "M", "M", "J", "V", "S", "D"};
        for (int i = 0; i < labels.length; i++) {
            addCalendarCell(calendarGrid, labels[i], 0, i, muted, false);
        }
        for (int day = 1; day <= daysInMonth; day++) {
            int position = firstColumn + day - 1;
            int row = 1 + position / 7;
            int column = position % 7;
            Calendar cellDate = Calendar.getInstance();
            cellDate.set(year, monthIndex, day, 0, 0, 0);
            cellDate.set(Calendar.MILLISECOND, 0);
            String key = dateKey(cellDate);
            String status = historyStatus(key);
            boolean past = day < today;
            int color = muted;
            if ("complete".equals(status)) {
                color = green;
            } else if ("partial".equals(status)) {
                color = blue;
            } else if (past && isWorkoutDay(cellDate)) {
                color = Color.rgb(255, 105, 105);
            }
            addCalendarCell(calendarGrid, String.valueOf(day), row, column, color, true);
        }
        addWithMargins(body, calendarGrid, 0, 0, 0, 14);

        LinearLayout details = cardAccent();
        details.addView(sectionTitle("Le fil de tes séances"));
        details.addView(centerText(historyDetails(), 14, text, Typeface.NORMAL));
        addWithMargins(body, details, 0, 0, 0, 14);

        Button back = quietButton("Retour à l'accueil");
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showDashboard(); }
        });
        body.addView(back);
        mount(body, true, "home");
    }

    private void addCalendarCell(GridLayout grid, String value, int row, int column, int color, boolean dayCell) {
        TextView cell = centerText(value, dayCell ? 14 : 12, color, Typeface.BOLD);
        cell.setGravity(Gravity.CENTER);
        cell.setPadding(0, dp(4), 0, dp(4));
        if (dayCell) {
            cell.setBackground(round(Color.rgb(10, 24, 31), dp(12), Color.rgb(38, 66, 76), 1));
        }
        GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(row), GridLayout.spec(column, 1, 1f));
        params.width = 0;
        params.height = dp(44);
        params.setMargins(dp(3), dp(3), dp(3), dp(3));
        grid.addView(cell, params);
    }

    private String dateKey(Calendar date) {
        return String.format(Locale.US, "%04d-%02d-%02d", date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH));
    }

    private String weekKey() {
        Calendar date = Calendar.getInstance(Locale.FRANCE);
        date.setFirstDayOfWeek(Calendar.MONDAY);
        return String.format(Locale.US, "%04d-W%02d", date.get(Calendar.YEAR), date.get(Calendar.WEEK_OF_YEAR));
    }

    private String weekRangeText() {
        Calendar start = Calendar.getInstance(Locale.FRANCE);
        start.setFirstDayOfWeek(Calendar.MONDAY);
        int day = start.get(Calendar.DAY_OF_WEEK);
        int diff = day == Calendar.SUNDAY ? -6 : Calendar.MONDAY - day;
        start.add(Calendar.DAY_OF_MONTH, diff);
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.DAY_OF_MONTH, 6);
        return "Semaine du " + shortDate(start) + " au " + shortDate(end);
    }

    private String shortDate(Calendar date) {
        return String.format(Locale.FRANCE, "%02d/%02d", date.get(Calendar.DAY_OF_MONTH), date.get(Calendar.MONTH) + 1);
    }

    private String historyStatus(String key) {
        if (sessionHistory.length() == 0) return "";
        String[] entries = sessionHistory.split(";");
        for (int i = 0; i < entries.length; i++) {
            String[] parts = entries[i].split("=", 2);
            if (parts.length == 2 && key.equals(parts[0])) return parts[1];
        }
        return "";
    }

    private void recordSession(String status) {
        recordSessionForDate(status, dateKey(Calendar.getInstance()));
    }

    private void recordSessionForDate(String status, String key) {
        String[] entries = sessionHistory.length() == 0 ? new String[0] : sessionHistory.split(";");
        StringBuilder builder = new StringBuilder();
        boolean replaced = false;
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].startsWith(key + "=")) {
                if (!replaced) {
                    if (builder.length() > 0) builder.append(';');
                    builder.append(key).append('=').append(status);
                    replaced = true;
                }
            } else if (entries[i].length() > 0) {
                if (builder.length() > 0) builder.append(';');
                builder.append(entries[i]);
            }
        }
        if (!replaced) {
            if (builder.length() > 0) builder.append(';');
            builder.append(key).append('=').append(status);
        }
        sessionHistory = builder.toString();
        prefs.edit().putString("sessionHistory", sessionHistory).apply();
    }

    private void trackCurrentSessionDeadline() {
        String today = dateKey(Calendar.getInstance());
        if ("complete".equals(historyStatus(today)) || "partial".equals(historyStatus(today)) || "missed".equals(historyStatus(today))) {
            trackedSessionDate = "";
            trackedSessionSince = 0L;
            return;
        }
        if (trackedSessionDate.length() == 0) {
            trackedSessionDate = today;
            trackedSessionSince = System.currentTimeMillis();
        }
    }

    private void processSessionDeadline() {
        if (trackedSessionDate.length() == 0 || trackedSessionSince <= 0L) return;
        if (!historyStatus(trackedSessionDate).isEmpty()) {
            trackedSessionDate = "";
            trackedSessionSince = 0L;
            return;
        }
        long elapsed = System.currentTimeMillis() - trackedSessionSince;
        if (elapsed < 24L * 60L * 60L * 1000L) return;

        recordSessionForDate("missed", trackedSessionDate);
        valhallaCoins = Math.max(0, valhallaCoins - 15);
        sessionCoachNotice = "La séance du " + trackedSessionDate + " n'a pas été validée dans les 24 heures. Cela peut arriver : reprends doucement aujourd'hui. 15 pièces ont été retirées, sans casser ta progression. " +
                "Le plus important est de revenir à la prochaine séance.";
        trackedSessionDate = "";
        trackedSessionSince = 0L;
        saveProfile(true);
    }

    private boolean isWorkoutDay(Calendar date) {
        return date.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY;
    }

    private int completeWorkoutStreak() {
        Calendar cursor = Calendar.getInstance();
        int streak = 0;
        for (int i = 0; i < 90; i++) {
            if (!isWorkoutDay(cursor)) {
                cursor.add(Calendar.DAY_OF_MONTH, -1);
                continue;
            }
            String status = historyStatus(dateKey(cursor));
            if ("complete".equals(status)) {
                streak++;
                cursor.add(Calendar.DAY_OF_MONTH, -1);
            } else {
                break;
            }
        }
        return streak;
    }

    private int sessionStreakBonusXp() {
        int streak = completeWorkoutStreak();
        if (streak >= 50) return 220;
        if (streak >= 30) return 120;
        if (streak >= 10) return 60;
        return 0;
    }

    private int challengeStreakBonus() {
        int streak = completeWorkoutStreak();
        if (streak >= 50) return 100;
        if (streak >= 30) return 30;
        if (streak >= 15) return 5;
        return 0;
    }

    private String historySummary() {
        int complete = 0;
        int partial = 0;
        int missed = 0;
        if (sessionHistory.length() > 0) {
            String[] entries = sessionHistory.split(";");
            for (int i = 0; i < entries.length; i++) {
                if (entries[i].endsWith("=complete")) complete++;
                if (entries[i].endsWith("=partial")) partial++;
                if (entries[i].endsWith("=missed")) missed++;
            }
        }
        return complete + " séance(s) complète(s) • " + partial + " partielle(s) • " + missed + " non faite(s) • " + xp + " XP gagnés";
    }

    private String historyDetails() {
        if (sessionHistory.length() == 0) {
            return "Valide ta première séance pour voir apparaître ton calendrier de progression.";
        }
        String[] entries = sessionHistory.split(";");
        StringBuilder builder = new StringBuilder();
        for (int i = entries.length - 1; i >= 0; i--) {
            if (entries[i].length() > 0) {
                builder.append(entries[i].replace("=complete", " : séance complète").replace("=partial", " : séance partielle").replace("=missed", " : séance non faite"));
                if (i > 0) builder.append("\n");
            }
        }
        return builder.toString();
    }

    private FrameLayout battleArenaCard() {
        FrameLayout frame = new FrameLayout(this);
        frame.setBackground(round(Color.rgb(10, 24, 31), dp(22), stroke, 1));
        frame.setClipToOutline(true);
        frame.setMinimumHeight(dp(380));

        ImageView image = new ImageView(this);
        image.setImageResource(arenaBackdropDrawable());
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        frame.addView(image, new FrameLayout.LayoutParams(-1, -1));

        View shade = new View(this);
        shade.setBackgroundColor(Color.argb(172, 3, 10, 14));
        frame.addView(shade, new FrameLayout.LayoutParams(-1, -1));

        LinearLayout content = new LinearLayout(this);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_HORIZONTAL);
        content.setPadding(dp(18), dp(18), dp(18), dp(18));
        content.addView(vikingText("Arène du jour", 24, Color.rgb(255, 218, 119)));
        content.addView(vikingText("Statut : " + arenaStatus(), 18, text));
        content.addView(vikingText("Niveau joueur : LVL " + playerLevel(), 18, text));
        content.addView(vikingText("Chaîne : " + completeWorkoutStreak() + " jour(s)", 18, text));
        frame.addView(content, new FrameLayout.LayoutParams(-1, -2));
        return frame;
    }

    private void showBattle() {
        LinearLayout body = page();
        body.setPadding(dp(20), dp(22), dp(20), dp(22));
        body.addView(header("Communauté", "Battle"));
        body.addView(subtitle("Défie tes amis, discute en privé ou en groupe, et gagne des pièces du Valhalla."));
        body.addView(space(10));
        if (activeBattleRoom.length() == 0) activeBattleRoom = defaultBattleRoom();
        fetchBattleSocialFromServerAsync(false);

        addWithMargins(body, battleArenaCard(), 0, 0, 0, 12);

        LinearLayout friends = card();
        friends.addView(sectionTitle("Mes amis"));
        final EditText friendInput = input("Nom d'utilisateur à ajouter", false, false);
        addWithMargins(friends, friendInput, 0, 10, 0, 8);
        Button addFriend = primaryButton("Ajouter un ami");
        final TextView friendStatus = small("");
        addFriend.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = friendInput.getText().toString().trim();
                if (name.length() < 3) {
                    friendStatus.setText("Entre un nom d'utilisateur valide.");
                    return;
                }
                if (name.equalsIgnoreCase(userName)) {
                    friendStatus.setText("Tu ne peux pas t'ajouter toi-même.");
                    return;
                }
                addFriendName(name);
                syncFriendToServerAsync(name);
                friendInput.setText("");
                friendStatus.setText(name + " a été ajouté à tes amis.");
                showBattle();
            }
        });
        friends.addView(addFriend);
        addWithMargins(friends, friendStatus, 0, 8, 0, 8);
        Button myFriends = secondaryButton("Mes amis");
        myFriends.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showFriendsList(); }
        });
        addWithMargins(friends, myFriends, 0, 8, 0, 0);
        addWithMargins(body, friends, 0, 0, 0, 12);

        LinearLayout groups = card();
        groups.addView(sectionTitle("Groupes"));
        groups.addView(centerText("Crée un groupe, choisis tes amis, puis ouvre une conversation collective.", 14, muted, Typeface.NORMAL));
        final EditText groupInput = input("Nom du groupe", false, false);
        addWithMargins(groups, groupInput, 0, 10, 0, 8);
        Button createGroup = primaryButton("Créer le groupe");
        createGroup.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = groupInput.getText().toString().trim();
                if (name.length() < 3) return;
                showGroupBuilder(name);
            }
        });
        groups.addView(createGroup);
        Button seeGroups = secondaryButton("Voir groupe");
        seeGroups.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showGroupsList(); }
        });
        addWithMargins(groups, seeGroups, 0, 10, 0, 0);
        addWithMargins(body, groups, 0, 0, 0, 12);

        LinearLayout suggestions = card();
        suggestions.addView(sectionTitle("Suggestions de ton niveau"));
        suggestions.addView(centerText("Des sportifs proches de ton niveau apparaîtront ici quand le compte en ligne sera activé.", 14, muted, Typeface.NORMAL));
        String suggested = level + " • FreyaFit";
        Button suggestion = secondaryButton("Ajouter " + suggested);
        suggestion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addFriendName("FreyaFit");
                showBattle();
            }
        });
        addWithMargins(suggestions, suggestion, 0, 12, 0, 0);
        addWithMargins(body, suggestions, 0, 0, 0, 12);

        LinearLayout community = card();
        community.addView(sectionTitle("Défis de communauté"));
        community.addView(centerText("Les défis changent selon ton niveau et ton arène. Une vidéo reste obligatoire.", 14, muted, Typeface.NORMAL));
        community.addView(centerText("La vidéo est vérifiée par IA, protégée, invisible pour les autres utilisateurs, puis supprimée après vérification.", 14, text, Typeface.NORMAL));
        Button videoGuide = quietButton("Comment réaliser la vidéo");
        videoGuide.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showVideoTutorial(); }
        });
        addWithMargins(community, videoGuide, 0, 12, 0, 8);
        Button communityVideo = secondaryButton(selectedChallengeVideo == null ? "Ajouter une preuve vidéo" : "Preuve vidéo prête");
        communityVideo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                chooseChallengeVideo();
            }
        });
        addWithMargins(community, communityVideo, 0, 12, 0, 8);
        addCommunityChallenges(community);
        addWithMargins(body, community, 0, 0, 0, 12);

        mount(body, true, "battle");
    }

    private void showVideoTutorial() {
        LinearLayout body = page();
        body.setPadding(dp(20), dp(22), dp(20), dp(22));
        body.addView(header("Défis", "Réussir sa preuve vidéo"));
        body.addView(subtitle("Quelques règles simples pour que l'IA puisse vérifier ton mouvement correctement."));
        body.addView(space(10));

        LinearLayout guide = cardAccent();
        guide.addView(mascot(dp(150)));
        guide.addView(infoLine("1", "Fais apparaître ton corps en entier dans le cadre."));
        guide.addView(infoLine("2", "Ton visage n'est pas obligatoire : tu peux le cadrer hors champ."));
        guide.addView(infoLine("3", "Filme dans un endroit suffisamment éclairé, sans contre-jour."));
        guide.addView(infoLine("4", "Pose le téléphone pour garder un cadrage stable et visible."));
        guide.addView(infoLine("5", "L'IA vérifie le mouvement, les répétitions et la cohérence de la preuve."));
        guide.addView(infoLine("6", "La vidéo de challenge reste privée : personne ne peut la voir, elle est supprimée après vérification."));
        addWithMargins(body, guide, 0, 0, 0, 12);

        LinearLayout rules = card();
        rules.addView(sectionTitle("Triche et confidentialité"));
        rules.addView(centerText("Filmer un écran, une autre personne ou une vidéo déjà existante est interdit. Une triche confirmée peut entraîner le retrait total du niveau et, en cas de récidive, un bannissement d'un an. Ta vidéo est protégée, non visible publiquement, utilisée uniquement pour l'IA de vérification, puis supprimée.", 14, text, Typeface.NORMAL));
        addWithMargins(body, rules, 0, 0, 0, 12);

        Button back = quietButton("Retour aux défis");
        back.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { showBattle(); } });
        body.addView(back);
        mount(body, true, "battle");
    }

    private Button challengeButton(String label, final TextView status) {
        Button button = quietButton(label);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                battleMessages = "Défi envoyé : " + ((Button) v).getText().toString() + "\n" + battleMessages;
                prefs.edit().putString("battleMessages", battleMessages).apply();
                status.setText("Défi envoyé. La vidéo sera obligatoire pour le valider.");
            }
        });
        return button;
    }

    private String arenaName() {
        int lvl = playerLevel();
        if (lvl <= 20) return "Prairie tranquille";
        if (lvl <= 25) return "Désert bouillant";
        if (lvl <= 30) return "Montagne enneigée";
        if (lvl <= 35) return "Porte du paradis";
        if (lvl <= 45) return "Vaisseau spatial de musculation";
        return "Mont Olympe flamboyant";
    }

    private String arenaStatus() {
        int lvl = playerLevel();
        if (lvl <= 20) return "Débutant";
        if (lvl <= 35) return "Intermédiaire";
        return "Avancé";
    }

    private void addCommunityChallenges(LinearLayout parent) {
        int lvl = playerLevel();
        if (lvl <= 20) {
            addWithMargins(parent, communityChallengeButton("Facile", "20 squats propres", 15), 0, 0, 0, 8);
            addWithMargins(parent, communityChallengeButton("Normal", "30 secondes de gainage", 30), 0, 0, 0, 8);
            addWithMargins(parent, communityChallengeButton("Difficile", "12 pompes contrôlées", 50), 0, 0, 0, 8);
            addWithMargins(parent, communityChallengeButton("Extrême", "Circuit 3 minutes sans pause", 100), 0, 0, 0, 0);
        } else if (lvl <= 25) {
            addWithMargins(parent, communityChallengeButton("Facile", "40 squats désert", 15), 0, 0, 0, 8);
            addWithMargins(parent, communityChallengeButton("Normal", "20 burpees propres", 30), 0, 0, 0, 8);
            addWithMargins(parent, communityChallengeButton("Difficile", "60 secondes mountain climbers", 50), 0, 0, 0, 8);
            addWithMargins(parent, communityChallengeButton("Extrême", "100 fentes alternées", 100), 0, 0, 0, 0);
        } else if (lvl <= 30) {
            addWithMargins(parent, communityChallengeButton("Facile", "45 secondes chaise", 15), 0, 0, 0, 8);
            addWithMargins(parent, communityChallengeButton("Normal", "25 pompes tempo", 30), 0, 0, 0, 8);
            addWithMargins(parent, communityChallengeButton("Difficile", "90 secondes gainage", 50), 0, 0, 0, 8);
            addWithMargins(parent, communityChallengeButton("Extrême", "150 squats neige", 100), 0, 0, 0, 0);
        } else if (lvl <= 35) {
            addWithMargins(parent, communityChallengeButton("Facile", "30 dips ou pompes serrées", 15), 0, 0, 0, 8);
            addWithMargins(parent, communityChallengeButton("Normal", "50 abdos contrôlés", 30), 0, 0, 0, 8);
            addWithMargins(parent, communityChallengeButton("Difficile", "8 minutes circuit paradis", 50), 0, 0, 0, 8);
            addWithMargins(parent, communityChallengeButton("Extrême", "300 répétitions cumulées", 100), 0, 0, 0, 0);
        } else if (lvl <= 45) {
            addWithMargins(parent, communityChallengeButton("Facile", "60 secondes corde imaginaire", 15), 0, 0, 0, 8);
            addWithMargins(parent, communityChallengeButton("Normal", "30 pompes alien tempo", 30), 0, 0, 0, 8);
            addWithMargins(parent, communityChallengeButton("Difficile", "12 minutes full body spatial", 50), 0, 0, 0, 8);
            addWithMargins(parent, communityChallengeButton("Extrême", "400 répétitions en orbite", 100), 0, 0, 0, 0);
        } else {
            addWithMargins(parent, communityChallengeButton("Facile", "50 pompes olympiennes", 15), 0, 0, 0, 8);
            addWithMargins(parent, communityChallengeButton("Normal", "100 squats flamboyants", 30), 0, 0, 0, 8);
            addWithMargins(parent, communityChallengeButton("Difficile", "15 minutes circuit éclair", 50), 0, 0, 0, 8);
            addWithMargins(parent, communityChallengeButton("Extrême", "Défi titan 600 répétitions", 100), 0, 0, 0, 0);
        }
    }

    private Button communityChallengeButton(final String difficulty, final String exercise, final int reward) {
        Button button = quietButton(difficulty + " • " + exercise + " • " + coinAmount(reward) + " à gagner");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (selectedChallengeVideo == null) {
                    appendBattleMessage(activeBattleRoom, "Défi de communauté choisi : " + difficulty + " • " + exercise + " • " + coinAmount(reward) + ". Preuve vidéo obligatoire avant validation.");
                    chooseChallengeVideo();
                    return;
                }
                completeCommunityChallenge(difficulty, exercise, reward);
            }
        });
        return button;
    }

    private void completeCommunityChallenge(String difficulty, String exercise, int reward) {
        int bonus = challengeStreakBonus();
        int total = reward + bonus;
        selectedChallengeVideo = null;
        showChallengeReward(difficulty, exercise, reward, bonus, total);
    }

    private void showChallengeReward(final String difficulty, final String exercise, final int reward, final int bonus, final int total) {
        LinearLayout body = page();
        body.setPadding(dp(20), dp(22), dp(20), dp(22));
        body.addView(header("Battle", "Défi validé"));

        LinearLayout rewardCard = cardAccent();
        rewardCard.addView(avatarMascot(dp(150)));
        rewardCard.addView(sectionTitle("Bravo, défi remporté !"));
        rewardCard.addView(centerText("Coach VALHALLA RAGE : mouvement validé. Belle discipline, tu gagnes ta récompense.", 15, text, Typeface.BOLD));
        rewardCard.addView(infoLine("Défi", difficulty + " • " + exercise));
        rewardCard.addView(infoLine("Récompense", coinAmount(reward)));
        if (bonus > 0) {
            rewardCard.addView(infoLine("Bonus chaîne", "+" + coinAmount(bonus)));
        }
        rewardCard.addView(centerText("Total : +" + coinAmount(total), 24, Color.rgb(255, 190, 72), Typeface.BOLD));

        Button collect = primaryButton("Récupérer +" + coinAmount(total));
        collect.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                grantChallengeReward(difficulty, exercise, reward, bonus, total, false);
            }
        });
        addWithMargins(rewardCard, collect, 0, 12, 0, 8);

        Button doubleReward = secondaryButton("Regarder une pub pour doubler");
        doubleReward.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                grantChallengeReward(difficulty, exercise, reward, bonus, total * 2, true);
            }
        });
        rewardCard.addView(doubleReward);
        addWithMargins(body, rewardCard, 0, 0, 0, 12);

        Button back = quietButton("Retour sans récupérer");
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showBattle(); }
        });
        body.addView(back);
        mount(body, true, "battle");
    }

    private void grantChallengeReward(String difficulty, String exercise, int reward, int bonus, int granted, boolean doubled) {
        valhallaCoins += granted;
        prefs.edit().putInt("valhallaCoins", valhallaCoins).apply();
        appendBattleMessage(activeBattleRoom, "Défi remporté : " + difficulty + " • " + exercise + " • +" + coinAmount(granted) + (doubled ? " après pub doublée." : " (" + coinAmount(reward) + " + bonus chaîne " + coinAmount(bonus) + ")."));
        playVictorySound();
        flashMessage("Récompense ajoutée : +" + coinAmount(granted));
        showBattle();
    }

    private void addFriendName(String name) {
        String[] names = battleFriends.length() == 0 ? new String[0] : battleFriends.split(",");
        for (int i = 0; i < names.length; i++) {
            if (name.equalsIgnoreCase(names[i].trim())) return;
        }
        battleFriends = battleFriends.length() == 0 ? name : battleFriends + "," + name;
        prefs.edit().putString("battleFriends", battleFriends).apply();
    }

    private void addGroupName(String name) {
        String[] names = battleGroups.length() == 0 ? new String[0] : battleGroups.split(",");
        for (int i = 0; i < names.length; i++) {
            if (name.equalsIgnoreCase(names[i].trim())) return;
        }
        battleGroups = battleGroups.length() == 0 ? name : battleGroups + "," + name;
        prefs.edit().putString("battleGroups", battleGroups).apply();
    }

    private void removeFriendName(String name) {
        StringBuilder builder = new StringBuilder();
        String[] names = battleFriends.split(",");
        for (int i = 0; i < names.length; i++) {
            if (names[i].trim().equalsIgnoreCase(name)) continue;
            if (names[i].trim().length() > 0) {
                if (builder.length() > 0) builder.append(',');
                builder.append(names[i].trim());
            }
        }
        battleFriends = builder.toString();
        battleFriendMeta = removeMetaEntry(battleFriendMeta, name);
        prefs.edit().putString("battleFriends", battleFriends).putString("battleFriendMeta", battleFriendMeta).apply();
    }

    private String removeMetaEntry(String meta, String key) {
        if (meta == null || meta.length() == 0) return "";
        StringBuilder builder = new StringBuilder();
        String[] entries = meta.split(";");
        for (int i = 0; i < entries.length; i++) {
            String existing = metaPart(entries[i], 0);
            if (existing.length() == 0 || existing.equalsIgnoreCase(key)) continue;
            if (builder.length() > 0) builder.append(';');
            builder.append(entries[i]);
        }
        return builder.toString();
    }

    private void showFriendsList() {
        LinearLayout body = page();
        body.setPadding(dp(20), dp(22), dp(20), dp(22));
        body.addView(header("Battle", "Mes amis"));
        body.addView(subtitle("Ouvre un chat privé ou supprime un ami de ta liste."));
        body.addView(space(10));

        LinearLayout list = card();
        if (battleFriends.length() == 0) {
            list.addView(centerText("Aucun ami ajouté pour le moment.", 15, muted, Typeface.NORMAL));
        } else {
            String[] names = battleFriends.split(",");
            for (int i = 0; i < names.length; i++) {
                final String friend = names[i].trim();
                if (friend.length() == 0) continue;
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.addView(centerText(friend, 15, text, Typeface.BOLD), new LinearLayout.LayoutParams(0, dp(56), 1));
                Button chat = secondaryButton("Chat");
                chat.setTextSize(12);
                chat.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) { openBattleRoom(friendRoom(friend)); }
                });
                row.addView(chat, new LinearLayout.LayoutParams(dp(78), dp(52)));
                Button remove = quietButton("Supprimer");
                remove.setTextSize(11);
                remove.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        syncRemoveFriendFromServerAsync(friend);
                        removeFriendName(friend);
                        showFriendsList();
                    }
                });
                row.addView(remove, new LinearLayout.LayoutParams(dp(112), dp(52)));
                list.addView(row);
            }
        }
        addWithMargins(body, list, 0, 0, 0, 12);
        Button back = quietButton("Retour Battle");
        back.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { showBattle(); } });
        body.addView(back);
        mount(body, true, "battle");
    }

    private void showGroupBuilder(String name) {
        if (!name.equals(pendingGroupName)) {
            pendingGroupName = name;
            pendingGroupMembers = "";
        }
        LinearLayout body = page();
        body.setPadding(dp(20), dp(22), dp(20), dp(22));
        body.addView(header("Créer un groupe", name));
        Button done = primaryButton("Terminer");
        done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String groupName = pendingGroupName;
                String groupMembers = pendingGroupMembers;
                saveGroup(groupName, groupMembers);
                syncGroupToServerAsync(groupName, groupMembers);
                openBattleRoom(groupRoom(groupName));
            }
        });
        addWithMargins(body, done, 0, 12, 0, 12);

        LinearLayout list = card();
        list.addView(sectionTitle("Ajouter des amis"));
        if (battleFriends.length() == 0) {
            list.addView(centerText("Ajoute d'abord des amis pour créer un groupe.", 14, muted, Typeface.NORMAL));
        } else {
            String[] names = battleFriends.split(",");
            for (int i = 0; i < names.length; i++) {
                final String friend = names[i].trim();
                if (friend.length() == 0) continue;
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setGravity(Gravity.CENTER_VERTICAL);
                row.addView(centerText(friend, 15, text, Typeface.BOLD), new LinearLayout.LayoutParams(0, dp(54), 1));
                Button add = secondaryButton(containsToken(pendingGroupMembers, friend) ? "Ajouté" : "Ajouter");
                add.setTextSize(12);
                add.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        pendingGroupMembers = addToken(pendingGroupMembers, friend);
                        showGroupBuilder(pendingGroupName);
                    }
                });
                row.addView(add, new LinearLayout.LayoutParams(dp(104), dp(52)));
                list.addView(row);
            }
        }
        addWithMargins(body, list, 0, 0, 0, 12);
        Button cancel = quietButton("Annuler");
        cancel.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { showBattle(); } });
        body.addView(cancel);
        mount(body, true, "battle");
    }

    private void showGroupsList() {
        LinearLayout body = page();
        body.setPadding(dp(20), dp(22), dp(20), dp(22));
        body.addView(header("Battle", "Mes groupes"));
        body.addView(subtitle("Ouvre une conversation de groupe pour discuter, envoyer des photos vérifiées par IA ou lancer un défi."));
        body.addView(space(10));

        LinearLayout list = card();
        if (battleGroups.length() == 0) {
            list.addView(centerText("Aucun groupe créé pour le moment.", 15, muted, Typeface.NORMAL));
        } else {
            String[] groups = battleGroups.split(";");
            for (int i = 0; i < groups.length; i++) {
                final String groupName = groupNameFromEntry(groups[i]);
                if (groupName.length() == 0) continue;
                Button open = secondaryButton(groupName + " • " + groupMembersFromEntry(groups[i]));
                open.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) { openBattleRoom(groupRoom(groupName)); }
                });
                addWithMargins(list, open, 0, 0, 0, 8);
            }
        }
        addWithMargins(body, list, 0, 0, 0, 12);
        Button back = quietButton("Retour Battle");
        back.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { showBattle(); } });
        body.addView(back);
        mount(body, true, "battle");
    }

    private void openBattleRoom(String room) {
        activeBattleRoom = room;
        prefs.edit().putString("activeBattleRoom", activeBattleRoom).apply();
        fetchRoomMessagesFromServerAsync(room, true);
        showBattleChat(room);
    }

    private void showBattleChat(final String room) {
        activeBattleRoom = room;
        prefs.edit().putString("activeBattleRoom", activeBattleRoom).apply();
        LinearLayout body = page();
        body.setPadding(dp(20), dp(22), dp(20), dp(22));
        body.addView(header("Conversation", roomTitle(room)));
        body.addView(subtitle(roomSubtitle(room)));
        body.addView(space(10));

        LinearLayout chat = cardAccent();
        chat.addView(sectionTitle("Messages"));
        addRoomMessageBubbles(chat, room);
        final EditText chatMessage = input("Écris un message", false, false);
        addWithMargins(chat, chatMessage, 0, 12, 0, 8);
        Button send = primaryButton("Envoyer");
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String message = chatMessage.getText().toString().trim();
                if (message.length() == 0) return;
                appendBattleMessage(room, "Toi : " + message);
                syncBattleMessageToServerAsync(room, message);
                showBattleChat(room);
            }
        });
        chat.addView(send);
        addWithMargins(body, chat, 0, 0, 0, 12);

        LinearLayout actions = card();
        actions.addView(sectionTitle("Actions"));
        actions.addView(centerText("Photos et vidéos sont vérifiées par IA, protégées, puis les fichiers de contrôle sont supprimés après vérification.", 13, muted, Typeface.NORMAL));
        Button defy = primaryButton("Défier");
        defy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showBattleChallenge(room); }
        });
        addWithMargins(actions, defy, 0, 10, 0, 8);
        Button photo = secondaryButton("Envoyer une photo");
        photo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { chooseBattlePhoto(room); }
        });
        actions.addView(photo);
        addWithMargins(body, actions, 0, 0, 0, 12);

        Button back = quietButton("Retour Battle");
        back.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { showBattle(); } });
        body.addView(back);
        mount(body, true, "battle");
    }

    private void showBattleChallenge(final String room) {
        LinearLayout body = page();
        body.setPadding(dp(20), dp(22), dp(20), dp(22));
        body.addView(header("Défier", roomTitle(room)));
        body.addView(subtitle("Choisis l'exercice, les répétitions ou le temps. Une vidéo privée est obligatoire et supprimée après vérification IA."));
        body.addView(space(10));

        LinearLayout challenge = card();
        final EditText exerciseInput = input("Exercice", false, false);
        final EditText repsInput = input("Nombre de répétitions", false, true);
        final EditText timeInput = input("Temps en secondes", false, true);
        addWithMargins(challenge, exerciseInput, 0, 0, 0, 8);
        addWithMargins(challenge, repsInput, 0, 0, 0, 8);
        addWithMargins(challenge, timeInput, 0, 0, 0, 8);
        Button video = secondaryButton(selectedChallengeVideo == null ? "Ajouter la preuve vidéo" : "Vidéo prête");
        video.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { chooseChallengeVideo(room); }
        });
        addWithMargins(challenge, video, 0, 0, 0, 8);
        Button send = primaryButton("Envoyer le défi");
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String exercise = exerciseInput.getText().toString().trim();
                String reps = repsInput.getText().toString().trim();
                String time = timeInput.getText().toString().trim();
                if (exercise.length() == 0) exercise = "exercice libre";
                if (reps.length() == 0 && time.length() == 0) reps = "objectif libre";
                String target = reps.length() > 0 ? reps + " répétition(s)" : time + " seconde(s)";
                String videoStatus = selectedChallengeVideo == null ? "preuve vidéo privée à ajouter" : "preuve vidéo privée ajoutée, vérifiée par IA puis supprimée";
                Uri proofVideo = selectedChallengeVideo;
                appendBattleMessage(room, "Défi : " + exercise + " • " + target + " • " + videoStatus);
                syncBattleMessageToServerAsync(room, "Défi : " + exercise + " • " + target + " • " + videoStatus);
                syncPrivateChallengeToServerAsync(room, exercise, reps, time, proofVideo);
                selectedChallengeVideo = null;
                showBattleChat(room);
            }
        });
        challenge.addView(send);
        addWithMargins(body, challenge, 0, 0, 0, 12);
        Button back = quietButton("Retour conversation");
        back.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { showBattleChat(room); } });
        body.addView(back);
        mount(body, true, "battle");
    }

    private void chooseChallengeVideo() {
        chooseChallengeVideo("");
    }

    private void chooseChallengeVideo(String returnRoom) {
        videoReturnRoom = returnRoom == null ? "" : returnRoom;
        Intent picker = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        picker.addCategory(Intent.CATEGORY_OPENABLE);
        picker.setType("video/*");
        startActivityForResult(picker, VIDEO_PICK_REQUEST);
    }

    private void chooseBattlePhoto(String room) {
        activeBattleRoom = room;
        prefs.edit().putString("activeBattleRoom", activeBattleRoom).apply();
        Intent picker = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        picker.addCategory(Intent.CATEGORY_OPENABLE);
        picker.setType("image/*");
        startActivityForResult(picker, BATTLE_PHOTO_REQUEST);
    }

    private void chooseProfilePhoto() {
        Intent picker = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        picker.addCategory(Intent.CATEGORY_OPENABLE);
        picker.setType("image/*");
        startActivityForResult(picker, PROFILE_PHOTO_REQUEST);
    }

    private void chooseFeedPhoto() {
        Intent picker = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        picker.addCategory(Intent.CATEGORY_OPENABLE);
        picker.setType("image/*");
        startActivityForResult(picker, FEED_PHOTO_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIDEO_PICK_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedChallengeVideo = data.getData();
            if (videoReturnRoom.length() > 0) {
                String room = videoReturnRoom;
                videoReturnRoom = "";
                showBattleChallenge(room);
            } else {
                showBattle();
            }
        } else if (requestCode == PROFILE_PHOTO_REQUEST && resultCode == RESULT_OK && data != null) {
            persistReadPermission(data);
            profilePhotoUri = data.getData().toString();
            prefs.edit().putString("profilePhotoUri", profilePhotoUri).apply();
            showProfile();
        } else if (requestCode == FEED_PHOTO_REQUEST && resultCode == RESULT_OK && data != null) {
            persistReadPermission(data);
            String uri = data.getData().toString();
            feedPhotos = feedPhotos.length() == 0 ? uri : uri + "|" + feedPhotos;
            prefs.edit().putString("feedPhotos", feedPhotos).apply();
            showProfile();
        } else if (requestCode == BATTLE_PHOTO_REQUEST && resultCode == RESULT_OK && data != null) {
            persistReadPermission(data);
            String photoMessage = "Photo envoyée : vérification IA en cours avant affichage sécurisé.";
            appendBattleMessage(activeBattleRoom, photoMessage);
            syncBattleMessageToServerAsync(activeBattleRoom, photoMessage);
            showBattleChat(activeBattleRoom);
        }
    }

    private void persistReadPermission(Intent data) {
        try {
            int flags = data.getFlags() & Intent.FLAG_GRANT_READ_URI_PERMISSION;
            if (data.getData() != null && flags != 0) {
                getContentResolver().takePersistableUriPermission(data.getData(), flags);
            }
        } catch (Exception ignored) {
        }
    }

    private String defaultBattleRoom() {
        if (battleFriends.length() > 0) return friendRoom(battleFriends.split(",")[0].trim());
        if (battleGroups.length() > 0) return groupRoom(groupNameFromEntry(battleGroups.split(";")[0]));
        return "general";
    }

    private void saveGroup(String name, String members) {
        if (name == null || name.trim().length() < 3) return;
        String cleanName = name.trim().replace(";", " ").replace("~", " ");
        String cleanMembers = members == null ? "" : members.replace(";", " ").replace("~", " ");
        String entry = cleanName + "~" + cleanMembers;
        String[] groups = battleGroups.length() == 0 ? new String[0] : battleGroups.split(";");
        StringBuilder builder = new StringBuilder();
        boolean replaced = false;
        for (int i = 0; i < groups.length; i++) {
            String existingName = groupNameFromEntry(groups[i]);
            if (existingName.equalsIgnoreCase(cleanName)) {
                if (!replaced) {
                    if (builder.length() > 0) builder.append(';');
                    builder.append(entry);
                    replaced = true;
                }
            } else if (existingName.length() > 0) {
                if (builder.length() > 0) builder.append(';');
                builder.append(groups[i]);
            }
        }
        if (!replaced) {
            if (builder.length() > 0) builder.append(';');
            builder.append(entry);
        }
        battleGroups = builder.toString();
        pendingGroupName = "";
        pendingGroupMembers = "";
        prefs.edit().putString("battleGroups", battleGroups).apply();
    }

    private String groupNameFromEntry(String entry) {
        if (entry == null) return "";
        int cut = entry.indexOf('~');
        return (cut >= 0 ? entry.substring(0, cut) : entry).trim();
    }

    private String groupMembersFromEntry(String entry) {
        if (entry == null) return "aucun membre";
        int cut = entry.indexOf('~');
        if (cut < 0 || cut >= entry.length() - 1) return "aucun membre";
        return entry.substring(cut + 1).replace("|", ", ");
    }

    private String friendRoom(String friend) {
        return "friend:" + friend;
    }

    private String groupRoom(String group) {
        return "group:" + group;
    }

    private void setActiveBattleRoom(String room) {
        activeBattleRoom = room;
        prefs.edit().putString("activeBattleRoom", activeBattleRoom).apply();
        showBattle();
    }

    private String roomTitle(String room) {
        if (room.startsWith("friend:")) return "Chat privé avec " + room.substring(7);
        if (room.startsWith("group:")) return "Groupe " + room.substring(6);
        return "Chat Battle";
    }

    private String roomSubtitle(String room) {
        if (room.startsWith("friend:")) return "Conversation individuelle : parler, envoyer une photo ou lancer un défi.";
        if (room.startsWith("group:")) return "Conversation collective : parler, envoyer une photo ou défier tout le groupe.";
        return "Conversation Battle.";
    }

    private void appendBattleMessage(String room, String message) {
        if (room == null || room.length() == 0) room = defaultBattleRoom();
        String clean = message.replace("|", " ").replace("\n", " ");
        String entry = room + "|" + clean;
        battleMessages = battleMessages.length() == 0 ? entry : battleMessages + "\n" + entry;
        prefs.edit().putString("battleMessages", battleMessages).apply();
    }

    private void appendBattleMessageUnique(String room, String message) {
        if (room == null || room.length() == 0) room = defaultBattleRoom();
        String clean = message.replace("|", " ").replace("\n", " ");
        String entry = room + "|" + clean;
        String[] entries = battleMessages.length() == 0 ? new String[0] : battleMessages.split("\n");
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].equals(entry)) return;
        }
        battleMessages = battleMessages.length() == 0 ? entry : battleMessages + "\n" + entry;
        prefs.edit().putString("battleMessages", battleMessages).apply();
    }

    private String roomMessages(String room) {
        if (battleMessages.length() == 0) return "La conversation apparaîtra ici.";
        StringBuilder builder = new StringBuilder();
        String[] entries = battleMessages.split("\n");
        for (int i = 0; i < entries.length; i++) {
            String[] parts = entries[i].split("\\|", 2);
            if (parts.length == 2 && parts[0].equals(room)) {
                if (builder.length() > 0) builder.append("\n\n");
                builder.append(parts[1]);
            }
        }
        return builder.length() == 0 ? "Aucun message dans cette conversation." : builder.toString();
    }

    private void addRoomMessageBubbles(LinearLayout parent, String room) {
        if (battleMessages.length() == 0) {
            parent.addView(centerText("La conversation apparaîtra ici.", 14, text, Typeface.NORMAL));
            return;
        }
        int count = 0;
        String[] entries = battleMessages.split("\n");
        for (int i = 0; i < entries.length; i++) {
            String[] parts = entries[i].split("\\|", 2);
            if (parts.length != 2 || !parts[0].equals(room)) continue;
            boolean mine = parts[1].startsWith("Toi :");
            TextView bubble = centerText(parts[1], 14, mine ? Color.rgb(4, 18, 22) : text, Typeface.NORMAL);
            bubble.setGravity(mine ? Gravity.RIGHT : Gravity.LEFT);
            bubble.setTextAlignment(mine ? View.TEXT_ALIGNMENT_TEXT_END : View.TEXT_ALIGNMENT_TEXT_START);
            bubble.setPadding(dp(14), dp(10), dp(14), dp(10));
            bubble.setBackground(mine ? gradient(teal, green, dp(18)) : round(Color.rgb(12, 29, 39), dp(18), Color.rgb(58, 92, 104), 1));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-2, -2);
            params.gravity = mine ? Gravity.RIGHT : Gravity.LEFT;
            params.width = dp(260);
            params.setMargins(0, dp(6), 0, dp(6));
            parent.addView(bubble, params);
            count++;
        }
        if (count == 0) {
            parent.addView(centerText("Aucun message dans cette conversation.", 14, text, Typeface.NORMAL));
        }
    }

    private void showAvatar() {
        cleanAvatarEquipment();
        ensureAvatarBaseItem();
        syncLegacyAvatarLabels();
        LinearLayout body = page();
        body.setPadding(dp(20), dp(22), dp(20), dp(22));
        body.addView(header("Valhalla", "Mon avatar"));
        body.addView(subtitle("Choisis ton héros, équipe tes objets et transforme tes victoires en pièces du Valhalla."));
        body.addView(space(10));

        LinearLayout preview = cardAccent();
        preview.addView(avatarStage(dp(235)));
        preview.addView(equippedVisuals());
        preview.addView(sectionTitle(avatarType));
        preview.addView(centerText("Équipé : " + equippedSummary(), 14, text, Typeface.BOLD));
        preview.addView(infoLine("Fond", avatarBackdrop));
        preview.addView(infoLine("Niveau joueur", String.valueOf(playerLevel())));
        preview.addView(infoLine("Pièces disponibles", coinAmount(valhallaCoins)));
        addWithMargins(body, preview, 0, 0, 0, 12);

        LinearLayout identity = card();
        identity.addView(sectionTitle("Choisir ton héros"));
        identity.addView(centerText("Héros actuel : " + avatarType, 15, text, Typeface.BOLD));
        Button chooseHero = primaryButton("Changer de héros");
        chooseHero.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showHeroSelection(); }
        });
        addWithMargins(identity, chooseHero, 0, 10, 0, 0);
        addWithMargins(body, identity, 0, 0, 0, 12);

        if (premiumHeroSelected()) {
            LinearLayout lockedSet = card();
            lockedSet.addView(sectionTitle("Set légendaire verrouillé"));
            lockedSet.addView(centerText(avatarType + " possède déjà son apparence complète. Les objets et tenues ne peuvent pas être équipés sur ce héros.", 14, text, Typeface.NORMAL));
            addWithMargins(body, lockedSet, 0, 0, 0, 12);
        } else {
            LinearLayout inventory = card();
            inventory.addView(sectionTitle("Inventaire"));
            inventory.addView(centerText("Tous les objets acquis par l'utilisateur. Appuie sur un objet pour l'équiper ou le retirer.", 14, muted, Typeface.NORMAL));
            addShopCarousel(inventory, "Objets acquis", inventoryShopItems());
            addWithMargins(body, inventory, 0, 0, 0, 12);

            LinearLayout outfits = card();
            outfits.addView(sectionTitle("Boutique du Valhalla"));
            outfits.addView(centerText("Fais défiler les objets avec ton doigt. Un objet acheté est ajouté à ton inventaire.", 14, muted, Typeface.NORMAL));
            addShopCarousel(outfits, "Tenues", outfitShopItems());
            addShopCarousel(outfits, "Accessoires", accessoryShopItems());
            addShopCarousel(outfits, "Fonds d'avatar", backdropShopItems());
            addWithMargins(body, outfits, 0, 0, 0, 12);
        }

        addCoinStore(body);

        Button back = quietButton("Retour accueil");
        back.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { showDashboard(); } });
        body.addView(back);
        mount(body, true, "shop");
    }

    private HorizontalScrollView equippedVisuals() {
        HorizontalScrollView scroll = new HorizontalScrollView(this);
        scroll.setHorizontalScrollBarEnabled(false);
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER);
        ShopItem[] all = allShopItems();
        for (int i = 0; i < all.length; i++) {
            if (!isEquipped(all[i].id)) continue;
            if (all[i].drawable == 0) continue;
            if ("backdrop".equals(all[i].category)) continue;
            ImageView image = new ImageView(this);
            image.setImageResource(all[i].drawable);
            image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(64), dp(64));
            params.setMargins(dp(4), 0, dp(4), 0);
            row.addView(image, params);
        }
        scroll.addView(row, new HorizontalScrollView.LayoutParams(-2, dp(72)));
        return scroll;
    }

    private void showHeroSelection() {
        LinearLayout body = page();
        body.setPadding(dp(20), dp(22), dp(20), dp(22));
        body.addView(header("Héros", "Choisis ton incarnation"));
        body.addView(subtitle("Viking, Valkyrie, mages ou héros légendaires : ton choix détermine le style de ton avatar."));
        body.addView(space(10));

        String[][] heroes = new String[][] {
                {"Viking", "Guerrier brutal, set d'entraînement gratuit", "0"},
                {"Valkyrie", "Combattante céleste, set d'entraînement gratuit", "0"},
                {"Magicien", "Mage runique masculin, set de base gratuit", "0"},
                {"Magicienne", "Mage runique féminine, set de base gratuit", "0"},
                {"THOR", "Dieu du tonnerre, set légendaire déjà équipé", "5000"},
                {"FREYA", "Déesse de guerre, set légendaire déjà équipé", "5000"}
        };

        for (int i = 0; i < heroes.length; i++) {
            addHeroOption(body, heroes[i][0], heroes[i][1], Integer.parseInt(heroes[i][2]));
        }

        Button back = quietButton("Retour boutique");
        back.setOnClickListener(new View.OnClickListener() { public void onClick(View v) { showAvatar(); } });
        body.addView(back);
        mount(body, true, "shop");
    }

    private void addHeroOption(LinearLayout parent, final String hero, String description, final int price) {
        LinearLayout box = card();
        box.setBackground(round(Color.rgb(10, 24, 31), dp(18), hero.equals(avatarType) ? green : heroPrice(hero) > 0 ? Color.rgb(255, 190, 72) : stroke, hero.equals(avatarType) ? 2 : 1));
        box.addView(heroPreview(hero, dp(135)));
        box.addView(sectionTitle(hero));
        box.addView(centerText(description, 14, muted, Typeface.NORMAL));
        boolean owned = ownsHero(hero);
        if (price > 0 && !owned) {
            box.addView(centerText("Prix : " + coinAmount(price), 15, Color.rgb(255, 190, 72), Typeface.BOLD));
        } else if (premiumHero(hero)) {
            box.addView(centerText("Set légendaire intégré • objets bloqués", 13, Color.rgb(255, 190, 72), Typeface.BOLD));
        } else {
            box.addView(centerText("Set de base inclus", 13, green, Typeface.BOLD));
        }

        Button action = hero.equals(avatarType) ? quietButton("Héros équipé") : primaryButton((price > 0 && !owned) ? "Acheter et incarner" : "Incarner");
        action.setEnabled(!hero.equals(avatarType));
        action.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { chooseHero(hero); }
        });
        addWithMargins(box, action, 0, 10, 0, 0);
        addWithMargins(parent, box, 0, 0, 0, 12);
    }

    private int heroPrice(String hero) {
        if ("THOR".equals(hero) || "FREYA".equals(hero)) return 5000;
        return 0;
    }

    private boolean premiumHero(String hero) {
        return heroPrice(hero) > 0;
    }

    private boolean premiumHeroSelected() {
        return premiumHero(avatarType);
    }

    private boolean ownsHero(String hero) {
        return containsToken(ownedHeroes, hero);
    }

    private void chooseHero(String hero) {
        int price = heroPrice(hero);
        if (!ownsHero(hero)) {
            if (valhallaCoins < price) {
                flashMessage("Il faut " + coinAmount(price) + " pour débloquer " + hero + ".");
                return;
            }
            valhallaCoins -= price;
            ownedHeroes = addToken(ownedHeroes, hero);
            flashMessage(hero + " débloqué.");
        }
        switchAvatarType(hero);
    }

    private void addShopCarousel(LinearLayout parent, String label, ShopItem[] items) {
        parent.addView(sectionLabel(label));
        HorizontalScrollView scroll = new HorizontalScrollView(this);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dp(2), dp(8), dp(2), dp(8));
        for (int i = 0; i < items.length; i++) {
            LinearLayout item = shopItemCard(items[i]);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(168), dp(228));
            params.setMargins(0, 0, dp(10), 0);
            row.addView(item, params);
        }
        scroll.addView(row, new HorizontalScrollView.LayoutParams(-2, -2));
        parent.addView(scroll, new LinearLayout.LayoutParams(-1, dp(244)));
    }

    private void addCoinStore(LinearLayout body) {
        resetDailyCoinAdsIfNeeded();
        LinearLayout store = card();
        store.addView(sectionTitle("Pas assez de pièces ?"));
        store.addView(centerText("Gagne des pièces gratuites avec une pub ou prends un pack de pièces du Valhalla.", 14, muted, Typeface.NORMAL));

        HorizontalScrollView scroll = new HorizontalScrollView(this);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(dp(2), dp(10), dp(2), dp(8));
        addCoinOffer(row, "Pièces gratuites", "Pub courte", 75, "Gratuit", 0);
        addCoinOffer(row, "Petit coffre", "Colonnes de pièces", 350, "1,99 €", 1);
        addCoinOffer(row, "Grand coffre", "Colonnes de pièces", 1000, "5,99 €", 2);
        addCoinOffer(row, "Trésor royal", "Sacs d'or + colonnes", 2000, "9,99 €", 3);
        scroll.addView(row, new HorizontalScrollView.LayoutParams(-2, -2));
        store.addView(scroll, new LinearLayout.LayoutParams(-1, dp(250)));
        addWithMargins(body, store, 0, 0, 0, 12);
    }

    private void addCoinOffer(LinearLayout row, final String title, String visualLabel, final int amount, final String price, final int tier) {
        LinearLayout offer = new LinearLayout(this);
        offer.setOrientation(LinearLayout.VERTICAL);
        offer.setGravity(Gravity.CENTER_HORIZONTAL);
        offer.setPadding(dp(8), dp(8), dp(8), dp(8));
        offer.setBackground(round(Color.rgb(10, 24, 31), dp(16), Color.rgb(255, 190, 72), 1));
        offer.addView(new CoinPackView(this, tier), new LinearLayout.LayoutParams(-1, dp(86)));
        offer.addView(centerText(title, 13, text, Typeface.BOLD));
        offer.addView(centerText(visualLabel, 11, muted, Typeface.NORMAL));
        offer.addView(centerText(coinAmount(amount), 14, Color.rgb(255, 190, 72), Typeface.BOLD));
        String buttonText;
        if (tier == 0) {
            buttonText = "Obtenir gratuitement (" + freeCoinAdsRemaining() + "/2)";
        } else {
            buttonText = price;
        }
        Button action = tier == 0 ? primaryButton(buttonText) : secondaryButton(buttonText);
        action.setTextSize(13);
        action.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                rememberScroll(v, "shop");
                if (tier == 0) {
                    claimFreeCoinsFromAd();
                } else {
                    buyCoinPack(amount, price);
                }
            }
        });
        addWithMargins(offer, action, 0, 8, 0, 0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(168), dp(232));
        params.setMargins(0, 0, dp(10), 0);
        row.addView(offer, params);
    }

    private void resetDailyCoinAdsIfNeeded() {
        String today = dateKey(Calendar.getInstance());
        if (!today.equals(freeCoinAdDate)) {
            freeCoinAdDate = today;
            freeCoinAdsToday = 0;
            prefs.edit().putString("freeCoinAdDate", freeCoinAdDate).putInt("freeCoinAdsToday", freeCoinAdsToday).apply();
        }
    }

    private int freeCoinAdsRemaining() {
        resetDailyCoinAdsIfNeeded();
        return Math.max(0, 2 - freeCoinAdsToday);
    }

    private void claimFreeCoinsFromAd() {
        resetDailyCoinAdsIfNeeded();
        if (freeCoinAdsToday >= 2) {
            flashMessage("Limite quotidienne atteinte : 2 pubs gratuites par jour.");
            return;
        }
        freeCoinAdsToday++;
        valhallaCoins += 75;
        prefs.edit().putString("freeCoinAdDate", freeCoinAdDate).putInt("freeCoinAdsToday", freeCoinAdsToday).putInt("valhallaCoins", valhallaCoins).apply();
        syncShopStateToServerAsync();
        flashMessage("Pub terminée : +" + coinAmount(75));
        showAvatar();
    }

    private void buyCoinPack(int amount, String price) {
        valhallaCoins += amount;
        prefs.edit().putInt("valhallaCoins", valhallaCoins).apply();
        syncShopStateToServerAsync();
        flashMessage("Pack " + price + " ajouté : +" + coinAmount(amount));
        showAvatar();
    }

    private LinearLayout shopItemCard(final ShopItem item) {
        final LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setGravity(Gravity.CENTER_HORIZONTAL);
        card.setPadding(dp(8), dp(8), dp(8), dp(8));
        boolean locked = itemLocked(item);
        int border = locked ? Color.rgb(88, 96, 101) : isEquipped(item.id) ? teal : rarityColor(item.rarity);
        card.setBackground(round(Color.rgb(10, 24, 31), dp(16), border, isEquipped(item.id) ? 2 : 1));

        if (item.drawable != 0) {
            ImageView image = new ImageView(this);
            image.setImageResource(item.drawable);
            image.setAlpha(locked ? 0.35f : 1.0f);
            image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            card.addView(image, new LinearLayout.LayoutParams(-1, dp(90)));
        } else if (item.price > 0) {
            ShopItemVisualView visual = new ShopItemVisualView(this, item.name, item.rarity, item.avatarTarget);
            visual.setAlpha(locked ? 0.35f : 1.0f);
            card.addView(visual, new LinearLayout.LayoutParams(-1, dp(90)));
        } else {
            TextView base = centerText("Objet de base", 12, muted, Typeface.BOLD);
            base.setGravity(Gravity.CENTER);
            card.addView(base, new LinearLayout.LayoutParams(-1, dp(90)));
        }
        card.addView(centerText(item.name, 13, text, Typeface.BOLD));
        if (item.drawable != 0) {
            card.addView(centerText(item.rarity + " • " + coinAmount(item.price), 12, rarityColor(item.rarity), Typeface.BOLD));
            card.addView(centerText("Niveau " + item.requiredLevel, 11, muted, Typeface.NORMAL));
        } else {
            card.addView(centerText("Gratuit", 12, muted, Typeface.NORMAL));
        }
        String status;
        int statusColor = muted;
        if (locked) {
            status = "🔒 Niveau " + item.requiredLevel + " requis";
            statusColor = Color.rgb(210, 214, 216);
        } else if (isEquipped(item.id)) {
            status = "Équipé • toucher pour retirer";
            statusColor = green;
        } else if (ownsItem(item.id)) {
            status = "Possédé • toucher pour équiper";
        } else if (item.price > valhallaCoins) {
            status = "Pas assez de pièces";
        } else {
            status = "Toucher pour acheter";
        }
        card.addView(centerText(status, 11, statusColor, Typeface.NORMAL));
        card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String block = shopBlockReason(item);
                if (block.length() > 0) {
                    flashMessage(block);
                    return;
                }
                rememberScroll(v, "shop");
                if (toggleShopItem(item)) showAvatar();
            }
        });
        return card;
    }

    private String shopBlockReason(ShopItem item) {
        if (premiumHeroSelected() && !"backdrop".equals(item.category)) return avatarType + " possède déjà son set légendaire.";
        if (!isItemForCurrentAvatar(item)) return "Cet objet n'est pas compatible avec ton avatar.";
        if (itemLocked(item)) return "Vous n'avez pas assez de pièces ou pas le niveau requis";
        if (!ownsItem(item.id) && (item.purchaseOnly || item.price > valhallaCoins)) return "Vous n'avez pas assez de pièces ou pas le niveau requis";
        return "";
    }

    private boolean toggleShopItem(ShopItem item) {
        if (shopBlockReason(item).length() > 0) return false;
        if (!ownsItem(item.id)) {
            valhallaCoins -= item.price;
            inventoryItems = addToken(inventoryItems, item.id);
        }
        if (isEquipped(item.id)) {
            equippedItems = removeToken(equippedItems, item.id);
        } else {
            if ("backdrop".equals(item.category)) equippedItems = removeCategoryFromTokens(equippedItems, "backdrop");
            if ("outfit".equals(item.category)) equippedItems = removeCategoryFromTokens(equippedItems, "outfit");
            if ("no_accessory".equals(item.id)) equippedItems = removeCategoryFromTokens(equippedItems, "accessory");
            equippedItems = addToken(equippedItems, item.id);
        }
        syncLegacyAvatarLabels();
        saveAvatar();
        return true;
    }

    private void switchAvatarType(String type) {
        avatarType = type;
        ownedHeroes = addToken(ownedHeroes, type);
        cleanAvatarEquipment();
        ensureAvatarBaseItem();
        syncLegacyAvatarLabels();
        saveAvatar();
        showAvatar();
    }

    private void cleanAvatarEquipment() {
        StringBuilder result = new StringBuilder();
        String[] values = equippedItems == null || equippedItems.length() == 0 ? new String[0] : equippedItems.split("\\|");
        for (int i = 0; i < values.length; i++) {
            ShopItem item = findShopItem(values[i]);
            if (item == null) continue;
            if (!isItemForCurrentAvatar(item)) continue;
            if (result.length() > 0) result.append('|');
            result.append(values[i]);
        }
        equippedItems = result.toString();
    }

    private void ensureAvatarBaseItem() {
        if (premiumHeroSelected()) {
            inventoryItems = addToken(inventoryItems, "bg_gods_temple");
            equippedItems = removeCategoryFromTokens(equippedItems, "backdrop");
            equippedItems = addToken(equippedItems, "bg_gods_temple");
            return;
        }
        String base = baseOutfitForHero(avatarType);
        inventoryItems = addToken(inventoryItems, base);
        if (!hasEquippedCategory("outfit")) {
            equippedItems = addToken(equippedItems, base);
        }
        if (!hasEquippedCategory("backdrop")) {
            inventoryItems = addToken(inventoryItems, "bg_prairie");
            equippedItems = addToken(equippedItems, "bg_prairie");
        }
    }

    private String baseOutfitForHero(String hero) {
        if ("Valkyrie".equals(hero)) return "valkyrie_training";
        if ("Magicien".equals(hero)) return "mage_training";
        if ("Magicienne".equals(hero)) return "sorceress_training";
        return "training_outfit";
    }

    private boolean hasEquippedCategory(String category) {
        ShopItem[] all = allShopItems();
        for (int i = 0; i < all.length; i++) {
            if (category.equals(all[i].category) && isEquipped(all[i].id) && isItemForCurrentAvatar(all[i])) return true;
        }
        return false;
    }

    private boolean isItemForCurrentAvatar(ShopItem item) {
        if (premiumHeroSelected() && !"backdrop".equals(item.category)) return false;
        return "Tous".equals(item.avatarTarget) || avatarType.equals(item.avatarTarget);
    }

    private ShopItem[] filterAvatarItems(ShopItem[] items) {
        ArrayList<ShopItem> filtered = new ArrayList<ShopItem>();
        for (int i = 0; i < items.length; i++) {
            if (isItemForCurrentAvatar(items[i])) filtered.add(items[i]);
        }
        return filtered.toArray(new ShopItem[filtered.size()]);
    }

    private ShopItem[] outfitShopItems() {
        return filterAvatarItems(new ShopItem[] {
                new ShopItem("training_outfit", "Tenue viking simple", 0, 0, "outfit", false, "Commun", 1, "Viking"),
                new ShopItem("novice_tunic", "Tunique de guerrier", 260, R.drawable.valhalla_novice_tunic, "outfit", false, "Commun", 5, "Viking"),
                new ShopItem("wrist_wraps", "Bandes de force", 320, R.drawable.valhalla_wrist_wraps, "outfit", false, "Commun", 8, "Viking"),
                new ShopItem("berserker_boots", "Bottes berserker", 650, R.drawable.valhalla_berserker_boots, "outfit", false, "Rare", 16, "Viking"),
                new ShopItem("rune_outfit", "Cape runique", 720, R.drawable.valhalla_rune_cape, "outfit", false, "Rare", 22, "Viking"),
                new ShopItem("hunter_cloak", "Cape du chasseur", 780, R.drawable.valhalla_hunter_cloak, "outfit", false, "Rare", 26, "Viking"),
                new ShopItem("champion_outfit", "Armure du champion", 1120, R.drawable.valhalla_runic_shoulder, "outfit", false, "Très rare", 36, "Viking"),
                new ShopItem("jarl_gauntlets", "Gantelets du Jarl", 390, R.drawable.valhalla_jarl_gauntlets, "outfit", false, "Commun", 10, "Viking"),
                new ShopItem("war_belt_north", "Ceinturon de guerre", 540, R.drawable.valhalla_war_belt_north, "outfit", false, "Rare", 18, "Viking"),
                new ShopItem("north_shoulders", "Épaulières du Nord", 980, R.drawable.valhalla_north_shoulders, "outfit", false, "Très rare", 32, "Viking"),
                new ShopItem("war_paint_viking", "Peinture de guerre", 360, R.drawable.valhalla_war_paint_viking, "outfit", false, "Commun", 12, "Viking"),
                new ShopItem("runic_axe", "Hache runique", 1500, R.drawable.valhalla_runic_axe, "outfit", false, "Légendaire", 45, "Viking"),
                new ShopItem("valkyrie_training", "Tenue valkyrie simple", 0, 0, "outfit", false, "Commun", 1, "Valkyrie"),
                new ShopItem("valkyrie_tunic", "Tunique de Valkyrie", 260, R.drawable.valhalla_valkyrie_tunic, "outfit", false, "Commun", 5, "Valkyrie"),
                new ShopItem("valkyrie_boots", "Bottes ailées", 650, R.drawable.valhalla_valkyrie_boots, "outfit", false, "Rare", 16, "Valkyrie"),
                new ShopItem("valkyrie_cape", "Cape de Valkyrie", 760, R.drawable.valhalla_valkyrie_cape, "outfit", false, "Rare", 22, "Valkyrie"),
                new ShopItem("valkyrie_rune_dress", "Armure runique", 920, R.drawable.valhalla_valkyrie_rune_dress, "outfit", false, "Rare", 27, "Valkyrie"),
                new ShopItem("valkyrie_astral_armor", "Armure épique de Valkyrie", 1120, R.drawable.valhalla_valkyrie_epic_armor, "outfit", false, "Très rare", 36, "Valkyrie"),
                new ShopItem("silver_circlet", "Serre-tête d'argent", 380, R.drawable.valhalla_silver_circlet, "outfit", false, "Commun", 10, "Valkyrie"),
                new ShopItem("aurora_breastplate", "Plastron d'aurore", 820, R.drawable.valhalla_aurora_breastplate, "outfit", false, "Rare", 24, "Valkyrie"),
                new ShopItem("celestial_spear", "Lance céleste", 1080, R.drawable.valhalla_celestial_spear, "outfit", false, "Très rare", 34, "Valkyrie"),
                new ShopItem("battle_skirt", "Jupe de bataille", 540, R.drawable.valhalla_battle_skirt, "outfit", false, "Rare", 18, "Valkyrie"),
                new ShopItem("mage_training", "Robe runique de magicien", 0, 0, "outfit", false, "Commun", 1, "Magicien"),
                new ShopItem("archmage_hat", "Chapeau d'archimage", 430, R.drawable.valhalla_archmage_hat, "outfit", false, "Commun", 10, "Magicien"),
                new ShopItem("ember_staff", "Bâton de braise", 760, R.drawable.valhalla_ember_staff, "outfit", false, "Rare", 22, "Magicien"),
                new ShopItem("rune_mantle", "Manteau des runes", 920, R.drawable.valhalla_rune_mantle, "outfit", false, "Rare", 28, "Magicien"),
                new ShopItem("ancient_grimoire", "Grimoire ancien", 1100, R.drawable.valhalla_ancient_grimoire, "outfit", false, "Très rare", 34, "Magicien"),
                new ShopItem("eclipse_ring", "Anneau d'éclipse", 1500, R.drawable.valhalla_eclipse_ring, "outfit", false, "Légendaire", 45, "Magicien"),
                new ShopItem("sorceress_training", "Robe runique de magicienne", 0, 0, "outfit", false, "Commun", 1, "Magicienne"),
                new ShopItem("lunar_diadem", "Diadème lunaire", 430, R.drawable.valhalla_lunar_diadem, "outfit", false, "Commun", 10, "Magicienne"),
                new ShopItem("star_staff", "Bâton d'étoiles", 760, R.drawable.valhalla_star_staff, "outfit", false, "Rare", 22, "Magicienne"),
                new ShopItem("astral_dress", "Robe astrale", 920, R.drawable.valhalla_astral_dress, "outfit", false, "Rare", 28, "Magicienne"),
                new ShopItem("freya_grimoire", "Grimoire de Freya", 1100, R.drawable.valhalla_freya_grimoire, "outfit", false, "Très rare", 34, "Magicienne"),
                new ShopItem("mist_cape", "Cape de brume", 1500, R.drawable.valhalla_mist_cape, "outfit", false, "Légendaire", 45, "Magicienne")
        });
    }

    private ShopItem[] accessoryShopItems() {
        return filterAvatarItems(new ShopItem[] {
                new ShopItem("no_accessory", "Aucun accessoire", 0, 0, "accessory", false, "Commun", 1, "Tous"),
                new ShopItem("rune_helmet", "Casque runique", 680, R.drawable.valhalla_rune_helmet, "accessory", false, "Rare", 18, "Viking"),
                new ShopItem("iron_belt", "Ceinture de fer", 600, R.drawable.valhalla_iron_belt, "accessory", false, "Rare", 20, "Viking"),
                new ShopItem("ice_shield", "Bouclier de glace", 1120, R.drawable.valhalla_ice_shield, "accessory", false, "Très rare", 34, "Viking"),
                new ShopItem("valkyrie_diadem", "Diadème runique", 680, R.drawable.valhalla_valkyrie_diadem, "accessory", false, "Rare", 18, "Valkyrie"),
                new ShopItem("valkyrie_belt", "Ceinture dorée", 600, R.drawable.valhalla_valkyrie_belt, "accessory", false, "Rare", 20, "Valkyrie"),
                new ShopItem("amethyst_bracers", "Brassards améthyste", 1060, R.drawable.valhalla_amethyst_bracers, "accessory", false, "Très rare", 32, "Valkyrie"),
                new ShopItem("valkyrie_mythic_wings", "Ailes mythiques", 1500, R.drawable.valhalla_valkyrie_wings, "accessory", false, "Légendaire", 45, "Valkyrie")
        });
    }

    private ShopItem[] backdropShopItems() {
        return new ShopItem[] {
                new ShopItem("bg_prairie", "Prairie", 0, R.drawable.bg_prairie, "backdrop", false, "Commun", 1),
                new ShopItem("bg_sunset", "Coucher de soleil", 130, R.drawable.bg_sunset, "backdrop", false, "Commun", 6),
                new ShopItem("bg_beach", "Plage", 180, R.drawable.bg_beach, "backdrop", false, "Commun", 10),
                new ShopItem("bg_forest", "Forêt", 420, R.drawable.bg_forest, "backdrop", false, "Rare", 18),
                new ShopItem("bg_stadium", "Stade de foot", 520, R.drawable.bg_stadium, "backdrop", false, "Rare", 24),
                new ShopItem("bg_luxury_gym", "Salle luxe animée", 900, R.drawable.bg_luxury_gym, "backdrop", false, "Très rare", 30),
                new ShopItem("bg_volcano", "Volcan animé", 980, R.drawable.bg_volcano, "backdrop", false, "Très rare", 34),
                new ShopItem("bg_blizzard", "Banquise animée", 1040, R.drawable.bg_blizzard, "backdrop", false, "Très rare", 36),
                new ShopItem("bg_ocean", "Sous l'océan animé", 1120, R.drawable.bg_ocean, "backdrop", false, "Très rare", 40),
                new ShopItem("bg_gods_temple", "Temple des dieux", 1500, R.drawable.bg_gods_temple, "backdrop", false, "Légendaire", 45)
        };
    }

    private boolean itemLocked(ShopItem item) {
        return item.requiredLevel > playerLevel();
    }

    private int playerLevel() {
        return Math.max(1, (xp / xpPerLevel()) + 1);
    }

    private int xpPerLevel() {
        return 2000;
    }

    private int xpProgressInLevel() {
        return Math.max(0, xp % xpPerLevel());
    }

    private int rarityColor(String rarity) {
        if ("Rare".equals(rarity)) return Color.rgb(45, 156, 255);
        if ("Très rare".equals(rarity)) return Color.rgb(188, 95, 255);
        if ("Légendaire".equals(rarity)) return Color.rgb(255, 190, 72);
        return Color.rgb(119, 236, 126);
    }

    private ShopItem[] inventoryShopItems() {
        ArrayList<ShopItem> owned = new ArrayList<ShopItem>();
        ShopItem[] all = allShopItems();
        for (int i = 0; i < all.length; i++) {
            if (ownsItem(all[i].id)) owned.add(all[i]);
        }
        return owned.toArray(new ShopItem[owned.size()]);
    }

    private ShopItem[] allShopItems() {
        return concatItems(concatItems(outfitShopItems(), accessoryShopItems()), backdropShopItems());
    }

    private ShopItem[] concatItems(ShopItem[] first, ShopItem[] second) {
        ShopItem[] result = new ShopItem[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    private boolean ownsItem(String id) {
        return containsToken(inventoryItems, id);
    }

    private boolean isEquipped(String id) {
        return containsToken(equippedItems, id);
    }

    private boolean containsToken(String source, String token) {
        if (source == null || source.length() == 0) return false;
        String[] values = source.split("\\|");
        for (int i = 0; i < values.length; i++) {
            if (token.equals(values[i])) return true;
        }
        return false;
    }

    private String addToken(String source, String token) {
        if (containsToken(source, token)) return source;
        return source == null || source.length() == 0 ? token : source + "|" + token;
    }

    private String removeToken(String source, String token) {
        if (source == null || source.length() == 0) return "";
        StringBuilder result = new StringBuilder();
        String[] values = source.split("\\|");
        for (int i = 0; i < values.length; i++) {
            if (token.equals(values[i]) || values[i].length() == 0) continue;
            if (result.length() > 0) result.append('|');
            result.append(values[i]);
        }
        return result.toString();
    }

    private String removeCategoryFromTokens(String source, String category) {
        if (source == null || source.length() == 0) return "";
        StringBuilder result = new StringBuilder();
        String[] values = source.split("\\|");
        for (int i = 0; i < values.length; i++) {
            ShopItem item = findShopItem(values[i]);
            if (item != null && category.equals(item.category)) continue;
            if (values[i].length() == 0) continue;
            if (result.length() > 0) result.append('|');
            result.append(values[i]);
        }
        return result.toString();
    }

    private ShopItem findShopItem(String id) {
        ShopItem[] all = allShopItems();
        for (int i = 0; i < all.length; i++) {
            if (all[i].id.equals(id)) return all[i];
        }
        return null;
    }

    private String equippedSummary() {
        if (premiumHeroSelected()) return "set légendaire intégré";
        ArrayList<String> names = new ArrayList<String>();
        ShopItem[] all = allShopItems();
        for (int i = 0; i < all.length; i++) {
            if ("backdrop".equals(all[i].category)) continue;
            if (isEquipped(all[i].id)) names.add(all[i].name);
        }
        if (names.size() == 0) return "aucun objet";
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < names.size(); i++) {
            if (i > 0) result.append(" • ");
            result.append(names.get(i));
        }
        return result.toString();
    }

    private String equippedBackdropId() {
        ShopItem[] all = allShopItems();
        for (int i = 0; i < all.length; i++) {
            if ("backdrop".equals(all[i].category) && isEquipped(all[i].id)) return all[i].id;
        }
        return "bg_prairie";
    }

    private void syncLegacyAvatarLabels() {
        avatarOutfit = premiumHeroSelected() ? "Set légendaire intégré" : "Aucune tenue";
        avatarAccessory = "Aucun";
        avatarBackdrop = "Prairie";
        ShopItem[] all = allShopItems();
        for (int i = 0; i < all.length; i++) {
            if (!isEquipped(all[i].id)) continue;
            if ("outfit".equals(all[i].category)) avatarOutfit = all[i].name;
            if ("accessory".equals(all[i].category) && !"no_accessory".equals(all[i].id)) avatarAccessory = all[i].name;
            if ("backdrop".equals(all[i].category)) avatarBackdrop = all[i].name;
        }
    }

    private void saveAvatar() {
        saveAvatarLocal();
        syncShopStateToServerAsync();
    }

    private void saveAvatarLocal() {
        prefs.edit().putString("avatarType", avatarType).putString("avatarOutfit", avatarOutfit).putString("avatarAccessory", avatarAccessory).putString("avatarBackdrop", avatarBackdrop).putString("ownedHeroes", ownedHeroes).putString("inventoryItems", inventoryItems).putString("equippedItems", equippedItems).putInt("valhallaCoins", valhallaCoins).apply();
    }

    private void showPlan() {
        LinearLayout body = page();
        body.setPadding(dp(20), dp(22), dp(20), dp(22));
        body.addView(header("Plan", "Tableau de la semaine"));
        body.addView(subtitle(weekRangeText() + " • " + adjustmentLabel()));
        if (serverPlanJson.length() > 0 && weekKey().equals(serverPlanWeek)) {
            body.addView(small("Programme synchronisé avec le serveur VALHALLA RAGE."));
        }
        if (customPlanActive && weekKey().equals(customPlanWeek)) {
            body.addView(small("Tableau personnel actif : tu peux le modifier quand tu veux."));
        }

        LinearLayout coach = cardAccent();
        coach.addView(mascot(dp(120)));
        coach.addView(sectionTitle("Ton coach valide le cap"));
        coach.addView(centerText("Une série propre vaut mieux qu'une série bâclée. Garde le contrôle, puis progresse semaine après semaine.", 15, text, Typeface.BOLD));
        Button askCoach = secondaryButton("Parler au coach IA");
        askCoach.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showCoach(); }
        });
        addWithMargins(coach, askCoach, 0, 12, 0, 0);
        addWithMargins(body, coach, 0, 12, 0, 12);

        Button export = secondaryButton("Exporter le tableau en PNG paysage");
        export.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                exportPlan();
            }
        });
        addWithMargins(body, export, 0, 12, 0, 8);

        Button custom = primaryButton(customPlanActive ? "Modifier mon tableau personnel" : "Créer mon propre tableau");
        custom.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showCustomPlanEditor(); }
        });
        addWithMargins(body, custom, 0, 0, 0, 8);

        if (customPlanActive) {
            Button automatic = quietButton("Revenir au tableau automatique");
            automatic.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    customPlanActive = false;
                    saveProfile(true);
                    flashMessage("Tableau automatique réactivé.");
                    showPlan();
                }
            });
            addWithMargins(body, automatic, 0, 0, 0, 8);
        }

        ArrayList<DayPlan> plan = buildPlan();
        addWeeklyPlanSurvey(body);
        addPlanTable(body, plan);
        mount(body, true, "plan");
    }

    private void showCustomPlanEditor() {
        LinearLayout body = page();
        body.setPadding(dp(20), dp(22), dp(20), dp(28));
        body.addView(header("Plan", "Créer mon tableau"));
        body.addView(subtitle("Écris ton programme si tu en as déjà un. Format conseillé : exercice | séries | répétitions ou temps | poids | repos."));

        ArrayList<DayPlan> plan = customPlanFromCache();
        if (plan == null) {
            boolean wasCustom = customPlanActive;
            customPlanActive = false;
            plan = buildPlan();
            customPlanActive = wasCustom;
        }

        final String[] days = new String[] {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
        final EditText[] focusInputs = new EditText[days.length];
        final EditText[] exerciseInputs = new EditText[days.length];

        for (int i = 0; i < days.length; i++) {
            DayPlan source = i < plan.size() ? plan.get(i) : new DayPlan(days[i], "Séance");
            LinearLayout card = card();
            card.addView(sectionTitle(days[i]));
            final EditText focus = input("Titre de la séance", false, false);
            focus.setText(source.focus);
            final EditText exercises = multilineInput("Exemple : Pompes | 3 | 12 reps | Poids du corps | 60 s", 142);
            exercises.setText(customPlanLines(source));
            addWithMargins(card, focus, 0, 10, 0, 8);
            addWithMargins(card, exercises, 0, 0, 0, 0);
            addWithMargins(body, card, 0, 0, 0, 12);
            focusInputs[i] = focus;
            exerciseInputs[i] = exercises;
        }

        Button save = primaryButton("Enregistrer comme tableau officiel");
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    JSONObject program = new JSONObject();
                    program.put("week", weekKey());
                    program.put("source", "custom");
                    JSONArray daysJson = new JSONArray();
                    for (int i = 0; i < days.length; i++) {
                        JSONObject day = new JSONObject();
                        day.put("day", days[i]);
                        String focus = focusInputs[i].getText().toString().trim();
                        day.put("focus", focus.length() == 0 ? "Séance personnalisée" : focus);
                        JSONArray exercises = new JSONArray();
                        String[] lines = exerciseInputs[i].getText().toString().split("\\n");
                        for (int j = 0; j < lines.length; j++) {
                            String exercise = customExerciseFromLine(lines[j]);
                            if (exercise.length() > 0) exercises.put(exercise);
                        }
                        if (exercises.length() == 0) exercises.put(customExercise("Repos complet", "1", "Repos", "Aucun", "Libre"));
                        day.put("exercises", exercises);
                        daysJson.put(day);
                    }
                    program.put("days", daysJson);
                    customPlanJson = program.toString();
                    customPlanWeek = weekKey();
                    customPlanActive = true;
                    previewPlanActive = false;
                    serverPlanJson = "";
                    serverPlanWeek = "";
                    saveProfile(true);
                    syncProgramToServerAsync(program);
                    flashMessage("Ton tableau personnel est enregistré.");
                    showPlan();
                } catch (Exception error) {
                    flashMessage("Impossible d'enregistrer ce tableau.");
                }
            }
        });
        addWithMargins(body, save, 0, 4, 0, 8);

        Button back = quietButton("Retour au plan");
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showPlan(); }
        });
        body.addView(back);
        mount(body, true, "plan");
    }

    private String customPlanLines(DayPlan day) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < day.exercises.size(); i++) {
            String exercise = day.exercises.get(i);
            if (builder.length() > 0) builder.append("\n");
            builder.append(exerciseName(exercise)).append(" | ")
                    .append(exerciseSeries(exercise)).append(" | ")
                    .append(exerciseReps(exercise)).append(" | ")
                    .append(exerciseWeight(exercise)).append(" | ")
                    .append(exerciseRest(exercise));
        }
        return builder.toString();
    }

    private String customExerciseFromLine(String line) {
        String clean = line == null ? "" : line.trim();
        if (clean.length() == 0) return "";
        String[] parts = clean.split("\\|");
        String name = parts.length > 0 ? parts[0].trim() : clean;
        if (name.length() == 0) return "";
        String series = parts.length > 1 && parts[1].trim().length() > 0 ? parts[1].trim() : "3";
        String reps = parts.length > 2 && parts[2].trim().length() > 0 ? parts[2].trim() : "à définir";
        String weight = parts.length > 3 && parts[3].trim().length() > 0 ? parts[3].trim() : (isBodyweightExerciseName(name) ? "Poids du corps" : "À définir");
        String rest = parts.length > 4 && parts[4].trim().length() > 0 ? parts[4].trim() : "60 s";
        return customExercise(name, series, reps, weight, rest);
    }

    private String customExercise(String name, String series, String reps, String weight, String rest) {
        return name + "\n" + series + " séries x " + reps + "  •  poids " + weight + "  •  repos " + rest;
    }

    private void exportPlan() {
        try {
            Uri uri = createPlanPng();
            Intent send = new Intent(Intent.ACTION_SEND);
            send.setType("image/png");
            send.putExtra(Intent.EXTRA_STREAM, uri);
            send.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Toast.makeText(this, "PNG VALHALLA RAGE créé dans Images / VALHALLA RAGE", Toast.LENGTH_LONG).show();
            startActivity(Intent.createChooser(send, "Exporter ou imprimer le PNG"));
        } catch (Exception error) {
            Toast.makeText(this, "Impossible de créer le PNG pour le moment.", Toast.LENGTH_LONG).show();
        }
    }

    private void addWeeklyPlanSurvey(LinearLayout body) {
        LinearLayout survey = card();
        survey.addView(kicker("BILAN HEBDOMADAIRE"));
        if (weekKey().equals(lastPlanSurveyWeek)) {
            survey.addView(sectionTitle("Questionnaire rempli"));
            survey.addView(centerText("Réponse : " + lastPlanSurveyAnswer + "\nLe tableau est daté et adapté pour la prochaine semaine.", 15, text, Typeface.NORMAL));
            addWithMargins(body, survey, 0, 8, 0, 12);
            return;
        }

        if (isSunday()) {
            surveyPending = true;
            saveProfile(true);
        }
        if (!surveyPending) {
            survey.addView(sectionTitle("Prochain bilan dimanche"));
            survey.addView(centerText("Le mini questionnaire s'ouvre le dimanche. Si tu ne le remplis pas, il restera proposé jusqu'à sa validation.", 14, muted, Typeface.NORMAL));
            addWithMargins(body, survey, 0, 8, 0, 12);
            return;
        }

        survey.addView(sectionTitle("Comment était la difficulté ?"));
        survey.addView(centerText("Chaque semaine, ce mini questionnaire permet de recréer un tableau plus adapté à tes forces et à tes faiblesses.", 14, muted, Typeface.NORMAL));

        Button hard = primaryButton("Trop difficile");
        hard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                completeWeeklySurvey("Trop difficile : programme allégé", -1);
            }
        });
        addWithMargins(survey, hard, 0, 12, 0, 8);

        Button ok = secondaryButton("Difficulté parfaite");
        ok.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                completeWeeklySurvey("Difficulté parfaite : programme conservé", 0);
            }
        });
        addWithMargins(survey, ok, 0, 0, 0, 8);

        Button easy = secondaryButton("Trop facile");
        easy.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                completeWeeklySurvey("Trop facile : programme renforcé", 1);
            }
        });
        addWithMargins(survey, easy, 0, 0, 0, 0);
        addWithMargins(body, survey, 0, 8, 0, 12);
    }

    private void completeWeeklySurvey(String answer, int direction) {
        lastPlanSurveyWeek = weekKey();
        lastPlanSurveyAnswer = answer;
        surveyPending = false;
        if (direction != 0) {
            adjustPlan(direction);
        }
        saveProfile(true);
        showPlan();
    }

    private void addPlanTable(LinearLayout body, ArrayList<DayPlan> plan) {
        body.addView(sectionTitle("Tableau sport"));
        body.addView(small("Fais glisser le tableau avec ton doigt pour voir toute la semaine."));

        addPlanTableScroll(body, plan, 230);
    }

    private void addPlanTableScroll(LinearLayout body, ArrayList<DayPlan> plan, int columnWidthDp) {

        HorizontalScrollView scroll = new HorizontalScrollView(this);
        scroll.setHorizontalScrollBarEnabled(false);
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, dp(8), dp(12), dp(8));

        for (int i = 0; i < plan.size(); i++) {
            DayPlan day = plan.get(i);
            LinearLayout column = new LinearLayout(this);
            column.setOrientation(LinearLayout.VERTICAL);
            column.setGravity(Gravity.CENTER_HORIZONTAL);
            column.setPadding(dp(12), dp(14), dp(12), dp(14));
            column.setBackground(round(cardSoft, dp(18), stroke, 1));
            column.addView(kicker(day.day.toUpperCase(Locale.FRANCE)));
            column.addView(centerText(day.focus, 16, text, Typeface.BOLD));

            for (int j = 0; j < day.exercises.size(); j++) {
                column.addView(planExerciseCell(day.exercises.get(j)));
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(columnWidthDp), -2);
            params.setMargins(0, 0, dp(10), 0);
            row.addView(column, params);
        }

        scroll.addView(row, new HorizontalScrollView.LayoutParams(-2, -2));
        addWithMargins(body, scroll, 0, 8, 0, 12);
    }

    private LinearLayout planExerciseCell(String exercise) {
        LinearLayout cell = new LinearLayout(this);
        cell.setOrientation(LinearLayout.VERTICAL);
        cell.setGravity(Gravity.CENTER_HORIZONTAL);
        cell.setPadding(dp(10), dp(10), dp(10), dp(10));
        cell.setBackground(round(Color.rgb(10, 24, 31), dp(14), Color.rgb(35, 78, 89), 1));

        TextView name = centerText(exerciseName(exercise), 14, text, Typeface.BOLD);
        name.setLineSpacing(dp(3), 1.0f);
        name.setMaxLines(2);
        name.setEllipsize(TextUtils.TruncateAt.END);
        cell.addView(name);
        cell.addView(planInfoLine("Séries : " + exerciseSeries(exercise)));
        cell.addView(planInfoLine("Répétitions : " + exerciseReps(exercise)));
        cell.addView(planInfoLine("Poids : " + exerciseWeight(exercise)));
        cell.addView(planInfoLine("Repos : " + exerciseRest(exercise)));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, dp(10), 0, 0);
        cell.setLayoutParams(params);
        return cell;
    }

    private TextView planInfoLine(String value) {
        TextView line = centerText(value, 12, muted, Typeface.NORMAL);
        line.setMaxLines(2);
        line.setEllipsize(TextUtils.TruncateAt.END);
        line.setHorizontallyScrolling(false);
        line.setLineSpacing(1, 1.0f);
        return line;
    }

    private Uri createPlanPng() throws Exception {
        Bitmap bitmap = Bitmap.createBitmap(1800, 1200, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawPlanPng(canvas, 1800, 1200);

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "VALHALLA-RAGE-tableau-" + weekKey() + ".png");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/VALHALLA RAGE");
        }

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri == null) {
            throw new IllegalStateException("Création du fichier impossible");
        }
        OutputStream output = getContentResolver().openOutputStream(uri);
        if (output == null) {
            throw new IllegalStateException("Ouverture du fichier impossible");
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
        output.close();
        bitmap.recycle();
        return uri;
    }

    private void drawPlanPng(Canvas canvas, int width, int height) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(new LinearGradient(0, 0, width, height, Color.rgb(5, 15, 22), Color.rgb(13, 43, 48), Shader.TileMode.CLAMP));
        canvas.drawRect(0, 0, width, height, paint);
        paint.setShader(null);

        paint.setColor(Color.rgb(244, 250, 250));
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(52);
        canvas.drawText("VALHALLA RAGE - Tableau d'entraînement", 58, 78, paint);

        paint.setColor(Color.rgb(180, 205, 205));
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(26);
        canvas.drawText(userName + " • " + goal + " • " + weekRangeText() + " • " + adjustmentLabel(), 60, 120, paint);

        drawCoachForPng(canvas, paint, width);

        ArrayList<DayPlan> plan = buildPlan();
        float margin = 48;
        float gap = 10;
        float top = 250;
        float tableHeight = 880;
        float columnWidth = (width - margin * 2 - gap * 6) / 7f;

        for (int i = 0; i < plan.size(); i++) {
            DayPlan day = plan.get(i);
            float left = margin + i * (columnWidth + gap);
            drawPngDayColumn(canvas, paint, day, left, top, columnWidth, tableHeight);
        }

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(24);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setColor(Color.rgb(161, 241, 83));
        canvas.drawText("À cocher après chaque séance : réalisé • partiel • repos", width / 2f, 1160, paint);
    }

    private void drawCoachForPng(Canvas canvas, Paint paint, int width) {
        Bitmap coach = BitmapFactory.decodeResource(getResources(), R.drawable.viking_mascot);
        if (coach != null) {
            Rect src = new Rect(0, 0, coach.getWidth(), coach.getHeight());
            Rect dst = new Rect(width - 270, 28, width - 88, 178);
            canvas.drawBitmap(coach, src, dst, paint);
        }

        RectF bubble = new RectF(810, 44, width - 410, 130);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(18, 76, 83));
        canvas.drawRoundRect(bubble, 28, 28, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);
        paint.setColor(Color.rgb(27, 214, 196));
        canvas.drawRoundRect(bubble, 28, 28, paint);
        paint.setStyle(Paint.Style.FILL);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(18);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.LEFT);
        drawWrappedText(canvas, paint, "Reste régulier : une bonne technique construit la vraie force.", bubble.left + 20, bubble.top + 31, bubble.width() - 40, 2, 22);
    }

    private void drawPngDayColumn(Canvas canvas, Paint paint, DayPlan day, float left, float top, float width, float height) {
        RectF column = new RectF(left, top, left + width, top + height);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(18, 31, 39));
        canvas.drawRoundRect(column, 24, 24, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setColor(Color.rgb(64, 99, 107));
        canvas.drawRoundRect(column, 24, 24, paint);
        paint.setStyle(Paint.Style.FILL);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(25);
        paint.setColor(Color.rgb(27, 214, 196));
        canvas.drawText(day.day.toUpperCase(Locale.FRANCE), left + width / 2f, top + 38, paint);

        paint.setTextSize(22);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.LEFT);
        drawWrappedText(canvas, paint, day.focus, left + 14, top + 72, width - 28, 2, 26);

        float exerciseTop = top + 126;
        float available = height - 148;
        float exerciseHeight = Math.max(112, (available - (day.exercises.size() - 1) * 8) / Math.max(1, day.exercises.size()));
        for (int i = 0; i < day.exercises.size(); i++) {
            float y = exerciseTop + i * (exerciseHeight + 8);
            drawPngExercise(canvas, paint, day.exercises.get(i), left + 10, y, width - 20, exerciseHeight);
        }
        paint.setTextAlign(Paint.Align.LEFT);
    }

    private void drawPngExercise(Canvas canvas, Paint paint, String exercise, float left, float top, float width, float height) {
        RectF rect = new RectF(left, top, left + width, top + height);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(7, 20, 28));
        canvas.drawRoundRect(rect, 18, 18, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        paint.setColor(Color.rgb(40, 79, 90));
        canvas.drawRoundRect(rect, 18, 18, paint);
        paint.setStyle(Paint.Style.FILL);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        paint.setTextSize(18);
        paint.setColor(Color.WHITE);
        float y = top + 25;
        drawWrappedText(canvas, paint, compactPlanText(exerciseName(exercise), 24), left + 12, y, width - 24, 1, 20);

        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        paint.setTextSize(15);
        paint.setColor(Color.rgb(195, 212, 216));
        canvas.drawText("Séries : " + compactPlanText(exerciseSeries(exercise), 16), left + 12, y + 22, paint);
        canvas.drawText("Rép. : " + compactPlanText(exerciseReps(exercise), 20), left + 12, y + 43, paint);
        canvas.drawText("Poids : " + compactPlanText(exerciseWeight(exercise), 20), left + 12, y + 64, paint);
        canvas.drawText("Repos : " + compactPlanText(exerciseRest(exercise), 20), left + 12, y + 85, paint);
    }

    private String compactPlanText(String value, int maxChars) {
        String compact = value == null ? "" : value.replace("répétitions", "rép.").replace("minutes", "min");
        if (compact.length() <= maxChars) return compact;
        return compact.substring(0, Math.max(1, maxChars - 3)).trim() + "...";
    }

    private float drawWrappedText(Canvas canvas, Paint paint, String value, float x, float y, float maxWidth, int maxLines, float lineHeight) {
        String[] words = value.split(" ");
        String line = "";
        int lines = 0;
        for (int i = 0; i < words.length; i++) {
            String candidate = line.length() == 0 ? words[i] : line + " " + words[i];
            if (paint.measureText(candidate) <= maxWidth || line.length() == 0) {
                line = candidate;
            } else {
                canvas.drawText(line, x, y + lines * lineHeight, paint);
                lines++;
                line = words[i];
                if (lines >= maxLines) return y + lines * lineHeight;
            }
        }
        if (line.length() > 0 && lines < maxLines) {
            canvas.drawText(line, x, y + lines * lineHeight, paint);
            lines++;
        }
        return y + lines * lineHeight;
    }

    private void showSessionValidation() {
        processSessionDeadline();
        trackCurrentSessionDeadline();
        final DayPlan day = todayPlan();
        final ArrayList<SessionExercise> session = new ArrayList<SessionExercise>();

        LinearLayout body = page();
        body.setPadding(dp(20), dp(22), dp(20), dp(22));
        body.addView(header("Séance du jour", day.focus));
        body.addView(subtitle("Les exercices sont verrouillés. Renseigne seulement les chiffres réalisés, ou valide toute la séance en un clic."));
        body.addView(space(10));

        for (int i = 0; i < day.exercises.size(); i++) {
            String exercise = day.exercises.get(i);
            final SessionExercise item = new SessionExercise(exerciseName(exercise), exercise, targetDoneValue(exercise));
            item.timed = isTimedExercise(exercise);
            session.add(item);

            final LinearLayout exerciseCard = card();
            exerciseCard.addView(sectionTitle(item.name));
            exerciseCard.addView(infoLine("Prévu", item.done));
            exerciseCard.addView(infoLine("Poids conseillé", exerciseWeight(exercise)));
            exerciseCard.addView(infoLine("Repos", exerciseRest(exercise)));

            if (item.timed) {
                final EditText timeInput = input("Temps réalisé en secondes", false, true);
                item.timeInput = timeInput;
                addWithMargins(exerciseCard, timeInput, 0, 12, 0, 0);
            } else {
                LinearLayout numbers = new LinearLayout(this);
                numbers.setOrientation(LinearLayout.HORIZONTAL);
                final EditText repsInput = input("Répétitions réalisées", false, true);
                final EditText weightInput = input("Kilos utilisés", false, true);
                item.repsInput = repsInput;
                item.weightInput = weightInput;
                numbers.addView(repsInput, new LinearLayout.LayoutParams(0, dp(56), 1));
                LinearLayout.LayoutParams weightParams = new LinearLayout.LayoutParams(0, dp(56), 1);
                weightParams.setMargins(dp(8), 0, 0, 0);
                numbers.addView(weightInput, weightParams);
                addWithMargins(exerciseCard, numbers, 0, 12, 0, 0);
            }
            addWithMargins(body, exerciseCard, 0, 0, 0, 12);
        }

        Button complete = primaryButton("J'ai fait toute la séance");
        complete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showSessionResult(true, 0, 0);
            }
        });
        addWithMargins(body, complete, 0, 8, 0, 10);

        Button modified = secondaryButton("Valider avec mes modifications");
        modified.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int skipped = 0;
                int changed = 0;
                for (int i = 0; i < session.size(); i++) {
                    SessionExercise item = session.get(i);
                    if (!item.hasResult()) {
                        skipped++;
                    } else if (item.isModified()) {
                        changed++;
                    }
                }
                showSessionResult(skipped == 0, skipped, changed);
            }
        });
        addWithMargins(body, modified, 0, 0, 0, 10);

        Button courage = quietButton("Je n'ai pas eu le courage cette fois-ci");
        courage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deferSessionForToday();
            }
        });
        addWithMargins(body, courage, 0, 0, 0, 10);

        Button back = quietButton("Retour accueil");
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDashboard();
            }
        });
        body.addView(back);
        mount(body, true, "home");
    }

    private void deferSessionForToday() {
        trackCurrentSessionDeadline();
        String today = dateKey(Calendar.getInstance());
        if (historyStatus(today).length() > 0) {
            showDashboard();
            return;
        }
        boolean firstDeferral = !today.equals(trackedSessionDate) || trackedSessionSince <= 0L;
        if (firstDeferral) {
            trackedSessionDate = today;
            trackedSessionSince = System.currentTimeMillis();
            valhallaCoins += 3;
            saveProfile(true);
        }
        showSessionDeferred(firstDeferral);
    }

    private void showSessionDeferred(boolean rewardGranted) {
        LinearLayout body = page();
        body.setGravity(Gravity.CENTER_HORIZONTAL);
        body.setPadding(dp(22), dp(30), dp(22), dp(28));
        body.addView(mascot(dp(235)));
        body.addView(kicker("MESSAGE DU COACH"));
        body.addView(title("Ça peut arriver"));
        body.addView(subtitle("Même les meilleurs jours ont parfois moins d'énergie. Reviens quand tu peux, sans culpabiliser : une séance imparfaite vaut mieux qu'un abandon."));
        body.addView(space(14));
        body.addView(centerText(rewardGranted ? "+3 pièces pour ta sincérité" : "Ta séance reste ouverte pendant 24 heures.", 16, green, Typeface.BOLD));
        body.addView(centerText("Après 24 heures sans validation, la séance sera notée comme non faite et 15 pièces seront retirées.", 14, muted, Typeface.NORMAL));

        Button back = primaryButton("Retour à l'accueil");
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showDashboard(); }
        });
        addWithMargins(body, back, 0, 20, 0, 10);
        mount(body, true, "home");
    }

    private void showSessionResult(final boolean complete, int skipped, int changed) {
        playVictorySound();
        String todayKey = dateKey(Calendar.getInstance());
        boolean firstValidationToday = historyStatus(todayKey).length() == 0;
        recordSession(complete ? "complete" : "partial");
        if (todayKey.equals(trackedSessionDate)) {
            trackedSessionDate = "";
            trackedSessionSince = 0L;
            saveProfile(true);
        }
        int earnedXp = awardSessionXp(complete, skipped, changed);
        if (firstValidationToday) {
            valhallaCoins += complete ? 10 : 4;
            prefs.edit().putInt("valhallaCoins", valhallaCoins).apply();
        }
        syncSessionCompletionToServer(complete, skipped, changed);

        LinearLayout body = page();
        body.setGravity(Gravity.CENTER_HORIZONTAL);
        body.setPadding(dp(22), dp(30), dp(22), dp(28));
        body.addView(mascot(dp(245)));
        body.addView(kicker("SÉANCE VALIDÉE"));
        body.addView(centerText("+" + earnedXp + " XP", 28, green, Typeface.BOLD));
        body.addView(centerText("Chaîne actuelle : " + completeWorkoutStreak() + " jour(s)", 14, text, Typeface.BOLD));

        if (complete) {
            body.addView(title("Bravo, séance complète"));
            body.addView(subtitle(fullSessionCoachProposal()));

            Button apply = primaryButton(progressionButtonLabel());
            apply.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    adjustPlan(1);
                    saveProfile(true);
                    showPlan();
                }
            });
            addWithMargins(body, apply, 0, 18, 0, 10);

            Button keep = quietButton("Laisser le programme tel quel");
            keep.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showDashboard();
                }
            });
            addWithMargins(body, keep, 0, 0, 0, 10);
        } else {
            body.addView(title("Séance ajustée"));
            body.addView(subtitle(partialSessionCoachProposal(skipped, changed)));

            Button lighten = primaryButton("Alléger les prochaines séances");
            lighten.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    adjustPlan(-1);
                    saveProfile(true);
                    showPlan();
                }
            });
            addWithMargins(body, lighten, 0, 18, 0, 10);

            Button keep = secondaryButton("Laisser le programme tel quel");
            keep.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    showDashboard();
                }
            });
            addWithMargins(body, keep, 0, 0, 0, 10);
        }

        Button coach = quietButton("En parler au coach");
        coach.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showCoach();
            }
        });
        body.addView(coach);
        mount(body, true, "home");
    }

    private void showCoach() {
        LinearLayout body = page();
        body.setPadding(dp(20), dp(22), dp(20), dp(22));
        body.addView(header("Coach IA", "Pose ta question"));

        LinearLayout intro = cardAccent();
        intro.addView(mascot(dp(150)));
        intro.addView(sectionTitle("Choisis ce que tu veux faire"));
        intro.addView(centerText(coachModeText(), 15, text, Typeface.NORMAL));
        addWithMargins(body, intro, 0, 12, 0, 12);

        chatLog = new LinearLayout(this);
        chatLog.setOrientation(LinearLayout.VERTICAL);
        chatLog.setGravity(Gravity.CENTER_HORIZONTAL);
        addBotMessage("Salut " + userName + ". Choisis une action rapide, ou écris ta question si tu veux être plus précis.");
        body.addView(chatLog);
        addCoachQuickActions(body);

        Button table = secondaryButton("Créer le tableau de la semaine");
        table.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String message = "Crée mon tableau de sport de la semaine.";
                addUserMessage(message);
                TextView pending = addBotMessage("Je prépare ton tableau avec le serveur...");
                requestAiCoach(message, pending);
            }
        });
        addWithMargins(body, table, 0, 10, 0, 10);

        chatInput = input("Écris au coach VALHALLA RAGE", false, false);
        addWithMargins(body, chatInput, 0, 0, 0, 10);
        Button send = primaryButton("Envoyer");
        send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String message = chatInput.getText().toString().trim();
                if (message.length() == 0) {
                    return;
                }
                chatInput.setText("");
                addUserMessage(message);
                if (coachWaitingForPlanFeedback) {
                    handleCoachPlanFeedback(message);
                    return;
                }
                TextView pending = addBotMessage("Je réfléchis avec ton profil et ton tableau...");
                requestAiCoach(message, pending);
            }
        });
        body.addView(send);

        addWithMargins(body, small(coachFooterText()), 0, 12, 0, 0);
        mount(body, true, "coach");
    }

    private String coachModeText() {
        if (serverAiKeyConfigured) {
            return "IA réelle active avec le serveur " + serverAiModel + ". Tu peux utiliser les choix guidés ou écrire librement.";
        }
        return "Mode guidé local actif. La zone libre reste disponible, et deviendra une vraie discussion intelligente quand la clé IA sera branchée au serveur.";
    }

    private String coachFooterText() {
        if (serverAiKeyConfigured) return "Serveur IA actif : les réponses libres peuvent utiliser l'IA connectée.";
        return "Serveur IA sans clé OpenAI : les choix guidés restent fiables, les réponses libres utilisent le mode local de test.";
    }

    private void addCoachQuickActions(LinearLayout body) {
        LinearLayout actions = card();
        actions.addView(sectionTitle("Actions rapides"));

        final Spinner exerciseSpinner = new Spinner(this);
        final String[] exercises = coachExerciseNames();
        String[] spinnerValues = new String[exercises.length + 1];
        spinnerValues[0] = "Choisir un exercice";
        System.arraycopy(exercises, 0, spinnerValues, 1, exercises.length);
        android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exerciseSpinner.setAdapter(adapter);
        actions.addView(exerciseSpinner, new LinearLayout.LayoutParams(-1, dp(54)));

        Button advice = primaryButton("Conseil exercice");
        advice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int selected = exerciseSpinner.getSelectedItemPosition();
                if (selected <= 0) {
                    flashMessage("Choisis d'abord un exercice.");
                    return;
                }
                String exercise = exercises[selected - 1];
                addUserMessage("Conseil exercice : " + exercise);
                addBotMessage(fullExerciseAdvice(exercise));
            }
        });
        addWithMargins(actions, advice, 0, 10, 0, 8);

        Button harder = secondaryButton("Augmenter la difficulté du programme");
        harder.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String message = "Augmenter la difficulté du programme";
                addUserMessage(message);
                showCoachPlanProposal("c'est trop facile", "harder");
            }
        });
        addWithMargins(actions, harder, 0, 0, 0, 8);

        Button easier = secondaryButton("Rendre le programme plus facile");
        easier.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String message = "Rendre le programme plus facile";
                addUserMessage(message);
                showCoachPlanProposal("c'est trop dur", "easier");
            }
        });
        addWithMargins(actions, easier, 0, 0, 0, 0);
        addWithMargins(body, actions, 0, 0, 0, 12);
    }

    private void showProfile() {
        LinearLayout body = page();
        body.setPadding(dp(20), dp(22), dp(20), dp(22));
        body.addView(header("Profil", "Tes réglages VALHALLA RAGE"));

        LinearLayout account = cardAccent();
        account.addView(sectionTitle(userName));
        account.addView(centerText("LVL " + playerLevel() + " • " + xpProgressInLevel() + " / " + xpPerLevel() + " XP\n" + coinAmount(valhallaCoins) + " du Valhalla", 15, text, Typeface.BOLD));
        Button settings = secondaryButton("Paramètre");
        settings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showSettings(); }
        });
        addWithMargins(account, settings, 0, 14, 0, 0);
        addWithMargins(body, account, 0, 12, 0, 14);

        LinearLayout profile = card();
        profile.addView(sectionTitle("Profil sportif"));
        profile.addView(infoLine("Genre", gender));
        profile.addView(infoLine("Taille", height + " cm"));
        profile.addView(infoLine("Poids", weight + " kg"));
        profile.addView(infoLine("Objectif", goal));
        profile.addView(infoLine("Niveau", level));
        profile.addView(infoLine("Matériel", equipment));
        profile.addView(infoLine("Alimentation", eating));
        addWithMargins(body, profile, 0, 12, 0, 14);

        Button redo = primaryButton("Refaire le questionnaire");
        redo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showQuestion(0);
            }
        });
        addWithMargins(body, redo, 0, 0, 0, 10);

        Button logout = quietButton("Déconnexion");
        logout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                serverAuthToken = "";
                serverUserId = "";
                prefs.edit().putBoolean("rememberConnection", false).putString("serverAuthToken", "").putString("serverUserId", "").apply();
                rememberConnection = false;
                showLogin();
            }
        });
        body.addView(logout);
        mount(body, true, "profile");
    }

    private void showSettings() {
        LinearLayout body = page();
        body.setPadding(dp(20), dp(22), dp(20), dp(22));
        body.addView(header("Paramètre", "Compte et sons"));

        addPersonalSettings(body);
        addSoundSettings(body);
        addAiSettings(body);

        Button back = quietButton("Retour au profil");
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showProfile(); }
        });
        body.addView(back);
        mount(body, true, "profile");
    }

    private void addPersonalSettings(LinearLayout body) {
        LinearLayout personal = card();
        personal.addView(sectionTitle("Infos perso"));
        personal.addView(infoLine("XP total", xp + " points"));
        final EditText usernameInput = input("Nom d'utilisateur", false, false);
        usernameInput.setText(userName);
        final EditText newPasswordInput = input("Nouveau mot de passe", true, false);
        final EditText confirmPasswordInput = input("Confirmer le mot de passe", true, false);
        final TextView personalStatus = small("");
        addWithMargins(personal, usernameInput, 0, 12, 0, 10);
        addWithMargins(personal, newPasswordInput, 0, 0, 0, 10);
        addWithMargins(personal, confirmPasswordInput, 0, 0, 0, 10);
        addWithMargins(personal, personalStatus, 0, 0, 0, 10);
        Button savePersonal = primaryButton("Enregistrer mes infos");
        savePersonal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String newName = usernameInput.getText().toString().trim();
                String newPassword = newPasswordInput.getText().toString();
                String confirmPassword = confirmPasswordInput.getText().toString();
                if (newName.length() < 3) {
                    personalStatus.setText("Le nom d'utilisateur doit avoir au moins 3 caractères.");
                    return;
                }
                final String previousPassword = accountPass;
                if (newPassword.length() > 0 && newPassword.length() < 6) {
                    personalStatus.setText("Le nouveau mot de passe doit avoir au moins 6 caractères.");
                    return;
                }
                if (newPassword.length() > 0 && !newPassword.equals(confirmPassword)) {
                    personalStatus.setText("Les deux mots de passe ne sont pas identiques.");
                    return;
                }
                userName = newName;
                accountUser = newName;
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("userName", userName);
                editor.putString("accountUser", accountUser);
                if (newPassword.length() > 0) {
                    accountPass = newPassword;
                    editor.putString("accountPass", accountPass);
                    newPasswordInput.setText("");
                    confirmPasswordInput.setText("");
                    syncPasswordToServerAsync(previousPassword, newPassword);
                }
                editor.apply();
                saveProfile(true);
                syncProfileToServerAsync();
                personalStatus.setText("Infos personnelles enregistrées.");
            }
        });
        personal.addView(savePersonal);
        Button exportAccount = secondaryButton("Exporter mes donnees serveur");
        exportAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                exportServerData();
            }
        });
        addWithMargins(personal, exportAccount, 0, 10, 0, 0);
        Button deleteAccount = quietButton("Supprimer mon compte serveur");
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDeleteAccountConfirm();
            }
        });
        addWithMargins(personal, deleteAccount, 0, 10, 0, 0);
        addWithMargins(body, personal, 0, 12, 0, 14);
    }

    private void exportServerData() {
        if (serverAuthToken.length() == 0) {
            Toast.makeText(this, "Aucun compte serveur connecte.", Toast.LENGTH_LONG).show();
            return;
        }
        Toast.makeText(this, "Export des donnees serveur...", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    final JSONObject result = callServerJson("GET", "/me/export", null, serverAuthToken);
                    handler.post(new Runnable() {
                        public void run() {
                            Intent send = new Intent(Intent.ACTION_SEND);
                            send.setType("text/plain");
                            send.putExtra(Intent.EXTRA_SUBJECT, "Export VALHALLA RAGE");
                            send.putExtra(Intent.EXTRA_TEXT, result.toString());
                            startActivity(Intent.createChooser(send, "Exporter mes donnees VALHALLA RAGE"));
                        }
                    });
                } catch (final Exception error) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Export impossible : " + error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).start();
    }

    private void showDeleteAccountConfirm() {
        LinearLayout body = page();
        body.setGravity(Gravity.CENTER_HORIZONTAL);
        body.setPadding(dp(24), dp(42), dp(24), dp(28));
        body.addView(header("Compte", "Supprimer mes donnees"));
        body.addView(subtitle("Cette action supprime le compte et les donnees stockees sur le serveur de test. L'application locale sera deconnectee."));
        body.addView(space(18));

        LinearLayout warning = cardAccent();
        warning.addView(sectionTitle("Confirmation"));
        warning.addView(centerText("A utiliser seulement si tu veux vraiment effacer le compte serveur VALHALLA RAGE.", 15, text, Typeface.BOLD));
        addWithMargins(body, warning, 0, 0, 0, 14);

        Button confirm = primaryButton("Oui, supprimer mon compte serveur");
        confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteServerAccount();
            }
        });
        addWithMargins(body, confirm, 0, 0, 0, 10);

        Button back = quietButton("Annuler");
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { showSettings(); }
        });
        body.addView(back);
        mount(body, false, "");
    }

    private void deleteServerAccount() {
        if (serverAuthToken.length() == 0) {
            Toast.makeText(this, "Aucun compte serveur connecte.", Toast.LENGTH_LONG).show();
            showSettings();
            return;
        }
        Toast.makeText(this, "Suppression du compte serveur...", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    callServerJson("DELETE", "/me", null, serverAuthToken);
                    handler.post(new Runnable() {
                        public void run() {
                            serverAuthToken = "";
                            accountUser = "";
                            accountPass = "";
                            rememberConnection = false;
                            prefs.edit()
                                    .putString("serverAuthToken", "")
                                    .putString("accountUser", "")
                                    .putString("accountPass", "")
                                    .putBoolean("rememberConnection", false)
                                    .apply();
                            Toast.makeText(MainActivity.this, "Compte serveur supprime.", Toast.LENGTH_LONG).show();
                            showLogin();
                        }
                    });
                } catch (final Exception error) {
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(MainActivity.this, "Suppression impossible : " + error.getMessage(), Toast.LENGTH_LONG).show();
                            showSettings();
                        }
                    });
                }
            }
        }).start();
    }

    private void addSoundSettings(LinearLayout body) {
        LinearLayout sounds = card();
        sounds.addView(sectionTitle("Sons"));
        final CheckBox enabled = new CheckBox(this);
        enabled.setText("Activer les sons");
        enabled.setTextColor(text);
        enabled.setTextSize(15);
        enabled.setGravity(Gravity.CENTER);
        enabled.setChecked(soundEnabled);
        enabled.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                soundEnabled = enabled.isChecked();
                prefs.edit().putBoolean("soundEnabled", soundEnabled).apply();
            }
        });
        sounds.addView(enabled);
        sounds.addView(centerText("Son de notification actuel : " + notificationSound, 14, muted, Typeface.BOLD));
        addNotificationSoundButton(sounds, "Clair");
        addNotificationSoundButton(sounds, "Foudre");
        addNotificationSoundButton(sounds, "Tambour");
        addWithMargins(body, sounds, 0, 0, 0, 14);
    }

    private void addNotificationSoundButton(LinearLayout parent, final String sound) {
        Button button = secondaryButton(sound);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                notificationSound = sound;
                prefs.edit().putString("notificationSound", notificationSound).apply();
                playNotificationSound();
                showSettings();
            }
        });
        addWithMargins(parent, button, 0, 8, 0, 0);
    }

    private void addAiSettings(LinearLayout body) {
        LinearLayout aiSettings = card();
        aiSettings.addView(sectionTitle("Serveur IA"));
        aiSettings.addView(centerText("Adresse utilisée par le coach, le compte et le programme serveur.", 14, muted, Typeface.NORMAL));
        aiSettings.addView(centerText("État : " + serverConnectionStatus, 14, "Connecté".equals(serverConnectionStatus) ? green : muted, Typeface.BOLD));
        aiSettings.addView(centerText("IA réelle : " + (serverAiKeyConfigured ? "active (" + serverAiModel + ")" : "non activée"), 14, serverAiKeyConfigured ? green : muted, Typeface.BOLD));
        final EditText serverInput = input("Adresse serveur IA", false, false);
        serverInput.setText(aiServerUrl);
        addWithMargins(aiSettings, serverInput, 0, 12, 0, 10);
        Button saveServer = secondaryButton("Enregistrer le serveur IA");
        saveServer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String value = serverInput.getText().toString().trim();
                if (value.length() > 0) {
                    aiServerUrl = value;
                    prefs.edit().putString("aiServerUrl", aiServerUrl).apply();
                    showSettings();
                }
            }
        });
        aiSettings.addView(saveServer);
        Button testServer = quietButton("Tester la connexion serveur");
        testServer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String value = serverInput.getText().toString().trim();
                if (value.length() > 0) {
                    aiServerUrl = value;
                    prefs.edit().putString("aiServerUrl", aiServerUrl).apply();
                }
                testServerConnection();
            }
        });
        addWithMargins(aiSettings, testServer, 0, 10, 0, 0);
        addWithMargins(body, aiSettings, 0, 0, 0, 14);
    }

    private void addFeedPhotos(LinearLayout parent) {
        if (feedPhotos.length() == 0) {
            parent.addView(small("Aucune photo partagée pour le moment."));
            return;
        }
        String[] photos = feedPhotos.split("\\|");
        for (int i = 0; i < photos.length && i < 6; i++) {
            if (photos[i].length() == 0) continue;
            LinearLayout post = card();
            post.addView(infoLine(userName, "publication locale"));
            ImageView image = new ImageView(this);
            image.setImageURI(Uri.parse(photos[i]));
            image.setScaleType(ImageView.ScaleType.CENTER_CROP);
            addWithMargins(post, image, 0, 8, 0, 8, -1, dp(190));
            post.addView(centerText("En attente de modération automatique dans la version en ligne.", 12, muted, Typeface.NORMAL));
            addWithMargins(parent, post, 0, 8, 0, 8);
        }
    }

    private void saveAnswer(String key, String value) {
        if ("goal".equals(key)) {
            goal = value;
        } else if ("level".equals(key)) {
            level = value;
        } else if ("frequency".equals(key)) {
            frequency = value;
        } else if ("place".equals(key)) {
            place = value;
        } else if ("equipment".equals(key)) {
            equipment = value;
        } else if ("sessionTime".equals(key)) {
            sessionTime = value;
        } else if ("eating".equals(key)) {
            eating = value;
        } else if ("foodChange".equals(key)) {
            foodChange = value;
        }
        saveProfile(false);
    }

    private String todayWorkout() {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.MONDAY) return "Haut du corps + gainage";
        if (day == Calendar.TUESDAY) return "Cardio progressif + abdos";
        if (day == Calendar.WEDNESDAY) return "Jambes + mobilité";
        if (day == Calendar.THURSDAY) return "Repos actif";
        if (day == Calendar.FRIDAY) return "Full body contrôlé";
        if (day == Calendar.SATURDAY) return "Course légère ou circuit";
        return "Récupération et étirements";
    }

    private DayPlan todayPlan() {
        ArrayList<DayPlan> plan = buildPlan();
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        if (day == Calendar.MONDAY) return plan.get(0);
        if (day == Calendar.TUESDAY) return plan.get(1);
        if (day == Calendar.WEDNESDAY) return plan.get(2);
        if (day == Calendar.THURSDAY) return plan.get(3);
        if (day == Calendar.FRIDAY) return plan.get(4);
        if (day == Calendar.SATURDAY) return plan.get(5);
        return plan.get(6);
    }

    private String exerciseName(String exercise) {
        int cut = exercise.indexOf("\n");
        if (cut > 0) return exercise.substring(0, cut).trim();
        return exercise.trim();
    }

    private String exerciseSeries(String exercise) {
        String details = exerciseDetails(exercise);
        int cut = details.indexOf(" séries x ");
        if (cut > 0) return details.substring(0, cut).trim();
        return "1";
    }

    private String exerciseReps(String exercise) {
        String details = exerciseDetails(exercise);
        int start = details.indexOf(" séries x ");
        if (start >= 0) {
            start += " séries x ".length();
        } else {
            start = 0;
        }
        int end = details.indexOf("  •  poids ");
        if (end < 0) end = details.indexOf(" • poids ");
        if (end < 0) end = details.indexOf("  •  repos ");
        if (end < 0) end = details.indexOf(" • repos ");
        if (end < 0) end = details.indexOf("repos ");
        if (end > start) return details.substring(start, end).replace("•", "").trim();
        return details.substring(start).replace("•", "").trim();
    }

    private String exerciseRest(String exercise) {
        String details = exerciseDetails(exercise);
        int rest = details.indexOf("repos ");
        if (rest >= 0) {
            String value = details.substring(rest + 6).trim();
            int bullet = value.indexOf("•");
            if (bullet >= 0) value = value.substring(0, bullet).trim();
            return value;
        }
        int bullet = details.indexOf("•");
        if (bullet >= 0) return details.substring(bullet + 1).trim();
        return "Selon forme";
    }

    private String exerciseDetails(String exercise) {
        int cut = exercise.indexOf("\n");
        if (cut < 0 || cut + 1 >= exercise.length()) return "";
        return exercise.substring(cut + 1).trim();
    }

    private String exerciseWeight(String exercise) {
        String details = exerciseDetails(exercise);
        int marker = details.indexOf("poids ");
        if (marker >= 0) {
            String value = details.substring(marker + 6).trim();
            int bullet = value.indexOf("•");
            if (bullet >= 0) value = value.substring(0, bullet).trim();
            if (value.length() > 0) return value;
        }
        String name = exerciseName(exercise);
        if (isCardioOrMobilityName(name)) {
            return "Aucun";
        }
        if (isBodyweightExerciseName(name)) {
            return "Poids du corps";
        }
        if (isWeightedExerciseName(name)) return "À renseigner dans performances";
        return "Poids du corps";
    }

    private String gymEntryFor(String exerciseName) {
        if (gymStrengthProfile == null || gymStrengthProfile.length() == 0) return null;
        String wanted = exerciseName.toLowerCase(Locale.FRANCE);
        String[] entries = gymStrengthProfile.split(";");
        for (int i = 0; i < entries.length; i++) {
            String[] parts = entries[i].split("\\|", -1);
            if (parts.length < 3) continue;
            String savedName = parts[0].toLowerCase(Locale.FRANCE);
            if (savedName.equals(wanted) || savedName.indexOf(wanted) >= 0 || wanted.indexOf(savedName) >= 0) {
                return entries[i];
            }
        }
        return null;
    }

    private String gymTrainingReps(String exerciseName, String fallback) {
        String entry = gymEntryFor(exerciseName);
        if (entry == null) return fallback;
        String[] parts = entry.split("\\|", -1);
        if (parts.length < 3) return fallback;
        try {
            int failureReps = Integer.parseInt(parts[2].trim());
            return trainingRepsText(exerciseName, failureReps, fallback);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private String performanceEntryFor(String exerciseName) {
        if (exercisePerformanceProfile == null || exercisePerformanceProfile.length() == 0) return null;
        String wanted = exerciseName.toLowerCase(Locale.FRANCE);
        String[] entries = exercisePerformanceProfile.split(";");
        for (int i = 0; i < entries.length; i++) {
            String[] parts = entries[i].split("\\|", -1);
            if (parts.length < 4) continue;
            String savedName = parts[0].toLowerCase(Locale.FRANCE);
            if (savedName.equals(wanted) || savedName.indexOf(wanted) >= 0 || wanted.indexOf(savedName) >= 0) return entries[i];
        }
        return null;
    }

    private String performanceTrainingReps(String exerciseName, String fallback) {
        String entry = performanceEntryFor(exerciseName);
        if (entry == null) return exactFallbackReps(exerciseName, fallback);
        String[] parts = entry.split("\\|", -1);
        try {
            if (parts[2].length() > 0) {
                int maxPerSet = Integer.parseInt(parts[2]);
                return trainingRepsText(exerciseName, maxPerSet, fallback);
            }
            if (parts[1].length() > 0) {
                int maxTotal = Integer.parseInt(parts[1]);
                return trainingRepsText(exerciseName, maxTotal, fallback);
            }
        } catch (NumberFormatException ignored) {
        }
        return exactFallbackReps(exerciseName, fallback);
    }

    private int performanceSeries(String exerciseName, int fallback) {
        String entry = performanceEntryFor(exerciseName);
        if (entry == null) return fallback;
        String[] parts = entry.split("\\|", -1);
        if (parts.length < 4) return fallback;
        try {
            if (parts[3].length() > 0) {
                int value = Integer.parseInt(parts[3]);
                if (value < 2) return 2;
                return Math.min(6, value);
            }
            if (parts[1].length() > 0) {
                int total = Integer.parseInt(parts[1]);
                if (total <= 10) return 2;
                if (total >= 45) return Math.min(6, fallback + 1);
            }
        } catch (NumberFormatException ignored) {
            return fallback;
        }
        return fallback;
    }

    private int performanceScoreFor(String... hints) {
        if (exercisePerformanceProfile == null || exercisePerformanceProfile.length() == 0) return 0;
        int best = 0;
        String[] entries = exercisePerformanceProfile.split(";");
        for (int i = 0; i < entries.length; i++) {
            String[] parts = entries[i].split("\\|", -1);
            if (parts.length < 4) continue;
            String saved = normalizeName(parts[0]);
            boolean match = false;
            for (int h = 0; h < hints.length; h++) {
                String hint = normalizeName(hints[h]);
                if (saved.indexOf(hint) >= 0 || hint.indexOf(saved) >= 0) {
                    match = true;
                    break;
                }
            }
            if (!match) continue;
            try {
                int score = 0;
                if (parts[1].length() > 0) {
                    score = Integer.parseInt(parts[1]);
                } else if (parts[2].length() > 0 && parts[3].length() > 0) {
                    score = Integer.parseInt(parts[2]) * Integer.parseInt(parts[3]);
                }
                if (score > best) best = score;
            } catch (NumberFormatException ignored) {
            }
        }
        return best;
    }

    private String normalizeName(String value) {
        return value == null ? "" : value.toLowerCase(Locale.FRANCE)
                .replace("é", "e")
                .replace("è", "e")
                .replace("ê", "e")
                .replace("ë", "e")
                .replace("à", "a")
                .replace("â", "a")
                .replace("î", "i")
                .replace("ï", "i")
                .replace("ô", "o")
                .replace("ù", "u")
                .replace("ç", "c")
                .trim();
    }

    private boolean isCardioOrMobilityName(String name) {
        String lower = normalizeName(name);
        return lower.indexOf("marche") >= 0
                || lower.indexOf("course") >= 0
                || lower.indexOf("velo") >= 0
                || lower.indexOf("mobilite") >= 0
                || lower.indexOf("etirement") >= 0
                || lower.indexOf("repos") >= 0
                || lower.indexOf("preparation") >= 0;
    }

    private boolean isBodyweightExerciseName(String name) {
        String lower = normalizeName(name);
        return lower.indexOf("gainage") >= 0
                || lower.indexOf("planche") >= 0
                || lower.indexOf("crunch") >= 0
                || lower.indexOf("mountain") >= 0
                || lower.indexOf("pompe") >= 0
                || lower.indexOf("chaise") >= 0
                || lower.indexOf("superman") >= 0
                || lower.indexOf("squat poids") >= 0
                || lower.indexOf("pont fessier") >= 0
                || lower.indexOf("burpee") >= 0;
    }

    private boolean isWeightedExerciseName(String name) {
        String lower = normalizeName(name);
        if (isCardioOrMobilityName(lower) || isBodyweightExerciseName(lower)) return false;
        return lower.indexOf("developpe") >= 0
                || lower.indexOf("presse") >= 0
                || lower.indexOf("squat guide") >= 0
                || lower.indexOf("squat haltere") >= 0
                || lower.indexOf("souleve") >= 0
                || lower.indexOf("tirage") >= 0
                || lower.indexOf("rowing") >= 0
                || lower.indexOf("curl") >= 0
                || lower.indexOf("triceps") >= 0
                || lower.indexOf("extension") >= 0
                || lower.indexOf("elevation") >= 0
                || lower.indexOf("pec deck") >= 0
                || lower.indexOf("hip thrust") >= 0
                || lower.indexOf("mollets") >= 0
                || lower.indexOf("leg ") >= 0
                || lower.indexOf("machine") >= 0
                || lower.indexOf("haltere") >= 0
                || lower.indexOf("farmer") >= 0
                || lower.indexOf("poulie") >= 0
                || lower.indexOf("dips assiste") >= 0;
    }

    private int targetRepsByGoal(String exerciseName) {
        String lowerGoal = goal.toLowerCase(Locale.FRANCE);
        int reps;
        if (lowerGoal.indexOf("muscle") >= 0) {
            reps = level.indexOf("Avancé") >= 0 ? 8 : 10;
        } else if (lowerGoal.indexOf("poids") >= 0 || lowerGoal.indexOf("tonique") >= 0) {
            reps = 14;
        } else if (lowerGoal.indexOf("reprendre") >= 0 || lowerGoal.indexOf("reprise") >= 0) {
            reps = 10;
        } else {
            reps = 12;
        }
        int adjustment = activePlanAdjustment();
        if (adjustment < 0) reps -= 2;
        if (adjustment > 0) reps += 2;
        if (isWeightedExerciseName(exerciseName) && level.indexOf("Avancé") >= 0 && reps > 10) reps = 10;
        if (reps < 5) reps = 5;
        if (reps > 18) reps = 18;
        return reps;
    }

    private String trainingRepsText(String exerciseName, int maxReps, String fallback) {
        if (isTimedExercise(exerciseName + "\n1 séries x " + fallback)) return exactFallbackReps(exerciseName, fallback);
        int target = targetRepsByGoal(exerciseName);
        if (maxReps > 0) {
            int cap = Math.max(2, maxReps - 2);
            if (target > cap) target = cap;
        }
        if (target < 2) target = 2;
        return target + " reps";
    }

    private String exactFallbackReps(String exerciseName, String fallback) {
        String lower = fallback.toLowerCase(Locale.FRANCE);
        if (lower.indexOf("reps") >= 0) return targetRepsByGoal(exerciseName) + " reps";
        if (lower.indexOf(" à ") >= 0 || lower.indexOf(" a ") >= 0) {
            int first = firstNumber(fallback);
            int second = secondNumber(fallback);
            if (first > 0 && second > 0) {
                int value = Math.round((first + second) / 2f);
                if (lower.indexOf("minute") >= 0) return value + " minutes";
                if (lower.indexOf("min") >= 0) return value + " min";
                if (lower.indexOf(" s") >= 0 || lower.endsWith("s")) return value + " s";
                if (lower.indexOf("pas") >= 0) return value + " pas";
            }
        }
        return fallback.replace("avec marge", "").replace("contrôlées", "").replace("propres", "").trim();
    }

    private int firstNumber(String value) {
        String digits = "";
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (Character.isDigit(c)) {
                digits += c;
            } else if (digits.length() > 0) {
                break;
            }
        }
        if (digits.length() == 0) return 0;
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private int secondNumber(String value) {
        boolean firstDone = false;
        String digits = "";
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (Character.isDigit(c)) {
                digits += c;
            } else if (digits.length() > 0) {
                if (!firstDone) {
                    firstDone = true;
                    digits = "";
                } else {
                    break;
                }
            }
        }
        if (digits.length() == 0 || !firstDone) return 0;
        try {
            return Integer.parseInt(digits);
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private int repsNumberFromText(String repsText) {
        return firstNumber(repsText);
    }

    private float performanceLoadFor(String exerciseName) {
        String entry = performanceEntryFor(exerciseName);
        if (entry != null) {
            String[] parts = entry.split("\\|", -1);
            if (parts.length >= 5 && parts[4].length() > 0) {
                try {
                    return Float.parseFloat(parts[4].replace(",", "."));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        String gymEntry = gymEntryFor(exerciseName);
        if (gymEntry != null) {
            String[] parts = gymEntry.split("\\|", -1);
            if (parts.length >= 2) {
                try {
                    return Float.parseFloat(parts[1].replace(",", "."));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return 0f;
    }

    private int performanceFailureRepsFor(String exerciseName) {
        String entry = performanceEntryFor(exerciseName);
        if (entry != null) {
            String[] parts = entry.split("\\|", -1);
            try {
                if (parts.length >= 3 && parts[2].length() > 0) return Integer.parseInt(parts[2]);
                if (parts.length >= 2 && parts[1].length() > 0) return Integer.parseInt(parts[1]);
            } catch (NumberFormatException ignored) {
            }
        }
        String gymEntry = gymEntryFor(exerciseName);
        if (gymEntry != null) {
            String[] parts = gymEntry.split("\\|", -1);
            try {
                if (parts.length >= 3 && parts[2].length() > 0) return Integer.parseInt(parts[2]);
            } catch (NumberFormatException ignored) {
            }
        }
        return 0;
    }

    private String trainingWeightText(String exerciseName, String repsText) {
        if (isCardioOrMobilityName(exerciseName)) return "Aucun";
        if (isBodyweightExerciseName(exerciseName)) return "Poids du corps";
        if (!isWeightedExerciseName(exerciseName)) return "Poids du corps";
        float failureLoad = performanceLoadFor(exerciseName);
        if (failureLoad <= 0f) return "À renseigner dans performances";
        int targetReps = repsNumberFromText(repsText);
        if (targetReps <= 0) targetReps = targetRepsByGoal(exerciseName);
        int failureReps = performanceFailureRepsFor(exerciseName);
        if (failureReps <= 0) {
            return formatKg(Math.max(0.5f, failureLoad * 0.85f));
        }
        int cappedFailureReps = Math.min(15, Math.max(1, failureReps));
        float estimatedOneRepMax = failureLoad * (1f + cappedFailureReps / 30f);
        float predictedTargetLoad = estimatedOneRepMax / (1f + targetReps / 30f);
        float safety = 0.90f;
        String lowerGoal = goal.toLowerCase(Locale.FRANCE);
        if (lowerGoal.indexOf("reprendre") >= 0 || lowerGoal.indexOf("reprise") >= 0) safety = 0.84f;
        if (lowerGoal.indexOf("poids") >= 0 || lowerGoal.indexOf("tonique") >= 0) safety = 0.88f;
        if (level.indexOf("Avancé") >= 0) safety += 0.02f;
        float recommended = predictedTargetLoad * safety;
        if (recommended > failureLoad && targetReps >= failureReps) recommended = failureLoad * 0.92f;
        return formatKg(roundHalfKg(recommended));
    }

    private float roundHalfKg(float value) {
        return Math.max(0.5f, Math.round(value * 2f) / 2f);
    }

    private String formatKg(float value) {
        float rounded = roundHalfKg(value);
        if (Math.abs(rounded - Math.round(rounded)) < 0.01f) {
            return String.format(Locale.FRANCE, "%.0f kg", rounded);
        }
        return String.format(Locale.FRANCE, "%.1f kg", rounded);
    }

    private String pushPlanExercise(boolean easier, boolean harder, boolean gym) {
        int score = performanceScoreFor("pompe", "dips", "developpe couche", "developpe incline");
        if (score > 0 && score <= 8) return "Pompes murales";
        if (score > 0 && score <= 18) return "Pompes inclinées";
        if (score >= 45) return gym ? "Dips assistés ou développé couché tempo" : "Pompes tempo lentes";
        if (harder) return "Pompes tempo lentes";
        if (easier) return "Pompes inclinées";
        return gym ? "Développé couché" : "Pompes contrôlées";
    }

    private String pullPlanExercise(boolean easier, boolean harder, boolean gym, boolean dumbbells) {
        int score = performanceScoreFor("rowing", "tirage", "traction");
        if (score > 0 && score <= 16) return "Rowing léger contrôlé";
        if (score >= 55) return gym ? "Tirage vertical tempo" : "Rowing un bras lent";
        if (harder) return "Rowing haltères tempo";
        if (easier) return "Rowing léger contrôlé";
        return gym ? "Tirage horizontal" : dumbbells ? "Rowing haltères" : "Rowing avec sac";
    }

    private String shoulderPlanExercise(boolean easier, boolean harder, boolean gym, boolean dumbbells) {
        int score = performanceScoreFor("developpe epaules", "pike", "elevations laterales");
        if (score > 0 && score <= 12) return "Développé épaules léger";
        if (score >= 40) return "Développé épaules avec pause";
        if (harder) return "Développé épaules avec pause";
        if (easier) return "Développé épaules léger";
        return dumbbells || gym ? "Développé épaules" : "Pike push-up";
    }

    private String legPlanExercise(boolean easier, boolean harder, boolean gym, boolean dumbbells) {
        int score = performanceScoreFor("squat", "presse", "fente");
        if (score > 0 && score <= 14) return "Squat assisté";
        if (score >= 70) return gym ? "Presse à cuisses tempo" : "Squat tempo avec pause";
        if (harder) return "Squat tempo avec pause";
        if (easier) return "Squat assisté";
        return gym ? "Presse à cuisses" : dumbbells ? "Squat haltères" : "Squat poids du corps";
    }

    private String hingePlanExercise(boolean easier, boolean harder, boolean gym) {
        int score = performanceScoreFor("fente", "souleve", "hip thrust", "pont");
        if (score > 0 && score <= 14) return "Fentes arrière courtes";
        if (score >= 60) return gym ? "Soulevé de terre roumain tempo" : "Fentes alternées lentes";
        if (harder) return "Fentes alternées lentes";
        if (easier) return "Fentes arrière courtes";
        return gym ? "Soulevé de terre roumain" : "Fentes alternées";
    }

    private String corePlanExercise(String fallback) {
        int score = performanceScoreFor("gainage", "planche");
        if (score > 0 && score <= 20) return "Gainage face";
        if (score >= 90) return "Gainage dynamique";
        return fallback;
    }

    private String cardioPlanExercise() {
        String lowerGoal = goal.toLowerCase(Locale.FRANCE);
        if (lowerGoal.indexOf("poids") >= 0 || lowerGoal.indexOf("tonique") >= 0) return "Marche rapide ou vélo";
        if (level.indexOf("Avancé") >= 0) return "Course légère ou vélo";
        return "Marche rapide ou vélo";
    }

    private String targetDoneValue(String exercise) {
        int cut = exercise.indexOf("\n");
        if (cut < 0 || cut + 1 >= exercise.length()) return "";
        String details = exercise.substring(cut + 1).trim();
        int rest = details.indexOf("•");
        if (rest > 0) {
            details = details.substring(0, rest).trim();
        }
        return details;
    }

    private boolean isTimedExercise(String exercise) {
        String name = exerciseName(exercise).toLowerCase(Locale.FRANCE);
        String reps = exerciseReps(exercise).toLowerCase(Locale.FRANCE);
        return reps.indexOf(" s") >= 0
                || reps.indexOf("minute") >= 0
                || reps.indexOf("min") >= 0
                || name.indexOf("gainage") >= 0
                || name.indexOf("planche") >= 0
                || name.indexOf("chaise") >= 0
                || name.indexOf("marche") >= 0
                || name.indexOf("course") >= 0
                || name.indexOf("mobilité") >= 0
                || name.indexOf("étirement") >= 0
                || name.indexOf("repos") >= 0;
    }

    private void playVictorySound() {
        if (!soundEnabled) return;
        try {
            ToneGenerator tone = new ToneGenerator(AudioManager.STREAM_MUSIC, 80);
            tone.startTone(ToneGenerator.TONE_PROP_ACK, 180);
            handler.postDelayed(new Runnable() {
                public void run() {
                    try {
                        ToneGenerator endTone = new ToneGenerator(AudioManager.STREAM_MUSIC, 65);
                        endTone.startTone(ToneGenerator.TONE_PROP_BEEP2, 120);
                    } catch (RuntimeException ignored) {
                    }
                }
            }, 190);
        } catch (RuntimeException ignored) {
        }
    }

    private void playNotificationSound() {
        if (!soundEnabled) return;
        try {
            ToneGenerator tone = new ToneGenerator(AudioManager.STREAM_MUSIC, 80);
            int first = ToneGenerator.TONE_PROP_BEEP;
            int second = ToneGenerator.TONE_PROP_ACK;
            if ("Foudre".equals(notificationSound)) {
                first = ToneGenerator.TONE_SUP_ERROR;
                second = ToneGenerator.TONE_PROP_BEEP2;
            } else if ("Tambour".equals(notificationSound)) {
                first = ToneGenerator.TONE_PROP_NACK;
                second = ToneGenerator.TONE_PROP_ACK;
            }
            tone.startTone(first, 120);
            final int nextTone = second;
            handler.postDelayed(new Runnable() {
                public void run() {
                    try {
                        ToneGenerator follow = new ToneGenerator(AudioManager.STREAM_MUSIC, 70);
                        follow.startTone(nextTone, 120);
                    } catch (RuntimeException ignored) {
                    }
                }
            }, 150);
        } catch (RuntimeException ignored) {
        }
    }

    private String fullSessionCoachProposal() {
        String lowerGoal = goal.toLowerCase(Locale.FRANCE);
        if (lowerGoal.indexOf("muscle") >= 0) {
            return "Le viking valide : tu as terminé la séance. Pour progresser vers ton objectif muscle, je peux augmenter très légèrement la charge prévue ou ajouter un tempo plus lent sur la descente.";
        }
        if (lowerGoal.indexOf("poids") >= 0 || lowerGoal.indexOf("tonique") >= 0) {
            return "Le viking valide : séance complète. Pour ton objectif, je peux augmenter un peu la cadence, réduire légèrement certains repos ou ajouter quelques minutes de cardio.";
        }
        if (lowerGoal.indexOf("reprise") >= 0 || lowerGoal.indexOf("reprendre") >= 0) {
            return "Le viking valide : séance complète. Comme tu reprends, je propose une progression douce sans brûler les étapes.";
        }
        return "Le viking valide : séance complète. Je peux augmenter légèrement la difficulté pour garder une progression régulière.";
    }

    private String partialSessionCoachProposal(int skipped, int changed) {
        String summary = "Tu as retiré " + skipped + " exercice(s) et modifié " + changed + " résultat(s). ";
        if (skipped > 0 && changed > 0) {
            summary = "Une partie de la séance n'a pas été terminée. ";
        } else if (skipped > 0) {
            summary = "Certains exercices n'ont pas été faits. ";
        } else if (changed > 0) {
            summary = "Certaines séries ou répétitions ont été ajustées. ";
        }
        return summary + "Le coach peut alléger les prochaines séances, ou garder le tableau tel quel si c'était juste une journée moins bonne.";
    }

    private String progressionButtonLabel() {
        String lowerGoal = goal.toLowerCase(Locale.FRANCE);
        if (lowerGoal.indexOf("muscle") >= 0) return "Augmenter légèrement les charges";
        if (lowerGoal.indexOf("poids") >= 0 || lowerGoal.indexOf("tonique") >= 0) return "Augmenter la cadence";
        return "Appliquer une progression douce";
    }

    private int awardSessionXp(boolean complete, int skipped, int changed) {
        int earned;
        if (complete) {
            earned = 120;
            earned += sessionStreakBonusXp();
        } else {
            earned = 80 - skipped * 18 - changed * 10;
            if (earned < 30) earned = 30;
        }
        xp += earned;
        prefs.edit().putInt("xp", xp).apply();
        return earned;
    }

    private void syncSessionCompletionToServer(final boolean complete, final int skipped, final int changed) {
        if (serverAuthToken.length() == 0) return;
        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("date", dateKey(Calendar.getInstance()));
                    payload.put("complete", complete);
                    payload.put("status", complete ? "complete" : "partial");
                    payload.put("skipped", skipped);
                    payload.put("changed", changed);
                    payload.put("profile", profileJson());
                    payload.put("currentPlan", planJson());
                    JSONObject result = callServerJson("POST", "/sessions/complete", payload, serverAuthToken);
                    JSONObject user = result.optJSONObject("user");
                    if (user != null) {
                        final int serverXp = user.optInt("xp", xp);
                        final int serverCoins = user.optInt("coins", valhallaCoins);
                        handler.post(new Runnable() {
                            public void run() {
                                xp = Math.max(xp, serverXp);
                                valhallaCoins = Math.max(valhallaCoins, serverCoins);
                                prefs.edit().putInt("xp", xp).putInt("valhallaCoins", valhallaCoins).apply();
                            }
                        });
                    }
                } catch (Exception error) {
                    serverConnectionStatus = "Hors ligne";
                    prefs.edit().putString("serverConnectionStatus", serverConnectionStatus).apply();
                }
            }
        }).start();
    }

    private void syncWeightToServerAsync(final String date, final String value) {
        if (serverAuthToken.length() == 0) return;
        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("date", date);
                    payload.put("weightKg", value);
                    payload.put("targetWeight", weightTarget);
                    callServerJson("POST", "/weight", payload, serverAuthToken);
                } catch (Exception error) {
                    serverConnectionStatus = "Hors ligne";
                    prefs.edit().putString("serverConnectionStatus", serverConnectionStatus).apply();
                }
            }
        }).start();
    }

    private void fetchProgressFromServerAsync() {
        if (serverAuthToken.length() == 0) return;
        new Thread(new Runnable() {
            public void run() {
                try {
                    final JSONObject historyResult = callServerJson("GET", "/history", null, serverAuthToken);
                    final JSONObject weightResult = callServerJson("GET", "/weight", null, serverAuthToken);
                    handler.post(new Runnable() {
                        public void run() {
                            applyServerSessionHistory(historyResult.optJSONArray("sessions"));
                            applyServerWeightHistory(weightResult.optJSONArray("entries"), weightResult.optString("targetWeight", weightTarget));
                            prefs.edit()
                                    .putString("sessionHistory", sessionHistory)
                                    .putString("weightHistory", weightHistory)
                                    .putString("weight", weight)
                                    .putString("weightTarget", weightTarget)
                                    .apply();
                        }
                    });
                } catch (Exception error) {
                    serverConnectionStatus = "Hors ligne";
                    prefs.edit().putString("serverConnectionStatus", serverConnectionStatus).apply();
                }
            }
        }).start();
    }

    private void applyServerSessionHistory(JSONArray sessions) {
        if (sessions == null) return;
        for (int i = 0; i < sessions.length(); i++) {
            JSONObject session = sessions.optJSONObject(i);
            if (session == null) continue;
            String date = session.optString("date", "");
            String status = session.optString("status", "");
            if (date.length() == 0 || status.length() == 0) continue;
            sessionHistory = upsertKeyValue(sessionHistory, date, status);
        }
    }

    private void applyServerWeightHistory(JSONArray entries, String serverTargetWeight) {
        if (serverTargetWeight != null && serverTargetWeight.trim().length() > 0) {
            weightTarget = serverTargetWeight.trim();
        }
        if (entries == null) return;
        for (int i = 0; i < entries.length(); i++) {
            JSONObject entry = entries.optJSONObject(i);
            if (entry == null) continue;
            String date = entry.optString("date", "");
            String value = formatServerNumber(entry.optDouble("weightKg", 0));
            if (date.length() == 0 || value.length() == 0) continue;
            weightHistory = upsertKeyValue(weightHistory, date, value);
            weight = value;
        }
    }

    private String upsertKeyValue(String source, String key, String value) {
        String[] entries = source == null || source.length() == 0 ? new String[0] : source.split(";");
        StringBuilder builder = new StringBuilder();
        boolean replaced = false;
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].startsWith(key + "=")) {
                if (!replaced) {
                    if (builder.length() > 0) builder.append(';');
                    builder.append(key).append('=').append(value);
                    replaced = true;
                }
            } else if (entries[i].length() > 0) {
                if (builder.length() > 0) builder.append(';');
                builder.append(entries[i]);
            }
        }
        if (!replaced) {
            if (builder.length() > 0) builder.append(';');
            builder.append(key).append('=').append(value);
        }
        return builder.toString();
    }

    private String formatServerNumber(double value) {
        if (value <= 0) return "";
        if (Math.abs(value - Math.round(value)) < 0.001) return String.valueOf((int) Math.round(value));
        return String.format(Locale.FRANCE, "%.1f", value).replace(",", ".");
    }

    private void syncPasswordToServerAsync(final String previousPassword, final String newPassword) {
        if (serverAuthToken.length() == 0 || newPassword == null || newPassword.length() == 0) return;
        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("currentPassword", previousPassword);
                    payload.put("newPassword", newPassword);
                    callServerJson("PUT", "/me/password", payload, serverAuthToken);
                } catch (Exception error) {
                    serverConnectionStatus = "Hors ligne";
                    prefs.edit().putString("serverConnectionStatus", serverConnectionStatus).apply();
                }
            }
        }).start();
    }

    private void syncShopStateToServerAsync() {
        if (serverAuthToken.length() == 0) return;
        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("coins", valhallaCoins);
                    payload.put("inventory", tokenJsonArray(inventoryItems));
                    payload.put("equipped", tokenJsonArray(equippedItems));
                    payload.put("ownedHeroes", tokenJsonArray(ownedHeroes));
                    payload.put("hero", avatarType);
                    payload.put("backdrop", equippedBackdropId());
                    final JSONObject result = callServerJson("PUT", "/shop/state", payload, serverAuthToken);
                    final JSONObject user = result.optJSONObject("user");
                    if (user != null) {
                        handler.post(new Runnable() {
                            public void run() {
                                applyServerShopState(user);
                                saveAvatarLocal();
                            }
                        });
                    }
                } catch (Exception error) {
                    serverConnectionStatus = "Hors ligne";
                    prefs.edit().putString("serverConnectionStatus", serverConnectionStatus).apply();
                }
            }
        }).start();
    }

    private void syncFriendToServerAsync(final String friendName) {
        if (serverAuthToken.length() == 0 || friendName == null || friendName.length() == 0) return;
        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("username", friendName);
                    final JSONObject result = callServerJson("POST", "/friends", payload, serverAuthToken);
                    handler.post(new Runnable() {
                        public void run() {
                            JSONObject friend = result.optJSONObject("friend");
                            if (friend != null) {
                                upsertFriendMeta(friend.optString("username", friendName), friend.optString("id", ""), result.optString("conversationId", ""));
                                saveBattleMeta();
                            }
                        }
                    });
                } catch (Exception error) {
                    serverConnectionStatus = "Hors ligne";
                    prefs.edit().putString("serverConnectionStatus", serverConnectionStatus).apply();
                }
            }
        }).start();
    }

    private void syncRemoveFriendFromServerAsync(String friendName) {
        if (serverAuthToken.length() == 0) return;
        final String friendId = targetUserIdForRoom(friendRoom(friendName));
        if (friendId.length() == 0) return;
        new Thread(new Runnable() {
            public void run() {
                try {
                    callServerJson("DELETE", "/friends/" + friendId, null, serverAuthToken);
                } catch (Exception error) {
                    serverConnectionStatus = "Hors ligne";
                    prefs.edit().putString("serverConnectionStatus", serverConnectionStatus).apply();
                }
            }
        }).start();
    }

    private void fetchBattleSocialFromServerAsync(final boolean refreshBattle) {
        if (serverAuthToken.length() == 0) return;
        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject friendsResult = callServerJson("GET", "/friends", null, serverAuthToken);
                    JSONObject groupsResult = callServerJson("GET", "/groups", null, serverAuthToken);
                    final String[] parsed = parseBattleSocial(friendsResult.optJSONArray("friends"), groupsResult.optJSONArray("groups"));
                    handler.post(new Runnable() {
                        public void run() {
                            if (parsed[0].length() > 0) battleFriends = mergeCommaNames(battleFriends, parsed[0]);
                            if (parsed[1].length() > 0) battleGroups = mergeGroupEntries(battleGroups, parsed[1]);
                            battleFriendMeta = parsed[2];
                            battleGroupMeta = parsed[3];
                            saveBattleMeta();
                            if (refreshBattle) showBattle();
                        }
                    });
                } catch (Exception error) {
                    serverConnectionStatus = "Hors ligne";
                    prefs.edit().putString("serverConnectionStatus", serverConnectionStatus).apply();
                }
            }
        }).start();
    }

    private String[] parseBattleSocial(JSONArray friends, JSONArray groups) {
        StringBuilder friendNames = new StringBuilder();
        StringBuilder groupEntries = new StringBuilder();
        StringBuilder friendMeta = new StringBuilder();
        StringBuilder groupMeta = new StringBuilder();
        if (friends != null) {
            for (int i = 0; i < friends.length(); i++) {
                JSONObject friend = friends.optJSONObject(i);
                if (friend == null) continue;
                String name = cleanBattlePart(friend.optString("username", ""));
                if (name.length() == 0) continue;
                if (friendNames.length() > 0) friendNames.append(',');
                friendNames.append(name);
                if (friendMeta.length() > 0) friendMeta.append(';');
                friendMeta.append(name).append('~')
                        .append(cleanBattlePart(friend.optString("id", ""))).append('~')
                        .append(cleanBattlePart(friend.optString("conversationId", "")));
            }
        }
        if (groups != null) {
            for (int i = 0; i < groups.length(); i++) {
                JSONObject group = groups.optJSONObject(i);
                if (group == null) continue;
                String name = cleanBattlePart(group.optString("name", ""));
                if (name.length() == 0) continue;
                String members = groupMembersFromJson(group.optJSONArray("members"));
                if (groupEntries.length() > 0) groupEntries.append(';');
                groupEntries.append(name).append('~').append(members);
                if (groupMeta.length() > 0) groupMeta.append(';');
                groupMeta.append(name).append('~')
                        .append(cleanBattlePart(group.optString("id", ""))).append('~')
                        .append(cleanBattlePart(group.optString("conversationId", ""))).append('~')
                        .append(members);
            }
        }
        return new String[] { friendNames.toString(), groupEntries.toString(), friendMeta.toString(), groupMeta.toString() };
    }

    private String groupMembersFromJson(JSONArray members) {
        if (members == null) return "";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < members.length(); i++) {
            JSONObject member = members.optJSONObject(i);
            if (member == null) continue;
            String name = cleanBattlePart(member.optString("username", ""));
            if (name.length() == 0 || name.equalsIgnoreCase(userName)) continue;
            if (builder.length() > 0) builder.append('|');
            builder.append(name);
        }
        return builder.toString();
    }

    private String cleanBattlePart(String value) {
        if (value == null) return "";
        return value.trim().replace(",", " ").replace(";", " ").replace("|", " ").replace("~", " ").replace("\n", " ");
    }

    private String mergeCommaNames(String current, String incoming) {
        String result = current == null ? "" : current;
        if (incoming == null || incoming.length() == 0) return result;
        String[] names = incoming.split(",");
        for (int i = 0; i < names.length; i++) {
            String name = names[i].trim();
            if (name.length() == 0 || containsCommaName(result, name)) continue;
            result = result.length() == 0 ? name : result + "," + name;
        }
        return result;
    }

    private boolean containsCommaName(String source, String name) {
        if (source == null || source.length() == 0) return false;
        String[] names = source.split(",");
        for (int i = 0; i < names.length; i++) {
            if (names[i].trim().equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    private String mergeGroupEntries(String current, String incoming) {
        String result = current == null ? "" : current;
        if (incoming == null || incoming.length() == 0) return result;
        String[] groups = incoming.split(";");
        for (int i = 0; i < groups.length; i++) {
            String name = groupNameFromEntry(groups[i]);
            if (name.length() == 0 || groupNameExists(result, name)) continue;
            result = result.length() == 0 ? groups[i] : result + ";" + groups[i];
        }
        return result;
    }

    private boolean groupNameExists(String source, String name) {
        if (source == null || source.length() == 0) return false;
        String[] groups = source.split(";");
        for (int i = 0; i < groups.length; i++) {
            if (groupNameFromEntry(groups[i]).equalsIgnoreCase(name)) return true;
        }
        return false;
    }

    private void upsertFriendMeta(String name, String userId, String conversationId) {
        String cleanName = cleanBattlePart(name);
        if (cleanName.length() == 0) return;
        String entry = cleanName + "~" + cleanBattlePart(userId) + "~" + cleanBattlePart(conversationId);
        battleFriendMeta = upsertMetaEntry(battleFriendMeta, cleanName, entry);
        addFriendName(cleanName);
    }

    private void upsertGroupMeta(String name, String groupId, String conversationId, String members) {
        String cleanName = cleanBattlePart(name);
        if (cleanName.length() == 0) return;
        String cleanMembers = members == null ? "" : members.replace(";", " ").replace("~", " ");
        String entry = cleanName + "~" + cleanBattlePart(groupId) + "~" + cleanBattlePart(conversationId) + "~" + cleanMembers;
        battleGroupMeta = upsertMetaEntry(battleGroupMeta, cleanName, entry);
    }

    private String upsertMetaEntry(String meta, String key, String entry) {
        String[] entries = meta == null || meta.length() == 0 ? new String[0] : meta.split(";");
        StringBuilder builder = new StringBuilder();
        boolean replaced = false;
        for (int i = 0; i < entries.length; i++) {
            String existing = metaPart(entries[i], 0);
            if (existing.length() == 0) continue;
            if (existing.equalsIgnoreCase(key)) {
                if (!replaced) {
                    if (builder.length() > 0) builder.append(';');
                    builder.append(entry);
                    replaced = true;
                }
            } else {
                if (builder.length() > 0) builder.append(';');
                builder.append(entries[i]);
            }
        }
        if (!replaced) {
            if (builder.length() > 0) builder.append(';');
            builder.append(entry);
        }
        return builder.toString();
    }

    private String metaEntry(String meta, String key) {
        if (meta == null || meta.length() == 0 || key == null) return "";
        String[] entries = meta.split(";");
        for (int i = 0; i < entries.length; i++) {
            if (metaPart(entries[i], 0).equalsIgnoreCase(key)) return entries[i];
        }
        return "";
    }

    private String metaPart(String entry, int index) {
        if (entry == null) return "";
        String[] parts = entry.split("~", -1);
        if (index < 0 || index >= parts.length) return "";
        return parts[index].trim();
    }

    private String conversationIdForRoom(String room) {
        if (room == null) return "";
        if (room.startsWith("friend:")) return metaPart(metaEntry(battleFriendMeta, room.substring(7)), 2);
        if (room.startsWith("group:")) return metaPart(metaEntry(battleGroupMeta, room.substring(6)), 2);
        return "";
    }

    private String targetUserIdForRoom(String room) {
        if (room == null || !room.startsWith("friend:")) return "";
        return metaPart(metaEntry(battleFriendMeta, room.substring(7)), 1);
    }

    private String groupIdForRoom(String room) {
        if (room == null || !room.startsWith("group:")) return "";
        return metaPart(metaEntry(battleGroupMeta, room.substring(6)), 1);
    }

    private void saveBattleMeta() {
        prefs.edit()
                .putString("battleFriends", battleFriends)
                .putString("battleGroups", battleGroups)
                .putString("battleFriendMeta", battleFriendMeta)
                .putString("battleGroupMeta", battleGroupMeta)
                .apply();
    }

    private void fetchRoomMessagesFromServerAsync(final String room, final boolean refreshRoom) {
        final String conversationId = conversationIdForRoom(room);
        if (serverAuthToken.length() == 0 || conversationId.length() == 0) return;
        new Thread(new Runnable() {
            public void run() {
                try {
                    final JSONObject result = callServerJson("GET", "/conversations/" + conversationId + "/messages", null, serverAuthToken);
                    handler.post(new Runnable() {
                        public void run() {
                            mergeServerMessages(room, result.optJSONArray("messages"));
                            if (refreshRoom && room.equals(activeBattleRoom)) showBattleChat(room);
                        }
                    });
                } catch (Exception error) {
                    serverConnectionStatus = "Hors ligne";
                    prefs.edit().putString("serverConnectionStatus", serverConnectionStatus).apply();
                }
            }
        }).start();
    }

    private void mergeServerMessages(String room, JSONArray messages) {
        if (messages == null) return;
        for (int i = 0; i < messages.length(); i++) {
            JSONObject message = messages.optJSONObject(i);
            if (message == null) continue;
            String textValue = cleanBattlePart(message.optString("text", ""));
            if (textValue.length() == 0) continue;
            String senderId = message.optString("senderId", "");
            String senderName = message.optString("senderUsername", "Sportif");
            String display = senderId.equals(serverUserId) ? "Toi : " + textValue : senderName + " : " + textValue;
            appendBattleMessageUnique(room, display);
        }
    }

    private void syncBattleMessageToServerAsync(String room, String message) {
        if (serverAuthToken.length() == 0) return;
        final String conversationId = conversationIdForRoom(room);
        if (conversationId.length() == 0) return;
        final String cleanMessage = message == null ? "" : message.trim();
        if (cleanMessage.length() == 0) return;
        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("text", cleanMessage);
                    callServerJson("POST", "/conversations/" + conversationId + "/messages", payload, serverAuthToken);
                } catch (Exception error) {
                    serverConnectionStatus = "Hors ligne";
                    prefs.edit().putString("serverConnectionStatus", serverConnectionStatus).apply();
                }
            }
        }).start();
    }

    private void syncGroupToServerAsync(final String name, final String members) {
        if (serverAuthToken.length() == 0 || name == null || name.length() == 0) return;
        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("name", name);
                    payload.put("memberUsernames", tokenJsonArray(members));
                    final JSONObject result = callServerJson("POST", "/groups", payload, serverAuthToken);
                    handler.post(new Runnable() {
                        public void run() {
                            JSONObject group = result.optJSONObject("group");
                            if (group != null) {
                                String serverMembers = groupMembersFromJson(group.optJSONArray("members"));
                                upsertGroupMeta(group.optString("name", name), group.optString("id", ""), group.optString("conversationId", ""), serverMembers);
                                saveGroup(group.optString("name", name), serverMembers);
                                saveBattleMeta();
                            }
                        }
                    });
                } catch (Exception error) {
                    serverConnectionStatus = "Hors ligne";
                    prefs.edit().putString("serverConnectionStatus", serverConnectionStatus).apply();
                }
            }
        }).start();
    }

    private void syncPrivateChallengeToServerAsync(final String room, final String exercise, final String reps, final String time, final Uri video) {
        if (serverAuthToken.length() == 0) return;
        final String targetUserId = targetUserIdForRoom(room);
        final String groupId = groupIdForRoom(room);
        if (targetUserId.length() == 0 && groupId.length() == 0) return;
        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("exercise", exercise);
                    payload.put("difficulty", "Normal");
                    if (targetUserId.length() > 0) payload.put("targetUserId", targetUserId);
                    if (groupId.length() > 0) payload.put("groupId", groupId);
                    int repsValue = parsePositiveInt(reps);
                    int timeValue = parsePositiveInt(time);
                    if (repsValue > 0) payload.put("reps", repsValue);
                    if (timeValue > 0) payload.put("timeSeconds", timeValue);
                    JSONObject result = callServerJson("POST", "/battle/challenges", payload, serverAuthToken);
                    JSONObject challenge = result.optJSONObject("challenge");
                    if (challenge != null && video != null) {
                        JSONObject proofPayload = new JSONObject();
                        proofPayload.put("fileName", fileNameFromUri(video));
                        callServerJson("POST", "/battle/challenges/" + challenge.optString("id", "") + "/proof", proofPayload, serverAuthToken);
                    }
                } catch (Exception error) {
                    serverConnectionStatus = "Hors ligne";
                    prefs.edit().putString("serverConnectionStatus", serverConnectionStatus).apply();
                }
            }
        }).start();
    }

    private int parsePositiveInt(String value) {
        try {
            int parsed = Integer.parseInt(value == null ? "" : value.trim());
            return Math.max(parsed, 0);
        } catch (Exception ignored) {
            return 0;
        }
    }

    private String fileNameFromUri(Uri uri) {
        if (uri == null) return "preuve-video";
        String value = uri.getLastPathSegment();
        if (value == null || value.length() == 0) return "preuve-video";
        return value.replace("/", "_").replace("\\", "_");
    }

    private ArrayList<DayPlan> buildPlan() {
        ArrayList<DayPlan> customPlan = customPlanFromCache();
        if (customPlan != null) return customPlan;

        ArrayList<DayPlan> serverPlan = serverPlanFromCache();
        if (serverPlan != null) return serverPlan;

        int activeAdjustment = activePlanAdjustment();
        int series = seriesCount();
        String reps = repsText();
        String rest = restTime();
        int count = exerciseCount();
        boolean easier = activeAdjustment < 0;
        boolean harder = activeAdjustment > 0;
        boolean gym = place.indexOf("salle") >= 0 || equipment.indexOf("Salle") >= 0;
        boolean dumbbells = equipment.indexOf("Halt") >= 0 || equipment.indexOf("halt") >= 0 || equipment.indexOf("Banc") >= 0;

        ArrayList<DayPlan> plan = new ArrayList<DayPlan>();

        DayPlan monday = new DayPlan("Lundi", "Haut du corps");
        monday.add(exercise(pushPlanExercise(easier, harder, gym), series, reps, rest));
        monday.add(exercise(pullPlanExercise(easier, harder, gym, dumbbells), series, reps, rest));
        monday.add(exercise(shoulderPlanExercise(easier, harder, gym, dumbbells), series, reps, rest));
        if (count > 3) monday.add(exercise(corePlanExercise("Gainage face"), 3, "30 à 45 s", "45 s"));
        if (count > 4) monday.add(exercise("Curl biceps + extension triceps", 3, "10 à 12 reps", "45 s"));
        plan.add(monday);

        DayPlan tuesday = new DayPlan("Mardi", "Cardio + abdos");
        tuesday.add(exercise(cardioPlanExercise(), 1, cardioTime(), "Rythme facile"));
        tuesday.add(exercise("Mountain climbers", 3, "30 s", "45 s"));
        tuesday.add(exercise("Crunch contrôlé", 3, "12 à 15 reps", "45 s"));
        if (count > 3) tuesday.add(exercise("Planche latérale", 3, "25 s par côté", "45 s"));
        plan.add(tuesday);

        DayPlan wednesday = new DayPlan("Mercredi", "Jambes");
        wednesday.add(exercise(legPlanExercise(easier, harder, gym, dumbbells), series, reps, rest));
        wednesday.add(exercise(hingePlanExercise(easier, harder, gym), series, reps, rest));
        wednesday.add(exercise("Pont fessier", series, "12 à 15 reps", rest));
        if (count > 3) wednesday.add(exercise("Mollets debout", 3, "15 reps", "45 s"));
        if (count > 4) wednesday.add(exercise("Chaise au mur", 3, "35 s", "60 s"));
        plan.add(wednesday);

        DayPlan thursday = new DayPlan("Jeudi", "Repos actif");
        thursday.add(exercise("Mobilité épaules et hanches", 2, "8 minutes", "Respiration calme"));
        thursday.add(exercise("Marche", 1, "20 à 30 minutes", "Facile"));
        plan.add(thursday);

        DayPlan friday = new DayPlan("Vendredi", "Full body");
        friday.add(exercise(legPlanExercise(easier, harder, gym, dumbbells), series, reps, rest));
        friday.add(exercise(pullPlanExercise(easier, harder, gym, dumbbells), series, reps, rest));
        friday.add(exercise(pushPlanExercise(easier, harder, gym), series, reps, rest));
        if (count > 3) friday.add(exercise(corePlanExercise("Gainage dynamique"), 3, "30 s", "45 s"));
        if (count > 4) friday.add(exercise("Farmer walk", 3, "40 pas", "60 s"));
        plan.add(friday);

        DayPlan saturday = new DayPlan("Samedi", "Option énergie");
        saturday.add(exercise("Course légère ou vélo", 1, cardioTime(), "Tu dois pouvoir parler"));
        saturday.add(exercise("Étirements bas du corps", 2, "6 minutes", "Lent"));
        plan.add(saturday);

        DayPlan sunday = new DayPlan("Dimanche", "Récupération");
        sunday.add(exercise("Repos complet", 1, "Priorité sommeil", "Aucun chrono"));
        sunday.add(exercise("Préparation de la semaine", 1, "10 minutes", "Planifier repas + séances"));
        plan.add(sunday);

        return plan;
    }

    private int seriesCount() {
        int base = 3;
        if (level.indexOf("Avancé") >= 0) base = 5;
        if (level.indexOf("Inter") >= 0) base = 4;
        base = base + activePlanAdjustment();
        if (base < 2) return 2;
        if (base > 6) return 6;
        return base;
    }

    private int exerciseCount() {
        int base = 5;
        if (sessionTime.indexOf("20") >= 0) base = 3;
        if (sessionTime.indexOf("35") >= 0) base = 4;
        if (activePlanAdjustment() < 0) base--;
        if (activePlanAdjustment() > 0) base++;
        if (base < 3) return 3;
        if (base > 6) return 6;
        return base;
    }

    private String repsText() {
        return targetRepsByGoal("exercice général") + " reps";
    }

    private String restTime() {
        if (activePlanAdjustment() < 0) return "120 s";
        if (activePlanAdjustment() > 0) return "60 s";
        if (level.indexOf("Avancé") >= 0) return "90 s";
        if (level.indexOf("Inter") >= 0) return "75 s";
        return "75 s";
    }

    private String cardioTime() {
        if (activePlanAdjustment() < 0) return "10 à 15 minutes";
        if (activePlanAdjustment() > 0) return "25 à 35 minutes";
        if (sessionTime.indexOf("20") >= 0) return "12 minutes";
        if (sessionTime.indexOf("35") >= 0) return "20 minutes";
        return "30 minutes";
    }

    private int activePlanAdjustment() {
        if (previewPlanActive) return previewPlanAdjustment;
        if (weekKey().equals(officialPlanWeek)) return officialPlanAdjustment;
        return planAdjustment;
    }

    private boolean isSunday() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    private ArrayList<DayPlan> serverPlanFromCache() {
        if (previewPlanActive) return null;
        if (serverPlanJson == null || serverPlanJson.length() == 0) return null;
        if (!weekKey().equals(serverPlanWeek)) return null;
        try {
            JSONObject program = new JSONObject(serverPlanJson);
            JSONArray days = program.optJSONArray("days");
            if (days == null || days.length() == 0) return null;
            ArrayList<DayPlan> plan = new ArrayList<DayPlan>();
            for (int i = 0; i < days.length(); i++) {
                JSONObject dayJson = days.optJSONObject(i);
                if (dayJson == null) continue;
                DayPlan day = new DayPlan(dayJson.optString("day", "Jour"), dayJson.optString("focus", "Séance"));
                JSONArray exercises = dayJson.optJSONArray("exercises");
                if (exercises != null) {
                    for (int j = 0; j < exercises.length(); j++) {
                        String exercise = exercises.optString(j, "");
                        if (exercise.length() > 0) day.add(exercise);
                    }
                }
                plan.add(day);
            }
            if (plan.size() == 0) return null;
            return plan;
        } catch (Exception ignored) {
            return null;
        }
    }

    private void cacheServerProgram(JSONObject program) {
        if (program == null) return;
        serverPlanJson = program.toString();
        serverPlanWeek = program.optString("week", weekKey());
        prefs.edit().putString("serverPlanJson", serverPlanJson).putString("serverPlanWeek", serverPlanWeek).apply();
    }

    private JSONObject programJsonFromPlan(ArrayList<DayPlan> plan, String source) {
        JSONObject program = new JSONObject();
        try {
            program.put("week", weekKey());
            program.put("source", source);
            JSONArray days = new JSONArray();
            for (int i = 0; i < plan.size(); i++) {
                DayPlan day = plan.get(i);
                JSONObject dayJson = new JSONObject();
                dayJson.put("day", day.day);
                dayJson.put("focus", day.focus);
                JSONArray exercises = new JSONArray();
                for (int j = 0; j < day.exercises.size(); j++) {
                    exercises.put(day.exercises.get(j));
                }
                dayJson.put("exercises", exercises);
                days.put(dayJson);
            }
            program.put("days", days);
        } catch (Exception ignored) {
        }
        return program;
    }

    private String exercise(String name, int series, String reps, String rest) {
        int tunedSeries = performanceSeries(name, series);
        String tunedReps = performanceTrainingReps(name, gymTrainingReps(name, reps));
        String tunedWeight = trainingWeightText(name, tunedReps);
        return name + "\n" + tunedSeries + " séries x " + tunedReps + "  •  poids " + tunedWeight + "  •  repos " + rest;
    }

    private String fullPlanText() {
        StringBuilder builder = new StringBuilder();
        builder.append("Voici ton tableau personnalisé. ").append(adjustmentLabel()).append("\n\n");
        ArrayList<DayPlan> plan = buildPlan();
        for (int i = 0; i < plan.size(); i++) {
            DayPlan day = plan.get(i);
            builder.append(day.day).append(" - ").append(day.focus).append("\n");
            for (int j = 0; j < day.exercises.size(); j++) {
                builder.append("- ").append(day.exercises.get(j).replace("\n", " : ")).append("\n");
            }
            builder.append("\n");
        }
        builder.append("Conseil : garde 1 à 2 répétitions en réserve sur chaque série pour progresser sans te griller.");
        return builder.toString();
    }

    private String printablePlanText() {
        StringBuilder builder = new StringBuilder();
        builder.append("VALHALLA RAGE - TABLEAU D'ENTRAÎNEMENT\n");
        builder.append("Utilisateur : ").append(userName).append("\n");
        builder.append("Objectif : ").append(goal).append("\n");
        builder.append("Niveau : ").append(level).append("\n");
        builder.append("Rythme : ").append(frequency).append("\n\n");
        builder.append(fullPlanText());
        builder.append("\n\nÀ cocher après la séance : réalisé / partiel / repos.");
        return builder.toString();
    }

    private String coachReply(String message) {
        String lower = message.toLowerCase(Locale.FRANCE);
        if ((lower.indexOf("explique") >= 0 || lower.indexOf("conseil") >= 0 || lower.indexOf("technique") >= 0)
                && (lower.indexOf("tableau") >= 0 || lower.indexOf("programme") >= 0 || lower.indexOf("tous") >= 0 || lower.indexOf("chaque") >= 0)) {
            return planTechniqueAdvice();
        }
        String advice = techniqueAdvice(lower);
        if (advice != null) {
            return advice;
        }
        if (isTooHardMessage(lower)) {
            adjustPlan(-1);
            return "J'ai allégé ton tableau. Les exercices sont plus accessibles, les séries sont réduites si besoin et les temps de repos sont plus longs. Appuie sur \"Voir le tableau mis à jour\" pour voir le nouveau plan.";
        }
        if (isTooEasyMessage(lower)) {
            adjustPlan(1);
            return "J'ai rendu ton tableau plus stimulant. Le volume monte progressivement, certains exercices deviennent plus contrôlés et les repos sont un peu plus courts. Appuie sur \"Voir le tableau mis à jour\" pour voir le nouveau plan.";
        }
        if (lower.indexOf("aliment") >= 0 || lower.indexOf("repas") >= 0 || lower.indexOf("manger") >= 0) {
            return "Côté alimentation : à chaque repas, vise protéines, légumes, un féculent dosé et de l'eau. Vu ta réponse \"" + foodChange + "\", change d'abord un seul repas par jour.";
        }
        if (lower.indexOf("court") >= 0 || lower.indexOf("temps") >= 0 || lower.indexOf("rapide") >= 0) {
            return "Version courte : 3 exercices par séance. 1 jambes, 1 haut du corps, 1 gainage. Fais " + seriesCount() + " séries, " + repsText() + ", repos " + restTime() + ".";
        }
        if (lower.indexOf("niveau") >= 0 || lower.indexOf("dur") >= 0 || lower.indexOf("facile") >= 0) {
            return "Ton niveau actuel est \"" + level + "\". Si c'est trop dur, baisse les répétitions. Si c'est trop facile deux semaines de suite, ajoute une série.";
        }
        return fullPlanText();
    }

    private void requestAiCoach(final String message, final TextView pending) {
        new Thread(new Runnable() {
            public void run() {
                String reply;
                String action = "none";
                boolean localFallback = false;
                try {
                    JSONObject result = callAiServer(message);
                    cacheServerProgram(result.optJSONObject("program"));
                    reply = result.optString("reply", "").trim();
                    action = result.optString("action", "none").trim().toLowerCase(Locale.FRANCE);
                    if (reply.length() == 0) {
                        throw new IllegalStateException("Réponse IA vide");
                    }
                } catch (Exception error) {
                    localFallback = true;
                    if (isPlanRequest(message)) {
                        reply = "Je prépare une proposition de tableau à vérifier ensemble.";
                    } else {
                        reply = coachReply(message);
                    }
                    if (isDifficultyMessage(message.toLowerCase(Locale.FRANCE)) && !isPlanRequest(message)) {
                        action = "local_changed";
                    }
                }

                final String finalReply = reply;
                final String finalAction = action;
                final boolean finalLocalFallback = localFallback;
                handler.post(new Runnable() {
                    public void run() {
                        pending.setText("Coach VALHALLA RAGE\n" + finalReply);
                        boolean planRequest = isPlanRequest(message)
                                || "show_plan".equals(finalAction)
                                || "easier".equals(finalAction)
                                || "harder".equals(finalAction);
                        if (!finalLocalFallback && !planRequest) {
                            applyAiAction(finalAction);
                        }
                        if (planRequest) {
                            showCoachPlanProposal(message, finalAction);
                        } else if ("local_changed".equals(finalAction)) {
                            addPlanShortcut();
                        }
                    }
                });
            }
        }).start();
    }

    private JSONObject callAiServer(String message) throws Exception {
        JSONObject payload = new JSONObject();
        payload.put("message", message);
        payload.put("profile", profileJson());
        payload.put("currentPlan", planJson());
        payload.put("planAdjustment", planAdjustment);
        payload.put("appVersion", "0.47.0");

        return callServerJson("POST", "/coach", payload, serverAuthToken);
    }

    private JSONObject callServerJson(String method, String path, JSONObject payload, String token) throws Exception {
        String base = aiServerUrl == null ? "" : aiServerUrl.trim();
        while (base.endsWith("/")) base = base.substring(0, base.length() - 1);
        URL url = new URL(base + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(7000);
        connection.setReadTimeout(30000);
        connection.setRequestMethod(method);
        connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        connection.setRequestProperty("Accept", "application/json");
        if (token != null && token.length() > 0) {
            connection.setRequestProperty("Authorization", "Bearer " + token);
        }
        connection.setDoOutput(payload != null);
        if (payload != null) {
            byte[] bytes = payload.toString().getBytes("UTF-8");
            OutputStream output = connection.getOutputStream();
            output.write(bytes);
            output.close();
        }

        int code = connection.getResponseCode();
        InputStream stream = code >= 200 && code < 300 ? connection.getInputStream() : connection.getErrorStream();
        String response = readAll(stream);
        connection.disconnect();
        if (code < 200 || code >= 300) {
            String message = response;
            try {
                JSONObject error = new JSONObject(response);
                message = error.optString("error", response);
            } catch (Exception ignored) {
            }
            throw new IllegalStateException(message);
        }
        serverConnectionStatus = "Connecté";
        prefs.edit().putString("serverConnectionStatus", serverConnectionStatus).apply();
        return response.length() == 0 ? new JSONObject() : new JSONObject(response);
    }

    private JSONObject profileJson() throws Exception {
        JSONObject profile = new JSONObject();
        profile.put("userName", userName);
        profile.put("goal", goal);
        profile.put("level", level);
        profile.put("frequency", frequency);
        profile.put("place", place);
        profile.put("equipment", equipment);
        profile.put("sessionTime", sessionTime);
        profile.put("eating", eating);
        profile.put("foodChange", foodChange);
        profile.put("height", height);
        profile.put("weight", weight);
        profile.put("weightTarget", weightTarget);
        profile.put("gymStrengthProfile", gymStrengthProfile);
        profile.put("exercisePerformanceProfile", exercisePerformanceProfile);
        profile.put("gender", gender);
        profile.put("adjustment", adjustmentLabel());
        return profile;
    }

    private JSONArray planJson() throws Exception {
        JSONArray days = new JSONArray();
        ArrayList<DayPlan> plan = buildPlan();
        for (int i = 0; i < plan.size(); i++) {
            DayPlan day = plan.get(i);
            JSONObject dayJson = new JSONObject();
            dayJson.put("day", day.day);
            dayJson.put("focus", day.focus);
            JSONArray exercises = new JSONArray();
            for (int j = 0; j < day.exercises.size(); j++) {
                exercises.put(day.exercises.get(j));
            }
            dayJson.put("exercises", exercises);
            days.put(dayJson);
        }
        return days;
    }

    private void syncProfileToServerAsync() {
        if (serverAuthToken.length() == 0) return;
        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("profile", profileJson());
                    callServerJson("PUT", "/me/profile", payload, serverAuthToken);
                } catch (Exception error) {
                    serverConnectionStatus = "Hors ligne";
                    prefs.edit().putString("serverConnectionStatus", serverConnectionStatus).apply();
                }
            }
        }).start();
    }

    private void requestServerProgramAsync() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("profile", profileJson());
                    payload.put("save", serverAuthToken.length() > 0);
                    JSONObject result = callServerJson("POST", "/program/generate", payload, serverAuthToken);
                    cacheServerProgram(result.optJSONObject("program"));
                } catch (Exception error) {
                    serverConnectionStatus = "Hors ligne";
                    prefs.edit().putString("serverConnectionStatus", serverConnectionStatus).apply();
                }
            }
        }).start();
    }

    private void fetchCurrentProgramFromServerAsync() {
        if (serverAuthToken.length() == 0) return;
        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject result = callServerJson("GET", "/program/current", null, serverAuthToken);
                    cacheServerProgram(result.optJSONObject("program"));
                } catch (Exception ignored) {
                }
            }
        }).start();
    }

    private void syncProgramToServerAsync(final JSONObject program) {
        if (serverAuthToken.length() == 0 || program == null) return;
        new Thread(new Runnable() {
            public void run() {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("program", program);
                    JSONObject result = callServerJson("POST", "/program/accept", payload, serverAuthToken);
                    cacheServerProgram(result.optJSONObject("program"));
                } catch (Exception error) {
                    serverConnectionStatus = "Hors ligne";
                    prefs.edit().putString("serverConnectionStatus", serverConnectionStatus).apply();
                }
            }
        }).start();
    }

    private void testServerConnection() {
        Toast.makeText(this, "Test du serveur...", Toast.LENGTH_SHORT).show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    final JSONObject health = callServerJson("GET", "/health", null, "");
                    handler.post(new Runnable() {
                        public void run() {
                            serverConnectionStatus = "Connecté";
                            serverAiKeyConfigured = health.optBoolean("keyConfigured", false);
                            serverAiModel = health.optString("model", serverAiModel);
                            prefs.edit()
                                    .putString("serverConnectionStatus", serverConnectionStatus)
                                    .putBoolean("serverAiKeyConfigured", serverAiKeyConfigured)
                                    .putString("serverAiModel", serverAiModel)
                                    .apply();
                            String mode = serverAiKeyConfigured ? "IA réelle active." : "Serveur connecté, IA réelle non activée.";
                            Toast.makeText(MainActivity.this, mode, Toast.LENGTH_LONG).show();
                            showSettings();
                        }
                    });
                } catch (final Exception error) {
                    handler.post(new Runnable() {
                        public void run() {
                            serverConnectionStatus = "Hors ligne";
                            serverAiKeyConfigured = false;
                            prefs.edit()
                                    .putString("serverConnectionStatus", serverConnectionStatus)
                                    .putBoolean("serverAiKeyConfigured", serverAiKeyConfigured)
                                    .apply();
                            Toast.makeText(MainActivity.this, "Serveur non joignable : " + error.getMessage(), Toast.LENGTH_LONG).show();
                            showSettings();
                        }
                    });
                }
            }
        }).start();
    }

    private String readAll(InputStream stream) throws Exception {
        if (stream == null) return "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        reader.close();
        return builder.toString();
    }

    private void applyAiAction(String action) {
        if ("easier".equals(action)) {
            adjustPlan(-1);
            saveProfile(true);
        } else if ("harder".equals(action)) {
            adjustPlan(1);
            saveProfile(true);
        } else if ("keep".equals(action)) {
            saveProfile(true);
        }
    }

    private boolean isDifficultyMessage(String lower) {
        return isTooHardMessage(lower) || isTooEasyMessage(lower);
    }

    private boolean isTooHardMessage(String lower) {
        return lower.indexOf("trop dur") >= 0
                || lower.indexOf("trop dure") >= 0
                || lower.indexOf("trop difficile") >= 0
                || lower.indexOf("impossible") >= 0
                || lower.indexOf("j'y arrive pas") >= 0
                || lower.indexOf("je n'y arrive pas") >= 0
                || lower.indexOf("allège") >= 0
                || lower.indexOf("alleger") >= 0
                || lower.indexOf("plus facile") >= 0;
    }

    private boolean isTooEasyMessage(String lower) {
        return lower.indexOf("trop facile") >= 0
                || lower.indexOf("pas assez dur") >= 0
                || lower.indexOf("plus dur") >= 0
                || lower.indexOf("plus dure") >= 0
                || lower.indexOf("augmente") >= 0
                || lower.indexOf("intensifie") >= 0
                || lower.indexOf("plus difficile") >= 0;
    }

    private void adjustPlan(int direction) {
        planAdjustment = planAdjustment + direction;
        if (planAdjustment < -2) planAdjustment = -2;
        if (planAdjustment > 2) planAdjustment = 2;
        serverPlanJson = "";
        serverPlanWeek = "";
        prefs.edit().putInt("planAdjustment", planAdjustment).putString("serverPlanJson", serverPlanJson).putString("serverPlanWeek", serverPlanWeek).apply();
    }

    private String adjustmentLabel() {
        int adjustment = activePlanAdjustment();
        if (adjustment <= -2) return "Plan très accessible";
        if (adjustment == -1) return "Plan allégé";
        if (adjustment == 1) return "Plan plus stimulant";
        if (adjustment >= 2) return "Plan intensif";
        return "Plan équilibré";
    }

    private String planTechniqueAdvice() {
        StringBuilder builder = new StringBuilder();
        builder.append("Voici les conseils pour les exercices de ton tableau actuel :\n\n");
        ArrayList<String> added = new ArrayList<String>();
        ArrayList<DayPlan> plan = buildPlan();
        for (int i = 0; i < plan.size(); i++) {
            DayPlan day = plan.get(i);
            builder.append(day.day).append(" - ").append(day.focus).append("\n");
            for (int j = 0; j < day.exercises.size(); j++) {
                String name = exerciseName(day.exercises.get(j));
                String key = name.toLowerCase(Locale.FRANCE);
                if (added.contains(key)) continue;
                added.add(key);
                builder.append("- ").append(name).append(" : ").append(techniqueTipForExercise(name)).append("\n");
            }
            builder.append("\n");
        }
        builder.append("Règle générale : si la posture se dégrade, baisse la charge, ralentis ou arrête la série.");
        return builder.toString();
    }

    private String exerciseAdviceFromMessage(String lower) {
        ArrayList<DayPlan> plan = buildPlan();
        for (int i = 0; i < plan.size(); i++) {
            DayPlan day = plan.get(i);
            for (int j = 0; j < day.exercises.size(); j++) {
                String name = exerciseName(day.exercises.get(j));
                if (messageMatchesExercise(lower, name)) {
                    return fullExerciseAdvice(name);
                }
            }
        }
        if (lower.indexOf("pike") >= 0) return fullExerciseAdvice("Pike push-up");
        if (lower.indexOf("triceps") >= 0 || lower.indexOf("extension") >= 0) return fullExerciseAdvice("Extension triceps");
        if (lower.indexOf("superman") >= 0) return fullExerciseAdvice("Superman au sol");
        if (lower.indexOf("mobilité") >= 0 || lower.indexOf("mobilite") >= 0) return fullExerciseAdvice("Mobilité épaules et hanches");
        if (lower.indexOf("étirement") >= 0 || lower.indexOf("etirement") >= 0) return fullExerciseAdvice("Étirements bas du corps");
        if (lower.indexOf("repos") >= 0) return fullExerciseAdvice("Repos complet");
        return null;
    }

    private boolean messageMatchesExercise(String lower, String exercise) {
        String clean = exercise.toLowerCase(Locale.FRANCE)
                .replace("+", " ")
                .replace("-", " ")
                .replace("é", "e")
                .replace("è", "e")
                .replace("ê", "e")
                .replace("à", "a")
                .replace("ù", "u")
                .replace("ç", "c");
        String message = lower.replace("é", "e").replace("è", "e").replace("ê", "e").replace("à", "a").replace("ù", "u").replace("ç", "c");
        String[] words = clean.split(" ");
        int hits = 0;
        for (int i = 0; i < words.length; i++) {
            String word = words[i].trim();
            if (word.length() < 4) continue;
            if (message.indexOf(word) >= 0) hits++;
        }
        return hits >= 1 && (clean.length() < 12 || hits >= 2 || message.indexOf(clean) >= 0);
    }

    private String techniqueTipForExercise(String exercise) {
        String lower = exercise.toLowerCase(Locale.FRANCE);
        if (lower.indexOf("curl") >= 0 || lower.indexOf("biceps") >= 0) {
            return "dos droit, épaules basses, coudes près du corps. Monte sans balancer, puis descends lentement.";
        }
        if (lower.indexOf("triceps") >= 0 || lower.indexOf("extension") >= 0) {
            return "garde les coudes fixes, épaules basses et ventre gainé. Tends les bras sans verrouiller brutalement, puis reviens lentement.";
        }
        if (lower.indexOf("presse") >= 0) {
            return "dos collé au dossier, pieds largeur d'épaules. Pousse sans verrouiller les genoux et contrôle la descente.";
        }
        if (lower.indexOf("squat") >= 0) {
            return "pieds stables, ventre gainé, dos neutre. Les genoux suivent les pieds et tu remontes en poussant dans le sol.";
        }
        if (lower.indexOf("pompe") >= 0 || lower.indexOf("push") >= 0) {
            return "corps aligné, ventre serré, mains solides. Descends contrôlé et passe en version inclinée si la posture se casse.";
        }
        if (lower.indexOf("pike") >= 0) {
            return "hanches hautes, tête entre les bras, descente lente vers le sol. Pousse fort sans hausser les épaules vers les oreilles.";
        }
        if (lower.indexOf("rowing") >= 0 || lower.indexOf("tirage") >= 0 || lower.indexOf("row") >= 0) {
            return "dos neutre et buste stable. Tire les coudes vers les hanches, serre les omoplates, puis redescends sans élan.";
        }
        if (lower.indexOf("développé") >= 0 || lower.indexOf("developpe") >= 0) {
            return "poignets droits, omoplates stables et trajectoire régulière. Garde le contrôle, surtout en descente.";
        }
        if (lower.indexOf("soulevé") >= 0 || lower.indexOf("souleve") >= 0 || lower.indexOf("roumain") >= 0) {
            return "charnière de hanches, dos neutre, genoux légèrement fléchis. Les charges restent proches des jambes.";
        }
        if (lower.indexOf("fente") >= 0) {
            return "pas assez long pour être stable, buste droit, genou avant aligné avec le pied. Pousse dans le talon avant pour remonter.";
        }
        if (lower.indexOf("gainage") >= 0 || lower.indexOf("planche") >= 0) {
            return "épaules, bassin et chevilles alignés. Serre fessiers et ventre, respire, et arrête avant que le bas du dos s'affaisse.";
        }
        if (lower.indexOf("crunch") >= 0 || lower.indexOf("abdos") >= 0) {
            return "monte avec les abdos, pas avec la nuque. Expire en montant et contrôle la descente.";
        }
        if (lower.indexOf("mountain") >= 0 || lower.indexOf("climber") >= 0) {
            return "position de pompe solide, épaules au-dessus des mains. Ramène les genoux sans faire rebondir les hanches.";
        }
        if (lower.indexOf("pont") >= 0 || lower.indexOf("fessier") >= 0) {
            return "pieds proches des fesses, pousse dans les talons, serre les fessiers en haut sans cambrer le bas du dos.";
        }
        if (lower.indexOf("mollet") >= 0) {
            return "monte haut sur la pointe des pieds, marque une petite pause et redescends lentement sans rebondir.";
        }
        if (lower.indexOf("chaise") >= 0) {
            return "dos contre le mur, pieds sous les genoux, ventre gainé. Remonte avant que les genoux ou le dos gênent.";
        }
        if (lower.indexOf("farmer") >= 0) {
            return "grandis-toi, épaules basses, ventre gainé. Marche lentement sans te pencher d'un côté.";
        }
        if (lower.indexOf("superman") >= 0) {
            return "regarde le sol, serre légèrement les fessiers et lève bras/jambes sans casser la nuque. Mouvement petit mais contrôlé.";
        }
        if (lower.indexOf("marche") >= 0 || lower.indexOf("course") >= 0 || lower.indexOf("vélo") >= 0 || lower.indexOf("velo") >= 0) {
            return "reste à une intensité où tu peux encore parler. Augmente d'abord la durée, puis seulement la vitesse.";
        }
        if (lower.indexOf("mobilité") >= 0 || lower.indexOf("mobilite") >= 0) {
            return "bouge lentement, sans forcer l'amplitude. Le but est de préparer les articulations, pas de chercher la douleur.";
        }
        if (lower.indexOf("étirement") >= 0 || lower.indexOf("etirement") >= 0) {
            return "tiens une tension douce, respire profondément et évite les à-coups. Relâche si ça devient douloureux.";
        }
        if (lower.indexOf("repos") >= 0) {
            return "priorité au sommeil, à l'hydratation et à une marche légère si tu en as envie. Pas besoin de compenser.";
        }
        if (lower.indexOf("préparation") >= 0 || lower.indexOf("preparation") >= 0) {
            return "prépare tes créneaux, ton matériel et un repas simple. Une semaine claire rend l'entraînement plus facile à tenir.";
        }
        return "reste gainé, garde un mouvement contrôlé et choisis une difficulté qui permet une exécution propre.";
    }

    private String techniqueAdvice(String lower) {
        boolean asksAdvice = lower.indexOf("conseil") >= 0
                || lower.indexOf("technique") >= 0
                || lower.indexOf("forme") >= 0
                || lower.indexOf("bien faire") >= 0
                || lower.indexOf("réaliser") >= 0
                || lower.indexOf("realiser") >= 0
                || lower.indexOf("comment faire") >= 0;
        if (!asksAdvice) return null;

        String planned = exerciseAdviceFromMessage(lower);
        if (planned != null) return planned;

        if (lower.indexOf("curl") >= 0 || lower.indexOf("biceps") >= 0) {
            return fullExerciseAdvice("Curl biceps");
        }
        if (lower.indexOf("squat") >= 0 || lower.indexOf("presse") >= 0 || lower.indexOf("cuisses") >= 0) {
            if (lower.indexOf("presse") >= 0) {
                return fullExerciseAdvice("Presse à cuisses");
            }
            return fullExerciseAdvice("Squat poids du corps");
        }
        if (lower.indexOf("pompe") >= 0 || lower.indexOf("push") >= 0) {
            return fullExerciseAdvice("Pompes");
        }
        if (lower.indexOf("rowing") >= 0 || lower.indexOf("tirage") >= 0 || lower.indexOf("row") >= 0) {
            return fullExerciseAdvice("Rowing haltères");
        }
        if (lower.indexOf("développé") >= 0 || lower.indexOf("developpe") >= 0 || lower.indexOf("couché") >= 0 || lower.indexOf("couche") >= 0 || lower.indexOf("épaule") >= 0 || lower.indexOf("epaule") >= 0) {
            return fullExerciseAdvice(lower.indexOf("épaule") >= 0 || lower.indexOf("epaule") >= 0 ? "Développé épaules" : "Développé couché");
        }
        if (lower.indexOf("soulevé") >= 0 || lower.indexOf("souleve") >= 0 || lower.indexOf("terre") >= 0 || lower.indexOf("roumain") >= 0) {
            return fullExerciseAdvice("Soulevé de terre roumain");
        }
        if (lower.indexOf("fente") >= 0) {
            return fullExerciseAdvice("Fentes arrière");
        }
        if (lower.indexOf("gainage") >= 0 || lower.indexOf("planche") >= 0) {
            return fullExerciseAdvice("Gainage face");
        }
        if (lower.indexOf("crunch") >= 0 || lower.indexOf("abdos") >= 0) {
            return "Conseil abdos / crunch : monte avec les abdos, pas avec la nuque. Garde le menton légèrement rentré, expire en montant, et contrôle la descente. Si tu sens surtout le cou, réduis l'amplitude.";
        }
        if (lower.indexOf("mountain") >= 0 || lower.indexOf("climber") >= 0) {
            return "Conseil mountain climbers : position de pompe solide, épaules au-dessus des mains. Ramène les genoux sans laisser les hanches rebondir. Commence lentement, puis accélère seulement si tu gardes le gainage.";
        }
        if (lower.indexOf("pont") >= 0 || lower.indexOf("fessier") >= 0) {
            return "Conseil pont fessier : pieds proches des fesses, pousse dans les talons, serre les fessiers en haut. Ne cambre pas le bas du dos pour monter plus haut. Redescends lentement.";
        }
        if (lower.indexOf("mollet") >= 0) {
            return "Conseil mollets : monte haut sur la pointe des pieds, marque une petite pause, puis descends lentement. Garde les genoux stables et évite les rebonds rapides.";
        }
        if (lower.indexOf("chaise") >= 0) {
            return "Conseil chaise au mur : dos contre le mur, pieds sous les genoux, ventre gainé. Vise une position confortable autour de 90 degrés, et remonte avant que les genoux ou le bas du dos gênent.";
        }
        if (lower.indexOf("farmer") >= 0 || lower.indexOf("marche") >= 0) {
            return "Conseil farmer walk / marche chargée : grandis-toi, épaules basses, ventre gainé. Marche lentement sans te pencher d'un côté. Si la posture change, prends moins lourd.";
        }
        if (lower.indexOf("course") >= 0 || lower.indexOf("courir") >= 0 || lower.indexOf("vélo") >= 0 || lower.indexOf("velo") >= 0) {
            return "Conseil cardio : commence à une intensité où tu peux encore parler. Augmente d'abord la durée, puis la vitesse. Si tu es essoufflé trop vite, ralentis avant de t'arrêter.";
        }
        return "Dis-moi l'exercice exact et je te donne les bons repères. Exemple : \"conseil pour les curls\", \"conseil squat\", \"conseil rowing\", \"conseil presse à cuisses\".";
    }

    private LinearLayout header(String overline, String main) {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.addView(kicker(overline.toUpperCase(Locale.FRANCE)));
        layout.addView(centerText(main, 30, text, Typeface.BOLD));
        return layout;
    }

    private LinearLayout page() {
        LinearLayout body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setGravity(Gravity.CENTER_HORIZONTAL);
        body.setClipToPadding(false);
        return body;
    }

    private void mount(LinearLayout body, boolean withNav, String selected) {
        FrameLayout root = new FrameLayout(this);
        root.setSystemUiVisibility(0);
        if ("battle".equals(selected)) {
            ImageView arenaBg = new ImageView(this);
            arenaBg.setImageResource(arenaBackdropDrawable());
            arenaBg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            arenaBg.setScaleX(1.10f);
            arenaBg.setScaleY(1.10f);
            root.addView(arenaBg, new FrameLayout.LayoutParams(-1, -1));
            root.addView(new BattleArenaEffectView(this, arenaName()), new FrameLayout.LayoutParams(-1, -1));
            View shade = new View(this);
            shade.setBackgroundColor(Color.argb(178, 3, 10, 14));
            root.addView(shade, new FrameLayout.LayoutParams(-1, -1));
        } else {
            root.addView(new FitBackgroundView(this), new FrameLayout.LayoutParams(-1, -1));
        }

        LinearLayout outer = new LinearLayout(this);
        outer.setOrientation(LinearLayout.VERTICAL);
        outer.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            public WindowInsets onApplyWindowInsets(View view, WindowInsets insets) {
                int top = 0;
                int bottom = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    android.graphics.Insets bars = insets.getInsets(WindowInsets.Type.systemBars());
                    top = bars.top;
                    bottom = bars.bottom;
                } else {
                    top = insets.getSystemWindowInsetTop();
                    bottom = insets.getSystemWindowInsetBottom();
                }
                view.setPadding(0, top, 0, bottom);
                return insets;
            }
        });
        root.addView(outer, new FrameLayout.LayoutParams(-1, -1));

        if (withNav) {
            outer.addView(levelHeader(), new LinearLayout.LayoutParams(-1, -2));
        }

        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(false);
        scroll.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scroll.addView(body, new ScrollView.LayoutParams(-1, -2));
        outer.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        if (withNav) {
            outer.addView(nav(selected), new LinearLayout.LayoutParams(-1, dp(78)));
        }
        setContentView(root);
        outer.requestApplyInsets();
        if (pendingScrollRestoreY >= 0 && selected.equals(pendingScrollRestoreScreen)) {
            final ScrollView targetScroll = scroll;
            final int restoreY = pendingScrollRestoreY;
            pendingScrollRestoreY = -1;
            pendingScrollRestoreScreen = "";
            handler.post(new Runnable() {
                public void run() {
                    targetScroll.scrollTo(0, restoreY);
                }
            });
        }
    }

    private void rememberScroll(View source, String screen) {
        ScrollView scroll = parentScrollView(source);
        if (scroll == null) return;
        pendingScrollRestoreY = scroll.getScrollY();
        pendingScrollRestoreScreen = screen;
    }

    private ScrollView parentScrollView(View source) {
        android.view.ViewParent parent = source == null ? null : source.getParent();
        while (parent instanceof View) {
            if (parent instanceof ScrollView) return (ScrollView) parent;
            parent = ((View) parent).getParent();
        }
        return null;
    }

    private ArrayList<DayPlan> customPlanFromCache() {
        if (!customPlanActive) return null;
        if (customPlanJson == null || customPlanJson.length() == 0) return null;
        try {
            JSONObject program = new JSONObject(customPlanJson);
            JSONArray days = program.optJSONArray("days");
            if (days == null || days.length() == 0) return null;
            ArrayList<DayPlan> plan = new ArrayList<DayPlan>();
            for (int i = 0; i < days.length(); i++) {
                JSONObject dayJson = days.optJSONObject(i);
                if (dayJson == null) continue;
                DayPlan day = new DayPlan(dayJson.optString("day", "Jour"), dayJson.optString("focus", "Séance personnalisée"));
                JSONArray exercises = dayJson.optJSONArray("exercises");
                if (exercises != null) {
                    for (int j = 0; j < exercises.length(); j++) {
                        String value = exercises.optString(j, "");
                        if (value.length() > 0) day.add(value);
                    }
                }
                if (day.exercises.size() == 0) day.add(customExercise("Repos complet", "1", "Repos", "Aucun", "Libre"));
                plan.add(day);
            }
            return plan.size() == 0 ? null : plan;
        } catch (Exception ignored) {
        }
        return null;
    }

    private int arenaBackdropDrawable() {
        String arena = arenaName();
        if (arena.indexOf("Désert") >= 0) return R.drawable.bg_sunset;
        if (arena.indexOf("Montagne") >= 0) return R.drawable.bg_blizzard;
        if (arena.indexOf("paradis") >= 0) return R.drawable.bg_gods_temple;
        if (arena.indexOf("Vaisseau") >= 0) return R.drawable.bg_luxury_gym;
        if (arena.indexOf("Olympe") >= 0) return R.drawable.bg_gods_temple;
        return R.drawable.bg_prairie;
    }

    private LinearLayout nav(String selected) {
        LinearLayout nav = new LinearLayout(this);
        nav.setOrientation(LinearLayout.HORIZONTAL);
        nav.setGravity(Gravity.CENTER);
        nav.setPadding(dp(10), dp(10), dp(10), dp(12));
        nav.setBackground(round(Color.rgb(8, 16, 22), dp(22), stroke, 1));
        addNavButton(nav, "Accueil", "home", selected);
        addNavButton(nav, "Plan", "plan", selected);
        addNavButton(nav, "Battle", "battle", selected);
        addNavButton(nav, "Boutique", "shop", selected);
        addNavButton(nav, "Profil", "profile", selected);
        return nav;
    }

    private void addNavButton(LinearLayout nav, String label, final String target, String selected) {
        Button button = new Button(this);
        final boolean isBattle = "battle".equals(target);
        button.setText(isBattle ? "⚡\nBattle" : label);
        button.setAllCaps(false);
        button.setTextSize(isBattle ? 11 : 12);
        button.setGravity(Gravity.CENTER);
        button.setTextColor(isBattle ? Color.rgb(4, 18, 24) : target.equals(selected) ? Color.rgb(4, 18, 24) : muted);
        button.setTypeface(Typeface.DEFAULT, isBattle ? Typeface.BOLD : Typeface.NORMAL);
        if (isBattle) {
            button.setBackground(ovalGradient(Color.rgb(255, 224, 95), Color.rgb(27, 214, 196)));
        } else {
            button.setBackground(target.equals(selected) ? gradient(teal, green, dp(18)) : transparentRound());
        }
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if ("home".equals(target)) showDashboard();
                if ("plan".equals(target)) showPlan();
                if ("battle".equals(target)) showBattle();
                if ("shop".equals(target)) showAvatar();
                if ("profile".equals(target)) showProfile();
            }
        });
        if (isBattle) {
            FrameLayout slot = new FrameLayout(this);
            LinearLayout.LayoutParams slotParams = new LinearLayout.LayoutParams(0, -1, 1);
            slotParams.setMargins(dp(3), 0, dp(3), 0);
            FrameLayout.LayoutParams battleParams = new FrameLayout.LayoutParams(dp(66), dp(66));
            battleParams.gravity = Gravity.CENTER;
            slot.addView(button, battleParams);
            nav.addView(slot, slotParams);
        } else {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, -1, 1);
            params.setMargins(dp(3), 0, dp(3), 0);
            nav.addView(button, params);
        }
    }

    private LinearLayout levelHeader() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setPadding(dp(16), dp(10), dp(16), dp(8));
        box.setBackground(round(Color.rgb(7, 17, 23), 0, Color.rgb(28, 51, 59), 1));

        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        AvatarHeadView avatarIcon = new AvatarHeadView(this);
        row.addView(avatarIcon, new LinearLayout.LayoutParams(dp(38), dp(38)));

        TextView levelText = centerText("LVL " + playerLevel(), 14, green, Typeface.BOLD);
        levelText.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
        LinearLayout.LayoutParams levelParams = new LinearLayout.LayoutParams(dp(54), dp(28));
        levelParams.setMargins(dp(8), 0, dp(6), 0);
        row.addView(levelText, levelParams);

        ProgressBar progress = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progress.setMax(xpPerLevel());
        progress.setProgress(xpProgressInLevel());
        row.addView(progress, new LinearLayout.LayoutParams(0, dp(16), 1));
        TextView coins = centerText(coinAmount(valhallaCoins), 12, Color.rgb(255, 190, 72), Typeface.BOLD);
        coins.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        row.addView(coins, new LinearLayout.LayoutParams(dp(78), dp(36)));
        box.addView(row, new LinearLayout.LayoutParams(-1, -2));

        TextView xpLine = centerText(xpProgressInLevel() + " / " + xpPerLevel() + " XP", 12, muted, Typeface.BOLD);
        xpLine.setGravity(Gravity.RIGHT);
        box.addView(xpLine, new LinearLayout.LayoutParams(-1, -2));
        return box;
    }

    private ImageView mascot(int heightPx) {
        ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.viking_mascot);
        image.setAdjustViewBounds(true);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setLayoutParams(new LinearLayout.LayoutParams(-1, heightPx));
        return image;
    }

    private View avatarMascot(int heightPx) {
        View hero = heroPreview(avatarType, heightPx);
        startWarriorIdle(hero);
        return hero;
    }

    private View heroPreview(String hero, int heightPx) {
        int resource = heroDrawableResource(hero);
        if (resource != 0) {
            ImageView image = new ImageView(this);
            image.setImageResource(resource);
            image.setAdjustViewBounds(true);
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
            image.setLayoutParams(new LinearLayout.LayoutParams(-1, heightPx));
            return image;
        }
        HeroAvatarView view = new HeroAvatarView(this, hero, true);
        view.setLayoutParams(new LinearLayout.LayoutParams(-1, heightPx));
        return view;
    }

    private int heroDrawableResource(String hero) {
        if ("Viking".equals(hero)) return R.drawable.viking_mascot;
        if ("Valkyrie".equals(hero)) return R.drawable.valkyrie_mascot;
        if ("Magicien".equals(hero)) return R.drawable.magicien_mascot;
        if ("Magicienne".equals(hero)) return R.drawable.magicienne_mascot;
        if ("THOR".equals(hero)) return R.drawable.thor_mascot;
        if ("FREYA".equals(hero)) return R.drawable.freya_mascot;
        return 0;
    }

    private FrameLayout avatarStage(int heightPx) {
        FrameLayout stage = new FrameLayout(this);
        stage.setBackground(round(Color.rgb(10, 24, 31), dp(20), stroke, 1));
        stage.setClipToOutline(true);

        ShopItem backdrop = activeBackdropItem();
        ImageView background = new ImageView(this);
        background.setImageResource(backdrop.drawable);
        background.setScaleType(ImageView.ScaleType.CENTER_CROP);
        stage.addView(background, new FrameLayout.LayoutParams(-1, -1));

        if (animatedBackdrop(backdrop)) {
            stage.addView(new AvatarBackdropEffectView(this, backdrop.id), new FrameLayout.LayoutParams(-1, -1));
        }

        View avatar = avatarMascot(heightPx - dp(22));
        FrameLayout.LayoutParams avatarParams = new FrameLayout.LayoutParams(-1, heightPx - dp(18));
        avatarParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        avatarParams.setMargins(dp(12), dp(12), dp(12), 0);
        stage.addView(avatar, avatarParams);

        stage.setLayoutParams(new LinearLayout.LayoutParams(-1, heightPx));
        return stage;
    }

    private ShopItem activeBackdropItem() {
        ShopItem[] items = backdropShopItems();
        for (int i = 0; i < items.length; i++) {
            if (isEquipped(items[i].id)) return items[i];
        }
        return items[0];
    }

    private boolean animatedBackdrop(ShopItem item) {
        return "Très rare".equals(item.rarity) || "Légendaire".equals(item.rarity);
    }

    private void startWarriorIdle(View image) {
        TranslateAnimation breath = new TranslateAnimation(0, 0, dp(2), -dp(4));
        breath.setDuration(1150);
        breath.setRepeatCount(Animation.INFINITE);
        breath.setRepeatMode(Animation.REVERSE);
        breath.setInterpolator(new AccelerateDecelerateInterpolator());

        RotateAnimation stance = new RotateAnimation(-1.4f, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.82f);
        stance.setDuration(1350);
        stance.setRepeatCount(Animation.INFINITE);
        stance.setRepeatMode(Animation.REVERSE);
        stance.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimationSet set = new AnimationSet(true);
        set.addAnimation(breath);
        set.addAnimation(stance);
        image.startAnimation(set);
    }

    private TextView kicker(String value) {
        TextView tv = centerText(value, 12, teal, Typeface.BOLD);
        tv.setLetterSpacing(0.16f);
        return tv;
    }

    private TextView title(String value) {
        TextView tv = centerText(value, 31, text, Typeface.BOLD);
        tv.setLineSpacing(dp(3), 1.0f);
        return tv;
    }

    private TextView subtitle(String value) {
        TextView tv = centerText(value, 16, muted, Typeface.NORMAL);
        tv.setLineSpacing(dp(4), 1.0f);
        return tv;
    }

    private TextView small(String value) {
        TextView tv = centerText(value, 12, muted, Typeface.NORMAL);
        tv.setLineSpacing(dp(3), 1.0f);
        return tv;
    }

    private TextView sectionTitle(String value) {
        TextView tv = centerText(value, 21, text, Typeface.BOLD);
        tv.setLineSpacing(dp(3), 1.0f);
        return tv;
    }

    private TextView sectionLabel(String value) {
        TextView tv = centerText(value, 14, teal, Typeface.BOLD);
        tv.setPadding(0, dp(4), 0, dp(6));
        return tv;
    }

    private TextView centerText(String value, int sp, int color, int style) {
        TextView tv = new TextView(this);
        tv.setText(value);
        tv.setTextColor(color);
        tv.setTextSize(sp);
        tv.setTypeface(Typeface.DEFAULT, style);
        tv.setGravity(Gravity.CENTER);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setIncludeFontPadding(true);
        tv.setPadding(dp(2), dp(2), dp(2), dp(2));
        return tv;
    }

    private TextView vikingText(String value, int sp, int color) {
        TextView tv = centerText(value.toUpperCase(Locale.FRANCE), sp, color, Typeface.BOLD);
        tv.setTypeface(Typeface.create(Typeface.SERIF, Typeface.BOLD));
        tv.setLetterSpacing(0.08f);
        tv.setShadowLayer(dp(2), 0, dp(2), Color.rgb(2, 8, 12));
        tv.setPadding(dp(4), dp(5), dp(4), dp(5));
        return tv;
    }

    private String coinAmount(int amount) {
        return amount + " 🪙 pièces";
    }

    private void flashMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private TextView infoLine(String label, String value) {
        TextView tv = centerText(label + " : " + value, 15, text, Typeface.NORMAL);
        tv.setPadding(dp(8), dp(8), dp(8), dp(8));
        return tv;
    }

    private TextView exerciseView(String value) {
        TextView tv = centerText(value, 15, text, Typeface.NORMAL);
        tv.setLineSpacing(dp(4), 1.0f);
        tv.setPadding(dp(12), dp(10), dp(12), dp(10));
        tv.setBackground(round(Color.rgb(10, 24, 31), dp(14), Color.rgb(35, 78, 89), 1));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, dp(8), 0, 0);
        tv.setLayoutParams(params);
        return tv;
    }

    private LinearLayout card() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);
        layout.setPadding(dp(18), dp(18), dp(18), dp(18));
        layout.setBackground(round(card, dp(22), stroke, 1));
        return layout;
    }

    private LinearLayout cardAccent() {
        LinearLayout layout = card();
        layout.setBackground(gradient(Color.rgb(11, 63, 75), Color.rgb(25, 114, 110), dp(22)));
        return layout;
    }

    private Button primaryButton(String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextColor(Color.rgb(3, 18, 22));
        button.setTextSize(16);
        button.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        button.setGravity(Gravity.CENTER);
        button.setMinHeight(dp(54));
        button.setPadding(dp(14), 0, dp(14), 0);
        button.setBackground(gradient(teal, green, dp(18)));
        return button;
    }

    private Button secondaryButton(String label) {
        Button button = primaryButton(label);
        button.setTextColor(text);
        button.setBackground(gradient(blue, teal, dp(18)));
        return button;
    }

    private Button quietButton(String label) {
        Button button = primaryButton(label);
        button.setTextColor(muted);
        button.setBackground(round(Color.rgb(9, 22, 30), dp(18), stroke, 1));
        return button;
    }

    private Button optionButton(String label) {
        Button button = quietButton(label);
        button.setTextColor(text);
        button.setMinHeight(dp(58));
        return button;
    }

    private EditText input(String hint, boolean password, boolean number) {
        EditText input = new EditText(this);
        input.setHint(hint);
        input.setHintTextColor(Color.rgb(132, 153, 164));
        input.setTextColor(text);
        input.setTextSize(16);
        input.setGravity(Gravity.CENTER);
        input.setSingleLine(true);
        input.setPadding(dp(14), 0, dp(14), 0);
        input.setMinHeight(dp(56));
        input.setBackground(round(Color.rgb(10, 24, 31), dp(18), Color.rgb(54, 92, 104), 1));
        if (password) {
            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        } else if (number) {
            input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        } else {
            input.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        return input;
    }

    private EditText multilineInput(String hint, int minHeightDp) {
        EditText input = input(hint, false, false);
        input.setSingleLine(false);
        input.setMinLines(3);
        input.setGravity(Gravity.CENTER);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        input.setMinHeight(dp(minHeightDp));
        input.setPadding(dp(14), dp(12), dp(14), dp(12));
        return input;
    }

    private ProgressBar progress(int value, int max) {
        ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        bar.setMax(max);
        bar.setProgress(value);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, dp(8));
        params.setMargins(0, dp(10), 0, 0);
        bar.setLayoutParams(params);
        return bar;
    }

    private void setSelectedGender(Button[] buttons, String selected) {
        for (int i = 0; i < buttons.length; i++) {
            Button button = buttons[i];
            if (button.getText().toString().equals(selected)) {
                button.setTextColor(Color.rgb(3, 18, 22));
                button.setBackground(gradient(teal, green, dp(18)));
            } else {
                button.setTextColor(text);
                button.setBackground(round(Color.rgb(10, 24, 31), dp(18), Color.rgb(54, 92, 104), 1));
            }
        }
    }

    private TextView addBotMessage(String value) {
        return addMessage("Coach VALHALLA RAGE", value, true);
    }

    private TextView addUserMessage(String value) {
        return addMessage("Toi", value, false);
    }

    private TextView addMessage(String label, String value, boolean bot) {
        if (chatLog == null) return null;
        TextView message = centerText(label + "\n" + value, 14, bot ? text : Color.rgb(4, 18, 22), Typeface.NORMAL);
        message.setLineSpacing(dp(4), 1.0f);
        message.setPadding(dp(14), dp(12), dp(14), dp(12));
        message.setBackground(bot ? round(cardSoft, dp(18), stroke, 1) : gradient(green, teal, dp(18)));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, dp(8), 0, 0);
        chatLog.addView(message, params);
        return message;
    }

    private void addPlanShortcut() {
        if (chatLog == null) return;
        Button button = secondaryButton("Voir le tableau mis à jour");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showPlan();
            }
        });
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, dp(10), 0, dp(2));
        chatLog.addView(button, params);
    }

    private boolean isPlanRequest(String message) {
        String lower = message == null ? "" : message.toLowerCase(Locale.FRANCE);
        return lower.indexOf("tableau") >= 0
                || lower.indexOf("programme") >= 0
                || lower.indexOf("plan de sport") >= 0
                || lower.indexOf("séance de la semaine") >= 0;
    }

    private int planDirectionFromMessage(String message) {
        String lower = message == null ? "" : message.toLowerCase(Locale.FRANCE);
        if (isTooHardMessage(lower)) return -1;
        if (isTooEasyMessage(lower)) return 1;
        return 0;
    }

    private void showCoachPlanProposal(String request, String action) {
        int direction = planDirectionFromMessage(request);
        if (!previewPlanActive) {
            previewPlanAdjustment = activePlanAdjustment() + direction;
        } else if (direction != 0) {
            previewPlanAdjustment = previewPlanAdjustment + direction;
        }
        if (previewPlanAdjustment < -2) previewPlanAdjustment = -2;
        if (previewPlanAdjustment > 2) previewPlanAdjustment = 2;
        previewPlanActive = true;
        hasPendingCoachPlan = true;
        pendingCoachPlanAdjustment = previewPlanAdjustment;
        coachWaitingForPlanFeedback = false;

        addBotMessage("Voici une vraie proposition de tableau, adaptée à ton profil. Vérifie les exercices, les séries, les répétitions et les repos.");
        addChatPlanTable(buildPlan());
        addBotMessage("Est-ce que ce nouveau tableau te convient ? Il ne deviendra officiel pour cette semaine qu'après ta confirmation.");

        Button accept = primaryButton("Oui, ce tableau me convient");
        accept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) { acceptCoachPlan(); }
        });
        chatLog.addView(accept, chatButtonParams());

        Button reject = secondaryButton("Non, je veux le modifier");
        reject.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                coachWaitingForPlanFeedback = true;
                addBotMessage("D'accord. Dis-moi ce qui doit changer : plus facile, plus dur, moins de séries, plus de repos, ou un exercice précis.");
            }
        });
        chatLog.addView(reject, chatButtonParams());
    }

    private LinearLayout.LayoutParams chatButtonParams() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(0, dp(10), 0, dp(2));
        return params;
    }

    private void addChatPlanTable(ArrayList<DayPlan> plan) {
        HorizontalScrollView scroll = new HorizontalScrollView(this);
        scroll.setHorizontalScrollBarEnabled(false);
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, dp(8), dp(12), dp(8));
        for (int i = 0; i < plan.size(); i++) {
            DayPlan day = plan.get(i);
            LinearLayout column = new LinearLayout(this);
            column.setOrientation(LinearLayout.VERTICAL);
            column.setGravity(Gravity.CENTER_HORIZONTAL);
            column.setPadding(dp(10), dp(12), dp(10), dp(12));
            column.setBackground(round(cardSoft, dp(16), stroke, 1));
            column.addView(kicker(day.day.toUpperCase(Locale.FRANCE)));
            TextView focus = centerText(day.focus, 14, text, Typeface.BOLD);
            focus.setMaxLines(2);
            focus.setEllipsize(TextUtils.TruncateAt.END);
            column.addView(focus);
            for (int j = 0; j < day.exercises.size(); j++) {
                column.addView(planExerciseCell(day.exercises.get(j)));
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dp(205), -2);
            params.setMargins(0, 0, dp(8), 0);
            row.addView(column, params);
        }
        scroll.addView(row, new HorizontalScrollView.LayoutParams(-2, -2));
        LinearLayout.LayoutParams outer = new LinearLayout.LayoutParams(-1, -2);
        outer.setMargins(0, dp(4), 0, dp(4));
        chatLog.addView(scroll, outer);
    }

    private void acceptCoachPlan() {
        JSONObject acceptedProgram = programJsonFromPlan(buildPlan(), "coach");
        officialPlanWeek = weekKey();
        officialPlanAdjustment = pendingCoachPlanAdjustment;
        planAdjustment = officialPlanAdjustment;
        previewPlanActive = false;
        hasPendingCoachPlan = false;
        coachWaitingForPlanFeedback = false;
        saveProfile(true);
        syncProgramToServerAsync(acceptedProgram);
        addBotMessage("Parfait. Ce tableau devient ton plan officiel jusqu'à la fin de la semaine. Tu peux le retrouver dans l'onglet Plan.");
        addPlanShortcut();
    }

    private void handleCoachPlanFeedback(String feedback) {
        int direction = planDirectionFromMessage(feedback);
        String lower = feedback.toLowerCase(Locale.FRANCE);
        if (lower.indexOf("repos") >= 0 && direction == 0) direction = -1;
        if (lower.indexOf("série") >= 0 && (lower.indexOf("moins") >= 0 || lower.indexOf("rédu") >= 0)) direction = -1;
        if (lower.indexOf("série") >= 0 && (lower.indexOf("plus") >= 0 || lower.indexOf("ajout") >= 0)) direction = 1;
        if (direction == 0) {
            addBotMessage("Je garde la même intensité et je vais surtout revoir le point que tu as indiqué. Pour une adaptation chiffrée, précise si tu veux plus facile ou plus dur.");
        }
        showCoachPlanProposal(feedback, "feedback");
    }

    private CheckBox rememberBox(String label) {
        CheckBox box = new CheckBox(this);
        box.setText(label);
        box.setTextColor(muted);
        box.setTextSize(14);
        box.setGravity(Gravity.CENTER);
        box.setButtonTintList(android.content.res.ColorStateList.valueOf(teal));
        box.setPadding(dp(8), dp(8), dp(8), dp(8));
        return box;
    }

    private View space(int height) {
        View view = new View(this);
        view.setLayoutParams(new LinearLayout.LayoutParams(1, height));
        return view;
    }

    private void addWithMargins(LinearLayout parent, View child, int left, int top, int right, int bottom) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.setMargins(dp(left), dp(top), dp(right), dp(bottom));
        parent.addView(child, params);
    }

    private void addWithMargins(LinearLayout parent, View child, int left, int top, int right, int bottom, int width, int height) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.setMargins(dp(left), dp(top), dp(right), dp(bottom));
        parent.addView(child, params);
    }

    private GradientDrawable round(int color, int radius, int strokeColor, int strokeWidth) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        drawable.setStroke(dp(strokeWidth), strokeColor);
        return drawable;
    }

    private GradientDrawable gradient(int start, int end, int radius) {
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, new int[] {start, end});
        drawable.setCornerRadius(radius);
        return drawable;
    }

    private GradientDrawable ovalGradient(int start, int end) {
        GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[] {start, Color.rgb(255, 190, 72), end});
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setStroke(dp(2), Color.rgb(255, 244, 170));
        return drawable;
    }

    private GradientDrawable transparentRound() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.TRANSPARENT);
        drawable.setCornerRadius(dp(18));
        return drawable;
    }

    private int heroPrimaryColor(String hero) {
        if ("THOR".equals(hero)) return Color.rgb(61, 146, 255);
        if ("FREYA".equals(hero)) return Color.rgb(255, 94, 156);
        if ("Magicien".equals(hero)) return Color.rgb(87, 73, 190);
        if ("Magicienne".equals(hero)) return Color.rgb(171, 78, 214);
        if ("Valkyrie".equals(hero)) return Color.rgb(225, 224, 238);
        return Color.rgb(123, 81, 44);
    }

    private int heroSecondaryColor(String hero) {
        if ("THOR".equals(hero) || "FREYA".equals(hero)) return Color.rgb(255, 211, 91);
        if ("Magicien".equals(hero) || "Magicienne".equals(hero)) return Color.rgb(42, 220, 202);
        if ("Valkyrie".equals(hero)) return Color.rgb(99, 189, 255);
        return Color.rgb(201, 64, 48);
    }

    private boolean femaleHero(String hero) {
        return "Valkyrie".equals(hero) || "Magicienne".equals(hero) || "FREYA".equals(hero);
    }

    private boolean mageHero(String hero) {
        return "Magicien".equals(hero) || "Magicienne".equals(hero);
    }

    private void drawHeroAvatar(Canvas canvas, Paint paint, RectF area, String hero, boolean fullBody) {
        float width = area.width();
        float height = area.height();
        float cx = area.centerX();
        float top = area.top;
        float bottom = area.bottom;

        paint.setStyle(Paint.Style.FILL);
        if (premiumHero(hero)) {
            paint.setColor(Color.argb(90, 255, 218, 92));
            canvas.drawCircle(cx, top + height * 0.45f, Math.min(width, height) * 0.34f, paint);
            paint.setStrokeWidth(dp(4));
            paint.setColor(Color.argb(220, 255, 240, 122));
            for (int i = 0; i < 4; i++) {
                float x = area.left + width * (0.22f + i * 0.18f);
                canvas.drawLine(x, top + height * 0.08f, x + dp(18), top + height * 0.38f, paint);
                canvas.drawLine(x + dp(18), top + height * 0.38f, x - dp(4), top + height * 0.62f, paint);
            }
        }

        if (fullBody) {
            float headY = top + height * 0.22f;
            float headR = Math.min(width, height) * 0.095f;
            float torsoTop = top + height * 0.34f;
            float torsoBottom = bottom - height * 0.16f;

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(95, 0, 0, 0));
            canvas.drawOval(cx - width * 0.22f, bottom - dp(18), cx + width * 0.22f, bottom - dp(4), paint);

            if ("FREYA".equals(hero)) {
                paint.setColor(Color.argb(180, 255, 180, 217));
                canvas.drawOval(cx - width * 0.35f, torsoTop - dp(10), cx - width * 0.08f, torsoBottom, paint);
                canvas.drawOval(cx + width * 0.08f, torsoTop - dp(10), cx + width * 0.35f, torsoBottom, paint);
            }

            paint.setColor(heroPrimaryColor(hero));
            RectF torso = new RectF(cx - width * 0.16f, torsoTop, cx + width * 0.16f, torsoBottom);
            canvas.drawRoundRect(torso, dp(18), dp(18), paint);

            paint.setColor(heroSecondaryColor(hero));
            android.graphics.Path chest = new android.graphics.Path();
            chest.moveTo(cx, torsoTop + dp(12));
            chest.lineTo(cx - width * 0.13f, torsoTop + height * 0.18f);
            chest.lineTo(cx + width * 0.13f, torsoTop + height * 0.18f);
            chest.close();
            canvas.drawPath(chest, paint);

            paint.setStrokeWidth(dp(8));
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setColor(heroPrimaryColor(hero));
            canvas.drawLine(cx - width * 0.14f, torsoTop + dp(18), cx - width * 0.27f, torsoTop + height * 0.22f, paint);
            canvas.drawLine(cx + width * 0.14f, torsoTop + dp(18), cx + width * 0.27f, torsoTop + height * 0.22f, paint);
            canvas.drawLine(cx - width * 0.07f, torsoBottom - dp(2), cx - width * 0.13f, bottom - height * 0.04f, paint);
            canvas.drawLine(cx + width * 0.07f, torsoBottom - dp(2), cx + width * 0.13f, bottom - height * 0.04f, paint);

            if (mageHero(hero)) {
                paint.setColor(Color.rgb(255, 235, 130));
                paint.setStrokeWidth(dp(5));
                canvas.drawLine(cx + width * 0.30f, torsoTop, cx + width * 0.34f, bottom - height * 0.08f, paint);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(cx + width * 0.30f, torsoTop - dp(8), dp(8), paint);
            } else if ("THOR".equals(hero)) {
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.rgb(120, 126, 136));
                canvas.drawRoundRect(new RectF(cx + width * 0.24f, torsoTop + height * 0.10f, cx + width * 0.39f, torsoTop + height * 0.20f), dp(4), dp(4), paint);
                paint.setStrokeWidth(dp(5));
                paint.setColor(Color.rgb(101, 62, 37));
                canvas.drawLine(cx + width * 0.31f, torsoTop + height * 0.20f, cx + width * 0.25f, torsoTop + height * 0.34f, paint);
            } else if ("Viking".equals(hero)) {
                paint.setStrokeWidth(dp(5));
                paint.setColor(Color.rgb(130, 134, 138));
                canvas.drawLine(cx + width * 0.28f, torsoTop + height * 0.07f, cx + width * 0.36f, torsoTop + height * 0.30f, paint);
            }

            drawHeroHead(canvas, paint, new RectF(cx - headR, headY - headR, cx + headR, headY + headR), hero);
        } else {
            drawHeroHead(canvas, paint, area, hero);
        }
    }

    private void drawHeroHead(Canvas canvas, Paint paint, RectF area, String hero) {
        float cx = area.centerX();
        float cy = area.centerY();
        float radius = Math.min(area.width(), area.height()) / 2f;

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(femaleHero(hero) ? Color.rgb(242, 188, 139) : Color.rgb(224, 160, 111));
        canvas.drawCircle(cx, cy, radius * 0.72f, paint);

        paint.setColor(femaleHero(hero) ? Color.rgb(245, 208, 91) : Color.rgb(91, 56, 38));
        if (mageHero(hero)) {
            paint.setColor(heroPrimaryColor(hero));
            android.graphics.Path hood = new android.graphics.Path();
            hood.moveTo(cx, cy - radius * 1.15f);
            hood.lineTo(cx - radius * 0.92f, cy + radius * 0.05f);
            hood.lineTo(cx + radius * 0.92f, cy + radius * 0.05f);
            hood.close();
            canvas.drawPath(hood, paint);
        } else {
            canvas.drawArc(new RectF(cx - radius * 0.82f, cy - radius * 0.88f, cx + radius * 0.82f, cy + radius * 0.72f), 185, 170, true, paint);
        }

        if ("Viking".equals(hero) || "THOR".equals(hero)) {
            paint.setColor(heroSecondaryColor(hero));
            canvas.drawRoundRect(new RectF(cx - radius * 0.78f, cy - radius * 0.86f, cx + radius * 0.78f, cy - radius * 0.42f), dp(8), dp(8), paint);
            paint.setColor(Color.rgb(236, 238, 230));
            android.graphics.Path leftHorn = new android.graphics.Path();
            leftHorn.moveTo(cx - radius * 0.70f, cy - radius * 0.65f);
            leftHorn.lineTo(cx - radius * 1.25f, cy - radius * 0.98f);
            leftHorn.lineTo(cx - radius * 0.92f, cy - radius * 0.34f);
            leftHorn.close();
            canvas.drawPath(leftHorn, paint);
            android.graphics.Path rightHorn = new android.graphics.Path();
            rightHorn.moveTo(cx + radius * 0.70f, cy - radius * 0.65f);
            rightHorn.lineTo(cx + radius * 1.25f, cy - radius * 0.98f);
            rightHorn.lineTo(cx + radius * 0.92f, cy - radius * 0.34f);
            rightHorn.close();
            canvas.drawPath(rightHorn, paint);
        } else if ("Valkyrie".equals(hero) || "FREYA".equals(hero)) {
            paint.setColor(heroSecondaryColor(hero));
            canvas.drawRoundRect(new RectF(cx - radius * 0.74f, cy - radius * 0.82f, cx + radius * 0.74f, cy - radius * 0.44f), dp(8), dp(8), paint);
            paint.setColor(Color.rgb(238, 244, 255));
            canvas.drawOval(cx - radius * 1.28f, cy - radius * 0.88f, cx - radius * 0.64f, cy - radius * 0.28f, paint);
            canvas.drawOval(cx + radius * 0.64f, cy - radius * 0.88f, cx + radius * 1.28f, cy - radius * 0.28f, paint);
        }

        paint.setColor(Color.rgb(20, 35, 42));
        canvas.drawCircle(cx - radius * 0.23f, cy - radius * 0.05f, radius * 0.055f, paint);
        canvas.drawCircle(cx + radius * 0.23f, cy - radius * 0.05f, radius * 0.055f, paint);
        paint.setStrokeWidth(Math.max(1f, radius * 0.055f));
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawArc(new RectF(cx - radius * 0.22f, cy + radius * 0.15f, cx + radius * 0.22f, cy + radius * 0.42f), 15, 150, false, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private class HeroAvatarView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final String hero;
        private final boolean fullBody;

        HeroAvatarView(Context context, String hero, boolean fullBody) {
            super(context);
            this.hero = hero;
            this.fullBody = fullBody;
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            RectF area = new RectF(dp(8), dp(4), getWidth() - dp(8), getHeight() - dp(4));
            drawHeroAvatar(canvas, paint, area, hero, fullBody);
        }
    }

    private class ShopItemVisualView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final String itemName;
        private final String rarity;
        private final String hero;

        ShopItemVisualView(Context context, String itemName, String rarity, String hero) {
            super(context);
            this.itemName = itemName == null ? "" : itemName;
            this.rarity = rarity == null ? "Commun" : rarity;
            this.hero = hero == null ? "Tous" : hero;
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int width = getWidth();
            int height = getHeight();
            if (width == 0 || height == 0) return;
            float cx = width / 2f;
            float cy = height / 2f;
            int accent = rarityColor(rarity);
            int heroColor = heroPrimaryColor(hero);
            int second = heroSecondaryColor(hero);

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(55, Color.red(accent), Color.green(accent), Color.blue(accent)));
            canvas.drawRoundRect(new RectF(dp(6), dp(6), width - dp(6), height - dp(6)), dp(18), dp(18), paint);

            paint.setColor(heroColor);
            canvas.drawCircle(cx, cy, Math.min(width, height) * 0.30f, paint);
            paint.setColor(second);
            canvas.drawCircle(cx, cy, Math.min(width, height) * 0.20f, paint);

            paint.setStrokeWidth(dp(4));
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setColor(accent);
            String lower = itemName.toLowerCase(Locale.FRANCE);
            if (lower.indexOf("bâton") >= 0 || lower.indexOf("baton") >= 0 || lower.indexOf("lance") >= 0 || lower.indexOf("hache") >= 0) {
                canvas.drawLine(cx - width * 0.24f, height - dp(14), cx + width * 0.22f, dp(14), paint);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(cx + width * 0.25f, dp(18), dp(9), paint);
            } else if (lower.indexOf("cape") >= 0 || lower.indexOf("manteau") >= 0 || lower.indexOf("robe") >= 0 || lower.indexOf("jupe") >= 0) {
                android.graphics.Path cloth = new android.graphics.Path();
                cloth.moveTo(cx, dp(14));
                cloth.lineTo(cx - width * 0.28f, height - dp(12));
                cloth.lineTo(cx + width * 0.28f, height - dp(12));
                cloth.close();
                canvas.drawPath(cloth, paint);
            } else if (lower.indexOf("diadème") >= 0 || lower.indexOf("diademe") >= 0 || lower.indexOf("couronne") >= 0 || lower.indexOf("serre") >= 0 || lower.indexOf("chapeau") >= 0) {
                canvas.drawArc(new RectF(cx - width * 0.28f, cy - dp(18), cx + width * 0.28f, cy + dp(18)), 200, 140, false, paint);
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(cx, cy - dp(16), dp(6), paint);
            } else if (lower.indexOf("ailes") >= 0) {
                paint.setStyle(Paint.Style.FILL);
                canvas.drawOval(cx - width * 0.42f, cy - dp(20), cx - width * 0.04f, cy + dp(24), paint);
                canvas.drawOval(cx + width * 0.04f, cy - dp(20), cx + width * 0.42f, cy + dp(24), paint);
            } else if (lower.indexOf("anneau") >= 0) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(7));
                canvas.drawCircle(cx, cy, dp(22), paint);
            } else if (lower.indexOf("grimoire") >= 0) {
                paint.setStyle(Paint.Style.FILL);
                canvas.drawRoundRect(new RectF(cx - width * 0.24f, cy - dp(24), cx + width * 0.24f, cy + dp(24)), dp(8), dp(8), paint);
                paint.setColor(Color.rgb(255, 235, 160));
                canvas.drawLine(cx, cy - dp(20), cx, cy + dp(20), paint);
            } else {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(dp(5));
                canvas.drawRoundRect(new RectF(cx - width * 0.25f, cy - dp(24), cx + width * 0.25f, cy + dp(24)), dp(12), dp(12), paint);
            }

            paint.setStyle(Paint.Style.FILL);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(dp(9));
            paint.setTypeface(Typeface.DEFAULT_BOLD);
            paint.setColor(text);
            String initials = itemInitials(itemName);
            canvas.drawText(initials, cx, height - dp(10), paint);
        }
    }

    private String itemInitials(String name) {
        StringBuilder result = new StringBuilder();
        String[] parts = name.split(" ");
        for (int i = 0; i < parts.length && result.length() < 3; i++) {
            if (parts[i].length() == 0) continue;
            result.append(parts[i].substring(0, 1).toUpperCase(Locale.FRANCE));
        }
        return result.length() == 0 ? "VR" : result.toString();
    }

    private class AvatarHeadView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        AvatarHeadView(Context context) {
            super(context);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int width = getWidth();
            int height = getHeight();
            if (width == 0 || height == 0) return;
            float radius = Math.min(width, height) / 2f;
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(16, 57, 66));
            canvas.drawCircle(width / 2f, height / 2f, radius - dp(1), paint);

            canvas.save();
            android.graphics.Path clip = new android.graphics.Path();
            clip.addCircle(width / 2f, height / 2f, radius - dp(3), android.graphics.Path.Direction.CW);
            canvas.clipPath(clip);
            int resource = heroDrawableResource(avatarType);
            Bitmap hero = resource == 0 ? null : BitmapFactory.decodeResource(getResources(), resource);
            if (hero != null) {
                Rect source = new Rect(hero.getWidth() / 4, 0, hero.getWidth() * 3 / 4, hero.getHeight() * 52 / 100);
                RectF target = new RectF(dp(1), dp(1), width - dp(1), height - dp(1));
                canvas.drawBitmap(hero, source, target, paint);
                hero.recycle();
            } else {
                drawHeroAvatar(canvas, paint, new RectF(dp(4), dp(4), width - dp(4), height - dp(4)), avatarType, false);
            }
            canvas.restore();

            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2));
            paint.setColor(teal);
            canvas.drawCircle(width / 2f, height / 2f, radius - dp(2), paint);
        }
    }

    private class WeightChartView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        WeightChartView(Context context) {
            super(context);
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int width = getWidth();
            int height = getHeight();
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(10, 24, 31));
            canvas.drawRoundRect(new RectF(0, 0, width, height), dp(16), dp(16), paint);

            ArrayList<Float> values = new ArrayList<Float>();
            ArrayList<String> dates = new ArrayList<String>();
            if (weightHistory.length() > 0) {
                String[] entries = weightHistory.split(";");
                int start = Math.max(0, entries.length - weightChartMaxPoints());
                for (int i = start; i < entries.length; i++) {
                    int cut = entries[i].indexOf("=");
                    if (cut <= 0) continue;
                    try {
                        values.add(Float.parseFloat(entries[i].substring(cut + 1).replace(",", ".")));
                        dates.add(entries[i].substring(5, Math.min(10, cut)));
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
            if (values.size() == 0) {
                paint.setColor(muted);
                paint.setTextSize(dp(14));
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Enregistre ton premier poids pour commencer le suivi", width / 2f, height / 2f, paint);
                return;
            }

            float min = values.get(0);
            float max = values.get(0);
            for (int i = 1; i < values.size(); i++) {
                min = Math.min(min, values.get(i));
                max = Math.max(max, values.get(i));
            }
            if (weightTarget.length() > 0) {
                try {
                    float target = Float.parseFloat(weightTarget.replace(",", "."));
                    min = Math.min(min, target);
                    max = Math.max(max, target);
                } catch (NumberFormatException ignored) {
                }
            }
            float padding = Math.max(1f, (max - min) * 0.18f);
            min -= padding;
            max += padding;
            if (max - min < 2f) {
                min -= 1f;
                max += 1f;
            }

            float left = dp(32);
            float right = width - dp(16);
            float top = dp(20);
            float bottom = height - dp(28);
            paint.setStrokeWidth(dp(1));
            paint.setColor(Color.rgb(42, 67, 76));
            for (int i = 0; i < 4; i++) {
                float y = top + (bottom - top) * i / 3f;
                canvas.drawLine(left, y, right, y, paint);
            }
            if (weightTarget.length() > 0) {
                try {
                    float target = Float.parseFloat(weightTarget.replace(",", "."));
                    float targetY = bottom - ((target - min) / (max - min)) * (bottom - top);
                    paint.setColor(Color.argb(180, 161, 241, 83));
                    paint.setStrokeWidth(dp(2));
                    canvas.drawLine(left, targetY, right, targetY, paint);
                    paint.setTextSize(dp(11));
                    paint.setTextAlign(Paint.Align.RIGHT);
                    canvas.drawText("objectif " + weightTarget + " kg", right, targetY - dp(4), paint);
                } catch (NumberFormatException ignored) {
                }
            }

            paint.setColor(teal);
            paint.setStrokeWidth(dp(3));
            paint.setStyle(Paint.Style.STROKE);
            PathBuilder path = new PathBuilder();
            for (int i = 0; i < values.size(); i++) {
                float x = values.size() == 1 ? (left + right) / 2f : left + (right - left) * i / (values.size() - 1f);
                float y = bottom - ((values.get(i) - min) / (max - min)) * (bottom - top);
                path.point(x, y, i == 0);
            }
            canvas.drawPath(path.path, paint);
            paint.setStyle(Paint.Style.FILL);
            for (int i = 0; i < values.size(); i++) {
                float x = values.size() == 1 ? (left + right) / 2f : left + (right - left) * i / (values.size() - 1f);
                float y = bottom - ((values.get(i) - min) / (max - min)) * (bottom - top);
                canvas.drawCircle(x, y, dp(5), paint);
            }
            paint.setColor(muted);
            paint.setTextSize(dp(10));
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(weightChartRange, left, dp(13), paint);
            canvas.drawText(dates.get(0), left, height - dp(8), paint);
            paint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText(dates.get(dates.size() - 1), right, height - dp(8), paint);
            paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(String.format(Locale.FRANCE, "%.1f kg", max), dp(4), top + dp(4), paint);
            canvas.drawText(String.format(Locale.FRANCE, "%.1f kg", min), dp(4), bottom, paint);
        }
    }

    private static class PathBuilder {
        final android.graphics.Path path = new android.graphics.Path();
        void point(float x, float y, boolean first) {
            if (first) path.moveTo(x, y); else path.lineTo(x, y);
        }
    }

    private int dp(int value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private int weightChartMaxPoints() {
        if ("Année".equals(weightChartRange)) return 366;
        if ("Mois".equals(weightChartRange)) return 31;
        return 7;
    }

    private static class Question {
        final String key;
        final String title;
        final String subtitle;
        final String[] options;

        Question(String key, String title, String subtitle, String[] options) {
            this.key = key;
            this.title = title;
            this.subtitle = subtitle;
            this.options = options;
        }
    }

    private static class DayPlan {
        final String day;
        final String focus;
        final ArrayList<String> exercises = new ArrayList<String>();

        DayPlan(String day, String focus) {
            this.day = day;
            this.focus = focus;
        }

        void add(String exercise) {
            exercises.add(exercise);
        }
    }

    private static class ShopItem {
        final String id;
        final String name;
        final int price;
        final int drawable;
        final String category;
        final boolean purchaseOnly;
        final String rarity;
        final int requiredLevel;
        final String avatarTarget;

        ShopItem(String id, String name, int price, int drawable, String category, boolean purchaseOnly, String rarity, int requiredLevel) {
            this(id, name, price, drawable, category, purchaseOnly, rarity, requiredLevel, "Tous");
        }

        ShopItem(String id, String name, int price, int drawable, String category, boolean purchaseOnly, String rarity, int requiredLevel, String avatarTarget) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.drawable = drawable;
            this.category = category;
            this.purchaseOnly = purchaseOnly;
            this.rarity = rarity;
            this.requiredLevel = requiredLevel;
            this.avatarTarget = avatarTarget;
        }
    }

    private static class SessionExercise {
        final String name;
        final String original;
        final String done;
        boolean timed = false;
        EditText repsInput;
        EditText weightInput;
        EditText timeInput;

        SessionExercise(String name, String original, String done) {
            this.name = name;
            this.original = original;
            this.done = done;
        }

        boolean hasResult() {
            if (timed) return timeInput != null && timeInput.getText().toString().trim().length() > 0;
            return repsInput != null && repsInput.getText().toString().trim().length() > 0;
        }

        boolean isModified() {
            if (timed) return hasResult();
            return hasResult() || (weightInput != null && weightInput.getText().toString().trim().length() > 0);
        }
    }

    private class CoinPackView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final int tier;

        CoinPackView(Context context, int tier) {
            super(context);
            this.tier = tier;
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int width = getWidth();
            int height = getHeight();
            if (width == 0 || height == 0) return;

            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(55, 255, 190, 72));
            canvas.drawRoundRect(dp(8), dp(8), width - dp(8), height - dp(8), dp(18), dp(18), paint);

            if (tier >= 3) {
                paint.setColor(Color.rgb(111, 64, 38));
                canvas.drawOval(width * 0.18f, height * 0.36f, width * 0.50f, height * 0.86f, paint);
                canvas.drawOval(width * 0.48f, height * 0.32f, width * 0.84f, height * 0.88f, paint);
                paint.setColor(Color.rgb(255, 214, 94));
                canvas.drawCircle(width * 0.50f, height * 0.40f, dp(9), paint);
                canvas.drawCircle(width * 0.60f, height * 0.44f, dp(8), paint);
            }

            int columns = tier == 0 ? 2 : tier == 1 ? 3 : tier == 2 ? 4 : 5;
            for (int c = 0; c < columns; c++) {
                int coinCount = 2 + tier + (c % 2);
                float x = dp(24) + c * ((width - dp(48)) / Math.max(1, columns - 1f));
                for (int i = 0; i < coinCount; i++) {
                    drawValhallaCoin(canvas, x, height - dp(18) - i * dp(9), dp(15));
                }
            }
        }

        private void drawValhallaCoin(Canvas canvas, float cx, float cy, int radius) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.rgb(255, 205, 73));
            canvas.drawCircle(cx, cy, radius, paint);
            paint.setColor(Color.rgb(130, 82, 24));
            canvas.drawCircle(cx, cy, radius / 3f, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(dp(2));
            paint.setColor(Color.rgb(255, 236, 145));
            canvas.drawCircle(cx, cy, radius - dp(2), paint);
            paint.setColor(Color.rgb(80, 46, 18));
            canvas.drawLine(cx - radius * 0.42f, cy - radius * 0.40f, cx - radius * 0.16f, cy - radius * 0.08f, paint);
            canvas.drawLine(cx + radius * 0.42f, cy - radius * 0.40f, cx + radius * 0.16f, cy - radius * 0.08f, paint);
            paint.setStyle(Paint.Style.FILL);
        }
    }

    private class FitBackgroundView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        FitBackgroundView(Context context) {
            super(context);
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int width = getWidth();
            int height = getHeight();
            paint.setShader(new LinearGradient(0, 0, 0, height, bgTop, bgBottom, Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, width, height, paint);
            paint.setShader(null);

            paint.setColor(Color.argb(34, 27, 214, 196));
            canvas.drawCircle(width * 0.15f, height * 0.10f, dp(150), paint);
            paint.setColor(Color.argb(30, 255, 151, 58));
            canvas.drawCircle(width * 0.90f, height * 0.18f, dp(135), paint);
            paint.setColor(Color.argb(22, 45, 156, 255));
            canvas.drawCircle(width * 0.50f, height * 0.94f, dp(190), paint);

            paint.setStrokeWidth(dp(3));
            paint.setColor(Color.argb(28, 255, 255, 255));
            for (int x = -width; x < width * 2; x += dp(52)) {
                canvas.drawLine(x, height, x + width, 0, paint);
            }
        }
    }

    private class BattleArenaEffectView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final String arena;
        private float phase = 0f;

        BattleArenaEffectView(Context context, String arena) {
            super(context);
            this.arena = arena == null ? "" : arena;
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int width = getWidth();
            int height = getHeight();
            if (width == 0 || height == 0) return;
            phase += 0.025f;

            paint.setStyle(Paint.Style.FILL);
            if (arena.indexOf("Vaisseau") >= 0) {
                paint.setColor(Color.argb(120, 27, 214, 196));
                canvas.drawRect(0, 0, width, height, paint);
                for (int i = 0; i < 4; i++) {
                    float x = dp(34) + i * (width - dp(68)) / 3f;
                    float y = dp(34) + (float) Math.sin(phase * 5 + i) * dp(8);
                    paint.setColor(Color.argb(210, 153, 255, 123));
                    canvas.drawOval(x - dp(13), y - dp(10), x + dp(13), y + dp(12), paint);
                    paint.setColor(Color.argb(230, 8, 20, 24));
                    canvas.drawCircle(x - dp(5), y - dp(1), dp(2), paint);
                    canvas.drawCircle(x + dp(5), y - dp(1), dp(2), paint);
                }
            } else if (arena.indexOf("Olympe") >= 0) {
                paint.setColor(Color.argb(120, 255, 205, 84));
                canvas.drawRect(0, 0, width, height, paint);
                paint.setColor(Color.argb(230, 255, 245, 145));
                paint.setStrokeWidth(dp(3));
                for (int i = 0; i < 5; i++) {
                    float x = (width * (0.12f + i * 0.18f) + phase * 180) % width;
                    canvas.drawLine(x, 0, x + dp(14), height * 0.42f, paint);
                    canvas.drawLine(x + dp(14), height * 0.42f, x - dp(6), height * 0.72f, paint);
                }
            } else if (arena.indexOf("Désert") >= 0) {
                paint.setColor(Color.argb(130, 255, 151, 58));
                canvas.drawRect(0, 0, width, height, paint);
                paint.setColor(Color.argb(100, 255, 235, 130));
                canvas.drawCircle(width * 0.78f, height * 0.38f, dp(22 + (int) (Math.sin(phase * 6) * 5)), paint);
            } else if (arena.indexOf("Montagne") >= 0) {
                paint.setColor(Color.argb(130, 100, 180, 255));
                canvas.drawRect(0, 0, width, height, paint);
                paint.setColor(Color.argb(190, 240, 250, 255));
                for (int i = 0; i < 12; i++) {
                    float x = (i * dp(33) + phase * 220) % Math.max(1, width);
                    float y = (i * dp(19) + phase * 160) % Math.max(1, height);
                    canvas.drawCircle(x, y, dp(2), paint);
                }
            } else if (arena.indexOf("paradis") >= 0) {
                paint.setColor(Color.argb(120, 220, 240, 255));
                canvas.drawRect(0, 0, width, height, paint);
                paint.setColor(Color.argb(115, 255, 245, 180));
                canvas.drawCircle(width * 0.50f, height * 0.50f, dp(28 + (int) (Math.sin(phase * 5) * 8)), paint);
            } else {
                paint.setColor(Color.argb(125, 161, 241, 83));
                canvas.drawRect(0, 0, width, height, paint);
                paint.setColor(Color.argb(120, 255, 255, 255));
                canvas.drawCircle(width * 0.25f, height * 0.42f, dp(13), paint);
                canvas.drawCircle(width * 0.72f, height * 0.54f, dp(18), paint);
            }
            postInvalidateOnAnimation();
        }
    }

    private class AvatarBackdropEffectView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final String mode;
        private float phase = 0f;

        AvatarBackdropEffectView(Context context, String mode) {
            super(context);
            this.mode = mode;
        }

        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            int width = getWidth();
            int height = getHeight();
            if (width == 0 || height == 0) return;
            phase += 0.018f;

            if (mode.indexOf("volcano") >= 0) {
                paint.setColor(Color.argb(135, 255, 95, 32));
                for (int i = 0; i < 26; i++) {
                    float x = ((i * 43 + phase * 420) % Math.max(1, width));
                    float y = height - ((i * 37 + phase * 520) % Math.max(1, height));
                    canvas.drawCircle(x, y, dp(2 + i % 4), paint);
                }
            } else if (mode.indexOf("blizzard") >= 0) {
                paint.setColor(Color.argb(175, 240, 250, 255));
                paint.setStrokeWidth(dp(2));
                for (int i = 0; i < 32; i++) {
                    float x = ((i * 51 + phase * 360) % Math.max(1, width));
                    float y = ((i * 29 + phase * 540) % Math.max(1, height));
                    canvas.drawLine(x, y, x + dp(12), y + dp(4), paint);
                }
            } else if (mode.indexOf("ocean") >= 0) {
                paint.setColor(Color.argb(145, 150, 240, 255));
                for (int i = 0; i < 22; i++) {
                    float x = ((i * 47 + phase * 110) % Math.max(1, width));
                    float y = height - ((i * 55 + phase * 420) % Math.max(1, height));
                    canvas.drawCircle(x, y, dp(3 + i % 5), paint);
                }
            } else if (mode.indexOf("gods") >= 0) {
                paint.setColor(Color.argb(190, 255, 230, 115));
                paint.setStrokeWidth(dp(3));
                for (int i = 0; i < 5; i++) {
                    float x = (width * (0.15f + 0.18f * i) + phase * 240) % width;
                    canvas.drawLine(x, 0, x + dp(28), height * 0.42f, paint);
                }
                paint.setColor(Color.argb(80, 255, 240, 180));
                canvas.drawCircle(width * 0.5f, height * 0.46f, dp(58 + (int) (Math.sin(phase * 8) * 16)), paint);
            } else {
                paint.setColor(Color.argb(90, 27, 214, 196));
                paint.setStrokeWidth(dp(4));
                for (int i = 0; i < 6; i++) {
                    float y = (height * 0.2f) + i * dp(24) + (float) Math.sin(phase * 8 + i) * dp(5);
                    canvas.drawLine(dp(20), y, width - dp(20), y, paint);
                }
            }
            postInvalidateOnAnimation();
        }
    }
}
