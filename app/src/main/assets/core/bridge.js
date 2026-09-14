(function (global) {
  'use strict';

  let A = global.AndroidBridge || global.NativeBridge || null;

  const NativeSQL = A ? {
    exec: function (sql) {
      try { return A.exec(sql); } catch (e) { return null; }
    },
    query: function (sql) {
      try { return A.query(sql); } catch (e) { return '[]'; }
    }
  } : null;

  const FileIO = A ? {
    read: function (name) {
      try { return A.fileRead(name); } catch (e) { return ''; }
    },
    write: function (name, text) {
      try { A.fileWrite(name, text); } catch (e) {}
    }
  } : null;

  const TTS = A ? {
    speak: function (text) {
      try { A.tts(text); } catch (e) {}
    }
  } : null;

  const VAD = A ? {
    state: function () {
      try { return A.vadState(); } catch (e) { return 'idle'; }
    }
  } : { state: function () { return 'idle'; } };

  const Wave = A ? {
    setMode: function (mode) {
      try { A.setWave(mode); } catch (e) {}
    },
    setAmp: function (amps) {
      try { A.setWaveAmp(amps); } catch (e) {}
    }
  } : null;

  // 暴露到全局
  global.HoshinoBridge = {
    sql: NativeSQL,
    file: FileIO,
    tts: TTS,
    vad: VAD,
    wave: Wave,
    get raw() { return A; }
  };

  // 通知原生核心已就绪
  if (A && A.onCoreReady) {
    try { A.onCoreReady('bridge_ready'); } catch (e) {}
  }

})(window || this);
