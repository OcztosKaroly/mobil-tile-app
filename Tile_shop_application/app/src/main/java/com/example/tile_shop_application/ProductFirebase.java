
package com.example.tile_shop_application;

import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ProductFirebase {
        private static final String COLLECTION_NAME = "Products";

        private final CollectionReference productsCollection;

        private final Executor executor = Executors.newSingleThreadExecutor();
        private final Handler handler = new Handler(Looper.getMainLooper());

        public ProductFirebase() {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            productsCollection = db.collection(COLLECTION_NAME);
        }

        public void getAllProducts(final OnCompleteListener<QuerySnapshot> onCompleteListener) {
            executor.execute(() -> {
                try {
                    productsCollection.get().addOnCompleteListener(task -> {
                        handler.post(() -> onCompleteListener.onComplete(task));
                    });
                } catch (Exception e) {
                    onCompleteListener.onComplete(Tasks.forException(e));
                }
            });
        }

        public void getAllProductsOrdered(int limit, final OnCompleteListener<QuerySnapshot> onCompleteListener) {
            executor.execute(() -> {
                try {
                    productsCollection.orderBy("name").limit(limit).get().addOnCompleteListener(task -> {
                        handler.post(() -> onCompleteListener.onComplete(task));
                    });
                } catch (Exception e) {
                    onCompleteListener.onComplete(Tasks.forException(e));
                }
            });
        }
    public void getAllProductsByName(String searchQuery, final OnCompleteListener<QuerySnapshot> onCompleteListener) {
        executor.execute(() -> {
            try {
                productsCollection.whereGreaterThanOrEqualTo("name", searchQuery)
                        .whereLessThan("name", searchQuery + "\uf8ff")
                        .get().addOnCompleteListener(task -> {
                    handler.post(() -> onCompleteListener.onComplete(task));
                });
            } catch (Exception e) {
                onCompleteListener.onComplete(Tasks.forException(e));
            }
        });
    }

//    public void getAllProducts(final OnCompleteListener<QuerySnapshot> onCompleteListener) {
//        executor.execute(() -> {
//            try {
//                productsCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
//                    List<Product> products = new ArrayList<>();
//                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                        Product product = documentSnapshot.toObject(Product.class);
//
//                        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + product.getId() + ".jpeg");
//                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                            product.setImageUrl(uri.toString());
//                            products.add(product);
//
//                            if (products.size() == queryDocumentSnapshots.size()) {
//                                callback.onProductsLoaded(products);
//                            }
//                        }).addOnFailureListener(exception -> {
//                            Log.e(LOG_TAG, "Error in downloading image", exception);
//                        });
//                    }
//                }).addOnFailureListener(exception -> {
//                    Log.e(LOG_TAG, "Error in fetching products", exception);
//                    callback.onError(exception);
//                });
////                productsCollection.get().addOnCompleteListener(task -> {
////                    handler.post(() -> onCompleteListener.onComplete(task));
////                });
//            } catch (Exception e) {
//                onCompleteListener.onComplete(Tasks.forException(e));
//            }
//        });
//    }

//    public static synchronized ProductFirebase getInstance() {
//        if (instance == null) {
//            synchronized (ProductFirebase.class) {
//                if (instance == null) {
//                    instance = new ProductFirebase();
//                }
//            }
//        }
//
//        return instance;
//    }
//
//    public interface ProductsCallback {
//        void onProductsLoaded(List<Product> products);
//        void onError(Exception e);
//    }
//
//    public void getProducts(ProductsCallback callback) {
//        firestore.collection(COLLECTION_NAME)
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    List<Product> products = new ArrayList<>();
//                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                        Product product = documentSnapshot.toObject(Product.class);
//
//                        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + product.getId() + ".jpeg");
//                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                            String imageUrl = uri.toString();
//                            product.setImageUrl(imageUrl);
//                            products.add(product);
//
//                            if (products.size() == queryDocumentSnapshots.size()) {
//                                callback.onProductsLoaded(products);
//                            }
//                        }).addOnFailureListener(exception -> {
//                            Log.e(LOG_TAG, "Error in downloading image", exception);
//                        });
//                    }
//                }).addOnFailureListener(exception -> {
//                    Log.e(LOG_TAG, "Error in fetching products", exception);
//                    callback.onError(exception);
//                });
//    }
//
//    public interface ProductCallback {
//        void onProductLoaded(Product product);
//        void onError(Exception e);
//    }
//
//    public void getProductById(String productId, ProductCallback callback) {
//        firestore.collection(COLLECTION_NAME).document(productId)
//                .get()
//                .addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        Product product = documentSnapshot.toObject(Product.class);
//
//                        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + productId + ".jpeg");
//                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                            String imageUrl = uri.toString();
//                            product.setImageUrl(imageUrl);
//                            callback.onProductLoaded(product);
//                        }).addOnFailureListener(exception -> {
//                            Log.e(LOG_TAG, "Error in downloading image", exception);
//                            callback.onError(exception);
//                        });
//                    } else {
//                        callback.onError(new Exception("Product not found"));
//                    }
//                }).addOnFailureListener(exception -> {
//                    Log.e(LOG_TAG, "Error in fetching product document", exception);
//                    callback.onError(exception);
//                });
//    }
//
//    public void getProductsByName(String searchQuery, ProductsCallback callback) {
//        firestore.collection(COLLECTION_NAME)
//                .whereGreaterThanOrEqualTo("name", searchQuery)
//                .whereLessThan("name", searchQuery + "\uf8ff")
//                .get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    List<Product> products = new ArrayList<>();
//                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
//                        Product product = documentSnapshot.toObject(Product.class);
//
//                        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("images/" + product.getId() + ".jpeg");
//                        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
//                            product.setImageUrl(uri.toString());
//                            products.add(product);
//
//                            if (products.size() == queryDocumentSnapshots.size()) {
//                                callback.onProductsLoaded(products);
//                            }
//                        }).addOnFailureListener(exception -> {
//                            Log.e(LOG_TAG, "Error in downloading image", exception);
//                        });
//                    }
//                }).addOnFailureListener(exception -> {
//                    Log.e(LOG_TAG, "Error in fetching products", exception);
//                    callback.onError(exception);
//                });
//    }
}