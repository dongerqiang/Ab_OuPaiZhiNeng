/*    */ package com.xiaofu_yan.blux.le.server;
/*    */ 
/*    */ import android.os.Bundle;
/*    */ import android.os.Message;
/*    */ import android.os.Messenger;
/*    */ import java.util.UUID;
/*    */ 
/*    */ public class BluxSsAccountManager extends BluxSsProxy
/*    */ {
/*    */   private static final int CMD_GET_USER = 1;
/*    */   private static final int CMD_SET_USER = 2;
/*    */   private static final int RSP_UPDATE_USER = 1;
/*    */   private BluxAccountManager mAccountManager;
/*    */   private AccountManagerDelegate mAccountManagerDelegate;
/*    */ 
/*    */   BluxSsAccountManager(BluxAccountManager bam, UUID processId, Messenger reply)
/*    */   {
/* 13 */     super(processId, reply);
/* 14 */     this.mAccountManager = bam;
/* 15 */     this.mAccountManagerDelegate = new AccountManagerDelegate(null);
/* 16 */     this.mAccountManager.registerDelegate(this.mAccountManagerDelegate);
/*    */   }
/*    */ 
/*    */   protected void terminate()
/*    */   {
/* 21 */     if (this.mAccountManager != null) {
/* 22 */       this.mAccountManager.unregisterDelegate(this.mAccountManagerDelegate);
/* 23 */       this.mAccountManager = null;
/* 24 */       this.mAccountManagerDelegate = null;
/*    */     }
/* 26 */     super.terminate();
/*    */   }
/*    */ 
/*    */   void setStateData(Bundle data) {
/* 30 */     data.putInt("count", this.mAccountManager.count());
/*    */   }
/*    */ 
/*    */   protected boolean handleMessage(Message cmd)
/*    */   {
/* 45 */     if (super.handleMessage(cmd)) {
/* 46 */       return true;
/*    */     }
/* 48 */     switch (cmd.what) {
/*    */     case 2:
/* 50 */       if (this.mAccountManager == null) break;
/* 51 */       int index = cmd.getData().getInt("index");
/* 52 */       int control = cmd.getData().getInt("control");
/* 53 */       int passkey = cmd.getData().getInt("passkey");
/* 54 */       String name = cmd.getData().getString("name");
/* 55 */       BluxAccountManager.Account account = new BluxAccountManager.Account(index, control, passkey, name);
/* 56 */       this.mAccountManager.setAccount(account);
/* 57 */       break;
/*    */     case 1:
/* 61 */       if (this.mAccountManager == null) break;
/* 62 */       int index = cmd.getData().getInt("index");
/* 63 */       this.mAccountManager.getAccount(index);
/* 64 */       break;
/*    */     default:
/* 67 */       return false;
/*    */     }
/* 69 */     return true;
/*    */   }
/*    */   private class AccountManagerDelegate extends BluxAccountManager.Delegate {
/*    */     private AccountManagerDelegate() {
/*    */     }
/*    */ 
/*    */     protected void updateAccount(BluxAccountManager.Account account) {
/* 76 */       Bundle data = new Bundle();
/* 77 */       data.putInt("index", account.index);
/* 78 */       data.putInt("control", account.control);
/* 79 */       data.putInt("passkey", account.passkey);
/* 80 */       if (account.name != null) {
/* 81 */         data.putString("name", account.name);
/*    */       }
/* 83 */       BluxSsAccountManager.this.notifyClient(1, data);
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\jpj\Desktop\新建文件夹\jindouyun\发送档位1-25\BluxBleServer\
 * Qualified Name:     com.xiaofu_yan.blux.le.server.BluxSsAccountManager
 * JD-Core Version:    0.6.0
 */