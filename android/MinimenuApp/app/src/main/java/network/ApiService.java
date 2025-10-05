package com.ozandgtn.minimenuapp.network;

import java.util.List;

import com.ozandgtn.minimenuapp.model.Category;
import com.ozandgtn.minimenuapp.model.Product;
import com.ozandgtn.minimenuapp.model.OrderRequest;
import com.ozandgtn.minimenuapp.model.OrderResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiService {

    @GET("categories")
    Call<List<Category>> getCategories();

    @GET("products")
    Call<List<Product>> getProducts();

    @GET("products/byCategory/{categoryId}")
    Call<List<Product>> getProductsByCategory(@Path("categoryId") long categoryId);

    @POST("orders")
    Call<OrderResponse> createOrder(@Body OrderRequest request);
}
