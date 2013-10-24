package ru.tulupov.nsuconnect.adapter;


import android.content.Context;
import android.view.View;
import android.widget.TextView;


import java.text.DateFormat;
import java.text.SimpleDateFormat;

import ru.tulupov.nsuconnect.R;
import ru.tulupov.nsuconnect.model.Message;
import ru.tulupov.nsuconnect.model.User;
import ru.tulupov.nsuconnect.util.adapter.BeanHolderAdapter;
import ru.tulupov.nsuconnect.util.adapter.FindViewById;

public class ConversationAdapter extends BeanHolderAdapter<Message, ConversationAdapter.Holder> {
    public static class Holder {
        @FindViewById(R.id.text)
        public TextView text;

        @FindViewById(R.id.date)
        public TextView date;
        @FindViewById(R.id.container)
        public View container;

    }

    private DateFormat dateFormat;

    public ConversationAdapter() {
        super(0, Holder.class);

        dateFormat = new SimpleDateFormat("HH:mm");
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getUser().getType();
    }

    @Override
    protected int getLayoutResourceId(int position) {
        switch (getItemViewType(position)) {
            case User.TYPE_OTHER:
                return R.layout.item_message_in;
            case User.TYPE_YOUR:
                return R.layout.item_message_out;
            case User.TYPE_SYSTEM:
                return R.layout.item_message_system;
        }
        return 0;
    }


    @Override
    protected void updateHolder(Context context, Holder holder, Message item, int position) {
        holder.text.setText(item.getMessage());

        holder.date.setText(dateFormat.format(item.getDate()));

        holder.container.setBackgroundResource(item.isSentFlag()?android.R.color.transparent:R.color.light_gray);


    }


}
