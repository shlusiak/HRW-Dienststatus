package de.saschahlusiak.hrw.dienststatus;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DetailServiceAdapter extends BaseAdapter {
	LayoutInflater inflater;
	ArrayList<HRWNode.Service> services;
	
	public DetailServiceAdapter(Context context, ArrayList<HRWNode.Service> services) {
		inflater = LayoutInflater.from(context);
		this.services = services;
	}

	@Override
	public int getCount() {
		return services.size();
	}

	@Override
	public Object getItem(int position) {
		return services.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		TextView name, output;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		HRWNode.Service service = (HRWNode.Service) getItem(position);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.service_detail_item, parent,
					false);

			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.output = (TextView) convertView.findViewById(R.id.output);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (service.name != null)
			holder.name.setText("<" + service.name + ">");
		else
			holder.name.setText("<Host>");
		
		holder.output.setText(service.output);
		convertView.setBackgroundColor(Color.argb(40, 255, 0, 0));

		return convertView;
	}

}
