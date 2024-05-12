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

public class ShippingFirebase {
    private static final String COLLECTION_NAME = "Shippings";

    private final CollectionReference shippingsCollection;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public ShippingFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        shippingsCollection = db.collection(COLLECTION_NAME);
    }

    public void createShipping(Shipping shipping, final OnCompleteListener<Void> onCompleteListener) {
        executor.execute(() -> {
            try {
                shippingsCollection.document(shipping.getUserId()).set(shipping).addOnCompleteListener(task -> {
                    handler.post(() -> onCompleteListener.onComplete(task));
                });
            } catch (Exception e) {
                onCompleteListener.onComplete(Tasks.forException(e));
            }
        });
    }

    public Task<Shipping> getShippingByUserId(String userId) {
        return shippingsCollection.document(userId).get().continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            return document.toObject(Shipping.class);
                        }
                    }
                    return null;
                });
    }

    public void getAllShippings(final OnCompleteListener<QuerySnapshot> onCompleteListener) {
        executor.execute(() -> {
            try {
                shippingsCollection.get().addOnCompleteListener(task -> {
                    handler.post(() -> onCompleteListener.onComplete(task));
                });
            } catch (Exception e) {
                onCompleteListener.onComplete(Tasks.forException(e));
            }
        });
    }

    public void updateShipping(Shipping shipping, final OnCompleteListener<Void> onCompleteListener) {
        executor.execute(() -> {
            try {
                shippingsCollection.document(shipping.getUserId()).set(shipping).addOnCompleteListener(task -> {
                    handler.post(() -> onCompleteListener.onComplete(task));
                });
            } catch (Exception e) {
                onCompleteListener.onComplete(Tasks.forException(e));
            }
        });
    }

    public void deleteShipping(String userId, final OnCompleteListener<Void> onCompleteListener) {
        executor.execute(() -> {
            try {
                shippingsCollection.document(userId).delete().addOnCompleteListener(task -> {
                    handler.post(() -> onCompleteListener.onComplete(task));
                });
            } catch (Exception e) {
                onCompleteListener.onComplete(Tasks.forException(e));
            }
        });
    }
}
