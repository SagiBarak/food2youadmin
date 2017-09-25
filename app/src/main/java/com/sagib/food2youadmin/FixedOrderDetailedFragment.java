package com.sagib.food2youadmin;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sagib.food2youadmin.models.Order;
import com.sagib.food2youadmin.models.Product;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class FixedOrderDetailedFragment extends DialogFragment {


    @BindView(R.id.tvOrderNumber)
    TextView tvOrderNumber;
    @BindView(R.id.tvForTitle)
    TextView tvForTitle;
    @BindView(R.id.tvFullName)
    TextView tvFullName;
    @BindView(R.id.tvPhoneNumber)
    TextView tvPhoneNumber;
    @BindView(R.id.tvFullAdress)
    TextView tvFullAdress;
    @BindView(R.id.tvCity)
    TextView tvCity;
    @BindView(R.id.tvFloorNum)
    TextView tvFloorNum;
    @BindView(R.id.tvHouseNum)
    TextView tvHouseNum;
    @BindView(R.id.tvDate)
    TextView tvDate;
    @BindView(R.id.tvHour)
    TextView tvHour;
    @BindView(R.id.tvOrderTimeTitle)
    TextView tvOrderTimeTitle;
    @BindView(R.id.tvOrderTime)
    TextView tvOrderTime;
    @BindView(R.id.rvItems)
    RecyclerView rvItems;
    @BindView(R.id.btnOrderFixed)
    BootstrapButton btnOrderFixed;
    @BindView(R.id.btnReturn)
    BootstrapButton btnReturn;
    Order order;
    String orderUID;
    Unbinder unbinder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_fixed_order_detailed, container, false);
        unbinder = ButterKnife.bind(this, v);
        order = getArguments().getParcelable("Order");
        ArrayList<Product> products = order.getProducts();
        Log.d("SagiB", products.toString());
        ProductListAdapter adapter = new ProductListAdapter(products, getContext(), order);
        btnOrderFixed.setText("החזרה לטיפול");
        btnOrderFixed.setBootstrapBrand(DefaultBootstrapBrand.WARNING);
        btnReturn.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
        rvItems.setAdapter(adapter);
        rvItems.setLayoutManager(new LinearLayoutManager(getContext()));
        tvCity.setText(order.getCity());
        tvDate.setText(order.getFutureDate());
        tvFloorNum.setText(order.getFloorNumber());
        tvFullAdress.setText(order.getAddress() + " " + order.getHouseNumber());
        tvFullName.setText(order.getFullName());
        tvHour.setText(order.getFutureHour());
        tvOrderNumber.setText("הזמנה: " + order.getOrderNumber());
        tvOrderTime.setText(order.getOrderTime());
        tvPhoneNumber.setText(order.getPhoneNumber());
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.btnOrderFixed)
    public void onBtnOrderFixedClicked() {
        orderUID = order.getOrderUID();
        FirebaseDatabase.getInstance().getReference("Orders").child("Fixed").child(orderUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    Order val = dataSnapshot.getValue(Order.class);
                    FirebaseDatabase.getInstance().getReference("Orders").child("New").child(orderUID).setValue(val).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            FirebaseDatabase.getInstance().getReference("Orders").child("Fixed").child(orderUID).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                    builder.setMessage("ההזמנה הוחזרה לטיפול!");
                                    builder.show().setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialogInterface) {
                                            dismiss();
                                        }
                                    });
                                }
                            });
                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @OnClick(R.id.btnReturn)
    public void onBtnReturnClicked() {
        dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder> {

        ArrayList<Product> products;
        Context context;
        LayoutInflater inflater;
        Order order;
        Order newOrder;
        Product newProduct;

        public ProductListAdapter(ArrayList<Product> products, Context context, Order order) {
            this.products = products;
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.order = order;
        }

        @Override
        public ProductListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = inflater.inflate(R.layout.product_item, parent, false);
            return new ProductListViewHolder(v);
        }

        @Override
        public void onBindViewHolder(final ProductListViewHolder holder, int position) {
            final Product product = products.get(position);
            holder.tvProductName.setText(product.getFood().getName());
            if (product.isHasAddon()) {
                holder.tvAddon.setText("תוספת ביצה");
            } else {
                holder.tvAddon.setText("ללא תוספת");
            }
            if (product.getFood().getName().equals("בקבוק שתיה 1.5 ל׳")) {
                holder.tvProductName.setText(product.getNotes());
            }
            boolean isFixed = product.isFixed();
            holder.tvQty.setText(String.valueOf("כמות: " + product.getQty()));
            if (isFixed) {
                holder.cbDone.setChecked(true);
                holder.itemView.setBackgroundColor(Color.argb(50, 0, 200, 0));
            } else {
                holder.cbDone.setChecked(false);
                holder.itemView.setBackgroundColor(Color.argb(50, 200, 0, 0));
            }
            if (product.getFood().getName().equals("בקבוק שתיה 1.5 ל׳")) {
                holder.tvAddon.setText(order.getNotes());
            }
            holder.cbDone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        newProduct = new Product(product.getFood(), product.isHasAddon(), product.getProductPrice(), true, product.getNotes(), product.getQty());
                        products.set(holder.getAdapterPosition(), newProduct);
                        newOrder = new Order(order.getFullName(), order.getPhoneNumber(), order.getAddress(), order.getHouseNumber(), order.getAptNumber(), order.getFloorNumber(), order.getCity(), order.getFutureHour(), order.getFutureDate(), order.getOrderTime(), order.getNotes(), products, order.getTotalPrice(), order.getOrderUID(), order.getOrderNumber());
                        FirebaseDatabase.getInstance().getReference("Orders").child("New").child(order.getOrderUID()).setValue(newOrder);
                        holder.itemView.setBackgroundColor(Color.argb(50, 0, 200, 0));
                    } else {
                        newProduct = new Product(product.getFood(), product.isHasAddon(), product.getProductPrice(), false, product.getNotes(), product.getQty());
                        products.set(holder.getAdapterPosition(), newProduct);
                        newOrder = new Order(order.getFullName(), order.getPhoneNumber(), order.getAddress(), order.getHouseNumber(), order.getAptNumber(), order.getFloorNumber(), order.getCity(), order.getFutureHour(), order.getFutureDate(), order.getOrderTime(), order.getNotes(), products, order.getTotalPrice(), order.getOrderUID(), order.getOrderNumber());
                        FirebaseDatabase.getInstance().getReference("Orders").child("New").child(order.getOrderUID()).setValue(newOrder);
                        holder.itemView.setBackgroundColor(Color.argb(50, 200, 0, 0));
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return products.size();
        }

        public class ProductListViewHolder extends RecyclerView.ViewHolder {

            TextView tvProductName;
            TextView tvAddon;
            CheckBox cbDone;
            TextView tvQty;

            public ProductListViewHolder(View itemView) {
                super(itemView);
                tvProductName = (TextView) itemView.findViewById(R.id.tvProductName);
                tvAddon = (TextView) itemView.findViewById(R.id.tvAddon);
                cbDone = (CheckBox) itemView.findViewById(R.id.cbDone);
                tvQty = (TextView) itemView.findViewById(R.id.tvQty);
            }
        }
    }

}
