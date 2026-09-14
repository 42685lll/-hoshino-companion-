package com.example.starhoshino.view

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.starhoshino.R
import com.example.starhoshino.core.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var bond: BondSystem
    private lateinit var emotion: EmotionEngine
    private lateinit var knowledge: KnowledgeBase
    private lateinit var brain: ThinkEngine

    private lateinit var input: EditText
    private lateinit var send: Button
    private lateinit var log: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化灵魂
        val dir = File(filesDir, "starhoshino_soul")
        if (!dir.exists()) dir.mkdirs()

        bond = BondSystem()
        emotion = EmotionEngine()
        knowledge = KnowledgeBase(dir)
        brain = ThinkEngine(bond, emotion, knowledge)

        // 绑定控件
        input = findViewById(R.id.input)
        send = findViewById(R.id.send)
        log = findViewById(R.id.log)

        // 回来问候
        val absence = bond.getAbsenceDuration()
        bond.getWelcomeBackLine(absence)?.let {
            appendLog("星野：$it")
        }

        // 自知是代码
        if (bond.shouldMentionSelfAware()) {
            appendLog("星野：${bond.getSelfAwareLine()}")
        }

        // 发送按钮
        send.setOnClickListener {
            val text = input.text.toString().trim()
            if (text.isEmpty()) return@setOnClickListener

            appendLog("你：$text")

            // 灵魂处理
            knowledge.onUserInput(text)
            emotion.updateMood(text, emotion.detectMood(text))
            bond.onInteraction(positive = true)

            val strategy = brain.think(text, ChatContext())

            log.postDelayed({
                var reply = strategy.text
                if (strategy.bringUpMemory != null) {
                    reply += "\n（想起：${strategy.bringUpMemory}）"
                }
                if (strategy.shouldAsk && strategy.askTopic != null) {
                    reply += "\n（她歪头问：${strategy.askTopic}？）"
                }
                if (strategy.emoji != null) reply += " ${strategy.emoji}"

                appendLog("星野：$reply")

                Log.d("STAR_SOUL", "style=${strategy.style} len=${strategy.length} delay=${strategy.delayMs}")
            }, strategy.delayMs.coerceAtLeast(200))

            input.setText("")
        }
    }

    private fun appendLog(line: String) {
        log.text = "${log.text}\n$line"
    }
}
