package com.example.contactselection.dto.out;

import lombok.Data;
import java.util.List;

/**
 * Response DTO: Contact Search Result
 *
 * Skill pattern: Standardised response data
 *
 * Business rules:
 *   - totalCount = 0  → errorCode = MSG_NO_RESULT
 *   - totalCount > 80 → needsConfirmation = true (client shows dialog)
 *   - otherwise       → refList populated
 */
@Data
public class RefSearchOutDto {

    /** Tổng số kết quả tìm kiếm */
    private int totalCount;

    /**
     * true  = có > 80 kết quả, client cần hiển thị dialog xác nhận
     * false = bình thường
     */
    private boolean needsConfirmation;

    /** Danh sách nơi liên hệ (rỗng nếu needsConfirmation = true) */
    private List<RefSelectItemDto> refList;
}
