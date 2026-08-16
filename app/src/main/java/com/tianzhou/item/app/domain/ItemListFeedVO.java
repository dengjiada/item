package com.tianzhou.item.app.domain;

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
    //瀑布流是否到结尾，现阶段为了方便就用isEnd，以后可能会修改
    private Boolean isEnd;
}
