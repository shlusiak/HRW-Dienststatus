package de.saschahlusiak.hrw.dienststatus.news;

import java.util.ArrayList;

import de.saschahlusiak.hrw.dienststatus.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NewsAdapter extends BaseAdapter {
	ArrayList<NewsItem> nodes;
	LayoutInflater inflater;
	
	NewsAdapter(Context context) {
		inflater = LayoutInflater.from(context);
	}

	void setNodes(ArrayList<NewsItem> nodes) {
		this.nodes = nodes;
	}
	
	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}
	
	@Override
	public int getCount() {
		if (nodes == null)
			return 0;
		return nodes.size();
	}

	@Override
	public Object getItem(int position) {
		if (nodes == null)
			return null;
		return nodes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	private class ViewHolder {
		TextView header, title;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		NewsItem news = (NewsItem) getItem(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.news_list_item, parent,
					false);

			holder = new ViewHolder();
			
			holder.header = (TextView) convertView.findViewById(R.id.header);
			holder.title = (TextView) convertView.findViewById(R.id.title);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (news.title.length() <= 1) {
//			holder.header.setVisibility(View.GONE);
			holder.header.setText(news.header);
			holder.title.setText(news.header);
		} else {
//			holder.header.setVisibility(View.VISIBLE);
			holder.header.setText(news.header);
			holder.title.setText(news.title);
		}
		
		return convertView;
	}
	
}
