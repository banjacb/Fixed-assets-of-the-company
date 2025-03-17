package com.example.projektnizadatak.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.projektnizadatak.R;
import com.example.projektnizadatak.model.OsnovnoSredstvo;

import java.util.List;

public class OsnovnoSredstvoAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private Activity activity;
    private List<OsnovnoSredstvo> lista;


    public OsnovnoSredstvoAdapter(Activity activity, List<OsnovnoSredstvo> lista)
    {
        this.activity=activity;
        this.lista=lista;
    }

    public void setLista(List<OsnovnoSredstvo> novaLista) {
         this.lista=novaLista;
         notifyDataSetChanged();
       // this.lista.clear();
        //this.lista.addAll(novaLista);
       //notifyDataSetChanged();
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

        OsnovnoSredstvo item = lista.get(position);


        tvTitle.setText(item.getNaizv());
        tvContent.setText(String.valueOf(item.getBarkod()));

        byte[] imageBytes = item.getSlika();
        if (imageBytes != null) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            ivImage.setImageBitmap(bitmap);
        } else {
            ivImage.setImageResource(R.drawable.ic_assets);
        }


        return convertView;


    }
}
