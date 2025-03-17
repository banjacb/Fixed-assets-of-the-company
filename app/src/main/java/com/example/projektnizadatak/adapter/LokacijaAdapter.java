package com.example.projektnizadatak.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.projektnizadatak.R;
import com.example.projektnizadatak.model.Lokacija;
import com.example.projektnizadatak.model.OsnovnoSredstvo;


import java.util.List;

public class LokacijaAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private Activity activity;
    private List<Lokacija> lista;

    public LokacijaAdapter(Activity activity, List<Lokacija> lista)
    {
        this.activity=activity;
        this.lista=lista;
    }

    public void setLista(List<Lokacija> lista) {
        this.lista = lista;
    }

    @Override
    public Object getItem(int position) {
        return lista.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(inflater==null)
        {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.lista, null);
        }
        TextView tvTitle = convertView.findViewById(R.id.tv_title);
        TextView tvContent = convertView.findViewById(R.id.tv_content);
        ImageView ivImage=convertView.findViewById(R.id.iv_image);


        Lokacija item = lista.get(position);
        tvTitle.setText(item.getGrad());
        tvContent.setText(String.valueOf(item.getPostanskiBroj()));
        ivImage.setImageResource(R.drawable.ic_location);
        return convertView;


    }
}
