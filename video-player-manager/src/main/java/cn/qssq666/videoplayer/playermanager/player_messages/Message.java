package cn.qssq666.videoplayer.playermanager.player_messages;

/**
 * This generic interface for messages
 */
public interface Message {
    void runMessage();
    void polledFromQueue();
    void messageFinished();
}
