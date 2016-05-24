package com.razikallayi.suraksha;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupManager;
import android.app.backup.SharedPreferencesBackupHelper;

/**
 * Created by Razi Kallayi on 23-05-2016.
 */
public class SurakshaBackup extends BackupAgentHelper {
    // The names of the SharedPreferences groups that the application maintains.  These
    // are the same strings that are passed to getSharedPreferences(String, int).
    static final String PREFS_SMS= "pref_sms";

    // An arbitrary string used within the BackupAgentHelper implementation to
    // identify the SharedPreferencesBackupHelper's data.
    static final String MY_PREFS_BACKUP_KEY = "myprefs";

    // Simply allocate a helper and install it
    public void onCreate() {
        SharedPreferencesBackupHelper helper =
                new SharedPreferencesBackupHelper(this, PREFS_SMS);
        addHelper(MY_PREFS_BACKUP_KEY, helper);
    }
    public void requestBackup() {
        BackupManager bm = new BackupManager(this);
        bm.dataChanged();
    }
//
//    @Override
//    public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState) throws IOException {
//        // Get the oldState input stream
//        FileInputStream instream = new FileInputStream(oldState.getFileDescriptor());
//        DataInputStream in = new DataInputStream(instream);
//
//        try {
//            // Get the last modified timestamp from the state file and data file
//            long stateModified = in.readLong();
//            long fileModified = mDataFile.lastModified();
//
//            if (stateModified != fileModified) {
//                // The file has been modified, so do a backup
//                // Or the time on the device changed, so be safe and do a backup
//            } else {
//                // Don't back up because the file hasn't changed
//                return;
//            }
//        } catch (IOException e) {
//            // Unable to read state file... be safe and do a backup
//        }
//
//        FileOutputStream outstream = new FileOutputStream(newState.getFileDescriptor());
//        DataOutputStream out = new DataOutputStream(outstream);
//
//        long modified = mDataFile.lastModified();
//        out.writeLong(modified);
//
//    }
//
//    @Override
//    public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
//// There should be only one entity, but the safest
//        // way to consume it is using a while loop
//        while (data.readNextHeader()) {
//            String key = data.getKey();
//            int dataSize = data.getDataSize();
//
//            // If the key is ours (for saving top score). Note this key was used when
//            // we wrote the backup entity header
//            if (TOPSCORE_BACKUP_KEY.equals(key)) {
//                // Create an input stream for the BackupDataInput
//                byte[] dataBuf = new byte[dataSize];
//                data.readEntityData(dataBuf, 0, dataSize);
//                ByteArrayInputStream baStream = new ByteArrayInputStream(dataBuf);
//                DataInputStream in = new DataInputStream(baStream);
//
//                // Read the player name and score from the backup data
//                mPlayerName = in.readUTF();
//                mPlayerScore = in.readInt();
//
//                // Record the score on the device (to a file or something)
//                recordScore(mPlayerName, mPlayerScore);
//            } else {
//                // We don't know this entity key. Skip it. (Shouldn't happen.)
//                data.skipEntityData();
//            }
//        }
//
//        // Finally, write to the state blob (newState) that describes the restored data
//        FileOutputStream outstream = new FileOutputStream(newState.getFileDescriptor());
//        DataOutputStream out = new DataOutputStream(outstream);
//        out.writeUTF(mPlayerName);
//        out.writeInt(mPlayerScore);
//    }
}
