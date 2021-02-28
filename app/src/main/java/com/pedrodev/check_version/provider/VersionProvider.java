package com.pedrodev.check_version.provider;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class VersionProvider {

    // Instance FirebaseDatabase
    FirebaseDatabase mFirebase = FirebaseDatabase.getInstance();

    // Instance DatabaseReference
    DatabaseReference mDatabase;

    public VersionProvider() {
        // This is a reference to the table in Realtime database
        mDatabase = mFirebase.getReference("Version");
    }

    public Query getVersion(){
        // Reference
        return mFirebase.getReference();
    }
}
