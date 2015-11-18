package learn2crack.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import learn2crack.chat.R;
import learn2crack.models.ChatMessage;
import learn2crack.models.WnChatMessage;
import learn2crack.models.WnConversation;
import learn2crack.models.WnMatch;
import learn2crack.models.WnMessageResult;
import learn2crack.models.WnMessageStatus;

/**
 * Created by $(USER) on $(DATE).
 */
public class ChatRVAdapter extends RecyclerView.Adapter<ChatRVAdapter.ChatMessageViewHolder> {

    private static int expandedPosition = -1;

    List<WnChatMessage> chatMessages;
    String myPhone;
    public ChatRVAdapter(List<WnChatMessage> chatMessages, String myPhone) {
        this.chatMessages = chatMessages;
        this.myPhone = myPhone;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public ChatMessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_chat_message, viewGroup, false);
        return new ChatMessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ChatMessageViewHolder chatMessageViewHolder, int i) {

        WnChatMessage chatMessage = chatMessages.get(i);
        chatMessage.updateIsMe(myPhone);
        setAlignment(chatMessageViewHolder, chatMessage.getIsme());
        chatMessageViewHolder.txtMessage.setText(chatMessage.getChat_text());
        chatMessageViewHolder.txtInfo.setText(chatMessage.getDelivery_date());

    }

    private void setAlignment(ChatMessageViewHolder chatMessageViewHolder, boolean isMe) {
        if (!isMe) {
            chatMessageViewHolder.contentWithBG.setBackgroundResource(R.drawable.in_message_bg);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) chatMessageViewHolder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            chatMessageViewHolder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) chatMessageViewHolder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            chatMessageViewHolder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) chatMessageViewHolder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            chatMessageViewHolder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) chatMessageViewHolder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.RIGHT;
            chatMessageViewHolder.txtInfo.setLayoutParams(layoutParams);
        } else {
            chatMessageViewHolder.contentWithBG.setBackgroundResource(R.drawable.out_message_bg);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) chatMessageViewHolder.contentWithBG.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            chatMessageViewHolder.contentWithBG.setLayoutParams(layoutParams);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) chatMessageViewHolder.content.getLayoutParams();
            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            chatMessageViewHolder.content.setLayoutParams(lp);
            layoutParams = (LinearLayout.LayoutParams) chatMessageViewHolder.txtMessage.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            chatMessageViewHolder.txtMessage.setLayoutParams(layoutParams);

            layoutParams = (LinearLayout.LayoutParams) chatMessageViewHolder.txtInfo.getLayoutParams();
            layoutParams.gravity = Gravity.LEFT;
            chatMessageViewHolder.txtInfo.setLayoutParams(layoutParams);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }



    public class ChatMessageViewHolder extends RecyclerView.ViewHolder{
        public TextView txtMessage;
        public TextView txtInfo;
        public LinearLayout content;
        public LinearLayout contentWithBG;

        public ChatMessageViewHolder(View itemView) {
            super(itemView);

            this.txtMessage = (TextView) itemView.findViewById(R.id.txtMessage);
            this.content = (LinearLayout) itemView.findViewById(R.id.content);
            this.contentWithBG = (LinearLayout) itemView.findViewById(R.id.contentWithBackground);
            this.txtInfo = (TextView) itemView.findViewById(R.id.txtInfo);
        }
    }
}
