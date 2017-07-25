/*     */ package com.xiaofu_yan.blux.le.server;
/*     */ 
/*     */ import android.os.Bundle;
/*     */ import android.os.Message;
/*     */ import android.os.Messenger;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map.Entry;
/*     */ import java.util.Set;
/*     */ import java.util.UUID;
/*     */ 
/*     */ class BluxSsProxy extends BluxObject
/*     */ {
/*     */   private static final int CMD_BROADCAST_MAX = -1;
/*     */   protected static final int CMD_BROADCAST_TERMINATE = -1;
/*     */   private static final int CMD_BROADCAST_PROCESS_DETACHED = -100;
/*     */   private static final int CMD_BROADCAST_MIN = -200;
/*     */   static Manager mManager;
/* 111 */   private UUID mClientId = UUID.randomUUID();
/*     */   private UUID mProcessId;
/*     */   private Messenger mReply;
/*     */   private boolean mTerminated;
/*     */ 
/*     */   static Manager manager()
/*     */   {
/*  80 */     if (mManager == null)
/*  81 */       mManager = new Manager();
/*  82 */     return mManager;
/*     */   }
/*     */ 
/*     */   static UUID getMessageProcess(Message msg)
/*     */   {
/*  88 */     String s = msg.getData().getString("process_uuid");
/*  89 */     if (s == null)
/*  90 */       return null;
/*  91 */     return UUID.fromString(s);
/*     */   }
/*     */ 
/*     */   static UUID getMessageReceiver(Message msg) {
/*  95 */     String s = msg.getData().getString("to");
/*  96 */     if (s == null)
/*  97 */       return null;
/*  98 */     return UUID.fromString(s);
/*     */   }
/*     */ 
/*     */   static UUID getMessageSender(Message msg) {
/* 102 */     String s = msg.getData().getString("from");
/* 103 */     if (s == null)
/* 104 */       return null;
/* 105 */     return UUID.fromString(s);
/*     */   }
/*     */ 
/*     */   BluxSsProxy(UUID processId, Messenger reply)
/*     */   {
/* 119 */     this.mProcessId = processId;
/* 120 */     this.mReply = reply;
/* 121 */     this.mTerminated = false;
/* 122 */     if ((processId != null) && (reply != null))
/* 123 */       manager().registerProxy(this);
/*     */   }
/*     */ 
/*     */   UUID clientId() {
/* 127 */     return this.mClientId;
/*     */   }
/*     */ 
/*     */   protected void terminate()
/*     */   {
/* 132 */     this.mTerminated = true;
/* 133 */     super.terminate();
/*     */   }
/*     */ 
/*     */   protected boolean handleMessage(Message cmd)
/*     */   {
/* 140 */     switch (cmd.what) {
/*     */     case -100:
/* 142 */       UUID processId = getMessageProcess(cmd);
/* 143 */       if (processId.compareTo(this.mProcessId) != 0) {
/*     */         break;
/*     */       }
/*     */     case -1:
/* 147 */       terminate();
/* 148 */       return false;
/*     */     }
/* 150 */     return false;
/*     */   }
/*     */ 
/*     */   void notifyClient(int what, Bundle data)
/*     */   {
/* 156 */     Message msg = Message.obtain(null, what);
/* 157 */     if (data == null)
/* 158 */       data = new Bundle();
/* 159 */     data.putString("from", uuid().toString());
/*     */ 
/* 161 */     if (this.mReply != null)
/*     */       try {
/* 163 */         data.putString("to", this.mClientId.toString());
/* 164 */         msg.setData(data);
/* 165 */         this.mReply.send(msg);
/*     */       }
/*     */       catch (Exception e) {
/* 168 */         delayAction(new BluxObject.DelayedAction() {
/*     */           protected void act() {
/* 170 */             BluxSsProxy.this.terminate();
/*     */           }
/*     */         }
/*     */         , 0);
/*     */       }
/*     */   }
/*     */ 
/*     */   static class Manager
/*     */   {
/*  24 */     private HashMap<UUID, BluxSsProxy> mProxies = new HashMap();
/*     */ 
/*     */     void registerProxy(BluxSsProxy proxy) {
/*  27 */       if (this.mProxies.get(proxy.uuid()) == null)
/*  28 */         this.mProxies.put(proxy.uuid(), proxy);
/*     */     }
/*     */ 
/*     */     void unregisterProxy(BluxSsProxy proxy)
/*     */     {
/*  33 */       this.mProxies.remove(proxy.uuid());
/*     */     }
/*     */ 
/*     */     void clearAllProxies() {
/*  37 */       Message msg = Message.obtain(null, -1);
/*  38 */       deliverMessage(msg);
/*  39 */       this.mProxies.clear();
/*     */     }
/*     */ 
/*     */     void deliverMessage(Message msg) {
/*  43 */       if ((msg.what >= -200) && (msg.what <= -1)) {
/*  44 */         Set set = this.mProxies.keySet();
/*  45 */         for (UUID e : set) {
/*  46 */           ((BluxSsProxy)this.mProxies.get(e)).handleMessage(msg);
/*     */         }
/*  48 */         clearTerminatedProxies();
/*     */       }
/*     */       else
/*     */       {
/*  52 */         BluxSsProxy proxy = (BluxSsProxy)this.mProxies.get(BluxSsProxy.getMessageReceiver(msg));
/*  53 */         if (proxy != null)
/*  54 */           proxy.handleMessage(msg);
/*     */       }
/*     */     }
/*     */ 
/*     */     void notifyProcessDetach(UUID processId)
/*     */     {
/*  60 */       Bundle data = new Bundle();
/*  61 */       data.putString("process_uuid", processId.toString());
/*  62 */       Message msg = Message.obtain(null, -100);
/*  63 */       msg.setData(data);
/*  64 */       deliverMessage(msg);
/*     */     }
/*     */ 
/*     */     private void clearTerminatedProxies() {
/*  68 */       Set set = this.mProxies.entrySet();
/*  69 */       for (Iterator iterator = set.iterator(); iterator.hasNext(); ) {
/*  70 */         Map.Entry e = (Map.Entry)iterator.next();
/*  71 */         if (((BluxSsProxy)e.getValue()).mTerminated)
/*  72 */           iterator.remove();
/*     */       }
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxSsProxy
 * JD-Core Version:    0.6.0
 */