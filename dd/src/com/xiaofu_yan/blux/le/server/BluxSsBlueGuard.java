/*     */ package com.xiaofu_yan.blux.le.server;
/*     */ 
/*     */ import android.os.Bundle;
/*     */ import android.os.Message;
/*     */ import android.os.Messenger;
/*     */ import java.util.UUID;
/*     */ 
/*     */ class BluxSsBlueGuard extends BluxSsProxy
/*     */ {
/*     */   private static final int CMD_CONNECT = 1;
/*     */   private static final int CMD_CANCEL_CONNECT = 2;
/*     */   private static final int CMD_PLAY_SOUND = 3;
/*     */   private static final int CMD_OPEN_TRUNK = 4;
/*     */   private static final int CMD_SET_NAME = 5;
/*     */   private static final int CMD_SET_STATE = 6;
/*     */   private static final int CMD_SET_FENCE_RANGE = 7;
/*     */   private static final int CMD_SET_ALARM_CONFIG = 8;
/*     */   private static final int CMD_SET_SHOCK_SENSITIVITY = 9;
/*     */   private static final int CMD_SET_MILEAGE = 10;
/*     */   private static final int CMD_GET_ALARM_CONFIG = 12;
/*     */   private static final int CMD_GET_SERIAL_NUMBER = 13;
/*     */   private static final int CMD_GET_SHOCK_SENSITIVITY = 14;
/*     */   private static final int CMD_GET_PAIR_PASS_KEY = 18;
/*     */   private static final int CMD_SET_CONNECTION_KEY = 21;
/*     */   private static final int CMD_PAIR = 22;
/*     */   private static final int CMD_CANCEL_PAIR = 23;
/*     */   private static final int CMD_GET_ACCOUNT_MANAGER = 24;
/*     */   private static final int CMD_WRITE_VIRTUAL_DEVICE = 100;
/*     */   private static final int RSP_ALARM = 1;
/*     */   private static final int RSP_STATE = 2;
/*     */   private static final int RSP_NAME = 3;
/*     */   private static final int RSP_CURRENT_RANGE = 4;
/*     */   private static final int RSP_REPORT_DATA = 5;
/*     */   private static final int RSP_CONNECTED = 11;
/*     */   private static final int RSP_DISCONNECTED = 12;
/*     */   private static final int RSP_ALARM_CONFIG = 14;
/*     */   private static final int RSP_PAIR = 15;
/*     */   private static final int RSP_SERIAL_NUMBER = 16;
/*     */   private static final int RSP_SHOCK_SENSITIVITY = 17;
/*     */   private static final int RSP_PAIR_PASS_KEY = 18;
/*     */   private static final int RSP_ACCOUNT_MANAGER = 19;
/*     */   private static final int STATE_UNKNOWN = 0;
/*     */   private static final int STATE_ARMED = 1;
/*     */   private static final int STATE_STARTED = 2;
/*     */   private static final int STATE_STOPPED = 3;
/*     */   private static final int STATE_RUNNING = 4;
/*     */   private static final int DISCONNECT_REASON_CLOSED = 0;
/*     */   private static final int DISCONNECT_REASON_PERMISSION = 1;
/*     */   private static final int DISCONNECT_REASON_KEY = 2;
/*     */   private static final int DISCONNECT_REASON_LINK_LOST = 3;
/*     */   private static final int DISCONNECT_REASON_UNKNOWN = 4;
/*     */   private static final int PAIR_RESULT_SUCCESS = 0;
/*     */   private static final int PAIR_RESULT_ERROR = 1;
/*     */   private static final int PAIR_RESULT_ERROR_PERMISSION = 2;
/*     */   private static final int PAIR_RESULT_ERROR_KEY = 3;
/*     */   private static final int ALARM_TYPE_LOW = 0;
/*     */   private static final int ALARM_TYPE_HIGH = 1;
/*     */   private static final int ALARM_TYPE_POWER_LEFT_ON = 2;
/*     */   private BluxBlueGuard mBlueGuard;
/*     */   private BlueGuardDelegate mBlueGuardDelegate;
/*     */ 
/*     */   BluxSsBlueGuard(BluxBlueGuard blueGuard, UUID processId, Messenger reply)
/*     */   {
/*  15 */     super(processId, reply);
/*  16 */     this.mBlueGuard = blueGuard;
/*  17 */     this.mBlueGuardDelegate = new BlueGuardDelegate(null);
/*  18 */     this.mBlueGuard.registerDelegate(this.mBlueGuardDelegate);
/*     */   }
/*     */ 
/*     */   protected void terminate()
/*     */   {
/*  23 */     if (this.mBlueGuard != null) {
/*  24 */       this.mBlueGuard.unregisterDelegate(this.mBlueGuardDelegate);
/*  25 */       this.mBlueGuard = null;
/*  26 */       this.mBlueGuardDelegate = null;
/*     */     }
/*  28 */     super.terminate();
/*     */   }
/*     */ 
/*     */   void setStateData(Bundle data) {
/*  32 */     data.putString("identifier", this.mBlueGuard.identifier());
/*  33 */     data.putString("name", this.mBlueGuard.name());
/*  34 */     data.putBoolean("connected", this.mBlueGuard.connected());
/*  35 */     data.putInt("fence_range", this.mBlueGuard.fenceRangePercent());
/*  36 */     data.putInt("state", stateToInt(this.mBlueGuard.state()));
/*     */   }
/*     */ 
/*     */   private int stateToInt(BluxBlueGuard.State state)
/*     */   {
/* 104 */     int s = 0;
/* 105 */     if (state == BluxBlueGuard.State.ARMED)
/* 106 */       s = 1;
/* 107 */     else if (state == BluxBlueGuard.State.STOPPED)
/* 108 */       s = 3;
/* 109 */     else if (state == BluxBlueGuard.State.STARTED)
/* 110 */       s = 2;
/* 111 */     else if (state == BluxBlueGuard.State.RUNNING)
/* 112 */       s = 4;
/* 113 */     return s;
/*     */   }
/*     */ 
/*     */   protected boolean handleMessage(Message cmd)
/*     */   {
/* 119 */     if ((super.handleMessage(cmd)) || (this.mBlueGuard == null)) {
/* 120 */       return true;
/*     */     }
/* 122 */     switch (cmd.what) {
/*     */     case 24:
/* 124 */       UUID processId = BluxSsProxy.getMessageProcess(cmd);
/* 125 */       BluxAccountManager bam = this.mBlueGuard.getAccountManager();
/* 126 */       if ((bam == null) || (processId == null) || (cmd.replyTo == null))
/*     */         break;
/* 128 */       Bundle data = new Bundle();
/* 129 */       BluxSsAccountManager ssbam = new BluxSsAccountManager(bam, processId, cmd.replyTo);
/* 130 */       data.putString("server_id", ssbam.uuid().toString());
/* 131 */       data.putString("client_id", ssbam.clientId().toString());
/* 132 */       ssbam.setStateData(data);
/* 133 */       notifyClient(19, data);
/* 134 */       break;
/*     */     case 1:
/* 138 */       this.mBlueGuard.connect();
/* 139 */       break;
/*     */     case 21:
/* 142 */       String key = cmd.getData().getString("key");
/* 143 */       if (key == null) break;
/* 144 */       this.mBlueGuard.setKey(key); break;
/*     */     case 22:
/* 149 */       int pass = cmd.getData().getInt("pass");
/* 150 */       this.mBlueGuard.passPair(pass);
/* 151 */       break;
/*     */     case 23:
/* 154 */       this.mBlueGuard.cancelPair();
/* 155 */       break;
/*     */     case 2:
/* 158 */       this.mBlueGuard.cancelConnect();
/* 159 */       break;
/*     */     case 3:
/* 162 */       int id = cmd.getData().getInt("id");
/* 163 */       this.mBlueGuard.playSound(id);
/* 164 */       break;
/*     */     case 4:
/* 167 */       this.mBlueGuard.openTrunk();
/* 168 */       break;
/*     */     case 5:
/* 171 */       String name = cmd.getData().getString("name");
/* 172 */       if (name == null) break;
/* 173 */       this.mBlueGuard.setName(name); break;
/*     */     case 7:
/* 178 */       int range = cmd.getData().getInt("range");
/* 179 */       this.mBlueGuard.setFenceRangePercent(range);
/* 180 */       break;
/*     */     case 8:
/* 183 */       boolean alarmDevice = cmd.getData().getBoolean("alarm_device");
/* 184 */       boolean notifyPhone = cmd.getData().getBoolean("notify_phone");
/* 185 */       this.mBlueGuard.setAlarmConfig(alarmDevice, notifyPhone);
/* 186 */       break;
/*     */     case 9:
/* 189 */       int level = cmd.getData().getInt("level");
/* 190 */       this.mBlueGuard.setShockSensitivity(level);
/* 191 */       break;
/*     */     case 10:
/* 194 */       long pulses = cmd.getData().getLong("pulses");
/* 195 */       this.mBlueGuard.setMileage(pulses);
/* 196 */       break;
/*     */     case 18:
/* 199 */       this.mBlueGuard.getPairPasskey();
/* 200 */       break;
/*     */     case 12:
/* 203 */       this.mBlueGuard.getAlarmConfig();
/* 204 */       break;
/*     */     case 13:
/* 207 */       this.mBlueGuard.getSerialNumber();
/* 208 */       break;
/*     */     case 14:
/* 211 */       this.mBlueGuard.getShockSensitivity();
/* 212 */       break;
/*     */     case 6:
/* 215 */       int state = cmd.getData().getInt("state");
/* 216 */       if (state == 1) {
/* 217 */         this.mBlueGuard.setState(BluxBlueGuard.State.ARMED);
/* 218 */       } else if (state == 2) {
/* 219 */         this.mBlueGuard.setState(BluxBlueGuard.State.STARTED); } else {
/* 220 */         if (state != 3) break;
/* 221 */         this.mBlueGuard.setState(BluxBlueGuard.State.STOPPED); } break;
/*     */     case 100:
/* 225 */       String sDevice = cmd.getData().getString("device");
/* 226 */       int register = cmd.getData().getInt("register");
/* 227 */       byte[] data = cmd.getData().getByteArray("data");
/*     */       try {
/* 229 */         UUID idDevice = UUID.fromString(sDevice);
/* 230 */         if (data != null) {
/* 231 */           this.mBlueGuard.writeVirtualDevice(idDevice, register, data);
/*     */         }
/*     */       }
/*     */       catch (Exception localException)
/*     */       {
/*     */       }
/*     */ 
/*     */     default:
/* 239 */       return false;
/*     */     }
/* 241 */     return true;
/*     */   }
/*     */ 
/*     */   private class BlueGuardDelegate extends BluxBlueGuard.Delegate {
/*     */     private BlueGuardDelegate() {
/*     */     }
/*     */ 
/*     */     protected void blueGuardConnected(BluxBlueGuard bg) {
/* 249 */       BluxSsBlueGuard.this.notifyClient(11, null);
/*     */     }
/*     */ 
/*     */     protected void blueGuardDisconnected(BluxBlueGuard bg, BluxDevice.DisconnectReason reason)
/*     */     {
/* 254 */       Bundle data = new Bundle();
/*     */ 
/* 256 */       if (reason == BluxDevice.DisconnectReason.KEY)
/* 257 */         data.putInt("reason", 2);
/* 258 */       else if (reason == BluxDevice.DisconnectReason.PERMISSION)
/* 259 */         data.putInt("reason", 1);
/* 260 */       else if (reason == BluxDevice.DisconnectReason.CLOSED)
/* 261 */         data.putInt("reason", 0);
/* 262 */       else if (reason == BluxDevice.DisconnectReason.LINKLOST)
/* 263 */         data.putInt("reason", 3);
/*     */       else {
/* 265 */         data.putInt("reason", 4);
/*     */       }
/* 267 */       BluxSsBlueGuard.this.notifyClient(12, data);
/*     */     }
/*     */ 
/*     */     protected void blueGuardCurrentRange(BluxBlueGuard bg, int percentRange)
/*     */     {
/* 272 */       Bundle data = new Bundle();
/* 273 */       data.putInt("range", percentRange);
/*     */ 
/* 275 */       BluxSsBlueGuard.this.notifyClient(4, data);
/*     */     }
/*     */ 
/*     */     protected void blueGuardPairPasskey(String passkey)
/*     */     {
/* 280 */       Bundle data = new Bundle();
/* 281 */       data.putString("passkey", passkey);
/*     */ 
/* 283 */       BluxSsBlueGuard.this.notifyClient(18, data);
/*     */     }
/*     */ 
/*     */     protected void blueGuardAlarm(BluxBlueGuard bg, BluxBlueGuard.AlarmType type)
/*     */     {
/* 288 */       Bundle data = new Bundle();
/* 289 */       int t = 0;
/* 290 */       if (type == BluxBlueGuard.AlarmType.HIGH_ALARM)
/* 291 */         t = 1;
/* 292 */       else if (type == BluxBlueGuard.AlarmType.POWER_LEFT_ON)
/* 293 */         t = 2;
/* 294 */       data.putInt("type", t);
/* 295 */       BluxSsBlueGuard.this.notifyClient(1, data);
/*     */     }
/*     */ 
/*     */     protected void blueGuardAlarmConfig(BluxBlueGuard bg, boolean deviceAlarm, boolean notifyPhone)
/*     */     {
/* 301 */       Bundle data = new Bundle();
/* 302 */       data.putBoolean("device_alarm", deviceAlarm);
/* 303 */       data.putBoolean("notify_phone", notifyPhone);
/*     */ 
/* 305 */       BluxSsBlueGuard.this.notifyClient(14, data);
/*     */     }
/*     */ 
/*     */     protected void blueGuardState(BluxBlueGuard bg, BluxBlueGuard.State state)
/*     */     {
/* 310 */       Bundle data = new Bundle();
/* 311 */       data.putInt("state", BluxSsBlueGuard.this.stateToInt(state));
/* 312 */       BluxSsBlueGuard.this.notifyClient(2, data);
/*     */     }
/*     */ 
/*     */     protected void blueGuardName(BluxBlueGuard bg, String name)
/*     */     {
/* 317 */       Bundle data = new Bundle();
/* 318 */       data.putString("name", name);
/*     */ 
/* 320 */       BluxSsBlueGuard.this.notifyClient(3, data);
/*     */     }
/*     */ 
/*     */     protected void blueGuardShockLevel(BluxBlueGuard bg, int level)
/*     */     {
/* 325 */       Bundle data = new Bundle();
/* 326 */       data.putInt("level", level);
/*     */ 
/* 328 */       BluxSsBlueGuard.this.notifyClient(17, data);
/*     */     }
/*     */ 
/*     */     protected void blueGuardSerialNumber(BluxBlueGuard bg, String sn)
/*     */     {
/* 333 */       Bundle data = new Bundle();
/* 334 */       data.putString("serial_number", sn);
/*     */ 
/* 336 */       BluxSsBlueGuard.this.notifyClient(16, data);
/*     */     }
/*     */ 
/*     */     protected void blueGuardUpdateData(BluxBlueGuard bg, byte[] data)
/*     */     {
/* 341 */       Bundle d = new Bundle();
/* 342 */       d.putByteArray("data", data);
/*     */ 
/* 344 */       BluxSsBlueGuard.this.notifyClient(5, d);
/*     */     }
/*     */ 
/*     */     protected void blueGuardPairResult(BluxBlueGuard bg, BluxDevice.PairResult result, String key)
/*     */     {
/* 349 */       Bundle d = new Bundle();
/* 350 */       if (key != null)
/* 351 */         d.putString("key", key);
/*     */       int r;
/*     */       int r;
/* 354 */       if (result == BluxDevice.PairResult.SUCCESS) {
/* 355 */         r = 0;
/*     */       }
/*     */       else
/*     */       {
/*     */         int r;
/* 356 */         if (result == BluxDevice.PairResult.ERROR_KEY) {
/* 357 */           r = 3;
/*     */         }
/*     */         else
/*     */         {
/*     */           int r;
/* 358 */           if (result == BluxDevice.PairResult.ERROR_PERMISSION)
/* 359 */             r = 2;
/*     */           else
/* 361 */             r = 1; 
/*     */         }
/*     */       }
/* 362 */       d.putInt("result", r);
/* 363 */       BluxSsBlueGuard.this.notifyClient(15, d);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxSsBlueGuard
 * JD-Core Version:    0.6.0
 */