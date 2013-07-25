package com.abir.photolotto;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

public class EventAdapter extends ArrayAdapter<EventModel> {
	private List<EventModel>	mListItems;
	private List<EventModel>	mFilteredItems;
	
	private Activity			mContext;
	public ImageLoader			imageLoader;

	private Filter filter;
	
	static class ViewHolder {
		public ImageView	imageViewEvent;
		public TextView		textViewHeading;
		public TextView		textViewDescription;
		public TextView		textViewDescription2;
	}

	private List<EventModel> cloneList(List<EventModel> list)
	{
		List<EventModel> clone = new ArrayList<EventModel>(list.size());
		for (EventModel item :list)
			try {
				clone.add((EventModel)(item.clone()));
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		
		return clone;
	}

	
    @Override
	public int getCount() {

		return mFilteredItems.size();
	}


    
	@Override
	public EventModel getItem(int position) {

		return mFilteredItems.get(position);
	}

	
	@Override
	public long getItemId(int position) {

		return position;
	}


	public EventAdapter(Activity context, List<EventModel> list) {
		super(context, R.layout.row_layout, list);
		this.mListItems = list;
		this.mFilteredItems = list;
		
		this.mContext = context;
		imageLoader = new ImageLoader(context.getApplicationContext());
		Log.d("1---------------------------", String.valueOf(mListItems.size()) + ":" + mFilteredItems.size());
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = null;
		if (convertView == null) {
			LayoutInflater inflater = mContext.getLayoutInflater();
			rowView = inflater.inflate(R.layout.row_layout, null);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.imageViewEvent = (ImageView) rowView.findViewById(R.id.imageViewEvent);
			viewHolder.textViewHeading = (TextView) rowView.findViewById(R.id.textViewHeading);
			viewHolder.textViewDescription = (TextView) rowView.findViewById(R.id.textViewDescription);
			viewHolder.textViewDescription2 = (TextView) rowView.findViewById(R.id.textViewDescription2);
			
			rowView.setTag(viewHolder);
		} else {
			rowView = convertView;
		}
		ViewHolder holder = (ViewHolder) rowView.getTag();
		imageLoader.displayImage(mFilteredItems.get(position).getsImgThumb(), holder.imageViewEvent);
		holder.textViewHeading.setText(mFilteredItems.get(position).getsName());
		holder.textViewDescription.setText(mFilteredItems.get(position).getsShortDescription1());
		holder.textViewDescription2.setText(mFilteredItems.get(position).getsShortDescription2());
		return rowView;
	}
	


    @Override
	public android.widget.Filter getFilter() {

         if(filter == null)
             filter = new EventFilter();
		return filter;
	}

	private class EventFilter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // NOTE: this function is *always* called from a background thread, and
            // not the UI thread.
            String sConstraint = constraint.toString().toLowerCase();
            
            Log.d("3:---------------------------", String.valueOf(mListItems.size()) + ":" + mFilteredItems.size());
            
            FilterResults result = new FilterResults();
            ArrayList<EventModel> filt = new ArrayList<EventModel>();
            
            if(constraint != null && sConstraint.length() > 0)
            {
                for(int i = 0, l = mListItems.size(); i < l; i++)
                {
                    EventModel m = mListItems.get(i);
                    if(m.getsName().toLowerCase().contains(sConstraint) ||
                		m.getsShortDescription1().toLowerCase().contains(sConstraint) ||
                		m.getsShortDescription2().toLowerCase().contains(sConstraint))
						try {
							filt.add((EventModel)m.clone());
						} catch (CloneNotSupportedException e) {
							e.printStackTrace();
						}
                }
                result.count = filt.size();
                result.values = filt;
            }
            else
            {
                synchronized(this)
                {
                    result.values = cloneList(mListItems);
                    result.count = mListItems.size();
                }
            }
            return result;
        }

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {
			mFilteredItems = (ArrayList<EventModel>)(results.values);
			notifyDataSetChanged();
		}
    }
}
