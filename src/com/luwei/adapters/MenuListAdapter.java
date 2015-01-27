package com.luwei.adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.luwei.domain.UserInfo;
import com.luwei.potato.R;
import com.luwei.ui.util.LruImageCache;

public class MenuListAdapter extends BaseAdapter {
	Context context;
	String[] mTitle;
	int[] mCounter;
	int[] mIcon;
	LayoutInflater inflater;
	String avatar_url;
	String nick_name;
    String city;
    String province;
	NetworkImageView networkImageView;
	public MenuListAdapter(Context context, String[] mTitle, int[] mCounter,
			int[] mIcon, UserInfo profileInfo) {
		super();
        if(profileInfo!=null){
            this.nick_name = profileInfo.getNick_name();
            this.avatar_url = profileInfo.getAvatar_url();
            this.city = profileInfo.getCity();
            this.province = profileInfo.getProvince();
        }
		this.context = context;
		this.mTitle = mTitle;
		this.mCounter = mCounter;
		this.mIcon = mIcon;
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mTitle.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mTitle[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView txtTitle;
		TextView txtCounter;
		ImageView imgIcon;
		View itemView = null;
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		if (position == 0) {
			itemView = inflater
					.inflate(R.layout.profile_coustom, parent, false);
			networkImageView = (NetworkImageView) itemView.findViewById(R.id.avatar_vollay);
            TextView userPosition = (TextView) itemView.findViewById(R.id.profile_position);
			if (avatar_url != null) {
				  
				RequestQueue mQueue = Volley.newRequestQueue(context);  
				  
				LruImageCache lruImageCache = LruImageCache.instance();  
				  
				ImageLoader imageLoader = new ImageLoader(mQueue,lruImageCache);
				
				networkImageView.setDefaultImageResId(R.drawable.avatar_male);  
				networkImageView.setErrorImageResId(R.drawable.avatar_male);          
				networkImageView.setImageUrl(avatar_url, imageLoader); 
			}else{
				networkImageView.setDefaultImageResId(R.drawable.avatar_male);  
			}
			if (nick_name != null) {
				TextView nickname = (TextView) itemView
						.findViewById(R.id.profile_nickname);
				nickname.setText(nick_name);
			}
            if(city!=null && province!=null){
                userPosition.setText("位置:"+province+"-"+city);
            }
		} else if(position ==1){
			itemView = inflater.inflate(R.layout.listitem_function, parent,
					false);
		}else if(position ==5){
			itemView = inflater.inflate(R.layout.listitem_production, parent,
					false);
		}
		else {
			itemView = inflater.inflate(R.layout.drawer_list_item, parent,
					false);

			txtTitle = (TextView) itemView.findViewById(R.id.title);
			txtCounter = (TextView) itemView.findViewById(R.id.counter);
			imgIcon = (ImageView) itemView.findViewById(R.id.icon);

			txtTitle.setText(mTitle[position]);
			if(position==3||position==6){
				if(mCounter[position]<10){
					txtCounter.setText("0"+mCounter[position] + "");
				}else{
					txtCounter.setText(mCounter[position] + ""); // 注意下int和R资源的问题
				}
			}
			imgIcon.setImageResource(mIcon[position]);
		}

		return itemView;
	}
	
	/**
	 * 禁止第0，1行的点击事件
	 */
	@Override
    public boolean isEnabled(int position) 
    {
         if(position==0|| position==1||position==5){
        	 return false;
         }
         return true;
     }


    public boolean areAllItemsEnabled() 
    {
        return false;
    }
	
}
