package org.xuxiaoxiao.firebasepagination;

/**
 *  Created by WuQiang on 2017/3/29.
 *  Copyright © 2017年 WuQiang. All rights reserved.
 *
 *  在 ChatMessage 当中添加一个属性需要改的地方：
 1：Model 也就是 ChatMessage 当中把这个属性加上
 2：与 Model 对应的视图，XML 文件需要改
 3：bind 方法需要改
 4：在发送信息的时候，这个新加的属性也要发送出去
*/
public class ChatMessage {

    private String message;
    private String author;
    private String messageID;
    private int mediaType;
    private String ImgUrl;

    // Required default constructor for Wilddog object mapping
    @SuppressWarnings("unused")
    private ChatMessage() {
    }

    public ChatMessage(String message, String author, String messageID, int mediaType, String imgUrl) {
        this.message = message;
        this.author = author;
        this.messageID = messageID;
        this.mediaType = mediaType;
        this.ImgUrl = imgUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public String getImgUrl() {
        return ImgUrl;
    }

    public void setImgUrl(String imgUrl) {
        ImgUrl = imgUrl;
    }
}
