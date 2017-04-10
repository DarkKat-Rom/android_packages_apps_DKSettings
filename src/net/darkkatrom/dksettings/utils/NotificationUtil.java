/*
 * Copyright (C) 2017 DarkKat
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.darkkatrom.dksettings.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import net.darkkatrom.dksettings.NotificationReplyReceiver;
import net.darkkatrom.dksettings.R;
import net.darkkatrom.dksettings.utils.PreferenceUtils;

public class NotificationUtil {

    public static final int TYPE_DEFAULT  = PreferenceUtils.TYPE_DEFAULT;
    public static final int TYPE_TEXT     = PreferenceUtils.TYPE_TEXT;
    public static final int TYPE_PICTURE  = PreferenceUtils.TYPE_PICTURE;
    public static final int TYPE_INBOX    = PreferenceUtils.TYPE_INBOX;
    public static final int TYPE_MESSAGE  = PreferenceUtils.TYPE_MESSAGE;
    public static final int TYPE_MEDIA    = PreferenceUtils.TYPE_MEDIA;

    public static final int PRIORITY_MIN     = PreferenceUtils.PRIORITY_MIN;
    public static final int PRIORITY_LOW     = PreferenceUtils.PRIORITY_LOW;
    public static final int PRIORITY_DEFAULT = PreferenceUtils.PRIORITY_DEFAULT;
    public static final int PRIORITY_HIGH    = PreferenceUtils.PRIORITY_HIGH;
    public static final int PRIORITY_MAX     = PreferenceUtils.PRIORITY_MAX;

    private Context mContext;
    private PreferenceUtils mUtils;
    private Resources mResources;

    public NotificationUtil(Context context, PreferenceUtils utils) {
        mContext = context;
        mUtils = utils;
        mResources = mContext.getResources();
    }

    public void sendNotification() {
        Notification notification = buildNotification();
        if (notification != null) {
            int notificationId = mUtils.getNotificationId();
            NotificationManager manager =
                    (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(notificationId, notification);
            int nextNotificationId = notificationId == 100 ? 1 : notificationId + 1;
            mUtils.setNotificationId(nextNotificationId);
        }
    }

    private Notification buildNotification() {
        Notification notification = null;
        Notification.Builder builder = new Notification.Builder(mContext)
            .setShowWhen(true)
            .setWhen(System.currentTimeMillis())
            .setSubText(mResources.getString(R.string.notification_test_sub_text))
            .setContentTitle(mResources.getString(R.string.notification_test_title))
            .setContentText(mResources.getString(R.string.notification_test_content_text));

        if (mUtils.getType() != PreferenceUtils.TYPE_MEDIA
                && mUtils.getNumberOfActions() != PreferenceUtils.NO_ACTIONS) {
            setButtons(builder);
        }
        if (mUtils.getPriority() != PreferenceUtils.PRIORITY_NONE) {
            setPriority(builder);
        }

        if (mUtils.getShowEmphasizedActions()) {
            PendingIntent intent = PendingIntent.getActivity(mContext, 0, new Intent(), 0);
            builder.setFullScreenIntent(intent, false);
        }
        builder.setColor(mUtils.getNotificationColor());

        switch (mUtils.getType()) {
            default:
            case TYPE_DEFAULT:
                notification = getDefaultNotification(builder);
                break;
            case TYPE_TEXT:
                notification = getBigTextStyle(builder);
                break;
            case TYPE_PICTURE:
                notification = getBigPictureStyle(builder);
                break;
            case TYPE_INBOX:
                notification = getInboxStyle(builder);
                break;
            case TYPE_MESSAGE:
                notification = getMessagingStyle(builder);
                break;
            case TYPE_MEDIA:
                notification = getMediaStyle(builder);
                break;
        }
        return notification;

    }

    private void setPriority(Notification.Builder builder) {
        int priority = Notification.PRIORITY_DEFAULT;
        switch (mUtils.getPriority()) {
            case PRIORITY_MIN:
                priority = Notification.PRIORITY_MIN;
                break;
            case PRIORITY_LOW:
                priority = Notification.PRIORITY_LOW;
                break;
            default:
            case PRIORITY_DEFAULT:
                priority = Notification.PRIORITY_DEFAULT;
                break;
            case PRIORITY_HIGH:
                priority = Notification.PRIORITY_HIGH;
                break;
            case PRIORITY_MAX:
                priority = Notification.PRIORITY_MAX;
                break;
        }
        builder.setPriority(priority);
    }

    private void setButtons(Notification.Builder builder) {
        PendingIntent intent = PendingIntent.getActivity(mContext, 0, new Intent(), 0);
        boolean showReplyAction = mUtils.getShowReplyAction();
        for (int i = 0; i < mUtils.getNumberOfActions(); i++) {

            int numberOfActions = mUtils.getNumberOfActions();
            boolean showOneAction = numberOfActions == PreferenceUtils.ONE_ACTION;
            boolean disableTombstoneActions = mUtils.getShowEmphasizedActions()
                    || (mUtils.getShowReplyAction() && showOneAction);
            boolean showTombstoneActions = mUtils.getShowTombstoneActions()
                    && !disableTombstoneActions;
            if (showReplyAction && i == 0) {
                builder.addAction(getReplyAction());
            } else {
                builder.addAction(R.drawable.ic_status_bar_dk, mResources.getString(
                        R.string.notification_test_action_text, (i + 1)), showTombstoneActions ? null : intent);
            }
        }
    }

    private Notification getDefaultNotification(Notification.Builder builder) {
        builder.setSmallIcon(R.drawable.ic_status_bar_dk);

        return builder.build();
    }

    private Notification.Action getReplyAction() {
        Intent resultIntent = new Intent(mContext, NotificationReplyReceiver.class);
        resultIntent.setAction(NotificationReplyReceiver.ACTION_NOTIFICATION_REPLY);
        resultIntent.putExtra(PreferenceUtils.NOTIFICATION_ID, mUtils.getNotificationId());
        PendingIntent resultPendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(),
                mUtils.getNotificationId(), resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String replyHint = mResources.getString(R.string.notification_test_action_reply_hint);
        RemoteInput remoteInput = new RemoteInput.Builder(PreferenceUtils.TEXT_REPLY)
            .setLabel(replyHint)
            .build();

        Notification.Action action = new Notification.Action.Builder(R.drawable.ic_status_bar_reply,
                mResources.getString(R.string.notification_test_action_reply_title), resultPendingIntent)
                .addRemoteInput(remoteInput)
                .build();
        return action;
    }

    private Notification getBigTextStyle(Notification.Builder builder) {
        Notification.BigTextStyle style = new Notification.BigTextStyle()
            .bigText(mResources.getString(R.string.notification_test_type_text_title));

        builder.setSmallIcon(R.drawable.ic_status_bar_text)
            .setStyle(style);

        return builder.build();
	}

    private Notification getBigPictureStyle(Notification.Builder builder) {
        Bitmap large = BitmapFactory.decodeResource(mResources, R.drawable.default_wallpaper);
        Notification.BigPictureStyle style = new Notification.BigPictureStyle()
            .bigPicture(large);

        builder.setSmallIcon(R.drawable.ic_status_bar_image)
            .setStyle(style)
            .setLargeIcon(large);

        return builder.build();
    }

    private Notification getInboxStyle(Notification.Builder builder) {
        Bitmap large = BitmapFactory.decodeResource(mResources, R.mipmap.ic_launcher);
        Notification.InboxStyle style = new Notification.InboxStyle();
        addInboxLines(style);

        builder.setSmallIcon(R.drawable.ic_status_bar_message)
            .setStyle(style)
            .setLargeIcon(large);

        return builder.build();
    }

    private Notification getMessagingStyle(Notification.Builder builder) {
        Bitmap large = BitmapFactory.decodeResource(mResources, R.mipmap.ic_launcher);
        Notification.MessagingStyle style = new Notification.MessagingStyle(
                mResources.getString(R.string.app_name))
            .setConversationTitle(mResources.getString(
                    R.string.notification_test_conversation_title));
        addMessages(style);

        builder.setSmallIcon(R.drawable.ic_status_bar_message)
            .setStyle(style)
            .setLargeIcon(large);

        return builder.build();
    }

    private Notification getMediaStyle(Notification.Builder builder) {
        Bitmap large = BitmapFactory.decodeResource(mResources, R.mipmap.ic_launcher);
        Notification.MediaStyle style = new Notification.MediaStyle()
                .setShowActionsInCompactView(1, 2, 3);
        PendingIntent intent = PendingIntent.getActivity(mContext, 0, new Intent(), 0);

        builder.setSmallIcon(R.drawable.ic_status_bar_media)
            .setStyle(style)
            .setLargeIcon(large)
            .addAction(R.drawable.ic_action_fast_rewind,
                    mResources.getString(R.string.notification_test_fast_rewind_title), intent)
            .addAction(R.drawable.ic_action_skip_previous,
                    mResources.getString(R.string.notification_test_skip_previous_title), intent)
            .addAction(R.drawable.ic_action_play,
                    mResources.getString(R.string.notification_test_play_title), intent)
            .addAction(R.drawable.ic_action_skip_next,
                    mResources.getString(R.string.notification_test_skip_next_title), intent)
            .addAction(R.drawable.ic_action_fast_forward,
                    mResources.getString(R.string.notification_test_fast_forward_title), intent);
        return builder.build();
    }

    private void addInboxLines(Notification.InboxStyle style) {
        for (int i = 0; i < 6; i++) {
            style.addLine(mResources.getString(R.string.notification_test_line_number_text, (i + 1)));
        }
    }

    private void addMessages(Notification.MessagingStyle style) {
        for (int i = 0; i < 6; i++) {
            style.addMessage(mResources.getString(R.string.notification_test_line_number_text, (i + 1)),
                    System.currentTimeMillis(), mResources.getString(R.string.app_name));
        }
    }
}
