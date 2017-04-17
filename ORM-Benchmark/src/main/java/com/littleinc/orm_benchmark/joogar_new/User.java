package com.littleinc.orm_benchmark.joogar_new;

import net.skoumal.joogar2.annotations.PrimaryKey;
import net.skoumal.joogar2.annotations.Table;

/**
 * Created by Jan Bína on 04/17/2017
 */
@Table
public class User
{
    @PrimaryKey
    public Long id;

    public String lastName;

    public String firstName;
}
