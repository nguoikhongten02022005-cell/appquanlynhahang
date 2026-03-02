package com.example.quanlynhahang;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.OrderAdapter;
import com.example.quanlynhahang.model.Order;
import com.example.quanlynhahang.model.RecommendedDishItem;

import java.util.ArrayList;
import java.util.List;

public class OrderFragment extends Fragment {

    private final List<Order> orders = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupOrderData();
        setupRecyclerView(view);
    }

    private void setupRecyclerView(View view) {
        RecyclerView rvOrders = view.findViewById(R.id.rvOrders);
        rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvOrders.setAdapter(new OrderAdapter(orders));
    }

    private void setupOrderData() {
        orders.clear();

        List<Order.OrderDish> orderDishes1 = new ArrayList<>();
        orderDishes1.add(new Order.OrderDish(
                new RecommendedDishItem(
                        R.drawable.ic_restaurant_24,
                        getString(R.string.dish_bo_luc_lac),
                        getString(R.string.price_145k),
                        true
                ),
                2
        ));
        orderDishes1.add(new Order.OrderDish(
                new RecommendedDishItem(
                        R.drawable.ic_local_drink_24,
                        getString(R.string.dish_tra_dao),
                        getString(R.string.price_45k),
                        true
                ),
                1
        ));
        orders.add(new Order(
                "#DH12345",
                "12/05/2024 10:30",
                getString(R.string.order_total_335k),
                Order.Status.PENDING_CONFIRMATION,
                orderDishes1
        ));

        List<Order.OrderDish> orderDishes2 = new ArrayList<>();
        orderDishes2.add(new Order.OrderDish(
                new RecommendedDishItem(
                        R.drawable.ic_restaurant_24,
                        getString(R.string.dish_lau_thai),
                        getString(R.string.price_259k),
                        false
                ),
                1
        ));
        orderDishes2.add(new Order.OrderDish(
                new RecommendedDishItem(
                        R.drawable.ic_restaurant_24,
                        getString(R.string.dish_salad_ca_hoi),
                        getString(R.string.price_129k),
                        true
                ),
                1
        ));
        orders.add(new Order(
                "#DH12346",
                "11/05/2024 19:15",
                getString(R.string.order_total_388k),
                Order.Status.CONFIRMED,
                orderDishes2
        ));

        List<Order.OrderDish> orderDishes3 = new ArrayList<>();
        orderDishes3.add(new Order.OrderDish(
                new RecommendedDishItem(
                        R.drawable.ic_restaurant_24,
                        getString(R.string.dish_salad_ca_hoi),
                        getString(R.string.price_129k),
                        true
                ),
                2
        ));
        orders.add(new Order(
                "#DH12347",
                "10/05/2024 12:00",
                getString(R.string.order_total_258k),
                Order.Status.COMPLETED,
                orderDishes3
        ));
    }
}
