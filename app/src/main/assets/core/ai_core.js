(function (global) {
  'use strict';

  let A = global.AndroidBridge || global.NativeBridge || null;

  // ==================== 工具函数 ====================
  function tokenize(t) {
    const text = String(t || '');
    const chinese = text.match(/[\u4e00-\u9fa5]/g) || [];
    const english = text.replace(/[\u4e00-\u9fa5]/g, ' ')
      .split(/\s+/).filter(function (w) { return w.length >= 2; });
    return chinese.concat(english);
  }

  function stripEmoTag(text) {
    return String(text).replace(/\[\[EMO:[^\]]+\]\]/g, '').trim();
  }

  // ==================== 记忆 ====================
  const memory = {
    hotLayer: { sessionStartTime: Date.now(), dialogues: [] },
    warmLayer: { userProfile: [], sessionSummaries: [], deletedTimeRanges: [] },
    coldLayer: { fullRecords: [] }
  };

  // ==================== Prompt ====================
  let cachedPrompt = '';

  function loadPrompt() {
    if (global.HOSHINO_PROMPT) {
      cachedPrompt = global.HOSHINO_PROMPT;
      return;
    }
    if (A && A.getPrompt) {
      try { cachedPrompt = A.getPrompt(); } catch (e) {}
    }
    if (!cachedPrompt) {
      fetch('prompt_hoshino.txt')
        .then(function (r) { return r.text(); })
        .then(function (t) { cachedPrompt = t; })
        .catch(function () {});
    }
  }

  function buildPrompt(userInput) {
    loadPrompt();
    let p = cachedPrompt || '你是星野，阿拜多斯高中的学生会长。';
    const profile = memory.warmLayer.userProfile
      .map(function (x) { return x.content; })
      .join('\n');
    if (profile) p += '\n\n【关于前辈】\n' + profile;
    return p + '\n\n前辈：' + userInput + '\n星野：';
  }

  // ==================== 对话 ====================
  function chat(userInput) {
    memory.hotLayer.dialogues.push({ role: 'user', text: userInput, time: Date.now() });

    const prompt = buildPrompt(userInput);

    if (A && A.llm) {
      try {
        const reply = A.llm(JSON.stringify({ prompt: prompt }));
        memory.hotLayer.dialogues.push({ role: 'hoshino', text: reply, time: Date.now() });
        return reply;
      } catch (e) {
        return '（星野思考中…）';
      }
    }

    return '（星野暂时无法回复，LLM 未接入）';
  }

  // ==================== 初始化 ====================
  function init() {
    loadPrompt();
    if (A && A.onCoreReady) {
      try { A.onCoreReady('ai_core_ready'); } catch (e) {}
    }
  }

  // 暴露到全局
  global.HoshinoAI = {
    chat: chat,
    memory: memory,
    init: init,
    buildPrompt: buildPrompt
  };

  // 自动初始化
  if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', init);
  } else {
    init();
  }

})(window || this);
