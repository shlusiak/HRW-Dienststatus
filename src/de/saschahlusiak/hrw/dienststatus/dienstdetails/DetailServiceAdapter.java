package de.saschahlusiak.hrw.dienststatus.dienstdetails;

import java.util.ArrayList;

import de.saschahlusiak.hrw.dienststatus.R;
import de.saschahlusiak.hrw.dienststatus.model.HRWNode;
import de.saschahlusiak.hrw.dienststatus.model.HRWService;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

public class DetailServiceAdapter extends BaseExpandableListAdapter {
	LayoutInflater inflater;

	class ServiceGroup extends ArrayList<HRWService> {
		private static final long serialVersionUID = 1L;

		int status;
		ServiceGroup(int status) {
			this.status = status;
		}
	};
	
	ServiceGroup services_ok, services_warn, services_err, services_unknown;
	
	public DetailServiceAdapter(Context context, ArrayList<HRWService> services) {
		inflater = LayoutInflater.from(context);
		
		services_ok = new ServiceGroup(HRWNode.OK);
		services_warn = new ServiceGroup(HRWNode.WARNING);
		services_err = new ServiceGroup(HRWNode.ERROR);
		services_unknown = new ServiceGroup(HRWNode.UNKNOWN);

		for (HRWService service: services) {
			switch (service.status) {
			case HRWNode.OK:
				services_ok.add(service);
				break;
			case HRWNode.ERROR:
				services_err.add(service);
				break;
			case HRWNode.UNKNOWN:
			case HRWNode.UNSET:
				services_unknown.add(service);
				break;
				
			case HRWNode.WARNING:
			default:
				services_warn.add(service);
				break;
			}
		}
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return getGroup(groupPosition).size();
	}

	@Override
	public ServiceGroup getGroup(int groupPosition) {
		if (services_err.size() == 0)
			groupPosition++;
		
		if (groupPosition == 0)
			return services_err;
		
		if (services_warn.size() == 0)
			groupPosition++;
		
		if (groupPosition == 1)
			return services_warn;

		if (services_unknown.size() == 0)
			groupPosition++;
		
		if (groupPosition == 2)
			return services_unknown;
		
		if (services_ok.size() == 0)
			groupPosition++;

		if (groupPosition == 3)
			return services_ok;
		return null;
	}
	
	public int getStatusGroupPosition(int status) {
		for (int i = 0; i < getGroupCount(); i++)
			if (getGroup(i).status == status)
				return i;
		return -1;
	}

	@Override
	public int getGroupCount() {
		int c = 0;
		
		if (services_err.size() > 0)
			c++;		
		if (services_warn.size() > 0)
			c++;
		if (services_unknown.size() > 0)
			c++;
		if (services_ok.size() > 0)
			c++;
		
		return c;
	}

	@Override
	public long getGroupId(int groupPosition) {
		return getGroup(groupPosition).status;
	}
	
	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return getGroup(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}
	
	static class ViewHolder {
		TextView name, output;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {
		
		ViewHolder holder;
		HRWService service = (HRWService) getChild(groupPosition, childPosition);
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
		holder.output.setTextColor(HRWNode.getStatusTextColor(service.status));
		holder.name.setTextColor(HRWNode.getStatusTextColor(service.status));
		convertView.setBackgroundColor(HRWNode.getStatusBackgroundColor(service.status, service.acknowledged));

		return convertView;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		ViewHolder holder;
		ServiceGroup group = getGroup(groupPosition);
		
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.service_detail_group, parent, false);

			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.output = (TextView) convertView.findViewById(R.id.output);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.name.setText(HRWNode.getStatusText(group.status, false));
		
		holder.output.setText("(" + group.size() + ")");
		
		return convertView;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}
}
