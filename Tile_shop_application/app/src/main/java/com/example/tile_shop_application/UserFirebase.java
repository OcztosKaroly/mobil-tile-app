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

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class UserFirebase {
    private static final String COLLECTION_NAME = "Users";

    private final CollectionReference usersCollection;
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());

    public UserFirebase() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usersCollection = db.collection(COLLECTION_NAME);
    }

    public void createUser(User user, final OnCompleteListener<Void> onCompleteListener) {
        executor.execute(() -> {
            try {
                usersCollection.document(user.getId()).set(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        onCompleteListener.onComplete(task);
                    } else {
                        onCompleteListener.onComplete(Tasks.forException(Objects.requireNonNull(task.getException())));
                    }
                });
            } catch (Exception e) {
                onCompleteListener.onComplete(Tasks.forException(e));
            }
        });
    }

    public Task<User> getUserById(String id) {
        return usersCollection.document(id).get().continueWith(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            return document.toObject(User.class);
                        }
                    }
                    return null;
                });
    }

    public void getAllUsers(final OnCompleteListener<QuerySnapshot> onCompleteListener) {
        executor.execute(() -> {
            try {
                usersCollection.get().addOnCompleteListener(task -> {
                    handler.post(() -> onCompleteListener.onComplete(task));
                });
            } catch (Exception e) {
                onCompleteListener.onComplete(Tasks.forException(e));
            }
        });
    }

    public void updateUser(User user, final OnCompleteListener<Void> onCompleteListener) {
        executor.execute(() -> {
            try {
                usersCollection.document(user.getId()).set(user).addOnCompleteListener(task -> {
                    handler.post(() -> onCompleteListener.onComplete(task));
                });
            } catch (Exception e) {
                onCompleteListener.onComplete(Tasks.forException(e));
            }
        });
    }

    public void deleteUser(String userId, final OnCompleteListener<Void> onCompleteListener) {
        executor.execute(() -> {
            try {
                usersCollection.document(userId).delete().addOnCompleteListener(task -> {
                    handler.post(() -> onCompleteListener.onComplete(task));
                });
            } catch (Exception e) {
                onCompleteListener.onComplete(Tasks.forException(e));
            }
        });
    }
}
