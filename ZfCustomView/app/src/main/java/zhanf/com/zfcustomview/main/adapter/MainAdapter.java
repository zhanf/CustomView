package zhanf.com.zfcustomview.main.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import zhanf.com.zfcustomview.R;

/**
 * Created by Administrator on 2017/8/25.
 */

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {

    private Activity context;
    private ArrayList<String> activityList;


    public ArrayList getActivityList() {
        return activityList;
    }

    public void setActivityList(ArrayList<String> activityList) {
        this.activityList = activityList;
    }

    public void addActivityList(String clazz) {
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
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_main, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final String clazz = activityList.get(position);
        holder.tvMainItem.setText(clazz);

        holder.tvMainItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class<? extends AppCompatActivity> aClass = null;
                try {
                    aClass = (Class<? extends AppCompatActivity>) Class.forName(clazz);
                    Intent intent = new Intent(context, aClass);
                    context.startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return null == activityList ? 0 : activityList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_main_item)
        ImageView ivMainItem;
        @BindView(R.id.tv_main_item)
        TextView tvMainItem;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
