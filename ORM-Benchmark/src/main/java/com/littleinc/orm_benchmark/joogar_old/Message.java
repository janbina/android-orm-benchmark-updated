package com.littleinc.orm_benchmark.joogar_old;

import net.skoumal.joogar.shared.dsl.Table;
import net.skoumal.joogar.shared.dsl.TableIndex;

/**
 * Created by Jan Bína on 04/17/2017
 */
@Table
//@TableIndex(columns = {"command_id"})
public class Message
{

    public Long id;

    public long clientId;

    public long commandId;

    public double sortedBy;

    public int createdAt;

    public String content;

    public long senderId;

    public long channelId;
}
