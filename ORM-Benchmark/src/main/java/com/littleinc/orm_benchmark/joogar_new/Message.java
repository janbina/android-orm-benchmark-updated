package com.littleinc.orm_benchmark.joogar_new;

import net.skoumal.joogar2.annotations.Index;
import net.skoumal.joogar2.annotations.PrimaryKey;
import net.skoumal.joogar2.annotations.Table;

/**
 * Created by Jan Bína on 04/17/2017
 */
@Table
public class Message
{

    @PrimaryKey
    public Long id;

    public long clientId;

    @Index
    public long commandId;

    public double sortedBy;

    public int createdAt;

    public String content;

    public long senderId;

    public long channelId;
}
