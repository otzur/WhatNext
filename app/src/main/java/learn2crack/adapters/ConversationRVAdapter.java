package learn2crack.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import learn2crack.chat.R;
import learn2crack.models.WnConversation;
import learn2crack.models.WnMatch;
import learn2crack.models.WnMessageResult;
import learn2crack.models.WnMessageStatus;

/**
 * Created by $(USER) on $(DATE).
 */
public class ConversationRVAdapter extends RecyclerView.Adapter<ConversationRVAdapter.ConversationViewHolder> {

    private static int expandedPosition = -1;
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
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.conversation_cardview2, viewGroup, false);
        return new ConversationViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ConversationViewHolder conversationViewHolder, int i) {
        String display_status = "";
        WnMessageStatus status = conversations.get(i).getStatus();
        WnMessageResult wnMessageResult = conversations.get(i).getWnMessageResult();
        conversationViewHolder.contactName.setText(conversations.get(i).getContacts().get(0).getName());
        //conversationViewHolder.conversation_id.setText(conversations.get(i).getConversation_guid());
        conversationViewHolder.updateDatetime.setText(conversations.get(i).getUpdate_datetime());
//        conversationViewHolder.tab.setText("Tab = " + conversations.get(i).getTab());
//        //historyViewHolder.contactPhoto.setScaleType(ImageView.ScaleType.FIT_XY);
        conversationViewHolder.contactPhoto.setImageBitmap(conversations.get(i).getContacts().get(0).getPhoto());

        switch (status) {
            case NEW:
                display_status = "New WN message";
                conversationViewHolder.resultImage.setImageResource(R.drawable.message_new_128);
                break;
            case SENT:
                display_status = "Waiting";
                conversationViewHolder.resultImage.setImageResource(R.drawable.message_waiting);
                break;
            case RESPONSE:
                display_status = "Waiting";
                conversationViewHolder.resultImage.setImageResource(R.drawable.message_waiting);
                break;
            case RESULTS:

                //display_status = "RESULTS inside";
                if(wnMessageResult.getWnMatch() == WnMatch.FULL_MATCH){
                    conversationViewHolder.resultImage.setImageResource(R.drawable.message_full_match);
                    display_status = "You have full match";
                }

                if(wnMessageResult.getWnMatch() == WnMatch.PARTIAL_MATCH){
                    conversationViewHolder.resultImage.setImageResource(R.drawable.message_partial_match);
                    display_status = "You have partial match";
                }

                if(wnMessageResult.getWnMatch() == WnMatch.NO_MATCH){
                    conversationViewHolder.resultImage.setImageResource(R.drawable.message_no_match);
                    display_status = "No match";
                }


                break;
            default :
                display_status = "default";
                break;

        }
        conversationViewHolder.status.setText(display_status);
        conversationViewHolder.left_text_button.setText("LEFT");
        conversationViewHolder.right_text_button.setText("RIGHT");

        if ( i == expandedPosition) {
            if(conversationViewHolder.llExpandArea.getVisibility() == View.VISIBLE )
                conversationViewHolder.llExpandArea.setVisibility(View.GONE);
            else
                conversationViewHolder.llExpandArea.setVisibility(View.VISIBLE);
        } else {
            conversationViewHolder.llExpandArea.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }


    public interface ConversationItemClickListener{
         void onItemClick(int position, View v);

    }

    public class ConversationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CardView cv;
        TextView contactName;
        TextView status;
        ImageView contactPhoto;
        TextView updateDatetime;
        //TextView conversation_id;
        TextView right_text_button;
        TextView left_text_button;
        ImageView resultImage;
        LinearLayout llExpandArea;
        //TextView tab;
        public ConversationViewHolder(View itemView) {
            super(itemView);

            cv = (CardView)itemView.findViewById(R.id.conversation_card_view);
            contactName = (TextView)itemView.findViewById(R.id.contact_name);
            status = (TextView)itemView.findViewById(R.id.status);
            contactPhoto = (ImageView)itemView.findViewById(R.id.contact_photo);
            resultImage = (ImageView)itemView.findViewById(R.id.resultImage);
            updateDatetime = (TextView)itemView.findViewById(R.id.datetime);
            //datetime = (TextView)itemView.findViewById(R.id.datetime);
            //conversation_id = (TextView)itemView.findViewById(R.id.conversation_id);
            right_text_button = (TextView)itemView.findViewById(R.id.right_text_button);
            left_text_button = (TextView)itemView.findViewById(R.id.left_text_button);
            llExpandArea = (LinearLayout)itemView.findViewById(R.id.llExpandArea);
            //tab = (TextView)itemView.findViewById(R.id.tab);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            // Check for an expanded view, collapse if you find one
            if (expandedPosition >= 0) {
                int prev = expandedPosition;
                notifyItemChanged(prev);
            }
            // Set the current position to "expanded"
            expandedPosition = getAdapterPosition();
            notifyItemChanged(expandedPosition);

            conversationItemClickListener.onItemClick(getAdapterPosition(), v);


        }
    }
}
