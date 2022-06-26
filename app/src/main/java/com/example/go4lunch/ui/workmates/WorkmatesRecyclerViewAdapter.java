package com.example.go4lunch.ui.workmates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.go4lunch.ui.MainActivityViewModel;
import com.example.go4lunch.R;
import com.example.go4lunch.utils.RetrieveIdRestaurant;
import com.example.go4lunch.model.User;

import java.util.List;


public class WorkmatesRecyclerViewAdapter extends RecyclerView.Adapter<WorkmatesRecyclerViewAdapter.ViewHolder> {

    private List<User> users;
    private RetrieveIdRestaurant listener;
    private MainActivityViewModel mainActivityViewModel;
    private String goToEat;
    private Context context;

    public WorkmatesRecyclerViewAdapter(List<User> users, Context context, MainActivityViewModel mainActivityViewModel, RetrieveIdRestaurant listener) {
        this.users = users;
        this.listener = listener;
        this.mainActivityViewModel = mainActivityViewModel;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_workmates_item, parent, false);
        goToEat = context.getResources().getString(R.string.eat_at);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.workMatesName.setText(user.getName()+ " ");
        if (user.getRestaurantName() != null){
            holder.workMatesStatus.setText(" "+goToEat +" " +user.getRestaurantName());
        }

        Glide.with(holder.roundView.getContext())
                .load(user.getUrlPicture())
                .apply(RequestOptions.circleCropTransform())
                .into(holder.roundView);

        holder.itemView.setOnClickListener(v -> {
            if (user.getRestaurantPlaceId() != null) {
                listener.onClickItem(user.getRestaurantPlaceId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        private ImageView roundView;
        private TextView workMatesName;
        private TextView workMatesStatus;


        public ViewHolder(View view) {
            super(view);
            roundView = view.findViewById(R.id.imageViewWorkmatesViewHolder);
            workMatesName = view.findViewById(R.id.textViewWorkmatesViewHolderName);
            workMatesStatus = view.findViewById(R.id.textViewWorkmatesViewHolderStatus);

        }
    }
}

