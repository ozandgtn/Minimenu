package com.ozandgtn.minimenuapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.ozandgtn.minimenuapp.R;
import com.ozandgtn.minimenuapp.model.OrderItem;
import com.ozandgtn.minimenuapp.model.OrderRequest;
import com.ozandgtn.minimenuapp.model.OrderResponse;
import com.ozandgtn.minimenuapp.model.Product;
import com.ozandgtn.minimenuapp.network.ApiClient;
import com.ozandgtn.minimenuapp.network.ApiService;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private Button btnCart;

    private ProductAdapter adapter;
    private ApiService api;

    private final Map<Long, OrderItem> cart = new LinkedHashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerViewProducts);
        progressBar  = findViewById(R.id.progressBar); // <- XML’de var
        btnCart      = findViewById(R.id.btnCart);

        adapter = new ProductAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        Retrofit retrofit = ApiClient.getClient();
        api = retrofit.create(ApiService.class);

        adapter.setCallback(this::onAddClicked);
        btnCart.setOnClickListener(v -> showCartBottomSheet());

        fetchProducts();
        updateCartBadge();
    }

    private void fetchProducts() {
        showLoading(true);
        api.getProducts().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    adapter.submit(response.body());
                } else {
                    Toast.makeText(MainActivity.this, "Veri alinamadi", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                showLoading(false);
                Toast.makeText(MainActivity.this, "Hata: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onAddClicked(Product product, int qty) {
        if (product == null || product.getId() == null) return;
        OrderItem line = cart.get(product.getId());
        if (line == null) {
            line = new OrderItem();
            line.setProductId(product.getId());
            line.setProductName(product.getName());
            line.setUnitPrice(product.getPrice() == null ? 0.0 : product.getPrice());
            line.setQuantity(Math.max(1, qty));
            cart.put(product.getId(), line);
        } else {
            line.setQuantity(line.getQuantity() + Math.max(1, qty));
        }
        updateCartBadge();
        Toast.makeText(this, product.getName() + " sepete eklendi (+" + qty + ")", Toast.LENGTH_SHORT).show();
    }

    private void updateCartBadge() {
        int sum = 0;
        for (OrderItem it : cart.values()) sum += it.getQuantity();
        btnCart.setText("Sepet (" + sum + ")");
    }

    private double calcCartTotal() {
        double total = 0.0;
        for (OrderItem it : cart.values()) {
            double unit = it.getUnitPrice() == null ? 0.0 : it.getUnitPrice();
            total += unit * it.getQuantity();
        }
        return total;
    }

    private void showCartBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View v = LayoutInflater.from(this).inflate(R.layout.cart_bottom_sheet, null, false);
        dialog.setContentView(v);

        RecyclerView rv = v.findViewById(R.id.rvCart); // <- XML’de rvCart olarak var
        TextView tvTotal = v.findViewById(R.id.tvTotal);
        EditText etAddress   = v.findViewById(R.id.etAddress);
        EditText etCardName  = v.findViewById(R.id.etCardName);
        EditText etCardNum   = v.findViewById(R.id.etCardNumber);
        Button btnOrder      = v.findViewById(R.id.btnOrder);

        CartAdapter cartAdapter = new CartAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        cartAdapter.submit(new ArrayList<>(cart.values()));
        rv.setAdapter(cartAdapter);

        NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("tr", "TR"));
        tvTotal.setText("Toplam: " + nf.format(calcCartTotal()));

        btnOrder.setOnClickListener(view -> {
            String address = etAddress.getText().toString().trim();
            String cardName = etCardName.getText().toString().trim();
            String cardNum = etCardNum.getText().toString().trim();
            if (address.isEmpty() || cardName.isEmpty() || cardNum.isEmpty()) {
                Toast.makeText(MainActivity.this, "Lutfen tum alanlari doldurun", Toast.LENGTH_SHORT).show();
                return;
            }

            OrderRequest req = new OrderRequest();
            req.setAddress(address);
            req.setCardHolderName(cardName);
            req.setCardNumber(cardNum);

            List<OrderRequest.Item> items = new ArrayList<>();
            for (OrderItem it : cart.values()) {
                OrderRequest.Item x = new OrderRequest.Item();
                x.setProductId(it.getProductId());
                x.setQuantity(it.getQuantity());
                items.add(x);
            }
            req.setItems(items);

            api.createOrder(req).enqueue(new Callback<OrderResponse>() {
                @Override
                public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(MainActivity.this,
                                "Siparis olustu. ID: " + response.body().getId(),
                                Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                        cart.clear();
                        updateCartBadge();
                    } else {
                        Toast.makeText(MainActivity.this, "Siparis basarisiz", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<OrderResponse> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Hata: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
    }
}
