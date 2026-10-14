package com.tianzhou.item.app.category.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class CategoryListVO {
    private Long categoryId;
    private String name;
    private String image;
}
