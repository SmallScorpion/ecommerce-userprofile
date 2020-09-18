package com.warehouse.ecommerceuserprofile.app.support;

import lombok.Data;


@Data
public class EsTagQuery {
    private String name;    // 当前标签的名称
    private String value;    // 当前标签的取值
    private String type;    // 查询的类型
}
