package com.example.academy1;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ProgressBar progressBar;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);

        // Enable JavaScript (optional)
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        // Clear cache and cookies when the app starts
        clearCacheAndCookies();

        // Load Google in the WebView
        webView.loadUrl("https://academy.code5fixer.com/");

        // Open links in the WebView, not in the default browser
        webView.setWebViewClient(new CustomWebViewClient());
    }

    // Handle the back button press
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack(); // Navigate back in WebView's history
                return true;
            } else {
                if (doubleBackToExitPressedOnce) {
                    super.onKeyDown(keyCode, event);
                    return true;
                }

                this.doubleBackToExitPressedOnce = true;
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 1000); // Set a 2-second delay for the double-press

                // Show an alert message
                new AlertDialog.Builder(this)
                        .setMessage("Please press one more time to exit.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                            }
                        })
                        .show();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    // Custom WebViewClient to handle special links
    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("whatsapp://send")) {
                openWhatsApp(url);
                return true;  // Indicates that the WebView should not load the URL
            } else if (url.startsWith("fb://")) {
                openFacebookInBrowser(url);
                return true;
            } else if (url.startsWith("mailto:")) {
                openMailTo(url);
                return true;
            }
            else if (url.startsWith("tel:")) {
                openPhoneDialer(url);
                return true;
            }

            // Load other URLs in the WebView
            return false;
        }
        // Custom WebViewClient to handle WhatsApp links

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // Show the progress bar when a new page starts loading
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // Hide the progress bar when the page finishes loading
            progressBar.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            // Handle other errors (e.g., display an error message)
            showErrorDialog("Error Loading Page", "There was an error loading the page. Please try again.");
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            // Handle URL schemes for Android N and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                String url = request.getUrl().toString();
                if (url.startsWith("mailto:")) {
                    openMailTo(url);
                    return true;
                }
            }

            return super.shouldOverrideUrlLoading(view, request);
        }
    }

    private void openWhatsApp(String url) {
        try {
            // Extract the phone number from the WhatsApp URL
            Uri uri = Uri.parse(url);
            String phoneNumber = uri.getQueryParameter("phone");
//            String phoneNumber = url.substring(url.lastIndexOf("/") + 1);

            // Create an Intent to open WhatsApp with the phone number
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber));
//            intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle any exceptions that may occur while opening WhatsApp
        }
    }

    private void openFacebookInBrowser(String url) {
        try {
            // Extract the Facebook page ID from the URL
            String pageId = url.substring(url.lastIndexOf("/") + 1);

            // Create an Intent to open the Facebook page in the browser
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://www.facebook.com/" + pageId));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle any exceptions that may occur while opening Facebook
        }
    }

    private void openMailTo(String url) {
        try {
            // Parse the mailto URL
            MailTo mt = MailTo.parse(url);
            String emailAddress = mt.getTo();

            // Create an Intent to open the default email client
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:" + emailAddress));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle any exceptions that may occur while opening the email client
        }
    }
    private void openPhoneDialer(String url) {
        try {
            // Extract the phone number from the tel URL
            Uri uri = Uri.parse(url);
            String phoneNumber = uri.getSchemeSpecificPart();

            // Create an Intent to open the phone dialer
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle any exceptions that may occur while opening the phone dialer
        }
    }
    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // Handle OK button click if needed
                    }
                })
                .show();
    }

    private void clearCacheAndCookies() {
        // Clear cache
        webView.clearCache(true);

        // Clear cookies
        CookieSyncManager.createInstance(this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookies(null);
        cookieManager.flush();
    }
}