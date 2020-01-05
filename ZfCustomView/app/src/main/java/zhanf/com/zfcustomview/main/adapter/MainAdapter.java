package zhanf.com.zfcustomview.main.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import zhanf.com.zfcustomview.R;
import zhanf.com.zfcustomview.widget.SelectorTextView;

/**
 * Created by Administrator on 2017/8/25.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private Activity context;
    private ArrayList<Class<? extends AppCompatActivity>> activityList;


    public ArrayList getActivityList() {
        return activityList;
    }

    public void setActivityList(ArrayList<Class<? extends AppCompatActivity>> activityList) {
        this.activityList = activityList;
    }

    public void addActivityList(Class<? extends AppCompatActivity> clazz) {
        if (null == activityList) {
            activityList = new ArrayList<>();
        }
        activityList.add(clazz);
        notifyItemChanged(activityList.size());
    }

    public MainAdapter(Activity context) {
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_main, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final Class<? extends AppCompatActivity> clazz = activityList.get(position);
        holder.tvMainItem.setText(clazz.getSimpleName());

        holder.tvMainItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, clazz);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == activityList ? 0 : activityList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMainItem;
        SelectorTextView tvMainItem;

        ViewHolder(View itemView) {
            super(itemView);
            ivMainItem = itemView.findViewById(R.id.iv_main_item);
            tvMainItem = itemView.findViewById(R.id.tv_main_item);
        }
    }
}
