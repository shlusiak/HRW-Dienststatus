package de.saschahlusiak.hrw.dienststatus;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class StatisticsAdapter extends BaseAdapter {
	LayoutInflater inflater;
	ArrayList<Statistic> items;
	
	private class Statistic {
		Drawable d = null;
		boolean valid = false;
	}

	public StatisticsAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		items = new ArrayList<Statistic>();
	}

	@Override
	public int getCount() {
		return items.size();
	}
	
	public void clear() {
		items.clear();
	}
	
	public void invalidate(int max) {
		if (items.size() < max) {
			int size = items.size();
			for (int i=0; i < max - size; i++)
			{
				Statistic s = new Statistic();
				s.valid = false;
				s.d = null;
				items.add(s);
			}
		}
		for (Statistic s: items) {
			s.valid = false;
		}
	}
	
	public void add(Drawable d, int i) {
		Statistic s;
		if (i < items.size())
			s = items.get(i);
		else {
			s = new Statistic();
			items.add(s);
		}
		
		s.d = d;
		s.valid = (d != null);
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		ImageView image;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		Statistic s;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.statistics_item, parent,
					false);

			holder = new ViewHolder();
			holder.image = (ImageView) convertView.findViewById(R.id.image);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		s = items.get(position);

//		convertView.setMinimumHeight(500);
		if (s.d != null) {
			BitmapDrawable b = (BitmapDrawable)s.d;
			b.setTargetDensity(b.getBitmap().getDensity());
			holder.image.setImageDrawable(s.d);
			holder.image.setMaxHeight(10000);
//			holder.image.setMinimumWidth(500);
			holder.image.setMinimumHeight(0);
		} else {
			holder.image.setImageResource(R.drawable.hrw_logo);
			holder.image.setMinimumHeight(150);
			holder.image.setMaxHeight(150);
		}
		if (s.valid)
			holder.image.setAlpha(255);
		else
			holder.image.setAlpha(120);
		
		return convertView;
	}

}
