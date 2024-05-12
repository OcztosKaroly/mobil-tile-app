package com.example.tile_shop_application;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder> implements Filterable {
    private ArrayList<Product> mShoppingItemsData;
    private ArrayList<Product> mShoppingItemsDataAll;
    private Context mContext;
    private int lastPosition = -1;

    ShoppingItemAdapter(Context context, ArrayList<Product> itemsData) {
        this.mShoppingItemsData = itemsData;
        this.mShoppingItemsDataAll = itemsData;
        this.mContext = context;
    }


    @NonNull
    @Override
    public ShoppingItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ShoppingItemAdapter.ViewHolder holder, int position) {
        Product currentItem = mShoppingItemsData.get(position);

        holder.bindTo(currentItem);

        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() { return mShoppingItemsData.size(); }

    @Override
    public Filter getFilter() { return shoppingFilter; }

    private Filter shoppingFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Product> filteredItems = new ArrayList<>();
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                results.count = mShoppingItemsDataAll.size();
                results.values = mShoppingItemsDataAll;
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Product item: mShoppingItemsDataAll) {
                    if (item.getName().toLowerCase().trim().contains(filterPattern)) {
                        filteredItems.add(item);
                    }
                }

                results.count = filteredItems.size();
                results.values = filteredItems;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mShoppingItemsData = (ArrayList<Product>) results.values;
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        private StorageReference storageReference;

        private TextView mItemNameText;
        private TextView mItemDescriptionText;
        private TextView mItemColorText;
        private TextView mItemSizeText;
        private TextView mItemWhereText;
        private TextView mItemPriceText;
        private ImageView mItemImage;

        ViewHolder(View itemView) {
            super(itemView);

            storageReference = FirebaseStorage.getInstance().getReference().child("images");

            mItemNameText = itemView.findViewById(R.id.itemName);
            mItemDescriptionText = itemView.findViewById(R.id.itemDescription);
            mItemColorText = itemView.findViewById(R.id.itemColor);
            mItemSizeText = itemView.findViewById(R.id.itemSize);
            mItemWhereText = itemView.findViewById(R.id.itemWhere);
            mItemPriceText = itemView.findViewById(R.id.itemPrice);
            mItemImage = itemView.findViewById(R.id.itemImage);

            itemView.findViewById(R.id.addToCart).setOnClickListener(v -> {
                Toast.makeText(mContext, "A termék bekerült a kosárba.", Toast.LENGTH_SHORT).show();
                ((ShopActivity) mContext).updateAlertIcon();
            });
        }

        public void bindTo(Product currentItem) {
            mItemNameText.setText(currentItem.getName());
            mItemDescriptionText.setText(currentItem.getDescription());
            mItemColorText.setText(currentItem.getColor());
            mItemSizeText.setText(currentItem.getSize());
            mItemWhereText.setText(currentItem.getWhere());
            mItemPriceText.setText(String.valueOf(currentItem.getPrice()));

//            Glide.with(mContext).load("images/" + currentItem.getId() + ".jpeg").into(mItemImage);
            loadProductImageById(currentItem.getId(), mItemImage);
        }

        private void loadProductImageById(String productId, ImageView imageView) {
            StorageReference imageRef = storageReference.child(productId + ".jpeg");
            Log.i("Images", "Kép referenciája: " + imageRef);
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                if (uri != null) {
                    Log.i("Images", "Kép urija: " + uri);
                    Glide.with(mContext).load(uri).into(imageView);
                }
            });
        }
    }
}
