package com.sagib.food2youadmin;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sagib.food2youadmin.models.Order;
import com.sagib.food2youadmin.models.Product;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class NewOrdersFragment extends Fragment {


    @BindView(R.id.rvNewOrders)
    RecyclerView rvNewOrders;
    AlertDialog show;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_orders, container, false);
        unbinder = ButterKnife.bind(this, v);
        AlertDialog.Builder wait = new AlertDialog.Builder(getContext());
        wait.setMessage("אנא המתן").setCancelable(false);
        show = wait.show();
        BroadcastReceiver dismissR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                show.dismiss();
            }
        };
        IntentFilter dismissF = new IntentFilter("Dismiss");
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(dismissR, dismissF);
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Orders").child("New");
        NewOrdersAdapter adapter = new NewOrdersAdapter(mRef.orderByChild("id"), this);
        rvNewOrders.setAdapter(adapter);
        rvNewOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    show.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        setTitle("ניהול - ג׳חנון ביתי עד הבית");
    }

    public void setTitle(String title){
        ((AppCompatActivity)getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView textView = new TextView(getActivity());
        textView.setText(title);
        textView.setTextSize(20);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setCustomView(textView);
    }

    public static class NewOrdersAdapter extends FirebaseRecyclerAdapter<Order, NewOrdersAdapter.NewOrdersViewHolder> {

        Fragment fragment;

        public NewOrdersAdapter(Query query, Fragment fragment) {
            super(Order.class, R.layout.order_item, NewOrdersViewHolder.class, query);
            this.fragment = fragment;
        }

        @Override
        protected void populateViewHolder(NewOrdersViewHolder viewHolder, final Order model, int position) {
            ArrayList<Product> products = model.getProducts();
            if (products != null) {
                LocalBroadcastManager.getInstance(fragment.getContext()).sendBroadcast(new Intent("Dismiss"));
            }
            viewHolder.tvTotalProducts.setText("סה״כ פריטים: " + products.size());
            viewHolder.tvTotalCost.setText("סה״כ לחיוב: " + model.getTotalPrice());
            viewHolder.tvCustomerDetails.setText(model.getFullName() + " " + model.getPhoneNumber());
            viewHolder.tvAddress.setText(model.getAddress() + " " + model.getHouseNumber() + ", " + model.getCity() + " קומה " + model.getFloorNumber() + " דירה " + model.getAptNumber());
            viewHolder.tvNotes.setText("הערות: " + model.getNotes());
            viewHolder.tvOrderNumber.setText("הזמנה: " + model.getOrderNumber());
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle args = new Bundle();
                    args.putParcelable("Order", model);
                    OrderDetailedFragment orderDetailedFragment = new OrderDetailedFragment();
                    orderDetailedFragment.setArguments(args);
                    orderDetailedFragment.show(fragment.getFragmentManager(), "Detailed");
                }
            });
        }

        public static class NewOrdersViewHolder extends RecyclerView.ViewHolder {

            TextView tvTotalCost;
            TextView tvCustomerDetails;
            TextView tvAddress;
            TextView tvTotalProducts;
            TextView tvNotes;
            TextView tvOrderNumber;

            public NewOrdersViewHolder(View itemView) {
                super(itemView);
                tvAddress = (TextView) itemView.findViewById(R.id.tvAddress);
                tvCustomerDetails = (TextView) itemView.findViewById(R.id.tvCustomerDetails);
                tvNotes = (TextView) itemView.findViewById(R.id.tvNotes);
                tvTotalCost = (TextView) itemView.findViewById(R.id.tvTotalCost);
                tvTotalProducts = (TextView) itemView.findViewById(R.id.tvTotalProducts);
                tvOrderNumber = (TextView) itemView.findViewById(R.id.tvOrderNumber);
            }
        }
    }

}
