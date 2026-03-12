package com.example.quanlynhahang;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;

public class ActivityHubFragment extends Fragment {

    public static final int TAB_ORDERS = 0;
    public static final int TAB_REQUESTS = 1;

    private static final String ARG_INITIAL_TAB = "initial_tab";
    private static final String TAG_ORDERS = "orders";
    private static final String TAG_REQUESTS = "requests";

    private MaterialButton btnTabOrders;
    private MaterialButton btnTabRequests;
    private int selectedTab = TAB_ORDERS;

    public static ActivityHubFragment newInstance(int initialTab) {
        ActivityHubFragment fragment = new ActivityHubFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_INITIAL_TAB, initialTab);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_activity_hub, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnTabOrders = view.findViewById(R.id.btnTabOrders);
        btnTabRequests = view.findViewById(R.id.btnTabRequests);

        if (savedInstanceState != null) {
            selectedTab = savedInstanceState.getInt(ARG_INITIAL_TAB, TAB_ORDERS);
        } else if (getArguments() != null) {
            selectedTab = getArguments().getInt(ARG_INITIAL_TAB, TAB_ORDERS);
        }

        btnTabOrders.setOnClickListener(v -> selectTab(TAB_ORDERS));
        btnTabRequests.setOnClickListener(v -> selectTab(TAB_REQUESTS));

        selectTab(selectedTab);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_INITIAL_TAB, selectedTab);
    }

    public void selectTab(int tab) {
        selectedTab = tab == TAB_REQUESTS ? TAB_REQUESTS : TAB_ORDERS;
        updateToggleState();
        showSelectedContent();
    }

    private void updateToggleState() {
        if (btnTabOrders == null || btnTabRequests == null) {
            return;
        }

        boolean showOrders = selectedTab == TAB_ORDERS;
        int selectedBackground = ContextCompat.getColor(requireContext(), R.color.surface);
        int unselectedBackground = ContextCompat.getColor(requireContext(), R.color.surface_variant);
        int selectedText = ContextCompat.getColor(requireContext(), R.color.on_surface);
        int unselectedText = ContextCompat.getColor(requireContext(), R.color.on_surface_variant);

        btnTabOrders.setSelected(showOrders);
        btnTabOrders.setBackgroundTintList(ColorStateList.valueOf(showOrders ? selectedBackground : unselectedBackground));
        btnTabOrders.setTextColor(showOrders ? selectedText : unselectedText);

        btnTabRequests.setSelected(!showOrders);
        btnTabRequests.setBackgroundTintList(ColorStateList.valueOf(showOrders ? unselectedBackground : selectedBackground));
        btnTabRequests.setTextColor(showOrders ? unselectedText : selectedText);
    }

    private void showSelectedContent() {
        if (!isAdded()) {
            return;
        }

        Fragment fragment = selectedTab == TAB_REQUESTS
                ? getOrCreateRequestsFragment()
                : getOrCreateOrdersFragment();
        String tag = selectedTab == TAB_REQUESTS ? TAG_REQUESTS : TAG_ORDERS;

        Fragment current = getChildFragmentManager().findFragmentById(R.id.activityHubContentContainer);
        if (current != null && tag.equals(current.getTag())) {
            return;
        }

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.activityHubContentContainer, fragment, tag)
                .commit();
    }

    private Fragment getOrCreateOrdersFragment() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(TAG_ORDERS);
        if (fragment instanceof OrderFragment) {
            return fragment;
        }
        OrderFragment orderFragment = new OrderFragment();
        Bundle args = new Bundle();
        args.putBoolean(OrderFragment.ARG_EMBEDDED, true);
        orderFragment.setArguments(args);
        return orderFragment;
    }

    private Fragment getOrCreateRequestsFragment() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(TAG_REQUESTS);
        if (fragment instanceof RequestsFragment) {
            return fragment;
        }
        RequestsFragment requestsFragment = new RequestsFragment();
        Bundle args = new Bundle();
        args.putBoolean(RequestsFragment.ARG_EMBEDDED, true);
        requestsFragment.setArguments(args);
        return requestsFragment;
    }
}
