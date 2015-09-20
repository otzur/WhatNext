package learn2crack.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import learn2crack.chat.R;
import learn2crack.models.WnMessage;

public class HistoryRVAdapter extends RecyclerView.Adapter<HistoryRVAdapter.HistoryViewHolder> {

    private static HistoryItemClickListener historyItemClickListener;

    public static class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        CardView cv;
        TextView contactName;
        TextView status;
        ImageView contactPhoto;
        TextView datetime;
        TextView options;
        TextView tab;


        HistoryViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            contactName = (TextView)itemView.findViewById(R.id.contact_name);
            status = (TextView)itemView.findViewById(R.id.status);
            contactPhoto = (ImageView)itemView.findViewById(R.id.contact_photo);
            datetime = (TextView)itemView.findViewById(R.id.datetime);
            datetime = (TextView)itemView.findViewById(R.id.datetime);
            options = (TextView)itemView.findViewById(R.id.options);
            tab = (TextView)itemView.findViewById(R.id.tab);
            //ImageView imageView = (ImageView) findViewById(R.id.img_contact);
            //imageView.setImageBitmap(photo);.

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            historyItemClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(HistoryItemClickListener historyItemClickListener){
        HistoryRVAdapter.historyItemClickListener = historyItemClickListener;
    }

    List<WnMessage> messages;

    public HistoryRVAdapter(List<WnMessage> messages){
        this.messages = messages;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.message_cradview, viewGroup, false);
        return new HistoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder historyViewHolder, int i) {
        //personViewHolder.contactName.setText(messages.get(i).getUser());
        String status = messages.get(i).getStatus();
        String display_status;
        historyViewHolder.contactName.setText("Move to conversation");
        historyViewHolder.datetime.setText(messages.get(i).getDelivery_date());
        historyViewHolder.options.setText("Options: " + messages.get(i).getOption_selected());
        historyViewHolder.tab.setText("Tab = Move to conversation");
        //historyViewHolder.contactPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
        historyViewHolder.contactPhoto.setImageBitmap(messages.get(i).getUser_photo());

        //messages.get(i).setUser_photo(Contacts.retrieveContactPhoto(, messages.get(i).getUser()));

        switch (status) {
            case "New":
                display_status = "New WN received- Waiting to your response";
                break;
            case "Sent":
                display_status = "WN Sent- Waiting for other to response";
                break;
            case "Response":
                display_status = "WN response- Waiting for results";
                break;
            case "Results":
                display_status = "Results inside";
                break;
            default :
                display_status = "default";
                break;

        }
        historyViewHolder.status.setText(display_status);
        //personViewHolder.contactPhoto.setImageResource(messages.get(i).photoId);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public interface HistoryItemClickListener{
        void onItemClick(int position, View v);

    }
}
