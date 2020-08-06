package com.macro.mall.dto;

import com.macro.mall.model.UmsAdmin;
import lombok.Data;
import lombok.ToString;

@Data
public class UpdUmsAdminParam extends UmsAdmin {
    private String newPassword;
    @Override
    public String toString() {
        return super.toString()+ "newPassword="+newPassword;
    }
}
