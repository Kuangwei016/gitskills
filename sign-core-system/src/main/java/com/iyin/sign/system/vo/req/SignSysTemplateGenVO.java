package com.iyin.sign.system.vo.req;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * <p>
 * 模板表
 * </p>
 *
 * @author wdf
 * @since 2019-07-15
 */
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@Data
public class SignSysTemplateGenVO implements Serializable  {

    private static final long serialVersionUID = 1L;

    /**
     * 模板编号
     */
    @ApiModelProperty(value = "模板编号")
    @NotBlank(message = "模板编号不能为空")
    private String id;

    /**
     * 模板名称
     */
    @ApiModelProperty(value = "模板名称")
    @Size(max = 30)
    private String tempName;

    /**
     * 模板用途
     */
    @ApiModelProperty(value = "模板用途")
    @Size(max = 30)
    private String tempPurpose;

    /**
     * 模板内容
     */
    @ApiModelProperty(value = "模板内容")
    private String tempHtml;

    /**
     * 外键关联ID
     */
    @ApiModelProperty(value = "外键关联ID")
    @JsonIgnore
    private String relationId;
}
