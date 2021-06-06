package com.example.myapplication;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.utils.options.Option12Activity;


public class ChannelSelect_Fragment extends Fragment {
    int[] btn={R.id.btn_12,R.id.btn_20,R.id.btn_40};
    Button btn_12,btn_20,btn_40;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //
        View view = inflater.inflate(R.layout.channelselect_fragment,container,false);
        int[] color=getResources().getIntArray(R.array.bgcolor);
        for (int i=0;i<3;i++){
            Button bt=(Button)view.findViewById(btn[i]);
            bt.setBackgroundColor(color[i]);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //12通道监听
        btn_12= (Button) getActivity().findViewById(R.id.btn_12);
        btn_12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),TesterActivity.class);
                intent.putExtra("channel","12通道");
                startActivity(intent);
            }
        });
        //20通道监听
        btn_20= (Button) getActivity().findViewById(R.id.btn_20);
        btn_20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),TesterActivity.class);
                intent.putExtra("channel","20通道");
                startActivity(intent);
            }
        });
        //40通道监听
        btn_40= (Button) getActivity().findViewById(R.id.btn_40);
        btn_40.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(),TesterActivity.class);
                intent.putExtra("channel","40通道");
                startActivity(intent);
            }
        });
    }

}
