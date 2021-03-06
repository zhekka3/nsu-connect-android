package ru.tulupov.nsuconnect.protocol.commands;

import android.content.Intent;

import ru.tulupov.nsuconnect.helper.BroadcastHelper;
import ru.tulupov.nsuconnect.util.Log;

import java.sql.SQLException;
import java.util.Date;

import ru.tulupov.nsuconnect.database.ContentUriHelper;
import ru.tulupov.nsuconnect.database.DatabaseConstants;
import ru.tulupov.nsuconnect.database.HelperFactory;
import ru.tulupov.nsuconnect.model.Message;
import ru.tulupov.nsuconnect.protocol.Command;
import ru.tulupov.nsuconnect.protocol.CommandContext;
import ru.tulupov.nsuconnect.service.DataService;

public class StartTypingCommand implements Command {
    private static final String TAG = StartTypingCommand.class.getSimpleName();

    @Override
    public void execute(CommandContext context) {
        context.getApplicationContext().sendBroadcast(BroadcastHelper.getChatTypingIntent(context.getChat().getId(), true));
    }
}
