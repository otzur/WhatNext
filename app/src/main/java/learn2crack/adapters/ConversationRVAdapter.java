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
import learn2crack.models.WnConversation;

/**
 * Created by otzur on 9/16/2015.
 */
public class ConversationRVAdapter extends RecyclerView.Adapter<ConversationRVAdapter.ConversationViewHolder> {

    private static ConversationItemClickListener conversationItemClickListener;

    public void setOnItemClickListener(ConversationItemClickListener conversationItemClickListener){
        ConversationRVAdapter.conversationItemClickListener = conversationItemClickListener;
    }


    List<WnConversation> conversations;
    public ConversationRVAdapter(List<WnConversation> conversations) {
        this.conversations = conversations;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.conversation_cardview, viewGroup, false);
        return new ConversationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ConversationViewHolder conversationViewHolder, int i) {
        conversationViewHolder.contactName.setText(conversations.get(i).getUser_name());
//        conversationViewHolder.datetime.setText(conversations.get(i).getDelivery_date());
//        conversationViewHolder.options.setText("Options: " + conversations.get(i).getOption_selected());
//        conversationViewHolder.tab.setText("Tab = " + conversations.get(i).getTab());
//        //historyViewHolder.contactPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
        conversationViewHolder.contactPhoto.setImageBitmap(conversations.get(i).getUser_photo());

    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    public interface ConversationItemClickListener{
        void onItemClick(int position, View v);

    }

    public static class ConversationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CardView cv;
        TextView contactName;
        //TextView status;
        ImageView contactPhoto;
        //TextView datetime;
        //TextView options;
        //TextView tab;
        public ConversationViewHolder(View itemView) {
            super(itemView);

            cv = (CardView)itemView.findViewById(R.id.conversation_card_view);
            contactName = (TextView)itemView.findViewById(R.id.contact_name);
            //status = (TextView)itemView.findViewById(R.id.status);
            contactPhoto = (ImageView)itemView.findViewById(R.id.contact_photo);
            //datetime = (TextView)itemView.findViewById(R.id.datetime);
            //datetime = (TextView)itemView.findViewById(R.id.datetime);
            //options = (TextView)itemView.findViewById(R.id.options);
            //tab = (TextView)itemView.findViewById(R.id.tab);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            conversationItemClickListener.onItemClick(getAdapterPosition(), v);
        }
    }
}
