package com.jxau.train.common.resp;

import java.io.Serializable;
import java.util.List;

/**
 * 分页返回
 * @param <T>
 */
public class PageResp<T> implements Serializable {

    /**
     * 总条数
     */
    private Long total;
    /**
     * 当前页的列表数据
     */
    private List<T> list;

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PageResp{");
        sb.append("total=").append(total);
        sb.append(", list=").append(list);
        sb.append('}');
        return sb.toString();
    }
}
