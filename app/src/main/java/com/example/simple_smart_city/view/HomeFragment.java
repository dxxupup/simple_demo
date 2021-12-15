package com.example.simple_smart_city.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.simple_smart_city.API;
import com.example.simple_smart_city.Const;
import com.example.simple_smart_city.MainActivity;
import com.example.simple_smart_city.R;
import com.example.simple_smart_city.bean_p.BannerBean;
import com.example.simple_smart_city.bean_p.ProjectBean;
import com.example.simple_smart_city.bean_p.ServerListBean;
import com.example.simple_smart_city.databinding.FragHomeBinding;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;

import java.io.IOException;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragHomeBinding binding;
    private MainActivity mainActivity;
    private final API req = Const.getReq();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainActivity = (MainActivity) view.getContext();
        initBanner();
        initServerList();
        initProject();
    }

    private void initProject() {
        binding.project.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false));
        new Thread(() -> {
            try {
                List<ProjectBean.RowsDTO> rows = req.project().execute().body().getRows();

                mainActivity.runOnUiThread(() -> {
                    binding.project.setAdapter(new RecyclerView.Adapter<ProjectHolder>() {
                        @NonNull
                        @Override
                        public ProjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            return new ProjectHolder(LayoutInflater.from(mainActivity).inflate(R.layout.item_project, parent, false));
                        }

                        @Override
                        public void onBindViewHolder(@NonNull ProjectHolder holder, int position) {
                            holder.text.setText(rows.get(position).getTitle());
                            Glide.with(mainActivity).load(Const.getIP() + rows.get(position).getCover()).apply(RequestOptions.bitmapTransform(new RoundedCorners(20))).into(holder.img);
//                Intent intent = new Intent(context, NewsActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("type", "project");
//                bundle.putSerializable("news", list.get(position));
//                intent.putExtras(bundle);
//                holder.img.setOnClickListener(v -> context.startActivity(intent));
//                holder.text.setOnClickListener(v -> context.startActivity(intent));
                        }

                        @Override
                        public int getItemCount() {
                            return rows.size();
                        }
                    });
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

    }

    private void initServerList() {
        binding.serverList.setLayoutManager(new LinearLayoutManager(mainActivity, LinearLayoutManager.HORIZONTAL, false));
        new Thread(() -> {
            try {
                List<ServerListBean.RowsDTO> serverListBean = req.serverList().execute().body().getRows();
                mainActivity.runOnUiThread(() -> binding.serverList.setAdapter(new RecyclerView.Adapter<ServerListHolder>() {
                    @NonNull
                    @Override
                    public ServerListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        return new ServerListHolder(LayoutInflater.from(mainActivity).inflate(R.layout.item_server, parent, false));
                    }

                    @Override
                    public void onBindViewHolder(@NonNull ServerListHolder holder, int position) {
                        holder.text.setText(serverListBean.get(position).getServiceName());
                        Glide.with(mainActivity).load(Const.getIP() + serverListBean.get(position).getImgUrl()).apply(RequestOptions.bitmapTransform(new CircleCrop())).into(holder.img);
                    }

                    @Override
                    public int getItemCount() {
                        return serverListBean.size();
                    }
                }));


            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void initBanner() {
        new Thread(() -> {
            List<BannerBean.RowsDTO> rows = null;
            try {
                rows = req.banner().execute().body().getRows();
            } catch (IOException e) {
                e.printStackTrace();
            }
            final List<BannerBean.RowsDTO> finalRows = rows;
            mainActivity.runOnUiThread(() -> {
                binding.banner.setAdapter(new BannerImageAdapter<BannerBean.RowsDTO>(finalRows) {
                    @Override
                    public void onBindView(BannerImageHolder holder, BannerBean.RowsDTO data, int position, int size) {
                        Glide.with(holder.itemView)
                                .load(Const.getIP() + data.getAdvImg())
                                .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                                .into(holder.imageView);
                    }
                }).addBannerLifecycleObserver(this);
            });
        }).start();
    }

    private static class ServerListHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView img;

        public ServerListHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            img = itemView.findViewById(R.id.img);
        }
    }

    private static class ProjectHolder extends RecyclerView.ViewHolder {
        TextView text;
        ImageView img;

        public ProjectHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.text);
            img = itemView.findViewById(R.id.img);
        }
    }
}
