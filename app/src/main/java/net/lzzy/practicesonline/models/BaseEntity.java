package net.lzzy.practicesonline.models;

import net.lzzy.sqllib.AsPrimaryKey;

import java.util.UUID;

public class BaseEntity {
    @AsPrimaryKey
    UUID id;
    BaseEntity(){
        id = UUID.randomUUID();
    }

    public Object getIdentityValue() {
        return id;
    }

    public UUID getId() {
        return id;
    }
}