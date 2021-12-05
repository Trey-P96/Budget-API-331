package io.none.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Pagination<T> {
    @JsonProperty("data")
    List<T> data;

    @JsonProperty("hasNextPage")
    boolean hasNextPage;

    @JsonProperty("hasPreviousPage")
    boolean hasPreviousPage;

    @JsonProperty("page")
    int page;

    @JsonProperty("pageSize")
    int pageSize;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
