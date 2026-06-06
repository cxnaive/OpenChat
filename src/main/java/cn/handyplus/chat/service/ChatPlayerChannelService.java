package cn.handyplus.chat.service;

import cn.handyplus.chat.constants.ChatConstants;
import cn.handyplus.chat.enter.ChatPlayerChannelEnter;
import cn.handyplus.lib.db.Db;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 玩家频道
 *
 * @author handy
 */
public class ChatPlayerChannelService {
    private ChatPlayerChannelService() {
    }

    private static class SingletonHolder {
        private static final ChatPlayerChannelService INSTANCE = new ChatPlayerChannelService();
    }

    public static ChatPlayerChannelService getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * 新增
     */
    public void add(ChatPlayerChannelEnter enter) {
        Db.use(ChatPlayerChannelEnter.class).execution().insert(enter);
    }

    /**
     * 根据uid查询
     *
     * @param playerUuid uid
     * @return 数据
     */
    public Optional<ChatPlayerChannelEnter> findByUid(UUID playerUuid) {
        Db<ChatPlayerChannelEnter> use = Db.use(ChatPlayerChannelEnter.class);
        use.where().eq(ChatPlayerChannelEnter::getPlayerUuid, playerUuid);
        return use.execution().selectOne();
    }

    /**
     * 根据playerUuid设置频道
     *
     * @param playerUuid uid
     * @param channel    频道
     * @since 1.0.6
     */
    public boolean setChannel(UUID playerUuid, String channel) {
        Db<ChatPlayerChannelEnter> db = Db.use(ChatPlayerChannelEnter.class);
        db.update().set(ChatPlayerChannelEnter::getChannel, channel);
        db.where().eq(ChatPlayerChannelEnter::getPlayerUuid, playerUuid);
        int update = db.execution().update();
        // 重新缓存数据
        ChatConstants.PLAYER_CHAT_CHANNEL.put(playerUuid, channel);
        return update > 0;
    }

    /**
     * 设置私聊模式目标
     *
     * @param playerUuid 玩家 UUID
     * @param tellTarget 私聊目标 UUID（null 表示退出私聊模式）
     * @since 3.4.0
     */
    public void setTellTarget(UUID playerUuid, UUID tellTarget) {
        String targetStr = tellTarget != null ? tellTarget.toString() : null;
        // 先尝试更新
        Db<ChatPlayerChannelEnter> db = Db.use(ChatPlayerChannelEnter.class);
        db.update().set(ChatPlayerChannelEnter::getTellTarget, targetStr);
        db.where().eq(ChatPlayerChannelEnter::getPlayerUuid, playerUuid);
        int updated = db.execution().update();
        // 如果没有记录则插入
        if (updated == 0) {
            ChatPlayerChannelEnter enter = new ChatPlayerChannelEnter();
            enter.setPlayerUuid(playerUuid);
            enter.setChannel(ChatConstants.DEFAULT);
            enter.setTellTarget(targetStr);
            enter.setIsApi(false);
            add(enter);
        }
        // 同步内存
        if (tellTarget != null) {
            ChatConstants.PLAYER_TELL_TARGET.put(playerUuid, tellTarget);
        } else {
            ChatConstants.PLAYER_TELL_TARGET.remove(playerUuid);
        }
    }

    /**
     * 根据频道查询
     *
     * @param channel 频道
     * @return 数据
     * @since 1.0.6
     */
    public List<ChatPlayerChannelEnter> findByChannel(String channel) {
        Db<ChatPlayerChannelEnter> use = Db.use(ChatPlayerChannelEnter.class);
        use.where().eq(ChatPlayerChannelEnter::getChannel, channel);
        return use.execution().list();
    }

    /**
     * 根据playerUuid设置频道
     *
     * @param channel    频道
     * @param newChannel 新频道
     * @param isApi      是否api
     * @since 1.0.6
     */
    public void setChannel(String channel, String newChannel, Boolean isApi) {
        Db<ChatPlayerChannelEnter> db = Db.use(ChatPlayerChannelEnter.class);
        db.update().set(ChatPlayerChannelEnter::getChannel, newChannel)
                .set(isApi != null, ChatPlayerChannelEnter::getIsApi, isApi);
        db.where().eq(ChatPlayerChannelEnter::getChannel, channel);
        db.execution().update();
    }

    /**
     * 查询全部
     *
     * @return list
     * @since 2.0.0
     */
    public List<ChatPlayerChannelEnter> findAll() {
        return Db.use(ChatPlayerChannelEnter.class).execution().list();
    }

}