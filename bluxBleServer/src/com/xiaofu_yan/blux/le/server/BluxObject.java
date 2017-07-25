/*    */ package com.xiaofu_yan.blux.le.server;
/*    */ 
/*    */ import android.os.Handler;
/*    */ import android.os.Message;
/*    */ import java.util.UUID;
/*    */ 
/*    */ class BluxObject
/*    */ {
/*    */   protected UUID mUUID;
/*    */   private static DelayHandler sDelayHandler;
/*    */ 
/*    */   protected void terminate()
/*    */   {
/* 12 */     this.mUUID = null;
/*    */   }
/*    */ 
/*    */   protected UUID uuid() {
/* 16 */     if (this.mUUID == null)
/* 17 */       this.mUUID = UUID.randomUUID();
/* 18 */     return this.mUUID;
/*    */   }
/*    */ 
/*    */   void delayAction(DelayedAction action, int delayMillis)
/*    */   {
/* 31 */     if (action != null)
/* 32 */       sDelayHandler.queueAction(action, delayMillis);
/*    */   }
/*    */ 
/*    */   static void initThread()
/*    */   {
/* 58 */     if (sDelayHandler == null)
/* 59 */       sDelayHandler = new DelayHandler(null);
/*    */   }
/*    */ 
/*    */   private static class DelayHandler extends Handler
/*    */   {
/*    */     void queueAction(BluxObject.DelayedAction action, int delayMillis)
/*    */     {
/* 38 */       Message msg = obtainMessage(100, action);
/* 39 */       if (delayMillis == 0)
/* 40 */         sendMessage(msg);
/*    */       else
/* 42 */         sendMessageDelayed(msg, delayMillis);
/*    */     }
/*    */ 
/*    */     public void handleMessage(Message msg)
/*    */     {
/* 47 */       if (msg.what == 100) {
/* 48 */         BluxObject.DelayedAction action = (BluxObject.DelayedAction)msg.obj;
/* 49 */         action.act();
/*    */       }
/* 51 */       super.handleMessage(msg);
/*    */     }
/*    */   }
/*    */ 
/*    */   class DelayedAction
/*    */   {
/*    */     DelayedAction()
/*    */     {
/*    */     }
/*    */ 
/*    */     protected void act()
/*    */     {
/*    */     }
/*    */ 
/*    */     void cancel()
/*    */     {
/* 24 */       if (BluxObject.sDelayHandler != null)
/* 25 */         BluxObject.sDelayHandler.removeMessages(100, this);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxObject
 * JD-Core Version:    0.6.0
 */