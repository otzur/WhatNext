package learn2crack.adapters;

/**
 * Created by otzur on 5/26/2015.
 */
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.List;

import learn2crack.chat.R;
import learn2crack.models.WnMessageRowOption;

public class WnMessageRowOptionArrayAdapter extends ArrayAdapter<WnMessageRowOption> {

    private final List<WnMessageRowOption> list;
    private final Activity context;
    private int maxSelectedOptionsNumber = 0;
    private int currentSelectedOptionsNumber = 0;

    public WnMessageRowOptionArrayAdapter(Activity context, List<WnMessageRowOption> list, int maxSelectedOptionsNumber) {
        super( context, R.layout.message_row_option_layout, list);
        this.context = context;
        this.list = list;
        this.maxSelectedOptionsNumber = maxSelectedOptionsNumber;
    }



    static class ViewHolder {
        /*protected TextView text;
        protected CheckBox checkbox;*/
        Button button;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.message_row_option_layout, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.button = (Button) view.findViewById(R.id.label);
            viewHolder.button.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View button) {
                            if(!button.isSelected()){
                                if(maxSelectedOptionsNumber == currentSelectedOptionsNumber){ //can't select anymore options
                                    return;
                                }
                                 //Set the button's appearance
                                 //button.setSelected(!button.isSelected());
                                button.setSelected(true);
                                 WnMessageRowOption element = (WnMessageRowOption) viewHolder.button
                                         .getTag();
                                //if (button.isSelected()) {
                                element.setSelected(true);
                                currentSelectedOptionsNumber++;
                                //}
                               // else {
                                //    element.setSelected(false);
                               // }
                            }
                            else{
                                button.setSelected(false);
                                WnMessageRowOption element = (WnMessageRowOption) viewHolder.button
                                        .getTag();
                                //if (button.isSelected()) {
                                element.setSelected(false);
                                currentSelectedOptionsNumber --;
                            }
                }});
            view.setTag(viewHolder);
            viewHolder.button.setTag(list.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).button.setTag(list.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.button.setText(list.get(position).getName());
        holder.button.setSelected(list.get(position).isSelected());
        return view;
    }
}
