package com.example.quanlynhahang;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quanlynhahang.adapter.MenuAdapter;
import com.example.quanlynhahang.data.DatabaseHelper;
import com.example.quanlynhahang.model.RecommendedDishItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MenuFragment extends Fragment {

    private final List<RecommendedDishItem> allDishes = new ArrayList<>();
    private final List<String> allDescriptions = new ArrayList<>();

    private DatabaseHelper databaseHelper;
    private MenuAdapter menuAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(requireContext());
        databaseHelper.seedDishesIfEmpty(requireContext());

        setupDishData();
        setupRecyclerView(view);
        setupSearch(view);
    }

    private void setupRecyclerView(View view) {
        RecyclerView rvMenu = view.findViewById(R.id.rvMenu);
        rvMenu.setLayoutManager(new LinearLayoutManager(requireContext()));

        menuAdapter = new MenuAdapter(
                allDishes,
                allDescriptions,
                dish -> Toast.makeText(
                        requireContext(),
                        getString(R.string.menu_added_to_cart, dish.getName()),
                        Toast.LENGTH_SHORT
                ).show()
        );

        rvMenu.setAdapter(menuAdapter);
    }

    private void setupSearch(View view) {
        EditText etMenuSearch = view.findViewById(R.id.etMenuSearch);
        etMenuSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void filter(String text) {
        String keyword = text == null ? "" : text.trim().toLowerCase(Locale.ROOT);

        if (keyword.isEmpty()) {
            menuAdapter.updateData(allDishes, allDescriptions);
            return;
        }

        List<RecommendedDishItem> filteredDishes = new ArrayList<>();
        List<String> filteredDescriptions = new ArrayList<>();

        for (int i = 0; i < allDishes.size(); i++) {
            RecommendedDishItem dish = allDishes.get(i);
            String description = allDescriptions.get(i);

            String nameLower = dish.getName().toLowerCase(Locale.ROOT);
            String descriptionLower = description.toLowerCase(Locale.ROOT);

            if (nameLower.contains(keyword) || descriptionLower.contains(keyword)) {
                filteredDishes.add(dish);
                filteredDescriptions.add(description);
            }
        }

        menuAdapter.updateData(filteredDishes, filteredDescriptions);
    }

    private void setupDishData() {
        allDishes.clear();
        allDescriptions.clear();

        List<DatabaseHelper.DishRecord> dishRecords = databaseHelper.getAllDishes();
        for (DatabaseHelper.DishRecord record : dishRecords) {
            allDishes.add(record.getDishItem());
            allDescriptions.add(record.getDescription());
        }
    }
}
