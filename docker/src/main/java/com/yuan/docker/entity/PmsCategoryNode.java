package com.yuan.docker.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PmsCategoryNode extends PmsCategory implements Serializable {
    private List<PmsCategoryNode> children;

}
