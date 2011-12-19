package de.saschahlusiak.hrw.dienststatus;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DienststatusAdapter extends BaseAdapter {
	LayoutInflater inflater;
	ArrayList<HRWNode> nodes;

	public DienststatusAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		nodes = new ArrayList<HRWNode>();
	}

	public void clear() {
		nodes.clear();
	}

	public void addNode(HRWNode node) {
		nodes.add(node);
	}

	@Override
	public int getCount() {
		return nodes.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return nodes.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	static class ViewHolder {
		TextView name, status, label;
		ImageView icon;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		HRWNode node = (HRWNode) getItem(position);
		Context context = parent.getContext();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.statuslist_item, parent,
					false);

			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.status = (TextView) convertView.findViewById(R.id.status);
			holder.label = (TextView) convertView.findViewById(R.id.label);
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (node.hasSubItems) {
			holder.name.setTypeface(Typeface.DEFAULT_BOLD);
		} else {
			holder.name.setTypeface(Typeface.DEFAULT);
		}

		holder.name.setText(node.name);

		holder.label.setVisibility(View.VISIBLE);
		if (node.title != null)
			holder.label.setText(node.title);
		else if (node.duration != null)
			holder.label.setText("Seit " + node.duration);
		else if (node.url != null) {
			holder.label.setText(node.url);
		} else
			holder.label.setVisibility(View.INVISIBLE);

		holder.status.setText(node.getStatusText());
		switch (node.status) {
		case 0:
			convertView.setBackgroundColor(Color.TRANSPARENT);

			holder.name.setTextColor(Color.WHITE);
			holder.status.setTextColor(Color.LTGRAY);
			if (holder.label != null)
				holder.label.setTextColor(Color.rgb(128, 128, 128));
			break;
		case 1:
			convertView.setBackgroundColor(Color.TRANSPARENT);

			holder.status.setTextColor(Color.DKGRAY);
			holder.name.setTextColor(Color.DKGRAY);
			if (holder.label != null)
				holder.label.setTextColor(Color.DKGRAY);
			break;
		case 2:
			if (node.acknowledged) {
				convertView
						.setBackgroundColor(Color.argb(40, 0xef, 0xdf, 0x5f));

				holder.status.setTextColor(Color.LTGRAY);
				holder.name.setTextColor(Color.LTGRAY);
				if (holder.label != null)
					holder.label.setTextColor(Color.LTGRAY);
			} else {
				convertView.setBackgroundColor(Color.rgb(0xff, 0xd7, 0x5f));
				holder.status.setTextColor(Color.BLACK);
				holder.name.setTextColor(Color.BLACK);
				if (holder.label != null)
					holder.label.setTextColor(Color.DKGRAY);
			}

			break;
		case 3:
			convertView.setBackgroundColor(Color.rgb(0xff, 0x57, 0x57));

			holder.name.setTextColor(Color.BLACK);
			holder.status.setTextColor(Color.BLACK);
			if (holder.label != null)
				holder.label.setTextColor(Color.DKGRAY);

			break;

		case -1:
		default:
			convertView.setBackgroundColor(Color.TRANSPARENT);
			// convertView.setBackgroundColor(Color.BLACK);

			holder.status.setTextColor(Color.DKGRAY);
			holder.name.setTextColor(Color.DKGRAY);
			if (holder.label != null)
				holder.label.setTextColor(Color.DKGRAY);
			break;
		}

		if (holder.icon != null) {
			if (node.url != null) {
				holder.icon.setImageResource(R.drawable.external);
				holder.icon.setVisibility(View.VISIBLE);
			} else {
				holder.icon.setVisibility(View.INVISIBLE);
			}
		}

		return convertView;
	}

}
