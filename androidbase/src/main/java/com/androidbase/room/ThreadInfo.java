package com.androidbase.room;


import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;

@Keep
@Entity(primaryKeys = {"id", "tag"})
public class ThreadInfo {
    long id;
    String uri;
    @NonNull
    String tag;
    long startoffset;
    long endoffset;
    long finished;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    @Nullable
    public String getTag() {
        return tag;
    }

    public void setTag(@Nullable String tag) {
        this.tag = tag;
    }

    public long getStartoffset() {
        return startoffset;
    }

    public void setStartoffset(long startoffset) {
        this.startoffset = startoffset;
    }

    public long getEndoffset() {
        return endoffset;
    }

    public void setEndoffset(long endoffset) {
        this.endoffset = endoffset;
    }

    public long getFinished() {
        return finished;
    }

    public void setFinished(long finished) {
        this.finished = finished;
    }
}
