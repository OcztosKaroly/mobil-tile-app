package com.example.tile_shop_application;

import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class BillingFirebase {
    private static final String COLLECTION_NAME = "Billings";

    private final CollectionReference billingsCollection;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public BillingFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        billingsCollection = db.collection(COLLECTION_NAME);
    }

    public void createBilling(Billing billing, final OnCompleteListener<Void> onCompleteListener) {
        executor.execute(() -> {
            try {
                billingsCollection.document(billing.getUserId()).set(billing).addOnCompleteListener(task -> {
                    handler.post(() -> onCompleteListener.onComplete(task));
                });
            } catch (Exception e) {
                onCompleteListener.onComplete(Tasks.forException(e));
            }
        });
    }

    public Task<Billing> getBillingByUserId(String userId) {
        return billingsCollection.document(userId).get().continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            return document.toObject(Billing.class);
                        }
                    }
                    return null;
                });
    }

    public void getAllBillings(final OnCompleteListener<QuerySnapshot> onCompleteListener) {
        executor.execute(() -> {
            try {
                billingsCollection.get().addOnCompleteListener(task -> {
                    handler.post(() -> onCompleteListener.onComplete(task));
                });
            } catch (Exception e) {
                onCompleteListener.onComplete(Tasks.forException(e));
            }
        });
    }

    public void updateBilling(Billing billing, final OnCompleteListener<Void> onCompleteListener) {
        executor.execute(() -> {
            try {
                billingsCollection.document(billing.getUserId()).set(billing).addOnCompleteListener(task -> {
                    handler.post(() -> onCompleteListener.onComplete(task));
                });
            } catch (Exception e) {
                onCompleteListener.onComplete(Tasks.forException(e));
            }
        });
    }

    public void deleteBilling(String userId, final OnCompleteListener<Void> onCompleteListener) {
        executor.execute(() -> {
            try {
                billingsCollection.document(userId).delete().addOnCompleteListener(task -> {
                    handler.post(() -> onCompleteListener.onComplete(task));
                });
            } catch (Exception e) {
                onCompleteListener.onComplete(Tasks.forException(e));
            }
        });
    }
}
