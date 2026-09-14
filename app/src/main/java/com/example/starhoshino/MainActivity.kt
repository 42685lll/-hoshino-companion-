package com.example.starhoshino

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)
        setContentView(webView)

        setupWebView()
    }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface", "AddJavascriptInterface")
    private fun setupWebView() {
        val ws: WebSettings = webView.settings
        ws.javaScriptEnabled = true
        ws.domStorageEnabled = true
        ws.allowFileAccess = true
        ws.allowContentAccess = true
        ws.mediaPlaybackRequiresUserGesture = false
        ws.cacheMode = WebSettings.LOAD_DEFAULT

        webView.webViewClient = WebViewClient()
        webView.webChromeClient = WebChromeClient()

        val bridge = NativeBridge(this)
        webView.addJavascriptInterface(bridge, "NativeBridge")
        webView.addJavascriptInterface(bridge, "AndroidBridge")

        webView.loadUrl("file:///android_asset/core/index.html")
    }

    class NativeBridge(private val context: Context) {

        @JavascriptInterface
        fun ping(): String {
            Log.i("STARHOSHINO", "ping")
            return "pong"
        }

        @JavascriptInterface
        fun log(msg: String) {
            Log.i("STARHOSHINO_JS", msg)
        }

        @JavascriptInterface
        fun ready(): String = "ready"

        @JavascriptInterface
        fun getPrompt(): String {
            return try {
                context.assets.open("core/prompt_hoshino.txt")
                    .bufferedReader(Charsets.UTF_8)
                    .use { it.readText() }
            } catch (e: Exception) {
                Log.e("STARHOSHINO", "getPrompt failed", e)
                ""
            }
        }

        @JavascriptInterface
        fun exec(sql: String?): String? {
            Log.d("STARHOSHINO_DB", "exec: $sql")
            return null
        }

        @JavascriptInterface
        fun query(sql: String?): String {
            Log.d("STARHOSHINO_DB", "query: $sql")
            return "[]"
        }

        @JavascriptInterface
        fun llm(json: String?): String {
            Log.d("STARHOSHINO_LLM", "llm called")
            return "（星野暂时无法回复）"
        }

        @JavascriptInterface
        fun tts(text: String?) {
            Log.d("STARHOSHINO_TTS", "tts: $text")
        }

        @JavascriptInterface
        fun vadState(): String = "idle"

        @JavascriptInterface
        fun fileRead(name: String?): String {
            Log.d("STARHOSHINO_FILE", "read: $name")
            return ""
        }

        @JavascriptInterface
        fun fileWrite(name: String?, text: String?) {
            Log.d("STARHOSHINO_FILE", "write: $name")
        }

        @JavascriptInterface
        fun setWave(mode: String?) {
            Log.d("STARHOSHINO_WAVE", "setWave: $mode")
        }

        @JavascriptInterface
        fun setWaveAmp(amps: String?) {
            Log.d("STARHOSHINO_WAVE", "setWaveAmp")
        }

        @JavascriptInterface
        fun saveWarmLayer(json: String?) {
            Log.d("STARHOSHINO_WARM", "saveWarmLayer")
        }

        @JavascriptInterface
        fun exportChatJson(json: String?) {
            Log.d("STARHOSHINO_EXPORT", "exportChatJson")
        }

        @JavascriptInterface
        fun onCoreReady(status: String?) {
            Log.i("STARHOSHINO", "Core ready: $status")
        }
    }

    override fun onBackPressed() {
        if (::webView.isInitialized && webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
    }
}
