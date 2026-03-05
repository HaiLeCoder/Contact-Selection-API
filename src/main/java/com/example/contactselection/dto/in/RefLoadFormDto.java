package com.example.contactselection.dto.in;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

/**
 * Request DTO: Initial Display (画面初期表示リクエスト)
 * Physical class name: refLoadFormDto (per server process definition)
 */
@Data
public class RefLoadFormDto {

    /**
     * 問合せ先選択の前画面 (Loại màn hình trước đó)
     * 0 = Màn hình khác
     * 1 = Màn hình 問合せ先一括追加 (thêm hàng loạt)
     */
    @NotNull(message = "kindRef là bắt buộc")
    @Min(value = 0, message = "kindRef phải là 0 hoặc 1")
    @Max(value = 1, message = "kindRef phải là 0 hoặc 1")
    private Integer kindRef;
}
