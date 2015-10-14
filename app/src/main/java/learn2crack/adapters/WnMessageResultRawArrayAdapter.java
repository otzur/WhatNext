package learn2crack.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import learn2crack.chat.R;
import learn2crack.models.WnMessageRowOption;

/**
 * Created by samzaleg on 8/23/2015.
 */
public class WnMessageResultRawArrayAdapter extends ArrayAdapter<WnMessageRowOption> {
    private final List<WnMessageRowOption> list;
    private final Activity context;

    public WnMessageResultRawArrayAdapter(Activity context, List<WnMessageRowOption> list) {
        super( context, R.layout.message_raw_result_layout, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView text;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            //view = inflator.inflate(R.layout.activity_listviewexampleactivity, null);
            view = inflator.inflate(R.layout.message_raw_result_layout, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.label);
            view.setTag(viewHolder);
            //viewHolder.checkbox.setTag(list.get(position));
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(list.get(position).getName());
        if(list.get(position).isSelected()){
            holder.text.setTextSize(30);
        }
        return view;
    }
}
