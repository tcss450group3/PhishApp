package tcss450.uw.edu.phishappwolfr1;

import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tcss450.uw.edu.phishappwolfr1.SetListFragment.OnListFragmentInteractionListener;
import tcss450.uw.edu.phishappwolfr1.Content.SetList;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link SetList} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 */
public class MySetListRecyclerViewAdapter extends RecyclerView.Adapter<MySetListRecyclerViewAdapter.ViewHolder> {

    private final List<SetList> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MySetListRecyclerViewAdapter(List<SetList> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_setlist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mLocation.setText(mValues.get(position).getLocation());
        holder.mShowDate.setText(mValues.get(position).getShowDate());
        holder.mVenue.setText(Html.fromHtml(mValues.get(position).getVenue()).toString());



        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        private final TextView mShowDate;
        private final TextView mLocation;
        private final TextView mVenue;
        public SetList mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mShowDate = (TextView) view.findViewById(R.id.showDate);
            mLocation = (TextView) view.findViewById(R.id.showLocation);
            mVenue = (TextView) view.findViewById(R.id.showVenue);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mLocation.getText() + " " + mVenue.toString() + "'";
        }
    }
}
