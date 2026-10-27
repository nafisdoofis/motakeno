package bd.hisabfit.tracker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class MainActivity extends Activity {

    private WebView webView;
    private static final String VAULT_FILENAME = "hisabfit_bd_vault.json";
    private static final String PREFS_NAME = "HisabFitBD_LocalPrefs";

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        setContentView(webView);

        // 1. Enable Persistent First-Party Cookies & File-Scheme Cookies inside APK
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptFileSchemeCookies(true);
        cookieManager.setAcceptThirdPartyCookies(webView, true);

        // 2. Enable LocalStorage, IndexedDB, and On-Device DOM Caching
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setMediaPlaybackRequiresUserGesture(false);

        // 3. Attach Native JavaScript Interface (`window.AndroidLocalVault`)
        // This mirrors every meal log, custom recipe, BMI target & cookie directly to Android internal storage
        webView.addJavascriptInterface(new AndroidLocalVaultBridge(this), "AndroidLocalVault");

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                CookieManager.getInstance().flush();
            }
        });

        // Load bundled local web app
        webView.loadUrl("file:///android_asset/public/index.html");
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Flush all WebView cookies and LocalStorage to Android flash storage immediately
        CookieManager.getInstance().flush();
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    public static class AndroidLocalVaultBridge {
        private final Context context;

        public AndroidLocalVaultBridge(Context ctx) {
            this.context = ctx;
        }

        @JavascriptInterface
        public void saveVaultJson(String jsonPayload) {
            if (jsonPayload == null) return;
            try (FileOutputStream fos = context.openFileOutput(VAULT_FILENAME, Context.MODE_PRIVATE)) {
                fos.write(jsonPayload.getBytes(StandardCharsets.UTF_8));
            } catch (Exception ignored) {
            }
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            prefs.edit().putString("vault_backup", jsonPayload).apply();
            CookieManager.getInstance().flush();
        }

        @JavascriptInterface
        public String loadVaultJson() {
            try (FileInputStream fis = context.openFileInput(VAULT_FILENAME);
                 ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[4096];
                int read;
                while ((read = fis.read(buffer)) != -1) {
                    bos.write(buffer, 0, read);
                }
                return bos.toString("UTF-8");
            } catch (Exception e) {
                SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                return prefs.getString("vault_backup", "");
            }
        }
    }
}
