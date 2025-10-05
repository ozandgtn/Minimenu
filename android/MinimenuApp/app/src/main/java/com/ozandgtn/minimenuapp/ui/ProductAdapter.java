package com.ozandgtn.minimenuapp.ui;

import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ozandgtn.minimenuapp.R;
import com.ozandgtn.minimenuapp.model.Product;
import com.ozandgtn.minimenuapp.network.ApiClient;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {

    public interface Callback {
        void onAdd(Product product, int quantity);
    }

    private final List<Product> items = new ArrayList<>();
    private final SparseIntArray quantities = new SparseIntArray(); // key: position, val: qty
    private Callback callback;

    public void submit(List<Product> list) {
        items.clear();
        if (list != null) items.addAll(list);
        quantities.clear();
        for (int i = 0; i < items.size(); i++) quantities.put(i, 1); // varsayılan 1
        notifyDataSetChanged();
    }

    public void setCallback(Callback cb) {
        this.callback = cb;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        Product p = items.get(position);

        h.tvName.setText(p.getName() == null ? "" : p.getName());
        h.tvDesc.setText(p.getDescription() == null ? "" : p.getDescription());

        Double price = p.getPrice() == null ? 0.0 : p.getPrice();
        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("tr", "TR"));
        h.tvPrice.setText(nf.format(price));

        // ---- Görsel Yükleme (Glide) ----
        String path = p.getImageUrl();
        if (path == null || path.trim().isEmpty()) {
            h.ivThumb.setImageResource(R.mipmap.ic_launcher_round);
        } else {
            String finalUrl;
            String t = path.trim();

            if (t.startsWith("http://") || t.startsWith("https://")) {
                finalUrl = t; // mutlak URL
            } else {
                // göreli URL -> BASE_URL ile birleştir
                String base = ApiClient.BASE_URL; // public oldu
                if (!base.endsWith("/")) base += "/";
                if (t.startsWith("/")) t = t.substring(1);
                finalUrl = base + t; // örn: http://10.0.2.2:8080/images/cola.png
            }

            Glide.with(h.ivThumb.getContext())
                    .load(finalUrl)
                    .placeholder(R.mipmap.ic_launcher_round)
                    .error(R.mipmap.ic_launcher_round)
                    .into(h.ivThumb);
        }
        // ---------------------------------

        int q = Math.max(1, quantities.get(position, 1));
        h.tvQty.setText(String.valueOf(q));

        h.btnMinus.setOnClickListener(v -> {
            int current = Math.max(1, quantities.get(position, 1));
            if (current > 1) current--;
            quantities.put(position, current);
            h.tvQty.setText(String.valueOf(current));
        });

        h.btnPlus.setOnClickListener(v -> {
            int current = Math.max(1, quantities.get(position, 1));
            if (current < 99) current++;
            quantities.put(position, current);
            h.tvQty.setText(String.valueOf(current));
        });

        h.btnAdd.setOnClickListener(v -> {
            if (callback != null) {
                int qty = Math.max(1, quantities.get(position, 1));
                callback.onAdd(p, qty);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvName, tvDesc, tvPrice, tvQty;
        ImageButton btnMinus, btnPlus;
        Button btnAdd;

        VH(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.ivThumb);
            tvName  = itemView.findViewById(R.id.tvName);
            tvDesc  = itemView.findViewById(R.id.tvDesc);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQty   = itemView.findViewById(R.id.tvQty);
            btnMinus= itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnAdd  = itemView.findViewById(R.id.btnAdd);
        }
    }
}
