package com.example.contactselection.dto.out;

import lombok.Data;

/**
 * 地域Ｍ情報Dto – Region info DTO
 * Physical: rgonInfoDto (per server process definition)
 *
 * Fields from SQL getRgonInfList:
 *   RGON_CD → rgonCd
 *   RGON_NM → rgonNm
 */
@Data
public class RgonInfoDto {

    /** 地域コード – Region code */
    private Integer rgonCd;

    /** 地域名称 – Region name (max 40 chars, full-width) */
    private String rgonNm;
}
