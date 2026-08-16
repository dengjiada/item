package com.tianzhou.item.console.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ItemListFeedVO {
    private List<ItemListVO> list;
    //总条数
    private Long total;
    //每页有多少条
    private Integer pageSize;
}
