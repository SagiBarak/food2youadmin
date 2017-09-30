package com.sagib.food2youadmin;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sagib.food2youadmin.models.Order;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeOrderNumberFragment extends BottomSheetDialogFragment {

    @BindView(R.id.tvCurrent)
    TextView tvCurrent;
    @BindView(R.id.etCustom)
    EditText etCustom;
    @BindView(R.id.btnCustomChange)
    BootstrapButton btnCustomChange;
    Unbinder unbinder;
    int newCounter;
    int fixedCounter;
    int totalOrders;
    @BindView(R.id.tvTotal)
    TextView tvTotal;
    ArrayList<Integer> orderNumbers = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_order_number, container, false);
        unbinder = ButterKnife.bind(this, v);
        FirebaseDatabase.getInstance().getReference("currentOrderUID").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tvCurrent.setText(String.valueOf(dataSnapshot.getValue(Long.class)));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference("Orders").child("New").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    newCounter++;
                    totalOrders = newCounter + fixedCounter;
                    tvTotal.setText(String.valueOf(totalOrders));
                    Order value = dataSnapshot.getValue(Order.class);
                    orderNumbers.add(Integer.valueOf(value.getOrderNumber()));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        FirebaseDatabase.getInstance().getReference("Orders").child("Fixed").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    fixedCounter++;
                    totalOrders = newCounter + fixedCounter;
                    tvTotal.setText(String.valueOf(totalOrders));
                    Order value = dataSnapshot.getValue(Order.class);
                    orderNumbers.add(Integer.valueOf(value.getOrderNumber()));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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

    @OnClick(R.id.btnCustomChange)
    public void onBtnCustomChangeClicked() {
        int newOrderNumber = Integer.valueOf(etCustom.getText().toString());
        if (orderNumbers.contains(newOrderNumber)) {
            etCustom.setError("ישנה הזמנה בעלת המספר הנ״ל");
        } else {
            btnCustomChange.setClickable(false);
            btnCustomChange.setBootstrapBrand(DefaultBootstrapBrand.REGULAR);
            btnCustomChange.setText("מעדכן");
            FirebaseDatabase.getInstance().getReference("currentOrderUID").setValue(newOrderNumber).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        dismiss();
                    }
                }
            });
        }
    }
}
