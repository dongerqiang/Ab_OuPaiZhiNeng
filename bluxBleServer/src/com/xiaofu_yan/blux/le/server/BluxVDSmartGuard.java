/*     */ package com.xiaofu_yan.blux.le.server;
/*     */ 
/*     */ import java.util.UUID;
/*     */ 
/*     */ class BluxVDSmartGuard extends BluxVirtualDevice
/*     */ {
/*     */   Delegate delegate;
/*  40 */   static final UUID VD_TYPE_UUID = UUID.fromString("78667579-3E4B-4BF2-9D52-5FFE97D10C2A")
/*  40 */     ;
/*     */   private static final int REG_SMART_GUARD_COMMAND = 17;
/*     */   private static final int REG_SMART_GUARD_PLAY_MUSIC = 18;
/*     */   private static final int REG_SMART_GUARD_OPEN_TRUNK = 19;
/*     */   private static final int REG_SMART_GUARD_ADC = 33;
/*     */   private static final int REG_SMART_GUARD_MILEAGE = 34;
/*     */   private static final int REG_SMART_GUARD_STATE = 50;
/*     */   private static final int REG_SMART_GUARD_SYNC_STATE = 51;
/*     */   private static final int REG_SMART_GUARD_AUTO_ARM = 52;
/*     */   private static final int REG_SMART_GUARD_ALARM = 53;
/*     */   private static final int REG_SMART_GUARD_SPEED_CONFIG = 65;
/*     */   private static final int REG_SMART_GUARD_SHOCK_SENSITIVITY = 66;
/*     */   private static final int REG_SMART_GUARD_SHOCK_LEVEL_TABLE = 67;
/*     */   private static final int REG_SMART_GUARD_ALARM_MUSIC_CONFIG = 68;
/*     */   private static final int REG_SMART_GUARD_ALARM_NOTE_TABLE = 69;
/*     */   private static final int REG_SMART_GUARD_ALARM_MUSIC_TABLE = 70;
/*     */   private static final int VD_SMART_GUARD_COMMAND_ARM = 16;
/*     */   private static final int VD_SMART_GUARD_COMMAND_SILENT_ARM = 17;
/*     */   private static final int VD_SMART_GUARD_COMMAND_DISARM = 18;
/*     */   private static final int VD_SMART_GUARD_STATE_ARMED = 16;
/*     */   private static final int VD_SMART_GUARD_STATE_STOPPED = 17;
/*     */   private static final int VD_SMART_GUARD_STATE_STARTED = 18;
/*     */   private static final int VD_SMART_GUARD_STATE_RUNNING = 19;
/*     */   private static final int ALARM_SENSITIVITY_LEVEL_MAX = 25;//4 change to 25
/*     */ 
/*     */   BluxVDSmartGuard(BluxServiceVirtualDevice service, BluxVirtualDevice.Descriptor desc)
/*     */   {
/*  74 */     super(service, desc);
/*     */   }
/*     */ 
/*     */   protected void terminate()
/*     */   {
/*  79 */     this.delegate = null;
/*  80 */     super.terminate();
/*     */   }
/*     */ 
/*     */   void sendCommand(Command command) {
/*  84 */     byte[] data = new byte[1];
/*     */ 
/*  86 */     if (command == Command.ARM)
/*  87 */       data[0] = 16;
/*  88 */     else if (command == Command.SILENT_ARM)
/*  89 */       data[0] = 17;
/*  90 */     else if (command == Command.DISARM) {
/*  91 */       data[0] = 18;
/*     */     }
/*  93 */     writeRegister(17, data);
/*     */   }
/*     */ 
/*     */   void playMusic(int id) {
/*  97 */     byte[] data = { (byte)id };
/*     */ 
/*  99 */     writeRegister(18, data);
/*     */   }
/*     */ 
/*     */   void openTrunk(int period) {
/* 103 */     byte[] data = new byte[2];
/*     */ 
/* 105 */     s2le((short)period, data, 0);
/*     */ 
/* 107 */     writeRegister(19, data);
/*     */   }
/*     */ 
/*     */   void getADC(byte channel) {
/* 111 */     byte[] data = { channel };
/*     */ 
/* 113 */     readRegister(33, data);
/*     */   }
/*     */ 
/*     */   void readState() {
/* 117 */     readRegister(50, null);
/*     */   }
/*     */ 
/*     */   void writeState(State state, boolean silent) {
/* 121 */     byte[] data = new byte[2];
/*     */ 
/* 123 */     data[0] = 19;
/*     */ 
/* 125 */     if (state == State.STARTED)
/* 126 */       data[0] = 18;
/* 127 */     else if (state == State.STOPPED)
/* 128 */       data[0] = 17;
/* 129 */     else if (state == State.ARMED) {
/* 130 */       data[0] = 16;
/*     */     }
/* 132 */     data[1] = (byte)(silent ? 0 : 1);
/* 133 */     writeRegister(50, data);
/*     */   }
/*     */ 
/*     */   void syncState(boolean autoArm) {
/* 137 */     byte[] data = new byte[1];
/*     */ 
/* 139 */     data[0] = (byte)(autoArm ? 1 : 0);
/* 140 */     writeRegister(51, data);
/*     */   }
/*     */ 
/*     */   void setAutoArm(boolean autoArm) {
/* 144 */     byte[] data = new byte[1];
/*     */ 
/* 146 */     data[0] = (byte)(autoArm ? 1 : 0);
/* 147 */     writeRegister(52, data);
/*     */   }
/*     */ 
/*     */   void readAlarmConfig() {
/* 151 */     readRegister(53, null);
/*     */   }
/*     */ 
/*     */   void writeAlarmConfig(boolean alarmDevice, boolean notifyPhone) {
/* 155 */     byte[] data = new byte[2];
/*     */ 
/* 157 */     data[0] = (byte)(alarmDevice ? 1 : 0);
/* 158 */     data[1] = (byte)(notifyPhone ? 1 : 0);
/* 159 */     writeRegister(53, data);
/*     */   }
/*     */ 
/*     */   void readSpeedConfig() {
/* 163 */     readRegister(65, null);
/*     */   }
/*     */ 
/*     */   void writeSpeedConfig(int monitorPeriod, int hallSensorCounter) {
/* 167 */     byte[] data = new byte[2];
/*     */ 
/* 169 */     hallSensorCounter = hallSensorCounter <= 0 ? 1 : hallSensorCounter;
/* 170 */     hallSensorCounter = hallSensorCounter > 255 ? 255 : hallSensorCounter;
/*     */ 
/* 172 */     monitorPeriod /= 100;
/* 173 */     monitorPeriod = monitorPeriod <= 0 ? 1 : monitorPeriod;
/* 174 */     monitorPeriod = monitorPeriod > 255 ? 255 : monitorPeriod;
/*     */ 
/* 176 */     data[0] = (byte)hallSensorCounter;
/* 177 */     data[1] = (byte)(monitorPeriod / 100);
/* 178 */     writeRegister(65, data);
/*     */   }
/*     */ 
/*     */   void readShockSensitivity() {
/* 182 */     readRegister(66, null);
/*     */   }
/*     */ 
/*     */   void writeShockSensitivity(int level) {
/* 186 */     byte[] data = new byte[1];
/*     */ 
/* 188 */     level = level < 0 ? 0 : level;
/* 189 */     level = level > 4 ? 4 : level;
/*     */ 
/* 191 */     data[0] = (byte)level;
/* 192 */     readRegister(66, data);
/*     */   }
/*     */ 
/*     */   void readShockLevelTable() {
/* 196 */     readRegister(67, null);
/*     */   }
/*     */ 
/*     */   void writeShockLevelTable(byte[] table) {
/* 200 */     writeRegister(52, table);
/*     */   }
/*     */ 
/*     */   void readAlarmMusicConfig() {
/* 204 */     readRegister(68, null);
/*     */   }
/*     */ 
/*     */   void readAlarmMusic(int musicID) {
/* 208 */     byte[] data = new byte[1];
/*     */ 
/* 210 */     data[0] = (byte)musicID;
/*     */ 
/* 212 */     readRegister(70, data);
/*     */   }
/*     */ 
/*     */   void writeAlarmMusic(int musicID, byte[] data) {
/* 216 */     byte[] d = new byte[data.length + 1];
/*     */ 
/* 218 */     d[0] = (byte)musicID;
/* 219 */     arrayCopyToArray(d, 1, data, 0, data.length);
/*     */ 
/* 221 */     writeRegister(70, d);
/*     */   }
/*     */ 
/*     */   void readAlarmNote(int noteID) {
/* 225 */     byte[] data = new byte[2];
/*     */ 
/* 227 */     data[0] = (byte)noteID;
/* 228 */     data[1] = 1;
/*     */ 
/* 230 */     readRegister(69, data);
/*     */   }
/*     */ 
/*     */   void writeAlarmNote(int noteID, byte[] data) {
/* 234 */     byte[] d = new byte[data.length + 1];
/*     */ 
/* 236 */     d[0] = (byte)noteID;
/* 237 */     arrayCopyToArray(d, 1, data, 0, data.length);
/*     */ 
/* 239 */     writeRegister(69, d);
/*     */   }
/*     */ 
/*     */   void writeMileage(long pulses)
/*     */   {
/* 247 */     byte[] data = new byte[4];
/*     */ 
/* 249 */     l2le((int)pulses, data, 0);
/*     */ 
/* 251 */     writeRegister(34, data);
/*     */   }
/*     */ 
/*     */   static boolean isKindOf(UUID uuidType)
/*     */   {
/* 256 */     return VD_TYPE_UUID.compareTo(uuidType) == 0;
/*     */   }
/*     */ 
/*     */   protected void didReadRegister(Object id, int register, boolean success, byte[] data)
/*     */   {
/* 261 */     if ((this.delegate == null) || (!success) || (data == null)) {
/* 262 */       return;
/*     */     }
/* 264 */     switch (register) {
/*     */     case 33:
/* 266 */       if (data.length < 3) break;
/* 267 */       short mv = le2s(data, 1);
/* 268 */       this.delegate.updateADC(mv);
/* 269 */       break;
/*     */     case 50:
/* 273 */       if (data.length == 0) break;
/* 274 */       State state = State.UNKNOWN;
/* 275 */       if (data[0] == 16)
/* 276 */         state = State.ARMED;
/* 277 */       else if (data[0] == 17)
/* 278 */         state = State.STOPPED;
/* 279 */       else if (data[0] == 18)
/* 280 */         state = State.STARTED;
/* 281 */       else if (data[0] == 19)
/* 282 */         state = State.RUNNING;
/* 283 */       this.delegate.updateState(state);
/* 284 */       break;
/*     */     case 53:
/* 288 */       if (data.length != 2) break;
/* 289 */       this.delegate.updateAlarmConfig(data[0] != 0, data[1] != 0); break;
/*     */     case 65:
/* 294 */       if (data.length != 2) break;
/* 295 */       int count = data[0] & 0xFF;
/* 296 */       int period = data[1] & 0xFF;
/* 297 */       period *= 100;
/* 298 */       this.delegate.updateSpeedConfig(period, count);
/* 299 */       break;
/*     */     case 66:
/* 303 */       if (data.length == 0) break;
/* 304 */       this.delegate.updateShockSensitivity(le2uc(data, 0)); break;
/*     */     case 67:
/* 309 */       if (data.length == 0) break;
/* 310 */       this.delegate.updateShockLevelTable(data); break;
/*     */     case 68:
/* 315 */       if (data.length != 4) break;
/* 316 */       this.delegate.updateAlarmMusicConfig(le2uc(data, 0), le2uc(data, 1), le2uc(data, 2), le2uc(data, 3)); break;
/*     */     case 69:
/* 321 */       if (data.length <= 1) break;
/* 322 */       byte[] d = new byte[data.length - 1];
/* 323 */       arrayCopyToArray(d, 0, data, 1, data.length - 1);
/* 324 */       this.delegate.updateAlarmNote(le2uc(data, 0), d);
/* 325 */       break;
/*     */     case 70:
/* 329 */       if (data.length <= 1) break;
/* 330 */       byte[] d1 = new byte[data.length - 1];
/* 331 */       arrayCopyToArray(d1, 0, data, 1, data.length - 1);
/* 332 */       this.delegate.updateAlarmMusic(le2uc(data, 0), d1);
/* 333 */       break;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected void irq(byte[] data)
/*     */   {
/* 345 */     if ((data.length > 0) && (this.delegate != null)) {
/* 346 */       int register = (short)data[0] & 0xFF;
/* 347 */       if (register == 53) {
/* 348 */         this.delegate.alarm(data[1]);
/*     */       }
/* 350 */       else if (register == 50) {
/* 351 */         State state = State.UNKNOWN;
/* 352 */         if (data[1] == 16)
/* 353 */           state = State.ARMED;
/* 354 */         else if (data[1] == 17)
/* 355 */           state = State.STOPPED;
/* 356 */         else if (data[1] == 18)
/* 357 */           state = State.STARTED;
/* 358 */         else if (data[1] == 19)
/* 359 */           state = State.RUNNING;
/* 360 */         this.delegate.updateState(state);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   static class Delegate
/*     */   {
/*     */     protected void alarm(byte type)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updateADC(short mv)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updateState(BluxVDSmartGuard.State state)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updateAlarmConfig(boolean alarmDevice, boolean notifyPhone)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updateSpeedConfig(int monitorPeriod, int hallSensorCounter)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updateShockSensitivity(int level)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updateShockLevelTable(byte[] table)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updateAlarmMusicConfig(int musicCount, int musicSize, int noteCount, int noteSize)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updateAlarmNote(int noteID, byte[] data)
/*     */     {
/*     */     }
/*     */ 
/*     */     protected void updateAlarmMusic(int musicID, byte[] data)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   static enum Command
/*     */   {
/*  17 */     ARM, 
/*  18 */     SILENT_ARM, 
/*  19 */     DISARM;
/*     */   }
/*     */ 
/*     */   static enum State
/*     */   {
/*   9 */     UNKNOWN, 
/*  10 */     ARMED, 
/*  11 */     STOPPED, 
/*  12 */     STARTED, 
/*  13 */     RUNNING;
/*     */   }
/*     */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxVDSmartGuard
 * JD-Core Version:    0.6.0
 */